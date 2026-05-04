package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class SustenanceEventView extends CardView {
    private int numPP;

    // Costruttore dell'evento: inizializza la penalità in PP
    public SustenanceEventView(int id, int era, int numPP) {
        super(id,era);
        this.numPP = numPP;
    }

    @Override
    public String toString() {
        return "| SustenanceEventView{" +
                "numPP=" + numPP +
                "} |";
    }
}
