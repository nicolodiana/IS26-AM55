package it.polimi.ingsw.am55.dto.resolveEvents;

import java.io.Serializable;
import java.util.Map;

public class ResolveShamanRitualView extends ResolveEventView implements Serializable {

    public ResolveShamanRitualView(Map<String, Integer> effectToPlayer, String nameEvent) {
        super(effectToPlayer, nameEvent);

        System.out.println("Nome evento: " + this.nameEvent);
        System.out.println("effect to food: " + this.effectToPlayer);
    }

    @Override
    public String toString() {
        return "ResolveShamanRitualView{" +
                "effectToPlayer=" + effectToPlayer +
                '}';
    }
}
