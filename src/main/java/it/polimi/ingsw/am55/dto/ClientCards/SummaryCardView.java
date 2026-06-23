package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;
/**
 * Client-side DTO for the summary reference card.
 * <p>It provides the textual event summary shown by the client views.
 */
public class SummaryCardView extends CardView {
    /**
     * Unique identifier of this project object.
     */
    private final int id;

    /**
     * Creates a summary card view from model data that can be sent to the client.
     *
     * @param id the identifier to use for the object
     */
    public SummaryCardView(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "- Shaman Ritual: player with max stars gain ? pp,\n " +
                "player with min stars lose ? pp\n" +
                "\n- Hunting Event: gain 1 food per hunter with icon\n" +
                "\n- Cave Paintings Event: player with less/equal artists than ? lose ? pp,\n " +
                "player with more/equal ? artist gain ? pp per artist\n" +
                "\n- Sustenance Event: lose 1 food per character";
    }

    /**
     * Builds the CLI rendering metadata used to display this card.
     *
     * @return the CLI rendering metadata for this card
     */
    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "SUMMARY",
                ConsoleColor.BLUE_BRIGHT,
                List.of(
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}
