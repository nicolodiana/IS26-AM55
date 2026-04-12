package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShamanRitualEventCardTest {
    //check effetti rituale sciamanico
    @Test
    void activateEventReturnsImmediatelyOnNullOrEmptyLists() {
        ShamanRitualEventCard card = new ShamanRitualEventCard(1, 1 ,4, 2);
        card.activateEvent(null);
        card.activateEvent(new ArrayList<>());
    }

    @Test
    void activateEventRewardsMaximumPunishesMinimumAndAppliesBuildingModifiers() {
        Player middle = new Player("middle", "red");
        middle.addTribeCard(new Shaman(2,2, 1));
        middle.addPP(10);

        Player maximum = new Player("maximum", "red");
        maximum.addPP(10);
        maximum.addTribeCard(new Shaman(3,5, 1));
        maximum.addTribeCard(new BuildingCard(4, 1, 0, 0, BuildingType.BUILDING7, null, 0));

        Player minimum = new Player("minimum", "red");
        minimum.addPP(10);
        minimum.addTribeCard(new Shaman(5,1, 1));
//caso neutralizzata perdita x edificio 3
        Player protectedMinimum = new Player("protectedMinimum", "red");
        protectedMinimum.addPP(10);
        protectedMinimum.addTribeCard(new Shaman(6,1, 1));
        protectedMinimum.addTribeCard(new BuildingCard(0, 0, 0, 0, BuildingType.BUILDING3, null, 0));
//per controllare i casi di n massimi o n minimi:
        Player doubleminimum = new Player("DoubleMinimum", "red");
        doubleminimum.addPP(10);
        doubleminimum.addTribeCard(new Shaman(7, 1, 1));

        Player doublemax = new Player("DoubleMax", "red");
        doublemax.addPP(10);
        doublemax.addTribeCard(new Shaman(8, 5, 1));

        new ShamanRitualEventCard(1, 1, 4, 2).activateEvent(List.of(middle, maximum, minimum, protectedMinimum,doubleminimum,doublemax));

        assertEquals(10, middle.getNumPP());
        assertEquals(18, maximum.getNumPP());
        assertEquals(14, doublemax.getNumPP());
        assertEquals(8, minimum.getNumPP());
        assertEquals(8, doubleminimum.getNumPP());
        assertEquals(10, protectedMinimum.getNumPP());
    }
}
