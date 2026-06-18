package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;

/**
 * Game update broadcast after a player picks a card.
 */
public class PickCardMessage extends  MessageToClient {
    /**
     * Identifier of the card that has been picked.
     */
    private final int cardId;
    /**
     * Player id of the client that picked the card.
     */
    private final String playerId;
    /**
     * Updated food amount for the player after applying the pick result.
     */
    private int newFood;
    /**
     * Updated prestige-point amount for the player after applying the pick result.
     */
    private int newPp;
    /**
     * Player id of the player whose turn is active after the card pick.
     */
    private final String currentPlayer;
    /**
     * Human-readable state message associated with the pick result.
     */
    private final String message;
    /**
     * Game state reached after the card pick has been processed.
     */
    public GameState state;


    /**
     * Creates a card-pick update.
     *
     * @param playerId      player that picked the card
     * @param cardId        identifier of the picked card
     * @param newFood       updated food value for the player
     * @param newPp         updated prestige-point value for the player
     * @param currentPlayer player that must act next
     * @param state         game state after the action
     */
    public PickCardMessage(String playerId, int cardId, int newFood, int newPp, String currentPlayer, GameState state) {
        this.playerId = playerId;
        this.cardId = cardId;
        this.newFood = newFood;
        this.newPp = newPp;
        this.currentPlayer = currentPlayer;
        this.state = state;
        message = "pick done";
    }

    /**
     * Applies the card-pick update to the local game model.
     *
     * @param model client-side model to update
     */
    @Override
    public void update(ClientModel model) {
        model.clearError();
        model.setStateRequest(message);
        model.setGameStarted(true);
        
        model.setLastMessageUpdatedGameView(true);
        model.setCurrentPlayer(this.currentPlayer);
        model.setCurrentGameState(this.state);
        model.pickCard(this.playerId, this.cardId, this.newFood, this.newPp);
    }

    /**
     * Broadcasts the card-pick update to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
