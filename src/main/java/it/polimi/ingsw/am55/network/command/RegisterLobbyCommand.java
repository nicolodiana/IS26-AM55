package it.polimi.ingsw.am55.network.command;


import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public class RegisterLobbyCommand implements ServerCommand {

    private static final long serialVersionUID = 1L;

    private final String sessionId;

    public RegisterLobbyCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean requiresLock() {
        return true;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.registerLobbyClient(sessionId, sender);
    }
}