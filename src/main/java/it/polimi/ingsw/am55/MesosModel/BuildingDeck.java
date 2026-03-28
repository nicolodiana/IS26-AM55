package it.polimi.ingsw.am55.MesosModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildingDeck {
    List<BuildingCard> BuildingCardsList;

    public BuildingDeck(){
        BuildingCardsList = new ArrayList<BuildingCard>();
    }

    public void initBuildingDeckEra1( int numPlayers) {
        List<BuildingCard> buildingDeckEra1 = new ArrayList<>();
        switch (numPlayers) {
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
        }
        Collections.shuffle(buildingDeckEra1);
        this.BuildingCardsList = buildingDeckEra1;
    }
    public void initBuildingDeckEra2(int numPlayer) {
        List<BuildingCard> buildingDeckEra2 = new ArrayList<>();
        switch (numPlayer) {
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
        }
        Collections.shuffle(buildingDeckEra2);
        this.BuildingCardsList = buildingDeckEra2;
    }
    public void initBuildingDeckEra3(int numPlayer) {
        List<BuildingCard> buildingDeckEra3 = new ArrayList<>();
        switch (numPlayer) {
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
        }
        Collections.shuffle(buildingDeckEra3);
        this.BuildingCardsList = buildingDeckEra3;
    }

    public List<BuildingCard> getBuildingDeck(){
        return this.BuildingCardsList;
    }
    public void clear(){
        BuildingCardsList.clear();
    }

    public void swapBuildingDeck(BuildingDeck donor, BuildingDeck receiver){
        receiver.BuildingCardsList = new ArrayList<>(donor.BuildingCardsList) ;
        donor.clear();
    }

    public void removeBuildingCard(BuildingCard buildingCard){
        BuildingCardsList.remove(buildingCard);
    }

    //just for testing
    public void addBuildingCard(BuildingCard buildingCard){
        BuildingCardsList.add(buildingCard);
    }

}
