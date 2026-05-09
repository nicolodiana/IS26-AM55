package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.network.ClientCommands;
import it.polimi.ingsw.am55.network.rmi.client.RmiClient;
import it.polimi.ingsw.am55.network.rmi.server.VirtualServerRmi;
import it.polimi.ingsw.am55.view.cli.CLIView;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    private static final String DEFAULT_HOST = "localhost";
    private static final int RMI_PORT = 1234;
    private static final int SOCKET_PORT = 1235;
    private static final String SERVER_NAME = "GameServer";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            String host = askHost(scanner, args);
            ConnectionTechnology technology = askTechnology(scanner);

            ClientModel model = new ClientModel();
            CLIView view = new CLIView(model);
            model.addObserver(view);

            ClientCommands client = createClient(technology, host, model);

            ClientController controller = new ClientController(client);
            view.setActionHandler(controller);

            view.start();

        } catch (Exception e) {
            System.err.println("Errore durante l'avvio del client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String askHost(Scanner scanner, String[] args) {
        if (args.length > 0 && args[0] != null && !args[0].isBlank()) {
            return args[0];
        }

        System.out.print("IP server [default: localhost]: ");
        String input = scanner.nextLine();

        if (input == null || input.isBlank()) {
            return DEFAULT_HOST;
        }

        return input.trim();
    }

    private static ConnectionTechnology askTechnology(Scanner scanner) {
        while (true) {
            System.out.print("Scegli tecnologia di connessione [rmi/socket]: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "rmi":
                    return ConnectionTechnology.RMI;

                case "socket":
                case "tcp":
                    return ConnectionTechnology.SOCKET;

                default:
                    System.out.println("Tecnologia non valida. Scrivi 'rmi' oppure 'socket'.");
            }
        }
    }

    private static ClientCommands createClient(
            ConnectionTechnology technology,
            String host,
            ClientModel model
    ) throws Exception {
        switch (technology) {
            case RMI:
                return createRmiClient(host, model);

            case SOCKET:
                return createSocketClient(host, model);

            default:
                throw new IllegalArgumentException("Tecnologia non supportata: " + technology);
        }
    }

    private static ClientCommands createRmiClient(String host, ClientModel model) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, RMI_PORT);
        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(SERVER_NAME);

        return new RmiClient(server, model);
    }

    private static ClientCommands createSocketClient(String host, ClientModel model) throws Exception {
        /*
         * Quando avrai implementato SocketClient:
         *
         * return new SocketClient(host, SOCKET_PORT, model);
         */

        throw new UnsupportedOperationException("Socket non ancora implementato.");
    }

    private enum ConnectionTechnology {
        RMI,
        SOCKET
    }
}