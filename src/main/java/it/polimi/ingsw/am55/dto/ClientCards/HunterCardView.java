package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for a hunter character card.
 * <p>It exposes the hunter icon and CLI metadata required to render the card in the client views.
 */
public class HunterCardView extends CardView {
    /**
     * Flag to know if the card has an icon or not.
     */
    private Boolean icon;
    /**
     * Creates a hunter card view from model data that can be sent to the client.
     *
     * @param id the identifier of this card
     * @param icon the icon value
     * @param era the era associated with the card
     */
    public HunterCardView(int id, Boolean icon, int era) {
        super(id, era);
        this.icon = icon;
    }

    public Boolean getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        if (icon == false) { return "Hunter"; }
        return "Hunter" +
                "\nIcon is present: " + icon;
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
                        new CliCardDetails("Type", "Hunter"),
                        new CliCardDetails("Icon", String.valueOf(this.icon))
                )
        );
    }
}
