package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
/**
 * Model representation of a character tribe card.
 * <p>Character cards are placed in the upper row and apply permanent or immediate effects when added to a player.
 */
public class CharacterCard extends TribeCard{
    /**
     * Minimum number of players required for this card or component to be used.
     */
    private int numPlayer;
    /**
     * Creates a character card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    public CharacterCard(int id, int era) {
        super(id, era);
    }
    /**
     * Places this card in the board row required by its card category.
     *
     * @param upperRow the upper board row
     * @param lowerRow the lower board row
     */
    public void addInRightRow(Row upperRow, Row lowerRow){
        lowerRow.addCharacterCard(this);
    }
    /**
     * Adds this card to the correct list inside the target row.
     *
     * @param row the board row affected by the operation
     */
    public void addInRightList(Row row){
        row.addCharacterCard(this);
    }


}
