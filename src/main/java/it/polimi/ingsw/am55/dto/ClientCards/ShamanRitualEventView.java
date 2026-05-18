package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class ShamanRitualEventView extends CardView {
    private int maxPP;
    private int minPP;

    public ShamanRitualEventView(int id, int era, int maxPP, int minPP) {
        super(id,era);
        this.maxPP = maxPP;
        this.minPP = minPP;
    }

    @Override
    public String toString() {
        return  "Shaman Ritual Event" +
                "\nMax PP: " + maxPP +
                "\nMin PP: " + minPP;
    }
}
