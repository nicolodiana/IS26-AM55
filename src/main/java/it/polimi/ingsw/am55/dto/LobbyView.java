package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LobbyView implements Serializable {

    private final GameState gameState;
    private final List<String> chosenTotems;
    private final List<String> playerIds;

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

    public List<String> getChosenTotems() {
        return new ArrayList<>(chosenTotems);
    }

    public List<String> getPlayerIds() {
        return new ArrayList<>(playerIds);
    }

    public boolean hasGame() {
        return gameState != null;
    }

    public boolean isGameCreated() {
        return gameState == GameState.CREATED;
    }

    public boolean isTotemAlreadyChosen(String totemColor) {
        if (totemColor == null) {
            return false;
        }

        return chosenTotems.contains(totemColor.trim().toUpperCase());
    }
}