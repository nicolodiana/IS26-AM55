package it.polimi.ingsw.am55.network.middleware;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ClientImpl;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * Client-side socket that exposes the server as a {@link VirtualServer}.
 *
 * The stub serializes outgoing {@link ServerCommand} instances to the socket and
 * runs a listener that deserializes {@link MessageToClient} objects sent by the
 * server-side skeleton.
 */
public class ServerStub implements VirtualServer, AutoCloseable {

    /**
     * TCP connection from the client process to the socket server.
     */
    private final Socket socket;
    /**
     * Object stream used to send serialized commands to the server-side skeleton.
     */
    private final ObjectOutputStream output;
    /**
     * Object stream used to read serialized messages sent by the server-side skeleton.
     */
    private final ObjectInputStream input;

    /**
     * Flag controlling the listener loop that receives server messages.
     */
    private volatile boolean running;
    /**
     * Background thread that forwards incoming server messages to the real client endpoint.
     */
    private Thread listenerThread;

    /**
     * Opens a TCP connection to the socket server and prepares object streams.
     *
     * @param host server host name or IP address
     * @param port server TCP port
     * @throws IOException if the socket or object streams cannot be opened
     */
    public ServerStub(String host, int port) throws IOException {
        this.socket = new Socket(host, port);

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.output.flush(); //Because the newly created ObjectOutputStream writes a header to the stream.
        //with flush(), it forces that header to be sent immediately to the socket.

        this.input = new ObjectInputStream(socket.getInputStream());

        this.running = true;
    }

    /**
     * Sends a command to the server through the socket output stream.
     *
     * @param command command to serialize and deliver to the server
     * @param sender  ignored by the socket transport because the skeleton already represents the client
     * @throws RemoteException if the command cannot be written to the socket
     */
    @Override
    public void receiveCommand(ServerCommand command, VirtualView sender) throws RemoteException {
        try {
            synchronized (output) {
                output.writeObject(command);
                output.flush();
                output.reset();
            }
        } catch (IOException e) {
            throw new RemoteException("[SERVER_STUB] Unable to send a command to the server socket.", e);
        }
    }

    /**
     * Starts the background listener that forwards server messages to the real client endpoint.
     *
     * @param realClient local client endpoint that must receive deserialized messages
     */
    public void startListener(ClientImpl realClient) {
        if (listenerThread != null && listenerThread.isAlive()) {
            return;
        }
        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    MessageToClient message = (MessageToClient) input.readObject();

                    if (message != null) {
                        realClient.executeUpdate(message);
                    }

                } catch (Exception e) {
                    break;
                }
            }
        });
        listenerThread.setName("server-stub-listener");
        listenerThread.start();
    }

    /**
     * Closes the socket connection and all associated resources.
     */
    @Override
    public void close() {
        closeQuietly();
    }

    /**
     * Performs best-effort cleanup without propagating close exceptions.
     */
    private void closeQuietly() {
        running = false;
        try{
            listenerThread.interrupt();
        }catch(Exception ignored){}
        try {
            input.close();
        } catch (Exception ignored) {
        }

        try {
            output.close();
        } catch (Exception ignored) {
        }

        try {
            socket.close();
        } catch (Exception ignored) {
        }

        System.out.println("[SERVER_STUB] Socket closed.");
    }
}