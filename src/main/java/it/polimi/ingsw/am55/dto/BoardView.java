package it.polimi.ingsw.am55.dto;

import java.io.Serializable;
import java.util.List;

public class BoardView implements Serializable {

    private final List<CardView> upperRow;
    private final List<CardView> lowerRow;
    private final List<BiddingTicketView> biddingTrail;
    private final List<PlayerView> turnTicket;

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

    public List<CardView> getUpperRow() {
        return upperRow;
    }

    public List<CardView> getLowerRow() {
        return lowerRow;
    }

    public List<BiddingTicketView> getBiddingTrail() {
        return biddingTrail;
    }

    public List<PlayerView> getTurnTicket() {
        return turnTicket;
    }

    @Override
    public String toString() {
        return "BoardView{" +
                "upperRow=" + upperRow +
                ", lowerRow=" + lowerRow +
                ", biddingTrail=" + biddingTrail +
                ", turnTicket=" + turnTicket +
                '}';
    }

    public void setPlayer(int index, PlayerView player) {
        biddingTrail.get(index).setPlayer(player);
    }

    public CardView searchCard(int cardId) {

        for (int i = 0; i < this.upperRow.size(); i++) {
            if (upperRow.get(i).getId() == cardId) {
                return this.upperRow.remove(i);
            }
        }

        for (int i = 0; i < this.lowerRow.size(); i++) {
            if (lowerRow.get(i).getId() == cardId) {
                return this.lowerRow.remove(i);
            }
        }

        return null;
    }

}