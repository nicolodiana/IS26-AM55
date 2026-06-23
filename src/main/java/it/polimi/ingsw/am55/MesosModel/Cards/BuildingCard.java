package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
import it.polimi.ingsw.am55.dto.ClientCards.BuildingCardView;
/**
 * Model representation of a building card.
 * <p>A building card has a food cost, may provide permanent discounts or end-game bonuses, and is added to a player when bought from the board.
 */
public class BuildingCard extends Card {
    /**
     * Food cost required to obtain this card or effect.
     */
    private int foodCost;
    /**
     * Card model field storing the num of prestige points value.
     */
    private int numOfPP;
    /**
     * Building type represented by this card or view.
     */
    BuildingType type;
    /**
     * Character type associated with the building effect.
     */
    private CharacterType charType;
    /**
     * Prestige points produced by the building effect.
     */
    private int effectPP;

    /**
     * Creates a building card with its card metadata and rule values.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     * @param foodCost the food value involved in the operation
     * @param numOfPP the prestige-point value involved in the operation
     * @param type the type used to select the proper rule or card behavior
     * @param charType the type used to select the proper rule or card behavior
     * @param effectPP the prestige-point bonus granted by the effect
     */
    public BuildingCard(int id, int era, int foodCost, int numOfPP, BuildingType type, CharacterType charType, int effectPP){
        super(id,era);
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.charType = charType;
        this.effectPP = effectPP;
    }

    /**
     * Computes the food discount granted by this building during the sustenance phase.
     *
     * @param p the player affected by the operation
     * @return the sustenance discount value
     */
    public int getSustenanceDiscount(Player p) {
        if (type == BuildingType.BUILDING2 && charType!=null) {
            return p.countByType(charType);
        }
        return 0;
    }

    /**
     * Computes the end-game prestige-point bonus granted by this building for the specified player.
     *
     * @param p the player affected by the operation
     * @return the end game bonus value
     */
    public int getEndGameBonus(Player p) {
        if (type == BuildingType.BUILDING12 && charType!=null) {
            return p.countByType(charType);
        }
        return 0;
    }
    /**
     * Applies this card to the specified player according to its game effect.
     *
     * @param player the player affected by the operation
     */
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }



    public BuildingType getType() {
        return type;
    }

    public int getNumOfPP() {
        return numOfPP;
    }
    public int getFoodCost() {return foodCost;}

    /**
     * Removes this building card from the specified board row.
     *
     * @param row the board row affected by the operation
     */
    public void removeBuildingCardFromRow(Row row){
        row.getBuildingCardsList().removeBuildingCard(this);
    }

    /**
     * Builds the client-facing view representation of this building card.
     *
     * @return the client-facing view representation of this building card
     */
    public BuildingCardView toView() { return new BuildingCardView(getId(), era, foodCost, numOfPP, type, charType, effectPP); }

}
