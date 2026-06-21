package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

/**
 * Common observer implemented by every client view.
 * <p>
 * The client model calls this method whenever a server message changes the local
 * state that must be rendered by either the CLI or the GUI.
 */
public interface ClientModelObserver {

    /**
     * Receives a model update and refreshes the concrete view.
     *
     * @param model updated client model
     */
    void onModelChanged(ClientModel model);
}
