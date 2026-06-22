package it.polimi.ingsw.am55.dto.resolveEvents;

import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.io.Serializable;
import java.util.Map;

public class ResolveShamanRitualView extends ResolveEventView implements Serializable {

    public ResolveShamanRitualView(Map<String, Integer> effectToPlayer, String nameEvent) {
        super(effectToPlayer, nameEvent);

        System.out.println("Nome evento: " + this.nameEvent);
        System.out.println("effect to food: " + this.effectToPlayer);
    }



    public StringBuilder showEvent() {
        StringBuilder result = new StringBuilder();

        for (String id : effectToPlayer.keySet()) {
            result.append("PP gained/lost by " + id + ": " + effectToPlayer.get(id) + "\n");
        }

        return result;
    }
}
