package it.polimi.ingsw.am55.MesosModel.SharedBoard;
import it.polimi.ingsw.am55.MesosModel.Cards.*;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.BoardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        board = new Board();

        players = new ArrayList<>();
        players.add(new Player("P1", "yellow"));
        players.add(new Player("P2", "green"));
        players.add(new Player("P3", "red"));

        board.initBoard(players);
    }

    @Test
    void initBoardTest() {
        assertEquals(1, board.getCurrentEra());

        assertNotNull(board.getUpperRow());
        assertNotNull(board.getLowerRow());

        assertNotNull(board.getBiddingTrail());
        assertNotNull(board.getPlayerOrder());
    }

    @Test
    void getBuildingDeckTest() {
        assertThrows(IllegalArgumentException.class, () -> board.getBuildingDeck(0));
        assertThrows(IllegalArgumentException.class, () -> board.getBuildingDeck(4));
    }

    @Test
    void setUpUpperRowTest() {
        int total =
                board.getUpperRow().getCharacterCardsList().size()
                        + board.getUpperRow().getEventCardsList().size();

        assertEquals(7, total);
    }

    @Test
    void setUpLowerRowTest() {
        int total =
                board.getLowerRow().getCharacterCardsList().size()
                + board.getLowerRow().getEventCardsList().size();
        assertEquals(4, total);
    }

    @Test
    void moveBuildingDeckTest() {
        Row donor = new Row();
        BuildingDeck buildingDeck = new BuildingDeck();
        buildingDeck.initBuildingDeckEra3(3);
        donor.setBuildingCardsList(buildingDeck);

        Row tmp = new Row();
        tmp.setBuildingCardsList(buildingDeck);

        Row receiver = new Row();
        buildingDeck.initBuildingDeckEra1(3);
        receiver.setBuildingCardsList(buildingDeck);

        board.moveBuildingDeck(donor, receiver);

        assertEquals(tmp.getBuildingCardsList().getBuildingDeck(), receiver.getBuildingCardsList().getBuildingDeck());
        assertEquals(0, donor.getBuildingCardsList().getBuildingDeck().size());
    }

    @Test
    void moveBuildingDeckTest2() {
        Row receiver = new Row();
        BuildingDeck buildingDeck = new BuildingDeck();
        buildingDeck.initBuildingDeckEra3(3);

        Row tmp = new Row();
        tmp.setBuildingCardsList(buildingDeck);

        board.moveBuildingDeck(buildingDeck, receiver);
        assertEquals(tmp.getBuildingCardsList().getBuildingDeck(), receiver.getBuildingCardsList().getBuildingDeck());
        assertEquals(0, buildingDeck.getBuildingDeck().size());
    }

    @Test
    void startNewEraTest(){
        int oldEra = board.getCurrentEra();
        Row tmpBuildingDeck = new Row();
        tmpBuildingDeck.setBuildingCardsList(board.getBuildingDeck(board.getCurrentEra() +1));
        Row tmpUpperRow = new Row();
        tmpUpperRow.setBuildingCardsList(board.getUpperRow().getBuildingCardsList());

        board.startNewEra();
        assertEquals(oldEra +1,board.getCurrentEra());
        assertEquals(tmpBuildingDeck.getBuildingCardsList().getBuildingDeck(), board.getUpperRow().getBuildingCardsList().getBuildingDeck());
        assertEquals(tmpUpperRow.getBuildingCardsList().getBuildingDeck(), board.getLowerRow().getBuildingCardsList().getBuildingDeck());
        assertEquals(0, board.getBuildingDeck(board.getCurrentEra()).getBuildingDeck().size());
    }

    @Test
    void restoreForRoundTest(){
        board.getUpperRow().getCharacterCardsList().clear();
        board.getUpperRow().getEventCardsList().clear();
        board.getUpperRow().getBuildingCardsList().clear();
        board.getLowerRow().getCharacterCardsList().clear();
        board.getLowerRow().getEventCardsList().clear();
        board.getLowerRow().getBuildingCardsList().clear();

        assertTrue(board.restoreForRound(3));
        assertEquals(7, board.getUpperRow().getCharacterCardsList().size()
        +board.getUpperRow().getEventCardsList().size());

        while(board.restoreForRound(3)){}
        assertEquals(3, board.getCurrentEra());

        setUp();
        board.getTribeDeck().tribeCardStack.clear();
        assertFalse(board.restoreForRound(3));
    }

    @Test
    void findCardUpperRowTest() {
        CardSearchResult result = new CardSearchResult();

        Optional<CharacterCard> cardOpt =
                board.getUpperRow().getCharacterCardsList().stream().findFirst();

        if (cardOpt.isEmpty()) return;

        int id = cardOpt.get().getId();

        board.findCardUpperRow(id, result);

        assertEquals(RowType.UPPER, result.getRowType());

        assertThrows(IllegalArgumentException.class, ()->board.findCardUpperRow(999, result));
    }

    @Test
    void FindCardTest() {
        CardSearchResult result = new CardSearchResult();

        Optional<CharacterCard> upper =
                board.getUpperRow().getCharacterCardsList().stream().findFirst();
        if (upper.isEmpty()) return;
        board.findCard(upper.get().getId(), result);
        assertEquals(RowType.UPPER, result.getRowType());

        Optional<CharacterCard> lower =
                board.getLowerRow().getCharacterCardsList().stream().findFirst();
        if (lower.isEmpty()) return;
        board.findCard(lower.get().getId(), result);
        assertEquals(RowType.LOWER, result.getRowType());

        assertThrows(IllegalArgumentException.class, ()->board.findCard(999, result));
    }

    @Test
    void removeCardTest() {
        CardSearchResult result = new CardSearchResult();

        Optional<CharacterCard> cardOptUpper =
                board.getUpperRow().getCharacterCardsList().stream().findFirst();
        if (cardOptUpper.isEmpty()) return;
        int idUpper = cardOptUpper.get().getId();
        board.findCard(idUpper, result);
        board.removeCard(result);
        boolean stillExistsUpper =
                board.getUpperRow().getCharacterCardsList()
                        .stream()
                        .anyMatch(c -> c.getId() == idUpper);
        assertFalse(stillExistsUpper);

        Optional<CharacterCard> cardOptLower =
                board.getLowerRow().getCharacterCardsList().stream().findFirst();
        if (cardOptLower.isEmpty()) return;
        int idLower = cardOptLower.get().getId();
        board.findCard(idLower, result);
        board.removeCard(result);
        boolean stillExistsLower =
                board.getLowerRow().getCharacterCardsList()
                        .stream()
                        .anyMatch(c -> c.getId() == idLower);
        assertFalse(stillExistsLower);
    }

    @Test
    void getBuildingCardByIndexTest() {
        CardSearchResult result = new CardSearchResult();

        Optional<BuildingCard> cardOptUpper =
                board.getUpperRow().getBuildingCardsList().getBuildingDeck().stream().findFirst();
        if (cardOptUpper.isEmpty()) return;
        int idUpper = cardOptUpper.get().getId();
        board.findCard(idUpper, result);
        assertEquals(RowType.UPPER, result.getRowType());
        assertEquals(result.getCard(), board.getBuildingCardByIndex(result));

        Optional<BuildingCard> cardOptLower =
                board.getLowerRow().getBuildingCardsList().getBuildingDeck().stream().findFirst();
        if (cardOptLower.isEmpty()) return;
        int idLower = cardOptLower.get().getId();
        board.findCard(idLower, result);
        assertEquals(RowType.LOWER, result.getRowType());
        assertEquals(result.getCard(), board.getBuildingCardByIndex(result));
    }

    @Test
    void orderEventsTest() {
        List<EventCard> events = board.orderEvents();
        assertNotNull(events);
    }

    @Test
    void orderEventsEndGameTest(){
        List<EventCard> events = board.orderEventsEndGame();
        int size = (board.getUpperRow().getEventCardsList().size() + board.getLowerRow().getEventCardsList().size());
        assertEquals(size, events.size());

        Comparator<EventCard> comparator =
                Comparator.comparingInt(EventCard::getOrder)
                        .thenComparingInt(EventCard::getEra);

        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(comparator.compare(events.get(i), events.get(i + 1)) <= 0);
        }
    }

    @Test
    void getPlayerPositionOnTrailTest(){
        Player player = new Player("prova", "White");
        board.setPlayer(0, player);

        assertEquals(0, board.getPlayerPositionOnTrail(player));
    }

    @Test
    void toViewTest(){
        BoardView bv1 = board.toView();

        assertEquals(board.getBiddingTrail().getTicketList().size(), bv1.getBiddingTrail().size() );
        assertEquals(board.getPlayerOrder().getTurnOrder().size(), bv1.getTurnTicket().size() );

    }
}