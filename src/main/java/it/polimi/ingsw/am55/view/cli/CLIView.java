package it.polimi.ingsw.am55.view.cli;

import it.polimi.ingsw.am55.ClientModel.ClientModel;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.controller.UserActionHandler;
import it.polimi.ingsw.am55.dto.GameView;
import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.dto.PlayerView;
import it.polimi.ingsw.am55.dto.endgame.EndGameEffectView;
import it.polimi.ingsw.am55.dto.endgame.EndGameResultView;
import it.polimi.ingsw.am55.dto.endgame.LeaderBoardEntryView;
import it.polimi.ingsw.am55.dto.resolveEvents.ResolveEventView;
import it.polimi.ingsw.am55.view.ClientAction;
import it.polimi.ingsw.am55.view.ClientActionResolver;
import it.polimi.ingsw.am55.view.ClientModelObserver;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CLIView implements ClientModelObserver {


    private final ClientModel model;
    private final Scanner input;
    private final CLIRenderHelper cliRenderHelper;
    private final ClientActionResolver actionResolver;
    private final ScheduledExecutorService scheduler;

    private UserActionHandler actionHandler;
    private volatile GameView currentGameView;
    private volatile String currentInfoMessage;
    private volatile String currentErrorMessage;
    private volatile boolean waitingServerResponse;
    private volatile String id;
    private volatile boolean inLobby;
    private volatile LobbyView currentLobbyView;

    public CLIView(ClientModel model) {
        this.model = model;
        this.input = new Scanner(System.in);
        this.cliRenderHelper = new CLIRenderHelper();
        this.actionResolver = new ClientActionResolver();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("CLI-Scheduler-Thread");
            thread.setDaemon(true);
            return thread;
        });

        this.currentGameView = null;
        this.currentInfoMessage = null;
        this.currentErrorMessage = null;
        this.waitingServerResponse = false;
        this.inLobby = true;
        this.currentLobbyView = null;
        this.id = null;
    }

    public void setActionHandler(UserActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }
    //inizializzazione cli, avrà un thread separato costantemente attivo a ricevere input
    //la scelta di metterlo separato è stata fatta per evitare di tenere occupato il thread principale che sarebbe quello degli aggiornamenti
    public void start() {
        System.out.println(ConsoleColor.CYAN_BOLD + "Client CLI avviato." + ConsoleColor.RESET);
        //sincronizzo schermata lobby in base alle scelte gia fatte in precedenza da altri player (se esistono)
        //poi dopo on model changed del Lobby Status Message vedrò la schermata lobby coerente
        System.out.println(ConsoleColor.YELLOW_BOLD
                + "Sincronizzazione lobby con il server..."
                + ConsoleColor.RESET);

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
//ogni comando dell'utente passa su handleCommand
            handleCommand(line);
        }
    }

    private void handleCommand(String line) {
        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase();

        if (command.equals("quit") || command.equals("exit")) {
            if (!this.inLobby) {
                askQuitGame(this.id);
                return;
            }
            askQuitLobby();
            return;
        }

        if (command.equals("help")) {
            printHelp();
            return;
        }


        ClientAction action = actionResolver.resolve(
                currentGameView,
                id,
                inLobby,
                model.isGameCrashed()
        );

        if (command.equals("myhand") || command.equals("hand")) {
            handleHandCommand(action, parts);
            printExpectedAction();
            return;
        }


        if (waitingServerResponse) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "In attesa della risposta del server..."
                    + ConsoleColor.RESET);
            return;
        }
        /* LA CLIVIEW NON CONTROLLA DIRETTAMENTE LO STATO DELLA PARTITA MA DELEGA
        LA DECISIONE AL CLIENT ACTION RESOLVER , IN BASE AL CURRENTGAMEVIEW E L'ID DEL GIOCATORE
        IL RESOLVER RESTITUISCE UN ETICHETTA CLIENTACTION A CUI CORRISPONDERA POI IL COMANDO ADEGUATO
        DA FAR FARE AL CLIENT
         */

        switch (action) {
            case LOBBY -> handleLobbyCommand(command, parts);
            case WAITING_TO_START -> showMessage("sono thread input: fermo in attesa di inizio partita");
            case PLACE_TOTEM -> handlePlaceTotemCommand(command, parts);
            case PICK_CARD -> handlePickCardCommand(command, parts);
            case PICK_SPECIAL -> handlePickSpecialCommand(command, parts);
            case WAITING_FOR_TURN -> showWaitingForTurnMessage();
            case RESOLVE_EVENTS -> showMessage("La partita sta risolvendo gli eventi. Attendi il prossimo aggiornamento.");
            case END_GAME -> showMessage("La partita è terminata. Usa refresh per ristampare il risultato.");
            case CRASHED -> showError("La partita è in stato CRASHED. Non ci sono comandi disponibili.");
            case WAITING_FOR_STATE -> showMessage("Nessuna azione disponibile: attendi un aggiornamento dello stato.");
        }
    }
    private void handleHandCommand(ClientAction currentState, String[] parts) {
        if (parts.length > 2) {
            showError("Uso corretto: myhand oppure myhand <nickname>");
            return;
        }

        String targetNickname = parts.length == 2 ? parts[1].trim() : id;
        printHand(currentState, targetNickname);
    }
    private void handleLobbyCommand(String command, String[] parts) {
        switch (command) {
            case "create" -> {
                if (currentLobbyView != null && currentLobbyView.getGameState() != null) {
                    showError("Una partita è già stata creata. Usa join.");
                    printLobbyHelp();
                    return;
                }
                handleCreateCommand(parts);
            }
            case "join" -> {
                if (currentLobbyView == null || currentLobbyView.getGameState() == null) {
                    showError("Nessuna partita creata. Usa create.");
                    printLobbyHelp();
                    return;
                }

                if (currentLobbyView.getGameState() != GameState.CREATED) {
                    showError("La partita non accetta nuovi giocatori.");
                    printLobbyHelp();
                    return;
                }
                handleJoinCommand(parts);
            }
            default -> {
                showError("Comando non valido in lobby.");
                printLobbyHelp();
            }
        }
    }

    private void handleCreateCommand(String[] parts) {
        if (parts.length != 4) {
            showError("Uso corretto: create <nickname> <totemColor> <numPlayers>");
            return;
        }

        String playerId = parts[1].trim();
        String totemColor = parts[2].trim();

        Integer numPlayers = parseInt(parts[3]);
        if (numPlayers == null) {
            showError("Il numero di giocatori deve essere un intero.");
            return;
        }

        askCreateGame(playerId, totemColor, numPlayers);
    }

    private void handleJoinCommand(String[] parts) {
        if (parts.length != 3) {
            showError("Uso corretto: join <nickname> <totemColor>");
            return;
        }

        String playerId = parts[1].trim();
        String totemColor = parts[2].trim();

        askJoinGame(playerId, totemColor);
    }

    private void handlePlaceTotemCommand(String command, String[] parts) {
        if (!command.equals("placetotem")) {
            printExpectedAction();
            return;
        }

        if (parts.length != 2) {
            showError("Uso corretto: placeTotem <index>");
            return;
        }

        Integer index = parseInt(parts[1]);
        if (index == null) {
            showError("L'indice deve essere un numero intero.");
            return;
        }

        askPlaceTotem(index);
    }

    private void handlePickCardCommand(String command, String[] parts) {
        if (!command.equals("pickcard")) {
            printExpectedAction();
            return;
        }

        if (parts.length != 2) {
            showError("Uso corretto: pickCard <cardId>");
            return;
        }

        Integer cardId = parseInt(parts[1]);
        if (cardId == null) {
            showError("Il cardId deve essere un numero intero.");
            return;
        }

        askPickCard(cardId);
    }

    private void handlePickSpecialCommand(String command, String[] parts) {
        if (!command.equals("pick")) {
            printExpectedAction();
            return;
        }

        if (parts.length != 2) {
            showError("Uso corretto: pick <cardId>");
            return;
        }

        Integer cardId = parseInt(parts[1]);
        if (cardId == null) {
            showError("Il cardId deve essere un numero intero.");
            return;
        }

        askPickSpecial(cardId);
    }

    // Equivalenti CLI delle action associate ai bottoni della futura GUI.
    public void askCreateGame(String playerId, String totemColor, int numPlayers) {
        if (!ensureActionHandler()) {
            return;
        }

        this.id = playerId.trim();
        this.waitingServerResponse = true;
        actionHandler.onCreateGameSelected(this.id, totemColor, numPlayers);
    }

    public void askJoinGame(String playerId, String totemColor) {
        if (!ensureActionHandler()) {
            return;
        }

        this.id = playerId.trim();
        this.waitingServerResponse = true;
        actionHandler.onJoinGameSelected(this.id, totemColor);
    }

    public void askPlaceTotem(int index) {
        if (!ensureActionHandler()) {
            return;
        }

        this.waitingServerResponse = true;
        actionHandler.onPlaceTotemSelected(this.id,index);
    }

    public void askPickCard(int cardId) {
        if (!ensureActionHandler()) {
            return;
        }

        this.waitingServerResponse = true;
        actionHandler.onPickCardSelected(this.id, cardId);
    }

    public void askPickSpecial(int cardId) {
        if (!ensureActionHandler()) {
            return;
        }

        this.waitingServerResponse = true;
        actionHandler.onPickSpecialSelected(this.id, cardId);
    }
    public void askQuitLobby(){
        if (actionHandler != null) {
            this.waitingServerResponse = true;
            showMessage("Richiesta di uscita inviata al server...");
            actionHandler.onQuitSelectedLobby();
        }
    }
    public void askQuitGame(String playerId) {
        if (actionHandler != null && playerId != null) {
            this.waitingServerResponse = true;
            showMessage("Richiesta di uscita inviata al server...");
            actionHandler.onQuitGameSelected(playerId.trim());
        }
    }
    // Qui la view decide solo cosa mostrare dopo un aggiornamento del ClientModel.
    private volatile boolean pendingEventResolutionDelay = false;

    @Override
    public void onModelChanged(ClientModel updatedModel) {
        this.waitingServerResponse = false;

        this.currentErrorMessage = updatedModel.getLastError();
        this.currentInfoMessage = updatedModel.getStateRequest();
        this.currentGameView = updatedModel.getGameView();

        this.inLobby = updatedModel.isInLobby();
        this.currentLobbyView = updatedModel.getLobbyView();

        EndGameResultView endGameResultView = updatedModel.getEndGameResultView();
        boolean gameViewUpdated = updatedModel.isLastMessageUpdatedGameView();

        ClientAction action = actionResolver.resolve(
                currentGameView,
                id,
                inLobby,
                updatedModel.isGameCrashed()
        );

        System.out.println();
        //post messaggio di errore dal server e in lobby (nickname duplicato, totem errato o non presente, rimando i comandi lobby perche devo rimanere in interfaccia lobby (perche essendo ancora in lobby lo swtich andra a fare print lobby help)
        //post  messaggio di errore quando non sono in lobby, mostro l'interfaccia game mode con prossimo comando                //post  messaggio di errore quando non sono in lobby, mostro l'interfaccia game mode con prossimo comando
        if (currentErrorMessage != null) {
            showError(currentErrorMessage);
            printExpectedAction();
            return;
        }
        if (action == ClientAction.CRASHED) {
            showMessage(
                    currentInfoMessage != null && !currentInfoMessage.isBlank()
                            ? currentInfoMessage
                            : "Connessione chiusa."
            );

            model.removeObserver(this);
            return;
        }
        //gestisco aggiornamenti real time lobby rimandandogli l'interfaccia lobby aggiornata
        //qualcuno ha creato una partita
        //qualcuno ha scelto un totem
        //qualcuno è entrato in partita
        if (action == ClientAction.LOBBY) {
            if (currentInfoMessage != null) {
                showMessage(currentInfoMessage);
            }

            printExpectedAction();
            return;
        }
//tutti questi controlli vengono fatti invece per stati riguardanti i client in game
        if (action == ClientAction.RESOLVE_EVENTS && gameViewUpdated) {
            showMessage("Inizia la risoluzione degli eventi...");
            pendingEventResolutionDelay = true;
            return;
        }

        if (action == ClientAction.END_GAME_RESOLVE && gameViewUpdated) {
            showMessage("La partita è terminata... qui di seguito il riepilogo di fine partita");
            pendingEventResolutionDelay = true;
            return;
        }

        if (currentInfoMessage != null) {
            if (!pendingEventResolutionDelay) {
                showMessage(currentInfoMessage);
            }
        }

        /*
         * PRIMA gestisco l'end game result.
         * Questo deve stare prima del blocco gameViewUpdated,
         * perché GameEndResolveMessage aggiorna sia GameView sia EndGameResultView.
         */
        if (endGameResultView != null) {
            final GameView snapshot = currentGameView;
            final EndGameResultView resultSnapshot = endGameResultView;

            if (pendingEventResolutionDelay) {
                pendingEventResolutionDelay = false;

                scheduler.schedule(() -> {
                    if (snapshot != null) {
                        renderGame(snapshot);
                    }

                    printEndGameResult(resultSnapshot);
                    printExpectedAction();
                }, 4, TimeUnit.SECONDS);

                return;
            }

            if (snapshot != null) {
                renderGame(snapshot);
            }

            printEndGameResult(resultSnapshot);
            printExpectedAction();
            return;
        }

        if (gameViewUpdated && currentGameView != null) {
            if (pendingEventResolutionDelay) {
                pendingEventResolutionDelay = false;

                final GameView snapshot = currentGameView;
                final String infoSnapshot = currentInfoMessage;

                scheduler.schedule(() -> {
                    if (infoSnapshot != null && !infoSnapshot.isBlank()) {
                        showMessage(infoSnapshot);
                    }

                    renderGame(snapshot);
                    printExpectedAction();
                }, 4, TimeUnit.SECONDS);

                return;
            }

            renderGame(currentGameView);
        }

        if (currentGameView != null && action.equals(ClientAction.END_GAME)) {
            showMessage("Partita terminata...");
            model.removeObserver(this);
            return;
        }
        if (currentGameView != null && action.equals(ClientAction.CRASHED)) {
            showMessage("Partita terminata...");
            model.removeObserver(this);
            return;
        }

        printExpectedAction();
    }
    // Equivalente testuale delle schermate/interazioni che la futura GUI mostrerà.
    private void printExpectedAction() {
        ClientAction action = actionResolver.resolve(
                currentGameView,
                id,
                inLobby,
                model.isGameCrashed()
        );

        switch (action) {
            case LOBBY -> {
                System.out.println();
                printLobbyHelp();
            }

            case WAITING_FOR_TURN -> {
                System.out.println();
                System.out.println(ConsoleColor.YELLOW_BOLD
                        + "Attendi il turno di "
                        + safeCurrentPlayer()
                        + ConsoleColor.RESET);
            }

            case PLACE_TOTEM -> {
                System.out.println();
                System.out.println(ConsoleColor.YELLOW_BOLD
                        + "Comando disponibile: placeTotem <index>"
                        + ConsoleColor.RESET);
            }

            case PICK_CARD -> {
                System.out.println();
                System.out.println(ConsoleColor.YELLOW_BOLD
                        + "Comando disponibile: pickCard <cardId> (usa myhand per vedere il mazzo personale)"
                        + ConsoleColor.RESET);
            }

            case PICK_SPECIAL -> {
                System.out.println();
                System.out.println(ConsoleColor.YELLOW_BOLD
                        + "Comando disponibile: pick <cardId> per scegliere una carta dalla upper row"
                        + ConsoleColor.RESET);
            }


            case CRASHED -> {
                System.out.println();
                System.out.println(ConsoleColor.RED_BOLD
                        + "Connessione chiusa."
                        + ConsoleColor.RESET);
            }

            case WAITING_TO_START, RESOLVE_EVENTS, WAITING_FOR_STATE,END_GAME -> {
                // Nessuna azione richiesta all'utente: non stampo nulla.
            }
        }
    }
    private void printLobbyHelp() {
        if (currentLobbyView == null || currentLobbyView.getGameState() == null) {
            System.out.println();
            System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI LOBBY ==========" + ConsoleColor.RESET);
            System.out.println("Nessuna partita creata.");
            System.out.println(
                    "create <nickname> <totemColor> <numPlayers> "
                            + "(TOTEM: "
                            + ConsoleColor.GREEN_BOLD
                            + "BLUE, ORANGE, PURPLE, YELLOW, WHITE"
                            + ConsoleColor.RESET
                            + ")"
            );
        } else if (currentLobbyView.getGameState() == GameState.CREATED) {
            System.out.println();
            System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI LOBBY ==========" + ConsoleColor.RESET);
            System.out.println("Partita già creata. Puoi unirti.");
            System.out.println(
                    "join <nickname> <totemColor> "
                            + "(TOTEM DISPONIBILI: "
                            + ConsoleColor.GREEN_BOLD
                            + cliRenderHelper.availableTotemsForLobby(currentLobbyView)
                            + ConsoleColor.RESET
                            + ")"
            );
        } else {
            System.out.println("La partita è già iniziata. Non puoi più unirti.");
            System.out.println("Richiesta di uscita inviata al server...");
            System.out.println(ConsoleColor.YELLOW_BOLD + "===================================" + ConsoleColor.RESET);

            if (!waitingServerResponse && actionHandler != null) {
                waitingServerResponse = true;

                scheduler.execute(() -> {
                    try {
                        actionHandler.onQuitSelectedLobby();
                    } catch (RuntimeException e) {
                        waitingServerResponse = false;
                        showError("Errore durante l'uscita dalla lobby: " + e.getMessage());
                    }
                });
            }

            return;
        }

        System.out.println("help");
        System.out.println("quit");
        System.out.println(ConsoleColor.YELLOW_BOLD + "===================================" + ConsoleColor.RESET);
    }

    private void printHelp() {
        ClientAction action = actionResolver.resolve(
                currentGameView,
                id,
                inLobby,
                model.isGameCrashed()
        );

        if (action == ClientAction.LOBBY) {
            printLobbyHelp();
            return;
        }

        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI GAME ==========" + ConsoleColor.RESET);
        System.out.println("help");
        System.out.println("quit");

        switch (action) {
            case PLACE_TOTEM -> System.out.println("placeTotem <index>");
            case PICK_CARD -> System.out.println("pickCard <cardId>");
            case PICK_SPECIAL -> System.out.println("pick <cardId>");
            default -> System.out.println("Nessun comando di gioco disponibile in questo momento.");
        }

        System.out.println(ConsoleColor.YELLOW_BOLD + "==================================" + ConsoleColor.RESET);
        printExpectedAction();
    }

//    private void refresh() {
//        GameView gameView = currentGameView != null ? currentGameView : model.getGameView();
//
//        if (gameView != null) {
//            renderGame(gameView);
//            EndGameResultView result = model.getEndGameResultView();
//            if (result != null) {
//                printEndGameResult(result);
//            }
//        } else {
//            showMessage("Nessuna partita da mostrare.");
//        }
//
//        printExpectedAction();
//    }

    private void showWaitingForTurnMessage() {
        System.out.println(ConsoleColor.YELLOW_BOLD
                + "Non è il tuo turno. Current player: "
                + safeCurrentPlayer()
                + ConsoleColor.RESET);
    }

    private boolean ensureActionHandler() {
        if (actionHandler != null) {
            return true;
        }

        showError("ActionHandler non configurato: impossibile inviare il comando.");
        return false;
    }

    private void scheduleExpectedActionPrint(int seconds) {
        scheduler.schedule(this::printExpectedAction, seconds, TimeUnit.SECONDS);
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String safeCurrentPlayer() {
        return currentGameView != null && currentGameView.getCurrentPlayer() != null
                ? currentGameView.getCurrentPlayer()
                : "unknown";
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

        printResolveEvents(gameView);
    }

    private void printResolveEvents(GameView gameView) {
        if (gameView.getResolveEvents() == null || gameView.getResolveEvents().isEmpty()) {
            return;
        }

        System.out.println(ConsoleColor.CYAN_BOLD + "RESOLVE EVENTS" + ConsoleColor.RESET);
        for (ResolveEventView view : gameView.getResolveEvents()) {
            System.out.println(ConsoleColor.RED_BOLD + view.getNameEvent() + ConsoleColor.RESET);
            System.out.println(view.showEvent());
            System.out.println();
        }
    }

    private void printEndGameResult(EndGameResultView result) {
        if (result == null) {
            return;
        }

        if (result.getResolvedEvents() != null && !result.getResolvedEvents().isEmpty()) {
            System.out.println(ConsoleColor.CYAN_BOLD
                    + "========== EVENTI FINALI RISOLTI =========="
                    + ConsoleColor.RESET);

            for (ResolveEventView view : result.getResolvedEvents()) {
                System.out.println(ConsoleColor.RED_BOLD + view.getNameEvent() + ConsoleColor.RESET);
                System.out.println(view.showEvent());
                System.out.println();
            }
        }

        if (result.getEndGameEffects() != null && !result.getEndGameEffects().isEmpty()) {
            System.out.println(ConsoleColor.PURPLE_BOLD
                    + "========== EFFETTI FINALI =========="
                    + ConsoleColor.RESET);

            for (EndGameEffectView effect : result.getEndGameEffects()) {
                String sign = effect.getPointDelta() >= 0 ? "+" : "";

                System.out.println(
                        effect.getPlayerNickname()
                                + ": "
                                + effect.getDescription()
                                + " ("
                                + sign
                                + effect.getPointDelta()
                                + " PP)"
                );
            }

            System.out.println();
        }

        if (result.getLeaderBoard() != null && !result.getLeaderBoard().isEmpty()) {
            System.out.println(ConsoleColor.CYAN_BOLD
                    + "========== CLASSIFICA GENERALE =========="
                    + ConsoleColor.RESET);

            System.out.printf("%-8s %-25s %-8s %-8s %-25s%n",
                    "Pos.", "Nickname", "PP", "Cibo", "Data");

            for (LeaderBoardEntryView entry : result.getLeaderBoard()) {
                if (entry == null) {
                    continue;
                }

                System.out.printf("%-8d %-25s %-8d %-8d %-25s%n",
                        entry.getPosition(),
                        entry.getPlayerNickname(),
                        entry.getPrestigePoint(),
                        entry.getFoodPoint(),
                        entry.getDate());
            }

            System.out.println(ConsoleColor.CYAN_BOLD
                    + "========================================="
                    + ConsoleColor.RESET);
            System.out.println();
        }

        if (result.getWinners() != null && !result.getWinners().isEmpty()) {
            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "========== VINCITORE/I =========="
                    + ConsoleColor.RESET);

            for (Map.Entry<String, Integer> winner : result.getWinners().entrySet()) {
                System.out.println(winner.getKey() + " = " + winner.getValue());
            }

            System.out.println(ConsoleColor.YELLOW_BOLD
                    + "================================="
                    + ConsoleColor.RESET);
        }
    }

    private void printHand(ClientAction currentState, String targetNickname) {
        if (currentState == ClientAction.LOBBY || currentState == ClientAction.WAITING_TO_START || currentGameView == null) {
            showError("Non puoi visualizzare la mano prima dell'inizio della partita.");
            return;
        }

        PlayerView targetPlayer = null;

        for (PlayerView player : currentGameView.getPlayers()) {
            if (player.getNickname().equalsIgnoreCase(targetNickname)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            showError("Giocatore non trovato: " + targetNickname);
            return;
        }

        boolean isMyHand = targetPlayer.getNickname().equalsIgnoreCase(id);


        cliRenderHelper.printPersonalDeck(
                targetPlayer.getMyHand(),
                targetPlayer.getNickname(),
                isMyHand
        );
    }

    private void shutdown() {
        System.out.println("Chiusura client.");
        scheduler.shutdownNow();
        System.exit(0);
    }

    public String getId() {
        return id;
    }
}
