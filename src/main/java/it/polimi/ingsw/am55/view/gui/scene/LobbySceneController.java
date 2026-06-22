package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.dto.LobbyView;
import it.polimi.ingsw.am55.view.gui.GuiView;
import it.polimi.ingsw.am55.view.gui.assets.ImageResources;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the lobby scene where players create or join a game.
 */
public class LobbySceneController implements GenericSceneController {

    private static final List<String> ALL_TOTEMS = List.of("white", "blue", "orange", "yellow", "purple");

    @FXML private TextField nicknameField;
    @FXML private ComboBox<String> totemComboBox;
    @FXML private Spinner<Integer> numPlayersSpinner;
    @FXML private Button createButton;
    @FXML private Button joinButton;
    @FXML private Label statusLabel;
    @FXML private HBox totemPreviewBox;

    private final ImageResources imageResources = new ImageResources();
    private GuiView guiView;
    private LobbyView latestLobbyView;

    /**
     * Injects the owning GUI view.
     */
    @Override
    public void setGuiView(GuiView guiView) {
        this.guiView = guiView;
    }

    /**
     * Initializes input controls and the totem preview.
     */
    @FXML
    private void initialize() {
        totemComboBox.getItems().setAll(ALL_TOTEMS);
        totemComboBox.getSelectionModel().select("white");
        numPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 2));
        refreshTotemPreview();
        totemComboBox.valueProperty().addListener((obs, oldValue, newValue) -> refreshTotemPreview());
    }

    /**
     * Validates form data and sends a create-game request.
     */
    @FXML
    private void onCreateButtonClick() {
        String nickname = readNickname();
        String totem = totemComboBox.getValue();
        int numPlayers = numPlayersSpinner.getValue();

        if (!validateNickname(nickname) || totem == null) {
            return;
        }

        guiView.createGame(nickname, totem, numPlayers);
    }

    /**
     * Validates form data and sends a join-game request.
     */
    @FXML
    private void onJoinButtonClick() {
        String nickname = readNickname();
        String totem = totemComboBox.getValue();

        if (!validateNickname(nickname) || totem == null) {
            return;
        }

        guiView.joinGame(nickname, totem);
    }

    /**
     * Requests to leave the lobby.
     */
    @FXML
    private void onQuitButtonClick() {
        if (guiView.getPlayerId() != null && !guiView.getPlayerId().isBlank()) {
            guiView.quitGame();
        } else {
            guiView.quitLobby();
        }
    }

    /**
     * Renders lobby state and enables only the actions currently allowed.
     *
     * @param lobbyView latest lobby DTO
     * @param locked whether controls must be locked because a command is pending
     */
    public void renderLobby(LobbyView lobbyView, boolean locked) {
        latestLobbyView = lobbyView;
        List<String> availableTotems = availableTotems(lobbyView);
        updateTotemComboBox(availableTotems);

        if (locked) {
            setLocked(true);
            return;
        }

        applyLobbyControls(lobbyView, availableTotems);
    }

    /**
     * Applies the enabled/disabled state dictated by the latest lobby snapshot.
     */
    private void applyLobbyControls(LobbyView lobbyView, List<String> availableTotems) {
        boolean hasGame = lobbyView != null && lobbyView.hasGame();
        boolean gameCreated = lobbyView != null && lobbyView.isGameCreated();

        if (!hasGame) {
            createButton.setDisable(false);
            joinButton.setDisable(true);
            nicknameField.setDisable(false);
            totemComboBox.setDisable(false);
            numPlayersSpinner.setDisable(false);
            return;
        }

        if (gameCreated) {
            createButton.setDisable(true);
            joinButton.setDisable(availableTotems.isEmpty());
            nicknameField.setDisable(false);
            totemComboBox.setDisable(availableTotems.isEmpty());
            numPlayersSpinner.setDisable(true);
            return;
        }

        createButton.setDisable(true);
        joinButton.setDisable(true);
        nicknameField.setDisable(true);
        totemComboBox.setDisable(true);
        numPlayersSpinner.setDisable(true);
    }


    /**
     * Restores lobby controls after an error using the same rules as a normal render.
     */
    private void restoreControlsFromLatestLobby() {
        List<String> availableTotems = availableTotems(latestLobbyView);
        updateTotemComboBox(availableTotems);
        applyLobbyControls(latestLobbyView, availableTotems);
    }

    /**
     * Shows a non-error status message.
     */
    public void showMessage(String message) {
        showStatus(message);
    }

    /**
     * Shows a non-error status message.
     */
    @Override
    public void showStatus(String message) {
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("info-text");
        statusLabel.setText(message == null ? "" : message);
    }

    /**
     * Shows an error without changing the current lobby availability state.
     * <p>
     * Server-side validation errors must not blindly re-enable every button: for
     * example, once a lobby already exists the Create Game button must remain
     * disabled even after a failed Join attempt.
     */
    @Override
    public void showError(String message) {
        restoreControlsFromLatestLobby();
        statusLabel.getStyleClass().removeAll("error-text", "info-text");
        statusLabel.getStyleClass().add("error-text");
        statusLabel.setText(message == null ? "" : message);
    }

    /**
     * Locks lobby controls without changing the currently visible status message.
     */
    @Override
    public void lockInteractions() {
        setLocked(true);
    }

    /**
     * Reads the nickname field safely.
     */
    private String readNickname() {
        return nicknameField.getText() == null ? "" : nicknameField.getText().trim();
    }

    /**
     * Validates that the nickname is not blank.
     */
    private boolean validateNickname(String nickname) {
        if (nickname.isBlank()) {
            showError("Enter a nickname.");
            return false;
        }
        return true;
    }

    /**
     * Computes totem colors that are still available in the lobby.
     */
    private List<String> availableTotems(LobbyView lobbyView) {
        List<String> available = new ArrayList<>();
        for (String totem : ALL_TOTEMS) {
            if (lobbyView == null || !lobbyView.isTotemAlreadyChosen(totem)) {
                available.add(totem);
            }
        }
        return available;
    }

    /**
     * Updates the totem combo box while preserving the current selection when possible.
     */
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

    /**
     * Refreshes the small image preview for the selected totem.
     */
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

    /**
     * Enables or disables every lobby input control.
     */
    private void setLocked(boolean locked) {
        createButton.setDisable(locked);
        joinButton.setDisable(locked);
        nicknameField.setDisable(locked);
        totemComboBox.setDisable(locked);
        numPlayersSpinner.setDisable(locked);
    }

    /**
     * Returns a fallback when a string is blank.
     */
    private String nonBlankOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
