package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.ClientCards.SummaryCardView;
/**
 * Reference card that summarizes the main event effects for players.
 * <p>It is converted to a client-side view but does not modify the game state directly.
 */
public class SummaryCard extends Card{

    /**
     * Creates a summary card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    public SummaryCard(int id, int era) {
        super(id, era);
    }

    /**
     * Builds the client-facing view representation of this summary card.
     *
     * @return the client-facing view representation of this summary card
     */
    public CardView toView() { return new SummaryCardView(this.id); }

}
