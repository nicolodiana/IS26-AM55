package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public class PickCardMessage implements MessageToClient {
    private int cardId;
    private String message;

    public PickCardMessage(String message, int cardId) {
        this.message = message;
        this.cardId = cardId;
    }

    @Override
    public void update(ClientModel model) {
        model.addCard(this.cardId);
        model.setStateRequest(message);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
