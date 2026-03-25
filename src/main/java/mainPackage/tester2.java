package mainPackage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class tester2 extends Application {
    int counter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {

        TreeView tableView = new TreeView<>();

        TreeItem<FilePathTreeItem> root = new TreeItem<>(new FilePathTreeItem(Paths.get("/Users/jacobmenke/Desktop"), null));

        tableView.setRoot(root);

        TreeItem<FilePathTreeItem> newTreeItem = new TreeItem<>(new FilePathTreeItem(Paths.get("/etc/passwd"), null));

        root.getChildren().add(newTreeItem);

        TextField directoryToSearchTextField = new TextField();

        ArrayList<String> autoCompleteDirectoriesArrayList = new ArrayList<>();

        TextFields.bindAutoCompletion(directoryToSearchTextField, param -> {
            return autoCompleteDirectoriesArrayList;
        });

        directoryToSearchTextField.setOnKeyReleased(e -> {

            if (e.getCode() == KeyCode.SLASH) {

                File possiblePath = new File(directoryToSearchTextField.getText());

                if (possiblePath.exists()) {

                    try {
                        Files.walkFileTree(Paths.get(possiblePath.getAbsolutePath()), new HashSet<FileVisitOption>(), 1, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                if (file.toFile().isDirectory()) {
                                    autoCompleteDirectoriesArrayList.add(file.toFile().getAbsolutePath());
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                                return FileVisitResult.SKIP_SUBTREE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                        });

                    } catch (Exception ex) {

                    }
                }
            }
        });

        Button b = new Button("add");

        b.setOnAction(e -> {
            autoCompleteDirectoriesArrayList.add("dogs " + Math.random() + "");
        });

        Scene scene = new Scene(new VBox(tableView, directoryToSearchTextField, b));

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    void calling() {

        System.err.println("__________Class:" + Thread.currentThread().getStackTrace()[1].getClassName() + "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                "______Called by: " + Thread.currentThread().getStackTrace()[2].getMethodName() + "_____ dogs");

        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            System.out.println(stackTraceElement);
        }
    }

    private class TreeCellFilePath extends TreeCell<FilePathTreeItem> {
        public TreeCellFilePath() {

            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {

                    System.err.println("___________Class: " + Thread.currentThread().getStackTrace()[1].getClassName() + "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                            "___ right clicked" + this.getItem().getPathString());
                }
            });
        }

        @Override
        protected void updateItem(FilePathTreeItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getPathString());
                setGraphic(new ImageView(FilePathTreeItem.dlImage));
            }
        }
    }

    public static void main(String[] args) {

        try {
            Preferences.userRoot().exportNode(System.out);
            Preferences.userRoot().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        System.err.println("__________Class:" + Thread.currentThread().getStackTrace()[1].getClassName() + "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                "______Called by: " + Thread.currentThread().getStackTrace()[2].getMethodName() + "_____");

        new tester2().calling();
    }
}
