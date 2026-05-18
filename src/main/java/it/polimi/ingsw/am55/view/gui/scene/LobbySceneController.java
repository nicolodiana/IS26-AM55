package it.polimi.ingsw.am55.view.gui.scene;

import it.polimi.ingsw.am55.view.gui.GuiView;
import it.polimi.ingsw.am55.view.gui.assets.ImageResources;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class LobbySceneController implements GenericSceneController {

    @FXML private TextField nicknameField;
    @FXML private ComboBox<String> totemComboBox;
    @FXML private Spinner<Integer> numPlayersSpinner;
    @FXML private Button createButton;
    @FXML private Button joinButton;
    @FXML private Button refreshButton;
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
        refreshButton.setDisable(locked);
        nicknameField.setDisable(locked);
        totemComboBox.setDisable(locked);
        numPlayersSpinner.setDisable(locked);
    }
}
