package sample;

import com.apple.eio.FileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class MainController implements Initializable {
    public TableView mainTableView;
    public TextField mainTextField;
    public TextField directoryToSearchTextField;
    public SplitPane mainSplitPane;
    public Label sizeLabel;
    public Label lastModifiedLabel;
    public Label pathLabel;
    public TextField destinationCopyAllTextField;
    public Label fileNamDetailLabel;
    public CheckBox caseInsensitiveMatchingCheckbox;
    public TreeView fileBrowserTreeTable;
    ObservableList<FileInfo> files = FXCollections.observableArrayList();

    public void searchForFile(ActionEvent actionEvent) {
        searchAndRefresh();
    }

    public void searchAndRefresh() {

        String fileToSearch = mainTextField.getText();
        String directory = directoryToSearchTextField.getText();
        files.clear();

        findFilesWithRegex(fileToSearch, directory);

        mainTableView.refresh();
    }

    public void findFilesWithRegex(String fileToSearch, String directory) {
        try {
            Files.walk(Paths.get(directory)).forEach(file -> {

                String fileName = file.toAbsolutePath().getFileName().toString();

                if (!fileToSearch.equals("")) {
                    Pattern pattern;

                    if (caseInsensitiveMatchingCheckbox.isSelected()) {
                        pattern = Pattern.compile(fileToSearch, Pattern.CASE_INSENSITIVE);
                    } else {
                        pattern = Pattern.compile(fileToSearch);
                    }

                    if (pattern.matcher(fileName).find()) {

                        files.add(new FileInfo(file.toString()));
                    }
                } else {

                    files.add(new FileInfo(file.toString()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainTableView.getColumns().clear();

        initTreeTable();

        mainSplitPane.setDividerPositions(0.1, 0.9);

        mainTableView.setEditable(false);

        mainTableView.setItems(files);

        TableColumn<FileInfo, String> filesColumn = new TableColumn<>("File Name");
        filesColumn.setPrefWidth(150);
        filesColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fileName"));

        TableColumn<FileInfo, String> pathColumn = new TableColumn<>("File Path");
        pathColumn.setPrefWidth(600);
        pathColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("filePath"));

        TableColumn<FileInfo, Boolean> directoryColumn = new TableColumn<>("Directory");
        directoryColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Boolean>("directoryProperty"));

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("File Size");
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Long>("fileSize"));

        mainTableView.getColumns().addAll(filesColumn, pathColumn, directoryColumn, fileSizeColumn);

        mainTableView.setRowFactory(new Callback<TableView<FileInfo>, TableRow<FileInfo>>() {
            @Override
            public TableRow<FileInfo> call(TableView<FileInfo> param) {
                TableRow<FileInfo> row = new TableRow<>();
                row.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {

                        FileInfo selectedItem = param.getSelectionModel().getSelectedItem();

                        System.out.println("here");
                        ContextMenu cm = Utilities.createContextMenu(selectedItem, mainTableView, files, MainController.this);
                        cm.show(row, e.getScreenX(), e.getScreenY());
                    }
                });

                return row;
            }
        });

        mainTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                FileInfo fileInfo = (FileInfo) newValue;
                if (fileInfo != null) {
                    fileNamDetailLabel.setText("File: " + fileInfo.getFileName());
                    sizeLabel.setText("Size: " + fileInfo.length() / 1000 + "kB");
                    pathLabel.setText("Path: " + fileInfo.getAbsolutePath());
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(fileInfo.lastModified()), ZoneId.systemDefault());

                    lastModifiedLabel.setText("Last Modified: " + localDateTime);
                }
            }
        });

        directoryToSearchTextField.setText("/Users/jacobmenke/Desktop");
    }

    private void initTreeTable() {
        String hostName = "computer";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Image image = new Image("png/computer.png");

        TreeItem<String> rootnode = new TreeItem<>(hostName, new ImageView(image));
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();

        for (Path name : rootDirectories) {
            FilePathTreeItem treeNode = new FilePathTreeItem(Paths.get("/Users/jacobmenke"));

            rootnode.getChildren().add(treeNode);

//            try {
//                treeNode.populateTreeItem(treeNode);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        rootnode.setExpanded(true);

        fileBrowserTreeTable.setRoot(rootnode);
    }

    public void chooseDir(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        File newDirToSearch = fc.showOpenDialog(mainTableView.getScene().getWindow());
        directoryToSearchTextField.setText(newDirToSearch.getParent());
    }
}
