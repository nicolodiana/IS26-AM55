package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Technical heartbeat response sent by the server after receiving a ping.
 */
public class PongMessage extends MessageToClient{

    /**
     * Prevents heartbeat messages from updating the client model.
     *
     * @return always {@code false}
     */
    @Override
    public boolean shouldUpdateModel(){
        return false;
    }
    /**
     * Sends the pong to the target player or session selected by the server.
     *
     * @param playerId target player or session identifier
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendTo(playerId, this);
    }

    /**
     * Records that the server is still alive.
     *
     * @param client client-side network control interface
     */
    @Override
    public void executeClientNetworkAction(ClientConnectionControl client){
        client.pongFromSever();
    }
}
