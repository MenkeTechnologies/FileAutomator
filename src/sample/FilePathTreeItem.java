package sample;

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

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FilePathTreeItem extends TreeItem<String> {
    public static Image folderCollapseImage=new Image(ClassLoader.getSystemResourceAsStream("png/folderClosed.png"));
    public static Image folderExpandImage=new Image(ClassLoader.getSystemResourceAsStream("png/folderOpen.png"));
    public static Image fileImage=new Image(ClassLoader.getSystemResourceAsStream("png/file.png"));

    private String fullPath;

    boolean isDirectory;

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

    public FilePathTreeItem(Path file){
        super(file.toString());
        this.fullPath = file.toString();

        if (Files.isDirectory(file)){
            isDirectory = true;
            setGraphic(new ImageView(folderCollapseImage));

        } else {
            setGraphic(new ImageView(fileImage));
        }

        if (!fullPath.endsWith(File.separator)){
            String value = file.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf>0){
                setValue(value.substring(indexOf+1));
            } else {
                setValue(value);
            }
        }

        try {
            populateTreeItem(this);
        } catch (IOException e) {
            System.out.println("permission error");
        }

        addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler(){
            @Override
            public void handle(Event event) {
                FilePathTreeItem source = (FilePathTreeItem)event.getSource();
                if (source.isDirectory() && source.isExpanded()){
                    ImageView iv = (ImageView)source.getGraphic();
                    iv.setImage(folderExpandImage);
                }
                
                try {
                    if (!source.getChildren().isEmpty()){
                        populateTreeItem(source);
                    } else{

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        });

        addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler(){
            @Override
            public void handle(Event event) {
                FilePathTreeItem source = (FilePathTreeItem)event.getSource();
                if (source.isDirectory() && !source.isExpanded()){
                    ImageView iv = (ImageView)source.getGraphic();
                    iv.setImage(folderCollapseImage);
                }
            }
        });


    }

    public void populateTreeItem(FilePathTreeItem source) throws java.io.IOException {
        Path path = Paths.get(source.getFullPath());
        BasicFileAttributes attribs = Files.readAttributes(path, BasicFileAttributes.class);
        if (attribs.isDirectory()) {
            DirectoryStream<Path> dir = Files.newDirectoryStream(path);
            for (Path path1 : dir) {
                FilePathTreeItem treeNode = new FilePathTreeItem(path1);
                source.getChildren().add(treeNode);
            }
        }
    }
}
