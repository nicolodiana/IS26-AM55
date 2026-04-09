package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;

public class BuildingCard extends Card {
    private int foodCost;
    private int numOfPP;
    BuildingType type;
    private CharacterType CharType;// Il personaggio da contare lo inserisco generico Card, poi a runtime capirà cosa è (avrà valore diverso da null) solo se e una building edificio 2 o 12
    private int effectPP;

    public BuildingCard(int id, int era, int foodCost, int numOfPP, BuildingType type, CharacterType CharType, int effectPP) {
        super(id,era);
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.CharType = CharType;
        this.effectPP = effectPP;
    }
    //devo gestire il caso se non ne ha abbastanza
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }

    public BuildingType getType() {
        return type;
    }

    public int getNumOfPP() {
        return numOfPP;
    }
    public int getFoodCost() {return foodCost;}

    public void removeBuildingCardFromRow(Row row){
        row.getBuildingCardsList().removeBuildingCard(this);
    }

    public int bonusCharType(Player p) {
        int ris = 0;

        switch (CharType) {
            case SHAMAN -> ris = p.sizeShamans();
            case HUNTER -> ris = p.sizeHunters();
            case INVENTOR -> ris = p.sizeInventors();
            case COLLECTOR -> ris = p.sizeCollectors();
            case BUILDER -> ris = p.sizeBuilders();
            case ARTIST -> ris = p.sizeArtists();
        }
        return ris;
    }

}
