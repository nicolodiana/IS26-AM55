package it.polimi.ingsw.am55.network.rmi.server.commandServer;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageToClient;

public interface ServerCommand {
    MessageToClient execute(GameController controller);
    String getPlayerId();
}
