package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public interface ClientModelObserver {

    void onModelChanged(ClientModel model);
}