package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.*;

/**
 * Models the turn-order track used during the totem-placement phase.
 *
 * <p>At game setup, the players are copied and shuffled to determine the first
 * round's order. Once all totems have been placed, the occupied slots are
 * cleared. Players are then inserted into the first free slot as they finish
 * resolving their offer actions, thereby creating the order for the following
 * round. The selected {@link TurnOrderEffect} applies the food bonuses and the
 * final-position penalty printed on the track for the current player count.</p>
 */
public class TurnTicket {

    /**
     * Number of players used to select the turn-order effect implementation.
     */
    private int numplayerticket;

    /**
     * Player references stored in current turn order; slots may contain {@code null}.
     */
    private List<Player> turnOrder;

    /**
     * Strategy that applies position-dependent food bonuses and the last-slot penalty.
     */
    TurnOrderEffect effect;

    /**
     * Random generator supplied through the test-oriented constructor.
     *
     */
    private Random random;

    /**
     * Creates an empty turn-order track.
     */
    public TurnTicket() {
        turnOrder = new ArrayList<Player>();
    }

    /**
     * Creates an empty turn-order track and stores a random generator.
     *
     * @param random random generator to store in this object
     */
    public TurnTicket(Random random) {
        this.turnOrder = new ArrayList<Player>();
        this.random = random;
    }

    /**
     * Initializes the track and randomizes the first-round player order.
     *
     * <p>A defensive copy of {@code players} is created and shuffled. The
     * method then chooses the player-count-specific {@link TurnOrderEffect}
     * used when players return to the track after resolving their offers.</p>
     *
     * @param players players participating in the game
     * @throws IllegalArgumentException if the list size is not between two and five
     * @throws NullPointerException if {@code players} is {@code null}
     */
    public void initTurnTicket(List<Player> players) {
        turnOrder = new ArrayList<>(players);
        this.numplayerticket=players.size();
        Collections.shuffle(turnOrder);
        this.effect = switch (numplayerticket) {
            case 2 -> new TwoPlayersEffect();
            case 3 -> new ThreePlayersEffect();
            case 4 -> new FourPlayersEffect();
            case 5 -> new FivePlayersEffect();
            default -> throw new IllegalArgumentException("Numero giocatori non valido");
        };
    }

    /**
     * Returns the live list representing the turn-order slots.
     *
     * @return the internal turn-order list
     */
    public List<Player> getTurnOrder() {
        return turnOrder;
    }

    /**
     * Returns the player reference stored at a turn-order position.
     *
     * @param index zero-based position in the turn-order list
     * @return the player at the requested position, possibly {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public Player getTurnPlayer(int index) {
        return turnOrder.get(index);
    }

    /**
     * Returns the player immediately following a player in first-phase order.
     *
     * <p>The supplied player is expected to be present in {@link #turnOrder}.
     *
     * @param player current player in the totem-placement order
     * @return the following player, or an empty optional if {@code player} is in
     *         the final slot
     * @throws IllegalArgumentException if {@code player} is {@code null}
     */
    public Optional<Player> getNextPlayerFirstPhase(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player isn't valid");
        }
        int index = this.turnOrder.indexOf(player);
        if (index < turnOrder.size() - 1) {
            return Optional.of(turnOrder.get(index + 1));
        }
        else{
            return Optional.empty(); //return an empty optional if there aren't no other players
        }
    }

    /**
     * Applies the effect associated with a player's new turn-order position.
     *
     * <p>The player in the last slot pays one food, or loses two prestige points
     * when no food is available. Every other position is delegated to the
     * player-count-specific {@link TurnOrderEffect}, which grants the applicable
     * food bonus. The track must have been initialized and the player is expected
     * to be present in it.</p>
     *
     * @param player player whose position effect must be resolved
     */
    public void giveMalusOrBonus(Player player) {
        int playerPosition = this.turnOrder.indexOf(player);
        int lastIndex = turnOrder.size() - 1;
        if (playerPosition == lastIndex) {
            effect.applyMalus(player);
        } else {
            effect.applyFood(player, playerPosition);
        }
    }

    /**
     * Returns the player stored in the first turn-order slot.
     *
     * @return the first player, possibly {@code null} if the track has been cleared
     * @throws IndexOutOfBoundsException if the turn-order list is empty
     */
    public Player getFirstPlayerFirstPhase(){
        return turnOrder.get(0);
    }

    /**
     * Inserts a player into the first available turn-order slot.
     *
     * <p>An available slot is represented by {@code null}. If no such slot
     * exists, the method leaves the list unchanged.</p>
     *
     * @param player player to insert; a {@code null} value leaves the selected
     *        slot empty
     */
    public void addPlayer(Player player) {
        turnOrder.add(player);
//        for (int i = 0; i < turnOrder.size(); i++) {
//
//            if (turnOrder.get(i) == null) {
//                turnOrder.set(i, player);
//                return;
//            }
//        }
    }

    /**
     * Removes all players from the turn order ticket
     */
    public void removePlayerFromTurnTicket() {
        turnOrder.clear();
    }
}