package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.dto.GameView;

/**
 * Traduce lo stato del GameView nello stato/interazione attesa dalla view.
 *
 * Questa classe evita di duplicare gli switch su GameState dentro CLI e GUI.
 */
public class ClientActionResolver {

    public ClientAction resolve(GameView gameView, String myPlayerId, boolean inLobby) {
        if (inLobby) {
            return ClientAction.LOBBY;
        }


        if (gameView == null) {
            return ClientAction.WAITING_FOR_STATE;
        }

        GameState state = gameView.getState();

        if (state == null) {
            return ClientAction.WAITING_FOR_STATE;
        }

        if (state == GameState.CREATED && !inLobby) {
            return ClientAction.WAITING_TO_START;
        }

        if (state == GameState.ENDED){
            return ClientAction.END_GAME;
        }

        if (state == GameState.EVENTRESOLVE) {
            return ClientAction.RESOLVE_EVENTS;
        }

        if (state == GameState.ENDGAMERESOLVE ) {
            return ClientAction.END_GAME_RESOLVE;
        }

        if (state == GameState.CRASHED) {
            return ClientAction.CRASHED;
        }

        if (!isMyTurn(gameView, myPlayerId)) {
            return ClientAction.WAITING_FOR_TURN;
        }

        return switch (state) {
            case PLACETOTEM -> ClientAction.PLACE_TOTEM;
            case PICKCARD -> ClientAction.PICK_CARD;
            case PICKSPECIAL -> ClientAction.PICK_SPECIAL;
            default -> ClientAction.WAITING_FOR_STATE;
        };
    }

    public boolean isMyTurn(GameView gameView, String myPlayerId) {
        if (gameView == null || gameView.getCurrentPlayer() == null || myPlayerId == null) {
            return false;
        }

        return gameView.getCurrentPlayer().trim().equalsIgnoreCase(myPlayerId.trim());
    }
}
