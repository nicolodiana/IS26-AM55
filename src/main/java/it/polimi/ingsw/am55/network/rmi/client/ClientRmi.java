package it.polimi.ingsw.am55.network.rmi.client;

import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.network.rmi.server.ServerRmi;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;
import it.polimi.ingsw.am55.view.ClientView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientRmi extends UnicastRemoteObject implements VirtualClientRmi {
   private ClientController clientController;
    private final VirtualServerRmi server;

    public ClientRmi(VirtualServerRmi server) throws RemoteException {
        super();
        this.server = server;
        //this.clientController = clientController;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    // to connect the client to the server
    public void run() throws RemoteException {
        this.server.connectClient(clientController.getId(), this);
        System.out.println("Client connected");
        runCli();
    }

    // per ora usato come test
    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            int command = scan.nextInt();

            if (command == 0) {
                //tolgo il match dalla mappa e quindi anche tutti i client
                System.out.println("Hai finito");
                this.server.endMatchConnection(this.clientController.getMatchId());
            } else {
                System.out.println(command);
            }
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "TestServer";

        Registry registry = LocateRegistry.getRegistry(args.length > 0 ? args[0] : "localhost", 1234);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);
    }

    //---------------------METODI GETTER---------------------
    public String getNickname() {
        return this.clientController.getNickname();
    }

    public String getTotem() {
        return this.clientController.getTotem();
    }

    //---------------------MESSAGE TO UPDATE THE VIEW---------------------

    @Override
    public void updateStateLobby(String message) throws RemoteException {
        clientController.showUpdate(message);
    }
}
