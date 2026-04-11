package it.polimi.ingsw.am55.MesosModel.Effect;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SustenanceEventCardTest {

    @Test
    void activateEventStandard() {
        // Testa in un unico scenario i casi principali dell'evento sostentamento:
        // - giocatore con abbastanza cibo
        // - giocatore che grazie a BUILDING2 ottiene uno sconto ma non basta e perde PP
        // - giocatore il cui costo finale va a zero grazie agli sconti

        Player enoughFood = new Player("enoughFood", "totem1", "summary1");
        enoughFood.addPP(5);
        enoughFood.addFood(10);
        enoughFood.addTribeCard(new Hunter(1, false, 0));
        enoughFood.addTribeCard(new Artist(2, 0));
        enoughFood.addTribeCard(new Builder(3, 0, 0, 0));
        enoughFood.addTribeCard(new Collector(4, 0));
        // personaggi = 4
        // collector discount = 3
        // costo finale = 1
        // ha 10 cibo -> paga 1, no penalità

        Player withBuilding2Penalty = new Player("withBuilding2Penalty", "totem2", "summary2");
        withBuilding2Penalty.addPP(10);
        withBuilding2Penalty.addFood(2);
        withBuilding2Penalty.addTribeCard(new Hunter(5, false, 0));
        withBuilding2Penalty.addTribeCard(new Hunter(6, false, 0));
        withBuilding2Penalty.addTribeCard(new Hunter(7, false, 0));
        withBuilding2Penalty.addTribeCard(new Artist(8, 0));
        withBuilding2Penalty.getBuildings().add(
                new BuildingCard(9, 0, 0, 0, BuildingType.BUILDING2, CharacterType.HUNTER, 0)
        );
        // personaggi = 4
        // collector discount = 0
        // building2 discount = 3 hunter
        // costo finale = 1
        // ha 2 cibo -> paga 1, no penalità

        Player freeDueToDiscounts = new Player("freeDueToDiscounts", "totem3", "summary3");
        freeDueToDiscounts.addPP(7);
        freeDueToDiscounts.addFood(9);
        freeDueToDiscounts.addTribeCard(new Collector(10, 0));
        freeDueToDiscounts.addTribeCard(new Builder(11, 0, 0, 0));
        // personaggi = 2
        // collector discount = 3
        // building2 discount = 0
        // costo finale = max(0, 2 - 3) = 0

        SustenanceEventCard card = new SustenanceEventCard(12, 1, 2);
        card.activateEvent(List.of(enoughFood, withBuilding2Penalty, freeDueToDiscounts));

        assertEquals(9, enoughFood.getNumFoods());
        assertEquals(5, enoughFood.getNumPP());

        assertEquals(1, withBuilding2Penalty.getNumFoods());
        assertEquals(10, withBuilding2Penalty.getNumPP());

        assertEquals(9, freeDueToDiscounts.getNumFoods());
        assertEquals(7, freeDueToDiscounts.getNumPP());
    }

    @Test
    void activateEventShouldReducePPWhenPlayerCannotPayEnoughFood() {
        // Testa il caso in cui il giocatore non abbia abbastanza cibo:
        // paga tutto quello che può e perde numPP per ogni unità di costo rimasta scoperta.

        Player poor = new Player("poor", "totem4", "summary4");
        poor.addPP(10);
        poor.addFood(1);

        poor.addTribeCard(new Shaman(13, 1, 0));
        poor.addTribeCard(new Hunter(14, false, 0));
        poor.addTribeCard(new Artist(15, 0));
        poor.addTribeCard(new Collector(16, 0));
        // personaggi = 4
        // collector discount = 3
        // costo finale = 1
        // qui in realtà pagherebbe tutto -> niente penalità
        // quindi aggiungo altri personaggi per generare costo residuo

        poor.addTribeCard(new Builder(17, 0, 0, 0));
        poor.addTribeCard(new Inventor("tool", 18, 0));
        // personaggi = 6
        // collector discount = 3
        // costo finale = 3
        // ha 1 cibo -> paga 1
        // restano 2 non pagati -> perde 2 * 2 = 4 PP

        SustenanceEventCard card = new SustenanceEventCard(19, 1, 2);
        card.activateEvent(List.of(poor));

        assertEquals(0, poor.getNumFoods());
        assertEquals(6, poor.getNumPP());
    }

    @Test
    void activateEventShouldSumDiscountFromMultipleBuilding2Cards() {
        // Testa che più BUILDING2 si sommino correttamente:
        // ogni edificio 2 conta il numero di personaggi del proprio CharacterType.

        Player player = new Player("multiDiscount", "totem5", "summary5");
        player.addPP(8);
        player.addFood(5);

        player.addTribeCard(new Hunter(20, false, 0));
        player.addTribeCard(new Hunter(21, false, 0));
        player.addTribeCard(new Builder(22, 0, 0, 0));
        player.addTribeCard(new Builder(23, 0, 0, 0));
        player.addTribeCard(new Artist(24, 0));
        // personaggi = 5

        player.addTribeCard(
                new BuildingCard(25, 0, 0, 0, BuildingType.BUILDING2, CharacterType.HUNTER, 0)
        );
        player.addTribeCard(
                new BuildingCard(26, 0, 0, 0, BuildingType.BUILDING2, CharacterType.BUILDER, 0)
        );
        // sconto totale = 2 hunter + 2 builder = 4
        // costo finale = 5 - 4 = 1

        SustenanceEventCard card = new SustenanceEventCard(27, 1, 3);
        card.activateEvent(List.of(player));

        assertEquals(4, player.getNumFoods());
        assertEquals(8, player.getNumPP());
    }
}