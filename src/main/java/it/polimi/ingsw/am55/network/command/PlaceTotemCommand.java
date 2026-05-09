package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public class PlaceTotemCommand implements ServerCommand {

    private static final long serialVersionUID = 1L;

    private final String playerId;
    private final int index;

    public PlaceTotemCommand(String playerId, int index) {
        this.playerId = playerId;
        this.index = index;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.placeTotem(playerId, index);
    }
}