package it.polimi.ingsw.am55.MesosModel.Player;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    /**
     * Tests valid additions and payments for Food.
     */
    @Test
    void testFoodManagement_ValidAmounts() {
        Player player = new Player("tribe", "totem1");
        player.addFood(10);
        assertEquals(10, player.getNumFoods());

        player.payFood(4);
        assertEquals(6, player.getNumFoods());
    }

    /**
     * Tests that negative amounts throw exceptions for Food methods.
     */
    @Test
    void testFoodManagement_NegativeAmountsThrowException() {
        Player player = new Player("tribe", "totem1");
        assertThrows(IllegalArgumentException.class, () -> player.addFood(-1));
        assertThrows(IllegalArgumentException.class, () -> player.payFood(-1));
    }

    /**
     * Tests valid additions and payments for Prestige Points (PP).
     */
    @Test
    void testPPManagement_ValidAmounts() {
        Player player = new Player("tribe", "totem1");
        player.addPP(10);
        assertEquals(10, player.getNumPP());

        player.payPP(4);
        assertEquals(6, player.getNumPP());
    }

    /**
     * Tests that negative amounts throw exceptions for PP methods.
     */
    @Test
    void testPPManagement_NegativeAmountsThrowException() {
        Player player = new Player("tribe", "totem1");
        assertThrows(IllegalArgumentException.class, () -> player.addPP(-1));
        assertThrows(IllegalArgumentException.class, () -> player.payPP(-1));
    }
    @Test
    void addTribeCardForCharactersShouldPopulateAllListsAndApplyHunterInstantEffect() {

        Player player = new Player("tribe", "totem1");

        player.addTribeCard(new Shaman(1, 2, 0));
        player.addTribeCard(new Hunter(2, true, 0));
        player.addTribeCard(new Hunter(3, false, 0));
        player.addTribeCard(new Artist(4, 0));
        player.addTribeCard(new Collector(5, 0));
        player.addTribeCard(new Builder(6, 4, 1, 0));
        player.addTribeCard(new Inventor("clay", 7, 0));

        assertEquals(1, player.getShamansList().size());
        assertEquals(2, player.getHuntersList().size());
        assertEquals(1, player.getArtistsList().size());
        assertEquals(1, player.getCollectorsList().size());
        assertEquals(1, player.getBuildersList().size());
        assertEquals(1, player.getInventorsList().size());
        assertEquals(7, player.playerDeckSize());

        assertEquals(1, player.getNumFoods());
    }

    @Test
    void addHunterWithIconShouldScaleFoodWithCurrentHunterCount() {

        Player player = new Player("hunters", "totem2");

        player.addTribeCard(new Hunter(1, true, 0));
        assertEquals(1, player.getNumFoods());

        player.addTribeCard(new Hunter(2, false, 0));
        assertEquals(1, player.getNumFoods());

        player.addTribeCard(new Hunter(3, true, 0));
        assertEquals(4, player.getNumFoods());
    }

    @Test
    void addInventorWithBuilding5ShouldAwardFoodOnlyWhenANewPairIsCompleted() {

        Player player = new Player("inventors", "totem3");
        player.getBuildings().add(new BuildingCard(1, 0, 0, 0, BuildingType.BUILDING5, null, 0));

        player.addTribeCard(new Inventor("hammer", 2, 0));
        assertEquals(0, player.getNumFoods());

        player.addTribeCard(new Inventor("HAMMER", 3, 0));
        assertEquals(3, player.getNumFoods());

        player.addTribeCard(new Inventor("hammer", 4, 0));
        assertEquals(3, player.getNumFoods());

        player.addTribeCard(new Inventor("HaMmEr", 5, 0));
        assertEquals(6, player.getNumFoods());
    }

    @Test
    void addBuildingCardShouldApplyBuilderDiscountFloorAtZeroAndRejectIfFoodIsInsufficient() {

        Player discounted = new Player("discounted", "totem4");
        discounted.addFood(10);
        discounted.addTribeCard(new Builder(1, 0, 1, 0));
        discounted.addTribeCard(new BuildingCard(2, 0, 5, 0, BuildingType.BUILDING4, null, 0));

        assertEquals(6, discounted.getNumFoods());
        assertTrue(discounted.hasBuilding(BuildingType.BUILDING4));

        Player zeroFloor = new Player("zeroFloor", "totem5");
        zeroFloor.addFood(5);
        zeroFloor.addTribeCard(new Builder(3, 0, 3, 0));
        zeroFloor.addTribeCard(new Builder(4, 0, 4, 0));
        zeroFloor.addTribeCard(new BuildingCard(5, 0, 5, 0, BuildingType.BUILDING8, null, 0));

        assertEquals(5, zeroFloor.getNumFoods());
        assertTrue(zeroFloor.hasBuilding(BuildingType.BUILDING8));

        Player poor = new Player("poor", "totem6");
        poor.addFood(1);
        assertThrows(IllegalArgumentException.class,()->poor.addTribeCard(new BuildingCard(6, 0, 3, 0, BuildingType.BUILDING9, null, 0)));
        assertEquals(1, poor.getNumFoods());
        assertFalse(poor.hasBuilding(BuildingType.BUILDING9));
    }

    @Test
    void addBuilding1ShouldNotRewardPastCompletedSetsButShouldRewardNewOnes() {

        Player player = new Player("building1", "totem7");
        player.addFood(20);

        addCompleteSet(player);
        assertEquals(20, player.getNumFoods());
        assertEquals(1, player.minCardSet());

        player.addTribeCard(new BuildingCard(7, 0, 2, 0, BuildingType.BUILDING1, null, 0));
        assertEquals(18, player.getNumFoods());
        assertEquals(1, player.minCardSet());

        player.addTribeCard(new Shaman(8, 1, 0));
        player.addTribeCard(new Hunter(9, false, 0));
        player.addTribeCard(new Artist(10, 0));
        player.addTribeCard(new Collector(11, 0));
        player.addTribeCard(new Builder(12, 1, 0, 0));

        assertEquals(18, player.getNumFoods());

        player.addTribeCard(new Inventor("second-tool", 13, 0));

        assertEquals(23, player.getNumFoods());
        assertEquals(2, player.minCardSet());
    }

    @Test
    void addBuilding1ShouldRewardEveryNewCompletedSetExactlyOnce() {

        Player player = new Player("multiSet", "totem8");
        player.addFood(10);

        player.addTribeCard(new BuildingCard(14, 0, 2, 0, BuildingType.BUILDING1, null, 0));
        assertEquals(8, player.getNumFoods());

        addCompleteSet(player);
        assertEquals(13, player.getNumFoods());
        assertEquals(1, player.minCardSet());

        player.addTribeCard(new Shaman(15, 1, 0));
        assertEquals(13, player.getNumFoods());

        player.addTribeCard(new Hunter(16, false, 0));
        player.addTribeCard(new Artist(17, 0));
        player.addTribeCard(new Collector(18, 0));
        player.addTribeCard(new Builder(19, 1, 0, 0));
        assertEquals(13, player.getNumFoods());

        player.addTribeCard(new Inventor("tool-2", 20, 0));
        assertEquals(18, player.getNumFoods());
        assertEquals(2, player.minCardSet());
    }

    @Test
    void addShamanShouldAffectCountShamanStarsAndBuilding6Bonus() {

        Player player = new Player("stars", "totem9");

        player.addTribeCard(new Shaman(1, 2, 0));
        player.addTribeCard(new Shaman(2, 1, 0));
        assertEquals(3, player.countShamanStars());

        player.getBuildings().add(new BuildingCard(21, 0, 0, 0, BuildingType.BUILDING6, null, 0));
        assertEquals(6, player.countShamanStars());
    }

    private void addCompleteSet(Player player) {
        player.addTribeCard(new Shaman(100, 1, 0));
        player.addTribeCard(new Hunter(101, false, 0));
        player.addTribeCard(new Artist(102, 0));
        player.addTribeCard(new Collector(103, 0));
        player.addTribeCard(new Builder(104, 1, 0, 0));
        player.addTribeCard(new Inventor("tool-1", 105, 0));
    }

}