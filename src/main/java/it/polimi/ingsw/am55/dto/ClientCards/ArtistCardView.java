package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

public class ArtistCardView extends CardView {

    public ArtistCardView(int id, int era){
        super(id, era);
    }

    @Override
    public String toString() {
        return "Artist";
    }

    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "CHARACTER",
                ConsoleColor.CYAN_BOLD,
                List.of(
                    new CliCardDetails("Type", "Artist"),
                    new CliCardDetails("Effect", "Artist card")
                )
            );
    }
}
