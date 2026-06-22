package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class SummaryCardView extends CardView {
    private final int id;

    public SummaryCardView(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Summary card";
    }

    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "SUMMARY",
                ConsoleColor.BLUE_BRIGHT,
                List.of(
                        new CliCardDetails("Type", "Summary"),
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}
