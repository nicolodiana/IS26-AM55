package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

/**
 * Informational game message broadcast to every client in the active game.
 */
public class GameBroadcastInfo extends MessageToClient {

    /**
     * Informational text broadcast to every client in the active game.
     */
    private final String message;

    /**
     * Creates an informational broadcast message.
     *
     * @param message text displayed by clients
     */
    public GameBroadcastInfo(String message) {
        this.message = message;
    }

    /**
     * Clears previous errors and stores the informational text in the model.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }

    /**
     * Broadcasts the message to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}