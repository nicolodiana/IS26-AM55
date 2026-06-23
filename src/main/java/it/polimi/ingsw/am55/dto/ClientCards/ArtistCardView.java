package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for an artist character card.
 * <p>It exposes the card data and CLI metadata required to render the artist in the client views.
 */
public class ArtistCardView extends CardView {

    /**
     * Creates an artist card view from model data that can be sent to the client.
     *
     * @param id the identifier of the card/view
     * @param era the era associated with the card
     */
    public ArtistCardView(int id, int era){
        super(id, era);
    }

    @Override
    public String toString() {
        return "Artist";
    }

    /**
     * Builds the CLI rendering metadata used to display this card.
     *
     * @return the CLI rendering metadata for this card
     */
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
