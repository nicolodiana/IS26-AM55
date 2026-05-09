package it.polimi.ingsw.am55.network.socket.Server;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageDelivery;
import it.polimi.ingsw.am55.message.MessageToClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class SocketServer  {
    /*private ServerSocket listen;
    private SocketClientHandler clientHandler;
    private List<SocketClientHandler> clients;// List for broadcasting updates
    private final GameController gameController;

    public SocketServer(ServerSocket listen) throws IOException {
        this.listen= listen;
        this.gameController = new GameController();
        clients = new ArrayList<>();
    }

//    public void start() throws IOException {
//        while(true){
//            Socket clientSocket = listen.accept();
//
//            System.out.println("Connection established with" + clientSocket.getInetAddress());
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
//
//            SocketClientHandler handler = new SocketClientHandler(out,in,this, this.gameController);
//
//            registerClient(handler);
//
//            new Thread(() -> {
//                try {
//                    handler.runVirtualView();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }).start();
//        }
//    }

    public void registerClient(SocketClientHandler client){
        synchronized (clients){
            clients.add(client);
        }
    }

//    static void main(String args[]) throws IOException {
//        int port = 4019;
//        ServerSocket socket= new ServerSocket(port);// Creazione del socket del server
//
//
//        new SocketServer(socket).start();// Una volta creato il socket bisogna mandare in esecuzione il server con
//        //il metodo start
//    }

    public void sendTo(SocketClientHandler socketClientHandler, MessageToClient message) {
        if(socketClientHandler==null || message==null){
            throw new IllegalArgumentException("nickname not valid or message is null");
        }
//        synchronized (clients){
//            try{
//                clientHandler.onMessage(message);
//            }catch(Exception e){
//                System.out.println("Unable to send message to clients");
//            }
//
//        }
    }

    @Override
    public void sendTo(String playerId, MessageToClient message) {

    }

//    @Override
//    public void broadcast(MessageToClient message) {
//        synchronized (clients){ //Perché l' handler che accede condivide con gli altri client l' oggetto server,
//            //Inoltre anche clients è condiviso tra i vari handlers
//            for (SocketClientHandler clientHandler : clients) {
//                try{
//                    clientHandler.onMessage(message);
//                }catch(Exception e){
//                    System.out.println("Unable to send message to clients");
//                }
//            }
//        }
//    }
}
*/
}

