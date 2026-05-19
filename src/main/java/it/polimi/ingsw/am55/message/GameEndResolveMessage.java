package it.polimi.ingsw.am55.message;


import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;

public class GameEndResolveMessage implements MessageToClient {

    private final GameView gameView;
    private final EndGameResultView endGameResultView;
    private final String message;

    public GameEndResolveMessage(GameView gameView, EndGameResultView endGameResultView, String message) {
        this.gameView = gameView;
        this.endGameResultView = endGameResultView;
        this.message = message;
    }

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

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}