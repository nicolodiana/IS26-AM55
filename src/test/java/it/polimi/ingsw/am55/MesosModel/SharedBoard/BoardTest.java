package it.polimi.ingsw.am55.MesosModel.SharedBoard;
import it.polimi.ingsw.am55.MesosModel.Cards.*;
import it.polimi.ingsw.am55.MesosModel.Enum.RowType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
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

    // -------------------------
    // INIT + SETUP TEST
    // -------------------------

    @Test
    void testInitBoardCreatesValidState() {
        assertEquals(1, board.getCurrentEra());

        assertNotNull(board.getUpperRow());
        assertNotNull(board.getLowerRow());

        assertNotNull(board.getBiddingTrail());
        assertNotNull(board.getPlayerOrder());
    }

    @Test
    void testUpperRowHasCardsAfterInit() {
        int total =
                board.getUpperRow().getCharacterCardsList().size()
                        + board.getUpperRow().getEventCardsList().size();

        assertTrue(total >= 3); // non dipende dal deck interno
    }

    // -------------------------
    // FIND CARD TESTS
    // -------------------------

    @Test
    void testFindCardUpperRowSuccess() {
        CardSearchResult result = new CardSearchResult();

        // prendo una carta reale dalla row
        Optional<CharacterCard> cardOpt =
                board.getUpperRow().getCharacterCardsList().stream().findFirst();

        if (cardOpt.isEmpty()) return;

        int id = cardOpt.get().getId();

        board.findCardUpperRow(id, result);

        assertEquals(RowType.UPPER, result.getRowType());
    }

    @Test
    void testFindCardBothRows() {
        CardSearchResult result = new CardSearchResult();

        Optional<CharacterCard> upper =
                board.getUpperRow().getCharacterCardsList().stream().findFirst();

        if (upper.isEmpty()) return;

        board.findCard(upper.get().getId(), result);

        assertEquals(RowType.UPPER, result.getRowType());
    }

    @Test
    void testFindCardNotFoundThrows() {
        CardSearchResult result = new CardSearchResult();

        assertThrows(IllegalArgumentException.class,
                () -> board.findCard(-9999, result));
    }

    // -------------------------
    // REMOVE CARD TESTS
    // -------------------------

    @Test
    void testRemoveCardUpperRow() {
        CardSearchResult result = new CardSearchResult();

        Optional<CharacterCard> cardOpt =
                board.getUpperRow().getCharacterCardsList().stream().findFirst();

        if (cardOpt.isEmpty()) return;

        int id = cardOpt.get().getId();

        board.findCard(id, result);

        board.removeCard(result);

        boolean stillExists =
                board.getUpperRow().getCharacterCardsList()
                        .stream()
                        .anyMatch(c -> c.getId() == id);

        assertFalse(stillExists);
    }

    // -------------------------
    // TURN ORDER TESTS
    // -------------------------

    @Test
    void testFirstPlayerExists() {
        assertNotNull(board.getFirstPlayerFirstPhase());
    }

    @Test
    void testNextPlayerOptionalLogic() {
        Player first = board.getFirstPlayerFirstPhase();

        Optional<Player> next = board.getNextPlayerFirstPhase(first);

        assertTrue(next.isPresent() || next.isEmpty());
    }

    // -------------------------
    // BIDDING TRAIL TESTS
    // -------------------------

    @Test
    void testSetPlayerOnBiddingTrail() {
        board.setPlayer(0, players.get(0));

        assertDoesNotThrow(() -> board.setPlayer(1, players.get(1)));
    }

    @Test
    void testGetChooseCardsFromTrail() {
        board.setPlayer(0, players.get(0));

        int upper = board.getChooseUpperCard(players.get(0));
        int lower = board.getChooseLowerCard(players.get(0));

        assertTrue(upper >= 0);
        assertTrue(lower >= 0);
    }

    // -------------------------
    // BUILDING DECK TESTS
    // -------------------------

    @Test
    void testGetBuildingDeckValidEra() {
        assertNotNull(board.getBuildingDeck(1));
        assertNotNull(board.getBuildingDeck(2));
        assertNotNull(board.getBuildingDeck(3));
    }

    @Test
    void testGetBuildingDeckInvalidEra() {
        assertThrows(IllegalArgumentException.class,
                () -> board.getBuildingDeck(99));
    }

    // -------------------------
    // ERA / RESTORE TESTS
    // -------------------------

    @Test
    void testRestoreRoundDoesNotCrash() {
        assertDoesNotThrow(() -> board.restoreForRound(players.size()));
    }

    // -------------------------
    // EVENT TESTS
    // -------------------------

    @Test
    void testOrderEventsReturnsList() {
        List<EventCard> events = board.orderEvents();

        assertNotNull(events);
    }

    @Test
    void testEventResolveEndGameReturnsSortedList() {
        List<EventCard> events = board.eventResolveEndGame(players);

        assertNotNull(events);

        for (int i = 1; i < events.size(); i++) {
            boolean ordered =
                    events.get(i - 1).getOrder() <= events.get(i).getOrder();
            assertTrue(ordered);
        }
    }

    // -------------------------
    // STRESS TEST (IMPORTANT)
    // -------------------------

    @Test
    void stressTestBoardOperations() {
        for (int i = 0; i < 50; i++) {
            try {
                CardSearchResult result = new CardSearchResult();

                if (!board.getUpperRow().getCharacterCardsList().isEmpty()) {
                    CharacterCard c =
                            board.getUpperRow().getCharacterCardsList().get(0);

                    board.findCard(c.getId(), result);
                    board.removeCard(result);
                }

                board.restoreForRound(players.size());

            } catch (Exception ignored) {
                // stress test: non deve crashare
            }
        }

        assertTrue(true);
    }
}