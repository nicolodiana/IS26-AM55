package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class CardSearchResult {
    private Card card;
    private CardType cardType;
    private RowType rowType;
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
