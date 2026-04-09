package it.polimi.ingsw.am55.MesosModel.SharedBoard;


import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class BiddingTicket {
    private final int foodBonus;
    private final int chooseLowerCard;
    private final int chooseUpperCard;
    private final int numPlayer; //for the biddingTrial will select for: biddingTicket.numPlayer <= numberOfPlayer
    private final char trailPlacement;
    private boolean isTaken;
    private Player player;

    public BiddingTicket(int foodBonus, int chooseLowerCard, int chooseUpperCard, int numPlayer, char trailPlacement){
        this.foodBonus = foodBonus;
        this.chooseLowerCard = chooseLowerCard;
        this.chooseUpperCard = chooseUpperCard;
        this.numPlayer = numPlayer;
        this.trailPlacement = trailPlacement;
        this.isTaken = false;
        player = null;
    }

    //getter
    public Player getPlayer(){
        return player;
    }
    public int getFoodBonus() {
        return foodBonus;
    }
    public int getChooseLowerCard() {
        return chooseLowerCard;
    }
    public int getChooseUpperCard() {
        return chooseUpperCard;
    }
    public int getNumPlayer() {
        return numPlayer;
    }
    public char getTrailPlacement() {
        return trailPlacement;
    }
    public boolean getIsTaken() {
        return isTaken;
    }

    /*public void setIsTaken(boolean isTaken) {
        this.isTaken = isTaken;
    }*/
    public void setPlayer(Player player){
        if(this.player == null){
            this.player = player;
            isTaken=true;
        }else{
            throw new BiddingTicketIsTaken("The bidding ticket has already been taken");
        }
    }
    public void removePlayer(){
        player = null;
        isTaken=false;
    }


}
