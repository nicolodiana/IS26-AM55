package it.polimi.ingsw.am55.MesosModel.Cards;

import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;
import it.polimi.ingsw.am55.MesosModel.Player.Player;
import it.polimi.ingsw.am55.MesosModel.SharedBoard.Row;
import it.polimi.ingsw.am55.dto.ClientCards.BuildingCardView;

public class BuildingCard extends Card {
    private int foodCost;
    private int numOfPP;
    BuildingType type;
    private CharacterType charType;
    private int effectPP;

    public BuildingCard(int id, int era, int foodCost, int numOfPP, BuildingType type, CharacterType charType, int effectPP){
        super(id,era);
        this.foodCost = foodCost;
        this.numOfPP = numOfPP;
        this.type = type;
        this.charType = charType;
        this.effectPP = effectPP;
    }

    //per gli edifici 2, se non è un edificio 2 non genera sconto
    public int getSustenanceDiscount(Player p) {
        if (type == BuildingType.BUILDING2 && charType!=null) {
            return p.countByType(charType);
        }
        return 0;
    }


    public int getEndGameBonus(Player p) {
        if (type == BuildingType.BUILDING12 && charType!=null) {
            return p.countByType(charType);
        }
        return 0;
    }

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

    public BuildingCardView toView() { return new BuildingCardView(getId(), era, foodCost, numOfPP, type, charType, effectPP); }

}
