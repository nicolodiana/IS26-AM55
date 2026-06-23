package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.server.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

/**
 * Command that asks the server to apply a special-card pick action.
 */
public class PickSpecialCommand implements ServerCommand {

    /**
     * Serialization identifier used when this command crosses RMI or socket object streams.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Player id of the client requesting the special-card pick.
     */
    private final String playerId;
    /**
     * Identifier of the special card selected by the player.
     */
    private final int cardId;

    /**
     * Creates a special-card pick command.
     *
     * @param playerId player requesting the action
     * @param cardId   selected special-card identifier
     */
    public PickSpecialCommand(String playerId, int cardId) {
        this.playerId = playerId;
        this.cardId = cardId;
    }
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
     * Delegates special-card picking to the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if the action fails
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.pickSpecial(playerId, cardId);
    }
}