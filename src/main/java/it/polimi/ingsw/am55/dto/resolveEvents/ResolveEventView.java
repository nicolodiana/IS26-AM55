package it.polimi.ingsw.am55.dto.resolveEvents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResolveEventView implements Serializable {
    protected String nameEvent;
    protected Map<String, Integer> effectToPlayer = new HashMap<>();
    //Map<String, Integer> effectToFood;


    public ResolveEventView(Map<String, Integer> effectToPlayer, String nameEvent) {
        this.effectToPlayer = effectToPlayer;
        this.nameEvent = nameEvent;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void showEvent() {}
}
