package it.polimi.ingsw.am55.dto.resolveEvents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResolveSustenanceView extends ResolveEventView implements Serializable {
    private Map<String, Integer> effectToPP = new HashMap<>();

    public ResolveSustenanceView(Map<String, Integer> effectToFood, Map<String, Integer> effectToPP, String nameEvent) {
        super(effectToFood, nameEvent);
        this.effectToPP = effectToPP;

        System.out.println("Nome evento: " + this.nameEvent);
        System.out.println("effect to food: " + this.effectToPlayer);
        System.out.println("Nome evento: " + this.effectToPP);
    }

    @Override
    public String toString() {
        return "ResolveSustenanceView{" +
                "effectToPP=" + effectToPP +
                ", effectToPlayer=" + effectToPlayer +
                '}';
    }
}
