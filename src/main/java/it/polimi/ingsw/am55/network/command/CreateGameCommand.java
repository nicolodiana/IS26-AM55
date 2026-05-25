package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public class CreateGameCommand implements ServerCommand {

    private static final long serialVersionUID = 1L;

    private final String playerId;
    private final String totemColor;
    private final int numPlayers;
    private final String sessionId;

    public CreateGameCommand(String playerId, String totemColor, int numPlayers, String sessionId) {
        this.playerId = playerId;
        this.totemColor = totemColor;
        this.numPlayers = numPlayers;
        this.sessionId = sessionId;
    }

    @Override
    public boolean requiresLock() {
        return true;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.createGame(playerId, totemColor, numPlayers, sessionId);
    }
}
