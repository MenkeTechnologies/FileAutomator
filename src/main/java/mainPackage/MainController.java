package mainPackage;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import javax.naming.Binding;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

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
    static MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/ClosedHH.wav").toURI().toString()));
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
    ObservableList<FileInfo> files = FXCollections.observableArrayList();
    TreeItem root;
    boolean out = false;
    boolean hidden = false;
    static CustomTask<String> backgroundTask;
    public DoubleProperty mediaPlayerRateProperty = new SimpleDoubleProperty(1);
    public DoubleProperty mediaPlayerVolumeProperty = new SimpleDoubleProperty(1);


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

        mainTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue instanceof FileInfo) {
                    FileInfo fileInfo = (FileInfo) newValue;
                    FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), MainController.this);
                    ;

                    if (selectInTreeViewCheckBox.isSelected()) {

                        runInBackgroundThreadSecondary(() -> {
                            FilePathTreeItem.selectTreeItemRecursively(MainController.this, Paths.get(fileInfo.getAbsolutePath()), true);
                        });
                    }

                    updateRightSidePane(fileInfo);

                    runInBackgroundThreadSecondary(() -> {
                        Utilities.updateThumbnailRightSidePane(MainController.this, filePathTreeItem);
                    });
                }
            }
        });

        fileBrowserTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue instanceof FilePathTreeItem) {
                    FilePathTreeItem filePathTreeItem = (FilePathTreeItem) newValue;

                    updateRightSidePane(new FileInfo(filePathTreeItem.getPathString()));

                    runInBackgroundThreadSecondary(() -> {
                        Utilities.updateThumbnailRightSidePane(MainController.this, filePathTreeItem);
                    });
                }
            }
        });

        directoryToSearchTextField.setText("/Users/jacobmenke/Desktop");

        initCheckBoxes();

        initTasks();
    }

    private void initTasks() {
        backgroundTask = new CustomTask<>(this);
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

        mainTextField.setText("png");
        RegexUtilities.searchAndRefresh(this);
        rightPaneMediaView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty());
        rightPaneImageView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty());
//        thinkingIndicator.progressProperty().bind(backgroundTask.progressProperty());
        stopCurrentSearchAction.visibleProperty().bind(thinkingIndicator.visibleProperty());
        searchButton.disableProperty().bind(thinkingIndicator.visibleProperty());
        activityIndicatorLabel.textProperty().bind(backgroundTask.messageProperty());
        stopCurrentSearchAction.setOnAction(e -> {
            backgroundTask.getFuture().cancel(true);
        });

        playPositionSlider.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.7));


//        initMediaPlayerBindings();

    }

    public void initMediaPlayerBindings() {

        mediaPlayer.rateProperty().bind(mediaPlayerRateProperty);

        volumeSlider.valueProperty().bindBidirectional(mediaPlayerVolumeProperty);
        mediaPlayer.volumeProperty().bind(mediaPlayerVolumeProperty);

        playMediaButton.textProperty().bind(Bindings.when(mediaPlayer.
                statusProperty().isEqualTo(MediaPlayer.Status.PLAYING)).then("Pause").otherwise("Play"));


        play2XFasterButton.setOnAction(e -> {
           if (mediaPlayerRateProperty.get() < 8){
               mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() + 0.5);
               mediaPlayerRateLabel.setText(Bindings.format("%3.2fx", mediaPlayerRateProperty.get()).getValue());
           }
        });

        play2XSlowerButton.setOnAction(e -> {
            if (mediaPlayerRateProperty.get() > 0){
                mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() - 0.25);
                mediaPlayerRateLabel.setText(Bindings.format("%3.2fx", mediaPlayerRateProperty.get()).getValue());
            }
        });

        mediaStackPane.setOnMouseEntered(e -> {
            mediaPlayerControls.setVisible(true);
        });

        mediaStackPane.setOnMouseExited(e -> {
            mediaPlayerControls.setVisible(false);
        });



        playMediaButton.setOnAction(ez -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
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
        });
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
        });
    }

    public void runInBackgroundThread(Runnable r) {
        thinkingIndicator.setVisible(true);

        backgroundTask = new CustomTask<>(this);

        backgroundTask.setRunnable(r);

        Thread thread = new Thread(backgroundTask);

        thread.start();
    }

    public void runInBackgroundThreadSecondary(Runnable r) {
        System.out.println("secondary background");

        Thread thread = new Thread(r);

        thread.start();
    }

    public void goToHomeDirectory(ActionEvent actionEvent) {

        String home = System.getProperty("user.home");

        runInBackgroundThread(() -> {
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

        runInBackgroundThread(() -> {
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
}
