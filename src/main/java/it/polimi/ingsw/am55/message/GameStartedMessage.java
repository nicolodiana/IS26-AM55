package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public class GameStartedMessage implements MessageToClient {
    private String message;
    private String playerId;

    public GameStartedMessage(String message, String playerId) {
        this.message = message;
        this.playerId = playerId;
    }

    @Override
    public void update(ClientModel model) {
        //model.setCurrentPlayer(playerId);
        model.setStateRequest(this.message);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {

    }
}
