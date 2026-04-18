package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.network.rmi.client.ClientRmi;
import it.polimi.ingsw.am55.network.rmi.client.VirtualClientRmi;
import it.polimi.ingsw.am55.network.rmi.server.ServerRmi;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;



public class ClientView implements VirtualView{
    private ClientController controller;
    private String stateRequest;

    public ClientView(ClientController controller) {
        this.controller = controller;
    }
    // DORVREMO CAMBIARE NOME CLASSE PERCHE QUESTI CONTROLLI POTREMMO FARLI FARE AD ALTRI TENENDO LA VIEW MOLTO ESTERNA
    public void start() throws RemoteException {
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("> ");
            //String line = input.nextLine();

            // solo per test in questo momento
            String command = input.nextLine();
            //String[] parts = line.split(", ");
            //String command = parts[0];
            //int matchId = Integer.parseInt((parts[1]));
            //int playerId = Integer.parseInt((parts[2]));
            //int numPlayers = 0;
            //if (parts.length == 4) numPlayers= Integer.parseInt(parts[3]);
            //

            if (command.equalsIgnoreCase("create game")) {
                this.controller.createGame(1, 1, 2);
                System.out.println(stateRequest);
            }
            else if (command.equalsIgnoreCase("join game")) {
                this.controller.joinGame(1, 1);
                System.out.println(stateRequest);
            }
            else System.out.println("This command doesn't exist. Please try another one");
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "TestServer";
        Registry registry = LocateRegistry.getRegistry(args.length > 0 ? args[0] : "localhost", 1234);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);
        VirtualClientRmi vcr = new ClientRmi(server);
        ClientController controller = new ClientController(1, 2, server, vcr);
        ClientView view = new ClientView(controller);

        vcr.setClientController(controller);
        controller.setClientView(view);

        view.start();

    }

    public void onUpdateStateRequest(String message) {
        stateRequest = message;
    }

    public String getStateRequest() {
        return stateRequest;
    }
}
