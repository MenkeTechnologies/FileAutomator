package mainPackage;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import windows.ServiceWindow;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class Utilities {
    static boolean toggle = false;
    static boolean stopSlider = false;
    static boolean swipeRight = false;
    static ArrayList<ImageView> imageViews;
    public static BooleanProperty maximized = new SimpleBooleanProperty(false);
    static String textContent = null;
    static Image image = null;

    public static void initMenuBar(MenuBar menuBar, Scene scene, Stage stage) {
        Menu file = menuBar.getMenus().get(0);

        Scene oldScene = menuBar.getScene();
        Group group = new Group();

        MenuBar menuBar1 = new MenuBar();
        menuBar1.getMenus().addAll(menuBar.getMenus());

        group.getChildren().addAll(new Button("text"), menuBar1);
//        Scene newScene = new Scene(group, oldScene.getWidth(), oldScene.getHeight());
        Parent root = scene.getRoot();

        file.getItems().get(0).setOnAction(e -> {
            Platform.exit();
        });
        file.getItems().get(1).setOnAction(e -> {

            Double width = stage.getWidth();
            Double height = stage.getHeight();
            Double x = stage.getX();
            Double y = stage.getY();
            System.out.println("WIDTH:" + width);

            if (!toggle) {

                scene.setRoot(group);

                toggle = true;
            } else {

                scene.setRoot(root);

                toggle = false;
            }
        });
    }

    public static boolean isText(File file) throws IOException {

        FileReader reader = new FileReader(file);
        String type = null;
        int data = reader.read();
        char[] c = new char[3];

        for (int i = 0; i < 3; i++) {
            c[i] = (char) data;
            data = reader.read();
        }
        reader.close();
        System.out.println();
        type = Character.toString(c[0]);
        for (int i = 1; i < 2; i++)
            type = type + Character.toString(c[i]);

        if (!type.matches("[_a-zA-Z0-9\\-\\.]*")) {
            return false;
        }

        switch (type) {
            case "þÿ":
            case "ÿþ":
            case "ï»":
            default:
                return true;
        }
    }

    public static void updateThumbnailRightSidePane(MainController mainController, FilePathTreeItem filePathTreeItem) {
        System.gc();
        MainController.loadingTask.updateMessage("Loading File: " + filePathTreeItem.getPath().getFileName());
        //loading phase
        String fileType = filePathTreeItem.getType();

        FileInfo fileInfo = new FileInfo(filePathTreeItem.getPathString());

        switch (fileType) {
            case "image":
                image = new javafx.scene.image.Image("file://" + filePathTreeItem.getPathString());
                break;
            case "music":
                image = FilePathTreeItem.musicLargeImage;
            case "video":
                Media m = new Media(fileInfo.toURI().toString());
                MainController.mediaPlayer = new MediaPlayer(m);
                break;
            case "pdf":
                getImageViewsFromPDF(mainController, filePathTreeItem);
                break;
            case "jar":
                textContent = CommonUtilities.invokeCommandLineAndReturnString("jar", "tf", filePathTreeItem.getPathString());

                break;
            case "excel":
                textContent = MicrosoftUtilities.getStringFromExcelDocument(filePathTreeItem.getPathString());
                break;
            case "word":
                textContent = MicrosoftUtilities.getStringFromWordDocument(filePathTreeItem.getPathString());
                break;
            case "java":
            case "js":
            case "ruby":
            case "python":
            case "xml":
            case "html":
            case "css":
            case "text":
                try {
                    textContent = new String(Files.readAllBytes(filePathTreeItem.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        //UI updates

        Platform.runLater(() -> {

            MainController.loadingTask.updateMessage("Clearing Items");

            removeFromView(mainController.mediaStackPane);
            removeFromView(mainController.rightPaneMediaView);
            removeFromView(mainController.mediaPlayerControls);
            removeFromView(mainController.imagesVBox);

            removeFromView(mainController.sliderHbox);
            removeFromView(mainController.volumeAndCurrentTimeSwipeLabel);
            removeTextFromRightPane(mainController);

            if (mainController.rightPaneMediaView.getMediaPlayer() != null) {
                mainController.rightPaneMediaView.getMediaPlayer().stop();
                mainController.rightPaneMediaView.getMediaPlayer().dispose();
                mainController.rightPaneMediaView.setMediaPlayer(null);
            }

            mainController.imagesVBox.getChildren().clear();

            MainController.loadingTask.updateMessage("Loading File: " + filePathTreeItem.getPath().getFileName());

            mainController.fileNameLabelMediaControls.setText("Playing " + filePathTreeItem.getPath().getFileName().toString());

            System.out.println("on Thread " + Thread.currentThread().getName());

            switch (fileType) {

                case "image":
                    mainController.initMediaPlayerBindings("image");
                    addToView(mainController.mediaStackPane);
                    addToView(mainController.imagesVBox);

                    mainController.imagesVBox.getChildren().add(mainController.rightPaneImageView);
                    mainController.rightPaneImageView.setImage(image);
                    mainController.rightPaneImageView.setEffect(new Reflection());

                    break;

                case "video":

                    try {

                        displayAudioUI(mainController);
                        addToView(mainController.rightPaneMediaView);
                        addToView(mainController.volumeAndCurrentTimeSwipeLabel);

                        mainController.rightPaneMediaView.setMediaPlayer(mainController.mediaPlayer);
                        mainController.initMediaPlayerBindings("video");

                        setupSlider(mainController);
                        checkForAutoPlay(mainController, fileInfo);
                        mainController.mediaPlayer.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "music":

                    displayAudioUI(mainController);
                    addToView(mainController.rightPaneImageView);
                    addToView(mainController.volumeAndCurrentTimeSwipeLabel);
                    addToView(mainController.imagesVBox);

                    mainController.rightPaneImageView.setImage(image);

                    mainController.imagesVBox.getChildren().add(mainController.rightPaneImageView);

                    System.out.println(mainController.imagesVBox.getChildren());
                    System.out.println(mainController.rightPaneImageView.getImage());

                    mainController.rightPaneMediaView.setMediaPlayer(mainController.mediaPlayer);

                    setupSlider(mainController);
                    mainController.initMediaPlayerBindings("music");

                    checkForAutoPlay(mainController, fileInfo);
                    mainController.mediaPlayer.play();
                    break;
                case "word":

                    mainController.textLabel.setText("Content: ");
                    mainController.textContent.setText(textContent);
                    break;
                case "excel":
                    mainController.textLabel.setText("Content: ");
                    mainController.textContent.setText(textContent);
                    break;
                case "jar":

                    mainController.textLabel.setText("Content: ");
                    mainController.textContent.setText(textContent);

                    break;
                case "pdf":

                    mainController.initMediaPlayerBindings("pdf");

                    addToView(mainController.mediaStackPane);
                    addToView(mainController.imagesVBox);

                    for (int i = 0; i < imageViews.size(); i++) {
                        mainController.imagesVBox.getChildren().add(imageViews.get(i));
                    }

                    break;
                case "java":
                case "js":
                case "ruby":
                case "python":
                case "xml":
                case "html":
                case "css":
                case "text":

                    mainController.textLabel.setText("Content: ");
                    mainController.textContent.setText(textContent);

                    break;
            }
        });
    }

    public static void getImageViewsFromPDF(MainController mainController, FilePathTreeItem filePathTreeItem) {
        imageViews = new ArrayList<>();
        mainController.loadingTask.updateMessage("Rasterizing PDF: " + filePathTreeItem.getPathString());

        ArrayList<Image> images = CommonUtilities.createImageFromPDF(filePathTreeItem.getPathString());
        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(images.get(i));
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(mainController.rightPaneScrollPane.widthProperty());

            imageViews.add(imageView);
        }
    }

    public static void displayAudioUI(MainController mainController) {
        addToView(mainController.mediaStackPane);
        addToView(mainController.mediaPlayerControls);
        if (!mainController.removeSliderMediaControl.isSelected()) {
            addToView(mainController.sliderHbox);
        }
        mainController.mediaPlayerControls.setVisible(false);
    }

    public static void addToView(Node node) {
        node.setManaged(true);
        node.setVisible(true);
    }

    public static void removeFromView(Node node) {
        node.setManaged(false);
        node.setVisible(false);
    }

    private static void checkForAutoPlay(MainController mainController, FileInfo fileInfo) {
        Integer tableIndex = mainController.mainTableView.getItems().indexOf(fileInfo);

        mainController.mediaPlayer.setOnEndOfMedia(() -> {

            MainController.mediaPlayer.pause();

            if (mainController.loopButton.isSelected()) {
                MainController.mediaPlayer.stop();
                MainController.mediaPlayer.seek(Duration.ZERO);
                mainController.mediaPlayer.play();
            } else {

                if (mainController.autoplayCheckbox.isSelected()) {
                    Integer currentIndex = tableIndex;

                    while (currentIndex + 1 < mainController.mainTableView.getItems().size()) {
                        currentIndex++;
                        FileInfo nextFile = (FileInfo) mainController.mainTableView.getItems().get(currentIndex);
                        String type = FilePathTreeItem.getFileType(nextFile.getAbsolutePath());
                        if (type.equals("music") || type.equals("video")) {

                            mainController.startPlayingMedia(mainController.mainTableView.getItems().get(currentIndex), true, false);

                            break;
                        } else {
                            System.out.println(nextFile.getFileName() + " no match");
                        }
                    }
                }
            }
        });
    }

    private static void setupSlider(MainController mainController) {
        //TODO:fix loop and slider position not updating to 0

        MediaPlayer mp = mainController.mediaPlayer;
        mainController.playPositionSlider.setManaged(true);
        mainController.playPositionSlider.setVisible(true);

        mp.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                mainController.currentTimeLabel.setText(CommonUtilities.formatDuration(mp.getCurrentTime()));
                mainController.totalTimeLabel.setText(CommonUtilities.formatDuration(mp.getTotalDuration()));
                if (swipeRight) {
                    if (mainController.removeSliderMediaControl.isSelected()) {
                        mainController.volumeAndCurrentTimeSwipeLabel.setText(CommonUtilities.formatDuration(mp.getCurrentTime()) + " of " + CommonUtilities.formatDuration(mp.getTotalDuration()));
                    } else {
                        mainController.volumeAndCurrentTimeSwipeLabel.setText(CommonUtilities.formatDuration(mp.getCurrentTime()));
                    }
                }
            }
        });

        class SliderListener implements InvalidationListener {
            @Override
            public void invalidated(Observable observable) {
                Platform.runLater(() -> {
                    updatePositonSlider(mainController);
                });
            }
        }

        SliderListener sliderListener = new SliderListener();

        mp.currentTimeProperty().addListener(sliderListener);

        mainController.playPositionSlider.setOnMousePressed(e -> {
            Double xPercentage = e.getX() / mainController.playPositionSlider.getWidth();
            mp.seek(mp.getTotalDuration().multiply(xPercentage));
        });

        mainController.playPositionSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                MediaPlayer mp = mainController.rightPaneMediaView.getMediaPlayer();

                if (mp != null) {
                    if (mp.getStatus() == MediaPlayer.Status.UNKNOWN || mp.getStatus() == MediaPlayer.Status.STOPPED) {
                        mp.play();
                    }
                    if (oldValue && !newValue) {
                        double pos = mainController.playPositionSlider.getValue();
                        mp.seek(mp.getTotalDuration().multiply(pos));
                    }
                }
            }
        });
    }

    private static void updatePositonSlider(MainController mainController) {
        if (mainController.playPositionSlider.isValueChanging()) {
            return;
        }
        MediaPlayer mp = mainController.mediaPlayer;

        final Duration total = mp.getTotalDuration();
        final Duration currentTime = mp.getCurrentTime();
        if (total == null || currentTime == null) {
            mainController.playPositionSlider.setValue(0);
        } else {
            mainController.playPositionSlider.setValue(currentTime.toMillis() / total.toMillis());
        }
    }

    static void initEffectsRightPane(MainController mainController) {

        initEffects(mainController.rightPaneMediaView);
        initEffects(mainController.rightPaneImageView);
        initEffects(mainController.playPositionSlider);
        initEffects(mainController.mediaPlayerControls);
        initEffects(mainController.volumeAndCurrentTimeSwipeLabel);
    }

    private static void initEffects(Node node) {
        node.setEffect(new Reflection());
        // Tooltip.install(node, new Tooltip("Click to Play or Pause..."));
    }

    public static void removeTextFromRightPane(MainController mainController) {
        mainController.textLabel.setText("");
        mainController.textContent.setText("");
    }

    public static void copyFilesFromTableViewToDirectory(TableView mainTableView, String destinationPathString) {

        if (!destinationPathString.equals("")) {

            ServiceWindow serviceWindow = new ServiceWindow(mainTableView, destinationPathString);
            serviceWindow.setTitle("Transfer Window");
            serviceWindow.initModality(Modality.APPLICATION_MODAL);
            serviceWindow.showAndWait();
        } else {
            CommonUtilities.showErrorAlert("No Destination Directory.");
        }
    }

    public static void copyFile(FileInfo fileInfo, FileInfo destinationPath) {

        FileInfo destinationFile = new FileInfo(destinationPath.getAbsolutePath() + File.separator + fileInfo.getFileName());
        try {

            FileUtils.copyFile(fileInfo, destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatDate(Long millis) {

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ISO_DATE) + " " + localDateTime.format(DateTimeFormatter.ISO_TIME);
    }

    public static ContextMenu createContextMenu(FileInfo fileInfo, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController, String sender) {
        ContextMenu rowContextMenu = new ContextMenu();
        String name = "Open \"" + fileInfo.getFileName() + "\"";
        System.out.println(name);

        MenuItem openItem = new MenuItem(name);
        MenuItem openInEnclosingItem = new MenuItem("Open Parent Directory");
        MenuItem deleteItem = new MenuItem("Move To Trash ");
        MenuItem secureDeleteItem = new Menu("Secure Delete");
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem createNewFile = new MenuItem("Create New File");
        MenuItem copyAbsolutePathItem = new MenuItem("Copy Absolute Path");
        MenuItem sendToSourceDirectoryTextFieldItem = new MenuItem("Send to Source Text Field");
        MenuItem sendToDestinationDirectoryTextFieldItem = new MenuItem("Send to Destination Text Field");
        MenuItem showInTreeView = new MenuItem("Show in Tree View");
        MenuItem playInRightPane = new MenuItem("Play in Right Pane");

        rowContextMenu.getItems().addAll(openItem, openInEnclosingItem, deleteItem, secureDeleteItem, renameItem, copyItem, copyAbsolutePathItem, sendToSourceDirectoryTextFieldItem, sendToDestinationDirectoryTextFieldItem);

        if (sender.equals("tableView") || sender.equals("stackPane")) {

            rowContextMenu.getItems().add(showInTreeView);
        }

        rowContextMenu.getItems().addAll(playInRightPane);

        playInRightPane.setOnAction(e -> {
            if (sender.equals("tableView")) {
                mainController.startPlayingMedia(fileInfo, true, true);
            } else {
                mainController.startPlayingMediaFromTree(new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), mainController));
            }
        });

        showInTreeView.setOnAction(e -> {
            mainController.runInBackgroundThreadSecondary(() -> {
                FilePathTreeItem.selectTreeItemRecursively(mainController, Paths.get(fileInfo.getAbsolutePath()), true);
            });
        });

        if (fileInfo.isDirectory()) {
            rowContextMenu.getItems().add(createNewFile);
        }

        copyAbsolutePathItem.setOnAction(e -> {
            CommonUtilities.copyToClipboard(fileInfo.getAbsolutePath());
        });

        createNewFile.setOnAction(e -> {

            CommonUtilities.createNewFile(fileInfo, mainController);
        });

        copyItem.setOnAction(e -> {

            CommonUtilities.copyItem(fileInfo, mainTableView, files, mainController);
        });

        secureDeleteItem.setOnAction(e -> {

            CommonUtilities.secureDelete(fileInfo, mainTableView, files, mainController);
        });
        deleteItem.setOnAction(e -> {
            CommonUtilities.moveToTrash(fileInfo, mainTableView, files, mainController);
        });

        openItem.setOnAction(e -> {
            CommonUtilities.openFile(fileInfo);
        });

        openInEnclosingItem.setOnAction(e -> {

            CommonUtilities.openEnclosingDirectory(fileInfo);
        });

        renameItem.setOnAction(e -> {
            CommonUtilities.renameFile(fileInfo, mainTableView, files, mainController);
        });

        sendToSourceDirectoryTextFieldItem.setOnAction(e -> {
            mainController.directoryToSearchTextField.setText(fileInfo.getAbsolutePath());
        });

        sendToDestinationDirectoryTextFieldItem.setOnAction(e -> {
            mainController.destinationCopyAllTextField.setText(fileInfo.getAbsolutePath());
        });

        return rowContextMenu;
    }
}
