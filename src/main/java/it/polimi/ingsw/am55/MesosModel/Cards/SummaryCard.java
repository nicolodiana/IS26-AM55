package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.dto.CardView;
import it.polimi.ingsw.am55.dto.ClientCards.SummaryCardView;

public class SummaryCard extends Card{

    public SummaryCard(int id, int era) {
        super(id, era);
    }

    public CardView toView() { return new SummaryCardView(this.id); }

}
