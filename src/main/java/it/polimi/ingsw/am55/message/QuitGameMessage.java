package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.network.ClientConnectionControl;
import it.polimi.ingsw.am55.network.command.QuitGameCommand;

import java.io.Serializable;

/**
 * Broadcast message sent when the active game is intentionally closed.
 */
public class QuitGameMessage extends MessageToClient{

    /**
     * Last game snapshot delivered before the client leaves or the game is closed.
     */
    private GameView gameView;
    /**
     * Human-readable text explaining the game quit outcome.
     */
    private String message;

    /**
     * Creates a game-quit message.
     *
     * @param gameView final or latest game snapshot to show before closing
     * @param message  text displayed by clients
     */
    public QuitGameMessage(GameView gameView,String message){
        this.gameView=gameView;
        this.message=message;
    }
    /**
     * Updates the client model to represent that the game is no longer running.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView);
        model.setStateRequest(message);
        model.setGameEnded(true);
       
        model.setGameStarted(false);
        model.setLastMessageUpdatedGameView(true);
    }

    /**
     * Broadcasts the game-quit message to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }

    /**
     * Closes the client connection after the quit message has been processed.
     *
     * @param client client-side network control interface
     * @throws Exception if the connection cannot be closed
     */
    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        
        client.closeConnection();
    }
}
