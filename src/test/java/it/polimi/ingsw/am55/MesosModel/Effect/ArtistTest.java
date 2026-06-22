package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.*;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.*;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArtistTest {

    @Test
    void addToPlayerAddsArtistAndCountSameTypeReflectsPlayerState() {
        Player player = new Player("Player1", "red");
        Card first = new Artist(1, 1);
        Card second = new Artist(2, 1);

        first.addToPlayer(player);
        second.addToPlayer(player);

        assertEquals(2, player.getArtistsList().size(),
                "Expected 2 artists in player's list, after execution got " + player.getArtistsList().size());
        assertEquals(2, player.countByType(CharacterType.ARTIST),
                "Expected countSameTypeIn to return 2, after execution got " + player.countByType(CharacterType.ARTIST));
    }
}
