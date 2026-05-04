package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class HuntEventView extends CardView {
    private final int numPP;

    public HuntEventView(int id, int era, int numPP) {
        this.numPP = numPP;
        super(id, era);
    }

    @Override
    public String toString() {
        return "| Hunting Event: " + this.numPP + " PP |";
    }
}
