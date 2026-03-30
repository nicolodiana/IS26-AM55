package it.polimi.ingsw.am55.MesosModel;
import it.polimi.ingsw.am55.MesosModel.Effect.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;
/**
 * DESCRIPTION:  The Game class is the model's entry point to the controller.
 * A Game instance represents a single game and coordinates the main steps of the game flow: player entry,
 * board initialization, current player management, totem placement, card acquisition,
 * eventual food bonus acquisition,and winner determination.
 * Each game is identified by a unique ID and maintains in its state the list of players, the shared board,
 * the reference to the current player,
 * the current round, and the set of winners (if any).
 */
public class Game {
    private String id;
    private List<Player> players;
    private Player currentPlayer;
    private Board sharedBoard;
    private int countRound;
    private List<Player> winners;

    /**
     * Generates a new game identifier, creates the player list,
     * initializes countRound to 0, instantiates a new Board, and sets winners to null.
     */
    public Game(){
        id =UUID.randomUUID().toString();
        players= new ArrayList<>();
        countRound=0;
        sharedBoard = new Board();
        winners = null;
    }
    /**
     * A modifier method that allows a new player to join the game.
     * It allocates a new Player object using the three parameters received and adds it to the end of the players list.
     *
     * @param  nickname indicates the player's nickname, it must be not null and not empty string
     * @param totem indicates the player's totem color, it must be not null and not empty string
     * @param summaryCardImage indicates the player's path of summary card, it must be not null and not empty string
     * **/
    public void addPlayer(String nickname, String totem, String summaryCardImage){
        players.add(new Player(nickname,totem,summaryCardImage));
    }
    /**
     * Returns the game's id
     * @return l'id della partita
     */
    public String getId() {return id;}
    /**
     * Returns the player whose turn it is.
     * @return the current player in the game
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    /**
     * Returns the list of match winners or the winner
     * @return the list of winning players
     */
    public List<Player> getWinners(){return winners;}
    /**
     * Places the current player's totem from turn order ticket to the indicated bidding ticket.
     * If after placement, there are still players who need to place
     * their totem, the turn passes to the next player on the turn order ticket.
     * Otherwise, the placement phase ends and the first player in the second phase is set as current player
     * and  updates the game round.
     *
     * @param index must find an existing and untaken  bidding ticket.
     * @throws IllegalArgumentException if the indicated bidding ticket has already taken.
     */
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
    /**
     * Handles the special case where a current player gets the food bonus from the top position on the bidding trail.
     * It provides 3 foods to the current player.
     * @throws IllegalCallerException  if invoked when there are more than 5 players in the game
     * or when the current player is not on the food card.
     * **/
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
    /**
     * Initializes the initial state of the game.
     * The method initializes the shared board based on the players in the game,
     * assigns the initial food resources to the players in turn order,
     * sets the round number to 1, and selects the first player in turn order as the currentPlayer.
     */
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
    /**
     * Ends the game and determines the winners.
     * The method selects the players with the highest number of victory points.
     * If multiple players are tied, a tie-break is applied based on the amount
     * of food. Only the players with the highest food among them remain winners.
     */
    private void endGame() {
        for (Player p : players) {

            // Painters' end-of-game effect.
            //Painters' end-of-game effect. Since the operation is only between int s, the result will also be truncated to the integer part only.
            //Example: 5:2 = 2, because with 5 artists, the calculation on which to apply 10 points is 2.
            p.addPP(p.getArtistsList().size() / 2 * 10);

            // Builders end game effect
            int sumPPbuilders = 0;
            int multiplier = p.hasBuilding(BuildingType.BUILDING9) ? 2 : 1;
            for (Builder b : p.getBuildersList()) {
                sumPPbuilders += b.getNumPP();
            }
            p.addPP(sumPPbuilders * multiplier);

            // Inventors' end-of-game effect
            int inventorCount = p.getInventorsList().size();
            //I create a set on the fly that adds me only if the element is not present (so I take into account the distinct icons) cost O(n)
            Set<String> distinctIcons = new HashSet<>();
            for (Inventor inventor : p.getInventorsList()) {
                distinctIcons.add(inventor.getIconInvention());
            }
            p.addPP(inventorCount * distinctIcons.size());

            // Building Effect 11
            if (p.hasBuilding(BuildingType.BUILDING11)) {
                p.addPP(p.minCardSet() * 6);
            }

            // Building Effect 12
            if (p.hasBuilding(BuildingType.BUILDING12)) {
                for (BuildingCard bc : p.getBuildings()) {
                    if (bc.getType().equals(BuildingType.BUILDING12)) {
                        //p.addPP(bc.getCharacterForED().countSameTypeIn(p) * bc.getNumOfPP());
                        break;
                    }
                }
            }

            // Building Effect 14
            multiplier = p.hasBuilding(BuildingType.BUILDING14) ? 1 : 0;
            p.addPP(25 * multiplier);
        }

        // ─── Winner calculation (outside the forum, after updating all PPs) ───

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
