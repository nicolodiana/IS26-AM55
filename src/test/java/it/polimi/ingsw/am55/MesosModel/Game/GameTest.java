package it.polimi.ingsw.am55.MesosModel.Game;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the  Game model.
 * It provides unit tests to verify the correct behavior of the game's initialization,
 * player management, and state transitions.
 */
class GameTest {

    private Game g;
    private final int PLAYERS = 3;

    /**
     * Sets up the test environment before each test method is executed.
     * Initializes a new Game instance with a standard configuration of 3 players.
     *
     * @throws PlayerNumberOutOfRange if the number of players is invalid during initialization.
     */
    @BeforeEach
    void setUp() throws PlayerNumberOutOfRange {
        g = new Game(PLAYERS);
    }

    /**
     * Tests the Game constructor and its initial getter methods.
     * Verifies that the initial game state is CREATED, a game ID is generated,
     * and getting winners before the game finishes throws an exception.
     * Also checks that invalid player counts throw a  PlayerNumberOutOfRange exception
     * and that the initial valid totem colors are correctly populated.
     */
    @Test
    void testConstructorAndInitialGetters() {
        assertAll(
                () -> assertEquals(GameState.CREATED, g.getGameState()),
                () -> assertNotNull(g.getIdGame()),
                () -> assertThrows(GameNotFinished.class, () -> g.getWinners())
        );
        assertThrows(PlayerNumberOutOfRange.class, () -> new Game(1));
        assertThrows(PlayerNumberOutOfRange.class, () -> new Game(6));

        assertTrue(g.getTotemColorsValid().contains("white"));
        assertTrue(g.getTotemColorsValid().contains("black"));
        assertTrue(g.getTotemColorsValid().contains("red"));
        assertTrue(g.getTotemColorsValid().contains("blue"));
        assertTrue(g.getTotemColorsValid().contains("yellow"));
    }

    /**
     * Tests that adding a player beyond the maximum allowed number (defined at initialization)
     * correctly throws a PlayerNumberOutOfRange exception.
     *
     * @throws PlayerNumberOutOfRange if the player limit is exceeded.
     * @throws NicknameAlreadyUsed if the chosen nickname is already taken.
     * @throws TotemAlreadyUsed if the chosen totem color is already taken.
     */
    @Test
    void testAddPlayerToGame_ExceedingLimit() throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        g.addPlayer("Player1", "white");
        g.addPlayer("Player2", "black");
        g.addPlayer("Player3", "yellow");
        assertThrows(PlayerNumberOutOfRange.class, () -> { g.addPlayer("Player4", "red"); });
    }

    /**
     * Tests that adding a player with a nickname that is already in use by another player
     * throws a  NicknameAlreadyUsed exception.
     *
     * @throws PlayerNumberOutOfRange if the player limit is exceeded.
     * @throws NicknameAlreadyUsed if the chosen nickname is already taken.
     * @throws TotemAlreadyUsed if the chosen totem color is already taken.
     */
    @Test
    void testAddPlayerToGame_NicknameAlreadyUsed() throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        g.addPlayer("Player1", "white");
        assertThrows(NicknameAlreadyUsed.class, () -> { g.addPlayer("Player1", "black"); });
    }

    /**
     * Tests that adding a player with a totem color that is already in use by another player
     * throws a TotemAlreadyUsed exception.
     *
     * @throws PlayerNumberOutOfRange if the player limit is exceeded.
     * @throws NicknameAlreadyUsed if the chosen nickname is already taken.
     * @throws TotemAlreadyUsed if the chosen totem color is already taken.
     */
    @Test
    void testAddPlayerToGame_TotemAlreadyUsed() throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        g.addPlayer("Player1", "white");
        assertThrows(TotemAlreadyUsed.class, () -> { g.addPlayer("Player2", "white"); });
    }

    /**
     * Tests the successful addition of multiple players to the game.
     * Verifies that the internal player count increases correctly and that the chosen
     * totem colors are properly removed from the pool of available valid colors.
     *
     * @throws PlayerNumberOutOfRange if the player limit is exceeded.
     * @throws NicknameAlreadyUsed if the chosen nickname is already taken.
     * @throws TotemAlreadyUsed if the chosen totem color is already taken.
     */
    @Test
    void testAddPlayerToGame_AddPlayersIntoTheGame() throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        assertEquals(0, g.getNumPlayers());
        assertTrue(g.getTotemColorsValid().contains("white"));
        assertTrue(g.getTotemColorsValid().contains("black"));
        assertTrue(g.getTotemColorsValid().contains("yellow"));

        g.addPlayer("Player1", "white");
        g.addPlayer("Player2", "black");
        g.addPlayer("Player3", "yellow");

        assertFalse(g.getTotemColorsValid().contains("white"));
        assertFalse(g.getTotemColorsValid().contains("black"));
        assertFalse(g.getTotemColorsValid().contains("yellow"));
        assertEquals(3, g.getNumPlayers());
    }

    /**
     * Tests the transition of the game state when an unexpected crash is handled.
     * Verifies that invoking the crash handler successfully updates the state to CRASHED.
     */
    @Test
    void testChangeStateIfGameCrashed() {
        g.handleGameCrashed();
        assertEquals(GameState.CRASHED, g.getGameState());
    }
}