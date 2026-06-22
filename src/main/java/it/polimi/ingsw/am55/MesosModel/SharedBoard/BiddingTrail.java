package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.Card;
import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Exceptions.PlayerNotOnTrail;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Manages the ordered offer spaces on which players place their totems.
 *
 * The active trail is built from the seven predefined tickets labelled
 * {@code A} through {@code G}. Tickets whose minimum player requirement is not
 * met are omitted, while the remaining tickets are kept in left-to-right
 * alphabetical order. During card selection, the first occupied ticket from
 * the left identifies the next player to act.
 */
public class BiddingTrail {

    /**
     * Active tickets in their left-to-right trail order.
     */
    private List<BiddingTicket> ticketList;

    /**
     * Creates a bidding trail with no active tickets.
     */
    public BiddingTrail() {
        ticketList = new ArrayList<BiddingTicket>();
    }

    /**
     * Builds the active trail for the supplied player count.
     *
     * The method recreates all predefined tickets, retains those whose
     * minimum player count is less than or equal to {@code numPlayers}, and
     * orders them by their placement letter.
     *
     * @param numPlayers number of players in the game
     */
    public void initBiddingTrail(int numPlayers) {
        ticketList = setUpBiddingTrail(createAllBiddingTicket(), numPlayers);
    }

    /**
     * Creates the complete set of offer tickets defined by the game rules.
     *
     * The returned list contains tickets {@code A-G}, including their food
     * rewards, upper-row allowances, lower-row allowances, and minimum player
     * counts.
     *
     * @return a new mutable list containing every predefined ticket
     */
    private List<BiddingTicket> createAllBiddingTicket() {
        List<BiddingTicket> allBiddingTicket = new ArrayList<>();

        allBiddingTicket.add(new BiddingTicket(3, 0, 0, 5, 'A'));
        allBiddingTicket.add(new BiddingTicket(0, 1, 0, 2, 'B'));
        allBiddingTicket.add(new BiddingTicket(0, 0, 1, 2, 'C'));
        allBiddingTicket.add(new BiddingTicket(0, 2, 0, 3, 'D'));
        allBiddingTicket.add(new BiddingTicket(0, 1, 1, 2, 'E'));
        allBiddingTicket.add(new BiddingTicket(0, 0, 2, 2, 'F'));
        allBiddingTicket.add(new BiddingTicket(0, 1, 2, 4, 'G'));
        return allBiddingTicket;
    }

    /**
     * Selects the tickets available for a game and orders them by placement.
     *
     * @param allBiddingTicket complete collection of predefined tickets
     * @param numPlayer number of players in the current game
     * @return tickets whose minimum player count is satisfied, ordered from
     *         {@code A} to {@code G}
     */
    private List<BiddingTicket> setUpBiddingTrail(List<BiddingTicket> allBiddingTicket, int numPlayer) {
        return allBiddingTicket.stream()
                .filter(b -> b.getNumPlayer() <= numPlayer)
                .sorted(Comparator.comparing(BiddingTicket::getTrailPlacement))
                .toList();
    }

    /**
     * Finds the next player who must resolve an offer action.
     *
     * The trail is scanned from left to right and the first occupied ticket
     * is returned. In the normal game flow, the player who has just completed
     * an action is removed before this method is called, so the result is the
     * next remaining player.
     *
     * @return the first player still present on the trail, or an empty optional
     *         if every ticket is free
     */
    public Optional<Player> nextPlayerSecondPhase() {
        for(BiddingTicket b : ticketList){
            Player p = b.getPlayer();
            if (p != null) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the position of a player on the active trail.
     *
     * @param player player whose occupied ticket must be located
     * @return zero-based index of the ticket occupied by {@code player}
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if no ticket contains the supplied player reference
     */
    public int getPlayerPositionOnTrail(Player player) throws PlayerNotOnTrail {
        if (player == null) {
            throw new IllegalArgumentException("Player is null");
        } else {
            for (int i = 0; i < ticketList.size(); i++) {
                if (ticketList.get(i).getPlayer() == player) {
                    return i;
                }
            }
            throw new PlayerNotOnTrail("Player " + player + " is not on the trail");
        }
    }

    /**
     * Returns the player occupying the leftmost taken ticket.
     *
     * @return the first player in second-phase order
     * @throws IllegalStateException if no active ticket is occupied
     */
    public Player getFirstPlayerSecondPhase() {
        for (int i = 0; i < ticketList.size(); i++) {
            Player p = ticketList.get(i).getPlayer();
            if (p != null) {
                return p;
            }
        }
        throw new IllegalStateException("No player found");
    }

    /**
     * Assigns a player to the ticket at the specified trail position.
     *
     * @param index zero-based index of the ticket to occupy
     * @param player player to place on the selected ticket
     * @throws BiddingTicketIsTaken if the selected ticket is already occupied
     * @throws IndexOutOfBoundsException if {@code index} is outside the active trail
     */
    public void setPlayer(int index, Player player) throws BiddingTicketIsTaken, IndexOutOfBoundsException {
        if (index < 0 || index > ticketList.size() - 1) {
            throw new IndexOutOfBoundsException("The current index doesn't exits");
        } else {
            ticketList.get(index).setPlayer(player);
        }
    }

    /**
     * Returns the upper-row allowance of the ticket occupied by a player.
     *
     * @param player player whose ticket must be inspected
     * @return number of cards the player may take from the upper row
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player does not occupy an active ticket
     */
    public int getChooseUpperCard(Player player) {
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getChooseUpperCard();
    }

    /**
     * Returns the lower-row allowance of the ticket occupied by a player.
     *
     * @param player player whose ticket must be inspected
     * @return number of cards the player may take from the lower row
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player does not occupy an active ticket
     */
    public int getChooseLowerCard(Player player) {
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getChooseLowerCard();
    }

    /**
     * Releases the ticket currently occupied by the supplied player.
     *
     * @param player player to remove from the trail
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player does not occupy an active ticket
     */
    public void removePlayer(Player player) {
        ticketList.get(getPlayerPositionOnTrail(player)).removePlayer();
    }


    /**
     * Returns the internal list of active bidding tickets.
     *
     * @return the active tickets in trail order
     */
    public List<BiddingTicket> getTicketList() {
        return ticketList;
    }
}
