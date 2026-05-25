package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.network.ClientConnectionControl;

public class StartPingMessage extends MessageToClient{
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendToSession(playerId, this);
    }

    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        client.startPing();
    }
    @Override
    public boolean shouldUpdateModel(){
        return false;
    }
}
