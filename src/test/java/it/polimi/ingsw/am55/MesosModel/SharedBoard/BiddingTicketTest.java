package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Exceptions.BiddingTicketIsTaken;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BiddingTicketTest {
    /**
     * Tests that a {@link BiddingTicketIsTaken} exception is thrown
     * when a player attempts to take a bidding ticket that is already occupied.
     */
    @Test
    void testException() {
        BiddingTicket t1 = new BiddingTicket(3,0,0,2,'A');
        Player p1 = new Player("Player1","black");

        t1.setPlayer(p1);
        Player p2 = new Player("Player2","white");
        assertThrows(BiddingTicketIsTaken.class, () -> t1.setPlayer(p2));
    }

    /**
     * Tests that the {@link BiddingTicket} constructor correctly initializes all fields
     * with the provided arguments and sets the default states (not taken and no assigned player).
     */
    @Test
    public void constructorTest_shouldInitializeFields() {
        BiddingTicket t1 = new BiddingTicket(3,0,0,2,'A');

        assertEquals(3, t1.getFoodBonus());
        assertEquals(0, t1.getChooseLowerCard());
        assertEquals(0, t1.getChooseUpperCard());
        assertEquals(2, t1.getNumPlayer());
        assertEquals('A', t1.getTrailPlacement());
        assertFalse(t1.getIsTaken());
        assertNull(t1.getPlayer());
    }

    /**
     * Tests the {@link BiddingTicket#setPlayer(Player)} method for a valid assignment.
     * Verifies that the player reference is correctly saved and the ticket's state
     * changes to taken.
     */
    @Test
    public void setPlayer_shouldAssignPlayerAndMarkTaken(){
        BiddingTicket t1 = new BiddingTicket(3,0,0,2,'A');
        Player p1 = new Player("Player1","black");

        assertNull(t1.getPlayer());

        t1.setPlayer(p1);

        assertSame(p1, t1.getPlayer());
        assertTrue(t1.getIsTaken());
    }

    /**
     * Tests the {@link BiddingTicket#removePlayer()} method.
     * Verifies that invoking this method clears the assigned player reference
     * and resets the ticket's state to available (not taken).
     */
    @Test
    public void removePlayer_shouldClearPlayerAndFreeTicket(){
        Player p1 = new Player("Player1","black");
        BiddingTicket t1 = new BiddingTicket(3,0,0,2,'A');

        t1.setPlayer(p1);
        assertTrue(t1.getIsTaken());
        assertSame(p1, t1.getPlayer());

        t1.removePlayer();

        assertNull(t1.getPlayer());
        assertFalse(t1.getIsTaken());
    }

}