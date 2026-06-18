package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.network.ClientConnectionControl;

import java.io.Serializable;

/**
 * Base class for every server-to-client message sent through the network.
 * <p>
 * A message is responsible for two independent decisions: how it updates the
 * client model and how it must be delivered by the server. Technical messages can
 * also execute network-side effects through {@link ClientConnectionControl}.
 */
public abstract class MessageToClient implements Serializable {



    /**
     * Applies the message content to the client-side model.
     * <p>
     * The default implementation does nothing, which is useful for technical
     * messages that only affect the network layer.
     *
     * @param model client-side model to update
     */
    public void update(ClientModel model){}

    /**
     * Delivers this message using the selected server-side delivery strategy.
     *
     * @param playerId player or session identifier used by unicast deliveries
     * @param context  delivery context implemented by the server application
     */
    public abstract void deliver(String playerId, MessageDelivery context);

    
    



    /**
     * Executes optional client-side technical network behavior.
     * <p>
     * Examples include starting the heartbeat, recording a pong, or closing the connection.
     * The default implementation performs no operation.
     *
     * @param client client-side network control interface
     * @throws Exception if the technical action fails
     */
    public void executeClientNetworkAction(ClientConnectionControl client) throws Exception {}

    




    /**
     * Indicates whether this message confirms a successful create/join setup.
     * <p>
     * The server uses the value to decide whether a lobby client can be moved into
     * the game-client registry. Error messages override this method to return {@code false}.
     *
     * @return {@code true} when connection setup succeeded; {@code false} otherwise
     */
    public boolean isConnectionSetupSuccessful() {
        return true;
    }
    /**
     * Indicates whether the client model should be updated before the network action is executed.
     *
     * @return {@code true} when {@link #update(ClientModel)} should be called
     */
    public boolean shouldUpdateModel() {return  true;}
}
