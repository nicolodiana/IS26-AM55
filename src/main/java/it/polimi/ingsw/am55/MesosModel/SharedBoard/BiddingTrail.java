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
 * The {@code BiddingTrail} class manages the sequence of bidding tickets available in the game.
 * It is responsible for setting up the trail based on the number of players, keeping track of
 * player positions on the trail, and determining turn order for the second phase of the game.
 */
public class BiddingTrail {
    private List<BiddingTicket> ticketList;

    /**
     * Default constructor.
     * Initializes an empty bidding trail.
     */
    public BiddingTrail() {
        ticketList = new ArrayList<BiddingTicket>();
    }

    /**
     * Initializes the bidding trail by generating all possible tickets, filtering them
     * based on the number of players, and sorting them according to their trail placement.
     *
     * @param numPlayers the total number of players in the game
     */
    public void initBiddingTrail(int numPlayers) {
        ticketList = setUpBiddingTrail(createAllBiddingTicket(), numPlayers);
    }

    //TestHelper
//    public List<BiddingTicket> getTicketList(){
//        return ticketList;
//    }

    /*public List<BiddingTicket> getTicketList() {
        return ticketList;
    }*/

    /**
     * Creates and returns a complete list of all possible bidding tickets in the game.
     *
     * @return a {@code List<BiddingTicket>} containing all predefined tickets
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
     * Filters and sorts the provided list of bidding tickets to set up the active trail.
     * Tickets are filtered out if their required player count is greater than the actual
     * number of players, and the remaining tickets are sorted alphabetically by their placement.
     *
     * @param allBiddingTicket the list of all available bidding tickets
     * @param numPlayer        the current number of players in the game
     * @return a filtered and sorted {@code List<BiddingTicket>} for the active trail
     */
    private List<BiddingTicket> setUpBiddingTrail(List<BiddingTicket> allBiddingTicket, int numPlayer) {
        // select only the biddinTicket needed and set them by TrailPlacement
        return allBiddingTicket.stream()
                .filter(b -> b.getNumPlayer() <= numPlayer)
                .sorted(Comparator.comparing(BiddingTicket::getTrailPlacement))
                .toList();
    }

    /*public void movePlayerToBiddingTrail(Player player, int index) {
        ticketList.get(index).setPlayer(player);
    }*/

    /**
     * Determines the next player to take a turn during the second phase by looking ahead
     * on the bidding trail from the current player's position.
     *
     *
     * @return an {@link Optional} containing the next {@link Player} on the trail,
     * or an empty Optional if no subsequent players are found
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
     * Finds the index of the specified player on the active bidding trail.
     *
     * @param player the {@link Player} to locate
     * @return the integer index of the ticket held by the player
     * @throws IllegalArgumentException if the provided player is null
     * @throws PlayerNotOnTrail if the player is not currently holding any ticket on the trail
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

    /*public void clearBiddingTrail() {
        for (BiddingTicket biddingTicket : ticketList) {
            setIsTaken(ticketList.indexOf(biddingTicket), false);
        }
    }*/

    /**
     * Finds the first player present on the bidding trail.
     * This player will be the first to act during the second phase.
     *
     * @return the first {@link Player} found on the trail
     * @throws IllegalStateException if the trail is empty or no players are on the trail
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

    /*private void setIsTaken(int index, boolean taken) {
        ticketList.get(index).setIsTaken(taken);
    }*/

    /**
     * Assigns a player to the bidding ticket at the specified index.
     *
     * @param index  the index of the ticket on the trail
     * @param player the {@link Player} taking the ticket
     * @throws BiddingTicketIsTaken  if the ticket at the specified index is already taken
     * @throws IndexOutOfBoundsException if the index is outside the bounds of the trail
     */
    public void setPlayer(int index, Player player) throws BiddingTicketIsTaken, IndexOutOfBoundsException {
        if (index < 0 || index > ticketList.size() - 1) {
            throw new IndexOutOfBoundsException("The current index doesn't exits");
        } else {
            ticketList.get(index).setPlayer(player);
        }
    }

    /*public int getFoodBonus(Player player){
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getFoodBonus();
    }*/

    /**
     * Gets the number of upper cards the specified player is allowed to choose,
     * based on their current ticket on the trail.
     *
     * @param player the {@link Player} whose ticket is being checked
     * @return the number of upper cards allowed
     * @throws PlayerNotOnTrail if the player is not on the trail
     */
    public int getChooseUpperCard(Player player) {
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getChooseUpperCard();
    }

    /**
     * Gets the number of lower cards the specified player is allowed to choose,
     * based on their current ticket on the trail.
     *
     * @param player the {@link Player} whose ticket is being checked
     * @return the number of lower cards allowed
     * @throws PlayerNotOnTrail if the player is not on the trail
     */
    public int getChooseLowerCard(Player player) {
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getChooseLowerCard();
    }

    /**
     * Removes the specified player from their current bidding ticket, freeing it up.
     *
     * @param player the {@link Player} to remove from the trail
     * @throws PlayerNotOnTrail if the player is not currently on the trail
     */
    public void removePlayer(Player player) {
        ticketList.get(getPlayerPositionOnTrail(player)).removePlayer();
    }

    public List<Integer> getTicketIds() {
        List<Integer> listOfIds = new ArrayList<>();

        for (BiddingTicket ticket : this.ticketList) {
            listOfIds.add(ticket.getId());
        }

        if (listOfIds != null) {
            return listOfIds;
        }
        return null;
    }
    public void removeAllPlayers() {
        ticketList.clear();
    }
    public List<BiddingTicket> getTicketList() {
        return ticketList;
    }
}