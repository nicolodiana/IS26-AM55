package it.polimi.ingsw.am55.network.rmi.server.commandServer;

import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.message.MessageToClient;

public class CreateGameCommand implements ServerCommand {
    private String playerId;
    private String totemColor;
    private int numPlayers;


    public CreateGameCommand(String playerId, String totemColor, int numPlayers) {
        this.playerId = playerId;
        this.totemColor = totemColor;
        this.numPlayers = numPlayers;
    }

    @Override
    public MessageToClient execute(GameController controller) {
        return controller.createGame(playerId, totemColor, numPlayers);
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }
}
