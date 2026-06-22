package it.polimi.ingsw.am55.utility;

/**
 * Stores and validates the network port configuration used by the server.
 *
 * <p>The server exposes two communication channels: an RMI endpoint and a socket
 * endpoint. Each endpoint must use a valid, non-reserved port in the range
 * {@code 1024} to {@code 65535}, and the two ports must be different.</p>
 *
 * <p>If no command-line arguments are supplied, the default ports are used:
 * {@code 1234} for RMI and {@code 1235} for sockets.</p>
 */
public class ServerConfig {
    /**
     * Port used by the RMI registry.
     */
    private int rmiPort;

    /**
     * Port used by the socket server.
     */
    private int socketPort;

    /**
     * Creates a server configuration with default port values.
     *
     * <p>The default RMI port is {@code 1234}; the default socket port is
     * {@code 1235}.</p>
     */
    public ServerConfig() {
        this.rmiPort = 1234;
        this.socketPort = 1235;
    }

    /**
     * Returns the configured RMI port.
     *
     * @return the RMI port
     */
    public int getRmiPort() {return this.rmiPort;}

    /**
     * Returns the configured socket port.
     *
     * @return the socket port
     */
    public int getSocketPort() {return this.socketPort;}

    /**
     * Reads and validates the server ports from command-line arguments.
     *
     * <p>The expected argument order is:</p>
     * <ol>
     *     <li>{@code args[0]}: RMI port, or blank to use the default value;</li>
     *     <li>{@code args[1]}: socket port, or blank to use the default value.</li>
     * </ol>
     *
     * <p>At most two arguments are accepted. After parsing, the RMI and socket
     * ports must be different.</p>
     *
     * @param args command-line arguments containing optional RMI and socket ports
     * @throws IllegalArgumentException if too many arguments are provided, a port
     *                                  is not numeric, a port is outside the
     *                                  accepted range, or both ports are equal
     */
    public void setPorts(String[] args) throws  IllegalArgumentException{
        if (args.length > 2) {
            throw new IllegalArgumentException("Too much arguments");
        }
        this.rmiPort = parsePortArgument(args, 0, 1234, "RMI port");
        this.socketPort = parsePortArgument(args, 1, 1235, "Socket port");

        if (rmiPort == socketPort) {
            throw new IllegalArgumentException("RMI port and Socket port must be different");
        }
    }

    /**
     * Parses a single port argument or returns the provided default value.
     *
     * <p>The default value is used when the requested argument is missing or blank.
     * Otherwise, the argument is trimmed and validated as a port number.</p>
     *
     * @param args        command-line arguments
     * @param index       index of the port argument to parse
     * @param defaultPort default port returned when the argument is missing or blank
     * @param fieldName   human-readable field name used in validation messages
     * @return the parsed port, or {@code defaultPort} when no value is provided
     * @throws IllegalArgumentException if the provided value is not a valid port
     */
    private int parsePortArgument(String[] args, int index, int defaultPort, String fieldName) {
        if (args.length <= index || args[index].isBlank()) {
            return defaultPort;
        }

        return parsePort(args[index].trim(), fieldName);
    }

    /**
     * Parses and validates a server port.
     *
     * @param input     textual port value
     * @param fieldName human-readable field name used in validation messages
     * @return the parsed port number
     * @throws IllegalArgumentException if the value is not numeric or is outside
     *                                  the accepted range {@code 1024} to {@code 65535}
     */
    private int parsePort(String input, String fieldName) {
        final int port;

        try {
            port = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " is not a number: " + input, e);
        }

        if (port < 1024 || port > 65535) {
            throw new IllegalArgumentException(fieldName + " out of range: must be between " + 1024+ " and " + 65535);
        }
        return port;
    }
}
