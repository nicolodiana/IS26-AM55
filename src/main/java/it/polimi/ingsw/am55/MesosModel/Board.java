package it.polimi.ingsw.am55.MesosModel;

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

    private void setNewEra(){
        currentEra = currentEra + 1;
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
        int index = biddingTrail.getPlayerPositionOnTrail(player);
        playerOrder.addPlayer(player);
    }

    //after all the event in order to restore the board
    public void restoreForRound(int numPlayers){
        lowerRow.clearRoundEnd();
        lowerRow.swapTribeRow(upperRow, lowerRow);
        TribeCard tmp;
        for (int i = 0; i < numPlayers + 4; i++) {
            tmp =tribeDeck.getNextCard();
            tmp.addInRightList(upperRow);
            if (tmp.getEra() != currentEra) {
                startNewEra();
            }
        }
    }

    public void startNewEra() {
        currentEra++;
        lowerRow.clearBuildingCards();
        lowerRow.swapBuildingRow(upperRow, lowerRow);
        moveBuildingCards(getBuildingDeck(currentEra), upperRow.getBuildingCardsList());
    }



}
