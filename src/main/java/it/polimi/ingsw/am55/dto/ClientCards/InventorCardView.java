package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class InventorCardView extends CardView {
    private String iconInvention;
    //final CharacterType type = CharacterType.INVENTOR;

    public InventorCardView(String iconInvention, int id, int era) {
        super(id, era);
        this.iconInvention = iconInvention;
        //this.era= era;
    }

    public String getIconInvention() {
        return iconInvention;
    }

    @Override
    public String toString() {
        return "Inventor" +
                "\nIcon: " + iconInvention;
    }

    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "CHARACTER",
                ConsoleColor.CYAN_BOLD,
                List.of(
                        new CliCardDetails("Type", "Inventor"),
                        new CliCardDetails("Icon", this.iconInvention)
                )
        );
    }
}
