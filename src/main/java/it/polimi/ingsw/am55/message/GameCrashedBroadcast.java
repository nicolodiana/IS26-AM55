package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;

public class GameCrashedBroadcast implements MessageToClient{
    private final String message;

    public GameCrashedBroadcast(String message) {
        this.message = message;
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
