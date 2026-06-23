package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.view.cli.CliCardInfo;

import java.io.Serializable;

/**
 * Base serializable DTO for card information sent to clients.
 * <p>Specialized card views to render each concrete card type.
 */
public class CardView implements Serializable {
    /**
     * Unique identifier of this card.
     */
    protected int id;
    /**
     * Field carrying the era value for client-side rendering.
     */
    private int era;

    /**
     * Creates a card view from model data that can be sent to the client.
     */
    public CardView() {
    }

    public CardView(int id, int era) {
        this.id = id;
        this.era = era;
    }

    public int getId() {
        return id;
    }

    public int getEra() {
        return era;
    }

    /**
     * Builds the CLI rendering metadata used to display this card.
     *
     * @return the CLI rendering metadata for this card
     */
    public CliCardInfo getCliCardInfo() { return new CliCardInfo("", "", null); }

}