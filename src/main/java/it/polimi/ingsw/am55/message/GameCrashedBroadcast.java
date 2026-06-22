package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Broadcast message sent when the active game is terminated because a client disconnected.
 */
public class GameCrashedBroadcast extends MessageToClient{
    /**
     * Crash notification text displayed before clients close their network connection.
     */
    private final String message;

    /**
     * Creates a game-crash notification.
     *
     * @param message text displayed by clients
     */
    public GameCrashedBroadcast(String message) {
        this.message = message;
    }
    /**
     * Marks the client model as ended and crashed.
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
     * Broadcasts the crash notification to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }

    /**
     * Closes the client connection after the crash notification has been processed.
     *
     * @param client client-side network control interface
     * @throws Exception if the connection cannot be closed
     */
    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        client.stopPing();
        client.closeConnection();
    }
}
