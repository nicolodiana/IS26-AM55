package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

/**
 * Messaggio locale client-side.
 * Non arriva dal server.
 * Viene creato dal ClientImpl quando il client non riceve più Pong.
 */
public class ConnectionLostMessage extends MessageToClient {

    /**
     * Human-readable connection-loss message exposed to the client view.
     */
    private final String message;

    /**
     * Creates a local notification for a lost server connection.
     *
     * @param message human-readable description of the connection loss
     */
    public ConnectionLostMessage(String message) {
        this.message = message;
    }

    /**
     * Updates the client model to represent a lost connection and crashed game session.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setStateRequest(message);
        model.setGameStarted(false);
        model.setGameCrashed(true);


        model.setInLobby(false);

        model.setLastMessageUpdatedGameView(false);
    }

    /**
     * Performs no server-side delivery because this message is created locally by the client.
     *
     * @param playerId player or session identifier supplied by the delivery interface
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
    }
}