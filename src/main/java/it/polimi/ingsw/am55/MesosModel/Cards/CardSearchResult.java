package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Mutable result object returned when searching a board row for a card.
 * <p>It keeps the found card together with its row, type, and index so the board can remove or apply it correctly.
 */
public class CardSearchResult {
    /**
     * Card found by the search operation.
     */
    private Card card;
    /**
     * Type of the card found by the search operation.
     */
    private CardType cardType;
    /**
     * Board row in which the card was found.
     */
    private RowType rowType;
    /**
     * Position of the card inside its row list.
     */
    private int indexInList;

    public Card getCard(){
        return card;
    }
    public void setCard(Card card){
        this.card = card;
    }
    public CardType getCardType(){
        return cardType;
    }
    public void setCardType(CardType cardType){this.cardType = cardType;}
    public RowType getRowType(){
        return rowType;
    }
    public void setRowType(RowType rowType){
        this.rowType = rowType;
    }
    public void addToPlayer(Player player){
        card.addToPlayer(player);
    }
    public void setIndexInList(int indexInList){
        this.indexInList = indexInList;
    }
    public int getIndexInList(){
        return indexInList;
    }

}
