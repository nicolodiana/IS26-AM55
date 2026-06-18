package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.view.gui.GuiView;
import it.polimi.ingsw.am55.view.gui.assets.ImageResources;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class LobbySceneController implements GenericSceneController {

    @FXML private TextField nicknameField;
    @FXML private ComboBox<String> totemComboBox;
    @FXML private Spinner<Integer> numPlayersSpinner;
    @FXML private Button createButton;
    @FXML private Button joinButton;
    @FXML private Label statusLabel;
    @FXML private HBox totemPreviewBox;

    private GuiView guiView;
    private final ImageResources imageResources = new ImageResources();

    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    @FXML
    private void initialize() {
        totemComboBox.getItems().setAll("white", "blue", "orange", "yellow", "purple");
        totemComboBox.getSelectionModel().select("white");

        numPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        refreshTotemPreview();

        totemComboBox.valueProperty().addListener((obs, oldValue, newValue) -> refreshTotemPreview());
    }

    @FXML
    private void onCreateButtonClick() {
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
        String nickname = nicknameField.getText() == null ? "" : nicknameField.getText().trim();
        String totem = totemComboBox.getValue();

        if (!validateNickname(nickname) || totem == null) {
            return;
        }
        lock("Joining the game");
        guiView.joinGame(nickname, totem);
    }

    @FXML
    private void onQuitButtonClick() {
        guiView.quitLobby();
    }
    private boolean validateNickname(String nickname) {
        if (nickname.isBlank()) {
            showError("Inserisci un nickname.");
            return false;
        }
        return true;
    }
    public void renderLobby(LobbyView lobbyView, boolean locked) {
        List<String> availableTotems = availableTotems(lobbyView);
        updateTotemComboBox(availableTotems);

        if (locked) {
            setLocked(true);
            showMessage(
                    statusLabel.getText() != null && !statusLabel.getText().isBlank()
                            ? statusLabel.getText()
                            : "Comando inviato. In attesa del server..."
            );
            return;
        }

        boolean hasGame = lobbyView != null && lobbyView.hasGame();
        boolean gameCreated = lobbyView != null && lobbyView.isGameCreated();


        if (!hasGame) {
            createButton.setDisable(false);
            joinButton.setDisable(true);
            nicknameField.setDisable(false);
            totemComboBox.setDisable(false);
            numPlayersSpinner.setDisable(false);

            showMessage("Nessuna partita creata. Puoi crearne una nuova.");
            return;
        }

        if (gameCreated) {
            createButton.setDisable(true);
            joinButton.setDisable(availableTotems.isEmpty());
            nicknameField.setDisable(false);
            totemComboBox.setDisable(availableTotems.isEmpty());
            numPlayersSpinner.setDisable(true);

            String players = lobbyView.getPlayerIds().isEmpty()
                    ? "nessun giocatore"
                    : String.join(", ", lobbyView.getPlayerIds());

            String totems = availableTotems.isEmpty()
                    ? "nessuno"
                    : String.join(", ", availableTotems).toUpperCase();

            showMessage("Partita creata. Giocatori: "
                    + players
                    + ". Totem disponibili: "
                    + totems
                    + ".");

            return;
        }

        createButton.setDisable(true);
        joinButton.setDisable(true);
        nicknameField.setDisable(true);
        totemComboBox.setDisable(true);
        numPlayersSpinner.setDisable(true);
        showMessage("La partita è già iniziata o non accetta nuovi giocatori.");
    }

    private List<String> availableTotems(LobbyView lobbyView) {
        List<String> allTotems = List.of("white", "blue", "orange", "yellow", "purple");
        List<String> available = new ArrayList<>();

        for (String totem : allTotems) {
            if (lobbyView == null || !lobbyView.isTotemAlreadyChosen(totem)) {
                available.add(totem);
            }
        }

        return available;
    }

    private void updateTotemComboBox(List<String> availableTotems) {
        String previousSelection = totemComboBox.getValue();

        totemComboBox.getItems().setAll(availableTotems);

        if (availableTotems.isEmpty()) {
            totemComboBox.getSelectionModel().clearSelection();
            refreshTotemPreview();
            return;
        }

        if (previousSelection != null && availableTotems.contains(previousSelection)) {
            totemComboBox.getSelectionModel().select(previousSelection);
        } else {
            totemComboBox.getSelectionModel().select(availableTotems.get(0));
        }

        refreshTotemPreview();
    }

    private void refreshTotemPreview() {
        if (totemPreviewBox == null) {
            return;
        }

        totemPreviewBox.getChildren().clear();

        String totem = totemComboBox.getValue();

        if (totem == null || totem.isBlank()) {
            return;
        }

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
        setLocked(false);
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
        nicknameField.setDisable(locked);
        totemComboBox.setDisable(locked);
        numPlayersSpinner.setDisable(locked);
    }
}
