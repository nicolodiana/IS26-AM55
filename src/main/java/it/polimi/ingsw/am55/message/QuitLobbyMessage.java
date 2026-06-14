package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

public class QuitLobbyMessage extends MessageToClient{
    private String message;

    public QuitLobbyMessage(){}
    public QuitLobbyMessage(String message){
        this.message = message;
    }


    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setInLobby(false);
        model.setGameStarted(false);
        model.setStateRequest(message);
        model.setLastMessageUpdatedGameView(false);
    }
    @Override
    public void deliver(String sessionId, MessageDelivery context) {
        context.sendToSession(sessionId, this);
    }

    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        //client.stopPing();
        client.closeConnection();
    }

}
