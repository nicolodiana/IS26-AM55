package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.server.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

/**
 * Command that asks the server to add a lobby client to an existing game.
 */
public class JoinGameCommand implements ServerCommand {

    /**
     * Serialization identifier used when this command crosses RMI or socket object streams.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Requested player id for the client joining the game.
     */
    private final String playerId;
    /**
     * Totem color selected by the joining player.
     */
    private final String totemColor;
    /**
     * Temporary lobby session id used to complete the network registration if joining succeeds.
     */
    private final String sessionId;
    /**
     * Creates a game-join command.
     *
     * @param playerId   requested player identifier
     * @param totemColor selected totem color
     * @param sessionId  temporary lobby session identifier
     */
    public JoinGameCommand(String playerId, String totemColor, String sessionId) {
        this.playerId = playerId;
        this.totemColor = totemColor;
        this.sessionId = sessionId;
    }

    /**
     * Requires locking because joining a game mutates shared game and lobby state.
     *
     * @return always {@code true}
     */
    @Override
    public boolean requiresLock() {
        return true;
    }

    /**
     * Delegates game joining to the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if joining or response delivery fails
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.joinGame(playerId, totemColor, sessionId);
    }
}
