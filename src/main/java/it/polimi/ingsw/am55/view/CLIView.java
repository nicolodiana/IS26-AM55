package it.polimi.ingsw.am55.view;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.PlayerView;

import java.util.Scanner;

public class CLIView implements ClientModelObserver {

    private UserActionHandler actionHandler;

    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public void start() {
        Scanner input = new Scanner(System.in);

        System.out.println("Client CLI avviato.");
        printMenu();

        while (true) {
            System.out.print("Scelta: ");
            String choice = input.nextLine();

            switch (choice) {
                case "1" -> askCreateGameFromInput(input);
                case "2" -> askJoinGameFromInput(input);
                case "3" -> askPlaceTotemFromInput(input);
                case "4" -> printMenu();
                case "0" -> {
                    System.out.println("Chiusura client.");
                    return;
                }
                default -> {
                    System.out.println("Scelta non valida.");
                    printMenu();
                }
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("========== MENU ==========");
        System.out.println("1) Create game");
        System.out.println("2) Join game");
        System.out.println("3) Place totem");
        System.out.println("4) Mostra menu");
        System.out.println("0) Quit");
        System.out.println("==========================");
        System.out.println();
    }

    private void askCreateGameFromInput(Scanner input) {
        System.out.print("Nickname: ");
        String playerId = input.nextLine();

        System.out.print("Colore totem: ");
        String totemColor = input.nextLine();

        System.out.print("Numero giocatori: ");
        int numPlayers = readInt(input);

        askCreateGame(playerId, totemColor, numPlayers);
    }

    private void askJoinGameFromInput(Scanner input) {
        System.out.print("Nickname: ");
        String playerId = input.nextLine();

        System.out.print("Colore totem: ");
        String totemColor = input.nextLine();

        askJoinGame(playerId, totemColor);
    }

    private void askPlaceTotemFromInput(Scanner input) {
        System.out.print("Indice posizione: ");
        int index = readInt(input);

        askPlaceTotem(index);
    }

    private int readInt(Scanner input) {
        while (true) {
            String line = input.nextLine();

            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un numero valido: ");
            }
        }
    }

    public void askCreateGame(String playerId, String totemColor, int numPlayers) {
        if (actionHandler != null) {
            actionHandler.onCreateGameSelected(playerId, totemColor, numPlayers);
        }
    }

    public void askJoinGame(String playerId, String totemColor) {
        if (actionHandler != null) {
            actionHandler.onJoinGameSelected(playerId, totemColor);
        }
    }

    public void askPlaceTotem(int index) {
        if (actionHandler != null) {
            actionHandler.onPlaceTotemSelected(index);
        }
    }

    @Override
    public void onModelChanged(ClientModel model) {
        System.out.println();

        if (model.getLastError() != null) {
            showError(model.getLastError());
            printMenu();
            return;
        }

        if (model.getStateRequest() != null) {
            showMessage(model.getStateRequest());
        }

        if (model.getGameView() != null) {
            renderGame(model.getGameView());
        }

        printMenu();
    }

    private void showMessage(String message) {
        System.out.println("[INFO] " + message);
    }

    private void showError(String message) {
        System.out.println("[ERRORE] " + message);
    }

    private void renderGame(GameView gameView) {
        System.out.println();
        System.out.println("========== GAME ==========");
        System.out.println("Game id: " + gameView.getGameId());
        System.out.println("Stato: " + gameView.getState());
        System.out.println("Round: " + gameView.getRound());
        System.out.println("Current player: " + gameView.getCurrentPlayer());

        System.out.println();
        System.out.println("Giocatori:");
        for (PlayerView player : gameView.getPlayers()) {
            System.out.println("- " + player.getNickname());
        }

        System.out.println();
        System.out.println("Board:");
        System.out.println(gameView.getBoard());

        System.out.println("==========================");
    }
}