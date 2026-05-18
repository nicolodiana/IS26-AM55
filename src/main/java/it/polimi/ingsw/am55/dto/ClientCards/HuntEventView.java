package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class HuntEventView extends CardView {

    private final int numPP;

    public HuntEventView(int id, int era, int numPP) {
        super(id, era);
        this.numPP = numPP;
    }

    public int getNumPP() {
        return numPP;
    }

    @Override
    public String toString() {
        return "Hunt Event" +
                "\nNum of PP: " + this.numPP;
    }
}