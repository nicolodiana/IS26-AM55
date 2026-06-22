package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SustenanceEventCardTest {

    @Test
    void activateEventStandard() {

        Player enoughFood = new Player("enoughFood", "totem1");
        enoughFood.addPP(5);
        enoughFood.addFood(10);
        enoughFood.addTribeCard(new Hunter(1, false, 0));
        enoughFood.addTribeCard(new Artist(2, 0));
        enoughFood.addTribeCard(new Builder(3, 0, 0, 0));
        enoughFood.addTribeCard(new Collector(4, 0));

        Player withBuilding2Penalty = new Player("withBuilding2Penalty", "totem2");
        withBuilding2Penalty.addPP(10);
        withBuilding2Penalty.addFood(2);
        withBuilding2Penalty.addTribeCard(new Hunter(5, false, 0));
        withBuilding2Penalty.addTribeCard(new Hunter(6, false, 0));
        withBuilding2Penalty.addTribeCard(new Hunter(7, false, 0));
        withBuilding2Penalty.addTribeCard(new Artist(8, 0));
        withBuilding2Penalty.getBuildings().add(
                new BuildingCard(9, 0, 0, 0, BuildingType.BUILDING2, CharacterType.HUNTER, 0)
        );

        Player freeDueToDiscounts = new Player("freeDueToDiscounts", "totem3");
        freeDueToDiscounts.addPP(7);
        freeDueToDiscounts.addFood(9);
        freeDueToDiscounts.addTribeCard(new Collector(10, 0));
        freeDueToDiscounts.addTribeCard(new Builder(11, 0, 0, 0));


        SustenanceEventCard card = new SustenanceEventCard(12, 1, 2);
        card.activateEvent(List.of(enoughFood, withBuilding2Penalty, freeDueToDiscounts));

        assertEquals(9, enoughFood.getNumFoods());
        assertEquals(5, enoughFood.getNumPP());

        assertEquals(1, withBuilding2Penalty.getNumFoods());
        assertEquals(10, withBuilding2Penalty.getNumPP());

        assertEquals(9, freeDueToDiscounts.getNumFoods());
        assertEquals(7, freeDueToDiscounts.getNumPP());
    }

    @Test
    void activateEventShouldReducePPWhenPlayerCannotPayEnoughFood() {

        Player poor = new Player("poor", "totem4");
        poor.addPP(10);
        poor.addFood(1);

        poor.addTribeCard(new Shaman(13, 1, 0));
        poor.addTribeCard(new Hunter(14, false, 0));
        poor.addTribeCard(new Artist(15, 0));
        poor.addTribeCard(new Collector(16, 0));


        poor.addTribeCard(new Builder(17, 0, 0, 0));
        poor.addTribeCard(new Inventor("tool", 18, 0));


        SustenanceEventCard card = new SustenanceEventCard(19, 1, 2);
        card.activateEvent(List.of(poor));

        assertEquals(0, poor.getNumFoods());
        assertEquals(6, poor.getNumPP());
    }

    @Test
    void activateEventShouldSumDiscountFromMultipleBuilding2Cards() {

        Player player = new Player("multiDiscount", "totem5");
        player.addPP(8);
        player.addFood(5);

        player.addTribeCard(new Hunter(20, false, 0));
        player.addTribeCard(new Hunter(21, false, 0));
        player.addTribeCard(new Builder(22, 0, 0, 0));
        player.addTribeCard(new Builder(23, 0, 0, 0));
        player.addTribeCard(new Artist(24, 0));

        player.addTribeCard(
                new BuildingCard(25, 0, 0, 0, BuildingType.BUILDING2, CharacterType.HUNTER, 0)
        );
        player.addTribeCard(
                new BuildingCard(26, 0, 0, 0, BuildingType.BUILDING2, CharacterType.BUILDER, 0)
        );

        SustenanceEventCard card = new SustenanceEventCard(27, 1, 3);
        card.activateEvent(List.of(player));

        assertEquals(4, player.getNumFoods());
        assertEquals(8, player.getNumPP());
    }
}