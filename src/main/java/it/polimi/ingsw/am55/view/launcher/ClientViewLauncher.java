package it.polimi.ingsw.am55.view.launcher;

import java.rmi.RemoteException;

/**
 * Common abstraction for the available client view startup strategies.
 *
 * <p>Each implementation knows how to initialize one specific user interface,
 * while the application bootstrap can work only with this abstraction and call
 * {@link #start()}.</p>
 */
public interface ClientViewLauncher {

    /**
     * Starts the selected user interface.
     */
    void start() throws RemoteException;
}
