package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class CollectorCardView extends CardView {
    final private int foodDiscount = 3;
    //final CharacterType type = CharacterType.COLLECTOR;

    public CollectorCardView(int id, int era) {

        super(id, era);
        //this.era= era;
    }

    @Override
    public String toString() {
        return "Collector";
    }
}
