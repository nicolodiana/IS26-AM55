package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Unicast message sent to a player while the game is waiting for more participants.
 */
public class WaitingMessage extends MessageToClient {

    /**
     * Human-readable waiting message shown while the client has no actionable game update.
     */
    private final String message;
    /**
     * Game snapshot delivered together with the waiting state.
     */
    private final GameView gameView;

    /**
     * Creates a waiting message.
     *
     * @param message  text displayed by the waiting client
     * @param gameView partial or current game snapshot associated with the waiting state
     */
    public WaitingMessage(String message, GameView gameView) {
        this.message = message;
        this.gameView = gameView;
    }

    /**
     * Stores the waiting state in the client model.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView); 
        model.setStateRequest(message);
        model.setGameStarted(false);
        model.setInLobby(false); 
        model.setLastMessageUpdatedGameView(false);
    }

    /**
     * Sends the waiting message only to the target player.
     *
     * @param playerId target player identifier
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }
}




