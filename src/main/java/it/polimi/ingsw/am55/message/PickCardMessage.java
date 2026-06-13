package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;

public class PickCardMessage extends  MessageToClient {
    private final int cardId;
    private final String playerId;
    private final String currentPlayer;
    private final String message;
    public GameState state;


    public PickCardMessage(String playerId, int cardId, String currentPlayer, GameState state) {
        this.playerId = playerId;
        this.cardId = cardId;
        this.currentPlayer = currentPlayer;
        this.state = state;
        message = "pick done";
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
        model.pickCard(this.playerId, this.cardId);
    }

    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
