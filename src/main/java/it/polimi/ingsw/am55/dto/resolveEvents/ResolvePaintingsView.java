package it.polimi.ingsw.am55.dto.resolveEvents;

import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResolvePaintingsView extends ResolveEventView implements Serializable {
    private Map<String, Integer> effectToPP = new HashMap<>();

    public ResolvePaintingsView(Map<String, Integer> effectToFood, Map<String, Integer> effectToPP, String nameEvent) {
        super(effectToFood, nameEvent);
        this.effectToPP = effectToPP;

        System.out.println("Nome evento: " + this.nameEvent);
        System.out.println("effect to food: " + this.effectToPlayer);
        System.out.println("Nome evento: " + this.effectToPP);
    }



    public StringBuilder showEvent() {
        StringBuilder result = new StringBuilder();

        for (String id : effectToPP.keySet()) {
            result.append("Food gained/lost by " + id + ": " + effectToPP.get(id) +
                            "\nPP gained/lost by " + id + ": " + effectToPlayer.get(id) + "\n");
        }

        return result;
    }
}
