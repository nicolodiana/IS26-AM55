package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Player.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectorTest {
    //stesso test degli artisti per check override
    @Test
    void addToPlayerAddsCollector() {
        Player player = new Player("Player 1", "red", "summary");
        Collector collector = new Collector(1, 1);

        collector.addToPlayer(player);

        assertEquals(1, player.getCollectorsList().size());
        assertEquals(1, player.playerDeckSize());
    }
}
