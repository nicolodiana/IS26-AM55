package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * Client-side DTO for a sustenance event card.
 * <p>It exposes the event data and CLI metadata required to render the event in the client views.
 */
public class SustenanceEventView extends CardView {
    /**
     * Number of prestige points to remove (multiplied by character that u can't sustain)
     */
    private int numPP;

    /**
     * Creates a sustenance event view from model data that can be sent to the client.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param numPP the num pp value
     */
    public SustenanceEventView(int id, int era, int numPP) {
        super(id,era);
        this.numPP = numPP;
    }


    @Override
    public String toString() {
        return  "Sustenance Event" +
                "\nNumPP: " + numPP;
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
                        new CliCardDetails("Type", "Sustenance Event"),
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}
