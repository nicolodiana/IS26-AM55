package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class PaintingsEventView extends CardView {
    private int upperPP;
    private int lowerPP;
    private int upperNumberOfArtist;
    private int lowerNumberOfArtist;


    public PaintingsEventView(int id, int era, int upperPP, int lowerPP, int upperNumberOfArtist, int lowerNumberOfArtist) {
        super(id, era);
        this.upperPP = upperPP;
        this.lowerPP = lowerPP;
        this.upperNumberOfArtist = upperNumberOfArtist;
        this.lowerNumberOfArtist = lowerNumberOfArtist;
    }

    @Override
    public String toString() {
        return "| PaintingsEventView{" +
                "upperPP=" + upperPP +
                ", lowerPP=" + lowerPP +
                ", upperNumberOfArtist=" + upperNumberOfArtist +
                ", lowerNumberOfArtist=" + lowerNumberOfArtist +
                "} |";
    }
}
