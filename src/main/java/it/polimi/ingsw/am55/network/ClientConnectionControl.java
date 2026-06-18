package it.polimi.ingsw.am55.network;






/**
 * Defines the client-side network control operations that technical messages can trigger.
 * <p>
 * The abstraction keeps transport-related side effects outside the model update logic:
 * each concrete message can request the operation it needs without forcing the client
 * model to inspect message types explicitly.
 */
public interface ClientConnectionControl {
    /**
     * Starts the client heartbeat mechanism after the server authorizes the connection.
     *
     * @throws Exception if the heartbeat mechanism cannot be started
     */
    void startPing() throws Exception;
    /**
     * Stops the client heartbeat and server availability timers.
     *
     * @throws Exception if the heartbeat mechanism cannot be stopped
     */
    void stopPing() throws Exception;
    /**
     * Records that a pong response has been received from the server.
     */
    void pongFromSever();
    /**
     * Closes the active client connection and terminates the client process.
     */
    void closeConnection();
}
