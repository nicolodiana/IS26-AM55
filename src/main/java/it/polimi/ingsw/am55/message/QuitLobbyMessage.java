package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Message used to disconnect one lobby client or all lobby clients.
 */
public class QuitLobbyMessage extends MessageToClient{
    /**
     * Human-readable text explaining why the client leaves the lobby.
     */
    private String message;
    /**
     * Whether the message must be broadcast to all lobby clients or sent only to one session.
     */
    private boolean broadcast;

    /**
     * Creates a lobby-quit message.
     *
     * @param message   text displayed by clients
     * @param broadcast {@code true} to broadcast to the whole lobby, {@code false} for one session
     */
    public QuitLobbyMessage(String message,boolean broadcast){
        this.message = message;
        this.broadcast=broadcast;
    }


    /**
     * Updates the client model to represent that the client has left the lobby.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setInLobby(false);
        model.setGameStarted(false);
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }
    /**
     * Delivers the message either to all lobby clients or to one session.
     *
     * @param sessionId target lobby session identifier when {@code broadcast} is {@code false}
     * @param context   server delivery context
     */
    @Override
    public void deliver(String sessionId, MessageDelivery context) {
        if(broadcast){
            context.broadcastToLobby(this);
        }else{
            context.sendToSession(sessionId, this);
        }
    }

    /**
     * Closes the client connection after the message has been processed.
     *
     * @param client client-side network control interface
     * @throws Exception if the connection cannot be closed
     */
    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        client.closeConnection();
    }

}
