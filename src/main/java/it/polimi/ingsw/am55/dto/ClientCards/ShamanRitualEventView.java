package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for a shaman ritual event card.
 * <p>It exposes the event data and CLI metadata required to render the event in the client views.
 */
public class ShamanRitualEventView extends CardView {
    /**
     * Maximum prestige points awarded by this effect.
     */
    private int maxPP;
    /**
     * Minimum prestige points lost by this effect.
     */
    private int minPP;

    /**
     * Creates a shaman ritual event view from model data that can be sent to the client.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param maxPP the prestige-point value involved in the operation
     * @param minPP the prestige-point value involved in the operation
     */
    public ShamanRitualEventView(int id, int era, int maxPP, int minPP) {
        super(id,era);
        this.maxPP = maxPP;
        this.minPP = minPP;
    }

    @Override
    public String toString() {
        return  "Shaman Ritual Event" +
                "\nMax PP: " + maxPP +
                "\nMin PP: " + minPP;
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
                        new CliCardDetails("Type", "Shaman Ritual"),
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}
