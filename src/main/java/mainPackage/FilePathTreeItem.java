package mainPackage;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FilePathTreeItem extends TreeItem<FilePathTreeItem> implements CommonFileInterface {
    public static Image folderCollapseImage = new Image(MainController.class.getResourceAsStream("/png/folderClosed.png"));
    public static Image folderExpandImage = new Image(MainController.class.getResourceAsStream("/png/folderOpen.png"));
    public static Image fileImage = new Image(MainController.class.getResourceAsStream("/png/file.png"));
    public static Image homeImage = new Image(MainController.class.getResourceAsStream("/png/home.png"));
    public static Image pictureImage = new Image(MainController.class.getResourceAsStream("/png/picture.png"));
    public static Image movieImage = new Image(MainController.class.getResourceAsStream("/png/movie.png"));
    public static Image musicImage = new Image(MainController.class.getResourceAsStream("/png/music.png"));
    public static Image musicLargeImage = new Image(MainController.class.getResourceAsStream("/png/music-large.png"));
    public static Image documentImage = new Image(MainController.class.getResourceAsStream("/png/word.png"));
    public static Image excelImage = new Image(MainController.class.getResourceAsStream("/png/excel.png"));
    public static Image pythonImage = new Image(MainController.class.getResourceAsStream("/png/py.png"));
    public static Image rubyImage = new Image(MainController.class.getResourceAsStream("/png/ruby.png"));
    public static Image javaImage = new Image(MainController.class.getResourceAsStream("/png/java.png"));
    public static Image jsImage = new Image(MainController.class.getResourceAsStream("/png/js.png"));
    public static Image htmlImage = new Image(MainController.class.getResourceAsStream("/png/html.png"));
    public static Image xmlImage = new Image(MainController.class.getResourceAsStream("/png/xml.png"));
    public static Image pdfImage = new Image(MainController.class.getResourceAsStream("/png/pdf.png"));
    public static Image cssImage = new Image(MainController.class.getResourceAsStream("/png/css.png"));
    public static Image aiImage = new Image(MainController.class.getResourceAsStream("/png/ai.png"));
    public static Image psdImage = new Image(MainController.class.getResourceAsStream("/png/psd.png"));
    public static Image txtImage = new Image(MainController.class.getResourceAsStream("/png/txt.png"));
    public static Image dlImage = new Image(MainController.class.getResourceAsStream("/png/dl.png"));
    public static Image desktopImage = new Image(MainController.class.getResourceAsStream("/png/desktop.png"));
    public static Image playingImage = new Image(MainController.class.getResourceAsStream("/png/playing.png"));
    public static Image computerImage = new Image(MainController.class.getResourceAsStream("/png/computer.png"));
    public static FilePathTreeItem oldFilePathTreeItem = null;
    public static Node oldPathGraphic = null;
    private String fullPath;
    boolean isDirectory = false;
    private String type;
    static String home = System.getProperty("user.home");
    static String downloads = System.getProperty("user.home") + File.separator + "Downloads";
    static String desktop = System.getProperty("user.home") + File.separator + "Desktop";
    static public HashMap<String, Image> specialDirs = new HashMap<>();
    private boolean isTextual;
    private boolean isHost;
    private String fileName;

    {

        FilePathTreeItem.specialDirs.put(FilePathTreeItem.home, FilePathTreeItem.homeImage);
        FilePathTreeItem.specialDirs.put(FilePathTreeItem.downloads, FilePathTreeItem.dlImage);
        FilePathTreeItem.specialDirs.put(FilePathTreeItem.desktop, FilePathTreeItem.desktopImage);
    }

    @Override
    public String toString() {
        return fullPath;
    }

    public boolean isTextual() {
        return isTextual;
    }

    public String getType() {
        return type;
    }

    public Path getPath() {
        return Paths.get(getPathString());
    }

    MainController mainController;

    public String getPathString() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public FilePathTreeItem(String fullPath, boolean isHost) {
        if (isHost) {
            this.fullPath = fullPath;
            this.fileName = fullPath;
            this.isHost = isHost;
            isDirectory = false;
            type = "host";
            isTextual = false;
            setGraphic(new ImageView(FilePathTreeItem.computerImage));
        } else {
            try {
                throw new Exception("Must Be Host for this Constructor");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isHost() {
        return isHost;
    }

    public String getFileName() {
        return fileName;
    }

    public FilePathTreeItem(Path file, MainController mainController) {
        isHost = false;

        if (file.getFileName() != null) {
            this.fileName = file.getFileName().toString();
        } else {
            this.fileName = "/";
        }
        this.fullPath = file.toString();
        this.mainController = mainController;

        if (Files.isDirectory(file)) {
            isDirectory = true;
            if (getPathString().equals(home)) {
                setGraphic(new ImageView(homeImage));
            } else if (getPathString().equals(downloads)) {

                setGraphic(new ImageView(dlImage));
            } else if (getPathString().equals(desktop)) {

                setGraphic(new ImageView(desktopImage));
            } else {

                setGraphic(new ImageView(folderCollapseImage));
            }
            type = "directory";
        } else {
            String pathName = getPathString().toLowerCase();

            type = FileTypeUtilities.getFileType(pathName);

            isTextual = FileTypeUtilities.determineIfTextual(type);

            setGraphic(new ImageView(FilePathTreeItem.getImageFromType(type)));
        }

        setValue(this);

        addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
            @Override
            public void handle(Event event) {
                FilePathTreeItem source = (FilePathTreeItem) event.getSource();
                if (source.isDirectory() && source.isExpanded()) {

                    boolean isSpecial = false;
                    for (int i = 0; i < specialDirs.size(); i++) {

                        if (specialDirs.containsKey(source.getPathString())) {
                            isSpecial = true;
                        }
                    }

                    if (!isSpecial) {
                        ImageView iv = (ImageView) source.getGraphic();
                        iv.setImage(folderExpandImage);
                    }
                }

                populateSourceAndImmediateChildrenSameThread(source);
            }
        });

        addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
            @Override
            public void handle(Event event) {
                FilePathTreeItem source = (FilePathTreeItem) event.getSource();
                if (source.isDirectory() && !source.isExpanded()) {

                    boolean isSpecial = false;
                    for (int i = 0; i < specialDirs.size(); i++) {
                        if (specialDirs.containsKey(source.getPathString())) {
                            isSpecial = true;
                        }
                    }

                    if (!isSpecial) {

                        ImageView iv = (ImageView) source.getGraphic();
                        iv.setImage(folderCollapseImage);
                    }
                }
            }
        });

    }

    public void populateSourceAndImmediateChildren(FilePathTreeItem source) {

        try {
            source.getChildren().clear();
//            System.out.println("Childredn b4 clear: " + source.getChildren());

            Platform.runLater(() -> {
                try {
                    populateTreeItem(source);
//                    System.out.println("Childredn: " + source.getChildren());
                    source.getChildren().forEach(item -> {
                        try {
                            populateTreeItem((FilePathTreeItem) item);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateSourceAndImmediateChildrenSameThread(FilePathTreeItem source) {

        try {
            source.getChildren().clear();
            try {
                populateTreeItem(source);
                source.getChildren().forEach(item -> {
                    try {
                        populateTreeItem((FilePathTreeItem) item);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(FilePathTreeItem source) {

        try {
            if (!source.isExpanded()) {
                source.getChildren().clear();
            }
            try {
                if (!source.isExpanded()) {
                    populateTreeItem(source);
                    source.getChildren().forEach(item -> {
                        try {
                            populateTreeItem((FilePathTreeItem) item);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateTreeItem(FilePathTreeItem source) throws java.io.IOException {
        Path path = Paths.get(source.getPathString());
        BasicFileAttributes attribs = Files.readAttributes(path, BasicFileAttributes.class);
        if (attribs.isDirectory()) {
            DirectoryStream<Path> dir = Files.newDirectoryStream(path);
            for (Path path1 : dir) {

                if (path1.toFile().isHidden()) {
                    if (mainController.showHiddenFilesCheckBox.isSelected()) {
                        FilePathTreeItem treeNode = new FilePathTreeItem(path1, mainController);
                        source.getChildren().add(treeNode);
                    }
                } else {
                    FilePathTreeItem treeNode = new FilePathTreeItem(path1, mainController);
                    source.getChildren().add(treeNode);
                }
            }
        }
    }

    public void recurseAndSelectTreeItems(Iterator pathIterator, boolean checkForExpanded, MainController mainController, boolean select) {
        Path nextPath = (Path) pathIterator.next();

        for (TreeItem<FilePathTreeItem> child : getChildren()) {


            String treePathName = child.getValue().getFileName().replace("/", "");

            if (treePathName.equals(nextPath.toString())) {

                FilePathTreeItem filePathTreeItem1 = (FilePathTreeItem) child;
                child.setExpanded(true);

                if (!checkForExpanded) {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThread(filePathTreeItem1);
                } else {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem1);
                }



                if (pathIterator.hasNext()) {

                    filePathTreeItem1.recurseAndSelectTreeItems(pathIterator, checkForExpanded, mainController, select);
                    break;
                } else {


                    if (select) {

                        Platform.runLater(() -> {
                            mainController.fileBrowserTreeTable.getSelectionModel().select(child);
                            mainController.fileBrowserTreeTable.scrollTo(mainController.fileBrowserTreeTable.getSelectionModel().getSelectedIndex() - 10);
                        });
                    } else {

                        if (oldFilePathTreeItem != null && oldPathGraphic != null) {

                            oldFilePathTreeItem.setGraphic(oldPathGraphic);
                        }

                        oldPathGraphic = child.getGraphic();

                        oldFilePathTreeItem = (FilePathTreeItem) child;

                        if (mainController.showPlayingIconTreeCheckbox.isSelected()) {
                            child.setGraphic(new ImageView(FilePathTreeItem.playingImage));
                        } else {
                            child.setGraphic(oldPathGraphic);
                        }
                        mainController.fileBrowserTreeTable.refresh();
                    }
                    break;
                }
            } else {
            }
        }
    }


    static void selectTreeItemRecursively(MainController mainController, Path path, boolean checkForExpanded) {
        Platform.runLater(() -> {
            mainController.fileBrowserTreeTable.getRoot().setExpanded(true);

            mainController.root = (TreeItem) mainController.fileBrowserTreeTable.getRoot().getChildren().get(0);
            mainController.root.setExpanded(true);


            FilePathTreeItem filePathTreeItem = (FilePathTreeItem) mainController.root;

            if (!checkForExpanded) {
                mainController.root.getChildren().clear();
                filePathTreeItem.populateSourceAndImmediateChildrenSameThread(filePathTreeItem);
            } else {
                filePathTreeItem.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem);
            }
            filePathTreeItem.recurseAndSelectTreeItems(path.iterator(), checkForExpanded, mainController, true);
        });
    }

    static void selectTreeItemRecursivelyAndChangeGraphic(MainController mainController, Path path, boolean checkForExpanded) {

        Platform.runLater(() -> {

            mainController.fileBrowserTreeTable.getRoot().setExpanded(true);

            mainController.root = (TreeItem) mainController.fileBrowserTreeTable.getRoot().getChildren().get(0);
            mainController.root.setExpanded(true);

            FilePathTreeItem filePathTreeItem = (FilePathTreeItem) mainController.root;

            if (!checkForExpanded) {
                mainController.root.getChildren().clear();
                filePathTreeItem.populateSourceAndImmediateChildrenSameThread(filePathTreeItem);
            } else {
                filePathTreeItem.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem);
            }
            filePathTreeItem.recurseAndSelectTreeItems(path.iterator(), checkForExpanded, mainController, false);
        });
    }

    static Image getImageFromType(String type) {
        switch (type) {
            case "music":
                return FilePathTreeItem.musicImage;

            case "image":
                return FilePathTreeItem.pictureImage;

            case "video":
                return FilePathTreeItem.movieImage;
            case "word":
                return FilePathTreeItem.documentImage;
            case "excel":
                return FilePathTreeItem.excelImage;
            case "js":
                return FilePathTreeItem.jsImage;
            case "java":
            case "jar":
                return FilePathTreeItem.javaImage;
            case "css":
                return FilePathTreeItem.cssImage;
            case "html":
                return FilePathTreeItem.htmlImage;
            case "pdf":
                return FilePathTreeItem.pdfImage;
            case "ai":
                return FilePathTreeItem.aiImage;
            case "psd":
                return FilePathTreeItem.psdImage;
            case "xml":
                return FilePathTreeItem.xmlImage;
            case "ruby":
                return FilePathTreeItem.rubyImage;
            case "python":
                return FilePathTreeItem.pythonImage;
            case "text":
                return FilePathTreeItem.txtImage;
            case "file":
                return FilePathTreeItem.fileImage;
            default:
                return null;
        }
    }
}
