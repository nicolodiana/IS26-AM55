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

/**
 * Serializable DTO that exposes the client-visible state of a player.
 * <p>It includes nickname, totem color, resources, prestige points, food and the cards owned by the player.
 */
public class PlayerView implements Serializable {
    /**
     * Nickname of the player represented by this object.
     */
    private final String nickname ;
    /**
     * Color selected by the player for the totem.
     */
    private final String totemColor;
    /**
     * Amount of food owned by this player.
     */
    private int food;
    /**
     * Amount of prestige points owned by this player.
     */
    private int points;
    /**
     * Cards currently visible in this player hand.
     */
    private List<CardView> myHand = new ArrayList<>();

    /**
     * Creates a player view from model data that can be sent to the client.
     *
     * @param player the player from the model from which to take the snapshot
     */
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

    /**
     * Processes a card picking action requested by the current player.
     *
     * @param card the card to add to this player hand
     */
    public void pickCard(CardView card) {
        this.myHand.add(card);
    }

    public void setPointsAndFood(int newFood, int newPp) {
        this.food = newFood;
        this.points = newPp;
    }
}
