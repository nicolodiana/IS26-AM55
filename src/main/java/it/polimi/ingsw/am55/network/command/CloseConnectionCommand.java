package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.server.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

/**
 * Placeholder command reserved for explicit server-side connection closing.
 * <p>
 * The current server implementation closes connections through quit messages and
 * heartbeat timeout handling, so this command intentionally performs no action.
 */
public class CloseConnectionCommand implements ServerCommand {
    /**
     * Player id whose connection would be closed by this command if explicit close handling is implemented.
     */
    private String playerId;
    /**
     * Creates a close-connection command for the given player.
     *
     * @param playerId player whose connection would be closed by a future implementation
     */
    public CloseConnectionCommand(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Does not require the game lock because no state-changing operation is currently executed.
     *
     * @return always {@code false}
     */
    @Override
    public boolean requiresLock() {
        return false;
    }

    /**
     * Executes the command.
     * <p>
     * This method is intentionally empty in the current implementation.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception reserved for future close-connection implementations
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {

    }
}