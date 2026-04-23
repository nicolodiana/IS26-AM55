package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.controller.UserActionHandler;

public class CLIPView {

    private UserActionHandler actionHandler;

    /*l'actionhandler che gli darò sarà un tipo concreto clientcontroller ma il fatto di riceverlo come
    UserActionHandler mi limita solo a utilizzare determinati metodi
    */
    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public void askCreateGame(String playerId, String totemColor, int numPlayers) {
        if (actionHandler != null) {
            actionHandler.onCreateGameSelected(playerId, totemColor, numPlayers);
        }
    }

    public void askPlaceTotem(int index) {
        if (actionHandler != null) {
            actionHandler.onPlaceTotemSelected(index);
        }
    }

    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void showMessage(String message) {
        System.out.println("[INFO] " + message);
    }

    public void refresh() {
        System.out.println("View aggiornata");
    }
}