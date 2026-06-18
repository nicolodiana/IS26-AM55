package it.polimi.ingsw.am55.message;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;

/**
 * Game update broadcast after a player places a totem.
 */
public class PlaceTotemMessage extends MessageToClient {
    /**
     * Trail index where the totem has been placed.
     */
    private final int index;
    /**
     * Player id of the client that placed the totem.
     */
    private final String playerId;
    /**
     * Player id of the player whose turn is active after the totem placement.
     */
    private final String currentPlayer;
    /**
     * Human-readable state message associated with the totem-placement result.
     */
    private final String message;
    /**
     * Game state reached after the totem placement has been processed.
     */
    public GameState state;


    /**
     * Creates a totem-placement update.
     *
     * @param playerId      player that placed the totem
     * @param index         position selected by the player
     * @param currentPlayer player that must act next
     * @param state         game state after the action
     */
    public PlaceTotemMessage(String playerId, int index, String currentPlayer, GameState state) {
        this.playerId = playerId;
        this.index = index;
        this.currentPlayer = currentPlayer;
        this.state = state;
        message = "place totem done";
    }

    /**
     * Applies the totem-placement update to the local game model.
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
        model.placeTotem(this.playerId, this.index);
    }

    /**
     * Broadcasts the totem-placement update to all game clients.
     *
     * @param playerId ignored because this message is broadcast
     * @param context  server delivery context
     */
    @Override
    public void deliver(String playerId, MessageDelivery context) {
        context.broadcast(this);
    }
}
