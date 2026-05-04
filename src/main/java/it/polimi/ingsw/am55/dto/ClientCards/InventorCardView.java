package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.ClientModel.ClientCard;
import it.polimi.ingsw.am55.dto.CardView;

public class InventorCardView extends CardView {
    private String iconInvention;
    //final CharacterType type = CharacterType.INVENTOR;

    public InventorCardView(String iconInvention, int id, int era) {
        super(id, era);
        this.iconInvention = iconInvention;
        //this.era= era;
    }

    public String getIconInvention() {
        return iconInvention;
    }

    @Override
    public String toString() {
        return "Inventor";
    }
}
