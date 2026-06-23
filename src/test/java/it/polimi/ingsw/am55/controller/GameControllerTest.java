package it.polimi.ingsw.am55.controller;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Effect.HuntEventCard;
import it.polimi.ingsw.am55.MesosModel.Effect.Hunter;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
import it.polimi.ingsw.am55.controller.GameController;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.message.MessageToClient;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @Test
    void gameControllerConstructorTest() {
        GameController gameController = new GameController();

        assertFalse(gameController.getLobbyView().hasGame());
        assertTrue(gameController.getLobbyView().getPlayerIds().isEmpty());
    }

    // CREATE GAME TEST

    @Test
    void createGameTest_gameAlreadyExist() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 2).update(clientModel);
        gameController.createGame("p2", "yellow", 2).update(clientModel);

        assertEquals("Game already exists", clientModel.getLastError());
    }

    @Test
    void createGameTest_Success() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 2).update(clientModel);

        assertEquals("game created waiting other players..", clientModel.getStateRequest());
        assertNull(clientModel.getLastError());
        assertTrue(gameController.getLobbyView().hasGame());
        assertEquals(List.of("p1"), gameController.getLobbyView().getPlayerIds());
        assertTrue(gameController.getLobbyView().isTotemAlreadyChosen("blue"));
    }

    @Test
    void createGameTest_CatchException() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 6).update(clientModel);

        assertEquals("Invalid numbers of player", clientModel.getLastError());
        assertFalse(gameController.getLobbyView().hasGame());
    }

    // JOIN GAME TEST

    @Test
    void joinGameTest_GameDoesNotExist() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.joinGame("p1", "blue").update(clientModel);

        assertEquals("No game created.", clientModel.getLastError());
    }

    @Test
    void joinGameTest_CatchException() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 2).update(clientModel);
        gameController.joinGame("p2", "blue").update(clientModel);

        assertEquals("Totem is already exists", clientModel.getLastError());
    }

    @Test
    void joinGameTest_Success() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 3).update(clientModel);
        gameController.joinGame("p2", "yellow").update(clientModel);

        assertEquals("added in the game, waiting for other players..", clientModel.getStateRequest());
        assertEquals(List.of("p1", "p2"), gameController.getLobbyView().getPlayerIds());

        gameController.joinGame("p3", "orange").update(clientModel);

        assertEquals("Game started!", clientModel.getStateRequest());
        assertEquals(GameState.PLACETOTEM, clientModel.getGameView().getState());
        assertEquals(3, clientModel.getGameView().getPlayers().size());
    }

    // IS IN GAME TEST

    @Test
    void isInGameTest() {
        GameController gameController = new GameController();

        gameController.createGame("p1", "blue", 2);

        assertTrue(gameController.getLobbyView().getPlayerIds().contains("p1"));
        assertFalse(gameController.getLobbyView().getPlayerIds().contains("p3"));
    }

    // QUIT GAME TEST

    @Test
    void quitGameTest_GameDoesNotExist() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.quitGame("p1").update(clientModel);

        assertEquals("No game created.", clientModel.getLastError());
    }

    @Test
    void quitGameTest_Success() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 2).update(clientModel);
        gameController.quitGame("p1").update(clientModel);

        assertEquals("PLAYER  p1 quit. ", clientModel.getStateRequest());
        assertTrue(clientModel.isGameEnded());
        assertFalse(gameController.getLobbyView().hasGame());
    }

    // HANDLE GAME CRASHED TEST

    @Test
    void handleGameCrashedTest_Success() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 2).update(clientModel);
        gameController.handleGameCrashed().update(clientModel);

        assertEquals("a Player got disconnected, game ended.", clientModel.getStateRequest());
        assertTrue(clientModel.isGameCrashed());
        assertFalse(gameController.getLobbyView().hasGame());
    }

    // PLACE TOTEM TEST

    @Test
    void placeTotemTest_GameDoesNotExist() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.placeTotem("p1", 9999).update(clientModel);

        assertEquals("No game created.", clientModel.getLastError());
    }

    @Test
    void placeTotemTest_Success() {
        Fixture fixture = startedTwoPlayerGame();

        String currentPlayer = fixture.clientModel.getGameView().getCurrentPlayer();
        fixture.controller.placeTotem(currentPlayer, 1).update(fixture.clientModel);

        assertEquals("place totem done", fixture.clientModel.getStateRequest());
        assertEquals(GameState.PLACETOTEM, fixture.clientModel.getGameView().getState());
    }

    @Test
    void placeTotemTest_CatchException() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.createGame("p1", "blue", 2).update(clientModel);
        gameController.placeTotem("p1", 9999).update(clientModel);

        assertEquals("You can't place your totem", clientModel.getLastError());
    }

    // PICK CARD TEST

    @Test
    void pickCardTest_GameDoesNotExist() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.pickCard("p1", 1).update(clientModel);

        assertEquals("No game created.", clientModel.getLastError());
    }

    @Test
    void pickCardTest_CatchException() {
        Fixture fixture = startedTwoPlayerGame();

        fixture.controller.pickCard("p1", 1).update(fixture.clientModel);

        assertEquals("can't pick a card now", fixture.clientModel.getLastError());
    }

    @Test
    void pickCardTest_Success_NormalPick() {
        Fixture fixture = preparedUpperOnlyRound(false, false, false);

        pickOneUpperCard(fixture);

        assertEquals("pick done", fixture.clientModel.getStateRequest());
        assertNull(fixture.clientModel.getLastError());
        assertEquals(GameState.PICKCARD, fixture.clientModel.getGameView().getState());
    }

    @Test
    void pickCardTest_EventResolve() {
        Fixture fixture = preparedUpperOnlyRound(false, false, true);

        finishStandardUpperOnlyRound(fixture);

        assertEquals("event resolved", fixture.clientModel.getStateRequest());
        assertNull(fixture.clientModel.getLastError());
        assertEquals(GameState.PLACETOTEM, fixture.clientModel.getGameView().getState());
        assertFalse(fixture.clientModel.getGameView().getResolveEvents().isEmpty());
    }

    @Test
    void pickCardTest_EndGameResolve() {
        Fixture fixture = preparedUpperOnlyRound(false, true, false);

        finishStandardUpperOnlyRound(fixture);

        assertEquals("Game ended", fixture.clientModel.getStateRequest());
        assertTrue(fixture.clientModel.isGameEnded());
        assertNotNull(fixture.clientModel.getEndGameResultView());
        assertEquals(GameState.ENDED, fixture.clientModel.getGameView().getState());
    }

    // PICK SPECIAL TEST

    @Test
    void pickSpecialTest_GameDoesNotExist() {
        GameController gameController = new GameController();
        ClientModel clientModel = new ClientModel();

        gameController.pickSpecial("p1", 1).update(clientModel);

        assertEquals("No game created.", clientModel.getLastError());
    }

    @Test
    void pickSpecialTest_EventResolve() {
        Fixture fixture = preparedUpperOnlyRound(true, false, true);

        finishStandardUpperOnlyRound(fixture);
        pickSpecialUpperCard(fixture);

        assertEquals("event resolved", fixture.clientModel.getStateRequest());
        assertNull(fixture.clientModel.getLastError());
        assertEquals(GameState.PLACETOTEM, fixture.clientModel.getGameView().getState());
        assertFalse(fixture.clientModel.getGameView().getResolveEvents().isEmpty());
    }

    @Test
    void pickSpecialTest_EndGameResolve() {
        Fixture fixture = preparedUpperOnlyRound(true, true, false);

        finishStandardUpperOnlyRound(fixture);
        pickSpecialUpperCard(fixture);

        assertEquals("Game ended.", fixture.clientModel.getStateRequest());
        assertTrue(fixture.clientModel.isGameEnded());
        assertNotNull(fixture.clientModel.getEndGameResultView());
        assertEquals(GameState.ENDED, fixture.clientModel.getGameView().getState());
    }

    @Test
    void pickSpecialTest_CatchException() {
        Fixture fixture = startedTwoPlayerGame();

        fixture.controller.pickSpecial("p1", 1).update(fixture.clientModel);

        assertEquals("Can't activate special Pick from building effect", fixture.clientModel.getLastError());
    }

    // HELPER

    private Fixture startedTwoPlayerGame() {
        GameController controller = new GameController();
        ClientModel clientModel = new ClientModel();

        controller.createGame("p1", "blue", 2).update(clientModel);
        controller.joinGame("p2", "yellow").update(clientModel);

        return new Fixture(controller, clientModel);
    }

    private Fixture preparedUpperOnlyRound(
            boolean ownerHasBuilding13,
            boolean endGameRound,
            boolean lowerEventToResolve
    ) {
        Fixture fixture = startedTwoPlayerGame();
        Game game = controllerGame(fixture.controller);

        clearRows(game);
        addUpperHunters(game, ownerHasBuilding13 ? 4 : 3);

        if (lowerEventToResolve) {
            game.getSharedBoard().getLowerRow().addEventCard(new HuntEventCard(85, 1, 1));
        }

        if (endGameRound) {
            game.setCountRound(10);
        }

        if (ownerHasBuilding13) {
            String ownerNickname = game.getCurrentPlayer();
            Player owner = playerByName(game, ownerNickname);
            owner.addFood(99);
            owner.addTribeCard(new BuildingCard(116, 3, 9, 3, BuildingType.BUILDING13, null, 0));
        }

        fixture.clientModel.setGameView(game.toView());

        String currentPlayer = fixture.clientModel.getGameView().getCurrentPlayer();
        fixture.controller.placeTotem(currentPlayer, 1).update(fixture.clientModel);

        currentPlayer = fixture.clientModel.getGameView().getCurrentPlayer();
        fixture.controller.placeTotem(currentPlayer, 3).update(fixture.clientModel);

        assertEquals(GameState.PICKCARD, fixture.clientModel.getGameView().getState());

        return fixture;
    }

    private void finishStandardUpperOnlyRound(Fixture fixture) {
        pickOneUpperCard(fixture);
        pickOneUpperCard(fixture);
        pickOneUpperCard(fixture);
    }

    private void pickOneUpperCard(Fixture fixture) {
        String currentPlayer = fixture.clientModel.getGameView().getCurrentPlayer();
        int cardId = firstUpperCharacterCard(fixture.clientModel);

        MessageToClient message = fixture.controller.pickCard(currentPlayer, cardId);
        message.update(fixture.clientModel);
    }

    private void pickSpecialUpperCard(Fixture fixture) {
        assertEquals(GameState.PICKSPECIAL, fixture.clientModel.getGameView().getState());

        String currentPlayer = fixture.clientModel.getGameView().getCurrentPlayer();
        int cardId = firstUpperCharacterCard(fixture.clientModel);

        fixture.controller.pickSpecial(currentPlayer, cardId).update(fixture.clientModel);
    }

    private int firstUpperCharacterCard(ClientModel clientModel) {
        return clientModel.getGameView()
                .getBoard()
                .getUpperRow()
                .stream()
                .map(CardView::getId)
                .filter(id -> id >= 1 && id < 85)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected at least one character card in upper row"));
    }

    private Game controllerGame(GameController controller) {
        try {
            Field gameModelField = GameController.class.getDeclaredField("gameModel");
            gameModelField.setAccessible(true);
            return (Game) gameModelField.get(controller);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Cannot access controller game model for deterministic test setup", e);
        }
    }

    private void clearRows(Game game) {
        for (Row row : List.of(
                game.getSharedBoard().getUpperRow(),
                game.getSharedBoard().getLowerRow()
        )) {
            row.clearRoundEnd();
            row.clearBuildingCards();
        }
    }

    private void addUpperHunters(Game game, int amount) {
        for (int i = 0; i < amount; i++) {
            game.getSharedBoard()
                    .getUpperRow()
                    .addCharacterCard(new Hunter(i + 1, false, 1));
        }
    }

    private Player playerByName(Game game, String nickname) {
        return game.getPlayers()
                .stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Player not found: " + nickname));
    }

    private static class Fixture {
        private final GameController controller;
        private final ClientModel clientModel;

        private Fixture(GameController controller, ClientModel clientModel) {
            this.controller = controller;
            this.clientModel = clientModel;
        }
    }
}
