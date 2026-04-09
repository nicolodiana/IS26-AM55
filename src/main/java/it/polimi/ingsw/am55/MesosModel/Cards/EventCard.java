package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;

import java.util.List;

public class EventCard extends TribeCard {
    private int numPlayer;
    public EventCard(int id, int era) {
        super(id, era);
    }


    //metodo da overridare negli eventi specifici per attivare l'effetto proprio
    public void activateEvent(List<Player> players) {}

    //metodi per
    public void addInRightRow(Row upperRow, Row lowerRow){
        upperRow.addEventCard(this);
    }

    public void addInRightList(Row row){
        row.addEventCard(this);
    }

    public void setOrder(List<EventCard> events, int index){};
}
