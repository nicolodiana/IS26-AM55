package it.polimi.ingsw.am55.dto;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Effect.*;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.*;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.am55.MesosModel.Enum.CharacterType.*;

public class PlayerView implements Serializable {

    private final String nickname ;
    private final String totemColor;
    private int food;
    private int points;
    private List<CardView> myHand = new ArrayList<>();

    private Map<CharacterType, Integer> counterTypeCard = new HashMap<>(Map.of(
            SHAMAN, 0,
            BUILDER, 0,
            COLLECTOR,0,
            HUNTER, 0,
            INVENTOR, 0,
            ARTIST, 0
    ));

    //private int counterHunterCard = 0;
    private int minSetCompleted = 0;
    private int buildersDiscount = 0;
    private List<String> inventorIcons = new ArrayList<>();
    private List<BuildingType> buildingsType = new ArrayList<>();

    public PlayerView(Player player) {
        this.nickname = player.getNickname();
        this.totemColor = player.getTotem();
        this.food = player.getNumFoods();
        this.points = player.getNumPP();
        this.myHand = player.giveMyHand();
    }

    public String getNickname() {
        return nickname;
    }

    public String getTotemColor() {
        return totemColor;
    }

    public int getFood() {
        return food;
    }

    public int getPoints() {
        return points;
    }

    public List<CardView> getMyHand() {
        return myHand;
    }

    public void pickCard(CardView card) {
        this.myHand.add(card);
    }

    public void addTribeCard(ShamanCardView card) {
        this.myHand.add(card);
        this.counterTypeCard.put(SHAMAN, this.counterTypeCard.get(SHAMAN) + 1);
    }

    public void addTribeCard(HunterCardView card) {
        //this.counterHunterCard++;
        //aggiunge cacciatore e se ha icona si guadagna 1 cibo x ogni cacciatore in tribù (effetto istantaneo)
        this.myHand.add(card);

        this.counterTypeCard.put(HUNTER, this.counterTypeCard.get(HUNTER) + 1);

        if (card.getIcon()) { addFood(this.counterTypeCard.get(HUNTER)); }
        checkBuilding1();
    }

    public void addTribeCard(ArtistCardView card) {
        //card.addCard(this);
        this.myHand.add(card);
        this.counterTypeCard.put(ARTIST, this.counterTypeCard.get(ARTIST) + 1);

        checkBuilding1();
    }

    public void addTribeCard(CollectorCardView card) {
        this.myHand.add(card);
        this.counterTypeCard.put(COLLECTOR, this.counterTypeCard.get(COLLECTOR) + 1);

        checkBuilding1();
    }

    // Il suo ppBonus viene conteggiato a fine partita da EndGameResolver.
    public void addTribeCard(BuilderCardView card) {
        this.myHand.add(card);
        this.counterTypeCard.put(BUILDER, this.counterTypeCard.get(BUILDER) + 1);

        checkBuilding1();
    }

    public void addTribeCard(InventorCardView card) {
        this.inventorIcons.add(card.getIconInvention());
        this.counterTypeCard.put(INVENTOR, this.counterTypeCard.get(INVENTOR) + 1);

        //Effetto edificio 5
        if (hasBuilding(BuildingType.BUILDING5)) {
            int countEqualsInvontors = 0;
            //equalsIgnoreCase controlla prima dell'aggiunta se 2 stringhe (invenzione) (poi json) sono uguali, ignorando uppercase e formattazione stringa
            for (String icon : this.inventorIcons) {
                if (icon.equalsIgnoreCase(card.getIconInvention())) { countEqualsInvontors++; }
            }
            // se prima dell'aggiunta erano dispari, con questa carta completo una nuova coppia
            // e quindi guadagno 3 cibi.
            addFood((countEqualsInvontors % 2 == 1) ? 3 : 0);
        }

        this.myHand.add(card);
        checkBuilding1();
    }

    public void addTribeCard(BuildingCardView card) {

        int buildingCost = card.getFoodCost() - buildersDiscount;
        buildingCost = Math.max(0, buildingCost); //se lo sconto è maggiore del costo dovuto, setto un minimo di 0


        this.payFood(buildingCost);

        this.myHand.add(card);
        this.buildingsType.add(card.getType());
        // se la building card appena aggiunta è BUILDING1,
        // allinea minSetCompleted ai set già completati così non li conta retroattivamente
        if (card.getType().equals(BuildingType.BUILDING1)) {
            minSetCompleted = minCardSet();
        }


    }

    private void payFood(int food) {
        this.food -= food;
    }

    private void addFood(int food) {
        this.food += food;
    }

    public  void checkBuilding1() {
        if (hasBuilding(BuildingType.BUILDING1)) {
            int currentSet = minCardSet();
            if (currentSet > minSetCompleted) {
                addFood(5);
                minSetCompleted = currentSet;
            }
        }
    }

    public int minCardSet() {
        return Collections.min(Arrays.asList(
                this.counterTypeCard.get(SHAMAN),
                this.counterTypeCard.get(BUILDER),
                this.counterTypeCard.get(COLLECTOR),
                this.counterTypeCard.get(INVENTOR),
                this.counterTypeCard.get(HUNTER),
                this.counterTypeCard.get(ARTIST)
        ));
    }

    public boolean hasBuilding(BuildingType type) {
        if (buildingsType.contains(type)) return true;
        return false;
    }
}
