package it.polimi.ingsw.am55.view;

/**
 * Azione/interazione che la view deve proporre al client in questo momento.
 *
 * La CLI userà questo valore per stampare il comando testuale corretto.
 * La GUI potrà usare lo stesso valore per abilitare bottoni, highlight e click.
 */
public enum ClientAction {
    LOBBY,
    WAITING_TO_START,
    WAITING_FOR_TURN,
    PLACE_TOTEM,
    PICK_CARD,
    PICK_SPECIAL,
    RESOLVE_EVENTS,
    END_GAME,
    CRASHED,
    WAITING_FOR_STATE
}
