package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.CliModel;

public class ErrorMessage implements MessageToClient {

    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void update(CliModel model) {
        model.setLastError(message);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }
}