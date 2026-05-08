package it.polimi.ingsw.am55.network.rmi.server.commandServer;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageToClient;

public class PlaceTotemCommand implements ServerCommand {
    private String playerId;
    private int index;

    public PlaceTotemCommand(String playerId, int index) {
        this.playerId = playerId;
        this.index = index;
    }

    @Override
    public MessageToClient execute(GameController controller) {
        return controller.placeTotem(playerId, index);
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }
}
