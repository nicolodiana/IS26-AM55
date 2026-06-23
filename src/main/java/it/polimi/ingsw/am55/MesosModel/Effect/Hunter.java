package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.HunterCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Character card that represents a hunter tribe member.
 * <p>Hunters may carry hunting icons used by hunting events to compute food and prestige-point effects.
 */
public class Hunter extends CharacterCard {
    /**
     * Whether the card carries the icon required by its effect.
     */
    private final Boolean icon;

    /**
     * Creates a new hunter instance and initializes its internal state.
     *
     * @param id the identifier to use for the object
     * @param icon the icon value
     * @param era the era associated with the card
     */
    public Hunter(int id, Boolean icon, int era) {
        super(id, era);
        this.icon = icon;
    }

    /**
     * Applies this card to the specified player according to its game effect.
     *
     * @param player the player affected by the operation
     */
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public Boolean getIcon() {
        return icon;
    }

    /**
     * Builds the client-facing view representation of this hunter.
     *
     * @return the client-facing view representation of this hunter
     */
    public HunterCardView toView() { return new HunterCardView(getId(), icon, era); }
}
