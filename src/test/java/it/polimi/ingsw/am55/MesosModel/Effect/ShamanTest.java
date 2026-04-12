package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShamanTest {
    //stesso test degli artisti per check override+ check del getter delle stelle
    @Test
    void shamanStoresStarsAndAddsItselfToPlayer() {
        Player player = new Player("Player1", "red", "summary");
        Shaman shaman = new Shaman(1, 4, 1);
        Shaman second = new Shaman(2, 1, 1);

        shaman.addToPlayer(player);
        second.addToPlayer(player);

        assertEquals(4, shaman.getNumStars());
        assertEquals(2, player.getShamansList().size());
        assertEquals(2, player.countByType(CharacterType.SHAMAN));
    }
}
