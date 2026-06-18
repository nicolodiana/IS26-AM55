package it.polimi.ingsw.am55.ClientModel;

import it.polimi.ingsw.am55.message.MessageToClient;

public interface ClientModelUpdater {

    void handleUpdate(MessageToClient message);
}
