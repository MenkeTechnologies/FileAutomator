package mainPackage;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FilePathTreeItem extends TreeItem<String> {
    public static Image folderCollapseImage = new Image(ClassLoader.getSystemResourceAsStream("png/folderClosed.png"));
    public static Image folderExpandImage = new Image(ClassLoader.getSystemResourceAsStream("png/folderOpen.png"));
    public static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("png/file.png"));
    public static Image homeImage = new Image(ClassLoader.getSystemResourceAsStream("png/home.png"));
    public static Image pictureImage = new Image(ClassLoader.getSystemResourceAsStream("png/picture.png"));
    public static Image movieImage = new Image(ClassLoader.getSystemResourceAsStream("png/movie.png"));

    private String fullPath;
    boolean isDirectory;
    MainController mainController;

    public String getFullPath() {
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

    public FilePathTreeItem(Path file, MainController mainController) {
        super(file.toString());
        this.fullPath = file.toString();
        this.mainController = mainController;

        if (Files.isDirectory(file)) {
            isDirectory = true;

            if (getFullPath().equals(System.getProperty("user.home"))) {
                setGraphic(new ImageView(homeImage));
            } else {
                setGraphic(new ImageView(folderCollapseImage));
            }
        } else {
            String pathName = getFullPath().toLowerCase();

           if (pathName.lastIndexOf('.') > 0){
               String fileType = pathName.substring(pathName.lastIndexOf('.')+1);

               switch (fileType) {
                   case "png":
                   case "jpg":
                   case "jpeg":
                   case "svg":
                       setGraphic(new ImageView(pictureImage));
                       break;
                   case "mp4":
                   case "mov":
                       setGraphic(new ImageView(movieImage));
                       break;
                   default:
                       setGraphic(new ImageView(fileImage));
                       break;
               }
           }
        }

        if (!fullPath.endsWith(File.separator)) {
            String value = file.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf > 0) {
                setValue(value.substring(indexOf + 1));
            } else {
                setValue(value);
            }
        }

        addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
            @Override
            public void handle(Event event) {
                FilePathTreeItem source = (FilePathTreeItem) event.getSource();
                if (source.isDirectory() && source.isExpanded()) {
                    if (!source.getFullPath().equals(System.getProperty("user.home"))) {
                        ImageView iv = (ImageView) source.getGraphic();
                        iv.setImage(folderCollapseImage);
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
                    if (!source.getFullPath().equals(System.getProperty("user.home"))) {
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
            System.out.println("Childredn b4 clear: " + source.getChildren());

            Platform.runLater(() -> {
                try {
                    populateTreeItem(source);
                    System.out.println("Childredn: " + source.getChildren());
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

    public void populateTreeItem(FilePathTreeItem source) throws java.io.IOException {
        Path path = Paths.get(source.getFullPath());
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
}
