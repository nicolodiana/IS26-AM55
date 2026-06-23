package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.InventorCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Character card that represents an inventor tribe member.
 * <p>Inventors expose their invention icon and are added to the player state for invention-related effects.
 */
public class Inventor extends CharacterCard {
    /**
     * Identifier of the invention icon represented by this card.
     */
    private final String iconInvention;

    /**
     * Creates a new inventor instance and initializes its internal state.
     *
     * @param iconInvention the icon invention value
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    public Inventor(String iconInvention, int id, int era) {
        super(id, era);
        this.iconInvention = iconInvention;
    }
    /**
     * Applies this card to the specified player according to its game effect.
     *
     * @param player the player affected by the operation
     */
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }


    public String getIconInvention() {
        return iconInvention;
    }

    /**
     * Builds the client-facing view representation of this inventor.
     *
     * @return the client-facing view representation of this inventor
     */
    public InventorCardView toView() { return new InventorCardView(iconInvention, getId(), era); }
}