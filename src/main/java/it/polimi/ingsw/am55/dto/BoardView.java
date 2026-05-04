package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;

import java.io.Serializable;
import java.util.List;

public class BoardView implements Serializable {

    private List<CardView> upperRow;
    private List<CardView> lowerRow;
    private List<BiddingTicketView> biddingTrail;
    private List<PlayerView> turnTicket;


    //private final String printableBoard;

    public BoardView(
            List<CardView> upperRow,
            List<CardView> lowerRow,
            List<PlayerView> turnTicket,
            List<BiddingTicketView> biddingTrail
    ) {
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
        this.turnTicket = turnTicket;
        this.biddingTrail = biddingTrail;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Turn Ticket:\n");

        for (PlayerView playerView : turnTicket) {
            if (playerView == null) {
                sb.append("- [vuoto]\n");
            } else {
                sb.append("- ")
                        .append(playerView.getNickname())
                        .append(" | Totem: ")
                        .append(playerView.getTotemColor())
                        .append("\n");
            }
        }

        sb.append("\n");
        sb.append("Upper Row: ").append(upperRow).append("\n");
        sb.append("Bidding Trail: ").append(biddingTrail).append("\n");
        sb.append("Lower Row: ").append(lowerRow).append("\n");

        return sb.toString();
    }
}