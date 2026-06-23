package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.ArtistCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

/**
 * Character card that represents an artist tribe member.
 * <p>When added to a player, it contributes to artist-related scoring and cave-painting event resolution.
 */
public class Artist extends CharacterCard {

    /**
     * Creates a new artist instance and initializes its internal state.
     *
     * @param id the identifier to use for the object
     * @param era the era associated with the card
     */
    public Artist(int id, int era){
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
     * Builds the client-facing view representation of this artist.
     *
     * @return the client-facing view representation of this artist
     */
    public ArtistCardView toView() { return new ArtistCardView(getId(), era); }
}