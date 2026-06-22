package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.dto.GameView;

/**
 * Converts the current {@link GameView} snapshot into a view-level interaction.
 * <p>
 * Keeping this logic centralized prevents the CLI and GUI from maintaining
 * separate and potentially inconsistent switches over {@link GameState}.
 */
public class ClientActionResolver {

    /**
     * Resolves the action that the local view should expose right now.
     *
     * @param gameView latest game snapshot, or {@code null} before the game starts
     * @param myPlayerId local player nickname
     * @param inLobby whether the client is still in the lobby flow
     * @param gameCrashed whether the client model marked the game as crashed
     * @return current view-level action
     */
    public ClientAction resolve(GameView gameView, String myPlayerId, boolean inLobby, boolean gameCrashed, boolean  game_ended) {
        if (gameCrashed) {
            return ClientAction.CRASHED;
        }
        if (inLobby) {
            return ClientAction.LOBBY;
        }

        if (game_ended ) {
            return ClientAction.END_GAME;
        }
        if (gameView == null || gameView.getState() == null) {
            return ClientAction.WAITING_FOR_STATE;
        }

        GameState state = gameView.getState();

        if (state == GameState.CREATED) {
            return ClientAction.WAITING_TO_START;
        }

        if (state == GameState.EVENTRESOLVE) {
            return ClientAction.RESOLVE_EVENTS;
        }
        if (state == GameState.ENDGAMERESOLVE) {
            return ClientAction.END_GAME_RESOLVE;
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

    /**
     * Checks whether the local player is the current game player.
     *
     * @param gameView latest game snapshot
     * @param myPlayerId local player nickname
     * @return {@code true} when it is the local player's turn
     */
    public boolean isMyTurn(GameView gameView, String myPlayerId) {
        if (gameView == null || gameView.getCurrentPlayer() == null || myPlayerId == null) {
            return false;
        }
        return gameView.getCurrentPlayer().trim().equalsIgnoreCase(myPlayerId.trim());
    }
}
