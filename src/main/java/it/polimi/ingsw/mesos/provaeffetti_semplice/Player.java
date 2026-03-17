package it.polimi.ingsw.mesos.provaeffetti_semplice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {
    private final String name;
    private int pp   = 0;
    private int food = 0;

    // Una lista separata per ogni tipo di personaggio.
    // Zero switch, zero enum: il tipo e codificato nella lista stessa.
    private final List<HunterCard>   hunters   = new ArrayList<>();
    private final List<ArtistCard>   artists   = new ArrayList<>();
    private final List<ShamanCard>   shamans   = new ArrayList<>();
    private final List<BuilderCard>  builders  = new ArrayList<>();
    private final List<InventorCard> inventors = new ArrayList<>();
    private final List<GathererCard> gatherers = new ArrayList<>();
    private final List<BuildingCard> buildings = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    // ── OVERLOAD addCard ────────────────────────────────────────────────────

    // Cacciatore: effetto immediato se ha l'icona.
    // Aggiunge cibo pari al numero di cacciatori in tribù incluso questo.
    public void addCard(HunterCard card) {
        hunters.add(card);
        if (card.hasIcon()) {
            int foodGained = hunters.size();
            addFood(foodGained);
            System.out.println(name + " IMMEDIATO Hunter icona: +" + foodGained + " cibo");
        }
    }

    // Artista: nessun effetto immediato, va solo nella lista.
    public void addCard(ArtistCard card) {
        artists.add(card);
    }

    // Sciamano: nessun effetto immediato, va solo nella lista.
    public void addCard(ShamanCard card) {
        shamans.add(card);
    }

    // Costruttore: nessun effetto immediato.
    // Il suo ppBonus viene conteggiato a fine partita da EndGameResolver.
    public void addCard(BuilderCard card) {
        builders.add(card);
    }

    // Inventore: nessun effetto immediato.
    // L'edificio 5 gestisce il bonus cibo per coppie di inventori (vedi sotto).
    public void addCard(InventorCard card) {
        inventors.add(card);
        // Effetto immediato edificio 5: ogni 2 inventori aggiungi 1 cibo.
        // POTREI USARE LAMBDA per il conteggio icone distinte
        if (hasBuilding(5)) {
            int pairs = inventors.size() / 2;
            // addFood riportiamo il cibo solo per la nuova coppia formata
            if (inventors.size() % 2 == 0) {
                addFood(1);
                System.out.println(name + " IMMEDIATO Edificio5 coppia inventori: +1 cibo");
            }
        }
    }

    // Raccoglitore: nessun effetto immediato, va solo nella lista.
    public void addCard(GathererCard card) {
        gatherers.add(card);
    }

    // Edificio: paga il costo in cibo scontato dal numero di costruttori,
    // poi aggiunge l'edificio alla lista.
    public void addCard(BuildingCard card) {
        int discount = builders.size();
        payFood(Math.max(0, card.getFoodCost() - discount));
        buildings.add(card);
        System.out.println(name + " costruisce: " + card.getName());
    }

    // ── METODI DI SUPPORTO usati dalle EventCard ────────────────────────────

    // Interrogato dalle EventCard per sapere se un edificio modificatore e presente.
    // POTREI USARE LAMBDA: buildings.stream().anyMatch(b -> b.getBuildingId() == id)
    public boolean hasBuilding(int id) {
        for (BuildingCard b : buildings) {
            if (b.getBuildingId() == id) return true;
        }
        return false;
    }

    // Somma delle stelle di tutti gli sciamani in tribù.
    // Usato da RitualEventCard e da Game per calcolare min/max tra player.
    // POTREI USARE LAMBDA: shamans.stream().mapToInt(ShamanCard::getStars).sum()
    public int getShamanStars() {
        int total = 0;
        for (ShamanCard s : shamans) {
            total += s.getStars();
        }
        return total;
    }

    // Numero di icone invenzione DISTINTE tra tutti gli inventori.
    // Usato da EndGameResolver per calcolare i PP degli inventori.
    // POTREI USARE LAMBDA: inventors.stream().map(InventorCard::getInventionIcon).distinct().count()
    public int getDistinctInventorIcons() {
        Set<String> icons = new HashSet<>();
        for (InventorCard i : inventors) {
            icons.add(i.getInventionIcon());
        }
        return icons.size();
    }

    // Somma dei PP bonus di tutti i costruttori in tribù.
    // Usato da EndGameResolver.
    // POTREI USARE LAMBDA: builders.stream().mapToInt(BuilderCard::getPpBonus).sum()
    public int getTotalBuilderPP() {
        int total = 0;
        for (BuilderCard b : builders) {
            total += b.getPpBonus();
        }
        return total;
    }

    // Numero totale di personaggi (tutte le liste sommate).
    // Usato da SustenanceEventCard per calcolare il cibo necessario.
    public int getTotalCharacters() {
        return hunters.size() + artists.size() + shamans.size()
             + builders.size() + inventors.size() + gatherers.size();
    }

    // ── GETTER LISTE ────────────────────────────────────────────────────────
    public List<HunterCard>   getHunters()   { return hunters; }
    public List<ArtistCard>   getArtists()   { return artists; }
    public List<ShamanCard>   getShamans()   { return shamans; }
    public List<BuilderCard>  getBuilders()  { return builders; }
    public List<InventorCard> getInventors() { return inventors; }
    public List<GathererCard> getGatherers() { return gatherers; }
    public List<BuildingCard> getBuildings() { return buildings; }

    // ── RISORSE ─────────────────────────────────────────────────────────────
    public void addPP(int amount)   { pp += amount; }
    public void losePP(int amount)  { pp -= amount; }
    public void addFood(int amount) { food += amount; }
    public void payFood(int amount) { food = Math.max(0, food - amount); }

    public int getPrestigePoints()  { return pp; }
    public int getFood()            { return food; }
    public String getName()         { return name; }
}
