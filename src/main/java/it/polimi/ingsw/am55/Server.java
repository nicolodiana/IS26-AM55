package it.polimi.ingsw.am55;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.network.SocketServer;
import it.polimi.ingsw.am55.utility.ServerConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

/**
 * Application entry point for the game server.
 *
 * <p>The server initializes the shared {@link ServerApplication} instance and
 * exposes it through both supported communication technologies: Java RMI and a
 * socket server. Port values are read and validated by {@link ServerConfig}.</p>
 *
 * <p>During startup, the class prints the private and public IP addresses, sets
 * the RMI hostname to the detected private IP address, starts or reuses the RMI
 * registry, binds the server application under the configured RMI name, and
 * starts the socket server.</p>
 *
 * @see ServerConfig
 * @see ServerApplication
 * @see SocketServer
 */
public class Server {

    /**
     * Default RMI registry port.
     */
    private static final int DEFAULT_RMI_PORT = 1234;

    /**
     * Default socket server port.
     */
    private static final int DEFAULT_SOCKET_PORT = 1235;

    /**
     * Minimum accepted port number.
     */
    private static final int MIN_PORT = 1024;

    /**
     * Maximum accepted port number.
     */
    private static final int MAX_PORT = 65535;

    /**
     * Name used to bind the server application in the RMI registry.
     */
    private static final String RMI_SERVER_NAME = "GameServer";

    /**
     * Attempts to detect the public IP address of the machine running the server.
     *
     * <p>The lookup is performed through the external {@code api.ipify.org}
     * service. If the address cannot be retrieved, a fallback message is returned
     * instead of propagating the exception.</p>
     *
     * @return the detected public IP address, or {@code "non rilevabile"} when it cannot be detected
     */
    private static String getPublicIp() {
        try {
            URL url = new URL("https://api.ipify.org");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream())
            );
            return reader.readLine();
        } catch (Exception e) {
            return "non rilevabile";
        }
    }

    /**
     * Starts the server application.
     *
     * <p>The accepted command-line syntax is:</p>
     * <pre>{@code
     * java -jar server.jar [rmiPort] [socketPort]
     * }</pre>
     *
     * <p>If no arguments are provided, the server uses the default RMI and socket
     * ports. Invalid command-line parameters terminate the application with exit
     * code {@code 2}; other startup failures terminate it with exit code {@code 1}.</p>
     *
     * @param args optional command-line arguments containing the RMI port at index
     *             {@code 0} and the socket port at index {@code 1}
     */
    public static void main(String[] args) {
        try {
            ServerConfig config = new ServerConfig();
            config.setPorts(args);

            String privateIp = InetAddress.getLocalHost().getHostAddress();
            String publicIp = getPublicIp();

            System.setProperty("java.rmi.server.hostname", privateIp);

            System.out.println("Server private IP: " + privateIp);
            System.out.println("Server public IP: " + publicIp);

            ServerApplication serverApplication = new ServerApplication();

            startRmiServer(serverApplication, config.getRmiPort());
            System.out.println("[SERVER] RMI started on the port " + config.getRmiPort());

            startSocketServer(serverApplication, config.getSocketPort());
            System.out.println("[SERVER] Socket started on the port" + config.getSocketPort());

        } catch (IllegalArgumentException e) {
            System.err.println("[SERVER] Invalid parameters: " + e.getMessage());
            printUsage();
            System.exit(2);
        } catch (Exception e) {
            System.err.println("[SERVER] Error starting server: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Starts the RMI registry on the specified port and binds the server application.
     *
     * <p>If a registry is already available on the requested port, it is reused.
     * The server application is then bound, or rebound, using the configured RMI
     * server name.</p>
     *
     * @param serverApplication remote server application to expose through RMI
     * @param rmiPort           port where the RMI registry should be created or reused
     * @throws Exception if the registry cannot be created or the server cannot be rebound
     */
    private static void startRmiServer(ServerApplication serverApplication, int rmiPort) throws Exception {
        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(rmiPort);
        } catch (ExportException alreadyExists) {
            registry = LocateRegistry.getRegistry(rmiPort);
        }

        registry.rebind(RMI_SERVER_NAME, serverApplication);
    }

    /**
     * Starts the socket server on the specified port.
     *
     * <p>If the socket server cannot be started, the error is printed and the
     * application terminates with exit code {@code 1}.</p>
     *
     * @param serverApplication server application used to handle socket requests
     * @param socketPort        port where the socket server should listen
     * @throws Exception if the socket server cannot be created
     */
    private static void startSocketServer(ServerApplication serverApplication, int socketPort) throws Exception {
        SocketServer socketServer = new SocketServer(socketPort, serverApplication);

        try {
            socketServer.start();
        } catch (Exception e) {
            System.out.println("[SERVER] Connection error in socket server: " + e.getMessage());
            System.exit(1);
        }

    }

    /**
     * Prints the server command-line usage instructions to the standard error stream.
     */
    private static void printUsage() {
        System.err.println("Correct use:");
        System.err.println("java -jar server.jar [rmiPort] [socketPort]");
        System.err.println("Default:");
        System.err.println("rmiPort=1234, socketPort=1235");
        System.err.println("Examples:");
        System.err.println("java -jar server.jar");
        System.err.println("java -jar server.jar 1234 1235");
        System.err.println("java -jar server.jar \"\" 1235");
    }
}
