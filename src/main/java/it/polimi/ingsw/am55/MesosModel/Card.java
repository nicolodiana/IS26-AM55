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

    //mi serve per contare nel player le occorrenze di una carta il cui tipo concreto è visibile solo a runtime (mi serve x edificio 2)
    public int countSameTypeIn(Player player) {
        return 0; // default: non conta nulla (se mettessi senza implementazione obbliga le carte a definire un implementazione (non devo obbligare le building ad esempio ma solo quelle personaggi)
    }


}
