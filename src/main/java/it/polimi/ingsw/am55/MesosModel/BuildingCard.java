package it.polimi.ingsw.am55.MesosModel;

public class BuildingCard extends Card {
    private int foodCost;
    private int numOfPP;
    BuildingType type;
    private Card characterForED; // Il personaggio da contare lo inserisco generico Card, poi a runtime capirà cosa è (avrà valore diverso da null) solo se e una building edificio 2 o 12

    public BuildingCard(int foodCost, int numOfPP, BuildingType type, Card characterForED) {
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.characterForED = characterForED;
    }
    //devo gestire il caso se non ne ha abbastanza
    public void addToPlayer(Player player) {
        player.addTribeCard(this);
    }


    public Card getCharacterForED() {
        return characterForED;
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



}
