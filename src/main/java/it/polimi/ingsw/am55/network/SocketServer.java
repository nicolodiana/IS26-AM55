package it.polimi.ingsw.am55.network;

import it.polimi.ingsw.am55.network.middleware.ClientSkeleton;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server endpoint used by the socket transport.
 * <p>
 * For each accepted TCP connection, the server creates a {@link ClientSkeleton}
 * that translates serialized socket commands into calls on {@link ServerApplication}.
 */
public class SocketServer {

    /**
     * Server socket that accepts incoming TCP client connections.
     */
    private final ServerSocket listen;
    /**
     * Shared server application used by each socket skeleton to execute received commands.
     */
    private final ServerApplication serverApplication;

    /**
     * Creates a socket server bound to the given port.
     *
     * @param port              TCP port used to listen for clients
     * @param serverApplication shared server application that executes received commands
     * @throws Exception if the server socket cannot be opened
     */
    public SocketServer(int port, ServerApplication serverApplication) throws Exception {
        this.listen = new ServerSocket(port);
        this.serverApplication = serverApplication;
    }

    /**
     * Accepts socket connections indefinitely and creates a skeleton for each client.
     *
     * @throws Exception if the accept loop or stream creation fails
     */
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