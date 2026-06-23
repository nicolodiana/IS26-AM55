package it.polimi.ingsw.am55.controller;
/**
 * Callback interface used by client views to request user actions.
 * <p>Both the CLI and GUI depend on this abstraction instead of directly knowing the networking layer.
 */
public interface UserActionHandler {
    /**
     * Handles the create game event or user action.
     *
     * @param playerId the identifier of the player performing the action
     * @param totem the selected totem color
     * @param numPlayers the players participating in the operation
     */
    void onCreateGameSelected(String playerId, String totem, int numPlayers);
    /**
     * Handles the join game event or user action.
     *
     * @param playerId the identifier of the player performing the action
     * @param totem the selected totem color
     */
    void onJoinGameSelected(String playerId, String totem);
    /**
     * Handles the pick card event or user action.
     *
     * @param playerId the identifier of the player performing the action
     * @param cardId the identifier of the card selected by the player
     */
    void onPickCardSelected(String playerId, int cardId);
    /**
     * Handles the pick special event or user action.
     *
     * @param playerId the identifier of the player performing the action
     * @param cardId the identifier of the card selected by the player
     */
    void onPickSpecialSelected(String playerId, int cardId);
    /**
     * Handles the place totem event or user action.
     *
     * @param playerId the identifier of the player performing the action
     * @param index the position to evaluate or access
     */
    void onPlaceTotemSelected(String playerId,int index);
    /**
     * Handles the quit game event or user action.
     *
     * @param playerId the identifier of the player performing the action
     */
    void onQuitGameSelected(String playerId);
    /**
     * Handles the quit lobby event or user action.
     */
    void onQuitSelectedLobby();

}