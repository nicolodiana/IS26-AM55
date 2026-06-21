package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

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
        return "Builder " +
                "\nNum of PP: " + numPP +
                "\nBuilding discount: " + pickbuildingdiscount + " food";
    }

    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "CHARACTER",
                ConsoleColor.CYAN_BOLD,
                List.of(
                        new CliCardDetails("Type", "Builder"),
                        new CliCardDetails("PP", String.valueOf(this.numPP)),
                        new CliCardDetails("Discount", String.valueOf(this.pickbuildingdiscount))
                )
        );
    }
}
