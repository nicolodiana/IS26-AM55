package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.server.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

/**
 * Command that asks the server to create a new game from a lobby client.
 */
public class CreateGameCommand implements ServerCommand {

    /**
     * Serialization identifier used when this command crosses RMI or socket object streams.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Requested player id for the client creating the game.
     */
    private final String playerId;
    /**
     * Totem color selected by the player during game creation.
     */
    private final String totemColor;
    /**
     * Number of players requested for the new game.
     */
    private final int numPlayers;
    /**
     * Temporary lobby session id used to move the client from the lobby registry to the game registry if creation succeeds.
     */
    private final String sessionId;

    /**
     * Creates a game-creation command.
     *
     * @param playerId   requested player identifier
     * @param totemColor selected totem color
     * @param numPlayers requested number of players
     * @param sessionId  temporary lobby session identifier
     */
    public CreateGameCommand(String playerId, String totemColor, int numPlayers, String sessionId) {
        this.playerId = playerId;
        this.totemColor = totemColor;
        this.numPlayers = numPlayers;
        this.sessionId = sessionId;
    }

    /**
     * Requires locking because game creation mutates shared game and lobby state.
     *
     * @return always {@code true}
     */
    @Override
    public boolean requiresLock() {
        return true;
    }

    /**
     * Delegates game creation to the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if game creation or response delivery fails
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.createGame(playerId, totemColor, numPlayers, sessionId);
    }
}
