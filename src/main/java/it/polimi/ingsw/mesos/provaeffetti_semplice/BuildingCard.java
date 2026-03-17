package it.polimi.ingsw.mesos.provaeffetti_semplice;

// NON specializzata: tutti gli edifici sono strutturalmente identici.
// Si distinguono solo per buildingId, interrogato con hasBuilding(id) in Player.
// Gli edifici sono modificatori passivi degli eventi, non attori attivi.
public class BuildingCard extends Card {
    private final int buildingId;
    private final int foodCost;

    public BuildingCard(String name, int buildingId, int foodCost) {
        super(name);
        this.buildingId = buildingId;
        this.foodCost = foodCost;
    }

    public int getBuildingId() { return buildingId; }
    public int getFoodCost()   { return foodCost; }
}
