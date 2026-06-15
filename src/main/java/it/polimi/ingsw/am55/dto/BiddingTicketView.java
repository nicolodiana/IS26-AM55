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

    public BiddingTicketView(BiddingTicket ticket) {
        this.foodBonus = ticket.getFoodBonus();
        this.chooseUpperCard = ticket.getChooseUpperCard();
        this.chooseLowerCard = ticket.getChooseLowerCard();
        this.numPlayer = ticket.getNumPlayer();
        this.trailPlacement = ticket.getTrailPlacement();

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
}