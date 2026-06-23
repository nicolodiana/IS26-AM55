package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.*;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.CannotPickEventCard;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores the cards currently displayed in one board row.
 *
 * <p>Character cards, event cards, and building cards are maintained in three
 * separate collections. Consequently, this class represents the row's logical
 * contents rather than preserving a single mixed insertion order. It also
 * provides the operations used during setup, round restoration, card lookup,
 * event ordering, affordability checks, and view creation.</p>
 */
public class Row {

    /**
     * Character cards currently displayed in this row.
     */
    private final List<CharacterCard> characterCardsList;

    /**
     * Event cards currently displayed in this row.
     */
    private final List<EventCard> eventCardsList;

    /**
     * Building cards currently displayed in this row.
     */
    private final BuildingDeck buildingCardsList;

    /**
     * Creates an empty row with separate collections for each card category.
     */
    public Row(){
        characterCardsList = new ArrayList<>();
        eventCardsList = new ArrayList<>();
        buildingCardsList = new BuildingDeck();
    }


    /**
     * Replaces this row's building cards with a copy of another deck's cards.
     *
     * <p>The {@link BuildingCard} objects themselves are not cloned; only the
     * containing list is copied. This method is also used as a test helper and
     * by board-level deck transfers.</p>
     *
     * @param deck source deck whose current cards are copied
     * @throws NullPointerException if {@code deck} is {@code null}
     */
    public void setBuildingCardsList(BuildingDeck deck) {
        buildingCardsList.setBuildingCardsList(new ArrayList<>(deck.getBuildingDeck()));
    }

    /**
     * Returns the live list of character cards in this row.
     *
     * @return the internal character-card list
     */
    public List<CharacterCard> getCharacterCardsList() {
        return characterCardsList;
    }

    /**
     * Returns the live list of event cards in this row.
     *
     * @return the internal event-card list
     */
    public List<EventCard> getEventCardsList() {
        return eventCardsList;
    }

    /**
     * Returns the building deck used to store this row's building cards.
     *
     * @return the live building-card container
     */
    public BuildingDeck getBuildingCardsList() {
        return buildingCardsList;
    }

    /**
     * Returns the building card at a position in this row's building list.
     *
     * @param index zero-based index in the building list
     * @return the building at the requested position
     * @throws IndexOutOfBoundsException if {@code index} is invalid
     */
    public BuildingCard getBuildingCardByIndex(int index){
        return buildingCardsList.getBuildingCardByIndex(index);
    }

    /**
     * Appends a character card to this row.
     *
     * @param characterCard character card to add
     */
    public void addCharacterCard(CharacterCard characterCard){
        characterCardsList.add(characterCard);
    }

    /**
     * Appends an event card to this row.
     *
     * @param eventCard event card to add
     */
    public void addEventCard(EventCard eventCard){
        eventCardsList.add(eventCard);
    }

    /**
     * Removes the character card at the supplied list index.
     *
     * @param index zero-based index in the character-card list
     * @throws IllegalArgumentException if {@code index} is negative or not less
     *         than the current character-card count
     */
    public void removeCharacterCardByIndex(int index) throws IllegalArgumentException{
        if(index >= characterCardsList.size() || index < 0){
            throw new IllegalArgumentException("Index out of bounds");
        }
        characterCardsList.remove(index);
    }

    /**
     * Removes the building card at the supplied list index.
     *
     * @param index zero-based index in the building-card list
     * @throws IllegalArgumentException if {@code index} is negative or not less
     *         than the current building-card count
     */
    public void removeBuildingCardByIndex(int index) throws IllegalArgumentException{
        if(index >= buildingCardsList.getBuildingDeck().size() || index < 0){
            throw new IllegalArgumentException("Index out of bounds");
        }
        buildingCardsList.removeBuildingCardByIndex(index);
    }

    /**
     * Replaces this row's tribe cards with those from another row.
     *
     * <p>This row's character and event lists are cleared, then populated with
     * all character and event cards from {@code donor}. The donor's corresponding
     * lists are cleared afterwards. Building cards in both rows are left
     * unchanged.</p>
     *
     * @param donor row whose character and event cards are moved
     */
    public void swapTribeRow(Row donor){
        this.characterCardsList.clear();
        this.eventCardsList.clear();
        this.characterCardsList.addAll(donor.characterCardsList);
        this.eventCardsList.addAll(donor.eventCardsList);
        donor.characterCardsList.clear();
        donor.eventCardsList.clear();
    }

    /**
     * Discards every character and event card in this row.
     *
     * <p>Building cards are intentionally preserved.</p>
     */
    public void clearRoundEnd(){
        characterCardsList.clear();
        eventCardsList.clear();
    }

    /**
     * Removes every building card from this row.
     *
     * <p>Character and event cards are left unchanged.</p>
     */
    public void clearBuildingCards(){
        buildingCardsList.clear();
    }

    /**
     * Searches this row for a card that a player is allowed to pick.
     *
     * <p>Character cards are searched first, followed by buildings. When one is
     * found, {@code cardSearchResult} receives the card reference, its
     * {@link CardType}, and its index in the corresponding category list. The
     * row type is not set here because it is supplied by {@link Board}. Event
     * cards are checked last and cause an exception because the rules forbid
     * taking them.</p>
     *
     * @param id identifier of the card to find
     * @param cardSearchResult mutable object that receives the card, type, and index
     * @return {@code true} if a character or building with {@code id} is found;
     *         {@code false} if the identifier is absent
     * @throws CannotPickEventCard if {@code id} belongs to an event card in this row
     */
    public boolean findCard(int id, CardSearchResult cardSearchResult) throws CannotPickEventCard {
        int i = 0;
        for(CharacterCard characterCard: characterCardsList){
            if (characterCard.getId() == id){
                cardSearchResult.setCard(characterCard);
                cardSearchResult.setCardType(CardType.CHARACTER);
                cardSearchResult.setIndexInList(i);
                return true;
            }
            i++;
        }
        i = 0;
        for(BuildingCard buildingCard: buildingCardsList.getBuildingDeck()){
            if (buildingCard.getId() == id){
                cardSearchResult.setCard(buildingCard);
                cardSearchResult.setCardType(CardType.BUILDING);
                cardSearchResult.setIndexInList(i);
                return true;
            }
            i++;
        }
        for(EventCard eventCard: eventCardsList){
            if (eventCard.getId() == id){
                throw new CannotPickEventCard("You can't pick a card from the EventList");
            }
        }
        return false;
    }

    /**
     * Removes the card identified by a populated search result.
     *
     * <p>Character and building results are removed from their respective lists
     * using the stored index. No other card type is handled.</p>
     *
     * @param cardSearchResult result containing the card type and category-list index
     * @throws IllegalArgumentException if the stored index is invalid for the
     *         selected category
     */
    public void removeCard(CardSearchResult cardSearchResult){
        if(cardSearchResult.getCardType() == CardType.CHARACTER){
            removeCharacterCardByIndex(cardSearchResult.getIndexInList());
        } else if(cardSearchResult.getCardType() == CardType.BUILDING){
            removeBuildingCardByIndex(cardSearchResult.getIndexInList());
        }
    }

    /**
     * Sorts this row's event cards in their resolution order.
     *
     * <p>Events are ordered first by {@link EventCard#getOrder()} and then by
     * era, so duplicate event types from different eras resolve from the older
     * era to the newer one. The internal list is sorted in place and returned.</p>
     *
     * @return the live, sorted event-card list
     */
    public List<EventCard> orderEvents(){
        this.getEventCardsList().sort(
                Comparator
                        .comparingInt(EventCard::getOrder)
                        .thenComparingInt(EventCard::getEra)
        );
        return this.getEventCardsList();
    }

    /**
     * Converts every card in this row to its client-facing representation.
     *
     * <p>The returned list contains character views first, building views
     * second, and event views last, reflecting the three internal collections.</p>
     *
     * @return a new list of card views for the current row contents
     */
    public List<CardView> createCardView() {
        List<CardView> listOfViews = new ArrayList<>();
        for (Card c : characterCardsList) {
            listOfViews.add(c.toView());
        }

        for (Card c : buildingCardsList.getBuildingDeck()) {
            listOfViews.add(c.toView());
        }

        for (Card c : eventCardsList) {
            listOfViews.add(c.toView());
        }

        return listOfViews;
    }

    /**
     * Checks whether the supplied player can legally take at least one card
     * from this row.
     *
     * <p>Any character card makes the row selectable because characters have no
     * acquisition cost. Otherwise, the method looks for a building whose food
     * cost, reduced by the player's total builder discount and never below
     * zero, does not exceed the player's available food. Event cards are ignored
     * because they cannot be picked.</p>
     *
     * @param player player whose food and builder discount are considered
     * @return {@code true} if at least one character or affordable building is
     *         available; {@code false} otherwise
     */
    public boolean hasSelectableCard(Player player) {
        if (!characterCardsList.isEmpty()) {
            return true;
        }

        int buildingDiscount = player.totalBuildingDiscount();

        return buildingCardsList.getBuildingDeck()
                .stream()
                .anyMatch(buildingCard -> {
                    int effectiveCost = Math.max(
                            0,
                            buildingCard.getFoodCost() - buildingDiscount
                    );

                    return effectiveCost <= player.getNumFoods();
                });
    }
}
