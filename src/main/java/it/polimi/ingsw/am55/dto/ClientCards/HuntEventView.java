package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for a hunting event card.
 * <p>It exposes the event data and CLI metadata required to render the event in the client views.
 */
public class HuntEventView extends CardView {
    /**
     * Number of prestige points granted by this event.
     */
    private final int numPP;

    /**
     * Creates a hunt event view from model data that can be sent to the client.
     *
     * @param id the identifier of this card
     * @param era the era associated with the card
     * @param numPP the num of pp value
     */
    public HuntEventView(int id, int era, int numPP) {
        super(id, era);
        this.numPP = numPP;
    }

    public int getNumPP() {
        return numPP;
    }

    @Override
    public String toString() {
        return "Hunt Event" +
                "\nNum of PP: " + this.numPP;
    }

    /**
     * Builds the CLI rendering metadata used to display this card.
     *
     * @return the CLI rendering metadata for this card
     */
    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "EVENT",
                ConsoleColor.RED_BOLD,
                List.of(
                        new CliCardDetails("Type", "Hunt Event"),
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}