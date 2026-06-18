package it.polimi.ingsw.am55.network.command;


import it.polimi.ingsw.am55.network.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

/**
 * Technical heartbeat command sent periodically by a client to prove it is alive.
 */
public class PingCommand implements ServerCommand{

    /**
     * Serialization identifier used when this command crosses RMI or socket object streams.
     */
    private static final long serialVersionUID = 1L;


    /**
     * Does not require the game lock because heartbeat handling does not mutate game state.
     *
     * @return always {@code false}
     */
    @Override
    public boolean requiresLock() {
        return false;
    }

    /**
     * Updates the server-side heartbeat timestamp for the sender.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the ping
     * @throws Exception if the pong response cannot be delivered
     */
    @Override
    public void execute(ServerApplication serverApplication, VirtualView sender) throws Exception {
        serverApplication.ping(sender);
    }
}
