package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.rmi.server.RmiServer;
import it.polimi.ingsw.am55.network.socket.server.SocketServer;

import java.net.InetAddress;
import java.rmi.server.ExportException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Server {

    private static final int DEFAULT_RMI_PORT = 1234;
    private static final int DEFAULT_SOCKET_PORT = 1235;
    private static final String RMI_SERVER_NAME = "GameServer";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Scegli tecnologia server [rmi/socket]: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            String hostIp = InetAddress.getLocalHost().getHostAddress();
            System.setProperty("java.rmi.server.hostname", hostIp);

            System.out.println("Server IP: " + hostIp);

            ServerApplication serverApplication = new ServerApplication();

            switch (choice) {
                case "rmi" -> {
                    startRmiServer(serverApplication, DEFAULT_RMI_PORT);
                    System.out.println("Server RMI avviato sulla porta " + DEFAULT_RMI_PORT);
                }

                case "socket" -> {
                    startSocketServer(serverApplication, DEFAULT_SOCKET_PORT);
                    System.out.println("Server Socket avviato sulla porta " + DEFAULT_SOCKET_PORT);
                }

                default -> System.out.println("Scelta non valida. Usa 'rmi' oppure 'socket'.");
            }

        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startRmiServer(ServerApplication serverApplication, int rmiPort) throws Exception {
        RmiServer rmiServer = new RmiServer(serverApplication);

        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(rmiPort);
        } catch (ExportException alreadyExists) {
            registry = LocateRegistry.getRegistry(rmiPort);
        }

        registry.rebind(RMI_SERVER_NAME, rmiServer);
    }

    private static void startSocketServer(ServerApplication serverApplication, int socketPort) throws Exception {
        SocketServer socketServer = new SocketServer(socketPort, serverApplication);
        socketServer.start();
    }
}