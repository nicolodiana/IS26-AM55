package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@link TurnTicket}.
 * Verifies the correct initialization of the turn order, turn management
 * during the first phase, and the assignment of maluses or bonuses based on position.
 */
class TurnTicketTest {

    private TurnTicket turnTicket;
    private Player p1, p2, p3;

    /**
     * Initial setup executed before each test method.
     * Initializes the {@link TurnTicket} by injecting a {@link Random} with a fixed seed (42)
     * to guarantee shuffle determinism. Creates three dummy players for testing.
     */
    @BeforeEach
    void setUp() {
        turnTicket = new TurnTicket(new Random(42));
        p1 = new Player("Player1", "black");
        p2 = new Player("Player2", "white");
        p3 = new Player("Player3", "yellow");
    }

    /**
     * Verifies that the default constructor initializes the list properly.
     */
    @Test
    void testDefaultConstructor() {
        TurnTicket emptyTicket = new TurnTicket();
        assertNotNull(emptyTicket.getTurnOrder(), "Turn order list should be initialized");
        assertEquals(0, emptyTicket.getTurnOrder().size(), "Turn order list should be empty initially");
    }

    /**
     * Verifies the initialization switch statement for 2, 3, 4, and 5 players
     * to ensure full branch coverage.
     */
    @Test
    void testInitTurnTicket_AllValidPlayerCounts() {
        Player p4 = new Player("Player4", "blue");
        Player p5 = new Player("Player5", "red");

        // Tests case 2
        turnTicket.initTurnTicket(List.of(p1, p2));
        assertEquals(2, turnTicket.getTurnOrder().size());

        // Tests case 3
        turnTicket.initTurnTicket(List.of(p1, p2, p3));
        assertEquals(3, turnTicket.getTurnOrder().size());

        // Tests case 4
        turnTicket.initTurnTicket(List.of(p1, p2, p3, p4));
        assertEquals(4, turnTicket.getTurnOrder().size());

        // Tests case 5
        turnTicket.initTurnTicket(List.of(p1, p2, p3, p4, p5));
        assertEquals(5, turnTicket.getTurnOrder().size());
    }

    /**
     * Verifies that the TurnTicket initialization fails if the number of players is invalid.
     * <p>
     * Expected result: An {@link IllegalArgumentException} is thrown when
     * passing a list containing only one player.
     */
    @Test
    void testInvalidNumberOfPlayers() {
        assertThrows(IllegalArgumentException.class, () -> turnTicket.initTurnTicket(List.of(p1)));
    }

    /**
     * Verifies the retrieval of the next player during the first phase under normal conditions (Happy Path).
     * Expected result: The method returns a non-empty Optional
     * containing the player exactly following the one passed as a parameter.
     */
    @Test
    void testGetNextPlayerFirstPhase_NextPlayerIsOnTurnOrderTicket() {
        turnTicket.initTurnTicket(List.of(p1, p2));
        Player first = turnTicket.getTurnPlayer(0);
        Player second = turnTicket.getTurnPlayer(1);

        assertTrue(turnTicket.getNextPlayerFirstPhase(first).isPresent());
        assertEquals(second, turnTicket.getNextPlayerFirstPhase(first).get());
    }

    /**
     * Verifies the robustness of the turn retrieval method against null inputs.
     * Expected result: An IllegalArgumentException is thrown if the parameter is null.
     */
    @Test
    void testGetNextPlayerFirstPhase_PlayerIsNull() {
        turnTicket.initTurnTicket(List.of(p1, p2));
        assertThrows(IllegalArgumentException.class, () -> turnTicket.getNextPlayerFirstPhase(null));
    }

    /**
     * Verifies the behavior when requesting the turn of a player not present in the game.
     * Expected result: An  IllegalArgumentException is thrown to prevent inconsistent advancement.
     */
    @Test
    void testGetNextPlayerFirstPhase_PlayerDoesNotExist() {
        turnTicket.initTurnTicket(List.of(p2, p3));
        assertThrows(IllegalArgumentException.class, () -> turnTicket.getNextPlayerFirstPhase(null));
    }

    /**
     * Verifies turn retrieval when the given player is the last one in the turn order.
     * Expected result: The method returns an {@link java.util.Optional#empty()}, indicating the end of the phase.
     */
    @Test
    void testGetNextPlayerFirstPhase_NoOtherPlayerOnTurnOrderTicket() {
        turnTicket.initTurnTicket(List.of(p1, p2));
        Player last = turnTicket.getTurnPlayer(1);

        assertTrue(turnTicket.getNextPlayerFirstPhase(last).isEmpty());
    }

    /**
     * Verifies that players who are not in the last position correctly receive the food bonus.
     * Expected result: The food stock of the first player increases by one unit.
     */
    @Test
    void testGiveFoodForTwoPlayersApplyFood() {
        turnTicket.initTurnTicket(List.of(p1, p2));
        Player first = turnTicket.getTurnPlayer(0);

        assertEquals(0, first.getNumFoods());
        turnTicket.giveMalusOrBonus(first);
        assertEquals(1, first.getNumFoods());
    }

    /**
     * Verifies that the player in the last position correctly receives the expected malus.
     * Expected result: The Prestige Points (PP) of the player in the last position suffer a -2 penalty.
     */
    @Test
    void testGiveFoodForTwoPlayersApplyMalus() {
        turnTicket.initTurnTicket(List.of(p1, p2));
        Player last = turnTicket.getTurnPlayer(1);

        assertEquals(0, last.getNumPP());
        turnTicket.giveMalusOrBonus(last);
        assertEquals(-2, last.getNumPP());
    }

    /**
     * Verifies the manual removal and addition methods of a player to the TurnTicket.
     * Expected result: Removal sets the first slot to null, while subsequent addition
     * repositions the player in the first available slot.
     */
    @Test
    void testAddAndRemovePlayer() {
        turnTicket.initTurnTicket(List.of(p1, p2));
        Player first = turnTicket.getTurnPlayer(0);
        Player second = turnTicket.getTurnPlayer(1);

        turnTicket.removePlayerFromTurnTicket();
        assertNull(turnTicket.getTurnOrder().get(0));

        turnTicket.addPlayer(first);
        assertSame(first, turnTicket.getTurnOrder().get(0));
        assertSame(second, turnTicket.getTurnOrder().get(1));
    }
}