package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.dto.GameView;

public class UpdateViewMessage implements MessageToClient {

    private final GameView gameView;
    private final String message;

    public UpdateViewMessage(GameView gameView) {
        this.gameView = gameView;
        this.message = "La partita è iniziata!";
    }

    public UpdateViewMessage(GameView gameView, String message) {
        this.gameView = gameView;
        this.message = message;
    }

    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setGameView(gameView);
        model.setStateRequest(message);
        model.setGameStarted(true);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}