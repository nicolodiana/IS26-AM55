package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.*;
import it.polimi.ingsw.am55.MesosModel.Player.*;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtistTest {
//testo l'override di add to player e del count same type istanziando un player e passandogli 2
// personaggi con tipo statico card e dinamico artisti, mi aspetto che la lista artisti del player 1 sia di lunghezza 2
 //e il conteggio della carta sul suo tipo dinamico sia 2
    @Test
    void addToPlayerAddsArtistAndCountSameTypeReflectsPlayerState() {
        Player player = new Player("Player1", "red", "summary");
        Card first = new Artist(1, 1);
        Card second = new Artist(2, 1);

        first.addToPlayer(player);
        second.addToPlayer(player);

        assertEquals(2, player.getArtistsList().size(),
                "Expected 2 artists in player's list, after execution got " + player.getArtistsList().size());
        assertEquals(2, ((Artist) first).countSameTypeIn(player),
                "Expected countSameTypeIn to return 2, after execution got " + ((Artist) first).countSameTypeIn(player));
    }
}
