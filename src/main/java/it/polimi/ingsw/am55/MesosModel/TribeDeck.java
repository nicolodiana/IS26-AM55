package it.polimi.ingsw.am55.MesosModel;

import it.polimi.ingsw.am55.MesosModel.Effect.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class TribeDeck {
    private Stack<TribeCard> tribeCardStack;

    public TribeDeck() {
        tribeCardStack = new Stack<>();
    }

    public void initTribeDeck(int numPlayer){

        tribeCardStack.addAll(createFinalEventCards(numPlayer));
        tribeCardStack.addAll(createAllCardsEra3(numPlayer));
        tribeCardStack.addAll(createAllCardsEra2(numPlayer));
        tribeCardStack.addAll(createAllCardsEra1(numPlayer));
    }

    //create all the character and event Card
    private List<TribeCard> createAllCardsEra1(int numPlayer) {
        List<TribeCard> allCardsEra1 = new ArrayList<>();
        switch (numPlayer) {
            case 2:
                createCardTwoPlayersEra1(allCardsEra1);
                break;
            case 3:
                createCardTwoPlayersEra1(allCardsEra1);
                allCardsEra1.add(new Hunter(false,1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Hunter(false,1));
                allCardsEra1.add(new Collector(1));
                break;
            case 4:
                createCardTwoPlayersEra1(allCardsEra1);
                allCardsEra1.add(new Hunter(false,1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Hunter(false,1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Shaman(1,1));
                allCardsEra1.add(new Inventor("Mortar and Pestle",1));
                allCardsEra1.add(new Inventor("Flute",1));
                allCardsEra1.add(new Inventor("Necklace",1));

                break;
            case 5:
                createCardTwoPlayersEra1(allCardsEra1);
                allCardsEra1.add(new Hunter(false,1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Hunter(false,1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Shaman(1,1));
                allCardsEra1.add(new Inventor("Mortar and Pestle",1));
                allCardsEra1.add(new Inventor("Flute",1));
                allCardsEra1.add(new Inventor("Necklace",1));
                allCardsEra1.add(new Collector(1));
                allCardsEra1.add(new Shaman(2,1));
                allCardsEra1.add(new Builder(1,2,1));
                break;
        }
        Collections.shuffle(allCardsEra1);
        return allCardsEra1;
    }
    private List<TribeCard> createAllCardsEra2(int numPlayer) {
        List<TribeCard> allCardsEra2 = new ArrayList<>();
        switch (numPlayer) {
            case 2:
                createCardsTwoPlayersEra2(allCardsEra2);
                break;
            case 3:
                createCardsTwoPlayersEra2(allCardsEra2);
                allCardsEra2.add(new Hunter(true,2));
                allCardsEra2.add(new Collector(2));
                allCardsEra2.add(new Builder(2,1,2));
                allCardsEra2.add(new Artist(2));
                break;
            case 4:
                createCardsTwoPlayersEra2(allCardsEra2);
                allCardsEra2.add(new Hunter(true,2));
                allCardsEra2.add(new Collector(2));
                allCardsEra2.add(new Builder(2,1,2));
                allCardsEra2.add(new Artist(2));
                allCardsEra2.add(new Hunter(true,2));
                allCardsEra2.add(new Collector(2));
                allCardsEra2.add(new Inventor("Fish Hook",2));
                break;
            case 5:
                createCardsTwoPlayersEra2(allCardsEra2);
                allCardsEra2.add(new Hunter(true,2));
                allCardsEra2.add(new Collector(2));
                allCardsEra2.add(new Builder(2,1,2));
                allCardsEra2.add(new Artist(2));
                allCardsEra2.add(new Hunter(true,2));
                allCardsEra2.add(new Collector(2));
                allCardsEra2.add(new Inventor("Fish Hook",2));
                allCardsEra2.add(new Hunter(false,2));
                allCardsEra2.add(new Collector(2));
                allCardsEra2.add(new Shaman(1,2));
                break;
        }
        Collections.shuffle(allCardsEra2);
        return allCardsEra2;
    }
    private List<TribeCard> createAllCardsEra3(int numPlayer) {
        List<TribeCard> allCardsEra3 = new ArrayList<>();
        switch (numPlayer) {
            case 2:
                createCardTwoPlayersEra3(allCardsEra3);
                break;
            case 3:
                createCardTwoPlayersEra3(allCardsEra3);
                allCardsEra3.add(new Shaman(2,3));
                allCardsEra3.add(new Inventor("Boat",3));
                allCardsEra3.add(new Inventor("Arrowhead",3));
                break;
            case 4:
                createCardTwoPlayersEra3(allCardsEra3);
                allCardsEra3.add(new Shaman(2,3));
                allCardsEra3.add(new Inventor("Boat",3));
                allCardsEra3.add(new Inventor("Arrowhead",3));
                allCardsEra3.add(new Shaman(2,3));
                allCardsEra3.add(new Inventor("Necklace",3));
                allCardsEra3.add(new Collector(3));
                break;
            case 5:
                createCardTwoPlayersEra3(allCardsEra3);
                allCardsEra3.add(new Shaman(2,3));
                allCardsEra3.add(new Inventor("Boat",3));
                allCardsEra3.add(new Inventor("Arrowhead",3));
                allCardsEra3.add(new Shaman(2,3));
                allCardsEra3.add(new Inventor("Necklace",3));
                allCardsEra3.add(new Collector(3));
                allCardsEra3.add(new Builder(4,1,3));
                allCardsEra3.add(new Artist(3));
                allCardsEra3.add(new Hunter(true,3));
                allCardsEra3.add(new Shaman(2,3));
                allCardsEra3.add(new Collector(3));
                break;
        }
        Collections.shuffle(allCardsEra3);
        return allCardsEra3;
    }
    private void createCardTwoPlayersEra1(List<TribeCard> allCards) {
        allCards.add(new Hunter(true, 1));
        allCards.add(new Hunter(true, 1));
        allCards.add(new Hunter(false, 1));
        allCards.add(new Shaman(1, 1));
        allCards.add(new Shaman(2, 1));
        allCards.add(new Artist(1));
        allCards.add(new Artist(1));
        allCards.add(new Artist(1));
        allCards.add(new Builder(3, 1, 1));
        allCards.add(new Builder(0, 2, 1));
        allCards.add(new Builder(2, 1, 1));
        allCards.add(new Collector(1));
        allCards.add(new Collector(1));
        allCards.add(new Inventor("Boat", 1));
        allCards.add(new Inventor("Arrowhead", 1));
        allCards.add(new Inventor("Bread", 1));
        allCards.add(new Inventor("Hide", 1));
        allCards.add(new ShamanRitualEventCard(5, 3, 1));
        allCards.add(new SustenanceEventCard(1, 1));
        allCards.add(new PaintingsEventCard(2,2,1,2,,1));
        allCards.add(new HuntEventCard(1, 1));
    }

    public void createCardsTwoPlayersEra2(List<TribeCard> allCards) {
        allCards.add(new Hunter(true, 2));
        allCards.add(new Hunter(false, 2));
        allCards.add(new Hunter(false, 2));
        allCards.add(new Shaman(2, 2));
        allCards.add(new Shaman(2, 2));
        allCards.add(new Builder(3, 2, 2));
        allCards.add(new Builder(1, 2, 2));
        allCards.add(new Builder(4, 1, 2));
        allCards.add(new Artist(2));
        allCards.add(new Artist(2));
        allCards.add(new Artist(2));
        allCards.add(new Collector(2));
        allCards.add(new Inventor("Hide", 2));
        allCards.add(new Inventor("Rope", 2));
        allCards.add(new Inventor("Mortar and Pestle", 2));
        allCards.add(new Inventor("Flute", 2));
        allCards.add(new Inventor("Figurine", 2));
        allCards.add(new ShamanRitualEventCard(10, 5, 2));
        allCards.add(new HuntEventCard(2, 2));
        allCards.add(new SustenanceEventCard(2, 2));
        allCards.add(new PaintingsEventCard(2));
    }
    private void createCardTwoPlayersEra3(List<TribeCard> allCards){
        allCards.add(new Inventor("Bread", 3));
        allCards.add(new Inventor("Fish Hook", 3));
        allCards.add(new Inventor("Rope", 3));
        allCards.add(new Inventor("Necklace", 3));
        allCards.add(new Artist(3));
        allCards.add(new Artist(3));
        allCards.add(new Artist(3));
        allCards.add(new Hunter(true, 3));
        allCards.add(new Hunter(false, 3));
        allCards.add(new Hunter(false, 3));
        allCards.add(new Builder(5, 1, 3));
        allCards.add(new Builder(3, 2, 3));
        allCards.add(new Builder(2, 2, 3));
        allCards.add(new Shaman(3, 3));
        allCards.add(new Shaman(2, 3));
        allCards.add(new Shaman(3, 3));
        allCards.add(new Collector(3));
        allCards.add(new PaintingsEventCard(3));
        allCards.add(new HuntEventCard(3, 3));
    }
    private List<TribeCard> createFinalEventCards(int numPlayer) {
        List<TribeCard> finalEventCards = new ArrayList<>();
        finalEventCards.add(new SustenanceEventCard(3));
        finalEventCards.add(new ShamanRitualEventCard(15,7));
        Collections.shuffle(finalEventCards);
        return finalEventCards;
    }

    public TribeCard getNextCard() {
        return tribeCardStack.pop();
    }
}
