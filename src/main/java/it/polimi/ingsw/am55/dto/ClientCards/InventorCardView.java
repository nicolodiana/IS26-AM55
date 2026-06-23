package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for an inventor character card.
 * <p>It exposes the invention icon and CLI metadata required to render the card in the client views.
 */
public class InventorCardView extends CardView {
   /**
    * Identifier of the invention icon present in this card.
    */
    private String iconInvention;

    /**
     * Creates an inventor card view from model data that can be sent to the client.
     *
     * @param iconInvention the icon invention value
     * @param id the identifier of this card
     * @param era the era associated with the card
     */
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
        return "Inventor" +
                "\nIcon: " + iconInvention;
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
                        new CliCardDetails("Type", "Inventor"),
                        new CliCardDetails("Icon", this.iconInvention)
                )
        );
    }
}
