package it.polimi.ingsw.am55.MesosModel.Game;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.*;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;
import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
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
public class Game implements GameModelInterface{
    private final String id;
    private List<Player> players;
    private Player currentPlayer;
    private Board sharedBoard;
    private int countRound;
    private List<Player> winners;
    private int numPlayers;
    private GameState state;

    /**
     * Generates a new game identifier, creates the player list,
     * initializes countRound to 0, instantiates a new Board, and sets winners to null.
     */
    public Game(int numPlayers){
        id =UUID.randomUUID().toString();
        players= new ArrayList<>();
        countRound=0;
        sharedBoard = new Board();
        winners = null;
        this.numPlayers=numPlayers;
        this.state=GameState.CREATED;
    }
    /**
     * A modifier method that allows a new player to join the game.
     * It allocates a new Player object using the three parameters received and adds it to the end of the players list.
     *
     * @param player that is the player to add in the game
     * **/
    public void addPlayer(Player player) throws PlayerNumberOutOfRange {
       if(player == null) throw new NullPointerException("Player is null");
       else if(players.size()>numPlayers){throw new PlayerNumberOutOfRange("The number of player is out of range");}
       for(Player p:players){
            if(p.getNickname().equals(player.getNickname())){
                throw new NicknameAlreadyUsed("Nickname already exists");
            }else if(p.getTotem().equals(player.getTotem())){
                throw new TotemAlreadyUsed("Totem is already exists");
            }
        }
        players.add(player);
    }
    /**
     * Returns the game's id
     * @return l'id della partita
     */
    public String getIdGame() {return id;}
    /**
     * Returns the player whose turn it is.
     * @return the current player in the game
     */
    public String getCurrentPlayer(){
        return currentPlayer.getNickname();
    }
    /**
     * Returns the number of players in the game
     * @return the number of players in the game
     */
    public int getNumPlayers(){return numPlayers;}
    /**
     * Return if the game's state
     * @return GameState which consist of game's state
     **/
    public GameState getGameState(){return state;}
    /**
     * Returns the list of match winners or the winner
     * @return the list of winning players
     * @throws GameNotFinished If the game isn't ended
     */
    public List<String> getWinners(){
        if(this.state.equals(GameState.CREATED) || this.state.equals(GameState.STARTED)){
            throw new GameNotFinished("Game isn't ended");
        }else{
            return players.stream().map(Player::getNickname).collect(Collectors.toList());
        }
    }
    /**
     * Places the current player's totem from turn order ticket to the indicated bidding ticket.
     * If after placement, there are still players who need to place
     * their totem, the turn passes to the next player on the turn order ticket.
     * Otherwise, the placement phase ends and the first player in the second phase is set as current player
     * and  updates the game round.
     *
     * @param index must find an existing and untaken  bidding ticket.
     * @throws IllegalArgumentException if the currentPlayer is null
     * @throws BiddingTicketIsTaken if the indicated bidding ticket has already taken
     * @throws IndexOutOfBoundsException if the index is out of the range of bidding tickets
     */
    public void placeTotem(int index) throws BiddingTicketIsTaken,IndexOutOfBoundsException,IllegalArgumentException{

        //check if the selected position is taken
        //sharedBoard.getIsTaken(index);
        //setupping in the selected position

        //If the bidding ticket has already taken throws BiddingTicketIsTaken => getIsTaken isn't used
        sharedBoard.setPlayer(index, currentPlayer);

        System.out.println(currentPlayer.getNickname() + " Moved his Totem to the Bidding Trail");

        //get the next player
        Optional<Player> nextPlayer = sharedBoard.getNextPlayerFirstPhase(currentPlayer);

        if(nextPlayer.isEmpty()){
            currentPlayer = sharedBoard.getFirstPlayerSecondPhase();

            System.out.println("Second Phase starting\n" + "Now playing: " + currentPlayer.getNickname());
            return;
        }
        currentPlayer = nextPlayer.get();

        System.out.println("Now playing: " + currentPlayer.getNickname());
    }



    public void pickCard(int id){
        if(id<1 || id > 120){ throw new IllegalArgumentException("Id is out of range");}

        CardSearchResult cardSearchResult = new CardSearchResult();

        sharedBoard.findCard(id, cardSearchResult);
        isCardPickRowValid(cardSearchResult);// This method compares how many cards you've already taken from that row with
        // how many you're entitled to according to your position on the bidding trail

        if (cardSearchResult.getCardType() == CardType.CHARACTER){
            //da verificare e aggiungere l'attivazione degli effetti
            cardSearchResult.addToPlayer(currentPlayer); //CardResult gives to player the card he picked
            sharedBoard.removeCard(cardSearchResult);
        } else {
            BuildingCard buildingCard = sharedBoard.getBuildingCardByIndex(cardSearchResult);
            int costWithDiscount = buildingCard.getFoodCost(); //da aggiungere lo sconto in base al numero di costruttori
            if (currentPlayer.getNumFoods()< costWithDiscount){
                throw new CannotAffordBuildingException("You can't afford this Building card");
            }
            currentPlayer.payFood(costWithDiscount); //Before add the card to player he must pay
            cardSearchResult.addToPlayer(currentPlayer);
            sharedBoard.removeCard(cardSearchResult);
        }

        updateRowCardSelected(cardSearchResult);// Add the cards selected by player

        //check Player's turn end, and place him on turn ticket
        if  (currentPlayer.getUpperRowCardSelected() == sharedBoard.getChooseUpperCard(currentPlayer) &&
                currentPlayer.getLowerRowCardSelected() == sharedBoard.getChooseLowerCard(currentPlayer)){

            sharedBoard.movePlayerToTurnTicket(currentPlayer);
        }
        Optional<Player> nextPlayer = sharedBoard.nextPlayerSecondPhase(currentPlayer);

        //check if the PlayingPlayer was the last player of the round
        if (nextPlayer.isEmpty()){
            //eventuali effetti fine Round prima della risoluzione degli eventi

            sharedBoard.eventResolve(players, RowType.LOWER);

            try { //WHAT????
                sharedBoard.restoreForRound(numPlayers);
            } catch (EmptyTribeDeckException e){
                sharedBoard.eventResolveEndGame(players);
                endGame();
            }
        }
        Player playerPlayer = nextPlayer.get();
        sharedBoard.removePlayerFromBiddingTrail(playerPlayer);

        countRound++;
        if (countRound == 11){
            sharedBoard.eventResolveEndGame(players);
            endGame();
        }
    }

    private void isCardPickRowValid(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.LOWER){
            if (currentPlayer.getLowerRowCardSelected() == sharedBoard.getChooseLowerCard(currentPlayer)){
                throw new CantPickFromRow("You can't pick a card from the Lower Row");
            }
        } else if (cardSearchResult.getRowType() == RowType.UPPER) {
            if (currentPlayer.getUpperRowCardSelected() == sharedBoard.getChooseUpperCard(currentPlayer)){
                throw new CantPickFromRow("You can't pick a card from the Upper Row");
            }
        }
    }

    private void updateRowCardSelected(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.LOWER){
            currentPlayer.addLowerRowCardSelected();
        }
        else {
            currentPlayer.addUpperRowCardSelected();
        }
    }
    /**
     * Handles the special case where a current player gets the food bonus from the top position on the bidding trail.
     * It provides 3 foods to the current player.
     * @throws IllegalCallerException  if invoked when there are more than 5 players in the game
     * or when the current player is not on the food card.
     * **/
    public void pickFood(){
        int playerPosition = sharedBoard.getBiddingTrail().getPlayerPositionOnTrail(currentPlayer); //Come cambia
        //If current player is on the first ticket, he will get food, else
        // the methods will throw exception
        if(players.size()==5 && playerPosition==0){
            currentPlayer.addFood(3);
            sharedBoard.movePlayerToTurnTicket(currentPlayer);
            Player p = currentPlayer;
            Optional<Player> nextPlayer = sharedBoard.getNextPlayerFirstPhase(currentPlayer);
            currentPlayer = nextPlayer.get();
            sharedBoard.removePlayerFromBiddingTrail(p);
        }else{
            throw new IllegalStateException("The current player isn't on the first card or there aren't 5 players");
        }

        System.out.println("Now playing: " + currentPlayer.getNickname());

    }
    /**
     * Changes the game's state
     * **/
    private void changeState(GameState newState){
        this.state=newState;
    }
    /**
     * Initializes the initial state of the game.
     * The method initializes the shared board based on the players in the game,
     * assigns the initial food resources to the players in turn order,
     * sets the round number to 1, and selects the first player in turn order as the currentPlayer.
     */
    public void startGame() {
        if(state.equals(GameState.CREATED)){
            try{
                sharedBoard.initBoard(players);
                Player player;
                byte food=2;
                for(int i=0; i<players.size(); i++){
                    player = sharedBoard.getPlayerFromTurnTicket(i);
                    player.addFood(food);
                    if(i%2==0) food++;
                }
            }catch(Exception e){
                //find the exception
            }
            this.changeState(GameState.STARTED);
            countRound=1;
            currentPlayer = sharedBoard.getFirstPlayerSecondPhase();
        }else{
            throw new GameAlreadyStarted("The game is already  started");
        }

        System.out.println("Now playing: "+currentPlayer.getNickname());
    }
    /**
     * Ends the game and determines the winners.
     * The method selects the players with the highest number of victory points.
     * If multiple players are tied, a tie-break is applied based on the amount
     * of food. Only the players with the highest food among them remain winners.
     */
    public void endGame() {
        if(state.equals(GameState.STARTED)){
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
        changeState(GameState.ENDED);
    }

}
