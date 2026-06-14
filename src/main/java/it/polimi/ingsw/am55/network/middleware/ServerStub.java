package it.polimi.ingsw.am55.network.middleware;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualServer;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class ServerStub implements VirtualServer, AutoCloseable {

    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    private volatile boolean running;
    private Thread listenerThread;

    public ServerStub(String host, int port) throws IOException {
        this.socket = new Socket(host, port);

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.output.flush();

        this.input = new ObjectInputStream(socket.getInputStream());

        this.running = true;
    }

    @Override
    public void receiveCommand(ServerCommand command, VirtualView sender) throws RemoteException {
        try {
            synchronized (output) {
                output.writeObject(command);
                output.flush();
                output.reset();
            }
        } catch (IOException e) {
            throw new RemoteException("[SERVER_STUB] Impossibile inviare comando al server socket.", e);
        }
    }

    public void startListener(VirtualView realClient) {
        if (listenerThread != null && listenerThread.isAlive()) {
            return;
        }

        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    MessageToClient message = (MessageToClient) input.readObject();

                    if (message != null) {
                        realClient.onMessage(message);
                    }

                } catch (Exception e) {
//                    if (running) {
//                        System.out.println("[SERVER_STUB] Listener terminato: " + e.getMessage());
//                    }
//                    closeQuietly();
                    break;
                }
            }
        });

        listenerThread.setName("server-stub-listener");
        listenerThread.start();
    }

    @Override
    public void close() {
        closeQuietly();
    }

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

        System.out.println("[SERVER_STUB] Socket chiuso.");
    }
}