package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.MesosModel.*;

public class Builder extends CharacterCard {
    int numPP;
    int pickbuildingdiscount; //sconto che forniscono su ogni edificio

    public Builder(int numPP,int pickbuildingdiscount,int era) {
        this.numPP = numPP;
        this.pickbuildingdiscount = pickbuildingdiscount;
        this.era= era;
    }

    public int getNumPP() {
        return numPP;
    }
    public int getPickbuildingdiscount() {return pickbuildingdiscount; }

    public void addToPlayer(Player player) {
        //player.getBuildersList().add(this);
        player.addTribeCard(this);
    }
    public int countSameTypeIn(Player player) {
        return player.getBuildersList().size(); // so che sono un Hunter, conto gli Hunter
    }
}
