package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for FourPlayersEffect.
 * Verifies the correct distribution of food bonuses based on the player's position
 * in the turn order and the presence of specific building cards during a 4-player game.
 */
class FourPlayersEffectTest {
    /**
     * Tests food assignment for a player in the first position (index 0) who owns a valid building.
     * Expected result: The player receives 3 food tokens.
     */
    @Test
    void testAddFoodWithBuldingOnFirstPosition(){
        Player player = new Player("Player1", "black");
        BuildingCard card= new BuildingCard(104, 1, 3, 3, BuildingType.BUILDING4, null, 0);
        player.addFood(3);
        player.addTribeCard(card);
        FourPlayersEffect fourPlayersEffect = new FourPlayersEffect();
        assertEquals(0,player.getNumFoods());
        fourPlayersEffect.applyFood(player,0);
        assertEquals(3,player.getNumFoods());
    }
    /**
     * Tests food assignment for a player in the first position (index 0) without a valid building.
     * Expected result: The player receives 2 food tokens.
     */
    @Test
    void testAddFoodWithoutBuldingOnFirstPosition(){
        Player player = new Player("Player1", "black");
        FourPlayersEffect fourPlayersEffect = new FourPlayersEffect();;
        assertEquals(0,player.getNumFoods());
        fourPlayersEffect.applyFood(player,0);
        assertEquals(2,player.getNumFoods());
    }
    /**
     * Tests food assignment for a player in the second position (index 1) who owns a valid building.
     * Expected result: The player receives 2 food tokens.
     */
    @Test
    void testAddFoodWithBuldingOnSecondPosition(){
        Player player = new Player("Player1", "black");
        BuildingCard card= new BuildingCard(104, 1, 3, 3, BuildingType.BUILDING4, null, 0);
        player.addFood(3);
        player.addTribeCard(card);
        FourPlayersEffect fourPlayersEffect = new FourPlayersEffect();
        assertEquals(0,player.getNumFoods());
        fourPlayersEffect.applyFood(player,1);
        assertEquals(2,player.getNumFoods());
    }
    /**
     * Tests food assignment for a player in the second position (index 1) without a valid building.
     * Expected result: The player receives 1 food token.
     */
    @Test
    void testAddFoodWithoutBuldingOnSecondPosition(){
        Player player = new Player("Player1", "black");
        FourPlayersEffect fourPlayersEffect = new FourPlayersEffect();;
        assertEquals(0,player.getNumFoods());
        fourPlayersEffect.applyFood(player,1);
        assertEquals(1,player.getNumFoods());
    }

}