package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.ClientController;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.network.client.ClientImpl;
import it.polimi.ingsw.am55.network.ClientImplFactory;
import it.polimi.ingsw.am55.network.networkException.ClientStartupException;
import it.polimi.ingsw.am55.utility.ClientConfig;
import it.polimi.ingsw.am55.view.launcher.ClientViewLauncher;
import it.polimi.ingsw.am55.view.launcher.ClientViewLauncherFactory;

/**
 * Application entry point for the game client.
 *
 * <p>The client reads its startup parameters through {@link ClientConfig}, creates
 * the client-side model and controller, creates the proper network client through
 * {@link ClientImplFactory}, and starts the selected user interface through a
 * generic {@link ClientViewLauncher}.</p>
 *
 * <p>The class coordinates the bootstrap phase only: configuration parsing,
 * network-client creation and view-specific startup details are delegated to
 * dedicated collaborators.</p>
 */
public class Client {

    /**
     * Starts the client application.
     *
     * <p>The accepted command-line syntax is:</p>
     *
     * <pre>{@code
     * java -jar client.jar <host> <rmi|socket> <port> <cli|gui>
     * }</pre>
     *
     * @param args command-line arguments containing server host, connection
     *             technology, port and view mode
     */
    public static void main(String[] args) {
        try {
            ClientConfig config = new ClientConfig();
            config.setHost(args);
            config.setConnectionTechnology(args);
            config.setPort(args);
            config.setViewMode(args);

            ClientModel model = new ClientModel();
            ClientImpl client = ClientImplFactory.create(config, model);
            UserActionHandler controller = new ClientController(client);

            ClientViewLauncher launcher = ClientViewLauncherFactory.create(config.getViewMode(), model, controller, client);

            launcher.start();

        } catch (ClientStartupException e) {
            printConnectionError(e.getMessage());

        } catch (IllegalArgumentException e) {
            printCorrectUsage(e.getMessage());

        } catch (Exception e) {
            System.err.println("[CLIENT] Unexpected error during startup: " + e.getMessage());
        }
    }

    private static void printConnectionError(String message) {
        System.err.println();
        System.err.println("Unable to start client.");
        System.err.println(message);
        System.err.println();
        System.err.println("Check that:");
        System.err.println("- the server is started;");
        System.err.println("- host and port are correct;");
        System.err.println("- the technology chosen is the same as that used by the server.");
        System.err.println();
    }

    private static void printCorrectUsage(String message) {
        System.err.println();
        System.err.println("Invalid client configuration: " + message);
        System.err.println();
        System.err.println("Correct use:");
        System.err.println("java -jar client.jar <host> <rmi|socket> <port> <cli|gui>");
        System.err.println();
        System.err.println("Example:");
        System.err.println("java -jar client.jar localhost socket 1235 cli");
        System.err.println();
    }
}
