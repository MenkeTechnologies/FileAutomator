package mainPackage;

import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import windows.Settings;
import windows.Splash;

import java.io.IOException;
import java.util.prefs.Preferences;

public class OldMain extends Application {
    public static final String APPLICATION_ICON =
            "http://cdn1.iconfinder.com/data/icons/Copenhagen/PNG/32/people.png";
    public static final String SPLASH_IMAGE =
            "http://fxexperience.com/wp-content/uploads/2010/06/logo.png";
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private Stage primaryStage;
    private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;
    Scene mainScene;
    int MaxSize = 100;

    @Override
    public void init() throws Exception {
        ImageView splash = new ImageView(new Image(SPLASH_IMAGE));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        progressText = new Label("Will find friends for peanuts . . .");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);

        String color = CommonUtilities.toWebColor(Color.valueOf(Preferences.userRoot().get("backgroundColorPicker",null)));

        String style =  "-fx-padding: 5; " +
                "-fx-border-width:5; " +
                "-fx-border-color: " +
                "linear-gradient(" +
                "to bottom, " +
                "chocolate, " +
                "derive(chocolate, 50%)" +
                ");";

        if (color != null) {
            style += "-fx-base: " + color;
        }

        System.err.println("___________" + Thread.currentThread().getStackTrace()[1].getClassName()+ "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                "___ style" + style);


        splashLayout.setStyle(style);
        splashLayout.setEffect(new DropShadow());
    }

    private void showSplash(final Stage initStage, Splash.InitCompletionHandler initCompletionHandler) {
        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.toFront();

//        initStage.getIcons().add(new Image(APPLICATION_ICON));


        CustomTask<String> loadingTask = new CustomTask<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Starting...");
                updateProgress(0, 100);
                Thread.sleep(2000);

                initMainStage(this);

                return null;
            }
        };
        loadProgress.progressProperty().bind(loadingTask.progressProperty());
        progressText.textProperty().bind(loadingTask.messageProperty());

        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setAlwaysOnTop(true);
        initStage.show();

        System.err.println("___________" + Thread.currentThread().getStackTrace()[1].getClassName()+ "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                "___ here");

        loadingTask.stateProperty().addListener((observableValue, oldState, newState) -> {

            if (newState == Worker.State.SUCCEEDED) {

                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);

                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.play();
                fadeSplash.setOnFinished(actionEvent -> {

                    initStage.hide();
                    initCompletionHandler.complete();
                });
            }
        });

        new Thread(loadingTask).start();
    }

    public interface InitCompletionHandler {
        void complete();
    }

    @Override
    public void start(Stage initStage) throws Exception {

        showSplash(initStage, () -> showMainStage());
    }

    FXMLLoader loader;
    Parent root = null;
    MainController mainController;

    private void initMainStage(CustomTask<String> loadingTask) throws Exception {
        loadingTask.updateProgress(0, MaxSize);
        int sleepTime = 400;
        Thread.sleep(sleepTime);

        Platform.runLater(() -> {
            primaryStage = new Stage();
            loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        });

        loadingTask.updateProgress(20, MaxSize);
        loadingTask.updateMessage("Loading UI...");

        Thread.sleep(sleepTime);

        Platform.runLater(() -> {
            try {
                root = loader.load();
                mainController = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        loadingTask.updateProgress(40, MaxSize);
        loadingTask.updateMessage("Getting Controller...");

        Thread.sleep(sleepTime);
        Platform.runLater(() -> {
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
        });
        loadingTask.updateProgress(60, MaxSize);
        loadingTask.updateMessage("Creating Scene...");
        Thread.sleep(sleepTime);
        Platform.runLater(()->{
            mainScene = new Scene(root, 1800, 1200);

            mainController.initBindings();
        });

        loadingTask.updateProgress(80, MaxSize);
        loadingTask.updateMessage("Creating Bindings...");
        Thread.sleep(sleepTime);

        Platform.runLater(()->{
            Settings.initMenuBar(mainController, mainScene, primaryStage);

            double[] sps = {0, 0, 0};

            for (int i = 0; i < mainController.mainSplitPane.getDividers().size(); i++) {

                Double sp = Preferences.userRoot().getDouble("dividerPos" + i, mainController.mainSplitPane.getDividerPositions()[i]);
                sps[i] = sp;
            }

            mainController.mainSplitPane.setDividerPositions(sps);

            //AquaFx.style();

            mainScene.getStylesheets().add("stylesheets/styles.css");
        });


        loadingTask.updateProgress(87, MaxSize);
        loadingTask.updateMessage("Adding styles...");
        Thread.sleep(sleepTime);
        loadingTask.updateProgress(93, MaxSize);
        loadingTask.updateMessage("Adding keybindings...");

        Platform.runLater(()->{
            initKeyBindings(mainController);
        });


        loadingTask.updateProgress(100, MaxSize);
        loadingTask.updateMessage("Done...");
    }

    private void showMainStage() {

        primaryStage.setScene(mainScene);
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
