package it.polimi.ingsw.am55.network.socket.server;

import it.polimi.ingsw.am55.network.ServerApplication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {

    private final ServerSocket listen;
    private final ServerApplication serverApplication;

    public SocketServer(int port, ServerApplication serverApplication) throws Exception {
        this.listen = new ServerSocket(port);
        this.serverApplication = serverApplication;
    }

    public void start() throws Exception {
        System.out.println("[SOCKET_SERVER] Server in ascolto sulla porta: " + listen.getLocalPort());

        while (true) {
            Socket clientSocket = listen.accept();

            System.out.println("[SOCKET_SERVER] Nuovo client si è connesso: " + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());


            //Viene creato l' handler che si occuperà di gestire il singolo client, perché
            //il metodo readObject è bloccante quindi andrebbe a bloccare il processo principale
            SocketClientHandler handler = new SocketClientHandler(clientSocket, in, out, serverApplication);
        }
    }
}