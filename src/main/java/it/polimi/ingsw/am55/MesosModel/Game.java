package it.polimi.ingsw.am55.MesosModel;
import it.polimi.ingsw.am55.MesosModel.Effect.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;
public class Game {
    private String id;
    private List<Player> players;
    private Player currentPlayer;
    private Board sharedBoard;
    private int countRound;
    private List<Player> winners;

    public Game(){
        id =UUID.randomUUID().toString();
        players= new ArrayList<>();
        countRound=0;
        sharedBoard = new Board();
        winners = null;
    }

    public void addPlayer(String nickname, String totem, String summaryCardImage){
        players.add(new Player(nickname,totem,summaryCardImage));
    }
    public String getId() {return id;}
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    public List<Player> getWinners(){return winners;}

    public void placeTotem(int index){
        //Check if biddingTicket has already been taken
        if (sharedBoard.getBiddingTrail().getTicketList().get(index).getIsTaken()){
            throw new IllegalArgumentException("The index referring to bidding tile has already occupied");
        }
        //Setting the parameters for bidding ticket
        sharedBoard.getBiddingTrail().getTicketList().get(index).setIsTaken(true);
        sharedBoard.getBiddingTrail().getTicketList().get(index).setPlayer(currentPlayer);

        System.out.println(currentPlayer.getNickname() + " Moved his Totem to the Bidding Trail");

        //get the next player
        currentPlayer = sharedBoard.getPlayerOrder().getNextPlayerFirstPhase(currentPlayer);

        //if the next player doesn't exist, the second phase will start
        if(currentPlayer == null){
            currentPlayer = sharedBoard.getBiddingTrail().getFirstPlayerSecondPhase();

            System.out.println("Second Phase starting\n" + "Now playing: " + currentPlayer.getNickname());
            return;
        }
        System.out.println("Now playing: " + currentPlayer.getNickname());
    }


    //controlli: row , carta ,
    public void pickCard(boolean rowIndex, boolean list, int index) {
        Row row;

        int PlayerPosition = sharedBoard.getBiddingTrail().getPlayerPositionOnTrail(currentPlayer);
        if (rowIndex) {
            if(currentPlayer.getUpperRowCardSelected() == sharedBoard.getBiddingTrail().getTicketList().get(PlayerPosition).getChooseUpperCard()){
                throw new IllegalArgumentException("You  can't pick a card from the Upper Row");
            }
            row = sharedBoard.getUpperRow();
        } else {
            if(currentPlayer.getLowerRowCardSelected() == sharedBoard.getBiddingTrail().getTicketList().get(PlayerPosition).getChooseLowerCard()){
                throw new IllegalArgumentException("U can't pick a card from the Lower Row");
            }
            row = sharedBoard.getLowerRow();
        }

        if (list) { //List is true when the player gets the card from bulding list
            BuildingCard card = row.getBuildingCardsList().getBuildingDeck().get(index);
            if (currentPlayer.getNumFoods()<card.getFoodCost()){
                throw new IllegalArgumentException("You can't afford this Building card");
            }
            //Player can pay => add card in the deck and remove bulding card from upper row
            card.addToPlayer(currentPlayer);
            row.getBuildingCardsList().removeBuildingCard(card);
        } else {
            CharacterCard card = row.getCharacterCardsList().get(index);

            //check effect somehow ??!?!?!???!?!!?!?!?!??!?!?!!??!?!?!?!?!?!?!?
            card.addToPlayer(currentPlayer);
            //aggiornare la lista togliendo la carta
            row.removeCharacterCard(card);
        }

        //Updating the number of cards that the player picked from row
        if (rowIndex){
            currentPlayer.addUpperRowCardSelected();
        } else {
            currentPlayer.addLowerRowCardSelected();
        }

        //Checking if the current player has finished picking up all cards
        if  (currentPlayer.getUpperRowCardSelected() == sharedBoard.getBiddingTrail().getTicketList().get(PlayerPosition).getChooseUpperCard() &&
                currentPlayer.getLowerRowCardSelected() == sharedBoard.getBiddingTrail().getTicketList().get(PlayerPosition).getChooseLowerCard()){
            sharedBoard.movePlayerToTurnTicket(currentPlayer);
            currentPlayer = sharedBoard.getBiddingTrail().nextPlayerSecondPhase(currentPlayer, players.size());
            sharedBoard.getBiddingTrail().getTicketList().get(PlayerPosition).removePlayer();
        }

        //controllo se è l'ultimo player
        if (currentPlayer == null){

            //risolvo eventi
            //risolvo effetti
            //restoreForRound
        }

        countRound++;
        if (countRound == 11){
            endGame();
        }
    }

    public void pickFood(){
        int playerPosition = sharedBoard.getBiddingTrail().getPlayerPositionOnTrail(currentPlayer);
        //If current player is on the first ticket, he will get food, else
        // the methods will throw exception
        if(players.size()==5 && playerPosition==0){
            //ask if they prefer constant or method invocation
            //sharedBoard.getBiddingTrail().getTicketList().get(PlayerPosition).getFoodBonus()
            currentPlayer.addFood(3);

            sharedBoard.movePlayerToTurnTicket(currentPlayer);
            currentPlayer = sharedBoard.getBiddingTrail().nextPlayerSecondPhase(currentPlayer, players.size());
            sharedBoard.getBiddingTrail().getTicketList().get(playerPosition).removePlayer();
        }else{

            throw new IllegalCallerException("The current player isn't on the first card or there aren't 5 players");
        }

        System.out.println("Now playing: " + currentPlayer.getNickname());

    }


    public void startGame() {
        sharedBoard.initBoard(players);
        byte food=2;
        for(int i=0; i<players.size(); i++){
            sharedBoard.getPlayerOrder().getTurnOrder().get(i).addFood(food);
            if(i%2==0) food++;
        }
        countRound=1;
        currentPlayer = sharedBoard.getPlayerOrder().getTurnOrder().getFirst();

        System.out.println("Now playing: "+currentPlayer.getNickname());
    }

    private void endGame() {
        for (Player p : players) {

            // Effetto fine partita pittori.
            //Effetto fine partita pittori. Dato che l'operazioni è tra solo int , anche il risultato verrà troncato alla sola parte intera
            //esempio 5:2 = 2 giusto perche con 5 artisti il conbteggio su cui applicare 10 pp sono 2
            p.addPP(p.getArtistsList().size() / 2 * 10);

            // Effetto fine partita builders
            int sumPPbuilders = 0;
            int multiplier = p.hasBuilding(BuildingType.BUILDING9) ? 2 : 1;
            for (Builder b : p.getBuildersList()) {
                sumPPbuilders += b.getNumPP();
            }
            p.addPP(sumPPbuilders * multiplier);

            // Effetto fine partita inventori
            int inventorCount = p.getInventorsList().size();
            //creo un set on fly che mi aggiunge solo se l'elemento non è presente (cosi tengo conto delle icone distinte) costo O(n)
            Set<String> distinctIcons = new HashSet<>();
            for (Inventor inventor : p.getInventorsList()) {
                distinctIcons.add(inventor.getIconInvention());
            }
            p.addPP(inventorCount * distinctIcons.size());

            // Effetto edificio 11
            if (p.hasBuilding(BuildingType.BUILDING11)) {
                p.addPP(p.minCardSet() * 6);
            }

            // Effetto edificio 12
            if (p.hasBuilding(BuildingType.BUILDING12)) {
                for (BuildingCard bc : p.getBuildings()) {
                    if (bc.getType().equals(BuildingType.BUILDING12)) {
                        p.addPP(bc.getCharacterForED().countSameTypeIn(p) * bc.getNumOfPP());
                        break;
                    }
                }
            }

            // Effetto edificio 14
            multiplier = p.hasBuilding(BuildingType.BUILDING14) ? 1 : 0;
            p.addPP(25 * multiplier);
        }

        // ─── Calcolo vincitori (fuori dal for, dopo aver aggiornato tutti i PP) ───

        int max = players.stream()
                .mapToInt(Player::getNumPP)
                .max()
                .orElse(0);

        winners = players.stream()
                .filter(player -> player.getNumPP() == max)
                .collect(Collectors.toList());

        if (winners.size() != 1) {
            int max2 = winners.stream()
                    .mapToInt(Player::getNumFoods)
                    .max()
                    .orElse(0);

            winners = winners.stream()
                    .filter(player -> player.getNumFoods() == max2)
                    .collect(Collectors.toList());
        }
    }


}
