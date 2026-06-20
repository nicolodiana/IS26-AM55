package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Cards.TribeCard;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Decks.TribeDeck;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Exceptions.CannotPickEventCard;
import it.polimi.ingsw.am55.MesosModel.Exceptions.PlayerNotOnTrail;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.BiddingTicketView;
import it.polimi.ingsw.am55.dto.BoardView;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.PlayerView;

import java.util.*;

/**
 * Represents the shared game board and coordinates its mutable components.
 *
 * <p>The board owns the turn-order track, the bidding trail, the tribe deck,
 * the upper and lower card rows, and one building deck for each era. It is
 * responsible for initial setup, card-row restoration, era transitions,
 * player movement between the two tracks, card lookup and removal, event
 * ordering, and creation of the client-facing {@link BoardView}.</p>
 */
public class Board {

    /**
     * Era currently in play. A newly created board starts in era {@code 1}.
     */
    private int currentEra;

    /**
     * Track containing the player order for the totem-placement phase.
     */
    private final TurnTicket playerOrder;

    /**
     * Ordered offer spaces used during totem placement and card selection.
     */
    private final BiddingTrail biddingTrail;

    /**
     * Deck containing character and event cards for all eras.
     */
    private final TribeDeck tribeDeck;

    /**
     * Upper card row displayed above the bidding trail.
     */
    private final Row upperRow;

    /**
     * Lower card row displayed below the bidding trail.
     */
    private final Row lowerRow;

    /**
     * Building cards selected for era I.
     */
    private final BuildingDeck buildingDeckEra1;

    /**
     * Building cards selected for era II.
     */
    private final BuildingDeck buildingDeckEra2;

    /**
     * Building cards selected for era III.
     */
    private final BuildingDeck buildingDeckEra3;

    /**
     * Creates an empty board in era I.
     *
     * <p>All tracks, rows, and decks are instantiated but are not populated
     * until {@link #initBoard(List)} is invoked.</p>
     */
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

    /**
     * Initializes every board component for a new game.
     *
     * <p>The method randomizes the initial turn order, builds the offer trail,
     * creates the tribe deck, selects the appropriate number of building cards
     * for each era, and fills the initial lower and upper rows.</p>
     *
     * @param players players taking part in the game
     * @throws IllegalArgumentException if the number of players is outside the
     *         supported range of two to five
     * @throws NullPointerException if {@code players} is {@code null}
     */
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

    /**
     * Returns the tribe deck owned by this board.
     *
     * <p>This accessor exposes the live deck and is primarily used by tests.</p>
     *
     * @return the board's tribe deck
     */
    public TribeDeck getTribeDeck(){
        return tribeDeck;
    }

    //getter

    /**
     * Returns the era currently in play.
     *
     * @return the current era number
     */
    public int getCurrentEra(){
        return currentEra;
    }

    /**
     * Returns the turn-order track.
     *
     * @return the live turn-order track
     */
    public TurnTicket getPlayerOrder() {
        return playerOrder;
    }

    /**
     * Returns the upper card row.
     *
     * @return the live upper row
     */
    public Row getUpperRow(){
        return upperRow;
    }

    /**
     * Returns the lower card row.
     *
     * @return the live lower row
     */
    public Row getLowerRow(){
        return lowerRow;
    }

    /**
     * Returns the active bidding trail.
     *
     * @return the live bidding trail
     */
    public BiddingTrail getBiddingTrail(){
        return biddingTrail;
    }

    /**
     * Returns the building deck associated with an era.
     *
     * @param currentEra era whose building deck is requested
     * @return the building deck for era {@code 1}, {@code 2}, or {@code 3}
     * @throws IllegalArgumentException if no building deck exists for the given era
     */
    public BuildingDeck getBuildingDeck(int currentEra) throws IllegalArgumentException {
        return switch (currentEra) {
            case 1 -> buildingDeckEra1;
            case 2 -> buildingDeckEra2;
            case 3 -> buildingDeckEra3;
            default -> throw new IllegalArgumentException("BuildingDeck of era: " + currentEra + "doesn't exist");
        };
    }

    /**
     * Fills the initial lower row with the required number of character cards.
     *
     * <p>Cards are drawn until the lower row contains {@code numPlayers + 1}
     * tribe cards. Character cards are placed in the lower row, while event
     * cards encountered during this setup are redirected to the upper row by
     * their polymorphic {@link TribeCard#addInRightRow(Row, Row)} implementation.</p>
     *
     * @param numPlayers number of players in the game
     */
    public void setUpLowerRow(int numPlayers){
        while( (lowerRow.getCharacterCardsList().size() + lowerRow.getEventCardsList().size()) < numPlayers+1){
            tribeDeck.getNextCard().addInRightRow(upperRow, lowerRow);
        }
    }

    /**
     * Completes the initial upper row and adds the era-I buildings.
     *
     * <p>The method draws tribe cards until the upper row contains
     * {@code numPlayers + 4} character and event cards, counting any events
     * already redirected there during lower-row setup. It then transfers the
     * selected era-I building deck to the upper row.</p>
     *
     * @param numPlayers number of players in the game
     */
    public void setUpUpperRow(int numPlayers){
        while((upperRow.getCharacterCardsList().size() + upperRow.getEventCardsList().size()) < (numPlayers + 4)){
            tribeDeck.getNextCard().addInRightList(upperRow);
        }
        moveBuildingDeck(buildingDeckEra1, upperRow);
    }

    /**
     * Transfers all building cards from one row to another.
     *
     * <p>The receiver's building list is replaced with a copy of the donor's
     * current building list, after which the donor's building list is cleared.
     * Character and event cards in both rows are left unchanged.</p>
     *
     * @param donor row whose building cards are transferred
     * @param receiver row that receives the building cards
     */
    public void moveBuildingDeck(Row donor, Row receiver){
        receiver.setBuildingCardsList(donor.getBuildingCardsList());
        donor.clearBuildingCards();
    }

    /**
     * Transfers all cards from a building deck to a row.
     *
     * <p>The receiver's building list is replaced with a copy of the deck's
     * cards, and the source deck is then cleared.</p>
     *
     * @param donor building deck whose cards are transferred
     * @param receiver row that receives the building cards
     */
    public void moveBuildingDeck(BuildingDeck donor, Row receiver){
        receiver.setBuildingCardsList(donor);
        donor.clear();
    }

    /**
     * Moves a player from the bidding trail to the next free turn-order slot.
     *
     * <p>After the player is removed from the occupied offer ticket, the first
     * {@code null} slot in the turn-order track is filled. The food bonus or
     * final-slot penalty associated with the resulting position is then
     * applied.</p>
     *
     * @param player player whose card-selection turn has ended
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player is not on the bidding trail
     */
    public void movePlayerToTurnTicket(Player player){
        biddingTrail.removePlayer(player);
        playerOrder.addPlayer(player);
        playerOrder.giveMalusOrBonus(player);
    }

    /**
     * Restores the card rows for the next round.
     *
     * <p>Character and event cards in the lower row are discarded, the
     * remaining character and event cards in the upper row move to the lower
     * row, and up to {@code numPlayers + 4} new tribe cards are drawn into the
     * upper row. Buildings remain in place unless drawing the first card of a
     * later era triggers {@link #startNewEra()}.</p>
     *
     * <p>If the tribe deck becomes empty before all required cards have been
     * drawn, the method returns immediately and leaves the partially restored
     * rows in their current state.</p>
     *
     * @param numPlayers number of players in the game
     * @return {@code true} if all required cards were drawn; {@code false} if
     *         the tribe deck became empty first
     */
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

    /**
     * Advances the board to the next era and updates the displayed buildings.
     *
     * <p>The era counter is incremented, buildings still present in the lower
     * row are discarded, buildings from the upper row move to the lower row,
     * and the selected building deck for the new era is moved to the upper
     * row.</p>
     *
     * @throws IllegalArgumentException if advancing produces an era for which
     *         no building deck exists
     */
    public void startNewEra() {
        currentEra++;
        lowerRow.clearBuildingCards();
        moveBuildingDeck(upperRow, lowerRow);
        moveBuildingDeck(getBuildingDeck(currentEra), upperRow);
    }

    /**
     * Places a player on the bidding ticket at the specified index.
     *
     * @param index zero-based position on the active bidding trail
     * @param player player whose totem is being placed
     * @throws BiddingTicketIsTaken if the selected ticket is already occupied
     * @throws IndexOutOfBoundsException if {@code index} is outside the trail
     */
    public void setPlayer(int index, Player player) throws BiddingTicketIsTaken,IndexOutOfBoundsException{
        biddingTrail.setPlayer(index, player);
    }

    /**
     * Empties every slot of the turn-order track while preserving its size.
     */
    public void removeAllPlayersFromTurnTicket(){
        playerOrder.removePlayerFromTurnTicket();
    }

    /**
     * Removes players from both board tracks.
     *
     * <p>The turn-order slots are set to {@code null}; the bidding trail then
     * delegates to {@link BiddingTrail#removeAllPlayers()}, which clears its
     * internal ticket list.</p>
     */
    public void removeAllPlayers(){
        removeAllPlayersFromTurnTicket();
        biddingTrail.removeAllPlayers();
    }

    /**
     * Returns the player following the supplied player in first-phase order.
     *
     * @param player current player in the turn-order track
     * @return the following player, or an empty optional if the supplied player
     *         occupies the final slot
     * @throws IllegalArgumentException if {@code player} is {@code null}
     */
    public Optional<Player> getNextPlayerFirstPhase(Player player) throws IllegalArgumentException{
        return playerOrder.getNextPlayerFirstPhase(player);
    }

    /**
     * Returns the player on the leftmost occupied bidding ticket.
     *
     * @return the first player in card-selection order
     * @throws IllegalStateException if no bidding ticket is occupied
     */
    public Player getFirstPlayerSecondPhase(){
        return biddingTrail.getFirstPlayerSecondPhase();
    }

    /**
     * Returns the first player still present on the bidding trail.
     *
     * @return the next player in second-phase order, or an empty optional when
     *         all tickets are free
     */
    public Optional<Player> nextPlayerSecondPhase() throws IllegalArgumentException{
        return biddingTrail.nextPlayerSecondPhase();
    }

    /**
     * Returns the player stored at a turn-order position.
     *
     * @param index zero-based turn-order index
     * @return the player at the requested position, possibly {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is outside the track
     */
    public Player getPlayerFromTurnTicket(int index){
        return playerOrder.getTurnPlayer(index);
    }

    /**
     * Returns the player in the first turn-order slot.
     *
     * @return the first player, possibly {@code null} if the slots have been cleared
     * @throws IndexOutOfBoundsException if the turn-order track is empty
     */
    public Player getFirstPlayerFirstPhase(){
        return playerOrder.getFirstPlayerFirstPhase();
    }

    /**
     * Returns the upper-row allowance of the player's occupied bidding ticket.
     *
     * @param player player whose allowance is requested
     * @return number of upper-row cards the player may select
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player is not on the trail
     */
    public int getChooseUpperCard(Player player){
        return biddingTrail.getChooseUpperCard(player);
    }

    /**
     * Returns the lower-row allowance of the player's occupied bidding ticket.
     *
     * @param player player whose allowance is requested
     * @return number of lower-row cards the player may select
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player is not on the trail
     */
    public int getChooseLowerCard(Player player){
        return biddingTrail.getChooseLowerCard(player);
    }

    /**
     * Checks whether a player can legally take at least one card from a row.
     *
     * <p>Character cards are always selectable, event cards are ignored, and a
     * building is selectable only if the player can pay its cost after builder
     * discounts.</p>
     *
     * @param rowType row to inspect
     * @param player player whose food and building discounts are considered
     * @return {@code true} if at least one selectable card exists in the row
     */
    public boolean hasSelectableCard(RowType rowType, Player player) {
        return switch (rowType) {
            case UPPER -> upperRow.hasSelectableCard(player);
            case LOWER -> lowerRow.hasSelectableCard(player);
        };
    }

    /**
     * Searches the upper row for a selectable card identifier.
     *
     * <p>When a character or building is found, {@code cardSearchResult} is
     * populated with the card, its type, its index in the corresponding row
     * list, and {@link RowType#UPPER}.</p>
     *
     * @param id identifier of the card to locate
     * @param cardSearchResult mutable object that receives the search result
     * @throws CannotPickEventCard if {@code id} identifies an event card
     * @throws IllegalArgumentException if no card with the identifier is found
     */
    public void findCardUpperRow(int id, CardSearchResult cardSearchResult) throws IllegalArgumentException{
        if (upperRow.findCard(id,cardSearchResult)) {
            cardSearchResult.setRowType(RowType.UPPER);
            return;
        }
        throw new IllegalArgumentException("Card not found");
    }

    /**
     * Searches both rows for a selectable card identifier.
     *
     * <p>The upper row is searched first, followed by the lower row. A
     * successful search populates {@code cardSearchResult} with the card, its
     * type, its list index, and the row in which it was found.</p>
     *
     * @param id identifier of the card to locate
     * @param cardSearchResult mutable object that receives the search result
     * @throws CannotPickEventCard if {@code id} identifies an event card
     * @throws IllegalArgumentException if no card with the identifier is found
     */
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

    /**
     * Removes the card described by a previously populated search result.
     *
     * <p>The stored row type selects the upper or lower row, while the card
     * type and list index select the concrete card within that row.</p>
     *
     * @param cardSearchResult result describing the card to remove
     */
    public void removeCard(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.UPPER) {
            upperRow.removeCard(cardSearchResult);
        }
        else {
            lowerRow.removeCard(cardSearchResult);
        }
    }

    /**
     * Retrieves a building from the row described by a search result.
     *
     * @param cardSearchResult result containing the row and building-list index
     * @return the building card at the stored index
     * @throws IndexOutOfBoundsException if the stored index is invalid
     */
    public BuildingCard getBuildingCardByIndex(CardSearchResult cardSearchResult){
        if (cardSearchResult.getRowType() == RowType.UPPER) {
            return upperRow.getBuildingCardByIndex(cardSearchResult.getIndexInList());
        } else{
            return lowerRow.getBuildingCardByIndex(cardSearchResult.getIndexInList());
        }
    }

    /**
     * Orders the event cards currently present in the lower row.
     *
     * <p>The lower row's live event list is sorted by event resolution order and
     * then by era, ensuring that two events of the same type resolve from the
     * earlier era first.</p>
     *
     * @return the lower row's sorted event list
     */
    public List<EventCard> orderEvents(){
        return lowerRow.orderEvents();
    }

    /**
     * Collects and orders every event visible at the end of the game.
     *
     * <p>A new list is built from the lower-row and upper-row events and sorted
     * by event resolution order and then by era. The row lists themselves are
     * not reordered by this method.</p>
     *
     * @return a new sorted list containing events from both rows
     */
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

    /**
     * Returns a player's zero-based position on the bidding trail.
     *
     * @param player player whose position is requested
     * @return index of the occupied ticket
     * @throws IllegalArgumentException if {@code player} is {@code null}
     * @throws PlayerNotOnTrail if the player does not occupy a ticket
     */
    public int getPlayerPositionOnTrail(Player player){
        return biddingTrail.getPlayerPositionOnTrail(player);
    }

    //------------------------

    /**
     * Creates a client-facing snapshot of the visible board state.
     *
     * <p>The result contains new view objects for cards, bidding tickets, and
     * players. Empty turn-order slots are preserved as {@code null} entries.</p>
     *
     * @return a new board view representing the current rows and tracks
     */
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
