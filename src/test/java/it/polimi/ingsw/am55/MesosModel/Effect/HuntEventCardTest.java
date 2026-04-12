package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HuntEventCardTest {
    //test per esclusivamente l'evento caccia
    @Test
    void activateEventAwardsPointsAndFoodWithAndWithoutBuilding8() {
        Player boosted = new Player("testhuntevent", "red", "summary");
        boosted.getHuntersList().add(new Hunter(1,false, 1));
        boosted.getHuntersList().add(new Hunter(2, false, 1));
        boosted.getBuildings().add(new BuildingCard(3, 1, 0, 0, BuildingType.BUILDING8, null, 0));

        Player normal = new Player("normal", "green", "normal summary");
        normal.getHuntersList().add(new Hunter(4,false, 1));

        new HuntEventCard(5,1,3).activateEvent(List.of(boosted, normal));

        assertEquals(4, boosted.getNumFoods());
        assertEquals(8, boosted.getNumPP());
        assertEquals(1, normal.getNumFoods());
        assertEquals(3, normal.getNumPP());
    }
}
