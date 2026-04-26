package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.CliModel;

public class InfoMessage implements MessageToClient {
    private String message;

    public InfoMessage(String message) {
        this.message = message;
    }

    public void update(CliModel model){
        model.update(this);
        model.setStateRequest(message);
    }

    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
