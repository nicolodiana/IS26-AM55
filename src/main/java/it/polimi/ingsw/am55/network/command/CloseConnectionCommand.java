package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public class CloseConnectionCommand implements ServerCommand {
    private String playerId;
    public CloseConnectionCommand(String playerId) {
        this.playerId = playerId;
    }
    @Override
    public boolean requiresLock() {
        return false;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.closeConnection(this.playerId);
    }
}
