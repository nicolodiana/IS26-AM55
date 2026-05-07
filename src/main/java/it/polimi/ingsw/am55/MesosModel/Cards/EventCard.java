package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveHuntingView;

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

    public int getOrder(){return 0;}

    public ResolveEventView toViewResolve(){ return new ResolveEventView(null, null); }
}
