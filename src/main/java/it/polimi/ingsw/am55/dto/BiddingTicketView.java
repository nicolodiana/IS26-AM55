package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.SharedBoard.BiddingTicket;

import java.io.Serializable;

public class BiddingTicketView implements Serializable {
    private final int foodBonus;
    private final int chooseLowerCard;
    private final int chooseUpperCard;
    private final int numPlayer;
    private final char trailPlacement;
    private PlayerView player;

    public BiddingTicketView(BiddingTicket ticket) {
        this.foodBonus = ticket.getFoodBonus();
        this.chooseUpperCard = ticket.getChooseUpperCard();
        this.chooseLowerCard = ticket.getChooseLowerCard();
        this.numPlayer = ticket.getNumPlayer();
        this.trailPlacement = ticket.getTrailPlacement();
        this.player = null;
    }

    @Override
    public String toString() {
        String nickname;
        if (player == null) { nickname = "none"; }
        else { nickname = player.getNickname(); }

        return ("\n---TICKET---\n" +
                "Food: " + this.foodBonus + "\n" +
                "Card to choose in the upper row: " + this.chooseUpperCard + "\n" +
                "Card to choose in the upper row: " + this.chooseUpperCard + "\n" +
                "Card to choose in the lower row: " + this.chooseLowerCard + "\n" +
                "Player on this ticket: " + nickname + "\n");
    }
}
