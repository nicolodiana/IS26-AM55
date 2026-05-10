package it.polimi.ingsw.am55.network.socket.server;

import it.polimi.ingsw.am55.network.ServerApplication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {

    private final List<SocketClientHandler> clients;
    private final ServerSocket listen;
    private final ServerApplication serverApplication;

    public SocketServer(int port, ServerApplication serverApplication) throws Exception {
        this.listen = new ServerSocket(port);
        this.serverApplication = serverApplication;
        this.clients = new ArrayList<>();
    }

    public void start() throws Exception {
        System.out.println("[SOCKET_SERVER] Server listening on port: " + listen.getLocalPort());

        while (true) {
            Socket clientSocket = listen.accept();

            System.out.println("[SOCKET_SERVER] New client connected: "
                    + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            SocketClientHandler handler =
                    new SocketClientHandler(clientSocket, in, out, serverApplication);

            synchronized (clients) {
                clients.add(handler);
            }


        }
    }
}