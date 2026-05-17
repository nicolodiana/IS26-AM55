package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;

public class GameBroadcastEndGame implements MessageToClient{
    private final String message;
    private final GameState gameState;

    public GameBroadcastEndGame(String message, GameState gameState) {
        this.message = message;
        this.gameState = gameState;
    }
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setStateRequest(message);
        model.setGameStarted(false);
        model.setGameEnded(true);
        model.setGameCrashed(true);
        model.setLastMessageUpdatedGameView(false);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
