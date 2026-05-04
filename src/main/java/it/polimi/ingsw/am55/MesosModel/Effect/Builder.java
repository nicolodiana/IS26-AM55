package it.polimi.ingsw.am55.MesosModel.Effect;
import it.polimi.ingsw.am55.dto.ClientCards.BuilderCardView;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

public class Builder extends CharacterCard {
    int numPP;
    int pickbuildingdiscount; //sconto che forniscono su ogni edificio
    //final CharacterType type = CharacterType.BUILDER;

    public Builder(int id, int numPP, int pickbuildingdiscount, int era) {
        super(id, era);
        this.numPP = numPP;
        this.pickbuildingdiscount = pickbuildingdiscount;
    }

    public int getNumPP() {
        return numPP;
    }

    public int getPickbuildingdiscount() {
        return pickbuildingdiscount;
    }

    public void addToPlayer(Player player) {
        //player.getBuildersList().add(this);
        player.addTribeCard(this);
    }

    public BuilderCardView toView() { return new BuilderCardView(getId(), era, numPP, pickbuildingdiscount); }
}