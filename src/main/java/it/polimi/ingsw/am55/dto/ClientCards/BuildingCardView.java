package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.dto.CardView;

public class BuildingCardView extends CardView {
    private int foodCost;
    private int numOfPP;
    BuildingType type;
    private CharacterType CharType;
    private int effectPP;

    public BuildingCardView(int id, int era, int foodCost, int numOfPP, BuildingType type, CharacterType CharType, int effectPP){
        super(id,era);
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.CharType = CharType;
        this.effectPP = effectPP;
    }

    @Override
    public String toString() {
        return //type + " " +
                "Id: " + id +
                "\nFood cost: " + foodCost +
                "\nNum of PP: " + numOfPP +
                "\nType: " + type +
                "\nCharType: " + CharType +
                "\nEffect to PP: " + effectPP;
    }
}
