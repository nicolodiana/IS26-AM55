package it.polimi.ingsw.am55.dto.resolveEvents;

import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Serializable DTO that formats the effects produced by a cave-painting event to the player's food and prestige-points.
 */
public class ResolvePaintingsView extends ResolveEventView implements Serializable {
    /**
     * Mapping between affected players and the prestige points assigned by the effect.
     */
    private Map<String, Integer> effectToPP = new HashMap<>();

    /**
     * Creates a resolve paintings view from model data that can be sent to the client.
     *
     * @param effectToFood the per-player food changes produced by the event
     * @param effectToPP the per-player prestige-point changes produced by the event
     * @param nameEvent the event name
     */
    public ResolvePaintingsView(Map<String, Integer> effectToFood, Map<String, Integer> effectToPP, String nameEvent) {
        super(effectToFood, nameEvent);
        this.effectToPP = effectToPP;

        System.out.println("Nome evento: " + this.nameEvent);
        System.out.println("effect to food: " + this.effectToPlayer);
        System.out.println("Nome evento: " + this.effectToPP);
    }


    /**
     * Builds the text shown to the client for this resolved event.
     *
     * @return the formatted event-resolution text
     */
    public StringBuilder showEvent() {
        StringBuilder result = new StringBuilder();

        for (String id : effectToPP.keySet()) {
            result.append("Food gained/lost by " + id + ": " + effectToPP.get(id) +
                            "\nPP gained/lost by " + id + ": " + effectToPlayer.get(id) + "\n");
        }

        return result;
    }
}
