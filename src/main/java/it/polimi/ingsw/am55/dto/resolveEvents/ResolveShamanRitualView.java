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

    @Override
    public String toString() {
        return "ResolveShamanRitualView{" +
                "effectToPP=" + effectToPlayer +
                '}';
    }

    public void showEvent() {
        System.out.println(ConsoleColor.RED_BOLD + nameEvent + ConsoleColor.RESET);

        for (String id : effectToPlayer.keySet()) {
            System.out.println("PP gained by " + id + ": " + effectToPlayer.get(id));
        }
    }
}
