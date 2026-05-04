package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class ShamanRitualView extends CardView {
    private int maxPP;
    private int minPP;

    public ShamanRitualView(int id, int era,int maxPP, int minPP) {
        super(id,era);
        this.maxPP = maxPP;
        this.minPP = minPP;
    }

    @Override
    public String toString() {
        return "| ShamanRitualView{" +
                "maxPP=" + maxPP +
                ", minPP=" + minPP +
                "} |";
    }
}
