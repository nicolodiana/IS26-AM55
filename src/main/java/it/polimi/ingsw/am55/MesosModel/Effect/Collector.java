package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.ClientCards.ArtistCardView;
import it.polimi.ingsw.am55.dto.ClientCards.CollectorCardView;

/**
 * Character card that represents a collector tribe member.
 * <p>Collectors are added to the player hand and contribute to collection-related effects handled by the model.
 */
public class Collector extends CharacterCard {
    /**
     * Food discount applied by this character effect.
     */
    final private int foodDiscount = 3;
    /**
     * Creates a new collector instance and initializes its internal state.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    public Collector(int id, int era) {
        super(id, era);
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

    /**
     * Builds the client-facing view representation of this collector.
     *
     * @return the client-facing view representation of this collector
     */
    public CollectorCardView toView() { return new CollectorCardView(getId(), getEra()); }
}