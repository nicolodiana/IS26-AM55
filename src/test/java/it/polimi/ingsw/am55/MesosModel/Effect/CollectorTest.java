package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectorTest {
    @Test
    void addToPlayerAddsCollector() {
        Player player = new Player("Player 1", "red");
        Collector collector = new Collector(1, 1);

        collector.addToPlayer(player);

        assertEquals(1, player.countByType(CharacterType.COLLECTOR));
        assertEquals(1, player.playerDeckSize());
    }
}
