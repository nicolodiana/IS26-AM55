package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Cards.TribeCard;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Decks.TribeDeck;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Exceptions.EmptyTribeDeckException;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.*;

public class Board {
    private int currentEra;
    private TurnTicket playerOrder;
    private BiddingTrail biddingTrail;
    private TribeDeck tribeDeck;
    private Row upperRow;
    private Row lowerRow;
    private BuildingDeck buildingDeckEra1;
    private BuildingDeck buildingDeckEra2;
    private BuildingDeck buildingDeckEra3;

    public Board() {
        currentEra = 1;
        playerOrder = new TurnTicket();
        biddingTrail = new BiddingTrail();
        tribeDeck = new TribeDeck();
        upperRow = new Row();
        lowerRow = new Row();
        buildingDeckEra1 = new BuildingDeck();
        buildingDeckEra2 = new BuildingDeck();
        buildingDeckEra3 = new BuildingDeck();

    }

    public void initBoard(List<Player> players) {
        int numPlayers = players.size();

        playerOrder.initTurnTicket(players);

        biddingTrail.initBiddingTrail(numPlayers);

        //create and join the tribeDeck
        tribeDeck.initTribeDeck(numPlayers);

        //create buildingDeck
        buildingDeckEra1.initBuildingDeckEra1(numPlayers);
        buildingDeckEra2.initBuildingDeckEra2(numPlayers);
        buildingDeckEra3.initBuildingDeckEra3(numPlayers);
    }



    //getter
    public int getCurrentEra(){
        return currentEra;
    }
    public TurnTicket getPlayerOrder() {
        return playerOrder;
    }
    public Row getUpperRow(){
        return upperRow;
    }
    public Row getLowerRow(){
        return lowerRow;
    }
    public BiddingTrail getBiddingTrail(){
        return biddingTrail;
    }
    public TribeCard drawFromTribeDeck(){
        return tribeDeck.getNextCard();
    }
    public BuildingDeck getBuildingDeck(int currentEra) {
        return switch (currentEra) {
            case 1 -> buildingDeckEra1;
            case 2 -> buildingDeckEra2;
            case 3 -> buildingDeckEra3;
            default -> null;
        };
    }

    public void setUpLowerRow(int numPlayers){
        while( (lowerRow.getCharacterCardsList().size() + lowerRow.getEventCardsList().size()) <= numPlayers){
            tribeDeck.getNextCard().addInRightRow(upperRow, lowerRow);
        }
    }
    public void setUpUpperRow(int numPlayers){
        while((upperRow.getCharacterCardsList().size() + upperRow.getEventCardsList().size()) <= (numPlayers + 3)){
            tribeDeck.getNextCard().addInRightList(upperRow);
        }
        moveBuildingCards(buildingDeckEra1, upperRow.getBuildingCardsList());
    }

    public void moveBuildingCards(BuildingDeck donor, BuildingDeck receiver){
        donor.swapBuildingDeck(donor, receiver);
    }

    public void movePlayerToTurnTicket(Player player){
        playerOrder.addPlayer(player);
    }

    //after all the event in order to restore the board
    public boolean restoreForRound(int numPlayers) throws EmptyTribeDeckException{
        lowerRow.clearRoundEnd();
        lowerRow.swapTribeRow(upperRow, lowerRow);
        TribeCard tmp;
        for (int i = 0; i < numPlayers + 4; i++) {
            if (tribeDeck.isEmpty()){
                return false;
            }
            tmp =tribeDeck.getNextCard();
            tmp.addInRightList(upperRow);
            if (tmp.getEra() != currentEra) {
                startNewEra();
            }
        }
        return true;
    }

    private void startNewEra() {
        currentEra++;
        lowerRow.clearBuildingCards();
        lowerRow.swapBuildingRow(upperRow, lowerRow);
        moveBuildingCards(getBuildingDeck(currentEra), upperRow.getBuildingCardsList());
    }

    /*public boolean getIsTaken(int index) {
        return biddingTrail.getIsTaken(index);
    }*/ //If the bidding ticket has already taken throws BiddingTicketIsTaken => getIsTaken isn't used

    public void setPlayer(int index, Player player) throws BiddingTicketIsTaken,IndexOutOfBoundsException{
        biddingTrail.setPlayer(index, player);
    }

    public Optional<Player> getNextPlayerFirstPhase(Player player) throws IllegalArgumentException{
        return playerOrder.getNextPlayerFirstPhase(player);
    }//***Changed by using optional

    public Player getFirstPlayerSecondPhase(){
        return biddingTrail.getFirstPlayerSecondPhase();
    }

    /*public int getFoodBonus(Player player){
        return biddingTrail.getFoodBonus(player);
    }*/ //***Unused we consider to assign 3 foods directly

    public Optional<Player> nextPlayerSecondPhase(Player currentPlayer){
        return biddingTrail.nextPlayerSecondPhase(currentPlayer);
    }//***Changed by using optional

    public Player getPlayerFromTurnTicket(int index){
        return playerOrder.getTurnPlayer(index);
    }

    public Player getFirstPlayerFirstPhase(){
        return playerOrder.getFirstPlayerFirstPhase();
    }

    public int getChooseUpperCard(Player player){
        return biddingTrail.getChooseUpperCard(player);
    }

    public int getChooseLowerCard(Player player){
        return biddingTrail.getChooseLowerCard(player);
    }

    public void giveMalusOrBonus(Player player){
        playerOrder.giveMalusOrBonus(player);
    }

    public void removePlayerFromBiddingTrail(Player player){
        biddingTrail.removePlayer(player);
    }

    public void findCard(int id, CardSearchResult cardSearchResult, RowType rowType) throws IllegalArgumentException{
        if (upperRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.UPPER);
        }
        throw new IllegalArgumentException("Card not found");
    }

    public void findCard(int id, CardSearchResult cardSearchResult){
        if (upperRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.UPPER);
        }
        else if (lowerRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.LOWER);
        }
        throw new IllegalArgumentException("Card not found");
    }

    public void removeCard(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.UPPER) {
            upperRow.removeCard(cardSearchResult);
        }
        else {
            lowerRow.removeCard(cardSearchResult);
        }
    }
    public BuildingCard getBuildingCardByIndex(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.UPPER) {
            return upperRow.getBuildingCardByIndex(cardSearchResult.getIndexInList());
        } else{
            return lowerRow.getBuildingCardByIndex(cardSearchResult.getIndexInList());
        }
    }

    public void eventResolve(List<Player> players, RowType rowType){
        if (rowType == RowType.UPPER) {
            upperRow.eventResolve(players);
        } else if (rowType == RowType.LOWER) {
            lowerRow.eventResolve(players);
        }
    }

    public void eventResolveEndGame(List<Player> players){
        ArrayList<EventCard> events = new ArrayList<EventCard>();
        events.addAll(lowerRow.getEventCardsList());
        events.addAll(upperRow.getEventCardsList());

        lowerRow.eventResolve(players, events);
    }
}
