package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameView implements Serializable {
    /**
     * game unique recognizer
     */
    private final String gameId;

    /**
     * current game state
     */
    private GameState state;

    /**
     * current player who need to execute a command
     */
    private String currentPlayer;

    /**
     * current number of round the game is in
     */
    private final int round;

    /**
     * players who partecipate in the game
     */
    private final List<PlayerView> players;

    /**
     * board of the game
     */
    private final BoardView board;

    /**
     * list of the events from the board
     * that need to be resolved at the end of the last player's turn in every round
     */
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

    /**
     * it takes the player and move his totem to the ticket choose by the player using its index
     * @param playerId player who execute placetotem command
     * @param index index of the ticket he wants to move into
     */

    public void placeTotem(String playerId, int index) {
        PlayerView player = getPlayer(playerId);
        if (!resolveEvents.isEmpty()) {
            resolveEvents.clear();
        }

        board.getBiddingTrail().get(index).setPlayer(player);
        board.removePlayerFromTurnTicket();
    }


    /**
     * this method add the card that the player want from the board to his hands
     * @param playerId id player who does the command pickcard
     * @param cardId card id that the player want to pick
     * @param newFood player's food after the pick
     * @param newPp players'pp after the pick
     */
    public void pickCard(String playerId, int cardId, int newFood, int newPp) {
        CardView card = this.board.searchCard(cardId);
        PlayerView player = getPlayer(playerId);

        player.pickCard(card);
        player.setPointsAndFood(newFood, newPp);

        board.putPlayerInTurnTicket(getPlayer(playerId));
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setState(GameState state) {
        this.state = state;
    }

}