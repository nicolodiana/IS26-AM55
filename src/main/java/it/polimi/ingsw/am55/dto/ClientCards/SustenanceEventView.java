package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class SustenanceEventView extends CardView {
    private int numPP;

    // Costruttore dell'evento: inizializza la penalità in PP
    public SustenanceEventView(int id, int era, int numPP) {
        super(id,era);
        this.numPP = numPP;
    }

    @Override
    public String toString() {
        return  "Sustenance Event" +
                "\nNumPP: " + numPP;
    }

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
