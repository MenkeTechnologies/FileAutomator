package mainPackage;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.*;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.Reflection;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.text.*;
import javafx.stage.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.prefs.Preferences;

import javafx.scene.text.Font;
import javafx.util.Duration;

public class MainController implements Initializable {
    public TableView<FileInfo> mainTableView;
    public TextField mainTextField;
    public TextField directoryToSearchTextField;
    public SplitPane mainSplitPane;
    public Text sizeLabel;
    public Text lastModifiedLabel;
    public Text pathLabel;
    public TextField destinationCopyAllTextField;
    public Text fileNamDetailLabel;
    public CheckBox caseInsensitiveMatchingCheckbox;
    public TreeView fileBrowserTreeTable;
    public CheckBox automaticSearchCheckBox;
    public CheckBox showHiddenFilesCheckBox;
    public CheckBox selectInTreeViewCheckBox;
    public CheckBox hideDirectoriesCheckBox;
    public CheckBox pathMatchingCheckbox;
    public ImageView rightPaneImageView;
    public VBox rightSidePaneVBox;
    public ScrollPane rightPaneScrollPane;
    public Text fileNamDetailLabelContent;
    public Text sizeDetailLabelContent;
    public Text pathLabelContent;
    public Text lastModifiedLabelContent;
    public TextFlow textFlowRightPane;
    public Text textLabel;
    public Text textContent;
    public MediaView rightPaneMediaView;
    public MenuBar menuBar;
    public ProgressIndicator thinkingIndicator;
    public Label activityIndicatorLabel;
    public Button stopCurrentSearchButton;
    public Button searchButton;
    public ProgressBar progressIndicator;
    public Slider playPositionSlider;
    public ToggleButton autoplayCheckbox;
    public FlowPane mediaPlayerControls;
    public Button play2XSlowerButton;
    public Button play2XFasterButton;
    public Label mediaPlayerRateLabel;
    public StackPane mediaStackPane;
    public Slider volumeSlider;
    public Button playMediaButton;
    public Label currentTimeLabel;
    public Label totalTimeLabel;
    public ToggleButton loopButton;
    public HBox sliderHbox;
    public Button fullScreenMediaButton;
    public HBox topHBox;
    public HBox bottomHBox;
    static MediaPlayer mediaPlayer;
    public VBox rightSidePaneTextVBox;
    public HBox topSecondHBox;
    public ToggleButton lockMediaViewMediaControlsToggle;
    public Label numberResultsLabel;
    public Label loadingFileLabel;
    public Label volumeAndCurrentTimeSwipeLabel;
    public ToggleButton autoPlayMediaControl;
    public Label fileNameLabelMediaControls;
    public Button normalScreenMediaButton;
    public ToggleButton removeSliderMediaControl;
    public HBox filterHBox;
    public MenuButton menuButtonAdd;
    public Cylinder sphere;
    public PointLight pointLight;
    public VBox imagesVBox;
    public ToggleButton showPlayingIconCheckbox;
    public ToggleButton showPlayingIconTreeCheckbox;
    public ToggleButton showLineNumbersCheckbox;
    public HBox showPlayinIconTreeHBox;
    public ToggleButton showReflectionBottomButton;
    public ToggleButton showReflectionButton;
    public ToggleButton lockMediaViewBottomToggle;
    public ToggleButton fitScreenToggleButton;
    public ToggleButton fitScreenToggleMediaButton;
    ObservableList<FileInfo> files = FXCollections.observableArrayList();
    TreeItem root;
    boolean out = false;
    boolean hidden = false;
    static CustomTask<String> searchingTask;
    static CustomTask<String> loadingTask;
    static CustomTask<String> rasterizingTask;
    public DoubleProperty mediaPlayerRateProperty = new SimpleDoubleProperty(1);
    public DoubleProperty mediaPlayerVolumeProperty = new SimpleDoubleProperty(1);
    Double[] dividerPositions = {0d, 0d};
    FilePathTreeItem currentlySelectedFilePathTreeItem = null;
    Timeline timeline;
    Timer disappearTimer;
    ArrayList<String> filesForAutoplay = new ArrayList<>();
    ObservableList<Node> splitPaneChildren;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/ClosedHH.wav").toExternalForm()));
        Double scalingFactor = 7d;

        sphere = new Cylinder(2 * scalingFactor, 2 * scalingFactor);
        PhongMaterial material = new PhongMaterial(Color.BLACK);
        material.setSpecularColor(Color.LIGHTBLUE);
        material.setSpecularPower(10.0d);
        sphere.setMaterial(material);
//        sphere.setEffect(new DropShadow());

        pointLight = new PointLight(Color.WHITE);
        sphere.setMaterial(material);

        DoubleProperty translateY = new SimpleDoubleProperty();

        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0), new KeyValue(translateY, 3000)),
                new KeyFrame(javafx.util.Duration.seconds(10), new KeyValue(translateY, 0)));

        sphere.setDrawMode(DrawMode.LINE);

        sphere.setRotationAxis(new Point3D(3, 1, 1));
        sphere.rotateProperty().bind(translateY);
        sphere.setTranslateZ(90);

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);

        Group group = new Group(sphere, pointLight);

        group.prefWidth(100);
        group.setStyle("-fx-background-color: red");

        topHBox.getChildren().add(group);

        menuBar.setUseSystemMenuBar(true);

        mainTableView.getColumns().clear();

        rightPaneMediaView.setMediaPlayer(mediaPlayer);

        TreeViewInitialization.initTreeView(this);

        Utilities.initEffectsRightPane(this);

        mainSplitPane.setDividerPositions(0.2, 0.9);

        mainTableView.setEditable(false);

        mainTableView.setItems(files);

        TableViewUtilities.initTableViewColumns(mainTableView, this);

        mainTableView.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isSecondaryButtonDown()) {
                e.consume();
            }
        });

        fileBrowserTreeTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isSecondaryButtonDown()) {
                e.consume();
            }
        });

        mainTableView.setOnMouseClicked(e -> {
            Utilities.fromAutoPlay = false;

            if (e.getButton() == MouseButton.PRIMARY) {

                Object item = mainTableView.getSelectionModel().getSelectedItem();

                if (item != null) {
                    if (lockMediaViewMediaControlsToggle.isSelected()) {
                        startPlayingMedia(item, false, false);
                    } else {
                        startPlayingMedia(item, true, false);
                    }
                }
            }
        });

        fileBrowserTreeTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                Object item = fileBrowserTreeTable.getSelectionModel().getSelectedItem();

                if (!lockMediaViewMediaControlsToggle.isSelected()) {

                    startPlayingMediaFromTree(item);
                }
            }
        });

        rightPaneScrollPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {

                ObservableList<FileInfo> list = mainTableView.getItems();

                ContextMenu cm = Utilities.createContextMenu(new FileInfo(pathLabelContent.getText()), mainTableView, list, this, "stackPane");
                cm.show(mediaStackPane.getScene().getWindow(), e.getSceneX(), e.getSceneY());
            }
        });

        DraggingInit.initDraggingBindings(this);

        directoryToSearchTextField.setText("/Users/jacobmenke/Desktop");

        initCheckBoxes();

        initTasks();

        initToolTips();
    }

    public void initTreeViewKeyBindings() {
        fileBrowserTreeTable.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {
                Object item = fileBrowserTreeTable.getSelectionModel().getSelectedItem();

                if (!lockMediaViewMediaControlsToggle.isSelected()) {

                    startPlayingMediaFromTree(item);
                }
            }
        });
    }

    public void initMainTableViewKeyBindings() {
        mainTableView.setOnKeyReleased(e -> {
            Utilities.fromAutoPlay = false;

            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {

                Object item = mainTableView.getSelectionModel().getSelectedItem();

                if (lockMediaViewMediaControlsToggle.isSelected()) {
                    startPlayingMedia(item, false, false);
                } else {
                    startPlayingMedia(item, true, false);
                }
            }

        });
    }

    private void initToolTips() {

    }

    public void startPlayingMediaFromTree(Object item) {

        FilePathTreeItem filePathTreeItem = (FilePathTreeItem) item;

        currentlySelectedFilePathTreeItem = filePathTreeItem;

        if (filePathTreeItem.getPathString() != null) {
            FileInfo file = new FileInfo(filePathTreeItem.getPathString());

            updateRightSidePane(file);

            runInBackgroundThreadSecondary(() -> {
                Utilities.updateThumbnailRightSidePane(MainController.this, filePathTreeItem);
            });

            if (showPlayingIconTreeCheckbox.isSelected()) {

                FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(filePathTreeItem.getPathString()), true);
            }
        }
    }

    public void startPlayingMedia(Object item, boolean playInRightPane, boolean fromContext) {

        FileInfo fileInfo = (FileInfo) item;

        FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), MainController.this);

        currentlySelectedFilePathTreeItem = filePathTreeItem;

        if (selectInTreeViewCheckBox.isSelected()) {

            if (!fromContext) {

                if (!Utilities.fromAutoPlay) {
                    runInBackgroundThreadSecondary(() -> {
                        FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(fileInfo.getAbsolutePath()), true);
                    });
                }
            }
        }

        if (playInRightPane) {

            if (showPlayingIconTreeCheckbox.isSelected()) {
                runInBackgroundThreadSecondary(() -> {

                    FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(fileInfo.getAbsolutePath()), true);
                });
            }
            updateRightSidePane(fileInfo);

            runInBackgroundThreadSecondary(() -> {
                Utilities.updateThumbnailRightSidePane(MainController.this, filePathTreeItem);
            });
        }
    }

    private void initTasks() {
        loadingTask = new CustomTask<>(this);
        searchingTask = new CustomTask<>(this);
    }

    public void searchForFile(ActionEvent actionEvent) {

        if (new File(directoryToSearchTextField.getText()).exists()) {
            RegexUtilities.searchAndRefresh(this);
        } else {
            CommonUtilities.showErrorAlert("Directory \"" + directoryToSearchTextField.getText() + "\" does not exist.");
        }
    }

    public void checkToShowHiddenFiles(Path file) {
        hidden = false;

        file.iterator().forEachRemaining(path -> {
            if (path.toFile().getAbsoluteFile().isHidden()) {
                hidden = true;
            }
        });

        if (hidden) {
            if (showHiddenFilesCheckBox.isSelected()) {
                checktoShowDirectories(file);
            }
        } else {
            checktoShowDirectories(file);
        }
    }

    private void checktoShowDirectories(Path file) {

        if (hideDirectoriesCheckBox.isSelected()) {
            if (!file.toFile().isDirectory()) {
                files.add(new FileInfo(file.toString()));
            }
        } else {
            files.add(new FileInfo(file.toString()));
        }
    }

    public void initBindings() {

        mainTextField.setText("mp4");

        fitScreenToggleButton.setOnAction(e -> fitScreenAction(e, 1.0));
        fitScreenToggleMediaButton.setOnAction(e -> fitScreenAction(e, 1.0));

        RegexUtilities.searchAndRefresh(this);
        rightPaneMediaView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty());
        rightPaneImageView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty());
//        thinkingIndicator.progressProperty().bind(searchingTask.progressProperty());
        stopCurrentSearchButton.visibleProperty().bind(sphere.visibleProperty());
        stopCurrentSearchButton.managedProperty().bind(sphere.managedProperty());

        activityIndicatorLabel.textProperty().bind(searchingTask.messageProperty());
        loadingFileLabel.textProperty().bind(loadingTask.messageProperty());

        fullScreenMediaButton.disableProperty().bind(Utilities.maximized);
        normalScreenMediaButton.disableProperty().bind(Utilities.maximized.not());
        fitScreenToggleMediaButton.selectedProperty().bindBidirectional(fitScreenToggleButton.selectedProperty());

        stopCurrentSearchButton.setOnAction(e -> {
            if (searchingTask.getFuture() != null) {
                searchingTask.getFuture().cancel(true);
            }
            if (loadingTask.getFuture() != null) {
                if (stopCurrentSearchButton.getText().equals("Stop Load")) {
                    loadingTask.getFuture().cancel(true);
                }
            }
        });

        lockMediaViewBottomToggle.selectedProperty().bindBidirectional(lockMediaViewMediaControlsToggle.selectedProperty());
        currentTimeLabel.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.15));

        ObjectProperty<Font> fontObjectProperty = new SimpleObjectProperty<Font>(Font.font("Helvetica", FontWeight.BOLD, 10));

        currentTimeLabel.fontProperty().bind(fontObjectProperty);
        totalTimeLabel.fontProperty().bind(fontObjectProperty);

        volumeAndCurrentTimeSwipeLabel.fontProperty().bind(fontObjectProperty);
        volumeAndCurrentTimeSwipeLabel.effectProperty().bind(Bindings.when(showReflectionBottomButton.selectedProperty()).then(new Reflection()).otherwise((Reflection) null));

        rightPaneScrollPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Double scalingFactor = 0.03;
                Integer startingValue = 5;

                fontObjectProperty.set(Font.font("Arial", FontWeight.BOLD, newValue.doubleValue() * scalingFactor + startingValue));
            }
        });

        totalTimeLabel.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.15));
        playPositionSlider.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.7));

        volumeSlider.valueProperty().set(1);
    }

    public void initMediaPlayerBindings(String sender) {

        if (sender.equals("video") || sender.equals("music")) {

            volumeAndCurrentTimeSwipeLabel.setVisible(false);

            mediaStackPane.setOnScroll(e -> {

                FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(pathLabelContent.getText()), this);
                if (filePathTreeItem.getType().equals("video") || filePathTreeItem.getType().equals("music")) {
                    if (Math.abs(e.getDeltaY()) > 4) {
                        Utilities.swipeRight = false;

                        double changeY = e.getDeltaY() * -1;
                        double scalingFactor = 0.005;

                        double changeTo = changeY * scalingFactor + volumeSlider.getValue();

                        if (changeTo < 1 || changeTo > 0) {

                            if (volumeSlider.valueProperty() != null) {
                                volumeSlider.valueProperty().set(changeTo);
                            }

                            volumeSlider.setValueChanging(true);
                            volumeSlider.setValueChanging(false);
                        }
                    }

                    if (mediaPlayer.getCurrentTime().greaterThan(Duration.seconds(0)) && mediaPlayer.getCurrentTime().lessThan(mediaPlayer.getTotalDuration().subtract(Duration.seconds(2)))) {
                        if (Math.abs(e.getDeltaX()) > 4) {
                            Utilities.swipeRight = true;
                            double changeX = e.getDeltaX();
                            double scalingFactorX = 0.001;
                            double changeToX = changeX * scalingFactorX;

                            if (changeToX < 1 || changeToX > 0) {
                                volumeAndCurrentTimeSwipeLabel.setVisible(true);
                                Duration newDuration = Duration.millis(changeToX * mediaPlayer.getTotalDuration().toMillis()).add(mediaPlayer.getCurrentTime());
                                if (newDuration.toMillis() < 0) newDuration = Duration.ZERO;
                                if (newDuration.greaterThan(mediaPlayer.getTotalDuration())) {
                                    newDuration = mediaPlayer.getTotalDuration();
                                    mediaPlayer.pause();
                                }

                                mediaPlayer.seek(newDuration);
                            }
                            hideNodeAfterDelay(volumeAndCurrentTimeSwipeLabel);
                        }
                    }

                    e.consume();
                }
            });

            volumeSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    volumeAndCurrentTimeSwipeLabel.setVisible(true);
                    if (oldValue && !newValue) {
                        hideNodeAfterDelay(volumeAndCurrentTimeSwipeLabel);
                    }
                }
            });

            mediaPlayerVolumeProperty.bind(volumeSlider.valueProperty());

            mediaPlayerVolumeProperty.addListener(new InvalidationListener() {
                @Override
                public void invalidated(javafx.beans.Observable observable) {
                    Utilities.swipeRight = false;

                    volumeAndCurrentTimeSwipeLabel.setText(String.format("Volume: %.1f%%", ((DoubleProperty) observable).doubleValue() * 100));
                }
            });

            mediaPlayer.rateProperty().bind(mediaPlayerRateProperty);

            mediaPlayer.volumeProperty().bind(mediaPlayerVolumeProperty);

            playMediaButton.graphicProperty().bind(Bindings.when(mediaPlayer.statusProperty().isEqualTo(MediaPlayer.Status.PLAYING))
                    .then(new ImageView(new Image(getClass().getResourceAsStream("/png/pause.png")))).otherwise(new ImageView(new Image(getClass().getResourceAsStream("/png/play.png")))));
            play2XFasterButton.setOnAction(e -> {
                if (mediaPlayerRateProperty.get() < 8) {
                    mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() + 0.25);
                    mediaPlayerRateLabel.setText(Bindings.format("%3.2fx", mediaPlayerRateProperty.get()).getValue());
                }
            });

            play2XSlowerButton.setOnAction(e -> {
                if (mediaPlayerRateProperty.get() > 0) {
                    mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() - 0.25);
                    mediaPlayerRateLabel.setText(Bindings.format("%3.2fx", mediaPlayerRateProperty.get()).getValue());
                }
            });

            mediaStackPane.setOnMouseEntered(e -> {
                FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(pathLabelContent.getText()), this);
                if (filePathTreeItem.getType().equals("video") || filePathTreeItem.getType().equals("music")) {
                    mediaPlayerControls.setVisible(true);
                    disappearTimer = new Timer();
                    disappearTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                Utilities.removeFromView(mediaPlayerControls);
                            });
                        }
                    }, 3 * 1000);
                }
            });

            mediaStackPane.setOnMouseMoved(e -> {

                FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(pathLabelContent.getText()), this);
                if (filePathTreeItem.getType().equals("video") || filePathTreeItem.getType().equals("music")) {

                    if (!mediaPlayerControls.isVisible()) {
                        Utilities.addToView(mediaPlayerControls);
                    } else {
                        if (disappearTimer != null) {
                            disappearTimer.cancel();
                            disappearTimer.purge();
                            disappearTimer = new Timer();
                            disappearTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        Utilities.removeFromView(mediaPlayerControls);
                                    });
                                }
                            }, 3000);
                        }
                    }
                }
            });

            mediaStackPane.setOnMouseExited(e -> {
                FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(pathLabelContent.getText()), this);
                if (filePathTreeItem.getType().equals("video") || filePathTreeItem.getType().equals("music")) {
                    Utilities.removeFromView(mediaPlayerControls);
                }
                if (disappearTimer != null) {
                    disappearTimer.cancel();
                    disappearTimer.purge();
                }
            });

            playMediaButton.setOnAction(ez -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                } else {

                    mediaPlayer.play();
                }
            });
        }
    }

    public void hideNodeAfterDelay(Node node) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(3000);

                Platform.runLater(() -> node.setVisible(false));
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });

        t.start();
    }

    public void updateRightSidePane(FileInfo newValue) {

        FileInfo fileInfo = newValue;
        if (fileInfo != null) {
            fileNamDetailLabelContent.setText(fileInfo.getFileName());

            sizeDetailLabelContent.setText(CommonUtilities.turnBytesIntoHumanReadable(fileInfo.length()));

            pathLabelContent.setText(fileInfo.getAbsolutePath());

            lastModifiedLabelContent.setText(Utilities.formatDate(fileInfo.lastModified()));
        }

        if (showPlayingIconCheckbox.isSelected()) {
            mainTableView.refresh();
        }
    }

    private void initCheckBoxes() {

        automaticSearchCheckBox.setOnAction(e -> {
            RegexUtilities.searchAndRefresh(this);
        });

        showHiddenFilesCheckBox.setOnAction(e -> {
            RegexUtilities.searchAndRefresh(this);
        });

        hideDirectoriesCheckBox.setOnAction(e -> {
            RegexUtilities.searchAndRefresh(this);
        });

        pathMatchingCheckbox.setOnAction(e -> {
            RegexUtilities.searchAndRefresh(this);
        });

        autoPlayMediaControl.selectedProperty().bindBidirectional(autoplayCheckbox.selectedProperty());
        showReflectionButton.selectedProperty().bindBidirectional(showReflectionBottomButton.selectedProperty());
    }

    private void refreshTreeViewFromBottom() {

    }

    public void chooseDir(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        File newDirToSearch = fc.showOpenDialog(mainTableView.getScene().getWindow());
        directoryToSearchTextField.setText(newDirToSearch.getParent());
    }

    public void refreshTreeView(ActionEvent actionEvent) {
        FilePathTreeItem filePathTreeItem = (FilePathTreeItem) fileBrowserTreeTable.getSelectionModel().getSelectedItem();
        try {
            filePathTreeItem.populateSourceAndImmediateChildren(filePathTreeItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void collapseNodesRecursive(TreeItem treeItem) {
        treeItem.setExpanded(false);
        treeItem.getChildren().forEach(node -> {
            collapseNodesRecursive((TreeItem) node);
        });
    }

    public void collapseAllTreeNodes(ActionEvent actionEvent) {
        runInBackgroundThreadSecondary(() -> {
            fileBrowserTreeTable.getRoot().setExpanded(false);
            if (!fileBrowserTreeTable.getRoot().getChildren().isEmpty()) {
                fileBrowserTreeTable.getRoot().getChildren().forEach(node -> {
                    collapseNodesRecursive((TreeItem) node);
                });
            }
        });
    }

    public void findDirectoryInTree(ActionEvent actionEvent) {

        runInBackgroundThreadSecondary(() -> {
            if (!directoryToSearchTextField.getText().equals("")) {
                FileInfo fileInfo = new FileInfo(directoryToSearchTextField.getText());

                if (fileInfo.exists()) {
                    FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), MainController.this);

                    FilePathTreeItem.selectTreeItemRecursively(MainController.this, Paths.get(fileInfo.getAbsolutePath()), true);
                } else {
                    String error = "Not A Valid File or Folder.";
                    CommonUtilities.showErrorAlert(error);
                }
            }
        });
    }

    public void runInBackgroundThread(Runnable r) {
        Utilities.addToView(sphere);
        timeline.play();
//        Utilities.addToView(thinkingIndicator);
        System.out.println("visible");
        stopCurrentSearchButton.setText("Stop Search");

        searchingTask = new CustomTask<>(this, r, false);
        activityIndicatorLabel.textProperty().bind(searchingTask.messageProperty());

        searchButton.disableProperty().bind(searchingTask.runningProperty());

        searchingTask.setRunnable(r);

        Thread thread = new Thread(searchingTask);

        thread.start();
    }

    public void runInBackgroundThreadSecondary(Runnable r) {

        if (searchingTask.getState() != Task.State.RUNNING) {
            Utilities.addToView(sphere);
            timeline.play();
            System.out.println("started secondary");
            stopCurrentSearchButton.setText("Stop Load");
            loadingTask = new CustomTask<String>(this, r, false);
        } else {
            loadingTask = new CustomTask<String>(this, r, true);
        }

        loadingFileLabel.textProperty().bind(MainController.loadingTask.messageProperty());

        Thread thread = new Thread(loadingTask);

        thread.start();
    }

    public void goToHomeDirectory(ActionEvent actionEvent) {

        String home = System.getProperty("user.home");

        runInBackgroundThreadSecondary(() -> {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true);
        });
    }

    public void copyAllButtonAction(ActionEvent actionEvent) {

        Utilities.copyFilesFromTableViewToDirectory(mainTableView, destinationCopyAllTextField.getText());
    }

    public void openFromRightSidePane(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().open(new File(pathLabelContent.getText()));
        } catch (IOException e) {
            CommonUtilities.showErrorAlert("Could not open file.");
        }
    }

    public void copyPath(ActionEvent actionEvent) {
        CommonUtilities.copyToClipboard(pathLabelContent.getText());
    }

    public void openEnclosing(ActionEvent actionEvent) {

        CommonUtilities.openEnclosingDirectory(new FileInfo(pathLabelContent.getText()));
    }

    public void moveToTrash(ActionEvent actionEvent) {
        CommonUtilities.moveToTrash(new FileInfo(pathLabelContent.getText()), mainTableView, files, MainController.this);
    }

    public void goToDesktopDirectory(ActionEvent actionEvent) {
        String home = System.getProperty("user.home") + File.separator + "Desktop";

        runInBackgroundThreadSecondary(() -> {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true);
        });
    }

    public void openDestinationFolder(ActionEvent actionEvent) {
        File newFile = new File(destinationCopyAllTextField.getText());

        if (newFile.exists()) {
            try {
                Desktop.getDesktop().open(newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            CommonUtilities.showErrorAlert("Destination directory does not exist.");
        }
    }

    public void maximizeVideo(ActionEvent actionEvent) {

        removePanes("mediaContextMenu");
    }

    public void returnToOldDividers(ActionEvent actionEvent) {
        restorePanesToOld("mediaContextMenu");
    }

    public void removePaneSingular(String pane) {
        ObservableList<Node> items = mainSplitPane.getItems();

        if (items.size() == 1) {
            CommonUtilities.showErrorAlert("Cannot Remove Last Pane");
        } else if (items.size() == 3) {
            checkForSender(pane, items);
        } else if (items.size() == 2) {

            if (pane.equals("mediaContextMenu")) {
                items.remove(rightPaneScrollPane);
            } else {

                if (pane.equals("tableView")) {
                    items.remove(mainTableView);
                } else if (pane.equals("treeView")) {
                    items.remove(fileBrowserTreeTable);
                }
                if (items.get(0) == rightPaneScrollPane) {
                    //removing all but media view right pane
                    Utilities.removeFromView(rightSidePaneTextVBox);
                    Utilities.removeFromView(topHBox);
                    Utilities.removeFromView(bottomHBox);
                    Utilities.removeFromView(topSecondHBox);
                    Utilities.maximized.set(true);
                }
            }
        }
    }

    public void checkForSender(String pane, ObservableList<Node> items) {
        if (pane.equals("tableView")) {
            items.remove(mainTableView);
        } else if (pane.equals("treeView")) {
            items.remove(fileBrowserTreeTable);
        } else {
            items.remove(rightPaneScrollPane);
        }
    }

    public void removePanes(String mediaControl) {

        ObservableList<Node> items = mainSplitPane.getItems();

        if (items.size() == 3) {
            Preferences.userRoot().putDouble("dividerPos0", mainSplitPane.getDividerPositions()[0]);
            Preferences.userRoot().putDouble("dividerPos1", mainSplitPane.getDividerPositions()[1]);

            if (mediaControl.equals("mediaContextMenu")) {

                FileInfo fileInfo = new FileInfo(pathLabelContent.getText());
                String type = FileTypeUtilities.getFileType(fileInfo.getAbsolutePath());

                if (type.equals("video") || type.equals("music") || type.equals("image")) {
                    Utilities.removeFromView(rightSidePaneTextVBox);
                }
                Utilities.removeFromView(topHBox);
                Utilities.removeFromView(bottomHBox);
                Utilities.removeFromView(topSecondHBox);
                items.remove(0, 2);

                Utilities.addToView(fileNameLabelMediaControls);
            } else if (mediaControl.equals("treeView")) {
                items.remove(1, 3);
            } else if (mediaControl.equals("tableView")) {
                items.remove(0);
                items.remove(1);
            }
        } else if (items.size() == 2) {

            if (mediaControl.equals("mediaContextMenu")) {

                Utilities.removeFromView(rightSidePaneTextVBox);
                Utilities.removeFromView(topHBox);
                Utilities.removeFromView(bottomHBox);
                Utilities.removeFromView(topSecondHBox);
                items.remove(0);
            } else if (mediaControl.equals("treeView")) {
                items.remove(1);
            } else if (mediaControl.equals("tableView")) {
                if (items.get(0) != mainTableView) {
                    items.remove(0);
                } else {
                    items.remove(1);
                }
            }
        } else {
            CommonUtilities.showErrorAlert("Already Maximized.");
        }
        Utilities.maximized.set(true);
    }

    public void restorePanesToOld(String mediaControl) {

        Double sp = Preferences.userRoot().getDouble("dividerPos0", 0.2);
        Double sp2 = Preferences.userRoot().getDouble("dividerPos1", 0.8);
        ObservableList<Node> items = mainSplitPane.getItems();

        mainSplitPane.getItems().clear();

        if (mediaControl.equals("mediaContextMenu")) {

            if (!rightSidePaneTextVBox.isVisible()) {
                Utilities.addToView(rightSidePaneTextVBox);
            }

            Utilities.addToView(topHBox);
            Utilities.addToView(bottomHBox);
            Utilities.addToView(topSecondHBox);
            Utilities.removeFromView(fileNameLabelMediaControls);

            Utilities.addToView(sliderHbox);
        }
        items.addAll(Arrays.asList(fileBrowserTreeTable, mainTableView, rightPaneScrollPane));

        mainSplitPane.setDividerPositions(sp, sp2);
        Utilities.maximized.set(false);
    }

    public void goToDownloadsDirectory(ActionEvent actionEvent) {
        String home = System.getProperty("user.home") + File.separator + "Downloads";

        runInBackgroundThreadSecondary(() -> {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true);
        });
    }

    public void rewindToStart(MouseEvent actionEvent) {

        if (actionEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (actionEvent.getButton() == MouseButton.PRIMARY) {
                if (actionEvent.getClickCount() == 1) {

                    mediaPlayer.stop();
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.pause();
                    mediaPlayer.play();
                } else {
                    Utilities.endOfMediaAction(this, false);
                }
            }
        }
    }

    public void removeSliderHBox(ActionEvent actionEvent) {

        if (removeSliderMediaControl.isSelected()) {
            Utilities.removeFromView(sliderHbox);
        } else {
            Utilities.addToView(sliderHbox);
        }
    }

    public void addFilter(ActionEvent actionEvent) {

        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        Label label = new Label();

        switch (menuItem.getText()) {
            case "+ Or":
                label.setText("OR");
                break;
            case "+ And":
                label.setText("AND");
                break;
            default:
                break;
        }

        TextField textField = new TextField();
        textField.setPrefWidth(40);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(label, textField);
        filterHBox.getChildren().add(hBox);

        label.setOnDragDetected(event -> {

            Dragboard db = label.startDragAndDrop(TransferMode.ANY);
            mainTableView.getScene().setCursor(Cursor.CLOSED_HAND);
            ClipboardContent clipboardContent = new ClipboardContent();

            clipboardContent.putString(filterHBox.getChildren().indexOf(hBox) + "");
            db.setContent(clipboardContent);

            event.consume();
        });

        fileBrowserTreeTable.setOnDragOver(e ->

        {
            if (e.getGestureSource() != mainTableView && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.ANY);

                mainTableView.getScene().setCursor(Cursor.DISAPPEAR);
            }

            e.consume();
        });

        fileBrowserTreeTable.setOnDragEntered(e -> {
            if (e.getSource() != mainTableView) {
                mainTableView.getScene().setCursor(Cursor.DISAPPEAR);
            }
            e.consume();
        });

        fileBrowserTreeTable.setOnDragExited(e -> {

            mainTableView.getScene().setCursor(Cursor.CLOSED_HAND);

            e.consume();
        });

        fileBrowserTreeTable.setOnDragDropped(e ->

        {

            Dragboard db = e.getDragboard();
            if (db.hasString()) {

                int integer = Integer.parseInt(db.getString());
                filterHBox.getChildren().remove(integer);
            }

            e.setDropCompleted(true);
            mainTableView.getScene().setCursor(Cursor.DEFAULT);

            e.consume();
        });

        label.setOnDragDone(e -> {
            System.out.println("done with drag and drop");
            e.consume();
        });
    }

    public void removeFilter(ActionEvent actionEvent) {

        if (filterHBox.getChildren().size() > 1) {
            filterHBox.getChildren().remove(filterHBox.getChildren().size() - 1);
        }
    }

    public void storeFileList(ActionEvent actionEvent) {
        if (autoplayCheckbox.isSelected()) {
            mainTableView.refresh();
            filesForAutoplay.clear();

            for (int i = 0; i < mainTableView.getItems().size(); i++) {

                FileInfo file = (FileInfo) mainTableView.getItems().get(i);
                filesForAutoplay.add(file.getAbsolutePath());
            }
        }
    }

    public void showPlayingIcon(ActionEvent actionEvent) {
        mainTableView.refresh();
    }

    public void showLineNumbers(ActionEvent actionEvent) {

        if (pathLabelContent != null) {

            FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(pathLabelContent.getText()), this);

            if (filePathTreeItem.isTextual()) {
                Utilities.updateThumbnailRightSidePane(this, filePathTreeItem);
            }
        }
    }

    public void showReflection(ActionEvent actionEvent) {

        if (showReflectionBottomButton.isSelected()) {
            Utilities.initEffects(this.rightPaneMediaView);
            Utilities.initEffects(this.rightPaneImageView);
            Utilities.initEffects(this.playPositionSlider);
            Utilities.initEffects(this.mediaPlayerControls);
//            Utilities.initEffects(this.volumeAndCurrentTimeSwipeLabel);
        } else {

            Utilities.removeEffects(this.rightPaneMediaView);
            Utilities.removeEffects(this.rightPaneImageView);
            Utilities.removeEffects(this.playPositionSlider);
            Utilities.removeEffects(this.mediaPlayerControls);
//            Utilities.removeEffects(this.volumeAndCurrentTimeSwipeLabel);
        }
    }

    public void playNext(ActionEvent actionEvent) {
        mediaPlayer.stop();
        mediaPlayer.play();
        if (autoplayCheckbox.isSelected()) {
            Utilities.endOfMediaAction(this, true);
        } else {
            Utilities.endOfMediaAction(this, true);
        }
    }

    public void showPlayingIconTree(ActionEvent actionEvent) {

        if (showPlayingIconTreeCheckbox.isSelected()) {
            fileBrowserTreeTable.refresh();

            FileInfo fileInfo = new FileInfo(pathLabelContent.getText());
            runInBackgroundThreadSecondary(() -> {

                FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(fileInfo.getAbsolutePath()), true);
            });
        } else {

            FileInfo fileInfo = new FileInfo(pathLabelContent.getText());
            runInBackgroundThreadSecondary(() -> {

                FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(fileInfo.getAbsolutePath()), true);
            });
        }
    }

    Rectangle2D oldScreenSize;
    ArrayList<Point2D> points = new ArrayList<>();
    Integer counter = 0;

    {
        Rectangle2D rect = Screen.getPrimary().getBounds();
        points.add(new Point2D(rect.getMinX(), rect.getMinY()));
        points.add(new Point2D(rect.getMinX(), rect.getMaxY() / 2));
        points.add(new Point2D(rect.getMaxX() / 2, rect.getMaxY() / 2));
        points.add(new Point2D(rect.getMaxX() / 2, rect.getMinY()));
    }

    {

    }

    public void fitScreenAction(ActionEvent actionEvent, Double size) {

        if (size == 0.25) {

            Rectangle2D rect = Screen.getPrimary().getBounds();

            Double oldX = mainSplitPane.getScene().getWindow().getX();
            Double oldY = mainSplitPane.getScene().getWindow().getY();

            Double height = mainSplitPane.getScene().getWindow().getHeight();
            Double width = mainSplitPane.getScene().getWindow().getWidth();
            oldScreenSize = new Rectangle2D(oldX, oldY, width, height);
            mainSplitPane.getScene().getWindow().setHeight(rect.getHeight() / 2);
            mainSplitPane.getScene().getWindow().setWidth(rect.getWidth() / 2);

            mainSplitPane.getScene().getWindow().setX(points.get(counter % 4).getX());
            mainSplitPane.getScene().getWindow().setY(points.get(counter % 4).getY());
            counter++;
        } else if (size == 1.0) {

            if (fitScreenToggleButton.isSelected()) {
                Rectangle2D rect = Screen.getPrimary().getBounds();

                Double oldX = mainSplitPane.getScene().getWindow().getX();
                Double oldY = mainSplitPane.getScene().getWindow().getY();

                Double height = mainSplitPane.getScene().getWindow().getHeight();
                Double width = mainSplitPane.getScene().getWindow().getWidth();

                oldScreenSize = new Rectangle2D(oldX, oldY, width, height);

                mainSplitPane.getScene().getWindow().setHeight(rect.getHeight());
                mainSplitPane.getScene().getWindow().setWidth(rect.getWidth());

                mainSplitPane.getScene().getWindow().setX(rect.getMinX());
                mainSplitPane.getScene().getWindow().setY(rect.getMinY());
            } else {

                if (oldScreenSize != null) {
                    mainSplitPane.getScene().getWindow().setHeight(oldScreenSize.getHeight());
                    mainSplitPane.getScene().getWindow().setWidth(oldScreenSize.getWidth());
                    mainSplitPane.getScene().getWindow().setX(oldScreenSize.getMinX());
                    mainSplitPane.getScene().getWindow().setY(oldScreenSize.getMinY());
                }
            }
        }
    }
}
