package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.BiddingTicketView;

/**
 * Represents one offer space on the bidding trail.
 *
 * Each ticket specifies the action available to the player who places a
 * totem on it: an optional food reward and the number of cards that may be
 * taken from the upper and lower rows. A ticket also records the minimum
 * number of players required for it to be included in a game and its
 * left-to-right position on the trail.
 *
 * During the totem-placement phase, at most one {@link Player} may occupy a
 * ticket. The ticket becomes available again when that player is removed.
 */
public class BiddingTicket {

    /**
     * Food reward printed on this offer space.
     */
    private final int foodBonus;

    /**
     * Number of cards that may be taken from the lower row.
     */
    private final int chooseLowerCard;

    /**
     * Number of cards that may be taken from the upper row.
     */
    private final int chooseUpperCard;

    /**
     * Minimum number of players required for this ticket to be used.
     */
    private final int numPlayer;

    /**
     * Letter identifying the ticket's position on the trail.
     */
    private final char trailPlacement;

    /**
     * Player currently occupying the ticket, or {@code null} when it is free.
     */
    private Player player;

    /**
     * Creates an unoccupied bidding ticket.
     *
     * @param foodBonus food reward associated with the ticket
     * @param chooseLowerCard number of cards selectable from the lower row
     * @param chooseUpperCard number of cards selectable from the upper row
     * @param numPlayer minimum number of players required for this ticket
     * @param trailPlacement letter identifying the ticket's trail position
     */
    public BiddingTicket(int foodBonus, int chooseLowerCard, int chooseUpperCard, int numPlayer, char trailPlacement) {
        this.foodBonus = foodBonus;
        this.chooseLowerCard = chooseLowerCard;
        this.chooseUpperCard = chooseUpperCard;
        this.numPlayer = numPlayer;
        this.trailPlacement = trailPlacement;
        this.player = null;
    }

    /**
     * Returns the player currently occupying this ticket.
     *
     * @return the occupying player, or {@code null} if the ticket is free
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the food reward printed on this ticket.
     *
     * @return the food reward
     */
    public int getFoodBonus() {
        return foodBonus;
    }

    /**
     * Returns how many cards the occupying player may take from the lower row.
     *
     * @return the lower-row card allowance
     */
    public int getChooseLowerCard() {
        return chooseLowerCard;
    }

    /**
     * Returns how many cards the occupying player may take from the upper row.
     *
     * @return the upper-row card allowance
     */
    public int getChooseUpperCard() {
        return chooseUpperCard;
    }

    /**
     * Returns the minimum player count required for this ticket to be active.
     *
     * @return the minimum number of players
     */
    public int getNumPlayer() {
        return numPlayer;
    }

    /**
     * Returns the letter used to order this ticket on the trail.
     *
     * @return the trail-position letter
     */
    public char getTrailPlacement() {
        return trailPlacement;
    }

    /**
     * Occupies this ticket with the supplied player.
     *
     * The assignment succeeds only while the ticket is free. The method does
     * not reject a {@code null} argument; assigning {@code null} leaves the
     * ticket available.
     *
     * @param player player reference to assign to the ticket
     * @throws BiddingTicketIsTaken if another player already occupies the ticket
     */
    public void setPlayer(Player player) throws BiddingTicketIsTaken {
        if (this.player == null) {
            this.player = player;
        } else {
            throw new BiddingTicketIsTaken("The bidding ticket has already been taken");
        }
    }

    /**
     * Releases this ticket by removing its current player reference.
     */
    public void removePlayer() {
        this.player = null;
    }

    /**
     * Creates a serializable view of this ticket and its current occupant.
     *
     * @return a new view containing the ticket's current state
     */
    public BiddingTicketView toView() { return new BiddingTicketView(this); }
}
