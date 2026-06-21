package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.view.cli.CliCardInfo;

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
    public CliCardInfo getCliCardInfo() { return new CliCardInfo("", "", null); }
    public void addToPlayer(PlayerView player) { }
}