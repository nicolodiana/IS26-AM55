package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.ClientModel.ClientCard;
import it.polimi.ingsw.am55.dto.CardView;

public class BuilderCardView extends CardView {
    int numPP;
    int pickbuildingdiscount; //sconto che forniscono su ogni edificio
    //final CharacterType type = CharacterType.BUILDER;

    public BuilderCardView(int id, int era, int numPP, int pickbuildingdiscount) {
        super(id, era);
        this.numPP = numPP;
        this.pickbuildingdiscount = pickbuildingdiscount;
    }

    public int getNumPP() {
        return numPP;
    }

    public int getPickbuildingdiscount() {
        return pickbuildingdiscount;
    }

    @Override
    public String toString() {
        return "| Builder with " + numPP + "PP {id=" + id + "} |";
    }
}
