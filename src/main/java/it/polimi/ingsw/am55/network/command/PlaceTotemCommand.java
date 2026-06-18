package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

/**
 * Command that asks the server to place a player's totem on the trail.
 */
public class PlaceTotemCommand implements ServerCommand {

    /**
     * Serialization identifier used when this command crosses RMI or socket object streams.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Player id of the client requesting the totem placement.
     */
    private final String playerId;
    /**
     * Target trail index where the player wants to place the totem.
     */
    private final int index;

    /**
     * Requires locking because the action mutates shared game state.
     *
     * @return always {@code true}
     */
    @Override
    public boolean requiresLock() {
        return true;
    }
    /**
     * Creates a totem-placement command.
     *
     * @param playerId player requesting the action
     * @param index    target trail position
     */
    public PlaceTotemCommand(String playerId, int index) {
        this.playerId = playerId;
        this.index = index;
    }

    /**
     * Delegates totem placement to the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if the action fails
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.placeTotem(playerId, index);
    }
}