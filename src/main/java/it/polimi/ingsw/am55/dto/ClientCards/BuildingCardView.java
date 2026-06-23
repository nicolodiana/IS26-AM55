package it.polimi.ingsw.am55.dto.ClientCards;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.view.cli.CliCardDetails;
import it.polimi.ingsw.am55.view.cli.CliCardInfo;
import it.polimi.ingsw.am55.view.cli.ConsoleColor;

import java.util.List;

/**
 * DTO for a building card.
 * <p>It exposes cost, type, discounts, bonuses, and CLI metadata required to render the building.
 */
public class BuildingCardView extends CardView {
    /**
     * Food cost required to obtain this card.
     */
    private int foodCost;
    /**
     * DTO field carrying the num of prestige points value.
     */
    private int numOfPP;
    /**
     * Building type represented by this card.
     */
    BuildingType type;
    /**
     * Character type associated with the building effect.
     */
    private CharacterType CharType;
    /**
     * Prestige points produced by the building effect.
     */
    private int effectPP;

    /**
     * Creates a building card view from model data that can be sent to the client.
     *
     * @param id the identifier of this card
     * @param era the era associated with the card
     * @param foodCost the food cost value
     * @param numOfPP the prestige-point value involved in the operation
     * @param type the type used to select the proper rule or card behavior
     * @param CharType the type used in the effect
     * @param effectPP the prestige-point bonus granted by the effect
     */
    public BuildingCardView(int id, int era, int foodCost, int numOfPP, BuildingType type, CharacterType CharType, int effectPP){
        super(id,era);
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.CharType = CharType;
        this.effectPP = effectPP;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public BuildingType getType() {
        return type;
    }

    @Override
    public String toString() {
        return //type + " " +
                "Id: " + id +
                "\nFood cost: " + foodCost +
                "\nNum of PP: " + numOfPP +
                "\nType: " + type +
                "\nCharType: " + CharType +
                "\nEffect to PP: " + effectPP;
    }

    /**
     * Builds the CLI rendering metadata used to display this card.
     *
     * @return the CLI rendering metadata for this card
     */
    public CliCardInfo getCliCardInfo() {
        return new CliCardInfo(
                "CHARACTER",
                ConsoleColor.YELLOW_BOLD,
                List.of(
                        new CliCardDetails("Type", "Building"),
                        new CliCardDetails("Info", this.toString())
                )
        );
    }
}
