package it.polimi.ingsw.am55.network.socket.server;

import it.polimi.ingsw.am55.message.MessageToClient;
import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.command.ServerCommand;
import it.polimi.ingsw.am55.network.socket.VirtualViewSocket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClientHandler implements VirtualViewSocket{

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final ServerApplication serverApplication;


    public SocketClientHandler(
            Socket socket,
            ObjectInputStream input,
            ObjectOutputStream output,
            ServerApplication serverApplication
    ) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.serverApplication = serverApplication;
        runVirtualView();
    }


    //"Orecchio" del socketclient handler verso i client, permette di passare il comando
    //da eseguire a serverapplication
    public void runVirtualView() {
     Thread virtualView = new  Thread(() -> {
            ServerCommand command;
            try{
                while((command = (ServerCommand) input.readObject()) != null){
                    try{
                        command.execute(serverApplication,this);
                    }catch(Exception e){
                        System.out.println("[SOCKET_HANDLER] Errore esecuzione del comando "+e.getMessage());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[SOCKET_HANDLER] Errore nel socket: " + e.getMessage());
                disconnectClient("Client disconnesso");
            }
     });
    }

    //
    private void disconnectClient(String reason) {
        //serverApplication.handleClientDisconnection(this, reason);
        close();
    }

    //Invia i messaggi di risposta verso il client che gestisce
    @Override
    public synchronized void onMessage(MessageToClient message) throws Exception {
        if (socket.isClosed()) {
            throw new IOException("Socket chiuso");
        }
        output.writeObject(message);
        output.reset();
        output.flush();
    }


    //Permette la chiusura del socket(dovrebbe essere invocato in serverapplication)
    private void close() {
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
    }
}