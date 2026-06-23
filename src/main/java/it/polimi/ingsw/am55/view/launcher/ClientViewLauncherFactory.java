package it.polimi.ingsw.am55.view.launcher;

import it.polimi.ingsw.am55.utility.*;
import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.network.client.ClientImpl;


/**
 * Factory that creates the startup strategy for the selected client view mode.
 */
public class ClientViewLauncherFactory {


    public static ClientViewLauncher create(ClientConfig.ViewMode viewMode, ClientModel model, UserActionHandler actionHandler, ClientImpl client) {
        return switch (viewMode) {
            case CLI -> new CliViewLauncher(model, actionHandler, client);
            case GUI -> new GuiViewLauncher(model, actionHandler, client);
        };
    }
}
