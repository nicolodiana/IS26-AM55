package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class ShamanCardView extends CardView {
    private int numStars;
    //final CharacterType type = CharacterType.SHAMAN;

    public ShamanCardView(int id, int numStars, int era) {
        super(id, era);
        this.numStars = numStars;
        //this.era = era;
    }

    public int getNumStars() {
        return this.numStars;
    }

    @Override
    public String toString() {
        return "Shaman";
    }
}
