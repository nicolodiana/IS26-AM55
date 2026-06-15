package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;

public class PlaceTotemMessage extends MessageToClient {
    private final int index;
    private final String playerId;
    private final String currentPlayer;
    private final String message;
    public GameState state;


    public PlaceTotemMessage(String playerId, int index, String currentPlayer, GameState state) {
        this.playerId = playerId;
        this.index = index;
        this.currentPlayer = currentPlayer;
        this.state = state;
        message = "place totem done";
    }

    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setStateRequest(message);
        model.setGameStarted(true);
        //model.setInLobby(false); //per far passare la view non piu da lobby mode ma a Game mode
        model.setLastMessageUpdatedGameView(true);
        model.setCurrentPlayer(this.currentPlayer);
        model.setCurrentGameState(this.state);
        model.placeTotem(this.playerId, this.index);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
