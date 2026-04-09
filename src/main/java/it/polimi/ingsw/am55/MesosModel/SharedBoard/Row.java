package it.polimi.ingsw.am55.MesosModel.SharedBoard;

import it.polimi.ingsw.am55.MesosModel.Cards.BuildingCard;
import it.polimi.ingsw.am55.MesosModel.Cards.CardSearchResult;
import it.polimi.ingsw.am55.MesosModel.Cards.CharacterCard;
import it.polimi.ingsw.am55.MesosModel.Cards.EventCard;
import it.polimi.ingsw.am55.MesosModel.Decks.BuildingDeck;
import it.polimi.ingsw.am55.MesosModel.Enum.CardType;
import it.polimi.ingsw.am55.MesosModel.Exceptions.CannotPickEventCard;
import it.polimi.ingsw.am55.MesosModel.Exceptions.CantPickFromRow;
import it.polimi.ingsw.am55.MesosModel.Player.Player;

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
    public BuildingCard getBuildingCardByIndex(int index){
        return buildingCardsList.getBuildingCardByIndex(index);
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
    public void removeCharacterCardByIndex(int index){
        characterCardsList.remove(index);
    }
    public void removeBuildingCardByIndex(int index){
        buildingCardsList.removeBuildingCardByIndex(index);
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
        receiver.buildingCardsList.swapBuildingDeck(donor.buildingCardsList, receiver.buildingCardsList);
    }

    public void clearRoundEnd(){
        characterCardsList.clear();
        eventCardsList.clear();
    }
    public void clearBuildingCards(){
        buildingCardsList.clear();
    }

    public boolean findCard(int id, CardSearchResult cardSearchResult){
        int i = 0;
        for(CharacterCard characterCard: characterCardsList){
            if (characterCard.getId() == id){
                cardSearchResult.setCard(characterCard);
                cardSearchResult.setCardType(CardType.CHARACTER);
                cardSearchResult.setIndexInList(i);
                return true;
            }
            i++;
        }
        i = 0;
        for(BuildingCard buildingCard: buildingCardsList.getBuildingDeck()){
            if (buildingCard.getId() == id){
                cardSearchResult.setCard(buildingCard);
                cardSearchResult.setCardType(CardType.BUILDING);
                cardSearchResult.setIndexInList(i);
                return true;
            }
            i++;
        }
        for(EventCard eventCard: eventCardsList){
            if (eventCard.getId() == id){
                throw new CannotPickEventCard("You can't pick a card from the EventList");
            }
        }
        return false;
    }

    public void removeCard(CardSearchResult cardSearchResult){
        if(cardSearchResult.getCardType() == CardType.CHARACTER){
            removeCharacterCardByIndex(cardSearchResult.getIndexInList());
        } else if(cardSearchResult.getCardType() == CardType.BUILDING){
            removeBuildingCardByIndex(cardSearchResult.getIndexInList());
        }
    }

    public void eventResolve(List<Player> players, List<EventCard> events){
        orderEvents(events);
        events.forEach(card -> {card.activateEvent(players);});
    }
    public void eventResolve(List<Player> players){
        orderEvents(eventCardsList);
        eventCardsList.forEach(card -> {card.activateEvent(players);});
    }

    private void orderEvents(List<EventCard> events){
        for(EventCard eventCard: events){
            eventCard.setOrder(events, events.indexOf(eventCard));
        }
    }

}
