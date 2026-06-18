package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.network.ClientConnectionControl;

/**
 * Technical message that authorizes the client to start heartbeat pings.
 */
public class StartPingMessage extends MessageToClient{
    /**
     * Sends the message to the target lobby session.
     *
     * @param playerId lobby session identifier used by the current delivery path
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.sendToSession(playerId, this);
    }

    /**
     * Starts the client heartbeat mechanism.
     *
     * @param client client-side network control interface
     * @throws Exception if heartbeat startup fails
     */
    @Override
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {
        client.startPing();
    }
    /**
     * Prevents this technical message from updating the client model.
     *
     * @return always {@code false}
     */
    @Override
    public boolean shouldUpdateModel(){
        return false;
    }
}
