package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Player.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventCardTest {
    //controllo che una EventCard generica non abbia effetti ( solo quelle piu specifiche portano effetti)
    @Test
    void baseEventCardDoesNothing() {
        Player player = new Player("testEvent", "red");
        player.addFood(6);
        player.addPP(7);
        new EventCard(1, 1).activateEvent(List.of(player));

        assertEquals(6, player.getNumFoods());
        assertEquals(7, player.getNumPP());
    }
}
