package it.polimi.ingsw.am55.MesosModel.Game;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Exceptions.*;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Game model.
 * It provides unit tests to verify the correct behavior of game initialization,
 * player management, state transitions, card picking, and end-game evaluation.
 */
class GameTest {

    private Game g;

    /**
     * Sets up the test environment before each test method is executed.
     * Initializes a new {@link Game} instance configured for 2 players.
     *
     * @throws PlayerNumberOutOfRange if the game is initialized with an invalid number of players
     */
    @BeforeEach
    void setUp() throws PlayerNumberOutOfRange {
        g = new Game(2);
    }


    /**
     * Verifies the initial state of a newly created game.
     * The test checks that:
     *
     *     the game state is {@code CREATED}
     *     a game identifier is generated
     *     invalid player counts throw {@link PlayerNumberOutOfRange}
     *     all valid totem colors are initially available
     *
     */
    @Test
    void testConstructorAndInitialGetters() {
        assertAll(
                () -> assertEquals(GameState.CREATED, g.getGameState()),
                () -> assertNotNull(g.getIdGame()),
                () -> assertThrows(PlayerNumberOutOfRange.class, () -> new Game(1)),
                () -> assertThrows(PlayerNumberOutOfRange.class, () -> new Game(6)),
                () -> assertTrue(g.getTotemColorsValid().contains("white")),
                () -> assertTrue(g.getTotemColorsValid().contains("black")),
                () -> assertTrue(g.getTotemColorsValid().contains("red")),
                () -> assertTrue(g.getTotemColorsValid().contains("blue")),
                () -> assertTrue(g.getTotemColorsValid().contains("yellow"))
        );
    }

    /**
     * Verifies that the game state changes to {@code CRASHED}
     * when the crash-handling method is invoked.
     */
    @Test
    void testChangeStateIfGameCrashed() {
        g.handleGameCrashed();
        assertEquals(GameState.CRASHED, g.getGameState());
    }


    /**
     * Verifies that adding a player beyond the configured player limit
     * throws a {@link PlayerNumberOutOfRange} exception.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testAddPlayerToGame_ExceedingLimit() throws PlayerNumberOutOfRange{
        g.addPlayer("Player1", "white");
        g.addPlayer("Player2", "Blue");
        assertEquals(2, g.getNumPlayers());
        assertThrows(PlayerNumberOutOfRange.class, ()->g.addPlayer("Player3", "orange"));
        assertEquals(2, g.getNumPlayers());
    }

    /**
     * Verifies that adding a player with a nickname that is already in use
     * throws a {@link NicknameAlreadyUsed} exception.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testAddPlayerToGame_NicknameAlreadyUsed() throws PlayerNumberOutOfRange {
        g.addPlayer("Player1", "white");
        assertEquals(1, g.getNumPlayers());
        assertThrows(NicknameAlreadyUsed.class,()->g.addPlayer("Player1", "blue"));
        assertEquals(1, g.getNumPlayers());
    }

    /**
     * Verifies that adding a player with a totem color already assigned
     * to another player throws a {@link TotemAlreadyUsed} exception.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testAddPlayerToGame_TotemAlreadyUsed() throws PlayerNumberOutOfRange {
        g.addPlayer("Player1", "white");
        assertEquals(1, g.getNumPlayers());
        assertThrows(TotemAlreadyUsed.class, () -> g.addPlayer("Player2", "white"));
        assertEquals(1, g.getNumPlayers());
    }

    /**
     * Verifies that players are added correctly to the game and that,
     * once the lobby is full, the game enters the {@code PLACETOTEM} phase.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testAddPlayerToGame_AddPlayersIntoTheGame() throws PlayerNumberOutOfRange {
        assertAll(
                () -> assertEquals(0, g.getNumPlayers()),
                () -> assertEquals(GameState.CREATED, g.getGameState()),
                () -> assertTrue(g.getTotemColorsValid().contains("white")),
                () -> assertTrue(g.getTotemColorsValid().contains("blue"))
        );

        g.addPlayer("Player1", "white");
        g.addPlayer("Player2", "blue");

        assertAll(
                () -> assertFalse(g.getTotemColorsValid().contains("white")),
                () -> assertFalse(g.getTotemColorsValid().contains("blue")),
                () -> assertEquals(2, g.getNumPlayers()),
                () -> assertEquals("Player1", g.getPlayers().getFirst().getNickname()),
                () -> assertEquals("Player2", g.getPlayers().get(1).getNickname()),
                () -> assertEquals(2,g.getSharedBoard().getPlayerFromTurnTicket(0).getNumFoods()),
                () -> assertEquals(3,g.getSharedBoard().getPlayerFromTurnTicket(1).getNumFoods()),
                () -> assertEquals(1, g.getCountRound()),
                () -> assertEquals(GameState.PLACETOTEM, g.getGameState())
        );
    }

    @Test
    void testAddPlayertoGame_WrongTotemColor() throws PlayerNumberOutOfRange {
        assertThrows(WrongTotemColor.class, ()->g.addPlayer("Player1", "wrong"));
    }

    /**
     * Verifies that placing a totem with an invalid player while the game
     * is not in a valid playable configuration throws an {@link IllegalStateException}.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPlaceTotem_GamePhaseInvalidAndPlayerInvalid() throws PlayerNumberOutOfRange {
        g.addPlayer("Player1", "white");
        assertThrows(IllegalStateException.class, () -> g.placeTotem(1, "invalid"));
    }

    /**
     * Verifies that placing valid totems in sequence starts the card-picking phase
     * and preserves the expected current player order.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPlaceTotem_GamePhaseValidAndPlayerValid() throws PlayerNumberOutOfRange {
        addTwoPlayers();
        assertEquals(GameState.PLACETOTEM, g.getGameState());

        String firstPlayer = g.getCurrentPlayer();

        g.placeTotem(1, g.getCurrentPlayer());
        g.placeTotem(2, g.getCurrentPlayer());

        assertAll(
                () -> assertEquals(GameState.PICKCARD, g.getGameState()),
                () -> assertEquals(firstPlayer, g.getCurrentPlayer())
        );
    }

    /**
     * Verifies that placing a totem with an invalid player identifier
     * during a valid game phase throws an {@link IllegalStateException}.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPlaceTotem_GamePhaseValidAndPlayerNotValid() throws PlayerNumberOutOfRange{
        addTwoPlayers();
        assertEquals(GameState.PLACETOTEM, g.getGameState());

        assertThrows(IllegalStateException.class, () -> g.placeTotem(1, "invalid"));
    }



    @Test
    void playerOnFoodTicketReceivesSixFoodAutomaticallyAtEndOfPlaceTotem() throws Exception {
        Game game = createFivePlayerGame();

        assertEquals(GameState.PLACETOTEM, game.getGameState());

        String foodPlayer = game.getCurrentPlayer();
        int foodBefore = game.getPlayerFood(foodPlayer);

        // Il primo giocatore piazza sulla tessera A: +3 cibo.
        game.placeTotem(0, foodPlayer);

        int nextTicketIndex = 1;

        // Gli altri giocatori piazzano i totem sulle tessere successive.
        while (game.getGameState().equals(GameState.PLACETOTEM)) {
            String currentPlayer = game.getCurrentPlayer();
            game.placeTotem(nextTicketIndex, currentPlayer);
            nextTicketIndex++;
        }

        int foodAfter = game.getPlayerFood(foodPlayer);

        // +3 dalla tessera A
        // +3 dal primo spazio della TurnTicket quando il totem ritorna
        assertEquals(foodBefore + 6, foodAfter);

        assertEquals(GameState.PICKCARD, game.getGameState());

        // Dopo la pick food automatica, il turno deve passare al prossimo giocatore.
        assertNotEquals(foodPlayer, game.getCurrentPlayer());

        // Il giocatore che ha preso cibo deve essere tornato nel primo spazio della TurnTicket.
        assertNotNull(game.getSharedBoard().getPlayerFromTurnTicket(0));
        assertEquals(
                foodPlayer,
                game.getSharedBoard().getPlayerFromTurnTicket(0).getNickname()
        );
    }


    /**
     * Verifies that attempting to pick a card during the wrong game phase
     * throws an {@link IllegalStateException}.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_GameStateNotValid() throws PlayerNumberOutOfRange {
        addTwoPlayers();

        String player1 = g.getCurrentPlayer();
        g.placeTotem(0, g.getCurrentPlayer());
        assertThrows(IllegalStateException.class, () -> g.pickCard(2, player1));
    }

    /**
     * Verifies that attempting to pick a card with an invalid card identifier
     * throws an {@link IllegalArgumentException}.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_IndexCardNotValid() throws PlayerNumberOutOfRange{
        addTwoPlayers();

        String player1 = g.getCurrentPlayer();
        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> g.pickCard(-1, player1)),
                () -> assertThrows(IllegalArgumentException.class, () -> g.pickCard(121, player1))
        );
    }

    /**
     * Verifies that a player identifier different from the current player
     * cannot be used to pick a card.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_PlayerIdNotValid() throws PlayerNumberOutOfRange {
        addTwoPlayers();
        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());
        assertThrows(IllegalStateException.class, () -> g.pickCard(2, "wrong"));
    }



    /**
     * Verifies a valid pick-card flow across a full round and checks that
     * the game correctly advances to the next round.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_pickValidAndEndRound() throws PlayerNumberOutOfRange {
        addTwoPlayers();
        clearRows(g);

        /*
         * IMPORTANTE: queste carte devono essere aggiunte PRIMA del secondo placeTotem.
         * placeTotem(), quando piazza l'ultimo totem, entra subito in PICKCARD e chiama
         * la logica di skip automatico. Se in quel momento le righe sono vuote, il gioco
         * salta direttamente a EVENTRESOLVE e il primo pickCard lancia:
         * "can't pick a card now".
         */
        g.getSharedBoard().getUpperRow().addCharacterCard(new Hunter(1, true, 1));
        g.getSharedBoard().getUpperRow().addCharacterCard(new Hunter(2, true, 1));
        g.getSharedBoard().getUpperRow().addCharacterCard(new Hunter(3, false, 1));
        g.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(4, false, 1));
        g.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(5, false, 1));
        g.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(6, false, 1));

        String firstPlacingPlayer = g.getCurrentPlayer();
        g.placeTotem(3, firstPlacingPlayer);      // ticket F: 2 carte upper

        String secondPlacingPlayer = g.getCurrentPlayer();
        g.placeTotem(2, secondPlacingPlayer);     // ticket E: 1 carta lower + 1 carta upper

        assertEquals(GameState.PICKCARD, g.getGameState());

        // Il giocatore sul ticket E gioca per primo: deve prendere 1 lower e 1 upper.
        g.pickCard(4, g.getCurrentPlayer());
        assertEquals(GameState.PICKCARD, g.getGameState());

        g.pickCard(1, g.getCurrentPlayer());
        assertEquals(GameState.PICKCARD, g.getGameState());

        // Ora tocca al giocatore sul ticket F: deve prendere 2 upper.
        g.pickCard(2, g.getCurrentPlayer());
        assertEquals(GameState.PICKCARD, g.getGameState());

        g.pickCard(3, g.getCurrentPlayer());
        assertEquals(GameState.EVENTRESOLVE, g.getGameState());

        g.eventResolve();

        String expectedFirstPlayerNextRound = g.getSharedBoard().getFirstPlayerFirstPhase().getNickname();

        assertAll(
                () -> assertEquals(GameState.PLACETOTEM, g.getGameState()),
                () -> assertEquals(expectedFirstPlayerNextRound, g.getCurrentPlayer()),
                () -> assertEquals(2, g.getCountRound())
        );
    }


    /**
     * Verifies the interaction between a normal card pick and the special-pick phase
     * granted by Building 13.
     *
     * @throws Exception if the setup triggers checked exceptions during test preparation
     */
    @Test
    void testPickCardAndPickSpecial_PlayerWithBuilding13() throws Exception {
        addTwoPlayers();

        Player playerWithSpecialBuilding = g.getSharedBoard().getFirstPlayerFirstPhase();
        g.placeTotem(1, g.getCurrentPlayer());
        g.placeTotem(0, g.getCurrentPlayer());

        CharacterCard lowerCard = firstLowerCharacterCard();
        g.pickCard(lowerCard.getId(), g.getCurrentPlayer());

        BuildingCard building13 = new BuildingCard(119, 3, 9, 3, BuildingType.BUILDING13, null, 0);
        Player targetPlayer = g.getPlayers().get(g.getPlayers().indexOf(playerWithSpecialBuilding));
        targetPlayer.addFood(7);


        targetPlayer.addTribeCard(building13);

        CharacterCard upperCard = firstUpperCharacterCard();
        g.pickCard(upperCard.getId(), targetPlayer.getNickname());

        assertAll(
                () -> assertEquals(GameState.PICKSPECIAL, g.getGameState()),
                () -> assertEquals(targetPlayer.getNickname(), g.getCurrentPlayer())
        );

        CharacterCard specialPickCard = firstUpperCharacterCard();
        g.pickSpecial(specialPickCard.getId(), g.getCurrentPlayer());
        g.eventResolve();

        assertEquals(GameState.PLACETOTEM, g.getGameState());
    }

    /**
     * Verifies that a player cannot pick an Era I building when the available food
     * is not sufficient to pay its cost.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_PlayerCannotAffordBuildingEra1() throws PlayerNumberOutOfRange {
        addTwoPlayers();

        Player firstPhasePlayer = g.getSharedBoard().getFirstPlayerFirstPhase();
        g.placeTotem(0, g.getCurrentPlayer());

        Optional<Player> secondPlayer = g.getSharedBoard().getNextPlayerFirstPhase(firstPhasePlayer);
        assertTrue(secondPlayer.isPresent());
        g.placeTotem(1, g.getCurrentPlayer());

        CharacterCard lowerCard = firstLowerCharacterCard();
        g.pickCard(lowerCard.getId(), g.getCurrentPlayer());

        BuildingCard buildingCard = g.getSharedBoard().getUpperRow().getBuildingCardsList().getBuildingCardByIndex(0);
        secondPlayer.get().payFood(1);

        assertAll(
                () -> assertThrows(CannotAffordBuildingException.class,
                        () -> g.pickCard(buildingCard.getId(), g.getCurrentPlayer())),
                () -> assertEquals(GameState.PICKCARD, g.getGameState())
        );
    }

    /**
     * Verifies that a player can successfully pick an Era I building
     * when enough food is available.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_PlayerCanAffordBuildingEra1() throws PlayerNumberOutOfRange {
        addTwoPlayers();

        Player firstPhasePlayer = g.getSharedBoard().getFirstPlayerFirstPhase();
        g.placeTotem(0, g.getCurrentPlayer());

        Optional<Player> secondPlayer = g.getSharedBoard().getNextPlayerFirstPhase(firstPhasePlayer);
        assertTrue(secondPlayer.isPresent());

        g.placeTotem(1, g.getCurrentPlayer());

        CharacterCard lowerCard = firstLowerCharacterCard();
        g.pickCard(lowerCard.getId(), g.getCurrentPlayer());

        BuildingCard buildingCard = g.getSharedBoard().getUpperRow().getBuildingCardsList().getBuildingCardByIndex(0);
        secondPlayer.get().addFood(buildingCard.getFoodCost());

        g.pickCard(buildingCard.getId(), g.getCurrentPlayer());

        assertTrue(secondPlayer.get().getBuildings().contains(buildingCard));
    }

    /**
     * Verifies that a card cannot be picked from the upper row
     * when the current offer tile does not allow it.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_InvalidUpperRow() throws PlayerNumberOutOfRange{
        addTwoPlayers();

        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());

        CharacterCard upperCard = firstUpperCharacterCard();
        assertThrows(CantPickFromRow.class, () -> g.pickCard(upperCard.getId(), g.getCurrentPlayer()));
    }

    /**
     * Verifies that a second illegal pick from the lower row
     * throws a {@link CantPickFromRow} exception.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     * @throws NicknameAlreadyUsed never expected in this test flow, but declared by the current method signature
     * @throws TotemAlreadyUsed never expected in this test flow, but declared by the current method signature
     */
    @Test
    void testPickCard_InvalidLowerRow()
            throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        addTwoPlayers();

        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());

        CharacterCard firstLowerCard = firstLowerCharacterCard();
        g.pickCard(firstLowerCard.getId(), g.getCurrentPlayer());

        CharacterCard secondLowerCard = firstLowerCharacterCard();
        assertThrows(CantPickFromRow.class, () -> g.pickCard(secondLowerCard.getId(), g.getCurrentPlayer()));
    }

    /**
     * Verifies that the game reaches the {@code ENDED} state
     * when the final round is completed.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     * @throws NicknameAlreadyUsed never expected in this test flow, but declared by the current method signature
     * @throws TotemAlreadyUsed never expected in this test flow, but declared by the current method signature
     */
    @Test
    void testPickCard_endGame()
            throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        addTwoPlayers();

        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());
        g.setCountRound(10);

        CharacterCard lowerCard = firstLowerCharacterCard();
        g.pickCard(lowerCard.getId(), g.getCurrentPlayer());

        CharacterCard upperCard = firstUpperCharacterCard();
        g.pickCard(upperCard.getId(), g.getCurrentPlayer());
        g.endGame();

        assertAll(
                () -> assertEquals(10, g.getCountRound()),
                () -> assertEquals(GameState.ENDED, g.getGameState())
        );
    }


    /**
     * Verifies that calling {@code pickSpecial} in the wrong game phase
     * throws an {@link IllegalStateException}.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     * @throws NicknameAlreadyUsed never expected in this test flow, but declared by the current method signature
     * @throws TotemAlreadyUsed never expected in this test flow, but declared by the current method signature
     */
    @Test
    void testPickSpecial_GameStateNotValid()
            throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        addTwoPlayers();

        g.placeTotem(0, g.getCurrentPlayer());

        CharacterCard upperCard = firstUpperCharacterCard();
        assertThrows(IllegalStateException.class, () -> g.pickSpecial(upperCard.getId(), g.getCurrentPlayer()));
    }

    /**
     * Verifies that invalid card identifiers passed to {@code pickSpecial}
     * throw an {@link IllegalArgumentException}.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     * @throws NicknameAlreadyUsed never expected in this test flow, but declared by the current method signature
     * @throws TotemAlreadyUsed never expected in this test flow, but declared by the current method signature
     */
    @Test
    void testPickSpecial_IdCardNotValid()
            throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        addTwoPlayers();

        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());
        g.changeState(GameState.PICKSPECIAL);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> g.pickSpecial(-1, g.getCurrentPlayer())),
                () -> assertThrows(IllegalArgumentException.class, () -> g.pickSpecial(121, g.getCurrentPlayer()))
        );
    }

    /**
     * Verifies that picking from the lower row during the special-pick phase
     * is rejected as an illegal action.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    @Test
    void testPickCard_NotValidRow() throws PlayerNumberOutOfRange {
        addTwoPlayers();
        // Illegal attempt to pick from the lower row.
        g.placeTotem(0, g.getCurrentPlayer());
        g.placeTotem(1, g.getCurrentPlayer());
        g.changeState(GameState.PICKSPECIAL);
        CharacterCard lowerCard = firstLowerCharacterCard();
        assertThrows(IllegalArgumentException.class, () -> g.pickSpecial(lowerCard.getId(), g.getCurrentPlayer()));
    }

    @Test
    void testSelectableCardsUsedBySkipLogic() throws Exception {
        Game game = createStartedGame(2);
        clearRows(game);
        Player player = game.getPlayers().getFirst();
        Row upper = game.getSharedBoard().getUpperRow();
        Row lower = game.getSharedBoard().getLowerRow();

        upper.addEventCard(new HuntEventCard(90, 1, 1));
        addBuilding(upper, 91, 4, BuildingType.BUILDING1);
        lower.addCharacterCard(new Hunter(1, false, 1));

        assertFalse(game.getSharedBoard().hasSelectableCard(
                it.polimi.ingsw.am55.MesosModel.Enum.RowType.UPPER, player));
        assertTrue(game.getSharedBoard().hasSelectableCard(
                it.polimi.ingsw.am55.MesosModel.Enum.RowType.LOWER, player));

        player.addFood(2);
        assertTrue(upper.hasSelectableCard(player));

        Row discountedRow = new Row();
        Player discountedPlayer = new Player("discounted", "blue");
        addBuilding(discountedRow, 92, 1, BuildingType.BUILDING1);
        discountedPlayer.addTribeCard(new Builder(2, 0, 2, 1));
        assertTrue(discountedRow.hasSelectableCard(discountedPlayer));
    }

    @Test
    void testLastPlacementSkipsConsecutiveBlockedPlayers() throws Exception {
        Game game = createStartedGame(3);
        clearRows(game);
        addBuilding(game.getSharedBoard().getLowerRow(), 91, 3, BuildingType.BUILDING1);

        String first = game.getCurrentPlayer();
        assertTrue(game.placeTotem(0, first).isEmpty());
        String second = game.getCurrentPlayer();
        assertTrue(game.placeTotem(1, second).isEmpty());
        String third = game.getCurrentPlayer();

        assertEquals(List.of(first, second), game.placeTotem(2, third));
        assertEquals(third, game.getCurrentPlayer());
        assertEquals(GameState.PICKCARD, game.getGameState());
    }

    @Test
    void testPartialPickSkipsCurrentAndFollowingBlockedPlayer() throws Exception {
        Game game = createStartedGame(2);
        clearRows(game);
        game.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(1, false, 1));
        addBuilding(game.getSharedBoard().getUpperRow(), 91, 10, BuildingType.BUILDING1);

        String first = game.getCurrentPlayer();
        game.placeTotem(2, first);
        String second = game.getCurrentPlayer();
        game.placeTotem(3, second);

        assertEquals(List.of(first, second), game.pickCard(1, first));
        assertEquals(GameState.EVENTRESOLVE, game.getGameState());
        assertEquals(1, playerByName(game, first).getHuntersList().size());
        assertEquals(0, playerByName(game, first).getLowerRowCardSelected());
    }

    @Test
    void testImmediateFoodEffectPreventsPrematureSkip() throws Exception {
        Game game = createStartedGame(2);
        clearRows(game);
        game.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(1, true, 1));
        addBuilding(game.getSharedBoard().getUpperRow(), 91, 3, BuildingType.BUILDING1);

        String first = game.getCurrentPlayer();
        game.placeTotem(2, first);
        String second = game.getCurrentPlayer();
        game.placeTotem(3, second);

        assertTrue(game.pickCard(1, first).isEmpty());
        assertEquals(first, game.getCurrentPlayer());
        assertEquals(List.of(second), game.pickCard(91, first));
        assertEquals(GameState.EVENTRESOLVE, game.getGameState());
        assertTrue(playerByName(game, first).hasBuilding(BuildingType.BUILDING1));
    }

    @Test
    void testSpecialPickRemainsAvailableAndDoesNotChangeCounters() throws Exception {
        Game game = createStartedGame(2);
        clearRows(game);
        String owner = game.getCurrentPlayer();
        Player ownerPlayer = playerByName(game, owner);
        ownerPlayer.addTribeCard(building(116, 0, BuildingType.BUILDING13));
        game.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(1, false, 1));
        game.getSharedBoard().getUpperRow().addCharacterCard(new Hunter(2, false, 1));
        game.getSharedBoard().getUpperRow().addCharacterCard(new Hunter(3, false, 1));

        game.placeTotem(0, owner);
        String other = game.getCurrentPlayer();
        game.placeTotem(1, other);
        assertTrue(game.pickCard(1, owner).isEmpty());
        assertTrue(game.pickCard(2, other).isEmpty());
        assertEquals(GameState.PICKSPECIAL, game.getGameState());

        game.pickSpecial(3, owner);
        assertEquals(GameState.EVENTRESOLVE, game.getGameState());
        assertEquals(0, ownerPlayer.getUpperRowCardSelected());
        assertEquals(0, ownerPlayer.getLowerRowCardSelected());
    }

    @Test
    void testUnavailableSpecialPickIsSkippedAtFinalRound() throws Exception {
        Game game = createStartedGame(2);
        clearRows(game);
        game.setCountRound(10);
        String owner = game.getCurrentPlayer();
        playerByName(game, owner).addTribeCard(building(116, 0, BuildingType.BUILDING13));
        game.getSharedBoard().getLowerRow().addCharacterCard(new Hunter(1, false, 1));
        game.getSharedBoard().getUpperRow().addCharacterCard(new Hunter(2, false, 1));

        game.placeTotem(0, owner);
        String other = game.getCurrentPlayer();
        game.placeTotem(1, other);
        game.pickCard(1, owner);

        assertEquals(List.of(owner), game.pickCard(2, other));
        assertEquals(GameState.ENDGAMERESOLVE, game.getGameState());
    }

    @Test
    void testAllPlayersBlockedDuringInitialCheck() throws Exception {
        Game game = createStartedGame(2);
        clearRows(game);
        String first = game.getCurrentPlayer();
        game.placeTotem(0, first);
        String second = game.getCurrentPlayer();

        assertEquals(List.of(first, second), game.placeTotem(1, second));
        assertEquals(GameState.EVENTRESOLVE, game.getGameState());
    }


    @Test
    void testEndGame_illegalStateException()
            throws PlayerNumberOutOfRange, NicknameAlreadyUsed, TotemAlreadyUsed {
        addTwoPlayers();
        assertThrows(IllegalStateException.class, () -> g.endGame());
    }
    private void setFood(Player player, int targetFood) {
        int delta = targetFood - player.getNumFoods();

        if (delta > 0) {
            player.addFood(delta);
        } else if (delta < 0) {
            player.payFood(-delta);
        }
    }

    private void addEndGameCardsWithFoodDiscount(Player player) {
        // 8 personaggi totali.
        new Artist(1, 1).addToPlayer(player);
        new Artist(2, 1).addToPlayer(player);

        new Builder(3, 3, 0, 1).addToPlayer(player);

        new Inventor("wheel", 4, 1).addToPlayer(player);
        new Inventor("fire", 5, 1).addToPlayer(player);

        // false: non aggiunge cibo extra quando viene aggiunto al player.
        new Hunter(6, false, 1).addToPlayer(player);

        // 1 Collector = sconto sostentamento di 3 cibi.
        new Collector(7, 1).addToPlayer(player);

        new Shaman(8, 1, 1).addToPlayer(player);

        // Building2(COLLECTOR) = ulteriore sconto di 1 cibo,
        // perché c'è 1 carta Collector.
        new BuildingCard(
                101,
                1,
                0,
                0,
                BuildingType.BUILDING2,
                CharacterType.COLLECTOR,
                0
        ).addToPlayer(player);

        // Building9: raddoppia i punti dei Builder a fine partita.
        new BuildingCard(
                102,
                1,
                0,
                0,
                BuildingType.BUILDING9,
                null,
                0
        ).addToPlayer(player);

        // Building11: +6 PP per ogni set completo di personaggi.
        new BuildingCard(
                103,
                1,
                0,
                0,
                BuildingType.BUILDING11,
                null,
                0
        ).addToPlayer(player);

        // Building12: +2 PP per ogni Artist.
        new BuildingCard(
                104,
                1,
                0,
                2,
                BuildingType.BUILDING12,
                CharacterType.ARTIST,
                0
        ).addToPlayer(player);

        // Building14: +25 PP a fine partita.
        new BuildingCard(
                105,
                1,
                0,
                0,
                BuildingType.BUILDING14,
                null,
                0
        ).addToPlayer(player);
    }

    private Game createEndGameScenarioWithSustenanceDiscount() throws Exception {
        Game game = new Game(2);

        game.addPlayer("alice", "blue");
        game.addPlayer("bob", "orange");

        // Rende deterministico il test: non vogliamo dipendere dagli eventi
        // pescati casualmente dal costruttore della board.
        clearRows(game);

        for (Player player : game.getPlayers()) {
            addEndGameCardsWithFoodDiscount(player);

            // Sostentamento:
            // 8 personaggi - 3 cibi del Collector - 1 cibo del Building2(COLLECTOR) = 4 cibi da pagare.
            // Partendo da 5, resta 1 cibo e NON si perde nessun PP.
            setFood(player, 5);
        }

        new SustenanceEventCard(90, 1, 1).activateEvent(game.getPlayers());

        for (Player player : game.getPlayers()) {
            assertEquals(1, player.getNumFoods());
            assertEquals(0, player.getNumPP());
        }

        // Evento finale deterministico: con 2 Artist dà +2 PP a ogni giocatore.
        // Quindi il totale atteso è 55 dagli effetti finali + 2 da PAINTINGS = 57.
        game.getSharedBoard()
                .getLowerRow()
                .addEventCard(new PaintingsEventCard(91, 1, 2, 1, 1, 2));

        game.changeState(GameState.ENDGAMERESOLVE);

        return game;
    }

    @Test
    void testEndGame_ShouldApplyAllEffectsInsideForLoop() throws Exception {
        Game game = createEndGameScenarioWithSustenanceDiscount();

        EndGameResultView result = game.endGame();

        Player alice = game.getSinglePlayer("alice");
        Player bob = game.getSinglePlayer("bob");

        // Bonus finali per player:
        // Artists: 2 / 2 * 10 = 10
        // Builder: 3 * 2 grazie a Building9 = 6
        // Inventors: 2 inventori * 2 icone distinte = 4
        // Building11: 1 set completo * 6 = 6
        // Building12: 2 Artists * 2 = 4
        // Building14: 25
        // Totale effetti finali = 55
        // Evento PAINTINGS risolto da endGame(): +2 PP
        // Totale finale = 57
        assertEquals(57, alice.getNumPP());
        assertEquals(57, bob.getNumPP());

        assertEquals(1, alice.getNumFoods());
        assertEquals(1, bob.getNumFoods());

        assertEquals(1, result.getResolvedEvents().size());
        assertEquals(12, result.getEndGameEffects().size());

        Map<String, Long> effectsByPlayer = result.getEndGameEffects()
                .stream()
                .collect(Collectors.groupingBy(
                        EndGameEffectView::getPlayerNickname,
                        Collectors.counting()
                ));

        assertEquals(6L, effectsByPlayer.get("alice"));
        assertEquals(6L, effectsByPlayer.get("bob"));

        assertEquals(GameState.ENDED, game.getGameState());
    }

    @Test
    void testEndGame_SharedVictory_WithAppliedEffects() throws Exception {
        Game game = createEndGameScenarioWithSustenanceDiscount();

        EndGameResultView result = game.endGame();

        assertEquals(1, result.getResolvedEvents().size());

        Map<String, Integer> winners = result.getWinners();

        assertEquals(2, winners.size());

        assertTrue(winners.containsKey("alice"));
        assertTrue(winners.containsKey("bob"));

        // In pareggio sui PP, la mappa winners contiene il cibo rimasto.
        // I PP sono pari a 57 per entrambi: 55 effetti finali + 2 PAINTINGS.
        // Il cibo deve essere 1 perché lo sconto cibo è applicato correttamente.
        assertEquals(1, winners.get("alice"));
        assertEquals(1, winners.get("bob"));

        assertEquals(57, game.getPlayerPoints("alice"));
        assertEquals(57, game.getPlayerPoints("bob"));

        assertEquals(1, game.getPlayerFood("alice"));
        assertEquals(1, game.getPlayerFood("bob"));

        assertEquals(GameState.ENDED, game.getGameState());
    }


    @Test
    void testEventResolver() throws PlayerNumberOutOfRange {
        assertThrows(IllegalStateException.class, g::eventResolve);
        addTwoPlayers();
        g.getSharedBoard().getLowerRow().addEventCard(new HuntEventCard(21, 1, 1));
        g.changeState(GameState.EVENTRESOLVE);

        assertNotNull(g.eventResolve());
    }



    @Test
    void quitGame() throws PlayerNumberOutOfRange {
        Game game = new Game(2);
        game.quitGame();
        assertEquals(GameState.ENDED , game.getGameState());
    }

    @Test
    void getStateTest(){
        GameState state = g.getGameState();
        assertEquals(GameState.CREATED, state);
    }

    @Test
    void getSinglePlayerTest(){
        Player p1 = new Player("prova", "white");
        Player p2 = new Player("prova2", "orange");
        try {
            g.addPlayer(p1.getNickname(), p1.getTotem());
            g.addPlayer(p2.getNickname(), p2.getTotem());
        } catch (PlayerNumberOutOfRange e) {}

        assertEquals(p1.getTotem(), g.getSinglePlayer("prova").getTotem());
    }

    @Test
    void getPlayerPoints_getPlayerFood_Test(){
        try {
            g.addPlayer("prova", "white");
        } catch (PlayerNumberOutOfRange e) {}

        g.getPlayers().get(0).addPP(20);
        g.getPlayers().get(0).addFood(20);

        assertEquals(20, g.getPlayerPoints("prova"));
        assertEquals(20, g.getPlayerFood("prova"));
    }

    @Test
    void toViewTest(){
        GameView gv = g.toView();
        assertEquals(g.getIdGame(), gv.getGameId());
        assertEquals(g.getPlayers(), gv.getPlayers());
        assertEquals(g.getCurrentPlayer(), gv.getCurrentPlayer());
        assertEquals(g.getCountRound(), gv.getRound());
    }


    /**
     * Adds two players to the shared game instance.
     *
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    private void addTwoPlayers() throws PlayerNumberOutOfRange{
        g.addPlayer("Player1", "white");
        g.addPlayer("Player2", "blue");
    }

    /**
     * Creates and returns a five-player game ready for testing.
     *
     * @return a game initialized with five players
     * @throws PlayerNumberOutOfRange if the game size becomes invalid during setup
     */
    private Game createFivePlayerGame() throws PlayerNumberOutOfRange {
        Game game = new Game(5);
        game.addPlayer("Player1", "white");
        game.addPlayer("Player2", "blue");
        game.addPlayer("Player3", "orange");
        game.addPlayer("Player4", "purple");
        game.addPlayer("Player 5", "yellow");
        return game;
    }

    /**
     * Retrieves the first available character card from the upper row.
     *
     * @return the first character card currently available in the upper row
     * @throws AssertionError if no character card is present in the upper row
     */
    private CharacterCard firstUpperCharacterCard() {
        return g.getSharedBoard()
                .getUpperRow()
                .getCharacterCardsList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected at least one character card in upper row"));
    }

    /**
     * Retrieves the first available character card from the lower row.
     *
     * @return the first character card currently available in the lower row
     * @throws AssertionError if no character card is present in the lower row
     */
    private CharacterCard firstLowerCharacterCard() {
        return g.getSharedBoard()
                .getLowerRow()
                .getCharacterCardsList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected at least one character card in lower row"));
    }


    private Game createStartedGame(int players) throws PlayerNumberOutOfRange {
        Game game = new Game(players);
        String[] names = {"p1", "p2", "p3", "p4", "p5"};
        String[] colors = {"white", "blue", "orange", "purple", "yellow"};
        for (int i = 0; i < players; i++) game.addPlayer(names[i], colors[i]);
        return game;
    }

    private void clearRows(Game game) {
        for (Row row : List.of(game.getSharedBoard().getUpperRow(),
                game.getSharedBoard().getLowerRow())) {
            row.clearRoundEnd();
            row.clearBuildingCards();
        }
    }

    private BuildingCard building(int id, int cost, BuildingType type) {
        return new BuildingCard(id, 1, cost, 0, type, null, 0);
    }

    private void addBuilding(Row row, int id, int cost, BuildingType type) {
        row.getBuildingCardsList().getBuildingDeck().add(building(id, cost, type));
    }

    private Player playerByName(Game game, String nickname) {
        return game.getPlayers().stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Adds the same predefined tie-oriented card setup to the given player.
     *
     * @param p the player who receives the setup
     */
    private void giveTieSetup(Player p) {
        new Artist(6, 1).addToPlayer(p);
        new Artist(7, 1).addToPlayer(p);
        new Builder(9, 3, 1, 1).addToPlayer(p);
        new Inventor("Boat", 14, 1).addToPlayer(p);
        new Inventor("Arrowhead", 15, 1).addToPlayer(p);
        new Hunter(3, false, 1).addToPlayer(p);
        new Collector(12, 1).addToPlayer(p);
    }


}
