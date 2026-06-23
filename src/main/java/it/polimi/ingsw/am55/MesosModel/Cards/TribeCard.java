package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;

/**
 * Base model for tribe cards displayed on the shared board.
 * <p>Subclasses decide which board row and player collection they belong to when they are drawn or picked.
 */
public abstract class  TribeCard extends Card {

    /**
     * Creates a tribe card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    protected TribeCard(int id, int era) {
        super(id, era);
    }
    /**
     * Places this card in the board row required by its card category.
     *
     * @param upperRow the upper board row
     * @param lowerRow the lower board row
     */
    public abstract void addInRightRow(Row upperRow, Row lowerRow);
    /**
     * Adds this card to the correct list inside the target row.
     *
     * @param row the board row affected by the operation
     */
    public abstract void addInRightList(Row row);
}
