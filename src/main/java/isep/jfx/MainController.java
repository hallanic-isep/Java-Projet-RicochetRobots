package isep.jfx;

import isep.ricrob.Game;
import isep.ricrob.Token;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static isep.ricrob.Token.Color.*;

public class MainController {

    public final int TILE_SIZE = 40;

    @FXML
    public GridPane boardPane;

    @FXML
    public Label statusLabel;

    // "initialize()" est appelé par JavaFX à l'affichage de la fenêtre
    @FXML
    public void initialize() {

        // Affichage un message "bloquant"
        showWarning("Ricochet Robots");

        // Construction du plateau
        Image tile = new Image("cell.png", TILE_SIZE, TILE_SIZE, false, true);
        // ... "cell.png" doit être placé à la racine de "resources/" (sinon PB)
        for (int col = 0; col < Game.SIZE; col ++) {
            for (int lig = 0; lig < Game.SIZE; lig++) {
                ImageView tileGui = new ImageView(tile);
                final int lambdaCol = col;
                final int lambdaLig = lig;
                tileGui.setOnMouseClicked
                        (event -> {
                            String status = Game.context.processSelectTile
                                    (lambdaCol, lambdaLig);
                            if ( "MOVE".equals(status)) {
                                updateSelectedRobotPosition();
                            } else if (status != null) {
                                showWarning(status);
                            }
                        });
                boardPane.add(tileGui, col, lig);
            }
        }

        // Ajout des pièces
        addRobot(RED);
        addRobot(GREEN);
        addRobot(BLUE);
        addRobot(YELLOW);

        boardPane.add(
                new ImageView( new Image(
                        Game.context.getTarget().getColor() + "_target.png",
                        TILE_SIZE, TILE_SIZE, false, true
                ) ),
                Game.context.getTarget().getCol(),
                Game.context.getTarget().getLig()
        );

        // "Binding JFX" - Synchronisation du "Label" avec l'état du jeu
        statusLabel.textProperty().bind(Game.context.statusToolTipProperty);

    }

    // Affiche une boite de dialogue construite avec "SceneBuilder"
    public void showPlayerView(ActionEvent actionEvent) throws IOException {
        if (Game.context.getStatus() == Game.Status.CHOOSE_PLAYER) {
            FXMLLoader fxmlLoader = new FXMLLoader
                    (MainApplication.class.getResource("player-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
    }

    private void addRobot(Token.Color color) {
        Token robot = Game.context.getRobots().get(color);
        ImageView robotGui = new ImageView( new Image(
                color.name() + "_robot.png",
                TILE_SIZE, TILE_SIZE, false, true
        ) );
        robotGui.setOnMouseClicked
                (event -> Game.context.processSelectRobot(color));
        boardPane.add(robotGui, robot.getCol(), robot.getLig());
        // Association de l' "ImageView" avec le robot stocké dans le jeu
        robot.setGui(robotGui);
    }

    private void updateSelectedRobotPosition() {
        Token robot = Game.context.getSelectedRobot();
        GridPane.setConstraints(robot.getGui(), robot.getCol(), robot.getLig());
    }

    private void showWarning(String message) {
        Alert startMessage
                = new Alert(Alert.AlertType.INFORMATION, message);
        startMessage.setHeaderText(null);
        startMessage.setGraphic(null);
        startMessage.showAndWait();
    }
}
