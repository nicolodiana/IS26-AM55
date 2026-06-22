
package it.polimi.ingsw.am55.utility;

/**
 * Stores and validates the startup configuration used by the client application.
 *
 * <p>The configuration contains the server host, the RMI and socket ports, the
 * selected networking technology, and the selected user interface mode.</p>
 *
 * <p>Default values are provided for host and ports. The connection technology
 * and view mode must be explicitly selected through the command-line arguments
 * before they are read by the application.</p>
 *
 * <p>The expected argument order is:</p>
 * <ol>
 *     <li>{@code args[0]}: server host;</li>
 *     <li>{@code args[1]}: connection technology;</li>
 *     <li>{@code args[2]}: port for the selected connection technology;</li>
 *     <li>{@code args[3]}: view mode.</li>
 * </ol>
 */
public class ClientConfig {


    /**
     * Default RMI registry port used when no custom RMI port is provided.
     */
    private static final int DEFAULT_RMI_PORT = 1234;

    /**
     * Default socket server port used when no custom socket port is provided.
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
     * Hostname or IP address of the server.
     */
    private String host;

    /**
     * Port used for RMI communication.
     */
    private int rmiPort;

    /**
     * Port used for socket communication.
     */
    private int socketPort;

    /**
     * Selected network communication technology.
     */
    private ConnectionTechnology connectionTechnology;

    /**
     * Selected client view mode.
     */
    private ViewMode viewMode;


    /**
     * Creates a client configuration with default host and port values.
     *
     * <p>The default host is {@code localhost}, the default RMI port is
     * {@code 1234}, and the default socket port is {@code 1235}.</p>
     */
    public ClientConfig() {
        host = "localhost";
        rmiPort = DEFAULT_RMI_PORT;
        socketPort = DEFAULT_SOCKET_PORT;
    }

    /**
     * Returns the configured server host.
     *
     * @return the server host
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the configured RMI port.
     *
     * @return the RMI port
     */
    public int getRmiPort() {
        return rmiPort;
    }

    /**
     * Returns the configured socket port.
     *
     * @return the socket port
     */
    public int getSocketPort() {
        return socketPort;
    }


    /**
     * Returns the selected connection technology.
     *
     * @return the selected connection technology
     */
    public ConnectionTechnology getConnectionTechnology() {
        return connectionTechnology;
    }

    /**
     * Returns the selected view mode.
     *
     * @return the selected view mode
     */
    public ViewMode getViewMode() {
        return viewMode;
    }

    /**
     * Reads and validates the server host from {@code args[0]}.
     *
     * <p>If the provided host is {@code null} or blank, the current host value is
     * kept unchanged. With the current validation logic, only IPv4 addresses
     * without leading zeroes are considered valid custom host values.</p>
     *
     * @param args command-line arguments containing the host at index {@code 0}
     * @throws IllegalArgumentException if the host value is not valid
     * @throws ArrayIndexOutOfBoundsException if {@code args} does not contain index {@code 0}
     */
    public void setHost(String[] args){
        String hostname = args[0];
        if (hostname!=null) {
            hostname= hostname.toLowerCase();
            if(hostname.isBlank() || hostname.equals("localhost")) return;
            if(!isValidHost(hostname)){
                throw new IllegalArgumentException("Invalid hostname "+hostname);
            }else{
                this.host = hostname;
            }
        }

    }

    /**
     * Reads and validates the port from {@code args[2]}.
     *
     * <p>The parsed port is assigned to either the RMI port or the socket port,
     * depending on the connection technology selected through
     * {@link #setConnectionTechnology(String[])}.</p>
     *
     * @param args command-line arguments containing the port at index {@code 2}
     * @throws IllegalArgumentException if the port is not numeric or outside the allowed range
     * @throws ArrayIndexOutOfBoundsException if {@code args} does not contain index {@code 2}
     * @throws NullPointerException if the connection technology has not been selected yet
     */
    public void setPort(String[] args) {
        String port = args[2];
        if (port == null || port.isBlank()) {
            return;
        }
        try {
            setPort(Integer.parseInt(port.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port: must be a number.", e);
        }
    }

    /**
     * Validates and assigns the provided port to the selected connection technology.
     *
     * @param port port to assign
     * @throws IllegalArgumentException if the port is outside the accepted range
     * @throws IllegalArgumentException if the user didn't put the technology
     * @throws NullPointerException if the connection technology has not been selected
     */
    private void setPort(int port) {
        validatePort(port);
         switch (connectionTechnology) {
                case RMI -> this.rmiPort = port;
                case SOCKET -> this.socketPort = port;
         }
        if (rmiPort == socketPort) {
            throw new IllegalArgumentException(
                    "RMI port and Socket port must be different. " +
                            "The selected port conflicts with the port reserved for the other network technology."
            );
        }

    }

    /**
     * Reads and validates the connection technology from {@code args[1]}.
     *
     * <p>Accepted values are {@code rmi}, {@code socket}, and {@code tcp};
     * matching is case-insensitive and ignores surrounding whitespace.</p>
     *
     * @param args command-line arguments containing the technology at index {@code 1}
     * @throws IllegalArgumentException if the technology value is blank or unsupported
     * @throws ArrayIndexOutOfBoundsException if {@code args} does not contain index {@code 1}
     */
    public void setConnectionTechnology(String[] args) {
        String technology = args[1];
        if (technology == null || technology.isBlank()) {
            throw new IllegalArgumentException("Please select a network technology.");
        }
        switch (technology.trim().toLowerCase()) {
            case "rmi" -> this.connectionTechnology = ConnectionTechnology.RMI;
            case "socket", "tcp" -> this.connectionTechnology = ConnectionTechnology.SOCKET;
            default -> throw new IllegalArgumentException("Network technology is not valid, digit RMI or socket");
        }
    }

    /**
     * Reads and validates the view mode from {@code args[3]}.
     *
     * <p>Accepted values are {@code cli}, {@code tui}, {@code gui}, and
     * {@code javafx}; matching is case-insensitive and ignores surrounding
     * whitespace.</p>
     *
     * @param args command-line arguments containing the view mode at index {@code 3}
     * @throws IllegalArgumentException if the view mode value is blank or unsupported
     * @throws ArrayIndexOutOfBoundsException if {@code args} does not contain index {@code 3}
     */
    public void setViewMode(String[] args) {
        String viewMode = args[3];
        if (viewMode == null || viewMode.isBlank()) {
            throw new IllegalArgumentException("Please select a view mode");
        }

        switch (viewMode.trim().toLowerCase()) {
            case "cli", "tui" -> this.viewMode = ViewMode.CLI;
            case "gui", "javafx" -> this.viewMode = ViewMode.GUI;
            default -> throw new IllegalArgumentException(
                    "Not valid view mode, please digit cli or gui."
            );
        }
    }


    /**
     * Ensures that a port number is within the valid user-space TCP/UDP range
     * accepted by this application.
     *
     * @param port port number to validate
     * @throws IllegalArgumentException if the port is lower than {@value #MIN_PORT}
     *                                  or greater than {@value #MAX_PORT}
     */
    private void validatePort(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException(
                    "Port is not valid, please choose between  " + MIN_PORT + " and " + MAX_PORT + "."
            );
        }
    }

    /**
     * Validates a host string according to the current client rules.
     *
     * <p>The host must not contain spaces or protocol prefixes and must be a
     * syntactically valid IPv4 address.</p>
     *
     * @param host host string to validate
     * @return {@code true} if the host is accepted; {@code false} otherwise
     */
    private static boolean isValidHost(String host) {

        if (host.contains(" ") || host.contains("://")) {
            return false;
        }

        return isValidIpv4(host);
    }

    /**
     * Checks whether a string is a valid IPv4 address.
     *
     * <p>Each octet must be numeric, between {@code 0} and {@code 255}, and must
     * not contain leading zeroes unless the octet is exactly {@code 0}.</p>
     *
     * @param host host string to validate as an IPv4 address
     * @return {@code true} if the host is a valid IPv4 address; {@code false} otherwise
     */
    private static boolean isValidIpv4(String host) {
        String[] parts = host.split("\\.");

        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                if (part.isBlank() || part.length() > 3) {
                    return false;
                }

                int value = Integer.parseInt(part);

                if (value < 0 || value > 255) {
                    return false;
                }

                if (part.length() > 1 && part.startsWith("0")) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Supported client-server communication technologies.
     */
    public enum ConnectionTechnology {
        /**
         * Java RMI-based communication.
         */
        RMI,

        /**
         * Socket-based communication.
         */
        SOCKET
    }

    /**
     * Supported client user interface modes.
     */
    public enum ViewMode {
        /**
         * Command-line interface.
         */
        CLI,

        /**
         * JavaFX graphical user interface.
         */
        GUI
    }
}
