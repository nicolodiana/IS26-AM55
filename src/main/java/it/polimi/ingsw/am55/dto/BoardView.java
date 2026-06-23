package it.polimi.ingsw.am55.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Serializable DTO that represents the shared board visible to clients.
 * <p>It contains the upper and lower card rows, the bidding trail, and the current turn-order track.
 */
public class BoardView implements Serializable {
    /**
     * Field carrying the upper row value for client-side rendering.
     */
    private final List<CardView> upperRow;
    /**
     * Field carrying the lower row value for client-side rendering.
     */
    private final List<CardView> lowerRow;
    /**
     * Field carrying the bidding trail value for client-side rendering.
     */
    private final List<BiddingTicketView> biddingTrail;
    /**
     * Field carrying the turn ticket value for client-side rendering.
     */
    private final List<PlayerView> turnTicket;

    /**
     * Creates a board view from model data that can be sent to the client.
     *
     * @param upperRow the upper board row
     * @param lowerRow the lower board row
     * @param turnTicket the turn ticket value
     * @param biddingTrail the bidding trail value
     */
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

    /**
     * it searches the card from the rows
     * if present it removes it from the row
     * @param cardId id of the card to search
     * @return the card that the player want to add to his deck
     */
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

    /**
     * it removes the player from the turn ticket
     */
    public void removePlayerFromTurnTicket(String playerId) {
        if (turnTicket == null || turnTicket.isEmpty()) {
            return;
        }
        for(int i=0;i<turnTicket.size();i++){
            if(turnTicket.get(i)!=null && turnTicket.get(i).getNickname().equals(playerId)){
                turnTicket.set(i,null);
                break;
            }
        }
    }

    /**
     *
     * @param player the player that needs to be moved from the bidding trail to the turn ticket is
     *               all pick has finished
     */
    public void putPlayerInTurnTicket(PlayerView player) {
        for (BiddingTicketView ticket : this.biddingTrail) {
            if (ticket.getPlayer() != null && ticket.getPlayer().getNickname().equals(player.getNickname())) {
                if (ticket.allPickDone()) {
                    for (int i = 0; i < turnTicket.size(); i++) {
                        if (turnTicket.get(i) == null) {
                            turnTicket.set(i, player);
                            ticket.setPlayer(null);
                            return;
                        }
                    }
                }
                return;
            }
        }
    }
}
