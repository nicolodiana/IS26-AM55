package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for ThreePlayerEffect.
 * Verifies the correct distribution of food bonuses for players
 * in a 3-player game, accounting for the presence of specific building cards.
 */
class ThreePlayersEffectTest {
    /**
     * Tests food assignment for a player who owns a valid building.

     * Expected result: The player receives 3 food tokens.
     */
    @Test
    void testAddFoodWithBulding(){
        Player player = new Player("Player1", "black");
        BuildingCard card= new BuildingCard(104, 1, 3, 3, BuildingType.BUILDING4, null, 0);
        player.addFood(3);
        player.addTribeCard(card);
        ThreePlayersEffect threePlayersEffect = new ThreePlayersEffect();
        assertEquals(0,player.getNumFoods());
        threePlayersEffect.applyFood(player,0);
        assertEquals(3,player.getNumFoods());
    }
    /**
     * Tests food assignment for a player without a valid building.
     * <p>
     * Expected result: The player receives 2 food tokens.
     */
    @Test
    void testAddFoodWithoutBulding(){
        Player player = new Player("Player1", "black");
        ThreePlayersEffect threePlayersEffect = new ThreePlayersEffect();
        assertEquals(0,player.getNumFoods());
        threePlayersEffect.applyFood(player,0);
        assertEquals(2,player.getNumFoods());
    }

}