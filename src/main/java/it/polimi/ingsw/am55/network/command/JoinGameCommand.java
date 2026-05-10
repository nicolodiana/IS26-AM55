package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public class JoinGameCommand implements ServerCommand {

    private static final long serialVersionUID = 1L;

    private final String playerId;
    private final String totemColor;

    public JoinGameCommand(String playerId, String totemColor) {
        this.playerId = playerId;
        this.totemColor = totemColor;
    }

    @Override
    public boolean requiresLock() {
        return true;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        if (sender != null) {
            serverApplication.registerClient(playerId, sender);
        }

        serverApplication.joinGame(playerId, totemColor);
    }
}