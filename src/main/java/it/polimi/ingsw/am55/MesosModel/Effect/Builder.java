package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.BuilderCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Character card that represents a builder tribe member.
 * <p>The card can grant prestige points and building-pick discounts according to its configured effect values.
 */
public class Builder extends CharacterCard {
    /**
     * Number of prestige points granted or required by this element.
     */
    int numPP;
    /**
     * Effect model field storing the pickbuildingdiscount value used during effect resolution.
     */
    int pickbuildingdiscount;

    /**
     * Creates a new builder instance and initializes its internal state.
     *
     * @param id the identifier to use for the object
     * @param numPP the num pp value
     * @param pickbuildingdiscount the pickbuildingdiscount value
     * @param era the era associated with the card
     */
    public Builder(int id, int numPP, int pickbuildingdiscount, int era) {
        super(id, era);
        this.numPP = numPP;
        this.pickbuildingdiscount = pickbuildingdiscount;
    }

    public int getNumPP() {
        return numPP;
    }

    public int getPickbuildingdiscount() {
        return pickbuildingdiscount;
    }

    /**
     * Applies this card to the specified player according to its game effect.
     *
     * @param player the player affected by the operation
     */
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    /**
     * Builds the client-facing view representation of this builder.
     *
     * @return the client-facing view representation of this builder
     */
    public BuilderCardView toView() {
        return new BuilderCardView(
                getId(),
                getEra(),
                numPP,
                pickbuildingdiscount
        );
    }
}