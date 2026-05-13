package it.polimi.ingsw.am55.view.gui;

/**
 * Modalità grafica della GameScene.
 * Non rappresenta lo stato completo del gioco: indica solo quali elementi
 * della board devono essere evidenziati/cliccabili in questo momento.
 */
public enum GuiInteractionMode {
    READ_ONLY,
    PLACE_TOTEM,
    PICK_CARD,
    PICK_SPECIAL,
    RESOLVE_EVENTS,
    END_GAME,
    LOCKED
}
