package it.polimi.ingsw.am55.MesosModel.Decks;

import it.polimi.ingsw.am55.MesosModel.Cards.TribeCard;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

public class TribeDeckTest {

    @Test
    void initTribeDeckTest() {
        TribeDeck deck2 = new TribeDeck();
        deck2.initTribeDeck(2);
        TribeDeck deck3 = new TribeDeck();
        deck3.initTribeDeck(3);
        TribeDeck deck4 = new TribeDeck();
        deck4.initTribeDeck(4);
        TribeDeck deck5 = new TribeDeck();
        deck5.initTribeDeck(5);

        assert(deck2.tribeCardStack.size() < deck3.tribeCardStack.size());
        assert(deck3.tribeCardStack.size() < deck4.tribeCardStack.size());
        assert(deck4.tribeCardStack.size() < deck5.tribeCardStack.size());
    }

    @Test
    void getNextCardTest() {
        TribeDeck deck = new TribeDeck();
        deck.initTribeDeck(2);

        TribeCard card = deck.tribeCardStack.peek();
        TribeCard card2 = deck.getNextCard();
        TribeCard card3 = deck.tribeCardStack.peek();
        TribeCard card4 = deck.getNextCard();

        assertEquals(card, card2);
        assertNotEquals(card, card3);
        assertNotEquals(card2, card4);
        assertEquals(card3, card4);
    }

    @Test
    void isEmptyTest(){
        TribeDeck deck = new TribeDeck();

        assertTrue(deck.isEmpty());

        deck.initTribeDeck(2);
        assertFalse(deck.isEmpty());
    }
}
