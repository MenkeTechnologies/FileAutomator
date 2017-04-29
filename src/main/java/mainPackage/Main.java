package mainPackage;

import com.aquafx_project.AquaFx;
import com.guigarage.flatterfx.FlatterFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.aerofx.AeroFX;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Parent root = loader.load();

        MainController mainController = loader.getController();

        primaryStage.setTitle("File Automator");
        primaryStage.setOnCloseRequest(e -> {
            if (mainController.mainSplitPane.getItems().size() > 1) {
                Preferences.userRoot().putDouble("dividerPos0", mainController.mainSplitPane.getDividerPositions()[0]);
                Preferences.userRoot().putDouble("dividerPos1", mainController.mainSplitPane.getDividerPositions()[1]);
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

        Utilities.initMenuBar(mainController.menuBar, scene, primaryStage);

        Double sp = Preferences.userRoot().getDouble("dividerPos0", 0.2);
        Double sp2 = Preferences.userRoot().getDouble("dividerPos1", 0.8);

        mainController.mainSplitPane.setDividerPositions(sp, sp2);

        AquaFx.style();

        scene.getStylesheets().add("stylesheets/styles.css");
        primaryStage.setScene(scene);

        initKeyBindings(mainController);

        primaryStage.show();
    }

    public static void initKeyBindings(MainController mainController) {
        mainController.mainSplitPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

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

        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
