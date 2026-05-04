package it.polimi.ingsw.am55.factory_registry;

import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;

import java.util.HashMap;
import java.util.Map;

public class CardViewRegistry {
    private static final Map<CharacterType, CharacterCard> registry = new HashMap<>();

    /*static  {
        registry.put(ARTIST, card -> card.toView());
        registry.put(BUILDER, card -> new BuilderCardView(card.getId(), card.getNumPP(), card.getBuildingDiscount(), card.getEra()));
        registry.put(COLLECTOR, card -> new CollectorCardView(id, card.getEra()));
        registry.put(HUNTER, card -> new HunterCardView(id, card.getIcon(), card.getEra()));
        registry.put(INVENTOR, (id,card) -> new InventorCardView(card.getIconInvention(), id, card.getEra()));
        registry.put(SHAMAN, (id,card) -> new ShamanCardView(id, card.getEra(), card.getNumStars()));
    }

    public static CardConstructor getCard(CharacterType type) {
        //return registry.get(type);
    }*/

}
