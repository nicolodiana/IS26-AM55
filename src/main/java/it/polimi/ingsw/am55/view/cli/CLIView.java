package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;

import java.util.Scanner;

public class CLIView implements ClientModelObserver {

    private final ClientModel model;
    private final Scanner input;
    private final CLIRenderHelper cliRenderHelper;

    private UserActionHandler actionHandler;

    private volatile GameView currentGameView;
    private volatile String currentInfoMessage;
    private volatile String currentErrorMessage;
    private volatile boolean waitingServerResponse;
    private volatile int currentRound = 0;
    private String id;

    public CLIView(ClientModel model) {
        this.model = model;
        this.input = new Scanner(System.in);
        this.cliRenderHelper = new CLIRenderHelper();

        this.currentGameView = null;
        this.currentInfoMessage = null;
        this.currentErrorMessage = null;
        this.waitingServerResponse = false;
        this.id = null;
    }

    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public void start() {
        System.out.println(ConsoleColor.CYAN_BOLD + "Client CLI avviato." + ConsoleColor.RESET);
        printLobbyHelp();

        Thread inputThread = new Thread(this::inputLoop);
        inputThread.setName("CLI-Input-Thread");
        inputThread.start();
    }

    private void inputLoop() {
        while (true) {
            System.out.print("> ");
            String line = input.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            handleCommand(line);
        }
    }

    private void handleCommand(String line) {
        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase();

        if (command.equals("quit") || command.equals("exit")) {
            System.out.println("Chiusura client.");
            System.exit(0);
        }

        if (command.equals("help")) {
            printHelp();
            return;
        }

        if (command.equals("refresh")) {
            refresh();
            return;
        }

        if (waitingServerResponse) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "In attesa della risposta del server..."
                    + ConsoleColor.RESET);
            return;
        }

        GameView gameView = currentGameView;

        if (gameView == null) {
            handleLobbyCommand(command, parts);
        } else {
            handleGameCommand(command, parts, gameView);
        }
    }

    private void handleLobbyCommand(String command, String[] parts) {
        switch (command) {
            case "create" -> handleCreateCommand(parts);
            case "join" -> handleJoinCommand(parts);
            default -> {
                System.out.println(ConsoleColor.RED_BOLD
                        + "Comando non valido in lobby."
                        + ConsoleColor.RESET);
                printLobbyHelp();
            }
        }
    }

    private void handleCreateCommand(String[] parts) {
        if (parts.length != 4) {
            System.out.println(ConsoleColor.RED_BOLD
                    + "Uso corretto: create <nickname> <totemColor> <numPlayers>"
                    + ConsoleColor.RESET);
            return;
        }

        String playerId = parts[1].trim();
        String totemColor = parts[2].trim();

        Integer numPlayers = parseInt(parts[3]);
        if (numPlayers == null) {
            System.out.println(ConsoleColor.RED_BOLD
                    + "Il numero di giocatori deve essere un intero."
                    + ConsoleColor.RESET);
            return;
        }

        askCreateGame(playerId, totemColor, numPlayers);
    }

    private void handleJoinCommand(String[] parts) {
        if (parts.length != 3) {
            System.out.println(ConsoleColor.RED_BOLD
                    + "Uso corretto: join <nickname> <totemColor>"
                    + ConsoleColor.RESET);
            return;
        }

        String playerId = parts[1].trim();
        String totemColor = parts[2].trim();

        askJoinGame(playerId, totemColor);
    }

    private void handleGameCommand(String command, String[] parts, GameView gameView) {
        if (!isMyTurn(gameView)) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Non è il tuo turno. Current player: "
                    + gameView.getCurrentPlayer()
                    + ConsoleColor.RESET);
            return;
        }

        GameState state = gameView.getState();

        if (state == null) {
            System.out.println(ConsoleColor.RED_BOLD
                    + "Stato di gioco non disponibile."
                    + ConsoleColor.RESET);
            return;
        }

        switch (state) {
            case PLACETOTEM -> handlePlaceTotemCommand(command, parts);
            case PICKCARD -> handlePickCardCommand(command, parts);
            case PICKSPECIAL -> handlePickSpecialCommand(command, parts);
            /*
             * In futuro aggiungerai qui le prossime fasi.
             *
             * case PICKCARD -> handlePickCardCommand(command, parts);
             * case CHOOSECARD -> handleChooseCardCommand(command, parts);
             * case ENDTURN -> handleEndTurnCommand(command, parts);
             */

            default -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Nessuna azione gestita dalla CLI per lo stato: "
                    + state
                    + ConsoleColor.RESET);
        }
    }

    private void handlePlaceTotemCommand(String command, String[] parts) {
        if (!command.equals("placetotem")) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Riprova -> Prossima azione da compiere: placeTotem <index>"
                    + ConsoleColor.RESET);
            return;
        }

        if (parts.length != 2) {
            System.out.println(ConsoleColor.RED_BOLD
                    + "Uso corretto: placeTotem <index>"
                    + ConsoleColor.RESET);
            return;
        }

        Integer index = parseInt(parts[1]);
        if (index == null) {
            System.out.println(ConsoleColor.RED_BOLD
                    + "L'indice deve essere un numero intero."
                    + ConsoleColor.RESET);
            return;
        }

        askPlaceTotem(index);
    }

    private void handlePickCardCommand(String command, String[] parts) {
            if (!command.equals("pickcard")) {
                System.out.println(ConsoleColor.YELLOW_BOLD
                        + "Riprova -> Prossima azione da compiere: pickCard <cardId>"
                        + ConsoleColor.RESET);
                return;
            }

            if (parts.length != 2) {
                System.out.println(ConsoleColor.RED_BOLD
                        + "Uso corretto: pickCard <cardId>"
                        + ConsoleColor.RESET);
                return;
            }

            Integer cardId = parseInt(parts[1]);
            if (cardId == null) {
                System.out.println(ConsoleColor.RED_BOLD
                        + "L'indice deve essere un numero intero."
                        + ConsoleColor.RESET);
                return;
            }

            askPickCard(cardId);
        }

        public void handlePickSpecialCommand(String command, String[] parts) {
            if (!command.equals("pick")) {
                System.out.println(ConsoleColor.YELLOW_BOLD
                        + "Try again -> Next action to write: pick <cardId>"
                        + ConsoleColor.RESET);
                return;
            }

            if (parts.length != 2) {
                System.out.println(ConsoleColor.RED_BOLD
                        + "Uso corretto: pickCard <cardId>"
                        + ConsoleColor.RESET);
                return;
            }

            Integer cardId = parseInt(parts[1]);
            if (cardId == null) {
                System.out.println(ConsoleColor.RED_BOLD
                        + "Index has to be an integer"
                        + ConsoleColor.RESET);
                return;
            }

            askPickSpecial(cardId);
        }

    public void askCreateGame(String playerId, String totemColor, int numPlayers) {
        if (actionHandler != null) {
            this.id = playerId.trim();
            this.waitingServerResponse = true;

            actionHandler.onCreateGameSelected(this.id, totemColor, numPlayers);
        }
    }

    public void askJoinGame(String playerId, String totemColor) {
        if (actionHandler != null) {
            this.id = playerId.trim();
            this.waitingServerResponse = true;

            actionHandler.onJoinGameSelected(this.id, totemColor);
        }
    }

    public void askPlaceTotem(int index) {
        if (actionHandler != null) {
            this.waitingServerResponse = true;

            actionHandler.onPlaceTotemSelected(index);
        }
    }

    public void askPickCard(int cardId) {
        if (actionHandler != null) {
            this.waitingServerResponse = true;

            actionHandler.onPickCardSelected(this.id, cardId);
        }
    }

    public void askPickSpecial(int cardId) {
        if (actionHandler != null) {
            this.waitingServerResponse = true;

            actionHandler.onPickSpecialSelected(this.id, cardId);
        }
    }


    @Override
    public void onModelChanged(ClientModel updatedModel) {
        /*
         * Questo metodo può essere chiamato dal thread RMI.
         * Qui NON leggiamo mai input.
         * Salviamo solo lo stato ricevuto e renderizziamo.
         */

        this.waitingServerResponse = false;
        this.currentErrorMessage = updatedModel.getLastError();
        this.currentInfoMessage = updatedModel.getStateRequest();
        this.currentGameView = updatedModel.getGameView();

        System.out.println();

        if (currentErrorMessage != null) {
            showError(currentErrorMessage);
            printNextAction();
            return;
        }

        if (currentInfoMessage != null) {
            showMessage(currentInfoMessage);
        }

        /*if (currentGameView != null && this.currentRound != currentGameView.getRound()) {
            this.currentRound = currentGameView.getRound();

            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "========== ROUND " + this.currentRound + " =========="
                    + ConsoleColor.RESET);
        }*/

        if (currentGameView != null) {
            renderGame(currentGameView);
        }

        printNextAction();
    }

    private void printNextAction() {
        GameView gameView = currentGameView;

        System.out.println();

        if (gameView == null) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: create <nickname> <totemColor> <numPlayers>"
                    + " oppure join <nickname> <totemColor>"
                    + ConsoleColor.RESET);
            return;
        }

        if (!isMyTurn(gameView)) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: attendi il turno di "
                    + gameView.getCurrentPlayer()
                    + ConsoleColor.RESET);
            return;
        }

        GameState state = gameView.getState();

        if (state == null) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: attendi aggiornamento stato."
                    + ConsoleColor.RESET);
            return;
        }

        switch (state) {
            case PLACETOTEM -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: placeTotem <index>"
                    + ConsoleColor.RESET);
            case PICKCARD -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: pickCard <card id>"
                    + ConsoleColor.RESET);
            case PICKSPECIAL -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "You have Building 13 please choose another with command: pick <cardId>"
                    + ConsoleColor.RESET);
            case EVENTRESOLVE -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Time to resolve the events on the boards, wait for the results"
                    + ConsoleColor.RESET);
            /*
             * In futuro:
             *
             * case PICKCARD -> System.out.println("Prossima azione da compiere: pickCard <cardId>");
             * case CHOOSECARD -> System.out.println("Prossima azione da compiere: chooseCard <row> <index>");
             */

            default -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: attendi aggiornamento."
                    + ConsoleColor.RESET);
        }
    }

    private void printLobbyHelp() {
        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI LOBBY ==========" + ConsoleColor.RESET);
        System.out.println("create <nickname> <totemColor> <numPlayers>");
        System.out.println("join <nickname> <totemColor>");
        System.out.println("refresh");
        System.out.println("help");
        System.out.println("quit");
        System.out.println(ConsoleColor.YELLOW_BOLD + "===================================" + ConsoleColor.RESET);
        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD
                + "Prossima azione da compiere: create <nickname> <totemColor> <numPlayers>"
                + " oppure join <nickname> <totemColor>"
                + ConsoleColor.RESET);
    }

    private void printHelp() {
        GameView gameView = currentGameView;

        if (gameView == null) {
            printLobbyHelp();
            return;
        }

        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI GAME ==========" + ConsoleColor.RESET);
        System.out.println("refresh");
        System.out.println("help");
        System.out.println("quit");

        if (GameState.PLACETOTEM.equals(gameView.getState())) {
            System.out.println("placeTotem <index>");
        }

        /*
         * In futuro:
         *
         * if (GameState.PICKCARD.equals(gameView.getState())) {
         *     System.out.println("pickCard <cardId>");
         * }
         */

        System.out.println(ConsoleColor.YELLOW_BOLD + "==================================" + ConsoleColor.RESET);
        printNextAction();
    }

    private void refresh() {
        if (currentGameView != null) {
            renderGame(currentGameView);
            printNextAction();
        } else if (model.getGameView() != null) {
            renderGame(model.getGameView());
            printNextAction();
        } else {
            showMessage("Nessuna partita da mostrare.");
            printNextAction();
        }
    }

    private boolean isMyTurn(GameView gameView) {
        if (gameView == null || gameView.getCurrentPlayer() == null || id == null) {
            return false;
        }

        return gameView.getCurrentPlayer().trim().equalsIgnoreCase(id.trim());
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
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

       // if (gameView.getState().equals(GameState.EVENTRESOLVE)) {
            if (gameView.getResolveEvents() != null && !gameView.getResolveEvents().isEmpty()) {
                System.out.println(ConsoleColor.CYAN_BOLD + "RESOLVE EVENTS" + ConsoleColor.RESET);
                for (ResolveEventView view : gameView.getResolveEvents()) {
                    view.showEvent();
                    System.out.println();
                }

                System.out.println(ConsoleColor.CYAN_BOLD + " END RESOLVE EVENTS" + ConsoleColor.RESET);
                System.out.println(ConsoleColor.CYAN_BOLD + "==========================" + ConsoleColor.RESET);
            }
        //}
    }

    public String getId() {
        return id;
    }
}