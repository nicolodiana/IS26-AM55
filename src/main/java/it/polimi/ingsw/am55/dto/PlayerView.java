package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.*;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.am55.MesosModel.Enum.CharacterType.*;

public class PlayerView implements Serializable {

    private final String nickname ;
    private final String totemColor;
    private int food;
    private int points;
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

    public void pickCard(CardView card) {
        this.myHand.add(card);
    }

    public void setPointsAndFood(int newFood, int newPp) {
        this.food = newFood;
        this.points = newPp;
    }
}
