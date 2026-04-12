package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventorTest {
    //stesso testo specificato per gli artisti
    @Test
    void inventorStoresIconAndAddsItselfToPlayer() {
        Player player = new Player("Player1", "red", "summary");
        Inventor inventor = new Inventor("Wheel", 1, 1);
        Inventor second = new Inventor("Saw", 2, 1);

        inventor.addToPlayer(player);
        second.addToPlayer(player);

        assertEquals("Wheel", inventor.getIconInvention());
        assertEquals(2, player.getInventorsList().size());
        assertEquals(2, player.countByType(CharacterType.INVENTOR));
    }
}
