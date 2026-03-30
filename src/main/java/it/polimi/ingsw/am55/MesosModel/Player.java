package it.polimi.ingsw.am55.MesosModel;

import it.polimi.ingsw.am55.MesosModel.Effect.*;

import java.util.*;
/**
 * DESCRIPTION: Represents a player in the game.
 * This class stores the full state of a player, including identification data,
 * resources, selected cards, owned tribe characters, constructed buildings,
 * and auxiliary values used to resolve game effects.
 * A Player keeps track of:
 *     The player's identity and personal information,
 *     victory or prestige-related resources and food,
 *     selected cards from the upper and lower rows,
 *     owned tribe cards grouped by character type,
 *     owned building cards and effect-related counters.
 * The first part of the class provides basic initialization, getters, and
 * resource management methods.
 *
 */
public class Player {
    private final String id;
    private final String nickname;
    private String totem;
    private int numPP;
    private int numFoods;
    private String summaryCard;
    private int upperRowCardSelected;
    private int lowerRowCardSelected;
    private List<Shaman> shamanList;
    private List<Hunter> hunterList = new ArrayList<>();
    private List<Artist> artistList = new ArrayList<>();
    private List<Collector> collectorList = new ArrayList<>();
    private List<Builder> builderList = new ArrayList<>();
    private List<Inventor> inventorList = new ArrayList<>();
    private List<BuildingCard> buildings;
    private Map<CharacterType, List<CharacterCard>> buildingsByCharacterType;
    private int minSetCompleted;
    /**
     * Creates a new player with the given nickname, totem, and summary card.
     * A random unique identifier is generated automatically. Resources and card
     * selection counters are initialized to zero.
     *
     * @param nickname the nickname of the player
     * @param totem the totem assigned to the player
     * @param summaryCard the summary card assigned to the player
     */
    public Player(String nickname, String totem, String summaryCard) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.totem = totem;
        this.summaryCard = summaryCard;
        this.numFoods = 0;
        this.numPP = 0;
        this.upperRowCardSelected = 0;
        this.lowerRowCardSelected = 0;
        this.minSetCompleted = 0;
    }
    /**
     * Returns the player's nickname.
     * @return the nickname of the player
     */
    public String getNickname() {
        return nickname;
    }
    /**
     * Returns the current amount of player points.
     * @return the player's current points
     */
    public int getNumPP() {
        return numPP;
    }

    /**
     * Returns the current amount of food.
     * @return the player's current food
     */
    public int getNumFoods() {
        return numFoods;
    }
    /**
     * Returns the player's totem.
     * @return the player's totem
     */
    public String getTotem() {
        return totem;
    }
    /**
     * Decreases the player's points by the specified amount.
     * @param amount the amount of points to spend
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void payPP(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numPP = numPP - amount;
    }

    /**
     * Increases the player's points by the specified amount.
     * @param amount the amount of points to add
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void addPP(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numPP = numPP + amount;
    }

    /**
     * Increases the player's food by the specified amount.
     * @param amount the amount of food to add
     * @throws IllegalArgumentException if  amount is negative
     */
    public void addFood(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numFoods += amount;
    }
    /**
     * Decreases the player's food by the specified amount.
     * @param amount the amount of food to pay
     * @throws IllegalArgumentException if  amount is negative
     */
    public void payFood(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numFoods = numFoods - amount;
    }

    public int getUpperRowCardSelected() {
        return upperRowCardSelected;
    }
    public int getLowerRowCardSelected() {
        return lowerRowCardSelected;
    }
    public void addUpperRowCardSelected(){
        upperRowCardSelected++;
    }
    public void addLowerRowCardSelected(){
        lowerRowCardSelected++;
    }
    public void clearRowCardsSelected(){
        upperRowCardSelected = 0;
        lowerRowCardSelected = 0;
    }


    // Edificio 1 (effetto passivo su pescaggio personaggi):
    // Dal momento in cui si possiede BUILDING1, si guadagnano 5 cibi ogni volta che
    // si completa un nuovo set di 6 carte personaggio di tipo diverso (1 per tipo).
    // Non conta i set già completati prima dell'acquisizione dell'edificio:
    // quando si acquisisce BUILDING1, minSetCompleted viene allineato al valore attuale
    // di minCardSet() cosi i set già fatti non vengono premiati.
    // Ad ogni aggiunta di personaggio si controlla se minCardSet() è aumentato
    // rispetto a minSetCompleted: se sì, si è completato un nuovo set e si aggiungono 5 cibi.
    public  void checkBuilding1() {
        if (hasBuilding(BuildingType.BUILDING1)) {
            int currentSet = minCardSet();
            if (currentSet > minSetCompleted) {
                addFood(5);
                minSetCompleted = currentSet;
            }
        }
    }

    //ADD CARD CON EFFETTI ISTANTANEI
    public void addTribeCard(Shaman card) {
        //card.addCard(this);
        shamanList.add(card);
        checkBuilding1();
    }

    public void addTribeCard(Hunter card) {
        //card.addCard(this);
        //aggiunge cacciatore e se ha icona si guadagna 1 cibo x ogni cacciatore in tribù (effetto istantaneo)
        hunterList.add(card);
        if (card.getIcon()) { addFood(1 * (hunterList.size())); }
        checkBuilding1();
    }

    public void addTribeCard(Artist card) {
        //card.addCard(this);
        artistList.add(card);
        checkBuilding1();
    }

    public void addTribeCard(Collector card) {
        //card.addCard(this);
        collectorList.add(card);
        checkBuilding1();
    }

    // Il suo ppBonus viene conteggiato a fine partita da EndGameResolver.
    public void addTribeCard(Builder card) {
        //card.addCard(this);
        builderList.add(card);
        checkBuilding1();
    }

    public void addTribeCard(Inventor card) {
        //Effetto edificio 5
        if (hasBuilding(BuildingType.BUILDING5)) {
            int countEqualsInvontors = 0;
            //equalsIgnoreCase controlla prima dell'aggiunta se 2 stringhe (invenzione) (poi json) sono uguali, ignorando uppercase e formattazione stringa
            for (Inventor inventor : inventorList) {
                if (inventor.getIconInvention().equalsIgnoreCase(card.getIconInvention())) { countEqualsInvontors++; }
            }
            // se prima dell'aggiunta erano dispari, con questa carta completo una nuova coppia
            // e quindi guadagno 3 cibi.
            addFood((countEqualsInvontors % 2 == 1) ? 3 : 0);
        }
        //card.addCard(this);
        inventorList.add(card);
        checkBuilding1();
    }

    public void addTribeCard(BuildingCard card) {
        try {
            int builderDiscount = 0;
            //calcolo costo degli sconti in base ai costruttori che ho
            for (Builder b : builderList) {
                builderDiscount += b.getPickbuildingdiscount();
            }

            int buildingCost = card.getFoodCost() - builderDiscount;
            buildingCost = Math.max(0, buildingCost); //se lo sconto è maggiore del costo dovuto, setto un minimo di 0

            if (this.getNumFoods() < buildingCost) { //se non ho abbastanza cibo non la prendo e lancio eccezione poi inviata anche alla view
                throw new IllegalArgumentException("Non puoi pescarla!");
            }

            this.payFood(buildingCost);
            buildings.add(card);

            // se la building card appena aggiunta è BUILDING1,
            // allinea minSetCompleted ai set già completati così non li conta retroattivamente
            if (card.getType().equals(BuildingType.BUILDING1)) {
                minSetCompleted = minCardSet();
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // ─── GETTER LISTE ────────────────────────────────────────────────────────────

    public List<BuildingCard> getBuildings() { return buildings; }
    public List<Shaman> getShamansList() { return shamanList; }
    public List<Hunter> getHuntersList() { return hunterList; }
    public List<Inventor> getInventorsList() { return inventorList; }
    public List<Builder> getBuildersList() { return builderList; }
    public List<Collector> getCollectorsList() { return collectorList; }
    public List<Artist> getArtistsList() { return artistList; }

    // ─── UTILITY ─────────────────────────────────────────────────────────────────

    public int playerDeckSize() {
        return shamanList.size() + hunterList.size() + artistList.size()
                + inventorList.size() + builderList.size() + collectorList.size();
    }

    public boolean hasBuilding(BuildingType type) {
        for (BuildingCard bc : buildings) {
            if (bc.type.equals(type)) return true;
        }
        return false;
    }

    public int countShamanStars() {
        int totalStars = 0;
        for (Shaman s : shamanList) {
            totalStars += s.getNumStars();
        }
        if (hasBuilding(BuildingType.BUILDING6)) {
            totalStars += 3;
        }
        return totalStars;
    }
    //metodo per sapere il conteggio in base al tipo di personaggio: SERVE PER EDIFICIO 2,12
    public int countByTypeforED(CharacterType type) {
        return buildingsByCharacterType.get(type).size();
    }
    public int minCardSet() {
        return Collections.min(Arrays.asList(
                shamanList.size(),
                hunterList.size(),
                artistList.size(),
                collectorList.size(),
                builderList.size(),
                inventorList.size()
        ));
    }

    //METODI SIZE
    public int sizeShamans() { return shamanList.size(); }
    public int sizeHunters() { return hunterList.size(); }
    public int sizeArtists() { return artistList.size(); }
    public int sizeBuilders() { return builderList.size(); }
    public int sizeCollectors() { return collectorList.size(); }
    public int sizeInventors() { return inventorList.size(); }
}
