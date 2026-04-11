package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Player.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//checko ovverride + getter della builderCard
class BuilderTest {

    @Test
    void builderExposesValuesAndAddsItselfToPlayer() {
        Player player = new Player("builder", "red", "summary");
        Builder builder = new Builder(1, 7, 2,  1);

        builder.addToPlayer(player);

        assertEquals(7, builder.getNumPP());
        assertEquals(2, builder.getPickbuildingdiscount());
        assertEquals(1, player.getBuildersList().size());
        assertEquals(1, builder.countSameTypeIn(player));
    }
}
