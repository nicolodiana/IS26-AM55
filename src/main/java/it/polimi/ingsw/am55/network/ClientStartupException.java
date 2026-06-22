package it.polimi.ingsw.am55.network;

/**
 * Application-level exception used when the client cannot be started because
 * the selected server endpoint is not reachable or not correctly configured.
 */
public class ClientStartupException extends Exception {

    public ClientStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
