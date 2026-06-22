package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.dto.CardView;

public abstract class Card {
    protected final int id ;
    public final  int era;

    protected Card(int id, int era) {
        this.id = id;
        this.era = era;
    }

    public int getEra() {
        return era;
    }
    public int getId() {return id;}

    //Metodo override per gestire aggiunta nel player con i tipi dinamici
    public void addToPlayer(Player player) {}
    public CardView toView() { return new CardView(); }

}
