package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.SocketServer;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class Server {

    private static final int DEFAULT_RMI_PORT = 1234;
    private static final int DEFAULT_SOCKET_PORT = 1235;
    private static final String RMI_SERVER_NAME = "GameServer";

    public static void main(String[] args) {
        try {
            String hostIp = InetAddress.getLocalHost().getHostAddress();
            System.setProperty("java.rmi.server.hostname", hostIp);

            System.out.println("Server IP: " + hostIp);

            ServerApplication serverApplication = new ServerApplication();

            startRmiServer(serverApplication, DEFAULT_RMI_PORT);
            System.out.println("[SERVER] RMI avviato sulla porta " + DEFAULT_RMI_PORT);

            startSocketServer(serverApplication, DEFAULT_SOCKET_PORT);
            System.out.println("[SERVER] Socket avviato sulla porta " + DEFAULT_SOCKET_PORT);

        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startRmiServer(ServerApplication serverApplication, int rmiPort) throws Exception {
        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(rmiPort);
        } catch (ExportException alreadyExists) {
            registry = LocateRegistry.getRegistry(rmiPort);
        }

        registry.rebind(RMI_SERVER_NAME, serverApplication);
    }

    private static void startSocketServer(ServerApplication serverApplication, int socketPort) throws Exception {
        SocketServer socketServer = new SocketServer(socketPort, serverApplication);

        Thread socketThread = new Thread(() -> {
            try {
                socketServer.start();
            } catch (Exception e) {
                System.err.println("[SERVER] Errore socket server: " + e.getMessage());
                e.printStackTrace();
            }
        });

        socketThread.setName("socket-server");
        socketThread.start();
    }
}