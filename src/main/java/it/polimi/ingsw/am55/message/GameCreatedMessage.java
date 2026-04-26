package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.CliModel;

public class GameCreatedMessage implements MessageToClient {

    private final String creatorId;
    private final int numPlayers;
    //private final String currentPlayer;
    private final String message;

    public GameCreatedMessage(String creatorId, int numPlayers, String message) {
        this.creatorId = creatorId;
        this.numPlayers = numPlayers;
        this.message = message;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

//    public String getCurrentPlayer() {
//        return currentPlayer;
//    }

    @Override
    public void update(CliModel model) {
        model.setNumPlayers(numPlayers);
        model.setStateRequest(message);
        //model.setCurrentPlayer(currentPlayer);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}