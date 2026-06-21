package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class ShamanRitualEventView extends CardView {
    private int maxPP;
    private int minPP;

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
