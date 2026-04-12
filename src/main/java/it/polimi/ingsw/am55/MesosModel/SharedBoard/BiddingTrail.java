package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Exceptions.PlayerNotOnTrail;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BiddingTrail {
    private List<BiddingTicket> ticketList;

    public BiddingTrail() {
        ticketList = new ArrayList<BiddingTicket>();
    }

    public void initBiddingTrail(int numPlayers) {
        ticketList = setUpBiddingTrail(createAllBiddingTicket(), numPlayers);
    }

    /*public List<BiddingTicket> getTicketList() {
        return ticketList;
    }*/
    //create all the bidding Ticket
    private List<BiddingTicket> createAllBiddingTicket() {
        List<BiddingTicket> allBiddingTicket = new ArrayList<>();

        allBiddingTicket.add(new BiddingTicket(3,0,0,5,'A'));
        allBiddingTicket.add(new BiddingTicket(0,1,0,2,'B'));
        allBiddingTicket.add(new BiddingTicket(0,0,1,2,'C'));
        allBiddingTicket.add(new BiddingTicket(0,2,0,3,'D'));
        allBiddingTicket.add(new BiddingTicket(0,1,1,2,'E'));
        allBiddingTicket.add(new BiddingTicket(0,0,2,2,'F'));
        allBiddingTicket.add(new BiddingTicket(0,1,2,4,'G'));
        return allBiddingTicket;
    }
    private List<BiddingTicket> setUpBiddingTrail(List<BiddingTicket> allBiddingTicket, int numPlayer) {
        //select only the biddinTicket needed and set them by TrailPlacement
        return allBiddingTicket.stream()
                .filter(b->b.getNumPlayer()<=numPlayer)
                .sorted(Comparator.comparing(BiddingTicket::getTrailPlacement))
                .toList();

        /*for (BiddingTicket biddingTicket : allBiddingTicket) {
            if (biddingTicket.getNumPlayer() <= numPlayer) {
                ticketList.add(biddingTicket);
            }
        }
        ticketList.sort(Comparator.comparing(BiddingTicket::getTrailPlacement));
        return ticketList;*/
    }

    /*public void movePlayerToBiddingTrail(Player player, int index) {
        ticketList.get(index).setPlayer(player);
    }*/

    public Optional<Player> nextPlayerSecondPhase(Player currentPlayer) {

        for (int i = getPlayerPositionOnTrail(currentPlayer) + 1; i < ticketList.size(); i++) {
            if (ticketList.get(i).getIsTaken()) {
                return Optional.of(ticketList.get(i).getPlayer());
            }
        }
        return Optional.empty();
    }

    public int getPlayerPositionOnTrail(Player player) throws PlayerNotOnTrail {
        int i = 0;
        while (i < ticketList.size()) {
            if (ticketList.get(i).getPlayer() == player && ticketList.get(i).getIsTaken()) {
                return i;
            }
            i++;
        }
        throw new PlayerNotOnTrail("Player " + player + " is not on the trail");
    }

    /*public void clearBiddingTrail() {
        for (BiddingTicket biddingTicket : ticketList) {
            setIsTaken(ticketList.indexOf(biddingTicket), false);
        }
    }*/

    public Player getFirstPlayerSecondPhase() {
        for (int i = 0; i < ticketList.size(); i++) {
            if (getIsTaken(i)) {
                return ticketList.get(i).getPlayer();
            }
        }
        throw new IllegalStateException("No player found");
    }

    public boolean getIsTaken(int index) {
        return ticketList.get(index).getIsTaken();
    }

    /*private void setIsTaken(int index, boolean taken) {
        ticketList.get(index).setIsTaken(taken);
    }*/

    public void setPlayer(int index, Player player) throws BiddingTicketIsTaken,IndexOutOfBoundsException {
        if(index<0 || index>ticketList.size()-1){
            throw new  IndexOutOfBoundsException("The current index doesn't exits");
        }else{
            ticketList.get(index).setPlayer(player);
        }
    }

    /*public int getFoodBonus(Player player){
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getFoodBonus();
    }*/

    public int getChooseUpperCard(Player player){
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getChooseUpperCard();
    }

    public int getChooseLowerCard(Player player){
        int index = getPlayerPositionOnTrail(player);
        return ticketList.get(index).getChooseLowerCard();
    }

    public void removePlayer(Player player){
        ticketList.get(getPlayerPositionOnTrail(player)).removePlayer();
    }
}
