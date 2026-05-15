package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerView implements Serializable {

    private final String nickname ;
    private final String totemColor;
    private final int food;
    private final int points;
    private List<CardView> myHand = new ArrayList<>();

    public PlayerView(Player player) {
        this.nickname = player.getNickname();
        this.totemColor = player.getTotem();
        this.food = player.getNumFoods();
        this.points = player.getNumPP();
        this.myHand = player.giveMyHand();
    }

    public String getNickname() {
        return nickname;
    }

    public String getTotemColor() {
        return totemColor;
    }

    public int getFood() {
        return food;
    }

    public int getPoints() {
        return points;
    }

    public List<CardView> getMyHand() {
        return myHand;
    }
}
