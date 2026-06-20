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
     * Creates a view containing the current game data.
     *
     * @return a new view of this game
     */
    public GameView toView() {
        return new GameView(this);
    }

    /**
     * Creates a lobby view containing the current game state and player list.
     *
     * @return a new lobby view for this game
     */
    public LobbyView toLobbyView() {return new LobbyView(getGameState(), getPlayers());}
    /**
     * A modifier method that allows a new player to join the game.
     * It allocates a new Player object using the two parameters received and adds it to the end of the players list.
     *
     * @param nickname that is the player's nickname to add in the game. Requires a lower case string.
     * @param totem that is the player's totem to add in the game. Requires a lower case string.
     * @throws TotemAlreadyUsed if the totem has been already taken.
     * @throws PlayerNumberOutOfRange if player is equals or greater than 5
     * @throws NicknameAlreadyUsed if the nickname has been already taken
     * @throws WrongTotemColor if the Totem Color doesn't exist
     * **/
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
//        if ("nico".equalsIgnoreCase(newPlayer.getNickname())) {
//            newPlayer.addTribeCard(new BuildingCard(
//                    999,
//                    1,
//                    0,
//                    0,
//                    BuildingType.BUILDING13,
//                    null,
//                    0
//            ));
//
//            System.out.println("[DEBUG] Aggiunta Building 13 a nico");
//        }

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
     * @return the game's identifier
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
    /**
     * Returns the shared board used by this game.
     *
     * @return the shared board
     */
    public Board getSharedBoard(){
        return sharedBoard;
    }

    /**
     * Sets the current round counter to the specified value.
     *
     * @param n the new round counter value
     */
    public void setCountRound(int n){
        this.countRound=n;
    }

    /**
     * Returns the players currently registered in the game.
     *
     * @return the internal list of players
     */
    public List<Player> getPlayers(){ return players;}

    /**
     * Returns the current round counter.
     *
     * @return the current round counter
     */
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
    public List<String> placeTotem(
            int index,
            String id
    ) throws BiddingTicketIsTaken,
            IndexOutOfBoundsException,
            IllegalArgumentException {

        if (!this.state.equals(GameState.PLACETOTEM)
                || !currentPlayer.getNickname().equals(id)) {

            throw new IllegalStateException(
                    "You can't place your totem"
            );
        }

        sharedBoard.setPlayer(index, currentPlayer);

        System.out.println(
                currentPlayer.getNickname()
                        + " Moved his Totem to the Bidding Trail"
        );

        Optional<Player> nextPlayer =
                sharedBoard.getNextPlayerFirstPhase(currentPlayer);

        if (nextPlayer.isPresent()) {
            currentPlayer = nextPlayer.get();

            System.out.println(
                    "Now playing: " + currentPlayer.getNickname()
            );

            return Collections.emptyList();
        }

        currentPlayer = sharedBoard.getFirstPlayerSecondPhase();

        System.out.println(
                "Second Phase starting\n"
                        + "Now playing: "
                        + currentPlayer.getNickname()
        );

        changeState(GameState.PICKCARD);
        sharedBoard.removeAllPlayersFromTurnTicket();

        return advanceAutomaticallyUntilInputNeeded();
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
    public List<String> pickCard(int id, String idPlayer) {
        if (!state.equals(GameState.PICKCARD)
                || !currentPlayer.getNickname().equals(idPlayer)) {

            throw new IllegalStateException(
                    "can't pick a card now"
            );
        }

        if (id < 1 || id > 120) {
            throw new IllegalArgumentException(
                    "Id is out of range"
            );
        }

        CardSearchResult cardSearchResult =
                new CardSearchResult();

        sharedBoard.findCard(id, cardSearchResult);

        isCardPickRowValid(cardSearchResult);

        obtainCard(cardSearchResult);
        
        return advanceAutomaticallyUntilInputNeeded();
    }

    /**
     * Checks whether the player still has at least one selectable card in a row
     * from which they are required to pick additional cards.
     *
     * @param player the player whose remaining choices are checked
     * @return {@code true} if at least one required row contains a selectable card;
     *         {@code false} otherwise
     */
    private boolean hasRemainingSelectableCard(Player player) {
        boolean needsUpperCard =
                player.getUpperRowCardSelected()
                        < sharedBoard.getChooseUpperCard(player);

        boolean needsLowerCard =
                player.getLowerRowCardSelected()
                        < sharedBoard.getChooseLowerCard(player);

        boolean canPickUpper =
                needsUpperCard
                        && sharedBoard.hasSelectableCard(
                        RowType.UPPER,
                        player
                );

        boolean canPickLower =
                needsLowerCard
                        && sharedBoard.hasSelectableCard(
                        RowType.LOWER,
                        player
                );

        return canPickUpper || canPickLower;
    }

    /**
     * Completes the current player's standard card-picking turn.
     * The player is moved back to the turn ticket, their row-selection counters
     * are reset, and the game advances to the next player, the special-pick phase,
     * event resolution, or end-game resolution as appropriate.
     */
    private void completeCurrentPickTurn() {

        sharedBoard.movePlayerToTurnTicket(currentPlayer);


        currentPlayer.clearRowCardsSelected();

        Optional<Player> nextPlayer =
                sharedBoard.nextPlayerSecondPhase();


        if (nextPlayer.isPresent()) {
            currentPlayer = nextPlayer.get();
            return;
        }

        for (Player player : players) {
            if (player.hasBuilding(BuildingType.BUILDING13)) {
                changeState(GameState.PICKSPECIAL);
                currentPlayer = player;
                return;
            }
        }


        secondPartPick();
    }

    /**
     * Advances through players who cannot perform any of the card picks still
     * required by their bidding ticket.
     * The process stops when it reaches a player with at least one selectable card,
     * the food bidding ticket, or the end of the standard card-picking phase.
     *
     * @return the nicknames of the skipped players, in the order in which they
     *         were skipped
     */
    private List<String> advancePickPhaseUntilPlayable() {
        List<String> skippedPlayers = new ArrayList<>();

        while (state == GameState.PICKCARD
                && currentPlayer != null) {

            int requiredUpperCards =
                    sharedBoard.getChooseUpperCard(currentPlayer);

            int requiredLowerCards =
                    sharedBoard.getChooseLowerCard(currentPlayer);


            if (requiredUpperCards == 0
                    && requiredLowerCards == 0) {

                break;
            }

            boolean completedAllPicks =
                    currentPlayer.getUpperRowCardSelected()
                            >= requiredUpperCards
                            &&
                            currentPlayer.getLowerRowCardSelected()
                                    >= requiredLowerCards;


            if (completedAllPicks) {
                completeCurrentPickTurn();
                continue;
            }

            if (hasRemainingSelectableCard(currentPlayer)) {
                break;
            }

            skippedPlayers.add(currentPlayer.getNickname());

            completeCurrentPickTurn();
        }

        return skippedPlayers;
    }

    /**
     * Checks whether the current player can perform a special pick from the upper row.
     * If no selectable upper-row card is available, the special pick is skipped and
     * the game advances to event resolution or end-game resolution.
     *
     * @return the nicknames of the players whose special pick was skipped
     */
    private List<String> advanceSpecialPhaseUntilPlayable() {
        List<String> skippedPlayers = new ArrayList<>();

        while (state == GameState.PICKSPECIAL
                && currentPlayer != null) {

            if (sharedBoard.hasSelectableCard(RowType.UPPER, currentPlayer)) {
                break;
            }

            skippedPlayers.add(currentPlayer.getNickname());

            secondPartPick();
        }

        return skippedPlayers;
    }

    /**
     * Automatically advances the game after a player's action until further input
     * is required. It skips blocked players during the standard card-picking phase
     * and skips an unavailable special pick when the upper row contains no
     * selectable card.
     *
     * @return the nicknames of all players skipped automatically
     */
    private List<String> advanceAutomaticallyUntilInputNeeded() {
        List<String> skippedPlayers = new ArrayList<>();

        skippedPlayers.addAll(advancePickPhaseUntilPlayable());
        skippedPlayers.addAll(advanceSpecialPhaseUntilPlayable());

        return skippedPlayers;
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
    public void pickSpecial(int id, String idPlayer) {
        if (!state.equals(GameState.PICKSPECIAL)
                || !currentPlayer.getNickname().equals(idPlayer)) {

            throw new IllegalStateException(
                    "Can't activate special Pick from building effect"
            );
        }

        if (id < 1 || id > 120) {
            throw new IllegalArgumentException(
                    "Id is out of range"
            );
        }

        CardSearchResult cardSearchResult =
                new CardSearchResult();

        sharedBoard.findCardUpperRow(id, cardSearchResult);

        obtainCard(cardSearchResult, false);

        secondPartPick();
    }

    /**
     * Resolves the events currently ordered by the shared board, applies each event
     * to all players, restores the board for the next round, and starts the next
     * totem-placement phase.
     *
     * @return the views describing the events resolved during this phase
     * @throws IllegalStateException if the game is not in the event-resolution state
     */
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


    /**
     * Selects the phase that follows card picking.
     * The game enters end-game resolution when the round counter has reached ten;
     * otherwise, it enters event resolution.
     */
    private void secondPartPick() {
        if (countRound >= 10){
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

    /**
     * Transfers the card described by the search result to the current player and
     * removes it from the shared board. Building affordability is checked before
     * the transfer. When requested, the selection counter for the card's row is
     * incremented.
     *
     * @param cardSearchResult the result containing the selected card and its row
     * @param updateRowCounters whether the current player's row-selection counter
     *                          must be updated
     * @throws CannotAffordBuildingException if the selected building is not affordable
     */
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
     * Handles the special case where a current player gets the food bonus from the top position on the bidding trail.
     * It provides 3 foods to the current player.
     * @throws IllegalCallerException  if invoked when there are more than 5 players in the game
     * or when the current player is not on the food card.
     * **/
    public List<String> pickFood(String id) {
        if (!state.equals(GameState.PICKCARD)
                || currentPlayer == null
                || !currentPlayer.getNickname().equals(id)) {

            throw new IllegalStateException(
                    "The current player cannot pick food now"
            );
        }

        if (players.size() != 5) {
            throw new IllegalStateException(
                    "There aren't 5 players"
            );
        }

        if (sharedBoard.getChooseUpperCard(currentPlayer) != 0
                || sharedBoard.getChooseLowerCard(currentPlayer) != 0) {

            throw new IllegalStateException(
                    "The current player isn't on the food bidding ticket"
            );
        }

        currentPlayer.addFood(3);

        completeCurrentPickTurn();

        List<String> skippedPlayers =
                advanceAutomaticallyUntilInputNeeded();

        if ((state == GameState.PICKCARD
                || state == GameState.PICKSPECIAL)
                && currentPlayer != null) {

            System.out.println(
                    "Now playing: "
                            + currentPlayer.getNickname()
            );
        }

        return skippedPlayers;
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

    /**
     * Applies all non-event end-game effects to the specified player and records
     * every applied bonus as an end-game effect view.
     *
     * @param p the player whose end-game effects are applied
     * @return the views describing the bonuses applied to the player
     */
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
    /**
     * Determines the winner or winners by comparing prestige points and, when
     * necessary, food as the tie-breaker. For a single winner, the mapped value is
     * the winner's prestige-point total; when multiple players remain tied, each
     * mapped value is that player's food total.
     *
     * @return the internal map containing the winning players
     */
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

    /**
     * Resolves all final events, applies non-event end-game effects, determines the
     * winner or winners, changes the game state to ended, and creates the final
     * result view.
     *
     * @return the result of the end-game resolution
     * @throws IllegalStateException if the game is not in the end-game-resolution state
     */
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

    /**
     * Ends the game by changing its state to {@link GameState#ENDED}.
     */
    @Override
    public void quitGame() {
        changeState(GameState.ENDED);
    }

    /**
     * Checks whether a player with the specified nickname is registered in the game.
     * The comparison is case-sensitive.
     *
     * @param idPlayer the nickname to search for
     * @return {@code true} if a matching player is present; {@code false} otherwise
     */
    public boolean isInGame(String idPlayer){
        for (Player p : players){
            if (p.getNickname().equals(idPlayer))
                return true;
        }
        return false;
    }

    /**
     * Returns the current game state.
     *
     * @return the current game state
     */
    @Override
    public GameState getState() {
        return getGameState();
    }

    /**
     * Searches for a player with the specified nickname.
     * The comparison is case-sensitive.
     *
     * @param nickname the nickname to search for
     * @return the matching player, or {@code null} if no player has that nickname
     */
    public Player getSinglePlayer(String nickname) {
        for (Player p : this.players) {
            if (p.getNickname().equals(nickname)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Returns the prestige-point total of the player with the specified nickname.
     *
     * @param nickname the nickname of the player
     * @return the player's prestige-point total
     * @throws NullPointerException if no player has the specified nickname
     */
    public int getPlayerPoints(String nickname) {
        return getSinglePlayer(nickname).getNumPP();
    }

    /**
     * Returns the food total of the player with the specified nickname.
     *
     * @param nickname the nickname of the player
     * @return the player's food total
     * @throws NullPointerException if no player has the specified nickname
     */
    public int getPlayerFood(String nickname) {
        return getSinglePlayer(nickname).getNumFoods();
    }



}
