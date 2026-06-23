package it.polimi.ingsw.am55.dto.resolveEvents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base serializable DTO for an event-resolution result.
 * <p>It stores the event name and the per-player effect values.
 */
public class ResolveEventView implements Serializable {
    /**
     * Name of this event.
     */
    protected String nameEvent;
    /**
     * Mapping between affected players and the event-resolution value applied to them.
     */
    protected Map<String, Integer> effectToPlayer = new HashMap<>();


    /**
     * Creates a resolve event view from model data that can be sent to the client.
     *
     * @param effectToPlayer the player-related value used by the operation
     * @param nameEvent the event name shown to the client
     */
    public ResolveEventView(Map<String, Integer> effectToPlayer, String nameEvent) {
        this.effectToPlayer = effectToPlayer;
        this.nameEvent = nameEvent;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    /**
     * Builds the text shown to the client for this resolved event.
     *
     * @return the formatted event-resolution text
     */
    public StringBuilder showEvent() { return new StringBuilder(); }
}
