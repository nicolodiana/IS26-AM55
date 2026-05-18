package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameView implements Serializable {

    private final String gameId;
    private final GameState state;
    private final String currentPlayer;
    private final int round;
    private final List<PlayerView> players;
    private final BoardView board;
    //questa lista si popolerà a fine di ogni round, per gli update non di fine round rimane nulla
    private List<ResolveEventView> resolveEvents = new ArrayList<>();


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

        //this.board = new BoardView(game.getSharedBoard());
        this.board = game.getSharedBoard().toView();
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

    public void setResolveEvents(List<ResolveEventView> list) {
        this.resolveEvents = list;
    }

    @Override
    public String toString() {
        return "GameView{" +
                "resolveEvents=" + resolveEvents +
                '}';
    }

    public List<ResolveEventView> getResolveEvents() {
        return resolveEvents;
    }

    public PlayerView getPlayer(String playerId) {
        for (PlayerView player : players) {
            if (playerId.equals(player.getNickname())) {
                return player;
            }
        }

        return null;
    }
    //public void addCardToPlayer(String nickname, )
}