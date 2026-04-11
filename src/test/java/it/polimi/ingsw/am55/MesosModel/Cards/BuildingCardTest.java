package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardTest {

    @Test
    void constructorGettersAndAddToPlayerThroughPlayerMethodShouldWork() {
        // Testa che il costruttore inizializzi correttamente i campi
        // e che l'edificio possa essere aggiunto al player pagando il costo corretto.

        Player player = new Player("building", "totem1", "summary1");
        player.addFood(10);

        BuildingCard card = new BuildingCard(
                1,
                0,
                3,
                6,
                BuildingType.BUILDING12,
                CharacterType.HUNTER,
                0
        );

        player.addTribeCard(card);

        assertEquals(7, player.getNumFoods());
        assertEquals(1, player.getBuildings().size());
        assertEquals(6, card.getNumOfPP());
        assertEquals(3, card.getFoodCost());
    }

    @Test
    void getSustenanceDiscountShouldReturnCountOfMatchingTypeForBuilding2() {
        // Testa che BUILDING2 restituisca come sconto
        // il numero di carte del tipo richiesto possedute dal player.

        Player player = new Player("discount", "totem2", "summary2");
        player.addTribeCard(new Hunter(1, false, 0));
        player.addTribeCard(new Hunter(2, false, 0));
        player.addTribeCard(new Hunter(3, true, 0));

        BuildingCard card = new BuildingCard(
                2,
                0,
                4,
                0,
                BuildingType.BUILDING2,
                CharacterType.HUNTER,
                0
        );

        assertEquals(3, card.getSustenanceDiscount(player));
    }

    @Test
    void getSustenanceDiscountShouldReturnZeroIfBuildingIsNotBuilding2() {
        // Testa che il metodo getSustenanceDiscount restituisca 0
        // se l'edificio non è di tipo BUILDING2.

        Player player = new Player("noDiscount", "totem3", "summary3");
        player.addTribeCard(new Hunter(1, false, 0));
        player.addTribeCard(new Hunter(2, false, 0));

        BuildingCard card = new BuildingCard(
                3,
                0,
                4,
                0,
                BuildingType.BUILDING12,
                CharacterType.HUNTER,
                0
        );

        assertEquals(0, card.getSustenanceDiscount(player));
    }

    @Test
    void getSustenanceDiscountShouldReturnZeroIfCharacterTypeIsNull() {
        // Testa che BUILDING2 non generi sconto
        // se il CharacterType associato è null.

        Player player = new Player("nullTypeDiscount", "totem4", "summary4");
        player.addTribeCard(new Hunter(1, false, 0));
        player.addTribeCard(new Hunter(2, false, 0));

        BuildingCard card = new BuildingCard(
                4,
                0,
                4,
                0,
                BuildingType.BUILDING2,
                null,
                0
        );

        assertEquals(0, card.getSustenanceDiscount(player));
    }

    @Test
    void getEndGameBonusShouldReturnCountOfMatchingTypeForBuilding12() {
        // Testa che BUILDING12 restituisca come bonus di fine partita
        // il numero di carte del tipo richiesto possedute dal player.

        Player player = new Player("endBonus", "totem5", "summary5");
        player.addTribeCard(new Builder(1, 3, 0, 0));
        player.addTribeCard(new Builder(2, 4, 0, 0));

        BuildingCard card = new BuildingCard(
                5,
                0,
                2,
                6,
                BuildingType.BUILDING12,
                CharacterType.BUILDER,
                0
        );

        assertEquals(2, card.getEndGameBonus(player));
    }

    @Test
    void getEndGameBonusShouldReturnZeroIfBuildingIsNotBuilding12() {
        // Testa che il bonus di fine partita sia 0
        // se l'edificio non è di tipo BUILDING12.

        Player player = new Player("notEndBonus", "totem6", "summary6");
        player.addTribeCard(new Builder(1, 3, 0, 0));
        player.addTribeCard(new Builder(2, 4, 0, 0));

        BuildingCard card = new BuildingCard(
                6,
                0,
                2,
                6,
                BuildingType.BUILDING2,
                CharacterType.BUILDER,
                0
        );

        assertEquals(0, card.getEndGameBonus(player));
    }

    @Test
    void getEndGameBonusShouldReturnZeroIfCharacterTypeIsNull() {
        // Testa che BUILDING12 non generi bonus di fine partita
        // se il CharacterType associato è null.

        Player player = new Player("nullTypeBonus", "totem7", "summary7");
        player.addTribeCard(new Builder(1, 3, 0, 0));
        player.addTribeCard(new Builder(2, 4, 0, 0));

        BuildingCard card = new BuildingCard(
                7,
                0,
                2,
                6,
                BuildingType.BUILDING12,
                null,
                0
        );

        assertEquals(0, card.getEndGameBonus(player));
    }
}