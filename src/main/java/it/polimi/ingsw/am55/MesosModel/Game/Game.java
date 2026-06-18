package it.polimi.ingsw.am55.MesosModel.Game;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.*;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Board;
import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.database.DatabaseManger;
import it.polimi.ingsw.am55.database.GameRepository;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
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
    private List<EventCard> eventList = new ArrayList<>();

    private GameRepository gameRepository;
    /**
     * The unique identifier for this specific game or match.
     */
    private final String id;

    /**
     * The list of players participating in the game.
     */
    private final List<Player> players;

    /**
     * The player whose turn is currently active.
     */
    private Player currentPlayer;

    /**
     * The main shared board of the game, containing common elements and resources.
     */
    private Board sharedBoard;

    /**
     * The counter tracking the current round number of the game.
     */
    private int countRound;

    /**
     * A map storing the winners of the game.
     * The key represents the player's identifier or name, and the value represents their final score.
     */
    private final Map<String, Integer> winners;

    /**
     * The total number of players participating in this game.
     */
    private final int numPlayers;

    /**
     * The current state or phase of the game (e.g., lobby, ongoing, finished).
     */
    private GameState state;
    private static final Set<String> VALID_TOTEM_COLORS = Set.of(
            "BLUE",
            "ORANGE",
            "PURPLE",
            "YELLOW",
            "WHITE"
    );

    /**
     * Generates a new game identifier, creates the player list,
     * initializes countRound to 0, instantiates a new Board, and sets winners to null.
     */
    public Game(int numPlayers)throws PlayerNumberOutOfRange{
        this.id =UUID.randomUUID().toString();
        this.players= new ArrayList<>();
        this.countRound=0;
        this.winners= new HashMap<>();
        this.sharedBoard = new Board();
        if(numPlayers<2 || numPlayers>5) throw new PlayerNumberOutOfRange("Invalid numbers of player");
        this.numPlayers=numPlayers;
        this.state=GameState.CREATED;
    }
    /**
     * A modifier method that allows a new player to join the game.
     * It allocates a new Player object using the two parameters received and adds it to the end of the players list.
     *
     * @param id that is the player's nickname to add in the game. Requires a lower case string.
     * @param totem that is the player's totem to add in the game. Requires a lower case string.
     * @throws TotemAlreadyUsed if the totem has been already taken.
     * @throws PlayerNumberOutOfRange if player is equals or greater than 5
     * @throws NicknameAlreadyUsed if the nickname has been already taken
     * **/
    public GameView toView() {
        return new GameView(this);
    }

    public LobbyView toLobbyView() {return new LobbyView(getGameState(), getPlayers());}

    public String addPlayer(String nickname, String totem)
            throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed, WrongTotemColor {

        String normalizedNickname = nickname.trim();
        String normalizedColor = totem.trim().toUpperCase();

        if (!VALID_TOTEM_COLORS.contains(normalizedColor)) {
            throw new WrongTotemColor(
                    "Wrong totem color. Please try again with this color: "
                            + String.join(", ", VALID_TOTEM_COLORS)
            );
        }

        for (Player p : players) {
            if (p.getNickname().equalsIgnoreCase(normalizedNickname)) {
                throw new NicknameAlreadyUsed("Nickname already exists");
            }else if(p.getTotem().equals(totem)){
                throw new TotemAlreadyUsed("Totem is already exists");
            }

            if (p.getTotem().equalsIgnoreCase(normalizedColor)) {
                throw new TotemAlreadyUsed("Totem already exists");
            }
        }

        Player newPlayer = new Player(
                normalizedNickname,
                normalizedColor.toLowerCase()
        );

        // PER DEBUG PICK SPECIAL
        if ("nico".equalsIgnoreCase(newPlayer.getNickname())) {
            newPlayer.addTribeCard(new BuildingCard(
                    999,
                    1,
                    0,
                    0,
                    BuildingType.BUILDING13,
                    null,
                    0
            ));

            System.out.println("[DEBUG] Aggiunta Building 13 a nico");
        }

        if (players.size() + 1 > numPlayers) {
            throw new PlayerNumberOutOfRange("The number of player is out of range");
        }

        players.add(newPlayer);

        if (players.size() == numPlayers) {
            startGame();
        }

        return newPlayer.getNickname();
    }
    /**
     * Returns the game's id
     * @return l'id della partita
     */
    public String getIdGame() {
        return id;}
    /**
     * Returns the player whose turn it is.
     * @return the current player in the game
     */
    public String getCurrentPlayer() {
        return currentPlayer == null ? null : currentPlayer.getNickname();
    }
    /**
     * Returns the current number of players in the game
     * @return the number of players in the game
     */
    public int getNumPlayers(){
        return players.size();
    }
    /**
     * Return if the game's state
     * @return GameState which consist of game's state
     **/
    public GameState getGameState(){
        return state;
    }

    //Getter for testing
    public Board getSharedBoard(){
        return sharedBoard;
    }
    public void setCountRound(int n){
        this.countRound=n;
    }
    public List<Player> getPlayers(){ return players;}
    public int getCountRound(){return this.countRound;}
    /**
     * Return the available color for totems in this game
     * @return available color for totems in this game
     * **/
    public Set<String> getTotemColorsValid(){
        if(!players.isEmpty()){
            Set<String> totemColors= Set.of("white","blue","red","yellow","black");
            return players.stream()
                    .filter(p->!totemColors.contains(p.getTotem()))
                    .map(Player::getTotem)
                    .collect(Collectors.toSet());
        }else{
            return Set.of("white","blue","red","yellow","black");
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
    public void placeTotem(int index, String id) throws BiddingTicketIsTaken,IndexOutOfBoundsException,IllegalArgumentException{
        if(!this.state.equals(GameState.PLACETOTEM) || !currentPlayer.getNickname().equals(id)){
            throw new IllegalStateException("You can't place your totem");
        }
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
            changeState(GameState.PICKCARD);
            sharedBoard.removeAllPlayersFromTurnTicket();
            return;
        }
        currentPlayer = nextPlayer.get();

        System.out.println("Now playing: " + currentPlayer.getNickname());
    }

    /**
     * Executes the card picking action for the current player.
     * This method validates the card ID, ensures the player is allowed to pick from the
     * specified row (based on their bidding trail position), and handles the logic
     * for card acquisition. It also checks if the player's turn has ended to apply
     * bonuses or maluses, manages the transition to the next player, and triggers
     * round-end procedures (event resolution and board restoration) if the last
     * player has finished.
     *
     * @param id the unique identifier of the card to be picked.
     * @throws IllegalStateException if the game is not in the PICKCARD state.
     * @throws IllegalArgumentException if the provided card ID is out of the valid range (1-120).
     * @throws CantPickFromRow if the player has already reached their limit for the row containing the card.
     * @throws CannotAffordBuildingException if the player lacks sufficient food to pay for a building card.
     */
    public void pickCard(int id,String idPlayer){
        if(!state.equals(GameState.PICKCARD) || !currentPlayer.getNickname().equals(idPlayer)){
            throw new IllegalStateException("can't pick a card now");
        }
        if(id<1 || id > 120){ throw new IllegalArgumentException("Id is out of range");}

        CardSearchResult cardSearchResult = new CardSearchResult();

        sharedBoard.findCard(id, cardSearchResult);
        isCardPickRowValid(cardSearchResult);// This method compares how many cards you've already taken from that row with
        // how many you're entitled to according to your position on the bidding trail

        obtainCard(cardSearchResult);

        //updateRowCardSelected(cardSearchResult);// Add the cards selected by player

        //check Player's turn end, and place him on turn ticket
        if  (currentPlayer.getUpperRowCardSelected() == sharedBoard.getChooseUpperCard(currentPlayer) &&
                currentPlayer.getLowerRowCardSelected() == sharedBoard.getChooseLowerCard(currentPlayer)){

            //Moving player on turn ticket, give malus or bonus and reset the total number of cards selected
            sharedBoard.movePlayerToTurnTicket(currentPlayer);
            currentPlayer.clearRowCardsSelected();

            //Gets the next player
            Optional<Player> nextPlayer = sharedBoard.nextPlayerSecondPhase();

            //check if the PlayingPlayer is the last player of the round
            if (nextPlayer.isEmpty()){
                for(Player p:players){
                    if(p.hasBuilding(BuildingType.BUILDING13)){
                        /*
                        per come è il gioco solo un player avrà la building 13
                        essendo unica quindi appena lo trovo cambio stato e aggiorno lui
                        come current player in modo che dalla view mi accorgo che devo fare
                        una pick special ( la risoluz. eventi quindi in questo caso sarà
                        posticipata e fatta dopo questa pick special )
                         */
                        changeState(GameState.PICKSPECIAL);
                        currentPlayer = p;
                        return;
                    }
                }
                secondPartPick(); //nel caso in cui non c'è nessuno con pick special passo diretto ad event resolve
            } else {
                currentPlayer = nextPlayer.get(); //Switching current player
            }
        }
    }
    /**
     * Handles a special card pick triggered by specific game effects (e.g., Building 13).
     * This method bypasses the standard row limits but restricts the search to the
     * upper row. After the card is obtained, it proceeds to resolve pending effects,
     * restores the board for the next round, or triggers the end-game sequence if
     * the maximum number of rounds has been reached.
     *
     * @param id the unique identifier of the card to be picked from the upper row.
     * @throws IllegalStateException if the game is not in the PICKSPECIAL state.
     * @throws IllegalArgumentException if the provided card ID is out of the valid range.
     */
    public void pickSpecial(int id,String idPlayer){
        if(!state.equals(GameState.PICKSPECIAL) || !currentPlayer.getNickname().equals(idPlayer)){
            throw new IllegalStateException("Can't activate special Pick from building effect");
        }
        if(id<1 || id > 120){ throw new IllegalArgumentException("Id is out of range");}

        CardSearchResult cardSearchResult = new CardSearchResult();

        sharedBoard.findCardUpperRow(id, cardSearchResult);

        obtainCard(cardSearchResult,false); //perche non deve sporcare i contatori di lower/upper card selected usati per gestire i ticket

        secondPartPick();
    }

    public List<ResolveEventView> eventResolve() {
        if (!state.equals(GameState.EVENTRESOLVE)) {
            throw new IllegalStateException("Cannot resolve events now");
        }

        List<ResolveEventView> resolvedEvents = new ArrayList<>();

        this.eventList = new ArrayList<>(sharedBoard.orderEvents());

        System.out.println("EventList: " + eventList);

        for (EventCard card : eventList) {
            card.activateEvent(players);

            ResolveEventView resolveView = card.toViewResolve();
            resolvedEvents.add(resolveView);
        }

        sharedBoard.restoreForRound(numPlayers);
        countRound++;

        changeState(GameState.PLACETOTEM);
        currentPlayer = sharedBoard.getFirstPlayerFirstPhase();

        return resolvedEvents;
    }


    private void secondPartPick() {
        if (countRound == 2){
            changeState(GameState.ENDGAMERESOLVE);
            return;
        }

        changeState(GameState.EVENTRESOLVE);
    }
    /**
     * Internal logic for moving a card from the shared board to the player's inventory.
     * If the card is a Character, it is added immediately. If it is a Building, the
     * method calculates the cost, verifies the player's ability to pay, deducts
     * the food resources, and then adds the card to the player's collection.
     *
     * @param cardSearchResult the result object containing the card found and its metadata.
     * @throws CannotAffordBuildingException if the player does not have enough food to purchase the building.
     */
    private void obtainCard(CardSearchResult cardSearchResult) {
        obtainCard(cardSearchResult, true);
    }

    private void obtainCard(CardSearchResult cardSearchResult, boolean updateRowCounters) {
        if (cardSearchResult.getCardType() == CardType.CHARACTER) {
            cardSearchResult.addToPlayer(currentPlayer);
            sharedBoard.removeCard(cardSearchResult);
        } else {
            BuildingCard buildingCard = sharedBoard.getBuildingCardByIndex(cardSearchResult);

            if (currentPlayer.getNumFoods() < (buildingCard.getFoodCost() - currentPlayer.totalBuildingDiscount())) {
                throw new CannotAffordBuildingException("You can't afford this Building card");
            }

            cardSearchResult.addToPlayer(currentPlayer);
            sharedBoard.removeCard(cardSearchResult);
        }

        if (!updateRowCounters) {
            return;
        }

        if (cardSearchResult.getRowType() == RowType.LOWER) {
            currentPlayer.addLowerRowCardSelected();
        } else {
            currentPlayer.addUpperRowCardSelected();
        }
    }
    /**
     * Validates whether the current player is permitted to pick a card from the
     * row specified in the search result.
     * It compares the player's current selection counters against the limits
     * defined by the shared board for the player's specific position.
     *
     * @param cardSearchResult the metadata of the card the player is attempting to pick.
     * @throws CantPickFromRow if the player's selection count for the row is already at maximum.
     */
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
    /**
     * Updates the internal counters of the current player based on the row
     * from which a card was successfully picked.
     * @param cardSearchResult the metadata of the successfully acquired card.
     */
    /*private void updateRowCardSelected(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.LOWER){
            currentPlayer.addLowerRowCardSelected();
        }
        else {
            currentPlayer.addUpperRowCardSelected();
        }
    }*/
    /**
     * Handles the special case where a current player gets the food bonus from the top position on the bidding trail.
     * It provides 3 foods to the current player.
     * @throws IllegalCallerException  if invoked when there are more than 5 players in the game
     * or when the current player is not on the food card.
     * **/
    public void pickFood(String id){
        if(currentPlayer.getNickname().equals(id)){
            //int playerPosition = sharedBoard.getPlayerPositionOnTrail(currentPlayer);
            //If current player is on the first ticket, he will get food, else
            // the methods will throw exception
            if(players.size()==5){
                currentPlayer.addFood(3);
                sharedBoard.movePlayerToTurnTicket(currentPlayer);
                Player p = currentPlayer;
                Optional<Player> nextPlayer = sharedBoard.nextPlayerSecondPhase();
                currentPlayer = nextPlayer.get();
            }else{
                throw new IllegalStateException("There aren't 5 players");
            }
        }else{
            throw new IllegalStateException("The current player isn't on the first card");
        }

        System.out.println("Now playing: " + currentPlayer.getNickname());

    }
    /**
     * Changes the game's state
     * **/
    protected void changeState(GameState newState){
        this.state=newState;
    }
    /**
     * Initializes the initial state of the game.
     * The method initializes the shared board based on the players in the game,
     * assigns the initial food resources to the players in turn order,
     * sets the round number to 1, and selects the first player in turn order as the currentPlayer.
     */
    private void startGame() {
        if(state.equals(GameState.CREATED)){
            sharedBoard.initBoard(players);
            byte food=2;
            for(int i=0; i<players.size(); i++){
                sharedBoard.getPlayerFromTurnTicket(i).addFood(food);
                if(i%2==0) food++;
            }
            countRound=1;
            currentPlayer = sharedBoard.getFirstPlayerFirstPhase();
        }
        changeState(GameState.PLACETOTEM);

        System.out.println("Now playing: "+currentPlayer.getNickname());
    }
    /**
     * Ends the game and determines the winners.
     * The method selects the players with the highest number of victory points.
     * If multiple players are tied, a tie-break is applied based on the amount
     * of food. Only the players with the highest food among them remain winners.
     * @return map that contains players winner with points
     */

//questo metodo applica gli effetti di fine partita al singolo player e ne tiene traccia degli effetti applicati nella
// lista di endgame effect view
    private List<EndGameEffectView> applyFinalEffectsToPlayer(Player p) {
        List<EndGameEffectView> effects = new ArrayList<>();

        // Painters' end-of-game effect:
        // ogni coppia di artisti dà 10 PP
        int paintersBonus = (p.getArtistsList().size() / 2) * 10;
        if (paintersBonus != 0) {
            p.addPP(paintersBonus);
            effects.add(new EndGameEffectView(
                    p.getNickname(),
                    "Bonus artisti: " + p.getArtistsList().size() + " artisti",
                    paintersBonus
            ));
        }

        // Builders' end-game effect
        int sumPPBuilders = 0;
        for (Builder b : p.getBuildersList()) {
            sumPPBuilders += b.getNumPP();
        }

        int building9Multiplier = p.hasBuilding(BuildingType.BUILDING9) ? 2 : 1;
        int buildersBonus = sumPPBuilders * building9Multiplier;

        if (buildersBonus != 0) {
            p.addPP(buildersBonus);

            String description = p.hasBuilding(BuildingType.BUILDING9)
                    ? "Bonus costruttori raddoppiato da Edificio 9"
                    : "Bonus costruttori";

            effects.add(new EndGameEffectView(
                    p.getNickname(),
                    description,
                    buildersBonus
            ));
        }

        // Inventors' end-of-game effect
        int inventorCount = p.getInventorsList().size();

        Set<String> distinctIcons = new HashSet<>();
        for (Inventor inventor : p.getInventorsList()) {
            distinctIcons.add(inventor.getIconInvention());
        }

        int inventorsBonus = inventorCount * distinctIcons.size();

        if (inventorsBonus != 0) {
            p.addPP(inventorsBonus);
            effects.add(new EndGameEffectView(
                    p.getNickname(),
                    "Bonus inventori: " + inventorCount + " inventori, "
                            + distinctIcons.size() + " icone distinte",
                    inventorsBonus
            ));
        }

        // Building Effect 11
        if (p.hasBuilding(BuildingType.BUILDING11)) {
            int building11Bonus = p.minCardSet() * 6;

            if (building11Bonus != 0) {
                p.addPP(building11Bonus);
                effects.add(new EndGameEffectView(
                        p.getNickname(),
                        "Bonus Edificio 11",
                        building11Bonus
                ));
            }
        }

        // Building Effect 12 / bonus end game buildings
        int buildingsBonus = 0;

        for (BuildingCard bc : p.getBuildings()) {
            buildingsBonus += bc.getEndGameBonus(p) * bc.getNumOfPP();
        }

        if (buildingsBonus != 0) {
            p.addPP(buildingsBonus);
            effects.add(new EndGameEffectView(
                    p.getNickname(),
                    "Bonus edifici di fine partita",
                    buildingsBonus
            ));
        }

        // Building Effect 14
        if (p.hasBuilding(BuildingType.BUILDING14)) {
            int building14Bonus = 25;

            p.addPP(building14Bonus);
            effects.add(new EndGameEffectView(
                    p.getNickname(),
                    "Bonus Edificio 14",
                    building14Bonus
            ));
        }

        return effects;
    }
//ritorna una mappa con id e punteggio del/dei vincitori
    private Map<String, Integer> calculateWinners() {
        this.winners.clear();

        int maxPP = players.stream()
                .mapToInt(Player::getNumPP)
                .max()
                .orElse(0);

        List<Player> winnersList = players.stream()
                .filter(player -> player.getNumPP() == maxPP)
                .collect(Collectors.toList());

        if (winnersList.size() > 1) {
            int maxFood = winnersList.stream()
                    .mapToInt(Player::getNumFoods)
                    .max()
                    .orElse(0);

            winnersList = winnersList.stream()
                    .filter(player -> player.getNumFoods() == maxFood)
                    .collect(Collectors.toList());

            for (Player p : winnersList) {
                this.winners.put(p.getNickname(), p.getNumFoods());
            }

            return this.winners;
        }

        Player winner = winnersList.getFirst();
        this.winners.put(winner.getNickname(), winner.getNumPP());

        return this.winners;
    }

    public EndGameResultView endGame() {
        if (!state.equals(GameState.ENDGAMERESOLVE)) {
            throw new IllegalStateException("Cannot resolve end game now");
        }

        List<ResolveEventView> resolvedEvents = new ArrayList<>();
        List<EndGameEffectView> endGameEffects = new ArrayList<>();

        /*
         * 1. Risolvo gli eventi finali.
         * Nell'end game uso lower row + upper row.
         */
        List<EventCard> finalEvents = new ArrayList<>(sharedBoard.orderEventsEndGame());

        for (EventCard card : finalEvents) {
            card.activateEvent(players);

            ResolveEventView resolveView = card.toViewResolve();
            resolvedEvents.add(resolveView);
        }

        /*
         * 2. Applico gli effetti finali non legati alle EventCard.
         */
        for (Player player : players) {
            List<EndGameEffectView> playerEffects = applyFinalEffectsToPlayer(player);
            endGameEffects.addAll(playerEffects);
        }

        /*
         * 3. Calcolo solo i vincitori.
         */
        Map<String, Integer> winners = calculateWinners();

        /*
        *  4.Salvo i dati nella base di dati
        */
        //RIATTIVO SOLO QUANDO HO DB LOCALE ALTRIMENTI DA PROBLEMI
//        gameRepository = new DatabaseManger();
//        gameRepository.registerGame(this.id,this.numPlayers);
//        for(Player p: players){
//            gameRepository.registerPlayer(this.id,p.getNickname(),p.getNumPP(),p.getNumFoods());
//        }
//        List<LeaderBoardEntryView> leaderBoard = gameRepository.getGeneralClassification(this.numPlayers);

        /*
         * 5. Cambio stato finale.
         */
        changeState(GameState.ENDED);

        return new EndGameResultView(resolvedEvents, endGameEffects, winners);
    }
    /**
     * Transitions the game state to CRASHED.
     * This is used to handle unexpected failures or network disconnections or the players leave the game.
     */
    @Override
    public void handleGameCrashed(){
        changeState(GameState.CRASHED);
    }

    @Override
    public void quitGame() {
        /*
        sharedBoard.removeAllPlayers();
        players.clear();
        currentPlayer = null;
        /*
         */
        changeState(GameState.ENDED);
    }

    public boolean isInGame(String idPlayer){
        for (Player p : players){
            if (p.getNickname().equals(idPlayer))
                return true;
        }
        return false;
    }

    @Override
    public GameState getState() {
        return getGameState();
    }

    public Player getSinglePlayer(String nickname) {
        for (Player p : this.players) {
            if (p.getNickname().equals(nickname)) {
                return p;
            }
        }

        return null;
    }

    public int getPlayerPoints(String nickname) {
        return getSinglePlayer(nickname).getNumPP();
    }

    public int getPlayerFood(String nickname) {
        return getSinglePlayer(nickname).getNumFoods();
    }



}
