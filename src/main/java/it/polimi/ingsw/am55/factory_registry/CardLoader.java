package it.polimi.ingsw.am55.factory_registry;

//import com.google.gson.Gson;
import it.polimi.ingsw.am55.ClientModel.ClientCard;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CardLoader {
    Map<String, ClientCard> cards = new HashMap<>();

    /*public CardLoader loadFromJson(String path) {
        try (FileReader reader = new FileReader(path)) {
            Gson gson = new Gson(); // libreria java fatta da google per convertire json in oggetti java

            return gson.fromJson(reader, CardLoader.class); // questa riga equivale a fare: CardLoader loader = new CardLoader();
                                                                                            //loader.cards = lista delle carte nel json
        } catch (IOException e) {
            throw new RuntimeException("Connection to file json failed", e);
        }
    }*/
//mettere la dipendenza nel pom
    /*public static CardLoader loadFromJson() {
        try (InputStream input = CardLoader.class
                .getClassLoader()
                .getResourceAsStream("Card.json")) {
            if (input == null) {
                throw new RuntimeException("Card.json not found");
            }

            Gson gson = new Gson(); // libreria java fatta da google per convertire json in oggetti java
            InputStreamReader reader = new InputStreamReader(input);

            return gson.fromJson(reader, CardLoader.class); // questa riga equivale a fare: CardLoader loader = new CardLoader();
            //loader.cards = lista delle carte nel json
        } catch (IOException e) {
            throw new RuntimeException("Connection to file json failed", e);
        }
    }*/



    /*public ClientCard getCard(String id) {
        ClientCard card = cards.get(id);

        if (card == null) {
            throw new IllegalArgumentException("Card id not found");
        }

        return card;
    }*/
}
