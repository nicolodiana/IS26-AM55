package it.polimi.ingsw.am55.network.socket.server;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.message.PongMessage;
import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClientHandler implements VirtualView {

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final ServerApplication serverApplication;
    private String playerId;
    private Thread virtualViewThread;


    public SocketClientHandler(Socket socket, ObjectInputStream input, ObjectOutputStream output,
                               ServerApplication serverApplication) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.serverApplication = serverApplication;
        this.playerId = "";
        runVirtualView();
    }


    //"Orecchio" del socketclient handler verso i client, permette di passare il comando
    //da eseguire a serverapplication
    public void runVirtualView() {
        virtualViewThread = new Thread(() -> {
            ServerCommand command;
            try {
                while ((command = (ServerCommand) input.readObject()) != null) {
                    try {
                        serverApplication.executeCommand(command, this);
                    } catch (Exception e) {
                        System.out.println("[SOCKET_HANDLER] Errore esecuzione del comando " + e.getMessage());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[SOCKET_HANDLER] Il client tcp si è disconesso " + e.getMessage());
                close();
            }
        });
        virtualViewThread.start();
    }

    //Invia i messaggi di risposta verso il client che gestisce
    //Il lock è preso sull' oggetto chiamante, in modo da evitare accesso concorrente allo stream di uscita da
    //parte di più thread come ad esempio thread handler e thread di ping
    @Override
    public synchronized void onMessage(MessageToClient message) throws Exception {
        if (socket.isClosed()) {
            throw new IOException("Socket chiuso");
        }
        output.writeObject(message);
        output.flush();
        output.reset();
    }



    @Override
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public void pong() {
        try {
            this.onMessage(new PongMessage());
        } catch (Exception e) {
            System.out.println("[SOCKET CLIENT HANDLER] Impossibili inviare il ping al client");
        }
    }



    //Permette la chiusura del socket(dovrebbe essere invocato in serverapplication)
    @Override
    public void close() {
        if(socket.isClosed()) return; //Ad esempio server application ha già chiuso il socket per il client
        try {
            input.close();
        } catch (IOException ignored) {
        }
        try {
            output.close();
        } catch (IOException ignored) {
        }
        try {
            socket.close();
        } catch (IOException ignored) {
        }
        System.out.println("[SOCKET_HANDLER] Socket è stato chiuso");
    }
}



//    @Override
//    public String getPlayerId() throws Exception {
//        if(playerId == null){
//            throw new Exception("PlayerId non configurato");
//        }else{
//            return this.playerId;
//        }
//    }
//
//    @Override
//    public void setPlayerId(String playerId) throws Exception {
//        this.playerId = playerId;
//    }
