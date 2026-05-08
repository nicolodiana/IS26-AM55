package it.polimi.ingsw.am55.network.rmi.server.commandServer;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageToClient;

public class PickCardCommand implements ServerCommand {
    private String playerId;
    private int cardId;

    public PickCardCommand(String playerId, int cardId) {
        this.playerId = playerId;
        this.cardId = cardId;
    }

    @Override
    public MessageToClient execute(GameController controller) {
       return controller.pickCard(playerId, cardId);
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }
}
