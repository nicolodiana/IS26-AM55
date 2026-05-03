package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Game.Game;

import java.io.Serializable;
import java.util.List;

public class GameView implements Serializable {

    private final String gameId;
    private final GameState state;
    private final String currentPlayer;
    private final int round;
    private final List<PlayerView> players;
    private final BoardView board;

    public GameView(Game game) {
        this.gameId = game.getIdGame();
        this.state = game.getGameState();
        this.currentPlayer = game.getCurrentPlayer() != null
                ? game.getCurrentPlayer()
                : null;
        this.round = game.getCountRound();

        this.players = game.getPlayers()
                .stream()
                .map(PlayerView::new)
                .toList();

        this.board = new BoardView(game.getSharedBoard());
    }

    public String getGameId() {
        return gameId;
    }

    public GameState getState() {
        return state;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRound() {
        return round;
    }

    public List<PlayerView> getPlayers() {
        return players;
    }

    public BoardView getBoard() {
        return board;
    }
}