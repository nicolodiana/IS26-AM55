package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaintingsEventCardTest {

    // test per pitture rupestri:
    // - player con artisti = upperNumberOfArtist -> perde PP
    // - player con artisti = lowerNumberOfArtist -> guadagna PP
    // - player con BUILDING10 -> guadagna cibo pari agli artisti, ma solo se ha almeno minArtist
    // - player con artisti < minArtist -> nessun effetto
    @Test
    void constructorsExistAndActivateEventAppliesEffects() {

        Player upper = new Player("upper", "orange", "orage summary");
        upper.addPP(10);
        upper.addTribeCard(new Artist(1, 1));
        upper.addTribeCard(new Artist(2, 1));
        upper.addTribeCard(new BuildingCard(0, 0, 0, 0, BuildingType.BUILDING10, null, 0));

        Player lower = new Player("lower", "green", "green summary");
        lower.addTribeCard(new Artist(3, 1));

        Player neutral = new Player("neutral", "yellow", "yellow summary");
        neutral.addTribeCard(new Artist(4, 1));
        neutral.addTribeCard(new Artist(5, 1));
        neutral.addTribeCard(new Artist(6, 1));

        Player belowMinimum = new Player("belowMinimum", "red", "red summary");
        belowMinimum.addPP(5);
        // 0 artisti, quindi sotto il minimo richiesto

        PaintingsEventCard card = new PaintingsEventCard(7, 1,4, 3, 2, 1, 1);
        card.activateEvent(List.of(upper, lower, neutral, belowMinimum));

        assertEquals(2, upper.getNumFoods(), "upper has BUILDING10 and 2 artists, so should gain 2 food");
        assertEquals(6, upper.getNumPP(), "upper has 2 artists, so should lose 4 PP");

        assertEquals(3, lower.getNumPP(), "lower has 1 artist, so should gain 1 * 3 PP");
        assertEquals(0, lower.getNumFoods
                (), "lower has no BUILDING10, so food should not change");

        assertEquals(0, neutral.getNumFoods(), "neutral has no BUILDING10, so food should not change");
        assertEquals(0, neutral.getNumPP(), "neutral matches neither upper nor lower threshold");

        assertEquals(0, belowMinimum.getNumFoods(), "player below minimum artists should receive no food");
        assertEquals(5, belowMinimum.getNumPP(), "player below minimum artists should receive no PP changes");
    }
}