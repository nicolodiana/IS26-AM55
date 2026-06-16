package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.network.middleware.ClientSkeleton;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
            out.flush();

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            new ClientSkeleton(clientSocket, in, out, serverApplication);
        }
    }
}