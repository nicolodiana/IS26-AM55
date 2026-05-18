package it.polimi.ingsw.am55.MesosModel.Decks;
import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Enum.BuildingType;
import it.polimi.ingsw.am55.MesosModel.Enum.CharacterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildingDeck {
    List<BuildingCard> buildingCardsList;

    public BuildingDeck(){
        buildingCardsList = new ArrayList<BuildingCard>();
    }

    //Test helper
    public void setBuildingCardsList(List<BuildingCard> buildingCardsList) {
        this.buildingCardsList = buildingCardsList;
    }

    public void initBuildingDeckEra1(int numPlayers) {
        List<BuildingCard> buildingDeckEra1 = new ArrayList<>();
        buildingDeckEra1.add(new BuildingCard( 97, 1, 4, 3, BuildingType.BUILDING1, null, 0));
        buildingDeckEra1.add(new BuildingCard(98, 1, 4, 4, BuildingType.BUILDING2, CharacterType.COLLECTOR, 0));
        buildingDeckEra1.add(new BuildingCard(99, 1, 5, 3, BuildingType.BUILDING2, CharacterType.ARTIST, 0));
        buildingDeckEra1.add(new BuildingCard(100, 1, 5, 2, BuildingType.BUILDING3, null, 0));
        buildingDeckEra1.add(new BuildingCard(101, 1, 3, 3, BuildingType.BUILDING4, null, 0));
        buildingDeckEra1.add(new BuildingCard(102, 1, 3, 4, BuildingType.BUILDING5, null, 0));
        Collections.shuffle(buildingDeckEra1);
        int numCards = buildingDeckEra1.size();
        if (numPlayers == 2){
            for (int i = numCards; i > 1; i--) {
                    buildingDeckEra1.removeFirst();
            }
        } else {
            for (int i = numCards; i > 2; i--) {
                buildingDeckEra1.removeFirst();
            }
        }
        this.buildingCardsList = buildingDeckEra1;
    }
    public void initBuildingDeckEra2(int numPlayers) {
        List<BuildingCard> buildingDeckEra2 = new ArrayList<>();
        buildingDeckEra2.add(new BuildingCard(103, 2, 7, 0, BuildingType.BUILDING7, null, 0));
        buildingDeckEra2.add(new BuildingCard(104, 2, 6, 4, BuildingType.BUILDING6, null, 0));
        buildingDeckEra2.add(new BuildingCard(105, 2, 7, 4, BuildingType.BUILDING2, CharacterType.INVENTOR, 0));
        buildingDeckEra2.add(new BuildingCard(106, 2, 7, 2, BuildingType.BUILDING8, null, 0));
        buildingDeckEra2.add(new BuildingCard(107, 2, 6, 4, BuildingType.BUILDING9, null, 0));
        buildingDeckEra2.add(new BuildingCard(108, 2, 5, 6, BuildingType.BUILDING10, null, 0));
        buildingDeckEra2.add(new BuildingCard(109, 2, 5, 6, BuildingType.BUILDING11, null, 0));
        Collections.shuffle(buildingDeckEra2);
        int numCards = buildingDeckEra2.size();
        if (numPlayers == 2 || numPlayers == 3){
            for (int i = numCards; i > 2; i--) {
                buildingDeckEra2.removeFirst();
            }
        } else {
            for (int i = numCards; i > 3; i--) {
                buildingDeckEra2.removeFirst();
            }
        }
        this.buildingCardsList = buildingDeckEra2;
    }
    public void initBuildingDeckEra3(int numPlayers) {
        List<BuildingCard> buildingDeckEra3 = new ArrayList<>();
        buildingDeckEra3.add(new BuildingCard(110, 3, 8, 8, BuildingType.BUILDING12, CharacterType.HUNTER, 3 ));
        buildingDeckEra3.add(new BuildingCard(111, 3, 7, 6, BuildingType.BUILDING12, CharacterType.COLLECTOR, 4));
        buildingDeckEra3.add(new BuildingCard(112, 3, 7, 4, BuildingType.BUILDING12, CharacterType.SHAMAN, 4));
        buildingDeckEra3.add(new BuildingCard(113, 3, 6, 3, BuildingType.BUILDING12, CharacterType.BUILDER, 4));
        buildingDeckEra3.add(new BuildingCard(114, 3, 7, 4, BuildingType.BUILDING12, CharacterType.ARTIST, 4 ));
        buildingDeckEra3.add(new BuildingCard(115, 3, 6, 6, BuildingType.BUILDING12, CharacterType.INVENTOR,2 ));
        buildingDeckEra3.add(new BuildingCard(116, 3, 9, 3, BuildingType.BUILDING13, null, 0));
        buildingDeckEra3.add(new BuildingCard(117, 3, 10, 0, BuildingType.BUILDING14, null, 0));
        Collections.shuffle(buildingDeckEra3);
        int numCards = buildingDeckEra3.size();
        if (numPlayers == 2){
            for (int i = numCards; i > 3; i--) {
                buildingDeckEra3.removeFirst();
            }
        } else if(numPlayers == 3 || numPlayers == 4){
            for (int i = numCards; i > 4; i--) {
                buildingDeckEra3.removeFirst();
            }
        } else{
            for (int i = numCards; i > 5; i--) {
                buildingDeckEra3.removeFirst();
            }
        }
        this.buildingCardsList = buildingDeckEra3;
    }
    public List<BuildingCard> getBuildingDeck(){
        return this.buildingCardsList;
    }
    public void clear(){
        buildingCardsList.clear();
    }
//    public void swapBuildingDeck(BuildingDeck donor, BuildingDeck receiver){
//        receiver.buildingCardsList = new ArrayList<>(donor.buildingCardsList) ;
//        donor.clear();
//    }
    public void removeBuildingCard(BuildingCard buildingCard){
        buildingCardsList.remove(buildingCard);
    }
    public void removeBuildingCardByIndex(int index){
        buildingCardsList.remove(index);
    }
    public BuildingCard getBuildingCardByIndex(int index){
        return buildingCardsList.get(index);
    }
}
