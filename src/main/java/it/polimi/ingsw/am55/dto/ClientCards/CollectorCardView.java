package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for a collector character card.
 * <p>It exposes the card data and CLI metadata required to render the collector in the client views.
 */
public class CollectorCardView extends CardView {
    /**
     * Food discount applied by this character effect
     */
    final private int foodDiscount = 3;

    /**
     * Creates a collector card view from model data that can be sent to the client.
     *
     * @param id the identifier of this object
     * @param era the era associated with the card
     */
    public CollectorCardView(int id, int era) {
        super(id, era);
    }

    @Override
    public String toString() {
        return "Collector";
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
                        new CliCardDetails("Type", "Collector"),
                        new CliCardDetails("Effect", "Food discount")

                )
        );
    }
}
