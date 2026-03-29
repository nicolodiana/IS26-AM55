package it.polimi.ingsw.am55.MesosModel;

public class BuildingCard extends Card {
    private int foodCost;
    private int numOfPP;
    BuildingType type;
    private CharacterType CharType; // Il personaggio da contare lo inserisco generico Card, poi a runtime capirà cosa è (avrà valore diverso da null) solo se e una building edificio 2 o 12

    public BuildingCard(int id, int era, int foodCost, int numOfPP, BuildingType type, CharacterType CharType) {
        super(id,era);
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.CharType = CharType;
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
