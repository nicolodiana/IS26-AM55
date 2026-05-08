package it.polimi.ingsw.am55.network.rmi.server.commandServer;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageToClient;

public class JoinGameCommand implements ServerCommand {
    private String playerId;
    private String totemColor;


    public JoinGameCommand(String playerId, String totemColor) {
        this.playerId = playerId;
        this.totemColor = totemColor;
    }

    @Override
    public MessageToClient execute(GameController controller) {
        return controller.joinGame(playerId, totemColor);
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }
}
