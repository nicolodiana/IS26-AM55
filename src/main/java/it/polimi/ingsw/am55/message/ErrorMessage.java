package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public class ErrorMessage extends MessageToClient {

    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public void update(ClientModel model) {
        model.setLastError(message);
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }

    @Override
    public boolean isConnectionSetupSuccessful() {
        return false;
    }
}
