package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;
import it.polimi.ingsw.am55.view.gui.assets.ImageResources;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import it.polimi.ingsw.am55.MesosModel.Enum.GameState;
import it.polimi.ingsw.am55.dto.LobbyView;

import java.util.ArrayList;
import java.util.List;

public class LobbySceneController implements GenericSceneController {
    private static final List<String> ALL_TOTEMS =
            List.of("white", "blue", "orange", "yellow", "purple");
    @FXML private TextField nicknameField;
    @FXML private ComboBox<String> totemComboBox;
    @FXML private Spinner<Integer> numPlayersSpinner;
    @FXML private Button createButton;
    @FXML private Button joinButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;
    @FXML private HBox totemPreviewBox;
    private volatile LobbyView currentLobbyView;
    private GuiView guiView;
    private final ImageResources imageResources = new ImageResources();

    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    public void renderLobby(LobbyView lobbyView, boolean locked) {
        this.currentLobbyView = lobbyView;

        boolean gameExists = lobbyView != null && lobbyView.getGameState() != null;
        boolean gameCreated = gameExists && lobbyView.getGameState() == GameState.CREATED;
        boolean gameAlreadyRunning = gameExists && !gameCreated;

        updateAvailableTotems(lobbyView);

        createButton.setDisable(locked || gameExists);
        joinButton.setDisable(locked || !gameCreated);

        nicknameField.setDisable(locked || gameAlreadyRunning);
        totemComboBox.setDisable(locked || gameAlreadyRunning);
        numPlayersSpinner.setDisable(locked || gameExists);

        refreshButton.setDisable(locked || gameAlreadyRunning);

        if (!gameExists) {
            showMessage("Nessuna partita creata. Puoi crearne una.");
        } else if (gameCreated) {
            showMessage("Partita già creata. Puoi unirti.");
        } else {
            showMessage("Una partita è già in corso su questo server. Puoi solo uscire.");
        }
    }
//per togliere i totem dalla gui una volta scelti da altri player
    private void updateAvailableTotems(LobbyView lobbyView) {
        String previousSelection = totemComboBox.getValue();

        List<String> availableTotems = new ArrayList<>(ALL_TOTEMS);

        if (lobbyView != null && lobbyView.getChosenTotems() != null) {
            for (String chosenTotem : lobbyView.getChosenTotems()) {
                availableTotems.removeIf(totem ->
                        totem.equalsIgnoreCase(chosenTotem)
                );
            }
        }

        totemComboBox.getItems().setAll(availableTotems);

        if (previousSelection != null && availableTotems.stream().anyMatch(t -> t.equalsIgnoreCase(previousSelection))) {
            totemComboBox.getSelectionModel().select(previousSelection);
        } else if (!availableTotems.isEmpty()) {
            totemComboBox.getSelectionModel().selectFirst();
        } else {
            totemComboBox.getSelectionModel().clearSelection();
        }

        refreshTotemPreview();
    }
    @FXML
    private void initialize() {
        totemComboBox.getItems().setAll(ALL_TOTEMS);
        totemComboBox.getSelectionModel().select("white");

        numPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        refreshTotemPreview();

        totemComboBox.valueProperty().addListener((obs, oldValue, newValue) -> refreshTotemPreview());
    }

    @FXML
    private void onCreateButtonClick() {
        if (currentLobbyView != null && currentLobbyView.getGameState() != null) {
            showError("Esiste già una partita su questo server. Non puoi crearne un'altra.");
            return;
        }

        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        String totem = totemComboBox.getValue();
        int numPlayers = numPlayersSpinner.getValue();

        if (!validateNickname(nickname) || totem == null) {
            return;
        }

        lock("Creating the game");
        guiView.createGame(nickname, totem, numPlayers);
    }

    @FXML
    private void onJoinButtonClick() {
        if (currentLobbyView == null || currentLobbyView.getGameState() == null) {
            showError("Non esiste ancora nessuna partita a cui unirsi.");
            return;
        }

        if (currentLobbyView.getGameState() != GameState.CREATED) {
            showError("La partita è già iniziata. Non puoi unirti.");
            return;
        }

        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        String totem = totemComboBox.getValue();

        if (!validateNickname(nickname) || totem == null) {
            return;
        }

        lock("Joining the game");
        guiView.joinGame(nickname, totem);
    }
    @FXML
    private void onRefreshButtonClick() {
        guiView.refreshCurrentScene();
    }

    private boolean validateNickname(String nickname) {
        if (nickname.isBlank()) {
            showError("Inserisci un nickname.");
            return false;
        }
        return true;
    }

    private void refreshTotemPreview() {
        if (totemPreviewBox == null) {
            return;
        }

        totemPreviewBox.getChildren().clear();
        String totem = totemComboBox.getValue();
        Image image = imageResources.loadTotem(totem);
        if (image != null) {
            ImageView view = new ImageView(image);
            view.setFitWidth(70);
            view.setFitHeight(70);
            view.setPreserveRatio(true);
            totemPreviewBox.getChildren().add(view);
        } else {
            totemPreviewBox.getChildren().add(new Label(totem));
        }
    }

    public void showMessage(String message) {
        //setLocked(false);
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("info-text");
        statusLabel.setText(message == null ? "" : message);
    }

    public void showError(String message) {
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("error-text");
        statusLabel.setText(message == null ? "" : message);
    }

    public void lock(String message) {
        setLocked(true);
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("info-text");
        statusLabel.setText(message);
    }

    private void setLocked(boolean locked) {
        createButton.setDisable(locked);
        joinButton.setDisable(locked);
        refreshButton.setDisable(locked);
        nicknameField.setDisable(locked);
        totemComboBox.setDisable(locked);
        numPlayersSpinner.setDisable(locked);
    }
}
