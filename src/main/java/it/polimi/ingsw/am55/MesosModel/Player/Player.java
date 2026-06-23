package it.polimi.ingsw.am55.MesosModel.Player;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.Card;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Cards.SummaryCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Game.Game;
import it.polimi.ingsw.am55.dto.CardView;

import java.util.*;
/**
 * Represents a player and stores their identity, resources, selected-card counts,
 * tribe cards, buildings, and values used to resolve card effects.
 */
public class Player {
    /**
     * The player's nickname, used as their identifier.
     */
    private final String id;
    /**
     * The totem assigned to the player.
     */
    private String totem;
    /**
     * The player's current prestige points.
     */
    private int numPP;
    /**
     * The player's current amount of food.
     */
    private int numFoods;
    /**
     * The number of cards selected from the upper row.
     */
    private int upperRowCardSelected;
    /**
     * The number of cards selected from the lower row.
     */
    private int lowerRowCardSelected;
    /**
     * The shaman cards in the player's tribe.
     */
    private List<Shaman> shamanList;
    /**
     * The hunter cards in the player's tribe.
     */
    private List<Hunter> hunterList = new ArrayList<>();
    /**
     * The artist cards in the player's tribe.
     */
    private List<Artist> artistList = new ArrayList<>();
    /**
     * The collector cards in the player's tribe.
     */
    private List<Collector> collectorList = new ArrayList<>();
    /**
     * The builder cards in the player's tribe.
     */
    private List<Builder> builderList = new ArrayList<>();
    /**
     * The inventor cards in the player's tribe.
     */
    private List<Inventor> inventorList = new ArrayList<>();
    /**
     * The building cards owned by the player.
     */
    private List<BuildingCard> buildings;
    /**
     * Maps each character type to the corresponding list of cards.
     */
    private Map<CharacterType, List<? extends CharacterCard>> characterLists;
    /**
     * The number of complete character sets already rewarded by Building 1.
     */
    private int minSetCompleted;
    /**
     * The summary card included in the player's hand view.
     */
    private final SummaryCard summeryCard = new SummaryCard(0, 0);

    /**
     * Creates a player with the specified nickname and totem and initializes an empty tribe.
     *
     * @param nickname the player's nickname
     * @param totem    the player's totem
     */
    public Player(String nickname, String totem) {
        this.id = nickname;
        this.totem = totem;
        this.numFoods = 0;
        this.numPP = 0;
        this.upperRowCardSelected = 0;
        this.lowerRowCardSelected = 0;
        this.minSetCompleted = 0;

        this.shamanList = new ArrayList<>();
        this.hunterList = new ArrayList<>();
        this.artistList = new ArrayList<>();
        this.collectorList = new ArrayList<>();
        this.builderList = new ArrayList<>();
        this.inventorList = new ArrayList<>();
        this.buildings = new ArrayList<>();

        this.characterLists = new EnumMap<>(CharacterType.class);
        characterLists.put(CharacterType.SHAMAN, shamanList);
        characterLists.put(CharacterType.HUNTER, hunterList);
        characterLists.put(CharacterType.ARTIST, artistList);
        characterLists.put(CharacterType.INVENTOR, inventorList);
        characterLists.put(CharacterType.BUILDER, builderList);
        characterLists.put(CharacterType.COLLECTOR, collectorList);
    }
    /**
     * Returns the player's nickname.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return id;
    }
    /**
     * Returns the player's current prestige points.
     *
     * @return the current prestige points
     */
    public int getNumPP() {
        return numPP;
    }

    /**
     * Returns the player's current amount of food.
     *
     * @return the current amount of food
     */
    public int getNumFoods() {
        return numFoods;
    }
    /**
     * Returns the player's totem.
     *
     * @return the player's totem
     */
    public String getTotem() {
        return totem;
    }
    /**
     * Decreases the player's prestige points by the specified amount.
     *
     * @param amount the number of prestige points to remove
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void payPP(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numPP = numPP - amount;
    }

    /**
     * Increases the player's prestige points by the specified amount.
     *
     * @param amount the number of prestige points to add
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void addPP(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numPP = numPP + amount;
    }

    /**
     * Increases the player's food by the specified amount.
     *
     * @param amount the amount of food to add
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void addFood(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        numFoods += amount;
    }
    /**
     * Decreases the player's food by the specified amount.
     *
     * @param amount the amount of food to remove
     * @throws IllegalArgumentException if {@code amount} is negative or exceeds the available food
     */
    public void payFood(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount is negative");
        if(amount > numFoods) throw new IllegalArgumentException("The player cannot pay");
        numFoods = numFoods - amount;
    }

    /**
     * Returns the number of cards selected from the upper row.
     *
     * @return the upper-row selection count
     */
    public int getUpperRowCardSelected() {
        return upperRowCardSelected;
    }
    /**
     * Returns the number of cards selected from the lower row.
     *
     * @return the lower-row selection count
     */
    public int getLowerRowCardSelected() {
        return lowerRowCardSelected;
    }
    /**
     * Increments the number of cards selected from the upper row.
     */
    public void addUpperRowCardSelected(){
        upperRowCardSelected++;
    }
    /**
     * Increments the number of cards selected from the lower row.
     */
    public void addLowerRowCardSelected(){
        lowerRowCardSelected++;
    }
    /**
     * Resets both row-selection counters.
     */
    public void clearRowCardsSelected(){
        upperRowCardSelected = 0;
        lowerRowCardSelected = 0;
    }


    /**
     * Awards five food when Building 1 is owned and a new complete character set is formed.
     */
    public  void checkBuilding1() {
        if (hasBuilding(BuildingType.BUILDING1)) {
            int currentSet = minCardSet();
            if (currentSet > minSetCompleted) {
                addFood(5);
                minSetCompleted = currentSet;
            }
        }
    }

    /**
     * Adds a shaman card to the player's tribe and applies the Building 1 effect.
     *
     * @param card the shaman card to add
     */
    public void addTribeCard(Shaman card) {
        shamanList.add(card);
        checkBuilding1();
    }

    /**
     * Adds a hunter card, applies its immediate food effect, and applies the Building 1 effect.
     *
     * @param card the hunter card to add
     */
    public void addTribeCard(Hunter card) {

        hunterList.add(card);
        if (card.getIcon()) { addFood(1 * (hunterList.size())); }
        checkBuilding1();
    }

    /**
     * Adds an artist card to the player's tribe and applies the Building 1 effect.
     *
     * @param card the artist card to add
     */
    public void addTribeCard(Artist card) {
        artistList.add(card);
        checkBuilding1();
    }

    /**
     * Adds a collector card to the player's tribe and applies the Building 1 effect.
     *
     * @param card the collector card to add
     */
    public void addTribeCard(Collector card) {
        collectorList.add(card);
        checkBuilding1();
    }

    /**
     * Adds a builder card to the player's tribe and applies the Building 1 effect.
     *
     * @param card the builder card to add
     */
    public void addTribeCard(Builder card) {
        builderList.add(card);
        checkBuilding1();
    }

    /**
     * Adds an inventor card and applies the Building 5 and Building 1 effects when applicable.
     *
     * @param card the inventor card to add
     */
    public void addTribeCard(Inventor card) {
        if (hasBuilding(BuildingType.BUILDING5)) {
            int countEqualsInvontors = 0;
            for (Inventor inventor : inventorList) {
                if (inventor.getIconInvention().equalsIgnoreCase(card.getIconInvention())) { countEqualsInvontors++; }
            }

            addFood((countEqualsInvontors % 2 == 1) ? 3 : 0);
        }
        inventorList.add(card);
        checkBuilding1();
    }

    /**
     * Pays the discounted cost of a building card and adds it to the player's buildings.
     * When Building 1 is added, records the character sets already completed.
     *
     * @param card the building card to add
     * @throws IllegalArgumentException if the player cannot pay the discounted cost
     */
    public void addTribeCard(BuildingCard card) {

        int buildingCost = card.getFoodCost() - totalBuildingDiscount();
        buildingCost = Math.max(0, buildingCost);


        this.payFood(buildingCost);
        buildings.add(card);

        if (card.getType().equals(BuildingType.BUILDING1)) {
            minSetCompleted = minCardSet();
        }


    }


    /**
     * Returns the player's building cards.
     *
     * @return the building cards
     */
    public List<BuildingCard> getBuildings() { return buildings; }
    /**
     * Returns the player's shaman cards.
     *
     * @return the shaman cards
     */
    public List<Shaman> getShamansList() { return shamanList; }
    /**
     * Returns the player's hunter cards.
     *
     * @return the hunter cards
     */
    public List<Hunter> getHuntersList() { return hunterList; }
    /**
     * Returns the player's inventor cards.
     *
     * @return the inventor cards
     */
    public List<Inventor> getInventorsList() { return inventorList; }
    /**
     * Returns the player's builder cards.
     *
     * @return the builder cards
     */
    public List<Builder> getBuildersList() { return builderList; }
    /**
     * Returns the player's collector cards.
     *
     * @return the collector cards
     */
    public List<Collector> getCollectorsList() { return collectorList; }
    /**
     * Returns the player's artist cards.
     *
     * @return the artist cards
     */
    public List<Artist> getArtistsList() { return artistList; }

    /**
     * Returns the total number of character cards in the player's tribe.
     *
     * @return the number of character cards
     */
    public int playerDeckSize() {
        return shamanList.size() + hunterList.size() + artistList.size()
                + inventorList.size() + builderList.size() + collectorList.size();
    }

    /**
     * Checks whether the player owns a building of the specified type.
     *
     * @param type the building type to search for
     * @return {@code true} if the player owns the building; {@code false} otherwise
     */
    public boolean hasBuilding(BuildingType type) {
        for (BuildingCard bc : buildings) {
            if (bc.getType().equals(type)) return true;
        }
        return false;
    }

    /**
     * Returns the total number of shaman stars, including the Building 6 bonus.
     *
     * @return the total number of shaman stars
     */
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
    /**
     * Returns the number of character cards of the specified type.
     *
     * @param type the character type to count
     * @return the number of cards of the specified type
     */
    public int countByType(CharacterType type) {
        return characterLists.get(type).size();
    }

    /**
     * Returns the number of complete character sets in the player's tribe.
     *
     * @return the smallest card count among all character types
     */
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

    /**
     * Returns the player's identifier.
     *
     * @return the player's identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Creates the card views representing the player's summary, tribe, and buildings.
     *
     * @return the player's card views
     */
    public List<CardView> giveMyHand() {
        List<CardView> list = new ArrayList<>();

        list.add(summeryCard.toView());

        for (Shaman card : this.shamanList) { list.add(card.toView()); }
        for (Artist card : this.artistList) { list.add(card.toView()); }
        for (Builder card : this.builderList) { list.add(card.toView()); }
        for (Inventor card : this.inventorList) { list.add(card.toView()); }
        for (Hunter card : this.hunterList) { list.add(card.toView()); }
        for (Collector card : this.collectorList) { list.add(card.toView()); }
        for (BuildingCard card : this.buildings) { list.add(card.toView()); }

        return list;
    }

    /**
     * Returns the total building discount provided by the player's builder cards.
     *
     * @return the total building discount
     */
    public int totalBuildingDiscount() {
        int builderDiscount = 0;

        for (Builder b : builderList) {
            builderDiscount += b.getPickbuildingdiscount();
        }

        return builderDiscount;
    }
}

