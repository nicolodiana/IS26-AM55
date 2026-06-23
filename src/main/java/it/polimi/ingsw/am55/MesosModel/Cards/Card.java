package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.CardView;
/**
 * Base model for every card used in the game.
 * <p>It provides common identity and era data, while concrete subclasses define how the card is added to a player and converted to a client view.
 */
public abstract class Card {
    /**
     * Unique identifier of this project object.
     */
    protected final int id ;
    /**
     * Card model field storing the era value.
     */
    public final  int era;

    /**
     * Creates a card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    protected Card(int id, int era) {
        this.id = id;
        this.era = era;
    }

    public int getEra() {
        return era;
    }
    public int getId() {return id;}

    /**
     * Applies this card to the specified player according to its game effect.
     *
     * @param player the player affected by the operation
     */
    public void addToPlayer(Player player) {}
    /**
     * Builds the client-facing view representation of this card.
     *
     * @return the client-facing view representation of this card
     */
    public CardView toView() { return new CardView(); }

}
