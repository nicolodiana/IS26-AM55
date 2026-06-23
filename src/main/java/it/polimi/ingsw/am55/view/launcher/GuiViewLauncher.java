package it.polimi.ingsw.am55.view.launcher;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.network.ClientImpl;
import it.polimi.ingsw.am55.view.gui.JavaFXGui;

/**
 * Startup strategy for the JavaFX graphical client interface.
 */
public class GuiViewLauncher implements ClientViewLauncher {

    private final ClientModel model;
    private final UserActionHandler actionHandler;
    private final ClientImpl client;

    public GuiViewLauncher(ClientModel model, UserActionHandler actionHandler, ClientImpl client) {
        this.model = model;
        this.actionHandler = actionHandler;
        this.client = client;
    }

    /**
     * start the GUI and setup connections
     * catching exceptions
     */
    @Override
    public void start() {
        JavaFXGui.launchGui(model, actionHandler, () -> {
            try {
                client.connect();
            } catch (Exception e) {
                System.err.println("[CLIENT] Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}