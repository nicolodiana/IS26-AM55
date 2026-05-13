package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.rmi.server.RmiServer;

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
            int rmiPort = parsePortArg(args, 0, DEFAULT_RMI_PORT);
            int socketPort = parsePortArg(args, 1, DEFAULT_SOCKET_PORT);

            String hostIp = InetAddress.getLocalHost().getHostAddress();

            System.setProperty("java.rmi.server.hostname", hostIp);

            System.out.println("Server IP: " + hostIp);

            ServerApplication serverApplication = new ServerApplication();

            startRmiServer(serverApplication, rmiPort);

            System.out.println("Server avviato correttamente.");
            System.out.println("RMI attivo su " + hostIp + ":" + rmiPort);
            System.out.println("Socket non ancora attivo. Porta prevista: " + socketPort);

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

    private static int parsePortArg(String[] args, int index, int defaultValue) {
        if (args == null || args.length <= index || args[index] == null || args[index].isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            System.out.println("Porta non valida: " + args[index] + ". Uso default: " + defaultValue);
            return defaultValue;
        }
    }
}