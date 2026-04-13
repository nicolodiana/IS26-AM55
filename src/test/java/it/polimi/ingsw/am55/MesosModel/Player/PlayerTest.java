package it.polimi.ingsw.am55.MesosModel.Player;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    /**
     * Tests valid additions and payments for Food.
     */
    @Test
    void testFoodManagement_ValidAmounts() {
        Player player = new Player("tribe", "totem1");
        player.addFood(10);
        assertEquals(10, player.getNumFoods());

        player.payFood(4);
        assertEquals(6, player.getNumFoods());
    }

    /**
     * Tests that negative amounts throw exceptions for Food methods.
     */
    @Test
    void testFoodManagement_NegativeAmountsThrowException() {
        Player player = new Player("tribe", "totem1");
        assertThrows(IllegalArgumentException.class, () -> player.addFood(-1));
        assertThrows(IllegalArgumentException.class, () -> player.payFood(-1));
    }

    /**
     * Tests valid additions and payments for Prestige Points (PP).
     */
    @Test
    void testPPManagement_ValidAmounts() {
        Player player = new Player("tribe", "totem1");
        player.addPP(10);
        assertEquals(10, player.getNumPP());

        player.payPP(4);
        assertEquals(6, player.getNumPP());
    }

    /**
     * Tests that negative amounts throw exceptions for PP methods.
     */
    @Test
    void testPPManagement_NegativeAmountsThrowException() {
        Player player = new Player("tribe", "totem1");
        assertThrows(IllegalArgumentException.class, () -> player.addPP(-1));
        assertThrows(IllegalArgumentException.class, () -> player.payPP(-1));
    }
    @Test
    void addTribeCardForCharactersShouldPopulateAllListsAndApplyHunterInstantEffect() {
        // Testa che ogni addTribeCard dei personaggi inserisca la carta
        // nella lista corretta e che l'effetto istantaneo dell'Hunter con icona
        // aggiunga cibo pari al numero di cacciatori presenti in tribù.

        Player player = new Player("tribe", "totem1");

        player.addTribeCard(new Shaman(1, 2, 0));
        player.addTribeCard(new Hunter(2, true, 0));
        player.addTribeCard(new Hunter(3, false, 0));
        player.addTribeCard(new Artist(4, 0));
        player.addTribeCard(new Collector(5, 0));
        player.addTribeCard(new Builder(6, 4, 1, 0));
        player.addTribeCard(new Inventor("clay", 7, 0));

        assertEquals(1, player.getShamansList().size());
        assertEquals(2, player.getHuntersList().size());
        assertEquals(1, player.getArtistsList().size());
        assertEquals(1, player.getCollectorsList().size());
        assertEquals(1, player.getBuildersList().size());
        assertEquals(1, player.getInventorsList().size());
        assertEquals(7, player.playerDeckSize());

        // Il primo hunter ha icona e viene aggiunto quando la lista hunters ha size = 1
        assertEquals(1, player.getNumFoods());
    }

    @Test
    void addHunterWithIconShouldScaleFoodWithCurrentHunterCount() {
        // Testa nel dettaglio l'effetto istantaneo dei cacciatori con icona:
        // ogni volta che si aggiunge un Hunter con icona, il cibo guadagnato
        // è pari al numero totale di Hunter dopo l'aggiunta.

        Player player = new Player("hunters", "totem2");

        player.addTribeCard(new Hunter(1, true, 0));
        assertEquals(1, player.getNumFoods());

        player.addTribeCard(new Hunter(2, false, 0));
        assertEquals(1, player.getNumFoods());

        player.addTribeCard(new Hunter(3, true, 0));
        // al momento dell'aggiunta i cacciatori sono 3, quindi +3
        assertEquals(4, player.getNumFoods());
    }

    @Test
    void addInventorWithBuilding5ShouldAwardFoodOnlyWhenANewPairIsCompleted() {
        // Testa l'effetto istantaneo di BUILDING5:
        // quando si aggiunge un Inventor con la stessa invenzione e si completa
        // una nuova coppia, il player guadagna 3 cibi.
        // Il controllo deve essere case-insensitive.

        Player player = new Player("inventors", "totem3");
        player.getBuildings().add(new BuildingCard(1, 0, 0, 0, BuildingType.BUILDING5, null, 0));

        player.addTribeCard(new Inventor("hammer", 2, 0));
        assertEquals(0, player.getNumFoods());

        player.addTribeCard(new Inventor("HAMMER", 3, 0));
        assertEquals(3, player.getNumFoods());

        player.addTribeCard(new Inventor("hammer", 4, 0));
        assertEquals(3, player.getNumFoods());

        player.addTribeCard(new Inventor("HaMmEr", 5, 0));
        assertEquals(6, player.getNumFoods());
    }

    @Test
    void addBuildingCardShouldApplyBuilderDiscountFloorAtZeroAndRejectIfFoodIsInsufficient() {
        // Testa l'addTribeCard(BuildingCard):
        // 1) applica correttamente lo sconto dato dai Builder
        // 2) il costo non può scendere sotto zero
        // 3) se il cibo non è sufficiente l'edificio non viene aggiunto

        Player discounted = new Player("discounted", "totem4");
        discounted.addFood(10);
        discounted.addTribeCard(new Builder(1, 0, 1, 0));
        discounted.addTribeCard(new BuildingCard(2, 0, 5, 0, BuildingType.BUILDING4, null, 0));

        assertEquals(6, discounted.getNumFoods());
        assertTrue(discounted.hasBuilding(BuildingType.BUILDING4));

        Player zeroFloor = new Player("zeroFloor", "totem5");
        zeroFloor.addFood(5);
        zeroFloor.addTribeCard(new Builder(3, 0, 3, 0));
        zeroFloor.addTribeCard(new Builder(4, 0, 4, 0));
        zeroFloor.addTribeCard(new BuildingCard(5, 0, 5, 0, BuildingType.BUILDING8, null, 0));

        assertEquals(5, zeroFloor.getNumFoods());
        assertTrue(zeroFloor.hasBuilding(BuildingType.BUILDING8));

        Player poor = new Player("poor", "totem6");
        poor.addFood(1);
        poor.addTribeCard(new BuildingCard(6, 0, 3, 0, BuildingType.BUILDING9, null, 0));

        assertEquals(1, poor.getNumFoods());
        assertFalse(poor.hasBuilding(BuildingType.BUILDING9));
    }

    @Test
    void addBuilding1ShouldNotRewardPastCompletedSetsButShouldRewardNewOnes() {
        // Testa il nuovo effetto di BUILDING1:
        // i set già completati prima di ottenere l'edificio non danno ricompensa retroattiva;
        // dopo aver ottenuto BUILDING1, ogni nuovo set completo di 6 tipi diversi
        // deve assegnare 5 cibi nel momento esatto in cui si completa.

        Player player = new Player("building1", "totem7");
        player.addFood(20);

        addCompleteSet(player);
        assertEquals(20, player.getNumFoods());
        assertEquals(1, player.minCardSet());

        player.addTribeCard(new BuildingCard(7, 0, 2, 0, BuildingType.BUILDING1, null, 0));
        assertEquals(18, player.getNumFoods());
        assertEquals(1, player.minCardSet());

        player.addTribeCard(new Shaman(8, 1, 0));
        player.addTribeCard(new Hunter(9, false, 0));
        player.addTribeCard(new Artist(10, 0));
        player.addTribeCard(new Collector(11, 0));
        player.addTribeCard(new Builder(12, 1, 0, 0));

        // non ho ancora completato il secondo set, quindi niente bonus
        assertEquals(18, player.getNumFoods());

        player.addTribeCard(new Inventor("second-tool", 13, 0));

        // qui completo il nuovo set e quindi ottengo +5 cibo
        assertEquals(23, player.getNumFoods());
        assertEquals(2, player.minCardSet());
    }

    @Test
    void addBuilding1ShouldRewardEveryNewCompletedSetExactlyOnce() {
        // Testa che BUILDING1 assegni 5 cibi per ogni nuovo set completato,
        // una sola volta per set, senza duplicare il bonus su aggiunte extra
        // che non aumentano minCardSet().
          // 10 cibo iniziale
                // -2 costo BUILDING1
                // +5 bonus per il completamento del set
                //=13

        Player player = new Player("multiSet", "totem8");
        player.addFood(10);

        player.addTribeCard(new BuildingCard(14, 0, 2, 0, BuildingType.BUILDING1, null, 0));
        assertEquals(8, player.getNumFoods());

        addCompleteSet(player);
        assertEquals(13, player.getNumFoods());
        assertEquals(1, player.minCardSet());

        player.addTribeCard(new Shaman(15, 1, 0));
        assertEquals(13, player.getNumFoods());

        player.addTribeCard(new Hunter(16, false, 0));
        player.addTribeCard(new Artist(17, 0));
        player.addTribeCard(new Collector(18, 0));
        player.addTribeCard(new Builder(19, 1, 0, 0));
        assertEquals(13, player.getNumFoods());

        player.addTribeCard(new Inventor("tool-2", 20, 0));
        assertEquals(18, player.getNumFoods());
        assertEquals(2, player.minCardSet());
    }

    @Test
    void addShamanShouldAffectCountShamanStarsAndBuilding6Bonus() {
        // Testa che l'aggiunta degli Shaman aggiorni correttamente il conteggio stelle
        // e che BUILDING6 aggiunga il bonus fisso di 3 stelle.

        Player player = new Player("stars", "totem9");

        player.addTribeCard(new Shaman(1, 2, 0));
        player.addTribeCard(new Shaman(2, 1, 0));
        assertEquals(3, player.countShamanStars());

        player.getBuildings().add(new BuildingCard(21, 0, 0, 0, BuildingType.BUILDING6, null, 0));
        assertEquals(6, player.countShamanStars());
    }

    private void addCompleteSet(Player player) {
        player.addTribeCard(new Shaman(100, 1, 0));
        player.addTribeCard(new Hunter(101, false, 0));
        player.addTribeCard(new Artist(102, 0));
        player.addTribeCard(new Collector(103, 0));
        player.addTribeCard(new Builder(104, 1, 0, 0));
        player.addTribeCard(new Inventor("tool-1", 105, 0));
    }

}