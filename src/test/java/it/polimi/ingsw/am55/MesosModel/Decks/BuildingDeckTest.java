package it.polimi.ingsw.am55.MesosModel.Decks;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingDeckTest {

    //test if the deck of Era 1 has the right amount of card
    @Test
    void initBuildingDeckEra1Test() {
        BuildingDeck deck = new BuildingDeck();
        deck.initBuildingDeckEra1(2);
        assertEquals(1, deck.buildingCardsList.size());
        deck.initBuildingDeckEra1(3);
        assertEquals(2, deck.buildingCardsList.size());
        deck.initBuildingDeckEra1(4);
        assertEquals(2, deck.buildingCardsList.size());
        deck.initBuildingDeckEra1(5);
        assertEquals(2, deck.buildingCardsList.size());
    }

    //check if the deck of Era 2 has the right amount of card
    @Test
    void initBuildingDeckEra2Test() {
        BuildingDeck deck = new BuildingDeck();
        deck.initBuildingDeckEra2(2);
        assertEquals(2, deck.buildingCardsList.size());
        deck.initBuildingDeckEra2(3);
        assertEquals(2, deck.buildingCardsList.size());
        deck.initBuildingDeckEra2(4);
        assertEquals(3, deck.buildingCardsList.size());
        deck.initBuildingDeckEra2(5);
        assertEquals(3, deck.buildingCardsList.size());
    }

    //test if the deck of Era 3 has the right amount of card
    @Test
    void initBuildingDeckEra3Test() {
        BuildingDeck deck = new BuildingDeck();
        deck.initBuildingDeckEra3(2);
        assertEquals(3, deck.buildingCardsList.size());
        deck.initBuildingDeckEra3(3);
        assertEquals(4, deck.buildingCardsList.size());
        deck.initBuildingDeckEra3(4);
        assertEquals(4, deck.buildingCardsList.size());
        deck.initBuildingDeckEra3(5);
        assertEquals(5, deck.buildingCardsList.size());
    }

    //test if the metod clear actually leave the deck Empty
    @Test
    void clearTest(){
        BuildingDeck deck = new BuildingDeck();
        deck.initBuildingDeckEra1(2);
        deck.clear();
        assertEquals(0, deck.buildingCardsList.size());
    }

    //test if the donor deck after the method is empty and
    //test if the receiver deck is equal to the starting donor
//    @Test
//    void swapBuildingDeckTest() {
//        BuildingDeck donor = new BuildingDeck();
//        donor.initBuildingDeckEra1(2);
//        BuildingDeck receiver = new BuildingDeck();
//        receiver.initBuildingDeckEra2(4);
//        BuildingDeck fakeDonor = new BuildingDeck();
//        fakeDonor.buildingCardsList.addAll(donor.buildingCardsList);
//
//        receiver.swapBuildingDeck(donor, receiver);
//        assertEquals(0, donor.buildingCardsList.size());
//        assertEquals(receiver.buildingCardsList, fakeDonor.buildingCardsList);
//    }

    //test that only the right card is removed from the deck
    @Test
    void removeBuildingCardTest() {
        BuildingDeck deck = new BuildingDeck();
        BuildingCard card1 = new BuildingCard(113, 3, 8, 8, BuildingType.BUILDING12, CharacterType.HUNTER, 3 );
        BuildingCard card2 = new BuildingCard(114, 3, 7, 6, BuildingType.BUILDING12, CharacterType.COLLECTOR, 4);
        BuildingCard card3 = new BuildingCard(115, 3, 7, 4, BuildingType.BUILDING12, CharacterType.SHAMAN, 4);
        deck.buildingCardsList.add(card1);
        deck.buildingCardsList.add(card2);
        deck.buildingCardsList.add(card3);

        assertEquals(3, deck.buildingCardsList.size());
        assertEquals(card1, deck.buildingCardsList.get(0));
        assertEquals(card2, deck.buildingCardsList.get(1));
        assertEquals(card3, deck.buildingCardsList.get(2));

        deck.removeBuildingCard(card2);

        assertEquals(2, deck.buildingCardsList.size());
        assertEquals(card1, deck.buildingCardsList.get(0));
        assertEquals(card3, deck.buildingCardsList.get(1));
    }

    @Test
    void removeBuildingCardsByIndexTest() {
        BuildingDeck deck = new BuildingDeck();
        BuildingCard card1 = new BuildingCard(113, 3, 8, 8, BuildingType.BUILDING12, CharacterType.HUNTER, 3 );
        BuildingCard card2 = new BuildingCard(114, 3, 7, 6, BuildingType.BUILDING12, CharacterType.COLLECTOR, 4);
        BuildingCard card3 = new BuildingCard(115, 3, 7, 4, BuildingType.BUILDING12, CharacterType.SHAMAN, 4);
        deck.buildingCardsList.add(card1);
        deck.buildingCardsList.add(card2);
        deck.buildingCardsList.add(card3);

        assertEquals(3, deck.buildingCardsList.size());
        assertEquals(card1, deck.buildingCardsList.get(0));
        assertEquals(card2, deck.buildingCardsList.get(1));
        assertEquals(card3, deck.buildingCardsList.get(2));

        deck.removeBuildingCardByIndex(1);
        assertEquals(2, deck.buildingCardsList.size());
        assertEquals(card1, deck.buildingCardsList.get(0));
        assertEquals(card3, deck.buildingCardsList.get(1));
    }

    @Test
    void getBuildingCardsByIndexTest() {
        BuildingDeck deck = new BuildingDeck();
        BuildingCard card1 = new BuildingCard(113, 3, 8, 8, BuildingType.BUILDING12, CharacterType.HUNTER, 3 );
        BuildingCard card2 = new BuildingCard(114, 3, 7, 6, BuildingType.BUILDING12, CharacterType.COLLECTOR, 4);
        BuildingCard card3 = new BuildingCard(115, 3, 7, 4, BuildingType.BUILDING12, CharacterType.SHAMAN, 4);
        BuildingCard test;
        deck.buildingCardsList.add(card1);
        deck.buildingCardsList.add(card2);
        deck.buildingCardsList.add(card3);

        assertEquals(card1, deck.buildingCardsList.get(0));
        assertEquals(card2, deck.buildingCardsList.get(1));
        assertEquals(card3, deck.buildingCardsList.get(2));

        test = deck.getBuildingCardByIndex(1);
        assertEquals(card2, test);
    }

    @Test
    void getBuildingDeckTest() {
        BuildingDeck deck = new BuildingDeck();
        BuildingCard card1 = new BuildingCard(113, 3, 8, 8, BuildingType.BUILDING12, CharacterType.HUNTER, 3 );
        BuildingCard card2 = new BuildingCard(114, 3, 7, 6, BuildingType.BUILDING12, CharacterType.COLLECTOR, 4);
        BuildingCard card3 = new BuildingCard(115, 3, 7, 4, BuildingType.BUILDING12, CharacterType.SHAMAN, 4);

        BuildingDeck test = new BuildingDeck();
        assertEquals(0, test.buildingCardsList.size());

        test.buildingCardsList = deck.getBuildingDeck();
        assertEquals(test.buildingCardsList, deck.buildingCardsList);
    }
}
