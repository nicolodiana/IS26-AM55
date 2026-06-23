package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * Client-side DTO for a builder character card.
 * <p>It exposes prestige-point and building-discount information for rendering in the client views.
 */
public class BuilderCardView extends CardView {
    /**
     * Number of prestige points granted by this element.
     */
    private int numPP;
    /**
     * DTO field carrying the pickbuildingdiscount value.
     */
    private int pickbuildingdiscount;

    /**
     * Creates a builder card view from model data that can be sent to the client.
     *
     * @param id the identifier of this object
     * @param era the era associated with the card
     * @param numPP the num of pp value
     * @param pickbuildingdiscount the pickbuildingdiscount value
     */
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

    /**
     * Builds the CLI rendering metadata used to display this card.
     *
     * @return the CLI rendering metadata for this card
     */
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
