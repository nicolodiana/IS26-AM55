package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.ShamanCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Character card that represents a shaman tribe member.
 * <p>Its star value is used by shaman ritual events and related end-of-round scoring.
 */
public class Shaman extends CharacterCard {
    /**
     * Number of shaman stars represented by this card or effect.
     */
    private final int numStars;

    /**
     * Creates a new shaman instance and initializes its internal state.
     *
     * @param id the identifier to use for the object
     * @param numStars the num stars value
     * @param era the era associated with the card
     */
    public Shaman(int id, int numStars, int era) {
        super(id, era);
        this.numStars = numStars;
    }
    /**
     * Applies this card to the specified player according to its game effect.
     *
     * @param player the player affected by the operation
     */
    @Override
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public int getNumStars() {
        return this.numStars;
    }

    /**
     * Builds the client-facing view representation of this shaman.
     *
     * @return the client-facing view representation of this shaman
     */

    public ShamanCardView toView() { return new ShamanCardView(getId(), era, numStars); }
}