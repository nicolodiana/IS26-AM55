package it.polimi.ingsw.am55.dto.resolveEvents;

import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.io.Serializable;
import java.util.Map;

/**
 * Serializable DTO that formats the prestige-point effects produced by a shaman ritual event.
 */
public class ResolveShamanRitualView extends ResolveEventView implements Serializable {

    /**
     * Creates a resolve shaman ritual view from model data that can be sent to the client.
     *
     * @param effectToPlayer the player-related value used by the operation
     * @param nameEvent the event name shown to the client
     */
    public ResolveShamanRitualView(Map<String, Integer> effectToPlayer, String nameEvent) {
        super(effectToPlayer, nameEvent);

        System.out.println("Nome evento: " + this.nameEvent);
        System.out.println("effect to food: " + this.effectToPlayer);
    }


    /**
     * Builds the text shown to the client for this resolved event.
     *
     * @return the formatted event-resolution text
     */
    public StringBuilder showEvent() {
        StringBuilder result = new StringBuilder();

        for (String id : effectToPlayer.keySet()) {
            result.append("PP gained/lost by " + id + ": " + effectToPlayer.get(id) + "\n");
        }

        return result;
    }
}
