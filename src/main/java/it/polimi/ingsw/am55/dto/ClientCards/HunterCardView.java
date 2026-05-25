package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class HunterCardView extends CardView {
    private Boolean icon;
    //final CharacterType type = CharacterType.COLLECTOR;

    public HunterCardView(int id, Boolean icon, int era) {
        super(id, era);
        this.icon = icon;
        //this.era= era;
    }

    public Boolean getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        if (icon == false) { return "Hunter"; }
        //return "| Hunter with icon { id=" + id + "} |";
        return "Hunter" +
                "\nIcon is present: " + icon;
    }

    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "CHARACTER",
                ConsoleColor.CYAN_BOLD,
                List.of(
                        new CliCardDetails("Type", "Hunter"),
                        new CliCardDetails("Icon", String.valueOf(this.icon))
                )
        );
    }
}
