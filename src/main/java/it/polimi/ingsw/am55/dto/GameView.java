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
     * Snapshot of board of the game
     */
    private final BoardView board;

    /**
     * List of the events from the board
     * that need to be resolved at the end of the last player turn in every round
     */
    private List<ResolveEventView> resolveEvents = new ArrayList<>();

    /**
     * Creates a game view from model data that can be sent to the client.
     *
     * @param game the game model used as the data source
     */
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

    /**
     * Finds the player view that matches the requested player identifier.
     *
     * @param playerId the identifier of the player
     * @return the player view value
     */
    public PlayerView getPlayer(String playerId) {
        for (PlayerView player : players) {
            if (playerId.equals(player.getNickname())) {
                return player;
            }
        }

        return null;
    }

    /**
     * Moves the player's totem to the selected bidding ticket.
     *
     * @param playerId identifier of the player executing the place-totem command
     * @param index index of the bidding ticket selected by the player
     */

    public void placeTotem(String playerId, int index) {
        PlayerView player = getPlayer(playerId);
        if (!resolveEvents.isEmpty()) {
            resolveEvents.clear();
        }
        if (players.size() == 5 && state == GameState.PICKCARD && board.getBiddingTrail().get(0).isTaken()) {
            PlayerView p = board.getBiddingTrail().get(0).getPlayer();

            if (p != null) {
                board.putPlayerInTurnTicket(p);
                int foodGain = 6; // bonus food ticket + turnorder effect
                p.setPointsAndFood(p.getFood() + foodGain, p.getPoints());
            }
        }
        board.getBiddingTrail().get(index).setPlayer(player);
        board.removePlayerFromTurnTicket(playerId);
    }


    /**
     * Adds the card picked from board to the player's hand and updates the local resources.
     *
     * @param playerId identifier of the player executing the pick-card command
     * @param cardId identifier of the card selected by the player
     * @param newFood player's food amount after the pick
     * @param newPp player's prestige-point amount after the pick
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