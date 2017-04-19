package mainPackage;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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

public class MainController implements Initializable {
    public TableView mainTableView;
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
    public Button stopCurrentSearchAction;
    public Button searchButton;
    public ProgressBar progressIndicator;
    public Slider playPositionSlider;
    public CheckBox autoplayCheckbox;
    public HBox mediaPlayerControls;
    public Button play2XSlowerButton;
    public Button play2XFasterButton;
    public Label mediaPlayerRateLabel;
    public StackPane mediaStackPane;
    public Slider volumeSlider;
    public Button stopMediaButton;
    public Button playMediaButton;
    public Label currentTimeLabel;
    public Label totalTimeLabel;
    public ToggleButton loopButton;
    public HBox sliderHbox;
    public Button fullScreenMediaButton;
    public HBox topHBox;
    public HBox bottomHBox;
    static MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/ClosedHH.wav").toURI().toString()));
    public VBox rightSidePaneTextVBox;
    public HBox topSecondHBox;
    public ToggleButton lockMediaView;
    ObservableList<FileInfo> files = FXCollections.observableArrayList();
    TreeItem root;
    boolean out = false;
    boolean hidden = false;
    static CustomTask<String> searchingTask;
    static CustomTask<String> loadingTask;

    public DoubleProperty mediaPlayerRateProperty = new SimpleDoubleProperty(1);
    public DoubleProperty mediaPlayerVolumeProperty = new SimpleDoubleProperty(1);
    Double[] dividerPositions = {0d, 0d};
    FilePathTreeItem currentlySelectedFilePathTreeItem = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        menuBar.setUseSystemMenuBar(true);

        mainTableView.getColumns().clear();

        rightPaneMediaView.setMediaPlayer(mediaPlayer);

        TreeViewInitialization.initTreeView(this);

        Utilities.initEffectsRightPane(this);

        mainSplitPane.setDividerPositions(0.2, 0.9);

        mainTableView.setEditable(false);

        mainTableView.setItems(files);

        TableViewUtilities.initTableViewColumns(mainTableView);

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
            if (e.getButton() == MouseButton.PRIMARY) {

                Object item = mainTableView.getSelectionModel().getSelectedItem();

                if (lockMediaView.isSelected()) {
                    startPlayingMedia(item, false);
                } else {
                    startPlayingMedia(item, true);
                }
            }
        });

        mainTableView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {
                Object item = mainTableView.getSelectionModel().getSelectedItem();

                if (lockMediaView.isSelected()) {
                    startPlayingMedia(item, false);
                } else {
                    startPlayingMedia(item, true);
                }
            }
        });

        fileBrowserTreeTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {

                Object item = fileBrowserTreeTable.getSelectionModel().getSelectedItem();

                if (!lockMediaView.isSelected()) {

                    startPlayingMediaFromTree(item);
                }
            }
        });

        directoryToSearchTextField.setText("/Users/jacobmenke/Desktop");

        initCheckBoxes();

        initTasks();
    }

    private void startPlayingMediaFromTree(Object item) {
        FilePathTreeItem filePathTreeItem = (FilePathTreeItem) item;

        currentlySelectedFilePathTreeItem = filePathTreeItem;

        updateRightSidePane(new FileInfo(filePathTreeItem.getPathString()));

        runInBackgroundThread(() -> {
            Utilities.updateThumbnailRightSidePane(MainController.this, filePathTreeItem);
        }, searchingTask);
    }

    public void startPlayingMedia(Object item, boolean openInRightPane) {
        FileInfo fileInfo = (FileInfo) item;

        FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), MainController.this);

        currentlySelectedFilePathTreeItem = filePathTreeItem;

        if (selectInTreeViewCheckBox.isSelected()) {

            runInBackgroundThread(() -> {
                FilePathTreeItem.selectTreeItemRecursively(MainController.this, Paths.get(fileInfo.getAbsolutePath()), true);
            }, searchingTask);
        }

        if (openInRightPane) {
            updateRightSidePane(fileInfo);

            runInBackgroundThread(() -> {
                Utilities.updateThumbnailRightSidePane(MainController.this, filePathTreeItem);
            }, searchingTask);
        }
    }

    private void initTasks() {
        searchingTask = new CustomTask<>(this);
    }

    public void searchForFile(ActionEvent actionEvent) {

        RegexUtilities.searchAndRefresh(this);
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
        RegexUtilities.searchAndRefresh(this);
        rightPaneMediaView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty());
        rightPaneImageView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty());
//        thinkingIndicator.progressProperty().bind(searchingTask.progressProperty());
        stopCurrentSearchAction.visibleProperty().bind(thinkingIndicator.visibleProperty());
        searchButton.disableProperty().bind(thinkingIndicator.visibleProperty());
        activityIndicatorLabel.textProperty().bind(searchingTask.messageProperty());
        stopCurrentSearchAction.setOnAction(e -> {
            searchingTask.getFuture().cancel(true);
        });
        currentTimeLabel.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.15));

        ObjectProperty<Font> fontObjectProperty = new SimpleObjectProperty<Font>(Font.font("Helvetica", FontWeight.BOLD, 10));

        currentTimeLabel.fontProperty().bind(fontObjectProperty);
        totalTimeLabel.fontProperty().bind(fontObjectProperty);
        mediaPlayerRateLabel.fontProperty().bind(fontObjectProperty);

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

//        initMediaPlayerBindings();

    }

    public void initMediaPlayerBindings() {

        mediaPlayer.rateProperty().bind(mediaPlayerRateProperty);

        volumeSlider.valueProperty().bindBidirectional(mediaPlayerVolumeProperty);
        mediaPlayer.volumeProperty().bind(mediaPlayerVolumeProperty);

        playMediaButton.graphicProperty().bind(Bindings.when(mediaPlayer.statusProperty().isEqualTo(MediaPlayer.Status.PLAYING))
                .then(new ImageView(new Image("file:src/main/resources/png/pause.png"))).otherwise(new ImageView(new Image("file:src/main/resources/png/play.png"))));
        play2XFasterButton.setOnAction(e -> {
            if (mediaPlayerRateProperty.get() < 8) {
                mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() + 0.5);
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
            if (currentlySelectedFilePathTreeItem != null && currentlySelectedFilePathTreeItem.getType().equals("music") || currentlySelectedFilePathTreeItem.getType().equals("video")) {
                mediaPlayerControls.setVisible(true);
            }
        });

        mediaStackPane.setOnMouseExited(e -> {
            if (currentlySelectedFilePathTreeItem != null && currentlySelectedFilePathTreeItem.getType().equals("music") || currentlySelectedFilePathTreeItem.getType().equals("video")) {
                mediaPlayerControls.setVisible(false);
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

    public void updateRightSidePane(FileInfo newValue) {
        FileInfo fileInfo = newValue;
        if (fileInfo != null) {
            fileNamDetailLabelContent.setText(fileInfo.getFileName());

            sizeDetailLabelContent.setText(CommonUtilities.turnBytesIntoHumanReadable(fileInfo.length()));

            pathLabelContent.setText(fileInfo.getAbsolutePath());

            lastModifiedLabelContent.setText(Utilities.formatDate(fileInfo.lastModified()));
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
        runInBackgroundThread(() -> {
            fileBrowserTreeTable.getRoot().setExpanded(false);
            if (!fileBrowserTreeTable.getRoot().getChildren().isEmpty()) {
                fileBrowserTreeTable.getRoot().getChildren().forEach(node -> {
                    collapseNodesRecursive((TreeItem) node);
                });
            }
        },loadingTask);
    }

    public void findDirectoryInTree(ActionEvent actionEvent) {

        runInBackgroundThread(() -> {
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
        }, searchingTask);
    }

    public void runInBackgroundThread(Runnable r, CustomTask<String> mytask) {
        thinkingIndicator.setVisible(true);

        mytask = new CustomTask<>(this);

        mytask.setRunnable(r);

        Thread thread = new Thread(mytask);

        thread.start();
    }

    public void goToHomeDirectory(ActionEvent actionEvent) {

        String home = System.getProperty("user.home");

        runInBackgroundThread(() -> {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true);
        }, searchingTask);
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

        runInBackgroundThread(() -> {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true);
        }, searchingTask);
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

        Preferences.userRoot().putDouble("dividerPos0", mainSplitPane.getDividerPositions()[0]);
        Preferences.userRoot().putDouble("dividerPos1", mainSplitPane.getDividerPositions()[1]);

        Utilities.removeFromView(rightSidePaneTextVBox);
        Utilities.removeFromView(topHBox);
        Utilities.removeFromView(bottomHBox);
        Utilities.removeFromView(topSecondHBox);

        mainSplitPane.setDividerPositions(0, 0);
    }

    public void returnToOldDividers(ActionEvent actionEvent) {

        Double sp = Preferences.userRoot().getDouble("dividerPos0", 0.2);
        Double sp2 = Preferences.userRoot().getDouble("dividerPos1", 0.8);

        Utilities.addToView(rightSidePaneTextVBox);
        Utilities.addToView(topHBox);
        Utilities.addToView(bottomHBox);
        Utilities.addToView(topSecondHBox);

        mainSplitPane.setDividerPositions(sp, sp2);
    }
}
