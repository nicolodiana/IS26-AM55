package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BiddingTrailTest {

    /**
     * Tests that an {@link IllegalStateException} is thrown when attempting to retrieve
     * the first player for the second phase while the trail is completely empty.
     */
    @Test
    void testGetFirstPlayerSecondPhaseException(){
        BiddingTrail biddingTrail = new BiddingTrail();
        assertThrows(IllegalStateException.class, biddingTrail::getFirstPlayerSecondPhase);
    }

    /**
     * Tests that an {@link IndexOutOfBoundsException} is thrown when attempting to place
     * a player at negative or out-of-bounds indices on the initialized bidding trail.
     */
    @Test
    void testSetPlayerException(){
        BiddingTrail biddingTrail = new BiddingTrail();
        biddingTrail.initBiddingTrail(2);
        Player p1 = new Player("Player1","black","path");
        assertThrows(IndexOutOfBoundsException.class, ()->biddingTrail.setPlayer(-1,p1));
        assertThrows(IndexOutOfBoundsException.class, ()->biddingTrail.setPlayer(4,p1));
    }

    /**
     * Tests that a {@link BiddingTicketIsTaken} exception is thrown when attempting
     * to assign a player to a bidding ticket that is already occupied by another player.
     */
    @Test
    void testSetPlayerOnBiddingTicket(){
        BiddingTrail biddingTrail = new BiddingTrail();
        biddingTrail.initBiddingTrail(2);
        Player p1 = new Player("Player1","black","path");
        biddingTrail.setPlayer(1,p1);
        Player p2 =new Player("Player2","white","path");
        assertThrows(BiddingTicketIsTaken.class, ()->biddingTrail.setPlayer(1,p2));
    }

    /**
     * Tests that the {@link BiddingTrail#getFirstPlayerSecondPhase()} method
     * correctly identifies and returns the player located furthest to the left (lowest index)
     * on the bidding trail.
     */
    @Test
    void testGetFirstPlayerSecondPhaseFurtherLeft(){
        BiddingTrail trail = new BiddingTrail();
        trail.initBiddingTrail(2);
        Player p1 = new Player("Player1","black","path");
        Player p2 = new Player("Player2","white","path");
        trail.setPlayer(0,p1);
        trail.setPlayer(1,p2);
        assertSame(p1, trail.getFirstPlayerSecondPhase());
    }

    /**
     * Tests the {@link BiddingTrail#nextPlayerSecondPhase(Player)} method to ensure
     * it correctly returns an Optional containing the next sequential player on the trail,
     * or an empty Optional if the current player is the last one.
     */
    @Test
    void testGetNextPlayerSecondPhase(){
        BiddingTrail trail = new BiddingTrail();
        trail.initBiddingTrail(2);
        Player p1 = new Player("Player1","black","path");
        Player p2 = new Player("Player2","white","path");
        trail.setPlayer(0,p1);
        trail.setPlayer(1,p2);
        assertEquals(p2, trail.nextPlayerSecondPhase(p1).get());
        assertEquals(Optional.empty(), trail.nextPlayerSecondPhase(p2));
    }

    /**
     * Tests the boundary limits of the bidding trail after initialization.
     * Verifies that valid max indices return expected states, while exceeding the
     * available size throws an {@link IndexOutOfBoundsException}.
     */
    @Test
    void testInitBiddingTrailSizeViaBounds() {
        BiddingTrail biddingTrail = new BiddingTrail();
        biddingTrail.initBiddingTrail(2);

        assertFalse(biddingTrail.getIsTaken(3));
        assertThrows(IndexOutOfBoundsException.class, () -> biddingTrail.getIsTaken(4));
    }

    /**
     * Tests the {@link BiddingTrail#getIsTaken(int)} method to verify that a bidding ticket's
     * state correctly updates from available to taken after a player is placed on it.
     */
    @Test
    void testBiddingTicketIsTaken(){
        BiddingTrail biddingTrail = new BiddingTrail();
        biddingTrail.initBiddingTrail(2);
        Player p1 = new Player("Player1","black","path");
        assertFalse(biddingTrail.getIsTaken(0));
        biddingTrail.setPlayer(0,p1);
        assertTrue(biddingTrail.getIsTaken(0));
    }

    /**
     * Tests the {@link BiddingTrail#removePlayer(Player)} method to ensure it correctly
     * frees the ticket previously occupied by the player, and that the player's position
     * can no longer be found.
     */
    @Test
    void testRemovePlayer(){
        BiddingTrail biddingTrail = new BiddingTrail();
        Player p1 = new Player("Player1","black","path");

        biddingTrail.initBiddingTrail(2);
        biddingTrail.setPlayer(0,p1);
        biddingTrail.removePlayer(p1);
        assertFalse(biddingTrail.getIsTaken(0));

        // Note: This relies on the current while(true) implementation in getPlayerPositionOnTrail.
        // If refactored, this assertion should be updated (e.g., to assert -1).
        assertThrows(IndexOutOfBoundsException.class, () -> {
            biddingTrail.getPlayerPositionOnTrail(p1);
        });
    }

    /**
     * Tests the {@link BiddingTrail#getPlayerPositionOnTrail(Player)} method to confirm
     * it accurately retrieves the index of a successfully placed player.
     */
    @Test
    void testPlayerPosition(){
        BiddingTrail biddingTrail = new BiddingTrail();
        biddingTrail.initBiddingTrail(2);
        Player p1 = new Player("Player1","black","path");
        biddingTrail.setPlayer(0,p1);
        assertEquals(0, biddingTrail.getPlayerPositionOnTrail(p1));
    }

    /**
     * Tests {@link BiddingTrail#getChooseLowerCard(Player)} and {@link BiddingTrail#getChooseUpperCard(Player)}.
     * Verifies that players receive the correct number of upper and lower card choices
     * depending on the specific properties of the bidding ticket they occupy.
     */
    @Test
    void testChooseUpperLowerCards(){
        BiddingTrail biddingTrail = new BiddingTrail();
        biddingTrail.initBiddingTrail(5);
        Player p1 = new Player("Player1","black","path");
        Player p2 = new Player("Player2","white","path");
        Player p3 = new Player("Player3","red","path");
        Player p4 = new Player("Player4","yellow","path");
        Player p5 = new Player("Player5","blue","path");

        biddingTrail.setPlayer(0,p1);
        biddingTrail.setPlayer(1,p2);
        biddingTrail.setPlayer(2,p3);
        biddingTrail.setPlayer(3,p4);
        biddingTrail.setPlayer(6,p5);

        assertEquals(0, biddingTrail.getChooseLowerCard(p1));
        assertEquals(0, biddingTrail.getChooseUpperCard(p1));

        assertEquals(1, biddingTrail.getChooseLowerCard(p2));
        assertEquals(0, biddingTrail.getChooseUpperCard(p2));

        assertEquals(0, biddingTrail.getChooseLowerCard(p3));
        assertEquals(1, biddingTrail.getChooseUpperCard(p3));

        assertEquals(2, biddingTrail.getChooseLowerCard(p4));
        assertEquals(0, biddingTrail.getChooseUpperCard(p4));

        assertEquals(1, biddingTrail.getChooseLowerCard(p5));
        assertEquals(2, biddingTrail.getChooseUpperCard(p5));
    }
}