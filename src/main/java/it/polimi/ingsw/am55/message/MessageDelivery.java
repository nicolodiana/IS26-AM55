package it.polimi.ingsw.am55.message;




/**
 * Delivery strategy used by messages to select their target audience.
 * <p>
 * The server implements this interface because it owns the network references to
 * lobby clients and game clients. Each concrete {@link MessageToClient} chooses
 * the appropriate delivery method without exposing the server's internal maps.
 */
public interface MessageDelivery {
    /**
     * Sends a message to a single player already registered in the active game.
     *
     * @param playerId player identifier used to locate the target client
     * @param message  message to deliver
     */
    void sendTo(String playerId, MessageToClient message);
    /**
     * Broadcasts a message to all clients registered in the active game.
     *
     * @param message message to deliver to game clients
     */
    void broadcast(MessageToClient message);
    
    
    /**
     * Broadcasts a message to all clients currently waiting in the lobby.
     *
     * @param message message to deliver to lobby clients
     */
    void broadcastToLobby(MessageToClient message);
    
    /**
     * Sends a message to a single lobby client identified by its session id.
     *
     * @param sessionId temporary lobby session identifier
     * @param message   message to deliver
     */
    void sendToSession(String sessionId, MessageToClient message);

}