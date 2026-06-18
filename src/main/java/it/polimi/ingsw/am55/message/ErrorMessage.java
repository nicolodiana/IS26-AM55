package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

/**
 * Error response sent to one client when a requested operation cannot be completed.
 * <p>
 * During create/join setup this message prevents the server from moving the
 * client from the lobby registry to the game registry.
 */
public class ErrorMessage extends MessageToClient {

    /**
     * Human-readable error text shown to the receiving client.
     */
    private final String message;

    /**
     * Creates an error message with the text shown to the client.
     *
     * @param message human-readable error description
     */
    public ErrorMessage(String message) {
        this.message = message;
    }

    /**
     * Stores the error in the client model and exposes it as the latest state request.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.setLastError(message);
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }

    /**
     * Sends the error only to the client identified by the given player id.
     *
     * @param playerId target player identifier
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }

    /**
     * Signals that create/join setup failed.
     *
     * @return always {@code false}
     */
    @Override
    public boolean isConnectionSetupSuccessful() {
        return false;
    }
}
