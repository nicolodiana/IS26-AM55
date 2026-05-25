package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class CollectorCardView extends CardView {
    final private int foodDiscount = 3;
    //final CharacterType type = CharacterType.COLLECTOR;

    public CollectorCardView(int id, int era) {

        super(id, era);
        //this.era= era;
    }

    @Override
    public String toString() {
        return "Collector";
    }

    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "CHARACTER",
                ConsoleColor.CYAN_BOLD,
                List.of(
                        new CliCardDetails("Type", "Collector"),
                        new CliCardDetails("Effect", "Food discount")

                )
        );
    }
}
