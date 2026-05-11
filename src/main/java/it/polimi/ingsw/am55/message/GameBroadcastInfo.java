package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public class GameBroadcastInfo implements MessageToClient {

    private final String message;

    public GameBroadcastInfo(String message) {
        this.message = message;
    }

    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}