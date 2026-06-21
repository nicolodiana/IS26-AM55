package it.polimi.ingsw.am55.view;

/**
 * Describes the interaction currently expected from a client view.
 * <p>
 * The value is derived from the model state and is shared by CLI and GUI so both
 * views enable the same player actions without duplicating game-state checks.
 */
public enum ClientAction {
    /** The player is still in the lobby. */
    LOBBY,
    /** The game exists but is waiting for enough players. */
    WAITING_TO_START,
    /** Another player is currently acting. */
    WAITING_FOR_TURN,
    /** The local player must place a totem on the bidding trail. */
    PLACE_TOTEM,
    /** The local player must pick a standard card from the board. */
    PICK_CARD,
    /** The local player must perform a special upper-row pick. */
    PICK_SPECIAL,
    /** The model is resolving round events. */
    RESOLVE_EVENTS,
    /** The game has ended and final results are available or about to arrive. */
    END_GAME,
    /** The model is resolving final events and final scoring. */
    END_GAME_RESOLVE,
    /** The game or network connection crashed. */
    CRASHED,
    /** The view does not have enough state to offer an action yet. */
    WAITING_FOR_STATE
}
