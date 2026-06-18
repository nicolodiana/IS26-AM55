package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.BiddingTicketView;

/**
 * The {@code BiddingTicket} class represents a ticket available during the bidding phase of the game.
 * It contains specific bonuses, rules for card selection, and placement information on the bidding trail.
 * It also keeps track of which player (if any) has currently claimed the ticket.
 */
public class BiddingTicket {


    /**
     * The amount of bonus food provided to the player who takes this ticket.
     */
    private final int foodBonus;

    /**
     * The number of cards the player is allowed to choose from the lower section.
     */
    private final int chooseLowerCard;

    /**
     * The number of cards the player is allowed to choose from the upper section.
     */
    private final int chooseUpperCard;

    /**
     * The minimum or maximum threshold of players required for this ticket to be available.
     * The bidding trial will select this ticket only if: {@code biddingTicket.numPlayer <= numberOfPlayer}.
     */
    private final int numPlayer;

    /**
     * The character representing the specific placement or position of this ticket on the trail.
     */
    private final char trailPlacement;

    /**
     * The player who has currently taken or claimed this bidding ticket.
     * It is {@code null} if the ticket is still available.
     */
    private Player player;

    /**
     * Constructs a new {@code BiddingTicket} with the specified attributes.
     * The ticket is initially unassigned (player is set to {@code null}).
     *
     * @param foodBonus       the amount of bonus food provided by this ticket
     * @param chooseLowerCard the number of lower cards the player can choose
     * @param chooseUpperCard the number of upper cards the player can choose
     * @param numPlayer       the player count parameter determining if this ticket is used in the game
     * @param trailPlacement  the character indicating the ticket's placement on the trail
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
     * Gets the player who currently holds this bidding ticket.
     *
     * @return the {@link Player} holding the ticket, or {@code null} if it is available
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the food bonus associated with this ticket.
     *
     * @return the amount of bonus food
     */
    public int getFoodBonus() {
        return foodBonus;
    }

    /**
     * Gets the number of lower cards the holding player can choose.
     *
     * @return the number of lower cards
     */
    public int getChooseLowerCard() {
        return chooseLowerCard;
    }

    /**
     * Gets the number of upper cards the holding player can choose.
     *
     * @return the number of upper cards
     */
    public int getChooseUpperCard() {
        return chooseUpperCard;
    }

    /**
     * Gets the player count condition for this ticket.
     *
     * @return the number of players parameter
     */
    public int getNumPlayer() {
        return numPlayer;
    }

    /**
     * Gets the placement character of this ticket on the trail.
     *
     * @return the trail placement character
     */
    public char getTrailPlacement() {
        return trailPlacement;
    }

    /**
     * Assigns this bidding ticket to a specified player.
     *
     * @param player the {@link Player} who is taking the ticket
     * @throws BiddingTicketIsTaken if the ticket has already been claimed by a player
     */
    public void setPlayer(Player player) {
        if (this.player == null) {
            this.player = player;
        } else {
            throw new BiddingTicketIsTaken("The bidding ticket has already been taken");
        }
    }

    /**
     * Removes the current player from this ticket, making it available again.
     */
    public void removePlayer() {
        this.player = null;
    }

    /*public int getId() {
        return this.id;
    }*/

    public BiddingTicketView toView() { return new BiddingTicketView(this); }
}