package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.network.*;

import it.polimi.ingsw.am55.network.middleware.ServerStub;
import it.polimi.ingsw.am55.view.cli.CLIView;
import it.polimi.ingsw.am55.view.gui.JavaFXGui;
import it.polimi.ingsw.am55.virtualview.VirtualServer;

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
            ViewMode viewMode = askViewMode(scanner);
            ClientModel model = new ClientModel();

            /*
             * Creo il client di rete, ma NON faccio ancora connect().
             * La connect() manda RegisterLobbyCommand, quindi deve partire
             * solo dopo che la view è già observer del model.
             */
            ClientImpl client = createClient(technology, host, model);
            ClientController controller = new ClientController(client);

            switch (viewMode) {
                case CLI -> startCli(model, controller, client);
                case GUI -> startGui(model, controller, client);
                default -> throw new IllegalArgumentException("View mode non supportata: " + viewMode);
            }

        } catch (Exception e) {
            System.err.println("[CLIENT] Errore durante l'avvio del client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startCli(
            ClientModel model,
            ClientController controller,
            ClientImpl client
    ) throws Exception {
        CLIView view = new CLIView(model);

        /*
         * Observer registrato PRIMA della connect().
         */
        model.addObserver(view);

        view.setActionHandler(controller);
        view.start();

        /*
         * Da qui parte RegisterLobbyCommand.
         * Ora la CLI riceverà LobbyStatusMessage.
         */
        client.connect();
    }

    /**
     * Starts the JavaFX GUI and connects the network client only after the GUI
     * has registered itself as an observer of the model.
     *
     * @param model shared client model
     * @param controller command handler used by the GUI
     * @param client network client
     * @throws Exception if the GUI initialization wait is interrupted
     */
    private static void startGui(
            ClientModel model,
            ClientController controller,
            ClientImpl client
    ) throws Exception {
        Thread guiThread = new Thread(
                () -> JavaFXGui.launchGui(model, controller),
                "JavaFX-Launcher-Thread"
        );
        guiThread.setDaemon(false);
        guiThread.start();

        JavaFXGui.awaitGuiReady();

        Thread connectThread = new Thread(() -> {
            try {
                client.connect();
            } catch (Exception e) {
                System.err.println("[CLIENT] Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        }, "GUI-Connect-Thread");

        connectThread.setDaemon(true);
        connectThread.start();
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

    private static ViewMode askViewMode(Scanner scanner) {
        while (true) {
            System.out.print("Scegli interfaccia [cli/gui]: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "cli":
                case "tui":
                    return ViewMode.CLI;

                case "gui":
                case "javafx":
                    return ViewMode.GUI;

                default:
                    System.out.println("Interfaccia non valida. Scrivi 'cli' oppure 'gui'.");
            }
        }
    }

    private static ClientImpl createClient(
            ConnectionTechnology technology,
            String host,
            ClientModel model
    ) throws Exception {
        return switch (technology) {
            case RMI -> createRmiClient(host, model);
            case SOCKET -> createSocketClient(host, model);
        };
    }

    private static ClientImpl createRmiClient(String host, ClientModel model) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, RMI_PORT);
        VirtualServer server = (VirtualServer) registry.lookup(SERVER_NAME);

        return new ClientImpl(server, model);
    }

    private static ClientImpl createSocketClient(String host, ClientModel model) throws Exception {
        ServerStub serverStub = new ServerStub(host, SOCKET_PORT);

        ClientImpl client = new ClientImpl(serverStub, model);

        serverStub.startListener(client);

        return client;
    }

    private enum ConnectionTechnology {
        RMI,
        SOCKET
    }

    private enum ViewMode {
        CLI,
        GUI
    }
}