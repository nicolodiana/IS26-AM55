package it.polimi.ingsw.am55.dto;

import java.io.Serializable;

public class CardView implements Serializable {

    protected int id;
    private int era;

    public CardView() {
    }

    public CardView(int id, int era) {
        this.id = id;
        this.era = era;
    }

    public int getId() {
        return id;
    }

    public int getEra() {
        return era;
    }

    public void addToPlayer(PlayerView player) { }
}