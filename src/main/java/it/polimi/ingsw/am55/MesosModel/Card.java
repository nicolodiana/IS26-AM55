package it.polimi.ingsw.am55.MesosModel;

public abstract class Card {
    private final int id ;
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



}
