package mainPackage;

import com.aquafx_project.AquaFx;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.prefs.Preferences;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Parent root = loader.load();

        root.setStyle("-fx-base: #000000");

        MainController mainController = loader.getController();

        primaryStage.setTitle("File Automator");
        primaryStage.setOnCloseRequest(e -> {
            if (mainController.mainSplitPane.getItems().size() > 1) {

                for (int i = 0; i < mainController.mainSplitPane.getDividers().size(); i++) {

                    Preferences.userRoot().putDouble("dividerPos" + i, mainController.mainSplitPane.getDividerPositions()[i]);
                }
            }

            Platform.exit();
            System.exit(0);
        });

        Scene scene = new Scene(root, 1800, 1200);

        mainController.initBindings();

        scene.widthProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (Utilities.maximized.get()) {
                    mainController.mainSplitPane.setDividerPositions(0, 0);
                }
            }
        });

        Utilities.initMenuBar(mainController, scene, primaryStage);

        double[] sps = {0, 0, 0};

        for (int i = 0; i < mainController.mainSplitPane.getDividers().size(); i++) {

            Double sp = Preferences.userRoot().getDouble("dividerPos" + i, mainController.mainSplitPane.getDividerPositions()[i]);
            sps[i] = sp;
        }

        mainController.mainSplitPane.setDividerPositions(sps);

        //AquaFx.style();

        scene.getStylesheets().add("stylesheets/styles.css");
        primaryStage.setScene(scene);

        initKeyBindings(mainController);

        primaryStage.show();
    }

    public static void initKeyBindings(MainController mainController) {

        EventHandler<KeyEvent> eventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                mainController.initMainTableViewKeyBindings();
                mainController.initTreeViewKeyBindings();

                if (e.getCode() == KeyCode.SPACE) {
                    if (MainController.mediaPlayer != null) {
                        if (MainController.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            MainController.mediaPlayer.pause();
                        } else {
                            MainController.mediaPlayer.play();
                        }
                    }
                    e.consume();
                }

                if (e.getCode() == KeyCode.F) {

                    if (Utilities.maximized.get()) {
                        mainController.restorePanesToOld("mediaContextMenu");
                    } else {
                        mainController.maximizeVideo(null);
                        Utilities.maximized.set(true);
                    }

                    e.consume();
                }

                if (e.getCode() == KeyCode.Q) {
                    mainController.fitScreenAction(null, 0.25);
                    e.consume();
                }
                if (e.getCode() == KeyCode.W) {
                    if (!mainController.fitScreenToggleMediaButton.isSelected()) {
                        mainController.fitScreenToggleMediaButton.setSelected(true);
                    } else {
                        mainController.fitScreenToggleMediaButton.setSelected(false);
                    }
                    mainController.fitScreenAction(null, 1.0);
                    e.consume();
                }

                if (MainController.mediaPlayer != null) {
                    if (e.getCode() == KeyCode.N) {
                        mainController.playNext(null);
                        mainController.mediaPlayerControls.setVisible(true);
                        mainController.hideNodeAfterDelay(mainController.mediaPlayerControls);
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.P) {
                        Utilities.endOfMediaAction(mainController, false);
                        mainController.mediaPlayerControls.setVisible(true);

                        mainController.hideNodeAfterDelay(mainController.mediaPlayerControls);
                        e.consume();
                    }

                    if (e.getCode() == KeyCode.DIGIT0) {
                        MainController.mediaPlayer.seek(Duration.minutes(0));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT1) {
                        MainController.mediaPlayer.seek(Duration.minutes(1));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT2) {
                        MainController.mediaPlayer.seek(Duration.minutes(2));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT3) {
                        MainController.mediaPlayer.seek(Duration.minutes(3));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT4) {
                        MainController.mediaPlayer.seek(Duration.minutes(4));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT5) {
                        MainController.mediaPlayer.seek(Duration.minutes(5));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT6) {
                        MainController.mediaPlayer.seek(Duration.minutes(6));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT7) {
                        MainController.mediaPlayer.seek(Duration.minutes(7));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT8) {
                        MainController.mediaPlayer.seek(Duration.minutes(8));
                        e.consume();
                    }
                    if (e.getCode() == KeyCode.DIGIT9) {
                        MainController.mediaPlayer.seek(Duration.minutes(9));
                        e.consume();
                    }

                    if (e.getCode() == KeyCode.B) {
                        MainController.mediaPlayer.seek(MainController.mediaPlayer.getCurrentTime().subtract(Duration.seconds(15)));
                        e.consume();
                    }
                }
            }
        };

        mainController.mainSplitPane.getItems().get(0).addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
        mainController.mainSplitPane.getItems().get(1).addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
        mainController.mainSplitPane.getItems().get(2).addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
