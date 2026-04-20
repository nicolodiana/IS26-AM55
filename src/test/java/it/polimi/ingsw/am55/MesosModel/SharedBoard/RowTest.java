package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Effect.HuntEventCard;
import it.polimi.ingsw.am55.MesosModel.Effect.PaintingsEventCard;
import it.polimi.ingsw.am55.MesosModel.Effect.ShamanRitualEventCard;
import it.polimi.ingsw.am55.MesosModel.Effect.SustenanceEventCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.CannotPickEventCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RowTest {

    @Test
    void addCharacterCardTest() {
        Row row = new Row();
        CharacterCard characterCard = new CharacterCard(1, 1);

        assertEquals(0, row.getCharacterCardsList().size());
        row.addCharacterCard(characterCard);
        CharacterCard tmp = row.getCharacterCardsList().getFirst();
        assertEquals(characterCard, tmp);
        assertEquals(1, row.getCharacterCardsList().size());
    }

    @Test
    void addEventCardTest() {
        Row row = new Row();
        EventCard eventCard = new EventCard(1, 1);

        assertEquals(0, row.getEventCardsList().size());
        row.addEventCard(eventCard);
        EventCard tmp = row.getEventCardsList().getFirst();
        assertEquals(eventCard, tmp);
        assertEquals(1, row.getEventCardsList().size());
    }

    @Test
    void removeCharacterCardByIndexTest() {
        Row row = new Row();
        CharacterCard characterCard = new CharacterCard(1, 1);
        CharacterCard characterCard2 = new CharacterCard(2, 2);
        CharacterCard characterCard3 = new CharacterCard(3, 3);

        assertThrows(IllegalArgumentException.class, () -> row.removeCharacterCardByIndex(0));

        row.addCharacterCard(characterCard);
        row.addCharacterCard(characterCard2);
        row.addCharacterCard(characterCard3);

        assertThrows(IllegalArgumentException.class, () -> row.removeCharacterCardByIndex(3));
        assertThrows(IllegalArgumentException.class, () -> row.removeCharacterCardByIndex(-1));

        row.removeCharacterCardByIndex(1);
        assertEquals(2, row.getCharacterCardsList().size());
    }

    @Test
    void removeBuildingCardByIndexTest() {
        Row row = new Row();
        BuildingDeck buildingDeck = new BuildingDeck();
        buildingDeck.initBuildingDeckEra3(3);

        assertThrows(IllegalArgumentException.class, () -> row.removeBuildingCardByIndex(0));

        row.setBuildingCardsList(buildingDeck);

        assertThrows(IllegalArgumentException.class, () -> row.removeBuildingCardByIndex(2000));
        assertThrows(IllegalArgumentException.class, () -> row.removeBuildingCardByIndex(-1));

        row.removeBuildingCardByIndex(1);
        assertEquals(3, row.getBuildingCardsList().getBuildingDeck().size());
    }

    @Test
    void swapTribeRowTest() {
        Row donor = new Row();
        Row receiver = new Row();
        CharacterCard characterCard = new CharacterCard(1, 1);
        CharacterCard characterCard2 = new CharacterCard(2, 2);
        CharacterCard characterCard3 = new CharacterCard(3, 3);
        EventCard eventCard = new EventCard(1, 1);
        EventCard eventCard2 = new EventCard(2, 2);
        EventCard eventCard3 = new EventCard(3, 3);
        donor.addEventCard(eventCard);
        donor.addEventCard(eventCard2);
        donor.addCharacterCard(characterCard);
        donor.addCharacterCard(characterCard2);
        receiver.addEventCard(eventCard3);
        receiver.addCharacterCard(characterCard3);

        Row tmp = new Row();
        tmp.addCharacterCard(characterCard);
        tmp.addCharacterCard(characterCard2);
        tmp.addEventCard(eventCard);
        tmp.addEventCard(eventCard2);

        receiver.swapTribeRow(donor, receiver);

        assertEquals(tmp.getCharacterCardsList(), receiver.getCharacterCardsList());
        assertEquals(tmp.getEventCardsList(), receiver.getEventCardsList());
        assertEquals(0, donor.getCharacterCardsList().size());
        assertEquals(0, donor.getEventCardsList().size());
    }

    @Test
    void clearRoundEndTest() {
        Row row = new Row();
        CharacterCard characterCard = new CharacterCard(1, 1);
        CharacterCard characterCard2 = new CharacterCard(2, 2);
        EventCard eventCard = new EventCard(1, 1);
        EventCard eventCard2 = new EventCard(2, 2);
        row.addEventCard(eventCard);
        row.addEventCard(eventCard2);
        row.addCharacterCard(characterCard);
        row.addCharacterCard(characterCard2);

        row.clearRoundEnd();
        assertEquals(0, row.getCharacterCardsList().size());
        assertEquals(0, row.getEventCardsList().size());
    }

    @Test
    void clearBuildingCardsTest() {
        Row row = new Row();
        BuildingDeck buildingDeck = new BuildingDeck();
        buildingDeck.initBuildingDeckEra3(3);
        row.setBuildingCardsList(buildingDeck);

        row.clearBuildingCards();
        assertEquals(0, row.getCharacterCardsList().size());
    }

    @Test
    void findCardTest() {
        Row row = new Row();
        CharacterCard characterCard = new CharacterCard(1, 1);
        CharacterCard characterCard2 = new CharacterCard(2, 2);
        EventCard eventCard = new EventCard(3, 1);
        EventCard eventCard2 = new EventCard(4, 2);
        row.addEventCard(eventCard);
        row.addEventCard(eventCard2);
        row.addCharacterCard(characterCard);
        row.addCharacterCard(characterCard2);
        List<BuildingCard> buildingCardList;
        buildingCardList = new ArrayList<BuildingCard>();
        BuildingCard buildingCard = new BuildingCard(5,1,1,1,BuildingType.BUILDING1,CharacterType.BUILDER, 1);
        BuildingCard buildingCard2 = new BuildingCard(6,1,1,1,BuildingType.BUILDING1,CharacterType.BUILDER, 1);
        buildingCardList.add(buildingCard);
        buildingCardList.add(buildingCard2);
        row.getBuildingCardsList().setBuildingCardsList(buildingCardList);

        CardSearchResult cardSearchResult = new CardSearchResult();
        assertTrue(row.findCard(1, cardSearchResult));
        assertEquals(cardSearchResult.getCard(), characterCard);
        assertEquals(CardType.CHARACTER, cardSearchResult.getCardType());
        assertEquals(0, cardSearchResult.getIndexInList());

        assertTrue(row.findCard(6, cardSearchResult));
        assertEquals(cardSearchResult.getCard(), buildingCard2);
        assertEquals(CardType.BUILDING, cardSearchResult.getCardType());
        assertEquals(1, cardSearchResult.getIndexInList());

        assertThrows(CannotPickEventCard.class, () -> row.findCard(4, cardSearchResult));

        assertFalse(row.findCard(10, cardSearchResult));
        assertFalse(row.findCard(-10, cardSearchResult));
    }

    @Test
    void removeCardTest() {
        Row row = new Row();
        CharacterCard characterCard = new CharacterCard(1, 1);
        CharacterCard characterCard2 = new CharacterCard(2, 2);
        row.addCharacterCard(characterCard);
        row.addCharacterCard(characterCard2);

        List<BuildingCard> buildingCardList;
        buildingCardList = new ArrayList<BuildingCard>();
        BuildingCard buildingCard = new BuildingCard(5,1,1,1,BuildingType.BUILDING1,CharacterType.BUILDER, 1);
        BuildingCard buildingCard2 = new BuildingCard(6,1,1,1,BuildingType.BUILDING1,CharacterType.BUILDER, 1);
        buildingCardList.add(buildingCard);
        buildingCardList.add(buildingCard2);
        row.getBuildingCardsList().setBuildingCardsList(buildingCardList);

        CardSearchResult cardSearchResult = new CardSearchResult();
        cardSearchResult.setCard(characterCard);
        cardSearchResult.setCardType(CardType.CHARACTER);
        cardSearchResult.setIndexInList(0);

        row.removeCard(cardSearchResult);

        assertEquals(1, row.getCharacterCardsList().size());

        cardSearchResult.setCard(buildingCard2);
        cardSearchResult.setCardType(CardType.BUILDING);
        cardSearchResult.setIndexInList(1);

        row.removeCard(cardSearchResult);

        assertEquals(1, row.getBuildingCardsList().getBuildingDeck().size());
    }

    @Test
    void orderEventsTest() {
        Row row = new Row();
        List<Integer> executionOrder = new ArrayList<>();
        HuntEventCard huntEventCard = new HuntEventCard(1,1,1);
        HuntEventCard huntEventCard2 = new HuntEventCard(2,3,1);
        PaintingsEventCard paintingsEventCard = new PaintingsEventCard(3, 1, 1,1,1,1);
        PaintingsEventCard paintingsEventCard2 = new PaintingsEventCard(4, 3, 1,1,1,1);
        ShamanRitualEventCard ShamanEventCard = new ShamanRitualEventCard(5, 2,1,1);
        ShamanRitualEventCard ShamanEventCard2 = new ShamanRitualEventCard(6, 2,1,2);
        SustenanceEventCard sustenance = new SustenanceEventCard(7, 2,1);
        SustenanceEventCard sustenance2 = new SustenanceEventCard(8, 2,2);
        row.addEventCard(paintingsEventCard2);
        row.addEventCard(sustenance);
        row.addEventCard(ShamanEventCard);
        row.addEventCard(huntEventCard2);
        row.addEventCard(ShamanEventCard2);
        row.addEventCard(sustenance2);
        row.addEventCard(paintingsEventCard);
        row.addEventCard(huntEventCard);

        List<EventCard> events = row.orderEvents();
        int size = (row.getEventCardsList().size());
        assertEquals(size, events.size());

        Comparator<EventCard> comparator =
                Comparator.comparingInt(EventCard::getOrder)
                        .thenComparingInt(EventCard::getEra);

        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(comparator.compare(events.get(i), events.get(i + 1)) <= 0);
        }
    }
}
