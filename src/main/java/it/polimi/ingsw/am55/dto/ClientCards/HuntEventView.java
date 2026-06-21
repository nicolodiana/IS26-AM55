package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class HuntEventView extends CardView {

    private final int numPP;

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