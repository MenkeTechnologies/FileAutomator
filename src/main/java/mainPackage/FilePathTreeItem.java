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
import java.util.Iterator;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FilePathTreeItem extends TreeItem<String> {
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


    private String fullPath;
    boolean isDirectory;
    private String type;

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

    public FilePathTreeItem(Path file, MainController mainController) {
        super(file.toString());
        this.fullPath = file.toString();
        this.mainController = mainController;

        if (Files.isDirectory(file)) {
            isDirectory = true;
            if (getPathString().equals(System.getProperty("user.home"))) {
                setGraphic(new ImageView(homeImage));
            } else {
                setGraphic(new ImageView(folderCollapseImage));
            }
            type = "directory";
        } else {
            String pathName = getPathString().toLowerCase();

            type = getFileType(pathName);

            switch (type) {
                case "music":
                    setGraphic(new ImageView(musicImage));
                    break;
                case "image":
                    setGraphic(new ImageView(pictureImage));
                    break;
                case "video":
                    setGraphic(new ImageView(movieImage));
                    break;
                case "word":
                    setGraphic(new ImageView(documentImage));
                    break;
                case "excel":
                    setGraphic(new ImageView(excelImage));
                    break;
                case "js":
                    setGraphic(new ImageView(jsImage));
                    break;
                case "java":
                case "jar":
                    setGraphic(new ImageView(javaImage));
                    break;
                case "css":
                    setGraphic(new ImageView(cssImage));
                    break;
                case "html":
                    setGraphic(new ImageView(htmlImage));
                    break;
                case "pdf":
                    setGraphic(new ImageView(pdfImage));
                    break;
                case "xml":
                    setGraphic(new ImageView(xmlImage));
                    break;
                case "ruby":
                    setGraphic(new ImageView(rubyImage));
                    break;
                case "python":
                    setGraphic(new ImageView(pythonImage));
                    break;
                case "text":
                case "file":
                    setGraphic(new ImageView(fileImage));
                    break;
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
                    if (!source.getPathString().equals(System.getProperty("user.home"))) {
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
                    if (!source.getPathString().equals(System.getProperty("user.home"))) {
                        ImageView iv = (ImageView) source.getGraphic();
                        iv.setImage(folderCollapseImage);
                    }
                }
            }
        });
    }

    static public String getFileType(String pathName) {
        String type = "";

        if (pathName.lastIndexOf('.') > 0) {
            String fileType = pathName.substring(pathName.lastIndexOf('.') + 1);

            switch (fileType) {
                case "png":
                case "jpg":
                case "jpeg":
                case "svg":
                    type = "image";
                    break;
                case "mp4":
                case "mov":
                    type = "video";
                    break;
                case "java":
                    type = "java";
                    break;
                case "py":
                    type = "python";
                    break;
                case "rb":
                    type = "ruby";
                    break;
                case "json":
                case "js":
                    type = "js";
                    break;
                case "fxml":
                case "xml":
                    type = "xml";
                    break;
                case "pdf":
                    type = "pdf";
                    break;
                case "html":
                    type = "html";
                    break;
                case "css":
                    type = "css";
                    break;
                case "mp3":
                case "wav":
                case "aiff":
                case "flac":
                    type = "music";
                    break;

                case "doc":
                case "docx":
                    type = "word";
                    break;
                case "xls":
                case "xlsx":
                    type = "excel";
                    break;
                case "pl":
                case "tcl":
                case "txt":
                case "c":
                case "cpp":
                case "h":
                case "swift":

                case "plist":
                case "conf":
                    type = "text";
                    break;
                case "jar":
                    type = "jar";
                    break;

                default:
                    type = "file";
                    break;
            }
        } else {
            //no file ending
            type = "file";
        }
        return type;
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

    public void recurseAndSelectTreeItems(Iterator pathIterator, boolean checkForExpanded, MainController mainController) {
        Path nextPath = (Path) pathIterator.next();
//        System.out.println("next path is " + nextPath);
//        System.out.println("original tree item is " + getPathString());
//        System.out.println("children are " + getChildren());

        for (TreeItem<String> child : getChildren()) {
            String treePathName = child.getValue().toString().replace("/", "");

            if (treePathName.equals(nextPath.toString())) {
//                System.out.println("treePathName " + treePathName + " equal " + nextPath);

                FilePathTreeItem filePathTreeItem1 = (FilePathTreeItem) child;
                child.setExpanded(true);
                if (!checkForExpanded) {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThread(filePathTreeItem1);
                } else {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem1);
                }
                if (pathIterator.hasNext()) {

                    filePathTreeItem1.recurseAndSelectTreeItems(pathIterator, checkForExpanded, mainController);
                    break;
                } else {

                    mainController.fileBrowserTreeTable.getSelectionModel().select(child);
                    mainController.fileBrowserTreeTable.scrollTo(mainController.fileBrowserTreeTable.getSelectionModel().getSelectedIndex() - 10);
//                    System.out.println("Selecting..." + child);
                    break;
                }
            } else {
//                System.out.println("treePathName " + treePathName + " not equal " + nextPath.toString());
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
            filePathTreeItem.recurseAndSelectTreeItems(path.iterator(), checkForExpanded, mainController);
        });
    }
}
