package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.message.MessageToClient;

/**
 * Interface used by client messages to update the local client model.
 * <p>The interface decouples message handling from the concrete model implementation used by the client views.
 */
public interface ClientModelUpdater {

    /**
     * Handles the update workflow.
     *
     * @param message the detail message associated with the exception or response
     */
    void handleUpdate(MessageToClient message);
}
