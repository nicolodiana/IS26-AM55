package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public class SoloQuitMessage extends MessageToClient {
    
    @Override
    public void update(ClientModel model){}
    
    @Override
    public void deliver(String playerId, MessageDelivery context){
        context.sendTo(playerId, this);
    }
}
