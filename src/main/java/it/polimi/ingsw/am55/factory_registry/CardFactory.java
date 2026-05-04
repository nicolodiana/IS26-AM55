package it.polimi.ingsw.am55.factory_registry;

import it.polimi.ingsw.am55.ClientModel.ClientCard;
import it.polimi.ingsw.am55.MesosModel.Cards.Card;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.dto.CardView;

public class CardFactory {
    private CardLoader loader;

    public CardFactory(CardLoader loader) {
        this.loader = loader;
    }

    public ClientCard createCard(String cardId) {
        /*ClientCard card = loader.getCard(cardId);
        CardConstructor cardConstructor = CardRegistry.getCard(card.getType());

        if (cardConstructor == null) {
            throw new IllegalArgumentException("Type of card is not registered");
        }

        return cardConstructor.create(Integer.parseInt(cardId), card);*/
        return null;
    }

    /*public CardView createCardView(CharacterType type, Card card) {
        CardConstructor constructor = CardViewRegistry.getCard(type);

        if (constructor == null) {
            throw new IllegalArgumentException("Type of card is not registered");
        }

        return constructor.createCardView(card);

    }*/
}
