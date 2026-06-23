package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of the lobby state sent to waiting clients.
 * <p>It exposes the current game state, already chosen totems, and player nicknames.
 */
public class LobbyView implements Serializable {

    /**
     * field carrying the game state value for client-side rendering.
     */
    private final GameState gameState;
    /**
     * field carrying the chosen totems value for client-side rendering.
     */
    private final List<String> chosenTotems;
    /**
     * field carrying the player ids value for client-side rendering.
     */
    private final List<String> playerIds;

    /**
     * Creates a lobby view from model data that can be sent to the client.
     *
     * @param gameState the game state value
     * @param players the players participating in the match
     */
    public LobbyView(GameState gameState, List<Player> players) {
        this.gameState = gameState;
        this.chosenTotems = new ArrayList<>();
        this.playerIds = new ArrayList<>();

        if (players == null) {
            return;
        }

        for (Player player : players) {
            if (player == null) {
                continue;
            }

            if (player.getTotem() != null) {
                chosenTotems.add(player.getTotem().toUpperCase());
            }

            if (player.getNickname() != null) {
                playerIds.add(player.getNickname());
            }
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * Returns the totem colors already selected by players in the lobby.
     *
     * @return the chosen totems value
     */
    public List<String> getChosenTotems() {
        return new ArrayList<>(chosenTotems);
    }

    public List<String> getPlayerIds() {
        return new ArrayList<>(playerIds);
    }

    /**
     * Checks whether this lobby view satisfies the has game condition.
     *
     * @return true if the condition is satisfied; false otherwise
     */
    public boolean hasGame() {
        return gameState != null;
    }

    /**
     * Checks whether the game has been created or not.
     *
     * @return true if the condition is satisfied; false otherwise
     */
    public boolean isGameCreated() {
        return gameState == GameState.CREATED;
    }

    /**
     * Checks whether a totem color has already chosen or not.
     *
     * @param totemColor the selected totem color
     * @return true if the condition is satisfied; false otherwise
     */
    public boolean isTotemAlreadyChosen(String totemColor) {
        if (totemColor == null) {
            return false;
        }

        return chosenTotems.contains(totemColor.trim().toUpperCase());
    }
}