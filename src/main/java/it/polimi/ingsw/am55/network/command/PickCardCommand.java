package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

public class PickCardCommand implements ServerCommand {

    private static final long serialVersionUID = 1L;

    private final String playerId;
    private final int cardId;

    public PickCardCommand(String playerId, int cardId) {
        this.playerId = playerId;
        this.cardId = cardId;
    }
    @Override
    public boolean requiresLock() {
        return true;
    }

    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.pickCard(playerId, cardId);
    }
}