package it.polimi.ingsw.am55.dto;

import java.io.Serializable;

public class CardView implements Serializable {
    protected int id;
    private int era;
    /*private int numStars = 0;
    private Boolean icon = false;
    private int numPP = 0;
    private int buildingDiscount = 0;
    private String iconInvention = null;

    private String type;*/

    public CardView() {

    }

    public CardView(int id, int era) {
        this.id = id;
        this.era = era;
    }

   /* public int getId() {
        return id;
    }

    public int getEra() {
        return era;
    }

    public String getType() {
        return type;
    }

    public int getNumStars() {
        return numStars;
    }

    public Boolean getIcon() {
        return icon;
    }

    public int getNumPP() {
        return numPP;
    }

    public int getBuildingDiscount() {
        return buildingDiscount;
    }

    public String getIconInvention() {
        return iconInvention;
    }*/
}
