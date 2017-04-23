package mainPackage;

import com.terminalfx.AppStarter;
import com.terminalfx.TerminalBuilder;
import com.terminalfx.TerminalTab;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.applet.Applet;
import java.nio.file.Paths;

public class tester2 extends Application{

    private class TreeCellFilePath extends TreeCell<FilePathTreeItem>{
        public TreeCellFilePath() {

            this.setOnMouseClicked(e->{
                if (e.getButton() == MouseButton.SECONDARY){
                    System.err.println("___________" + Thread.currentThread().getStackTrace()[1].getClassName()+ "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                    "___ right clicked" + this.getItem().getPathString());
                }
            });
        }

        @Override
        protected void updateItem(FilePathTreeItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty){
                setText(null);
                setGraphic(null);

            } else{
                setText(item.getPathString());
                setGraphic(new ImageView(FilePathTreeItem.dlImage));
            }

        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        TreeView tableView = new TreeView<>();

        TreeItem<FilePathTreeItem> root = new TreeItem<>(new FilePathTreeItem(Paths.get("/Users/jacobmenke/Desktop"), null));

        tableView.setRoot(root);

        TreeItem<FilePathTreeItem> newTreeItem = new TreeItem<>(new FilePathTreeItem(Paths.get("/etc/passwd"), null));

        root.getChildren().add(newTreeItem);

        tableView.setCellFactory(new Callback<TreeView<FilePathTreeItem>, TreeCell<FilePathTreeItem>>() {
            @Override
            public TreeCell<FilePathTreeItem> call(TreeView<FilePathTreeItem> param) {
                return new TreeCellFilePath();
            }
        });


        Scene scene = new Scene(tableView);

        primaryStage.setScene(scene);




        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}