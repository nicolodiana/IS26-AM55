package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.*;

/**
 * The {@code TurnTicket} class manages the turn order of the players in the game.
 * It is responsible for initializing the turn sequence, determining the next player,
 * and applying specific game effects (such as bonuses or maluses) based on a player's
 * position in the turn order.
 */
public class TurnTicket {

    /**
     * The list representing the current turn order of the players.
     */
    private List<Player> turnOrder;

    /**
     * The effect applied to players based on the total number of players in the game.
     * Polymorphism is used to apply the correct effect via dynamic binding.
     */
    TurnOrderEffect effect;

    /**
     * Random instance utilized for shuffling operations or random events.
     */
    private Random random;

    /**
     * Default constructor.
     * Initializes an empty turn order list.
     */
    public TurnTicket() {
        turnOrder = new ArrayList<Player>();
    }

    /**
     * Constructor that accepts a specific {@link Random} instance.
     * Initializes an empty turn order list and sets the custom random generator.
     *
     * @param random the {@link Random} instance to be used by this class
     */
    public TurnTicket(Random random) {
        this.turnOrder = new ArrayList<Player>();
        this.random = random;
    }

    /**
     * Initializes the turn ticket with a list of players.
     * The method shuffles the provided players to create a random initial turn order
     * and assigns the appropriate {@link TurnOrderEffect} based on the number of players.
     *
     * @param players the list of players to be added to the turn sequence
     * @throws IllegalArgumentException if the number of players is not between 2 and 5
     */
    public void initTurnTicket(List<Player> players) {
        turnOrder = new ArrayList<>(players);
        Collections.shuffle(turnOrder);
        this.effect = switch (players.size()) {
            case 2 -> new TwoPlayersEffect();
            case 3 -> new ThreePlayersEffect();
            case 4 -> new FourPlayersEffect();
            case 5 -> new FivePlayersEffect();
            default -> throw new IllegalArgumentException("Numero giocatori non valido");
        };
    }

    /**
     * Gets the complete list representing the current turn order.
     *
     * @return a {@code List<Player>} indicating the turn sequence
     */
    public List<Player> getTurnOrder() {
        return turnOrder;
    }

    /**
     * Gets the player at the specified index in the turn order.
     *
     * @param index the position of the player to retrieve
     * @return the {@link Player} at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Player getTurnPlayer(int index) {
        return turnOrder.get(index);
    }

    /**
     * Retrieves the player who plays immediately after the specified player during the first phase.
     *
     * @param player the current {@link Player}
     * @return an {@link Optional} containing the next {@link Player} if one exists,
     * or an empty Optional if the specified player is the last in the turn order
     * @throws IllegalArgumentException if the provided player is null
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
     * Applies a bonus or a malus to the specified player based on their position in the turn order.
     * The last player in the order receives a malus, while the others receive a food bonus
     * depending on their specific position.
     *
     * @param player the {@link Player} receiving the malus or bonus
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

    public Player getFirstPlayerFirstPhase(){
        return turnOrder.get(0);
    }

    /**
     * Adds a player to the turn order.
     * The player is inserted into the first available {@code null} position found in the list.
     *
     * @param player the {@link Player} to add
     */
    public void addPlayer(Player player) {
        for (int i = 0; i < turnOrder.size(); i++) {
            if (turnOrder.get(i) == null) {
                turnOrder.set(i, player);
                return;
            }
        }
    }

    /**
     * Removes the first valid player found in the turn order.
     * The first non-{@code null} player in the sequence is replaced with {@code null}.
     */
    public void removePlayerFromTurnTicket() {
        for (int i = 0; i < turnOrder.size(); i++) {
            if (turnOrder.get(i) != null) {
                turnOrder.set(i, null);
                return;
            }
        }
    }
}