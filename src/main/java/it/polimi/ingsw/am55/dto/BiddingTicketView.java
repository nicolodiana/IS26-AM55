package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.BiddingTicket;

import java.io.Serializable;

public class BiddingTicketView implements Serializable {
    /**
     * Food bonus awarded by this bidding ticket or effect.
     */
    private final int foodBonus;
    /**
     * Number of cards that may be chosen from the lower row.
     */
    private final int chooseLowerCard;
    /**
     * Number of cards that may be chosen from the upper row.
     */
    private final int chooseUpperCard;
    /**
     * Minimum number of players required for this card or component to be used.
     */
    private final int numPlayer;
    /**
     * Letter identifying this ticket position on the bidding trail.
     */
    private final char trailPlacement;
    /**
     * Player currently associated with this component, if any.
     */
    private PlayerView player;
    /**
     * Total number of card picks granted by the ticket.
     */
    private int sumPick;

    /**
     * Creates a bidding ticket view from model data that can be sent to the client.
     *
     * @param ticket the ticket value
     */
    public BiddingTicketView(BiddingTicket ticket) {
        this.foodBonus = ticket.getFoodBonus();
        this.chooseUpperCard = ticket.getChooseUpperCard();
        this.chooseLowerCard = ticket.getChooseLowerCard();
        this.numPlayer = ticket.getNumPlayer();
        this.trailPlacement = ticket.getTrailPlacement();
        this.sumPick = chooseLowerCard + chooseUpperCard;
        Player playerOnTicket = ticket.getPlayer();

        if (playerOnTicket != null) {
            this.player = new PlayerView(playerOnTicket);
        } else {
            this.player = null;
        }
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

    public PlayerView getPlayer() {
        return player;
    }

    public boolean isTaken() {
        return player != null;
    }

    @Override
    public String toString() {
        String nickname = player == null ? "empty" : player.getNickname();

        return "Ticket " + trailPlacement +
                " | food=" + foodBonus +
                " | upper=" + chooseUpperCard +
                " | lower=" + chooseLowerCard +
                " | player=" + nickname;
    }

    public void setPlayer(PlayerView player) {
        this.player = player;
    }

    /**
     * First it reduces the sumPick when it's called during a pick
     * if sumPick then is 0 the player cannot do another pick
     *
     * @return true if all pick are done, otherwise false
     */
    public boolean allPickDone() {
        this.sumPick--;
        if (sumPick <= 0) {
            sumPick = this.chooseLowerCard + this.chooseUpperCard;
            return true;
        }

        return false;
    }
}