package it.polimi.ingsw.am55.MesosModel.Game;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.MesosModel.Exceptions.PlayerNumberOutOfRange;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTestForEndEffect {

    @Test
    void endGameShouldApplyAllEffectsInsideForLoop() throws Exception {
        Game game = new Game(2);

        Player rich = new Player("rich", "totem1", "summary1");
        Player plain = new Player("plain", "totem2", "summary2");

        // rich player
        rich.addTribeCard(new Shaman(1, 1, 0));
        rich.addTribeCard(new Hunter(2, false, 0));
        rich.addTribeCard(new Hunter(3, false, 0));
        rich.addTribeCard(new Hunter(4, false, 0));
        rich.addTribeCard(new Artist(5, 0));
        rich.addTribeCard(new Artist(6, 0));
        rich.addTribeCard(new Artist(7, 0));
        rich.addTribeCard(new Artist(8, 0));
        rich.addTribeCard(new Artist(9, 0));
        rich.addTribeCard(new Collector(10, 0));
        rich.addTribeCard(new Builder(11, 3, 0, 0));
        rich.addTribeCard(new Builder(12, 4, 0, 0));
        rich.addTribeCard(new Inventor("hammer", 13, 0));
        rich.addTribeCard(new Inventor("saw", 14, 0));
        rich.addTribeCard(new Inventor("hammer", 15, 0));

        rich.getBuildings().add(new BuildingCard(16, 1, 0, 0, BuildingType.BUILDING2, CharacterType.HUNTER, 0));
        rich.getBuildings().add(new BuildingCard(17, 1, 0, 0, BuildingType.BUILDING9, null, 0));
        rich.getBuildings().add(new BuildingCard(18, 1, 0, 0, BuildingType.BUILDING11, null, 0));
        rich.getBuildings().add(new BuildingCard(19, 1, 0, 2, BuildingType.BUILDING12, CharacterType.HUNTER, 0));
        rich.getBuildings().add(new BuildingCard(20, 1, 0, 0, BuildingType.BUILDING14, null, 0));

        // plain player
        plain.addTribeCard(new Builder(21, 5, 0, 0));
        plain.addTribeCard(new Inventor("stone", 22, 0));
        plain.addTribeCard(new Inventor("rope", 23, 0));
        plain.addTribeCard(new Artist(24, 0));

        game.addPlayer(rich);
        game.addPlayer(plain);
        game.changeState(GameState.STARTED);

        game.endGame();

        // rich:
        // artists = 5 / 2 * 10 = 20
        // builders = (3 + 4) * 2 = 14
        // inventors = 3 * 2 distinct icons = 6
        // BUILDING11 = minCardSet() * 6 = 1 * 6 = 6
        // BUILDING12 = 3 hunters * 2 PP = 6
        // BUILDING14 = 25
        // total = 77
        assertEquals(77, rich.getNumPP());

        // plain:
        // artists = 1 / 2 * 10 = 0
        // builders = 5
        // inventors = 2 * 2 = 4
        // total = 9
        assertEquals(9, plain.getNumPP());

        assertEquals(GameState.ENDED, game.getGameState());
    }

    @Test
    void endGameShouldNotApplyForLoopEffectsIfGameIsNotStarted() throws PlayerNumberOutOfRange {
        // Testa che gli effetti di fine partita nel ciclo for
        // non vengano applicati se lo stato del game non è STARTED.

        Game game = new Game(1);
        Player player = new Player("p1", "totem1", "summary1");

        player.addTribeCard(new Artist(1, 0));
        player.addTribeCard(new Artist(2, 0));
        player.addTribeCard(new Builder(3, 4, 0, 0));
        player.addTribeCard(new Inventor("tool", 4, 0));

        game.addPlayer(player);
        game.changeState(GameState.CREATED);

        game.endGame();

        // Il for non viene eseguito perché state != STARTED
        assertEquals(0, player.getNumPP());
        assertEquals(GameState.ENDED, game.getGameState());
    }
}