package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

/**
 * Observer comune per tutte le view client.
 *
 * Non appartiene alla CLI: anche la futura GUI potrà implementare questa
 * interfaccia per reagire agli aggiornamenti del ClientModel.
 */
public interface ClientModelObserver {

    void onModelChanged(ClientModel model);

}
