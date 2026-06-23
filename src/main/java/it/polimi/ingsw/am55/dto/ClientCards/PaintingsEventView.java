package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;
/**
 * DTO for a cave-painting event card.
 * <p>It exposes the event data and CLI metadata required to render the event in the client views.
 */
public class PaintingsEventView extends CardView {
    /**
     * Prestige points to lose by the upper-row condition.
     */
    private int upperPP;
    /**
     * Prestige points awarded by the lower-row condition.
     */
    private int lowerPP;
    /**
     * Number of artist cards required for the upper-row reward.
     */
    private int upperNumberOfArtist;
    /**
     * Number of artist cards required for the lower-row reward.
     */
    private int lowerNumberOfArtist;


    /**
     * Creates a paintings event view from model data that can be sent to the client.
     *
     * @param id the identifier of this card
     * @param era the era associated with the card
     * @param upperPP the prestige-point value involved in the operation
     * @param lowerPP the prestige-point value involved in the operation
     * @param upperNumberOfArtist the upper number of artist value
     * @param lowerNumberOfArtist the lower number of artist value
     */

    public PaintingsEventView(int id, int era, int upperPP, int lowerPP, int upperNumberOfArtist, int lowerNumberOfArtist) {
        super(id, era);
        this.upperPP = upperPP;
        this.lowerPP = lowerPP;
        this.upperNumberOfArtist = upperNumberOfArtist;
        this.lowerNumberOfArtist = lowerNumberOfArtist;
    }

    @Override
    public String toString() {
        return "Cave Paintings Event" +
                "\nUpper PP: " + upperPP +
                "\nLower PP: " + lowerPP +
                "\nUpperNumberOfArtist: " + upperNumberOfArtist +
                "\nLowerNumberOfArtist: " + lowerNumberOfArtist;
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
                        new CliCardDetails("Type", "Paintings Event"),
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}
