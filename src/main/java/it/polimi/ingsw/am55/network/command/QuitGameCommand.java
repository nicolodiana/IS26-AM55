package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.Server;
import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

/**
 * Command that asks the server to close the active game.
 */
public class QuitGameCommand implements ServerCommand {

    /**
     * Player id of the client requesting to quit the active game.
     */
    private String playerId;

    /**
     * Creates a game-quit command.
     *
     * @param playerId player requesting to quit the game
     */
    public QuitGameCommand(String playerId) {
        this.playerId = playerId;
    }
    /**
     * Requires locking because quitting the game mutates shared game and connection state.
     *
     * @return always {@code true}
     */
    @Override
    public boolean requiresLock() {
        return true;
    }

    /**
     * Delegates game quitting to the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if quit handling fails
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.quitGame(playerId);
    }
}
