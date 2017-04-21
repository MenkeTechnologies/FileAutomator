package mainPackage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class TreeViewInitialization {
    static void initTreeView(final MainController mainController) {
        String hostName = "computer";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Image image = new Image(mainController.getClass().getResourceAsStream("/png/computer.png"));

        TreeItem<String> rootnode = new TreeItem<>(hostName, new ImageView(image));
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();

        for (Path name : rootDirectories) {
            FilePathTreeItem treeNode = new FilePathTreeItem(name, mainController);

            rootnode.getChildren().add(treeNode);

            try {
                treeNode.populateTreeItem(treeNode);
                treeNode.setExpanded(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        rootnode.setExpanded(true);

        mainController.fileBrowserTreeTable.setRoot(rootnode);

        mainController.fileBrowserTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                if (newValue instanceof FilePathTreeItem) {
                    FilePathTreeItem filePathTreeItem = (FilePathTreeItem) newValue;

                    if (filePathTreeItem.isDirectory() && !filePathTreeItem.getChildren().isEmpty()) {
                        if (mainController.automaticSearchCheckBox.isSelected()) {
                            mainController.directoryToSearchTextField.setText(filePathTreeItem.getPathString());
                            RegexUtilities.searchAndRefresh(mainController);
                        }
                    }
                }
            }
        });

        mainController.mainTableView.setRowFactory(new Callback<TableView<FileInfo>, TableRow<FileInfo>>() {
            @Override
            public TableRow<FileInfo> call(TableView<FileInfo> param) {
                TableRow<FileInfo> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {

                        FileInfo selectedItem = row.getItem();

                        ContextMenu cm = Utilities.createContextMenu(selectedItem, mainController.mainTableView, mainController.files, mainController, "tableView");
                        cm.show(row, e.getScreenX(), e.getScreenY());
                    }
                });

                return row;
            }
        });

        mainController.fileBrowserTreeTable.setCellFactory(new Callback<TreeView, TreeCell>() {
            @Override
            public TreeCell call(TreeView param) {

                return new TextFieldTreeCellImpl(mainController.fileBrowserTreeTable, mainController.mainTableView, mainController.files, mainController);

            }
        });
    }
}
