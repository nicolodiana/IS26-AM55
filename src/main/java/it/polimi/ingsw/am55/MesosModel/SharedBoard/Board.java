package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Cards.TribeCard;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Decks.TribeDeck;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.BiddingTicketView;
import it.polimi.ingsw.am55.dto.BoardView;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.PlayerView;

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

        setUpLowerRow(numPlayers);
        setUpUpperRow(numPlayers);
    }

    //Test Helper
    public TribeDeck getTribeDeck(){
        return tribeDeck;
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
    //    public TribeCard drawFromTribeDeck(){
//        return tribeDeck.getNextCard();
//    }
    public BuildingDeck getBuildingDeck(int currentEra) throws IllegalArgumentException {
        return switch (currentEra) {
            case 1 -> buildingDeckEra1;
            case 2 -> buildingDeckEra2;
            case 3 -> buildingDeckEra3;
            default -> throw new IllegalArgumentException("BuildingDeck of era: " + currentEra + "doesn't exist");
        };
    }

    public void setUpLowerRow(int numPlayers){
        while( (lowerRow.getCharacterCardsList().size() + lowerRow.getEventCardsList().size()) < numPlayers+1){
            tribeDeck.getNextCard().addInRightRow(upperRow, lowerRow);
        }
    }
    public void setUpUpperRow(int numPlayers){
        while((upperRow.getCharacterCardsList().size() + upperRow.getEventCardsList().size()) < (numPlayers + 4)){
            tribeDeck.getNextCard().addInRightList(upperRow);
        }
        moveBuildingDeck(buildingDeckEra1, upperRow);
    }

//    public void moveBuildingCards(BuildingDeck donor, BuildingDeck receiver){
//        donor.swapBuildingDeck(donor, receiver);
//    }

    public void moveBuildingDeck(Row donor, Row receiver){
        receiver.setBuildingCardsList(donor.getBuildingCardsList());
        donor.clearBuildingCards();
    }
    public void moveBuildingDeck(BuildingDeck donor, Row receiver){
        receiver.setBuildingCardsList(donor);
        donor.clear();
    }

    public void movePlayerToTurnTicket(Player player){
        biddingTrail.removePlayer(player);
        playerOrder.addPlayer(player);
        playerOrder.giveMalusOrBonus(player);
    }

    //after all the event in order to restore the board
    public boolean restoreForRound(int numPlayers) {
        lowerRow.clearRoundEnd();
        lowerRow.swapTribeRow(upperRow);
        TribeCard tmp;
        for (int i = 0; i < numPlayers + 4; i++) {
            if (tribeDeck.isEmpty()){
                return false;
            }
            tmp =tribeDeck.getNextCard();//Return a TribeCard to put in the row for next round
            //By using polymorfism we can insert the card in the correct list
            tmp.addInRightList(upperRow);
            if (tmp.getEra() > currentEra) {
                startNewEra();
            }
        }
        return true;
    }

    public void startNewEra() {
        currentEra++;
        lowerRow.clearBuildingCards();
        moveBuildingDeck(upperRow, lowerRow);
        moveBuildingDeck(getBuildingDeck(currentEra), upperRow);
    }

    /*public boolean getIsTaken(int index) {
        return biddingTrail.getIsTaken(index);
    }*/ //If the bidding ticket has already taken throws BiddingTicketIsTaken => getIsTaken isn't used

    public void setPlayer(int index, Player player) throws BiddingTicketIsTaken,IndexOutOfBoundsException{
        biddingTrail.setPlayer(index, player);
    }

    public void removeAllPlayersFromTurnTicket(){
        playerOrder.removePlayerFromTurnTicket();
    }

    public void removeAllPlayers(){
        removeAllPlayersFromTurnTicket();
        biddingTrail.removeAllPlayers();
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

    public Optional<Player> nextPlayerSecondPhase() throws IllegalArgumentException{
        //if(currentPlayer==null) throw new IllegalArgumentException("Player is null");
        return biddingTrail.nextPlayerSecondPhase();
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

    public void findCardUpperRow(int id, CardSearchResult cardSearchResult) throws IllegalArgumentException{
        if (upperRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.UPPER);
            return;
        }
        throw new IllegalArgumentException("Card not found");
    }

    public void findCard(int id, CardSearchResult cardSearchResult){
        if (upperRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.UPPER);
            return;
        }
        else if (lowerRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.LOWER);
            return;
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

    public List<EventCard> orderEvents(){
        return lowerRow.orderEvents();
    }

    public List<EventCard> orderEventsEndGame(){
        ArrayList<EventCard> events = new ArrayList<>();
        events.addAll(lowerRow.getEventCardsList());
        events.addAll(upperRow.getEventCardsList());

        events.sort(
                Comparator
                        .comparingInt(EventCard::getOrder)
                        .thenComparingInt(EventCard::getEra)
        );
        return events;
    }

    public int getPlayerPositionOnTrail(Player player){
        return biddingTrail.getPlayerPositionOnTrail(player);
    }

    //------------------------

    public BoardView toView() {
        List<CardView> upperRow = new ArrayList<>();
        List<CardView> lowerRow = new ArrayList<>();
        List<BiddingTicketView> biddingTrailView = new ArrayList<>();
//ogni carta bidding ticket la trasformo in ticket view e la inserisco nella biddingtrailView
        for (BiddingTicket ticket : biddingTrail.getTicketList()) {
            biddingTrailView.add(ticket.toView());
        }

        List<PlayerView> turnOrderView = new ArrayList<>();

        for (Player player : playerOrder.getTurnOrder()) {
            if (player != null) {
                turnOrderView.add(new PlayerView(player));
            } else {
                turnOrderView.add(null);
            }
        }


        upperRow = this.upperRow.createCardView();
        lowerRow = this.lowerRow.createCardView();

        return new BoardView(upperRow, lowerRow, turnOrderView, biddingTrailView);
    }

}
