package it.polimi.ingsw.am55.view.launcher;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.network.ClientImpl;
import it.polimi.ingsw.am55.view.cli.CLIView;

import java.rmi.RemoteException;

/**
 * Startup strategy for the command-line client interface.
 */
public class CliViewLauncher implements ClientViewLauncher {

    private final ClientModel model;
    private final UserActionHandler actionHandler;
    private final ClientImpl client;

    public CliViewLauncher(ClientModel model, UserActionHandler actionHandler, ClientImpl client) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.client = client;
    }

    /**
     * Starts the CLI and then opens the network connection.
     *
     * <p>The CLI is registered as model observer before {@link ClientImpl#connect()}
     * because the connection phase may immediately update the lobby state.</p>
     */
    @Override
    public void start() throws RemoteException {
        CLIView view = new CLIView(model);

        model.addObserver(view);

        view.setActionHandler(actionHandler);
        view.start();

        try {
            client.connect();
        } catch (Exception e) {
            throw new RemoteException();
        }
    }
}
