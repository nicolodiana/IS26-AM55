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

        Player player = new Player("building", "totem1");
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

        Player player = new Player("discount", "totem2");
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

        Player player = new Player("noDiscount", "totem3");
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


        Player player = new Player("nullTypeDiscount", "totem4");
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


        Player player = new Player("endBonus", "totem5");
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


        Player player = new Player("notEndBonus", "totem6");
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


        Player player = new Player("nullTypeBonus", "totem7");
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