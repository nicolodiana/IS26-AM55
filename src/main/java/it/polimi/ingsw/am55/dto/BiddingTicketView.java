package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.BiddingTicket;

import java.io.Serializable;

public class BiddingTicketView implements Serializable {

    private final int foodBonus;
    private final int chooseLowerCard;
    private final int chooseUpperCard;
    private final int numPlayer;
    private final char trailPlacement;
    private PlayerView player;
    private int sumPick;

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
     * First it reduces the sumPick cause it's called during a pick
     * if sumPick then is 0 the player cannot do another pick so i return true and reset the counter
     * @return
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