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
    private final Object inputLock;
    private final Object consoleLock;
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

        this.inputLock = new Object();
        this.consoleLock = new Object();
        this.currentGameView = null;
        this.currentInfoMessage = null;
        this.currentErrorMessage = null;
        stopWaitingServerResponseAndWakeInput();
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
        synchronized (consoleLock) {
            System.out.println(ConsoleColor.CYAN_BOLD + "Client CLI avviato." + ConsoleColor.RESET);
            printLobbyHelp();
        }

        Thread inputThread = new Thread(()->
        {
            try{
                inputLoop();
            }catch(Exception e){

            }
        });
        inputThread.setName("CLI-Input-Thread");
        inputThread.start();
    }

    private void inputLoop() throws InterruptedException {
        while (true) {
            printPrompt();
            String line = input.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            // Ogni comando dell'utente passa su handleCommand.
            // Il lock evita che un aggiornamento asincrono del server scriva
            // in mezzo all'output prodotto dal comando locale.
            synchronized (consoleLock) {
                handleCommand(line);
            }

            // Dopo la gestione del comando, il thread di input resta in attesa
            // solo se il comando ha effettivamente inviato una richiesta al server.
            waitServerResponseIfNeeded();

        }
    }

    private void printPrompt() {
        synchronized (consoleLock) {// Poiché il thread di input e il thread che aggiorna la grafica
            //dopo aver ricevuto risposta dal server usano entrambi la console, bisogna sincronizzarli
            printPromptUnsafe();
        }
    }

    private void printPromptUnsafe() {
        System.out.print("> ");
        System.out.flush();
    }

    private void clearConsole() {
        // 3J prova a cancellare anche lo scrollback; 2J cancella lo schermo; H torna in alto a sinistra.
        System.out.print("\033[3J\033[H\033[2J");
        System.out.flush();
    }

    private void redrawConsole(Runnable renderer) {
        synchronized (consoleLock) {
            boolean inputThreadIsAlreadyWaitingOnNextLine = !waitingServerResponse;

            clearConsole();
            renderer.run();

            // Se il thread input era bloccato su Scanner.nextLine(), il vecchio prompt è stato cancellato
            // dalla clearConsole(), quindi lo ristampiamo.
            // Se invece siamo in attesa della risposta server, il prompt sarà stampato dal prossimo ciclo
            // dell'input thread dopo stopWaitingServerResponseAndWakeInput().
            if (inputThreadIsAlreadyWaitingOnNextLine) {
                printPromptUnsafe();
            }
        }
    }

    private void startWaitingServerResponse() {
        synchronized (inputLock) {
            waitingServerResponse = true;
        }
    }

    private void stopWaitingServerResponseAndWakeInput() {
        synchronized (inputLock) {
            waitingServerResponse = false;
            inputLock.notifyAll();
        }
    }

    private void waitServerResponseIfNeeded() throws InterruptedException {
        synchronized (inputLock) {
            while (waitingServerResponse) {
                inputLock.wait();
            }
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
            stopWaitingServerResponseAndWakeInput();
            return;
        }

//        if (command.equals("refresh")) {
//            refresh();
//            return;
//        }

        ClientAction action = actionResolver.resolve(currentGameView, id, inLobby);

        if (command.equals("myhand") || command.equals("hand")) {
            handleHandCommand(action, parts);
            printExpectedAction();

            // myhand/hand non passa dalla rete: non arriverà onModelChanged.
            // Quindi sblocco esplicitamente il thread di input.
            stopWaitingServerResponseAndWakeInput();
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
            case WAITING_TO_START -> {
                showMessage("sono thread input: fermo in attesa di inizio partita");
                stopWaitingServerResponseAndWakeInput();
            }
            case PLACE_TOTEM -> handlePlaceTotemCommand(command, parts);
            case PICK_CARD -> handlePickCardCommand(command, parts);
            case PICK_SPECIAL -> handlePickSpecialCommand(command, parts);
            case WAITING_FOR_TURN -> {
                showWaitingForTurnMessage();
                stopWaitingServerResponseAndWakeInput();
            }
            case RESOLVE_EVENTS -> {
                showMessage("La partita sta risolvendo gli eventi. Attendi il prossimo aggiornamento.");
                stopWaitingServerResponseAndWakeInput();
            }
            case END_GAME -> {
                showMessage("La partita è terminata. Usa refresh per ristampare il risultato.");
                stopWaitingServerResponseAndWakeInput();
            }
            case CRASHED -> {
                showError("La partita è in stato CRASHED. Non ci sono comandi disponibili.");
                stopWaitingServerResponseAndWakeInput();
            }
            case WAITING_FOR_STATE -> {
                showMessage("Nessuna azione disponibile: attendi un aggiornamento dello stato.");
                stopWaitingServerResponseAndWakeInput();
            }
        }
    }
    private void handleHandCommand(ClientAction currentState, String[] parts) {
        if (parts.length > 2) {
            showError("Uso corretto: myhand oppure myhand <nickname>");
            stopWaitingServerResponseAndWakeInput();
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
                    stopWaitingServerResponseAndWakeInput();
                    return;
                }
                handleCreateCommand(parts);
            }
            case "join" -> {
                if (currentLobbyView == null || currentLobbyView.getGameState() == null) {
                    showError("Nessuna partita creata. Usa create.");
                    printLobbyHelp();
                    stopWaitingServerResponseAndWakeInput();
                    return;
                }

                if (currentLobbyView.getGameState() != GameState.CREATED) {
                    showError("La partita non accetta nuovi giocatori.");
                    printLobbyHelp();
                    stopWaitingServerResponseAndWakeInput();
                    return;
                }
                handleJoinCommand(parts);
            }
            default -> {
                showError("Comando non valido in lobby.");
                printLobbyHelp();
                stopWaitingServerResponseAndWakeInput();
            }
        }
    }

    private void handleCreateCommand(String[] parts) {
        if (parts.length != 4) {
            showError("Uso corretto: create <nickname> <totemColor> <numPlayers>");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        String playerId = parts[1].trim();
        String totemColor = parts[2].trim();

        Integer numPlayers = parseInt(parts[3]);
        if (numPlayers == null) {
            showError("Il numero di giocatori deve essere un intero.");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        askCreateGame(playerId, totemColor, numPlayers);
    }

    private void handleJoinCommand(String[] parts) {
        if (parts.length != 3) {
            showError("Uso corretto: join <nickname> <totemColor>");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        String playerId = parts[1].trim();
        String totemColor = parts[2].trim();

        askJoinGame(playerId, totemColor);
    }

    private void handlePlaceTotemCommand(String command, String[] parts) {
        if (!command.equals("placetotem")) {
            printExpectedAction();
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        if (parts.length != 2) {
            showError("Uso corretto: placeTotem <index>");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        Integer index = parseInt(parts[1]);
        if (index == null) {
            showError("L'indice deve essere un numero intero.");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        askPlaceTotem(index);
    }

    private void handlePickCardCommand(String command, String[] parts) {
        if (!command.equals("pickcard")) {
            printExpectedAction();
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        if (parts.length != 2) {
            showError("Uso corretto: pickCard <cardId>");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        Integer cardId = parseInt(parts[1]);
        if (cardId == null) {
            showError("Il cardId deve essere un numero intero.");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        askPickCard(cardId);
    }

    private void handlePickSpecialCommand(String command, String[] parts) {
        if (!command.equals("pick")) {
            printExpectedAction();
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        if (parts.length != 2) {
            showError("Uso corretto: pick <cardId>");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        Integer cardId = parseInt(parts[1]);
        if (cardId == null) {
            showError("Il cardId deve essere un numero intero.");
            stopWaitingServerResponseAndWakeInput();
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
        startWaitingServerResponse();

        try {
            actionHandler.onCreateGameSelected(this.id, totemColor, numPlayers);
        } catch (Exception e) {
            showError("Errore durante l'invio del comando create: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    public void askJoinGame(String playerId, String totemColor) {
        if (!ensureActionHandler()) {
            return;
        }

        this.id = playerId.trim();
        startWaitingServerResponse();

        try {
            actionHandler.onJoinGameSelected(this.id, totemColor);
        } catch (Exception e) {
            showError("Errore durante l'invio del comando join: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    public void askPlaceTotem(int index) {
        if (!ensureActionHandler()) {
            return;
        }

        startWaitingServerResponse();

        try {
            actionHandler.onPlaceTotemSelected(this.id, index);
        } catch (Exception e) {
            showError("Errore durante l'invio del comando placeTotem: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    public void askPickCard(int cardId) {
        if (!ensureActionHandler()) {
            return;
        }

        startWaitingServerResponse();

        try {
            actionHandler.onPickCardSelected(this.id, cardId);
        } catch (Exception e) {
            showError("Errore durante l'invio del comando pickCard: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    public void askPickSpecial(int cardId) {
        if (!ensureActionHandler()) {
            return;
        }

        startWaitingServerResponse();

        try {
            actionHandler.onPickSpecialSelected(this.id, cardId);
        } catch (Exception e) {
            showError("Errore durante l'invio del comando pick: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    public void askQuitLobby() {
        if (actionHandler == null) {
            showError("ActionHandler non configurato: impossibile inviare quit.");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        startWaitingServerResponse();
        showMessage("Richiesta di uscita inviata al server...");

        try {
            actionHandler.onQuitSelectedLobby();
        } catch (Exception e) {
            showError("Errore durante l'invio del comando quit lobby: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    public void askQuitGame(String playerId) {
        if (actionHandler == null || playerId == null) {
            showError("Impossibile inviare quit game: ActionHandler o playerId non disponibile.");
            stopWaitingServerResponseAndWakeInput();
            return;
        }

        startWaitingServerResponse();
        showMessage("Richiesta di uscita inviata al server...");

        try {
            actionHandler.onQuitGameSelected(playerId.trim());
        } catch (Exception e) {
            showError("Errore durante l'invio del comando quit game: " + e.getMessage());
            stopWaitingServerResponseAndWakeInput();
        }
    }

    // Qui la view decide solo cosa mostrare dopo un aggiornamento del ClientModel.
    private volatile boolean pendingEventResolutionDelay = false;

    @Override
    public void onModelChanged(ClientModel updatedModel) {
        try {

            this.currentErrorMessage = updatedModel.getLastError();
            this.currentInfoMessage = updatedModel.getStateRequest();
            this.currentGameView = updatedModel.getGameView();

            this.inLobby = updatedModel.isInLobby();
            this.currentLobbyView = updatedModel.getLobbyView();

            EndGameResultView endGameResultView = updatedModel.getEndGameResultView();
            boolean gameViewUpdated = updatedModel.isLastMessageUpdatedGameView();

            ClientAction action = actionResolver.resolve(currentGameView, id, inLobby);

            // Da qui in poi ogni aggiornamento server viene renderizzato come una schermata completa:
            // non accodiamo blocchi sotto al prompt, ma puliamo la console e ridisegniamo lo stato corrente.
            if (currentErrorMessage != null) {
                redrawConsole(() -> {
                    showError(currentErrorMessage);
                    printExpectedAction();
                });
                return;
            }

            if (inLobby) {
                redrawConsole(() -> {
                    if (currentInfoMessage != null) {
                        showMessage(currentInfoMessage);
                    }
                    printExpectedAction();
                });
                return;
            }

            if (action == ClientAction.RESOLVE_EVENTS && gameViewUpdated) {
                pendingEventResolutionDelay = true;
                redrawConsole(() -> showMessage("Inizia la risoluzione degli eventi..."));
                return;
            }

            if (action == ClientAction.END_GAME_RESOLVE && gameViewUpdated) {
                pendingEventResolutionDelay = true;
                redrawConsole(() -> showMessage("La partita è terminata... qui di seguito il riepilogo di fine partita"));
                return;
            }

            if (gameViewUpdated && currentGameView != null && pendingEventResolutionDelay) {
                pendingEventResolutionDelay = false;

                final GameView snapshot = currentGameView;
                final String infoSnapshot = currentInfoMessage;

                scheduler.schedule(() -> redrawConsole(() -> {
                    if (infoSnapshot != null) {
                        showMessage(infoSnapshot);
                    }
                    renderGame(snapshot);
                    printExpectedAction();
                }), 4, TimeUnit.SECONDS);

                return;
            }

            if (endGameResultView != null) {
                final String infoSnapshot = currentInfoMessage;

                scheduler.schedule(() -> redrawConsole(() -> {
                    if (infoSnapshot != null) {
                        showMessage(infoSnapshot);
                    }
                    printEndGameResult(endGameResultView);
                }), 4, TimeUnit.SECONDS);

                return;
            }

            redrawConsole(() -> {
                if (currentInfoMessage != null) {
                    showMessage(currentInfoMessage);
                }

                if (gameViewUpdated && currentGameView != null) {
                    renderGame(currentGameView);
                }

                if (currentGameView != null && action.equals(ClientAction.END_GAME)) {
                    showMessage("Chiusura connessioni in corso...");
                    model.removeObserver(this);
                    return;
                }

                if (updatedModel.isGameCrashed()) {
                    showMessage("Partita terminata per crash di un client. Connessione in chiusura...");
                    model.removeObserver(this);
                    return;
                }

                printExpectedAction();
            });
        } finally {
            stopWaitingServerResponseAndWakeInput();
        }
    }
    // Equivalente testuale delle schermate/interazioni che la futura GUI mostrerà.
    private void printExpectedAction() {
        ClientAction action = actionResolver.resolve(currentGameView, id, inLobby);

        System.out.println();

        switch (action) {
            case LOBBY -> {
                printLobbyHelp();
            }
            case WAITING_TO_START -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "PRINT EXPECTED ACTION: IN ATTESA DI INIZIO PARTITA "

                    + ConsoleColor.RESET);

            case WAITING_FOR_TURN -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: attendi il turno di "
                    + safeCurrentPlayer()
                    + ConsoleColor.RESET);

            case PLACE_TOTEM -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: placeTotem <index>"
                    + ConsoleColor.RESET);

            case PICK_CARD -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: pickCard <cardId> (command: myhand  to see personal deck) "
                    + ConsoleColor.RESET);

            case PICK_SPECIAL -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Hai Building 13: puoi scegliere un'altra carta dalla UPPER ROW con: pick <cardId>"
                    + ConsoleColor.RESET);

            case RESOLVE_EVENTS -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: attendi la fine della risoluzione eventi."
                    + ConsoleColor.RESET);

            case END_GAME -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Partita terminata. Usa refresh per ristampare board/risultati."
                    + ConsoleColor.RESET);

            case CRASHED -> System.out.println(ConsoleColor.RED_BOLD
                    + "Partita in stato CRASHED."
                    + ConsoleColor.RESET);

            case WAITING_FOR_STATE -> System.out.println(ConsoleColor.YELLOW_BOLD
                    + "Prossima azione da compiere: attendi aggiornamento stato."
                    + ConsoleColor.RESET);
        }
    }
    private void printLobbyHelp() {
        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI LOBBY ==========" + ConsoleColor.RESET);

        if (currentLobbyView == null || currentLobbyView.getGameState() == null) {
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
            System.out.println("La partita è già iniziata o non accetta nuovi giocatori.");
        }

        System.out.println("refresh");
        System.out.println("help");
        System.out.println("quit");
        System.out.println(ConsoleColor.YELLOW_BOLD + "===================================" + ConsoleColor.RESET);
    }

    private void printHelp() {
        ClientAction action = actionResolver.resolve(currentGameView, id, inLobby);

        if (action == ClientAction.LOBBY) {
            printLobbyHelp();
            return;
        }

        System.out.println();
        System.out.println(ConsoleColor.YELLOW_BOLD + "========== COMANDI GAME ==========" + ConsoleColor.RESET);
        System.out.println("refresh");
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
        stopWaitingServerResponseAndWakeInput();
        return false;
    }

    private void scheduleExpectedActionPrint(int seconds) {
        scheduler.schedule(() -> redrawConsole(this::printExpectedAction), seconds, TimeUnit.SECONDS);
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

            System.out.printf("%-8s %-25s %-8s %-8s%n",
                    "Pos.", "Nickname", "PP", "Cibo");

            for (LeaderBoardEntryView entry : result.getLeaderBoard()) {
                if (entry == null) {
                    continue;
                }

                System.out.printf("%-8d %-25s %-8d %-8d%n",
                        entry.getPosition(),
                        entry.getPlayerNickname(),
                        entry.getPrestigePoint(),
                        entry.getFoodPoint());
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
