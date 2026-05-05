package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.PlayerView;

import java.util.Scanner;

public class CLIView implements ClientModelObserver {

    private final ClientModel model;
    private final CLIRenderHelper cliRenderHelper;

    private UserActionHandler actionHandler;

    public CLIView(ClientModel model) {
        this.model = model;
        this.cliRenderHelper = new CLIRenderHelper();
    }

    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public void start() {
        Scanner input = new Scanner(System.in);

        System.out.println(ConsoleColor.CYAN_BOLD + "Client CLI avviato." + ConsoleColor.RESET);
        printMenu();

        while (true) {
            System.out.print("Scelta: ");
            String choice = input.nextLine();

            switch (choice) {
                case "1" -> askCreateGameFromInput(input);
                case "2" -> askJoinGameFromInput(input);
                case "3" -> askPlaceTotemFromInput(input);
                case "4" -> printMenu();
                case "5" -> refresh();
                case "0" -> {
                    System.out.println("Chiusura client.");
                    return;
                }
                default -> {
                    System.out.println(ConsoleColor.RED_BOLD + "Scelta non valida." + ConsoleColor.RESET);
                    printMenu();
                }
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== MENU ==========" + ConsoleColor.RESET);
        System.out.println("1) Create game");
        System.out.println("2) Join game");
        System.out.println("3) Place totem");
        System.out.println("4) Mostra menu");
        System.out.println("5) Refresh board");
        System.out.println("0) Quit");
        System.out.println(ConsoleColor.YELLOW_BOLD + "==========================" + ConsoleColor.RESET);
        System.out.println();
    }

    private void refresh() {
        if (model.getGameView() != null) {
            renderGame(model.getGameView());
        } else {
            showMessage("Nessuna partita da mostrare.");
        }

        printMenu();
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
                System.out.print(ConsoleColor.RED_BOLD + "Inserisci un numero valido: " + ConsoleColor.RESET);
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
    public void onModelChanged(ClientModel updatedModel) {
        System.out.println();

        if (updatedModel.getLastError() != null) {
            showError(updatedModel.getLastError());
            printMenu();
            return;
        }

        if (updatedModel.getStateRequest() != null) {
            showMessage(updatedModel.getStateRequest());
        }

        if (updatedModel.getGameView() != null) {
            renderGame(updatedModel.getGameView());
        }

        printMenu();
    }

    private void showMessage(String message) {
        System.out.println(ConsoleColor.GREEN_BOLD + "[INFO] " + ConsoleColor.RESET + message);
    }

    private void showError(String message) {
        System.out.println(ConsoleColor.RED_BOLD + "[ERRORE] " + ConsoleColor.RESET + message);
    }

    private void renderGame(GameView gameView) {
        System.out.println();
        System.out.println(ConsoleColor.CYAN_BOLD + "========== GAME ==========" + ConsoleColor.RESET);

        System.out.println("Game id: " + gameView.getGameId());
        System.out.println("Stato: " + gameView.getState());
        System.out.println("Round: " + gameView.getRound());
        System.out.println("Current player: " + gameView.getCurrentPlayer());

        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "Giocatori:" + ConsoleColor.RESET);

        if (gameView.getPlayers() == null || gameView.getPlayers().isEmpty()) {
            System.out.println("- Nessun giocatore disponibile");
        } else {
            for (PlayerView player : gameView.getPlayers()) {
                if (player == null) {
                    continue;
                }

                System.out.println("- "
                        + ConsoleColor.totemColor(player.getTotemColor())
                        + player.getNickname()
                        + ConsoleColor.RESET
                        + " | Totem: "
                        + player.getTotemColor()
                        + " | Food: "
                        + player.getFood()
                        + " | Points: "
                        + player.getPoints());
            }
        }

        if (gameView.getBoard() != null) {
            cliRenderHelper.printBoard(gameView.getBoard());
        } else {
            System.out.println(ConsoleColor.RED_BOLD + "Board non disponibile." + ConsoleColor.RESET);
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "==========================" + ConsoleColor.RESET);
    }
}