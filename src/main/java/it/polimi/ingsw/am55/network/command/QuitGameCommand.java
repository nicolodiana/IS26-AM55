package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.Server;
import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

public class QuitGameCommand implements ServerCommand {

    private String playerId;

    public QuitGameCommand(String playerId) {
        this.playerId = playerId;
    }
    @Override
    public boolean requiresLock() {
        return true;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.quitGame(playerId);
    }
}
