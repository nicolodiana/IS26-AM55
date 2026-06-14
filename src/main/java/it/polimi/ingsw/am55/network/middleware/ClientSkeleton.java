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

public class ClientSkeleton implements VirtualView {

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final ServerApplication serverApplication;

    private volatile boolean running;
    private String playerId;

    private Thread listenerThread;

    public ClientSkeleton(
            Socket socket,
            ObjectInputStream input,
            ObjectOutputStream output,
            ServerApplication serverApplication
    ) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.serverApplication = serverApplication;

        this.running = true;
        this.playerId = null;

        startListener();
    }

    private void startListener() {
        listenerThread = new Thread(() -> {
            while (running) {
                try {
                    ServerCommand command = (ServerCommand) input.readObject();

                    if (command != null) {
                        //nel caso del socket la chiamata non è necessario affidarla ad un altro thread perche gia asincrono
                        serverApplication.executeCommand(command, this);
                    }

                } catch (IOException | ClassNotFoundException e) {
//                    if (running) {
//                        System.out.println("[CLIENT_SKELETON] Client socket disconnesso: " + e.getMessage());
//                    }
//
//                    try {
//                        close();
//                    } catch (Exception ignored) {
//                    }

                    break;

                } catch (Exception e) {
                    System.out.println("[CLIENT_SKELETON] Errore executeCommand: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        listenerThread.setName("client-skeleton-listener");
        listenerThread.start();
    }

    @Override
    public synchronized void onMessage(MessageToClient message) throws RemoteException {
        try {
            if (socket.isClosed()) {
                throw new IOException("Socket chiuso");
            }

            output.writeObject(message);
            output.flush();
            output.reset();

        } catch (IOException e) {
            throw new RemoteException("[CLIENT_SKELETON] Impossibile inviare messaggio al client socket.", e);
        }
    }

    @Override
    public String getPlayerId() throws RemoteException {
        return playerId;
    }

    @Override
    public void setPlayerId(String playerId) throws RemoteException {
        this.playerId = playerId;
    }

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

        System.out.println("[CLIENT_SKELETON] Socket chiuso.");
    }
}