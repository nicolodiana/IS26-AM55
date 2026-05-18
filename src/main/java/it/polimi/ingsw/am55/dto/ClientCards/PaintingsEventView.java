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
        return "Cave Paintings Event" +
                "\nUpper PP: " + upperPP +
                "\nLower PP: " + lowerPP +
                "\nUpperNumberOfArtist: " + upperNumberOfArtist +
                "\nLowerNumberOfArtist: " + lowerNumberOfArtist;
    }
}
