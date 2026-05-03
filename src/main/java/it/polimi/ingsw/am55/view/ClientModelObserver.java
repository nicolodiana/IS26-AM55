package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.ClientModel.ClientModel;

public interface ClientModelObserver {

    void onModelChanged(ClientModel model);
}