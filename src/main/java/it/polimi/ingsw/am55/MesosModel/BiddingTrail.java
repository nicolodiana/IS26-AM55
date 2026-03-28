package it.polimi.ingsw.am55.MesosModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BiddingTrail {
    private List<BiddingTicket> ticketList;

    public BiddingTrail(){
        ticketList = new ArrayList<BiddingTicket>();
    }

    public void initBiddingTrail(int numPlayers){
        ticketList = setUpBiddingTrail( createAllBiddingTicket(), numPlayers);
    }

    public List<BiddingTicket> getTicketList(){
        return ticketList;
    }

    //create all the bidding Ticket
    private List<BiddingTicket> createAllBiddingTicket() {
        List<BiddingTicket> allBiddingTicket = new ArrayList<>();

        //gonna add all the "new" for creating the new cards

        return allBiddingTicket;
    }
    private List<BiddingTicket> setUpBiddingTrail(List<BiddingTicket> allBiddingTicket, int numPlayer) {
        //select only the biddinTicket needed and set them by TrailPlacement
        for (BiddingTicket biddingTicket : allBiddingTicket) {
            if (biddingTicket.getNumPlayer() <= numPlayer){
                ticketList.add(biddingTicket);
            }
        }
        ticketList.sort(Comparator.comparing(BiddingTicket::getTrailPlacement));
        return ticketList;
    }

    public void movePlayerToBiddingTrail(Player player, int index){
        ticketList.get(index).setPlayer(player);
    }

    public Player nextPlayerSecondPhase(Player currentPlayer, int numPlayers){

        for (int i = getPlayerPositionOnTrail(currentPlayer) +1; i < ticketList.size(); i++) {
            if (ticketList.get(i).getIsTaken()) {
                return ticketList.get(i).getPlayer();
            }
        }
        return null;
    }

    public int getPlayerPositionOnTrail(Player player){
        int i=0;
        while (true){
            if (ticketList.get(i).getPlayer() == player && ticketList.get(i).getIsTaken()){
                return i;
            }
            i++;
        }
    }

    public void clearBiddingTrail(){
        for (BiddingTicket biddingTicket : ticketList) {
            biddingTicket.setIsTaken(false);
        }
    }

    public Player getFirstPlayerSecondPhase(){
        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).getIsTaken()) {
                return ticketList.get(i).getPlayer();
            }
        }
        return null;
    }

}
