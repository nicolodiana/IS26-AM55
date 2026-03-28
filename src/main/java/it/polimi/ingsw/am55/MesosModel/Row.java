package it.polimi.ingsw.am55.MesosModel;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<CharacterCard> characterCardsList;
    private List<EventCard> eventCardsList;
    private BuildingDeck buildingCardsList;

    public Row(){
        characterCardsList = new ArrayList<>();
        eventCardsList = new ArrayList<>();
        buildingCardsList = new BuildingDeck();
    }

    //getter
    public List<CharacterCard> getCharacterCardsList() {
        return characterCardsList;
    }
    public List<EventCard> getEventCardsList() {
        return eventCardsList;
    }
    public BuildingDeck getBuildingCardsList() {
        return buildingCardsList;
    }
    public int getNumCharacterCards(){
        return characterCardsList.size();
    }
    public int getNumEventCards(){
        return eventCardsList.size();
    }


    public void addCharacterCard(CharacterCard characterCard){
        characterCardsList.add(characterCard);
    }
    public void addEventCard(EventCard eventCard){
        eventCardsList.add(eventCard);
    }
    public void setBuildingCards(BuildingDeck buildingCardsList){
        this.buildingCardsList = buildingCardsList;
    }
    public void removeCharacterCard(CharacterCard characterCard){
        characterCardsList.remove(characterCard);
    }
    public void removeEventCard(EventCard eventCard){
        eventCardsList.remove(eventCard);
    }

//    public void swapFullRow(Row donor, Row receiver){
//        receiver = donor;
//        donor.buildingCardsList.clear();
//        donor.characterCardsList.clear();
//        donor.eventCardsList.clear();
//    }
    public void swapTribeRow(Row donor, Row receiver){
        receiver.characterCardsList = new ArrayList<CharacterCard>(donor.characterCardsList);
        receiver.eventCardsList = new ArrayList<EventCard>(donor.eventCardsList);
        donor.characterCardsList.clear();
        donor.eventCardsList.clear();
    }
    public void swapBuildingRow(Row donor, Row receiver){
        receiver.buildingCardsList = donor.buildingCardsList;
        donor.buildingCardsList.clear();
    }

    public void clearRoundEnd(){
        characterCardsList.clear();
        eventCardsList.clear();
    }
    public void clearBuildingCards(){
        buildingCardsList.clear();
    }

}
