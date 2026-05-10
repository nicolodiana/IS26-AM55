package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.rmi.server.RmiServer;
import it.polimi.ingsw.am55.network.socket.server.SocketServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    private static final int RMI_PORT = 1234;
    private static final int SOCKET_PORT = 1235;
    private static final String RMI_SERVER_NAME = "GameServer";

    public static void main(String[] args) {
        try {
            ServerApplication serverApplication = new ServerApplication();

            //startRmiServer(serverApplication);

            // Quando implementerete socket:
             startSocketServer(serverApplication);

            System.out.println("Server avviato correttamente.");
//            System.out.println("RMI attivo sulla porta " + RMI_PORT);
//            System.out.println("Socket non ancora attivo. Porta prevista: " + SOCKET_PORT);

        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startRmiServer(ServerApplication serverApplication) throws Exception {
        RmiServer rmiServer = new RmiServer(serverApplication);

        Registry registry = LocateRegistry.createRegistry(RMI_PORT);
        registry.rebind(RMI_SERVER_NAME, rmiServer);
    }


    private static void startSocketServer(ServerApplication serverApplication) throws Exception {
        SocketServer socketServer = new SocketServer(SOCKET_PORT, serverApplication);
        socketServer.start();
    }

}