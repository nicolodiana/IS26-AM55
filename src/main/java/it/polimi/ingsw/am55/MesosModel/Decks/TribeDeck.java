package it.polimi.ingsw.am55.MesosModel.Decks;

import it.polimi.ingsw.am55.MesosModel.Cards.TribeCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * DESCRIPTION: Represents the deck of tribe cards used during the game.
 * This class is responsible for creating, initializing, and managing the full
 * set of {@link TribeCard} instances according to the number of players.
 * The deck is composed of cards from Era 1, Era 2, Era 3, and a small set of
 * final event cards. Each era is generated separately and shuffled before being
 * added to the main stack.
 * Once initialized, cards can be drawn from the top of the deck through
 * {@link #getNextCard()}.
 */
public class TribeDeck {
    private Stack<TribeCard> tribeCardStack;
    /**
     * Creates an empty tribe deck.
     * The deck must be initialized later through {@link #initTribeDeck(int)}
     * before cards can be drawn.
     */
    public TribeDeck() {
        tribeCardStack = new Stack<>();
    }
    /**
     * Initializes the tribe deck according to the number of players.
     * The method fills the deck by adding:
     *     final event cards,
     *     Era 3 cards,
     *     Era 2 cards,
     *     Era 1 cards.
     * Each group of cards is shuffled before being added to the stack.
     * @param numPlayer the number of players in the game
     */
    public void initTribeDeck(int numPlayer){

        tribeCardStack.addAll(createFinalEventCards(numPlayer));
        tribeCardStack.addAll(createAllCardsEra3(numPlayer));
        tribeCardStack.addAll(createAllCardsEra2(numPlayer));
        tribeCardStack.addAll(createAllCardsEra1(numPlayer));
    }
    /**
     * Creates all Era 1 cards for the given number of players.
     * The base set for two players is always included, and additional cards
     * are added for games with more players.
     * @param numPlayer the number of players
     * @return a shuffled list containing all Era 1 cards
     */
    private List<TribeCard> createAllCardsEra1(int numPlayer) {
        List<TribeCard> allCardsEra1 = new ArrayList<>();
        switch (numPlayer) {
            case 2:
                createCardTwoPlayersEra1(allCardsEra1);
                break;
            case 3:
                createCardTwoPlayersEra1(allCardsEra1);
                allCardsEra1.add(new Hunter(22,false,1));
                allCardsEra1.add(new Collector(23,1));
                allCardsEra1.add(new Hunter(24,false,1));
                allCardsEra1.add(new Artist(25,1));
                break;
            case 4:
                createCardTwoPlayersEra1(allCardsEra1);
                allCardsEra1.add(new Hunter(22,false,1));
                allCardsEra1.add(new Collector(23,1));
                allCardsEra1.add(new Hunter(24,false,1));
                allCardsEra1.add(new Artist(25,1));
                allCardsEra1.add(new Collector(26,1));
                allCardsEra1.add(new Shaman(27,1,1));
                allCardsEra1.add(new Inventor("Mortar and Pestle",28,1));
                allCardsEra1.add(new Inventor("Flute",29,1));
                allCardsEra1.add(new Inventor("Necklace",30,1));

                break;
            case 5:
                createCardTwoPlayersEra1(allCardsEra1);
                allCardsEra1.add(new Hunter(22,false,1));
                allCardsEra1.add(new Collector(23,1));
                allCardsEra1.add(new Hunter(24,false,1));
                allCardsEra1.add(new Artist(25,1));
                allCardsEra1.add(new Collector(26,1));
                allCardsEra1.add(new Shaman(27,1,1));
                allCardsEra1.add(new Inventor("Mortar and Pestle",28,1));
                allCardsEra1.add(new Inventor("Flute",29,1));
                allCardsEra1.add(new Inventor("Necklace",30,1));
                allCardsEra1.add(new Collector(31,1));
                allCardsEra1.add(new Shaman(32,2,1));
                allCardsEra1.add(new Builder(33,1,2,1));
                break;
        }
        Collections.shuffle(allCardsEra1);
        return allCardsEra1;
    }
    /**
     * Creates all Era 2 cards for the given number of players.
     * The base set for two players is always included, and extra cards are
     * added depending on the number of players.
     * @param numPlayer the number of players
     * @return a shuffled list containing all Era 2 cards
     */
    private List<TribeCard> createAllCardsEra2(int numPlayer) {
        List<TribeCard> allCardsEra2 = new ArrayList<>();
        switch (numPlayer) {
            case 2:
                createCardsTwoPlayersEra2(allCardsEra2);
                break;
            case 3:
                createCardsTwoPlayersEra2(allCardsEra2);
                allCardsEra2.add(new Hunter(55,true,2));
                allCardsEra2.add(new Collector(56,2));
                allCardsEra2.add(new Builder(57,2,1,2));
                allCardsEra2.add(new Artist(58,2));
                break;
            case 4:
                createCardsTwoPlayersEra2(allCardsEra2);
                allCardsEra2.add(new Hunter(55,true,2));
                allCardsEra2.add(new Collector(56,2));
                allCardsEra2.add(new Builder(57,2,1,2));
                allCardsEra2.add(new Artist(58,2));
                allCardsEra2.add(new Hunter(59,true,2));
                allCardsEra2.add(new Collector(60,2));
                allCardsEra2.add(new Inventor("Fish Hook",61,2));
                break;
            case 5:
                createCardsTwoPlayersEra2(allCardsEra2);
                allCardsEra2.add(new Hunter(55,true,2));
                allCardsEra2.add(new Collector(56,2));
                allCardsEra2.add(new Builder(57,2,1,2));
                allCardsEra2.add(new Artist(58,2));
                allCardsEra2.add(new Hunter(59,true,2));
                allCardsEra2.add(new Collector(60,2));
                allCardsEra2.add(new Inventor("Fish Hook",61,2));
                allCardsEra2.add(new Hunter(62,false,2));
                allCardsEra2.add(new Collector(63,2));
                allCardsEra2.add(new Shaman(64,1,2));
                break;
        }
        Collections.shuffle(allCardsEra2);
        return allCardsEra2;
    }
    /**
     * Creates all Era 3 cards for the given number of players.
     * The base set for two players is always included, and extra cards are
     * added depending on the number of players.
     * @param numPlayer the number of players
     * @return a shuffled list containing all Era 3 cards
     */
    private List<TribeCard> createAllCardsEra3(int numPlayer) {
        List<TribeCard> allCardsEra3 = new ArrayList<>();
        switch (numPlayer) {
            case 2:
                createCardTwoPlayersEra3(allCardsEra3);
                break;
            case 3:
                createCardTwoPlayersEra3(allCardsEra3);
                allCardsEra3.add(new Shaman(84,2,3));
                allCardsEra3.add(new Inventor("Boat",85,3));
                allCardsEra3.add(new Inventor("Arrowhead",86,3));
                break;
            case 4:
                createCardTwoPlayersEra3(allCardsEra3);
                allCardsEra3.add(new Shaman(84,2,3));
                allCardsEra3.add(new Inventor("Boat",85,3));
                allCardsEra3.add(new Inventor("Arrowhead",86,3));
                allCardsEra3.add(new Shaman(87,2,3));
                allCardsEra3.add(new Inventor("Necklace",88,3));
                allCardsEra3.add(new Collector(89,3));
                break;
            case 5:
                createCardTwoPlayersEra3(allCardsEra3);
                allCardsEra3.add(new Shaman(84,2,3));
                allCardsEra3.add(new Inventor("Boat",85,3));
                allCardsEra3.add(new Inventor("Arrowhead",86,3));
                allCardsEra3.add(new Shaman(87,2,3));
                allCardsEra3.add(new Inventor("Necklace",88,3));
                allCardsEra3.add(new Collector(89,3));
                allCardsEra3.add(new Builder(90,4,1,3));
                allCardsEra3.add(new Artist(91,3));
                allCardsEra3.add(new Hunter(92,true,3));
                allCardsEra3.add(new Shaman(93,2,3));
                allCardsEra3.add(new Collector(94,3));
                break;
        }
        Collections.shuffle(allCardsEra3);
        return allCardsEra3;
    }
    /**
     * Adds the complete Era 1 base card set for a two-player game to the given list.
     *
     * @param allCards the list that will receive the Era 1 cards
     */
    private void createCardTwoPlayersEra1(List<TribeCard> allCards) {
        allCards.add(new Hunter(1,true, 1));
        allCards.add(new Hunter(2,true, 1));
        allCards.add(new Hunter(3,false, 1));
        allCards.add(new Shaman(4,1, 1));
        allCards.add(new Shaman(5,2, 1));
        allCards.add(new Artist(6,1));
        allCards.add(new Artist(7,1));
        allCards.add(new Artist(8,1));
        allCards.add(new Builder(9,3, 1, 1));
        allCards.add(new Builder(10,0, 2, 1));
        allCards.add(new Builder(11,2, 1, 1));
        allCards.add(new Collector(12,1));
        allCards.add(new Collector(13,1));
        allCards.add(new Inventor("Boat", 14,1));
        allCards.add(new Inventor("Arrowhead",15, 1));
        allCards.add(new Inventor("Bread",16, 1));
        allCards.add(new Inventor("Hide",17, 1));
        allCards.add(new ShamanRitualEventCard(18,1,5, 3));
        allCards.add(new SustenanceEventCard(19, 1,1));
        allCards.add(new PaintingsEventCard(20,1,2,1,1,0,1));
        allCards.add(new HuntEventCard(21,1, 1));
    }
    /**
     * Adds the complete Era 2 base card set for a two-player game to the given list.
     *
     * @param allCards the list that will receive the Era 1 cards
     */
    private void createCardsTwoPlayersEra2(List<TribeCard> allCards) {
        allCards.add(new Hunter(34,true, 2));
        allCards.add(new Hunter(35,false, 2));
        allCards.add(new Hunter(36,false, 2));
        allCards.add(new Shaman(37,2, 2));
        allCards.add(new Shaman(38,2, 2));
        allCards.add(new Builder(39,3, 2, 2));
        allCards.add(new Builder(40,1, 2, 2));
        allCards.add(new Builder(41,4, 1, 2));
        allCards.add(new Artist(42,2));
        allCards.add(new Artist(43,2));
        allCards.add(new Artist(44,2));
        allCards.add(new Collector(45,2));
        allCards.add(new Inventor("Hide",46, 2));
        allCards.add(new Inventor("Rope", 47,2));
        allCards.add(new Inventor("Mortar and Pestle", 48,2));
        allCards.add(new Inventor("Flute", 49,2));
        allCards.add(new Inventor("Figurine", 50,2));
        allCards.add(new ShamanRitualEventCard(51,10, 5, 2));
        allCards.add(new HuntEventCard(52,2, 2));
        allCards.add(new SustenanceEventCard(53,2, 2));
        allCards.add(new PaintingsEventCard(54,2,2,2,2,1,0));
    }
    /**
     * Adds the complete Era 3 base card set for a two-player game to the given list.
     *
     * @param allCards the list that will receive the Era 1 cards
     */
    private void createCardTwoPlayersEra3(List<TribeCard> allCards){
        allCards.add(new Inventor("Bread", 65,3));
        allCards.add(new Inventor("Fish Hook",66, 3));
        allCards.add(new Inventor("Rope", 67,3));
        allCards.add(new Inventor("Necklace", 68,3));
        allCards.add(new Artist(69,3));
        allCards.add(new Artist(70,3));
        allCards.add(new Artist(71,3));
        allCards.add(new Hunter(72,true, 3));
        allCards.add(new Hunter(73,false, 3));
        allCards.add(new Hunter(74,false, 3));
        allCards.add(new Builder(75,5, 1, 3));
        allCards.add(new Builder(76,3, 2, 3));
        allCards.add(new Builder(77,2, 2, 3));
        allCards.add(new Shaman(78,3, 3));
        allCards.add(new Shaman(79,2, 3));
        allCards.add(new Shaman(80,3, 3));
        allCards.add(new Collector(81,3));
        allCards.add(new PaintingsEventCard(82,3,3,2,3,2,0));
        allCards.add(new HuntEventCard(83,3, 3));
    }
    /**
     * Creates the final event cards that are placed in the deck.
     * These cards represent the final events of the game and are shuffled
     * before being returned.
     * @param numPlayer the number of players in the game
     * @return a shuffled list containing the final event cards
     */
    private List<TribeCard> createFinalEventCards(int numPlayer) {
        List<TribeCard> finalEventCards = new ArrayList<>();
        finalEventCards.add(new SustenanceEventCard(95,3,3));
        finalEventCards.add(new ShamanRitualEventCard(96,3,15,7));
        Collections.shuffle(finalEventCards);
        return finalEventCards;
    }
    /**
     * Draws and returns the next card from the top of the deck.
     * @return the next {@link TribeCard} in the deck
     */
    public TribeCard getNextCard() {
        return tribeCardStack.pop();
    }

    public boolean isEmpty() {
        return tribeCardStack.isEmpty();
    }
}
