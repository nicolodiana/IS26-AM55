package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class PaintingsEventView extends CardView {
    private int upperPP;
    private int lowerPP;
    private int upperNumberOfArtist;
    private int lowerNumberOfArtist;


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
