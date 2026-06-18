package it.polimi.ingsw.am55.network.middleware;


import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * Server-side socket client that represents one connected client.
 *
 * The skeleton receives serialized {@link ServerCommand} objects from the socket
 * stream and delegates them to {@link ServerApplication}. It also implements
 * {@link VirtualView} so the server can send {@link MessageToClient} objects back
 * through the same socket connection.
 */
public class ClientSkeleton implements VirtualView {

    /**
     * TCP connection associated with one remote client.
     */
    private final Socket socket;
    /**
     * Object stream used to read serialized client-to-server commands from the socket.
     */
    private final ObjectInputStream input;
    /**
     * Object stream used to send serialized server-to-client messages to the socket.
     */
    private final ObjectOutputStream output;
    /**
     * Shared server application that executes commands read by this skeleton.
     */
    private final ServerApplication serverApplication;

    /**
     * Flag controlling the listener loop that reads commands from the socket.
     */
    private volatile boolean running;
    /**
     * Player id associated with this socket client after successful game creation or join.
     *
     * The value is {@code null} while the client is still represented only by its lobby session.
     */
    private String playerId;

    /**
     * Background thread that continuously reads commands from the client socket.
     */
    private Thread listenerThread;

    /**
     * Creates the socket skeleton and immediately starts its command listener.
     *
     * @param socket            TCP connection associated with one client
     * @param input             object input stream used to read client commands
     * @param output            object output stream used to send messages to the client
     * @param serverApplication shared server application that executes commands
     */
    public ClientSkeleton(Socket socket, ObjectInputStream input, ObjectOutputStream output,
                          ServerApplication serverApplication) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.serverApplication = serverApplication;

        this.running = true;
        this.playerId = null;

        startListener();
    }

    /**
     * Starts the background thread that reads socket commands and executes them on the server.
     */
    private void startListener() {
        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    ServerCommand command = (ServerCommand) input.readObject();

                    if (command != null) {
                        serverApplication.executeCommand(command, this);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    break;

                } catch (Exception e) {
                    System.out.println("[CLIENT_SKELETON] Error executeCommand: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        listenerThread.setName("client-skeleton-listener");
        listenerThread.start();
    }

    /**
     * Sends a server-to-client message through the socket output stream.
     *
     * @param message message to serialize and send to the client
     * @throws RemoteException if the socket is closed or the object cannot be written
     */
    @Override
    public synchronized void onMessage(MessageToClient message) throws RemoteException {
        try {
            if (socket.isClosed()) {
                throw new IOException("Socket already closed");
            }

            output.writeObject(message);
            output.flush();
            output.reset();

        } catch (IOException e) {
            throw new RemoteException("[CLIENT_SKELETON] Unable to send message to client socket.", e);
        }
    }

    /**
     * Returns the player identifier associated with this socket client.
     *
     * @return the player identifier, or {@code null} while the client is still in the lobby
     * @throws RemoteException if the value cannot be read through the remote interface
     */
    @Override
    public String getPlayerId() throws RemoteException {
        return playerId;
    }

    /**
     * Stores the player identifier assigned to this socket client.
     *
     * @param playerId player identifier assigned after successful create/join
     * @throws RemoteException if the value cannot be set through the remote interface
     */
    @Override
    public void setPlayerId(String playerId) throws RemoteException {
        this.playerId = playerId;
    }

    /**
     * Stops the listener and closes all socket resources associated with this client.
     *
     * @throws RemoteException if the close operation is invoked through the remote interface
     */
    @Override
    public void close() throws RemoteException {
        running = false;

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

        System.out.println("[CLIENT_SKELETON] Socket closed.");
    }
}