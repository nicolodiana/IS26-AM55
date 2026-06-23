package it.polimi.ingsw.am55.network.command;


import it.polimi.ingsw.am55.network.server.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

/**
 * Command that asks the server to disconnect one client from the lobby.
 */
public class QuitLobbyCommand implements ServerCommand {
    /**
     * Temporary lobby session id of the client requesting to leave the lobby.
     */
    private String sessionId;

    /**
     * Creates a lobby-quit command.
     *
     * @param sessionId temporary lobby session identifier to disconnect
     */
    public QuitLobbyCommand(String sessionId){
        this.sessionId=sessionId;
    }
    /**
     * Does not require the game lock because it affects only one lobby session.
     *
     * @return always {@code false}
     */
    @Override
    public boolean requiresLock() {
        return false;
    }

    /**
     * Delegates lobby disconnection to the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if lobby disconnection fails
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.quitLobby(this.sessionId);
    }

}
