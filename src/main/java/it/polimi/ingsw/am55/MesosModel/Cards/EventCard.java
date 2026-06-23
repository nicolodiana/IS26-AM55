package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveHuntingView;

import java.util.List;
/**
 * Base model for event tribe cards.
 * <p>Event cards are placed in the lower row, activated during event resolution, and converted to dedicated resolution DTOs for the client.
 */
public class EventCard extends TribeCard {
    /**
     * Minimum number of players required for this card or component to be used.
     */
    private int numPlayer;
    /**
     * Creates a event card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    public EventCard(int id, int era) {
        super(id, era);
    }

    /**
     * Resolves this event card and applies its effects to the participating players.
     *
     * @param players the players participating in the operation
     */
    public void activateEvent(List<Player> players) {}
    /**
     * Places this card in the board row required by its card category.
     *
     * @param upperRow the upper board row
     * @param lowerRow the lower board row
     */
    public void addInRightRow(Row upperRow, Row lowerRow){
        upperRow.addEventCard(this);
    }
    /**
     * Adds this card to the correct list inside the target row.
     *
     * @param row the board row affected by the operation
     */
    public void addInRightList(Row row){
        row.addEventCard(this);
    }
    /**
     * Returns the event-resolution order used to sort event cards.
     *
     * @return the order value
     */
    public int getOrder(){return 0;}
    /**
     * Builds the event-resolution view generated after resolving this event card.
     *
     * @return the client-facing view representation of this event card
     */
    public ResolveEventView toViewResolve(){ return new ResolveEventView(null, null); }
}
