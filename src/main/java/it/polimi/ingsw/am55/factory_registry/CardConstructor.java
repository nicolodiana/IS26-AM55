package it.polimi.ingsw.am55.factory_registry;

import it.polimi.ingsw.am55.ClientModel.ClientCard;
import it.polimi.ingsw.am55.MesosModel.Cards.Card;
import it.polimi.ingsw.am55.dto.CardView;

public interface CardConstructor {
    public ClientCard create(int id, ClientCard card);
    CardView createCardView(Card card);
}
