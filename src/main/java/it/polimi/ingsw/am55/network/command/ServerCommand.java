package it.polimi.ingsw.am55.network.command;

import it.polimi.ingsw.am55.network.server.ServerApplication;
import it.polimi.ingsw.am55.virtualview.VirtualView;

import java.io.Serializable;

/**
 * Serializable command sent from a client to the server application.
 *
 * Commands encapsulate client requests so both RMI and socket transports can use
 * the same execution path.
 */
public interface ServerCommand extends Serializable {
    /**
     * Indicates whether this command must run under the server game lock on controller.
     *
     * @return {@code true} for commands that mutate shared game state; {@code false} otherwise
     */
    boolean requiresLock();
    /**
     * Executes this command on the server application.
     *
     * @param serverApplication server-side application receiving the command
     * @param sender            client endpoint that sent the command
     * @throws Exception if command execution fails
     */
    void execute(ServerApplication serverApplication, VirtualView sender) throws Exception;
}