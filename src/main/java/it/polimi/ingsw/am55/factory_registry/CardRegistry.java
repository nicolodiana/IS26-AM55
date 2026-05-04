package it.polimi.ingsw.am55.factory_registry;

import it.polimi.ingsw.am55.ClientModel.ClientCards.*;

import java.util.HashMap;
import java.util.Map;

public class CardRegistry {
    private static final Map<String, CardConstructor> registry = new HashMap<>();

    /*static  {
        registry.put("Artist", (id,card) -> new ArtistCardView(id, card.getEra()));
        registry.put("Builder", (id,card) -> new BuilderCardView(id, card.getNumPP(), card.getBuildingDiscount(), card.getEra()));
        registry.put("Collector", (id,card) -> new CollectorCardView(id, card.getEra()));
        registry.put("Hunter", (id,card) -> new HunterCardView(id, card.getIcon(), card.getEra()));
        registry.put("Inventor", (id,card) -> new InventorCardView(card.getIconInvention(), id, card.getEra()));
        registry.put("Shaman", (id,card) -> new ShamanCardView(id, card.getEra(), card.getNumStars()));
    }

    public static CardConstructor getCard(String type) {
        return registry.get(type);
    }*/
}
