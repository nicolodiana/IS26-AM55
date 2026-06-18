package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Game-state snapshot broadcast to clients after a meaningful game update.
 */
public class UpdateViewMessage extends MessageToClient {

    /**
     * Updated game snapshot that replaces the client-side view of the game.
     */
    private final GameView gameView;
    /**
     * Human-readable state message associated with the game update.
     */
    private final String message;

    /**
     * Creates a game-view update with a custom message.
     *
     * @param gameView game snapshot to store in the client model
     * @param message  text displayed by clients
     */
    public UpdateViewMessage(GameView gameView, String message) {
        this.gameView = gameView;
        this.message = message;
    }

    /**
     * Applies the game snapshot to the client model and switches the client out of lobby mode.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView);
        model.setStateRequest(message);
        model.setGameStarted(true);
        model.setInLobby(false); 
        model.setLastMessageUpdatedGameView(true);
    }

    /**
     * Broadcasts the game-view update to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}