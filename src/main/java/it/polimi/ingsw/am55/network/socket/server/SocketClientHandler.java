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
    private Thread virtualViewThread;


    public SocketClientHandler(Socket socket, ObjectInputStream input, ObjectOutputStream output,
                               ServerApplication serverApplication) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.serverApplication = serverApplication;
        runVirtualView();
    }


    //"Orecchio" del socketclient handler verso i client, permette di passare il comando
    //da eseguire a serverapplication
    public void runVirtualView() {
        virtualViewThread = new  Thread(() -> {
            ServerCommand command;
            try{
                while((command = (ServerCommand) input.readObject()) != null){
                    try{
                        serverApplication.executeCommand(command,this);
                    }catch(Exception e){
                        System.out.println("[SOCKET_HANDLER] Errore esecuzione del comando "+e.getMessage());
                    }
                }
//            } catch (EOFException e){
//                System.out.println("[SOCKET_HANDLER] EOF"+e.getMessage());
//                close();
            } catch (IOException | ClassNotFoundException e){
                System.out.println("[SOCKET_HANDLER] Il client tcp si è disconesso " + e.getMessage());
                close();
            }
     });
     virtualViewThread.start();
    }

    //Invia i messaggi di risposta verso il client che gestisce
    @Override
    public synchronized void onMessage(MessageToClient message) throws Exception {
        if (socket.isClosed()) {
            throw new IOException("Socket chiuso");
        }
        output.reset();
        output.writeObject(message);
        output.flush();
    }


    //Permette la chiusura del socket(dovrebbe essere invocato in serverapplication)
    @Override
    public void close() {
        try {
            virtualViewThread.interrupt();
        } catch (Exception ignored) {}
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
        System.out.println("[SOCKET_HANDLER] Socket closed");
    }
}