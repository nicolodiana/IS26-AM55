package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaintingsEventCardTest {

    @Test
    void constructorsExistAndActivateEventAppliesEffects() {

        Player upper = new Player("upper", "orange");
        upper.addPP(10);
        upper.addTribeCard(new Artist(1, 1));
        upper.addTribeCard(new BuildingCard(0, 0, 0, 0, BuildingType.BUILDING10, null, 0));

        Player lower = new Player("lower", "green");
        lower.addTribeCard(new Artist(3, 1));
        lower.addTribeCard(new Artist(2, 1));
        lower.addTribeCard(new Artist(11, 1));

        Player neutral = new Player("neutral", "yellow");
        neutral.addTribeCard(new Artist(4, 1));
        neutral.addTribeCard(new Artist(5, 1));


        PaintingsEventCard card = new PaintingsEventCard(7, 1,4, 3, 1, 3);
        card.activateEvent(List.of(upper, lower, neutral));

        assertEquals(1, upper.getNumFoods(), "upper has BUILDING10 and 1 artist, so should gain 2 food");
        assertEquals(6, upper.getNumPP(), "upper has 1 artists, so should lose 4 PP");

        assertEquals(9, lower.getNumPP(), "lower has 3 artists, so should gain 3 * 3 PP");
        assertEquals(0, lower.getNumFoods(), "lower has no BUILDING10, so food should not change");

        assertEquals(0, neutral.getNumFoods(), "neutral has no BUILDING10, so food should not change");
        assertEquals(0, neutral.getNumPP(), "neutral matches neither upper nor lower threshold");

    }
}