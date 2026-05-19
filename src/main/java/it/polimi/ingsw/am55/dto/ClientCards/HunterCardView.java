package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;

public class HunterCardView extends CardView {
    private Boolean icon;
    //final CharacterType type = CharacterType.COLLECTOR;

    public HunterCardView(int id, Boolean icon, int era) {
        super(id, era);
        this.icon = icon;
        //this.era= era;
    }

    public Boolean getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        if (icon == false) { return "Hunter"; }
        //return "| Hunter with icon { id=" + id + "} |";
        return "Hunter" +
                "\nIcon is present: " + icon;
    }
}
