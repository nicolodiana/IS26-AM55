package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for a shaman character card.
 * <p>It exposes the shaman star value and CLI metadata required to render the card in the client views.
 */
public class ShamanCardView extends CardView {
    /**
     * Number of shaman stars present in this card.
     */
    private int numStars;
    //final CharacterType type = CharacterType.SHAMAN;

    /**
     * Creates a shaman card view from model data that can be sent to the client.
     *
     * @param id the identifier of this object
     * @param era the era associated with the card
     * @param numStars the num stars value
     */
    public ShamanCardView(int id,  int era, int numStars) {
        super(id, era);
        this.numStars = numStars;
    }

    public int getNumStars() {
        return this.numStars;
    }

    @Override
    public String toString() {
    return "Shaman " +
            "\nNum of starts: " + numStars;
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
                        new CliCardDetails("Type", "Shaman"),
                        new CliCardDetails("Effect", String.valueOf(this.numStars))
                )
        );
    }
}
