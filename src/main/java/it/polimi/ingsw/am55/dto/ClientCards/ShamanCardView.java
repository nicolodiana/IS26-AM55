package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class ShamanCardView extends CardView {
    private int numStars;
    //final CharacterType type = CharacterType.SHAMAN;

    public ShamanCardView(int id,  int era, int numStars) {
        super(id, era);
        this.numStars = numStars;
        //this.era = era;
    }

    public int getNumStars() {
        return this.numStars;
    }

    @Override
    public String toString() {
    return "Shaman " +
            "\nNum of starts: " + numStars;
    }

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
