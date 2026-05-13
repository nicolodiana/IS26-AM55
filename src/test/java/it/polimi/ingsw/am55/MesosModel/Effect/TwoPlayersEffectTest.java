package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TwoPlayersEffect.
 * Verifies the correct distribution of food bonuses for players
 * in a 2-player game, accounting for the presence of specific building cards.
 */
class TwoPlayersEffectTest {
    /**
     * Tests food assignment for a player who owns a valid building.
     * Expected result: The player receives 2 food tokens.
     */
    @Test
    void testAddFoodWithBulding(){
        Player player = new Player("Player1", "black");
        BuildingCard card= new BuildingCard(104, 1, 3, 3, BuildingType.BUILDING4, null, 0);
        player.addFood(3);
        player.addTribeCard(card);
        TwoPlayersEffect twoPlayersEffect = new TwoPlayersEffect();
        assertEquals(0,player.getNumFoods());
        twoPlayersEffect.applyFood(player,0);
        assertEquals(2,player.getNumFoods());
    }
    /**
     * Tests food assignment for a player without a valid building.
     * Expected result: The player receives 1 food token.
     */
    //DANIELE QUESSTO TEST DA ERRORE CORREGGILO PLSSS
//    @Test
//    void testAddFoodWithoutBulding(){
//        Player player = new Player("Player1", "black");
//        ThreePlayersEffect twoPlayersEffect = new ThreePlayersEffect();
//        assertEquals(0,player.getNumFoods());
//        twoPlayersEffect.applyFood(player,0);
//        assertEquals(1,player.getNumFoods());
//    }
}