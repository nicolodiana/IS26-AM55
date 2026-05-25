package it.polimi.ingsw.am55.network.command;


import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

public class QuitLobbyCommand implements ServerCommand {
    private String sessionId;

    public QuitLobbyCommand(String sessionId){
        this.sessionId=sessionId;
    }
    @Override
    public boolean requiresLock() {
        return false;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.quitLobby(this.sessionId);
    }

}
