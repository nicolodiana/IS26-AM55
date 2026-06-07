package it.polimi.ingsw.am55.network.command;


import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

public class PingCommand implements ServerCommand{

    private static final long serialVersionUID = 1L;

    private final String sessionId;
    private final String playerId;

    public PingCommand(String sessionId, String playerId) {
        this.sessionId = sessionId;
        this.playerId = playerId;
    }

    @Override
    public boolean requiresLock() {
        return false;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.ping(sessionId, playerId, sender);
    }
}
