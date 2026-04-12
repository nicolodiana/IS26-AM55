package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HunterTest {
    //stesso test specificato per gli artisti
    @Test
    void hunterKeepsIconAndAddsItselfToPlayer() {
        Player player = new Player("hunter", "red", "summary");
        Hunter hunter = new Hunter(1,true, 1);
        Hunter second = new Hunter(2, false, 1);

        hunter.addToPlayer(player);
        second.addToPlayer(player);

        assertTrue(hunter.getIcon());
        assertEquals(2, player.getHuntersList().size());
        assertEquals(2, player.countByType(CharacterType.HUNTER));
    }
}
