package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

public class PongMessage extends MessageToClient{

    @Override
    public boolean shouldUpdateModel(){
        return false;
    }
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }

    @Override
    public void executeClientNetworkAction(ClientConnectionControl client){
        client.pongFromSever();
    }
}
