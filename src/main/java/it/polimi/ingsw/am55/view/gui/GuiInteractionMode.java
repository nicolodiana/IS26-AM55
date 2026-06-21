package it.polimi.ingsw.am55.view.gui;

/**
 * Interaction mode applied by the JavaFX game scene.
 * <p>
 * It is not a full game state: it only tells the scene which board elements are
 * clickable or highlighted for the local player.
 */
public enum GuiInteractionMode {
    /** The board is visible but not interactive. */
    READ_ONLY,
    /** Free bidding tickets are clickable. */
    PLACE_TOTEM,
    /** The next allowed card row is clickable. */
    PICK_CARD,
    /** The upper row is clickable for a special effect pick. */
    PICK_SPECIAL,
    /** Event resolution is visible and interactions are disabled. */
    RESOLVE_EVENTS,
    /** The game is over. */
    END_GAME,
    /** A command has been sent and the view is waiting for the server. */
    LOCKED
}
