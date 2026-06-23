package it.polimi.ingsw.am55.dto.resolveEvents;

import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Serializable DTO that formats the food and prestige-point effects for every player
 * produced by a hunting event.
 */
public class ResolveSustenanceView extends ResolveEventView implements Serializable {
    /**
     * Mapping between affected players and the prestige points assigned by the effect.
     */
    private Map<String, Integer> effectToPP = new HashMap<>();

    /**
     * Creates a resolve sustenance event view from model data that can be sent to the client.
     *
     * @param effectToFood the per-player food changes produced by the event
     * @param effectToPP the per-player prestige-point changes produced by the event
     * @param nameEvent the event name shown to the client
     */
    public ResolveSustenanceView(Map<String, Integer> effectToFood, Map<String, Integer> effectToPP, String nameEvent) {
        super(effectToFood, nameEvent);
        this.effectToPP = effectToPP;
    }


    /**
     * Builds the text shown to the client for this resolved event.
     *
     * @return the formatted event-resolution text
     */
    public StringBuilder showEvent() {
        StringBuilder result = new StringBuilder();

        for (String id : effectToPP.keySet()) {
            result.append("Food gained/lost by " + id + ": " + effectToPlayer.get(id) +
                    "\nPP gained/lost by " + id + ": " + effectToPP.get(id) + "\n");
        }

        return result;
    }
}
