package it.polimi.ingsw.am55.message;


import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Final game update containing the resolved end-game result.
 */
public class GameEndResolveMessage extends MessageToClient {

    /**
     * Final immutable game snapshot delivered to clients at the end of the game.
     */
    private final GameView gameView;
    /**
     * Computed end-game scoring result displayed by clients.
     */
    private final EndGameResultView endGameResultView;
    /**
     * Human-readable text associated with the end-game resolution.
     */
    private final String message;

    /**
     * Creates an end-game resolution message.
     *
     * @param gameView          final game snapshot
     * @param endGameResultView end-game result details
     * @param message           text displayed by clients
     */
    public GameEndResolveMessage(GameView gameView, EndGameResultView endGameResultView, String message) {
        this.gameView = gameView;
        this.endGameResultView = endGameResultView;
        this.message = message;
    }

    /**
     * Stores the final game state and end-game result in the client model.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView);
        model.setEndGameResultView(endGameResultView);
        model.setStateRequest(message);
        model.setGameStarted(false);
        model.setGameEnded(true);
        model.setLastMessageUpdatedGameView(true);
    }

    /**
     * Broadcasts the final result to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }

    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        client.stopPing();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                client.closeConnection();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    @Override
    public boolean closeGameSession(){
        return true;
    }
}