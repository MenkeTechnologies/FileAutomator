package mainPackage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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
    public CheckBox automaticSearchCheckBox;
    public CheckBox showHiddenFilesCheckBox;
    public CheckBox selectInTreeViewCheckBox;
    ObservableList<FileInfo> files = FXCollections.observableArrayList();
    TreeItem root;
    boolean out = false;

    public void searchForFile(ActionEvent actionEvent) {
        searchAndRefresh();
    }

    public void searchAndRefresh() {

        Platform.runLater(() -> {
            String fileToSearch = mainTextField.getText();
            String directory = directoryToSearchTextField.getText();
            files.clear();

            findFilesWithRegex(fileToSearch, directory);

            mainTableView.refresh();
        });
    }

    public void findFilesWithRegex(String fileToSearch, String directory) {
        try {
            Files.walk(Paths.get(directory)).forEach(file -> {

                String fileName = file.toAbsolutePath().getFileName().toString();

                if (!fileToSearch.equals("")) {
                    Pattern pattern;

                    StringTokenizer st = new StringTokenizer(fileToSearch);

                    StringBuilder sb = new StringBuilder();

                    while (st.hasMoreTokens()) {
                        String next = st.nextToken();

                        sb.append(".*").append(next);
                    }

                    String regexString = sb.toString();
                    System.out.println("REGEX: " + regexString);

                    if (caseInsensitiveMatchingCheckbox.isSelected()) {
                        pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
                    } else {
                        pattern = Pattern.compile(regexString);
                    }

                    if (pattern.matcher(fileName).find()) {

                        checkToShowHiddenFiles(file);
                    }
                } else {

                    checkToShowHiddenFiles(file);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkToShowHiddenFiles(Path file) {
        if (file.toFile().isHidden()) {
            if (showHiddenFilesCheckBox.isSelected()) {
                files.add(new FileInfo(file.toString()));
            }
        } else {
            files.add(new FileInfo(file.toString()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mainTableView.getColumns().clear();

        initTreeTable();

        showHiddenFilesCheckBox.setOnAction(e -> {
            searchAndRefresh();
        });

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

        TableColumn<FileInfo, Boolean> hiddenColumn = new TableColumn<>("Hidden");
        hiddenColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Boolean>("hiddenProperty"));

        mainTableView.getColumns().addAll(filesColumn, pathColumn, directoryColumn, fileSizeColumn, hiddenColumn);

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
                if (newValue instanceof FileInfo) {
                    FileInfo fileInfo = (FileInfo) newValue;

                    if (selectInTreeViewCheckBox.isSelected()) {

                        Platform.runLater(() -> {
                            FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), MainController.this);

                            selectTreeItemRecursively(Paths.get(fileInfo.getAbsolutePath()), false);
                        });
                    }

                    updateRightSidePane(fileInfo);
                }
            }
        });

        fileBrowserTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue instanceof FilePathTreeItem) {
                    FilePathTreeItem filePathTreeItem = (FilePathTreeItem) newValue;

                    updateRightSidePane(new FileInfo(filePathTreeItem.getFullPath()));
                }
            }
        });

        directoryToSearchTextField.setText("/Users/jacobmenke/Desktop");

        initCheckBoxes();
    }

    private void selectTreeItemRecursively(Path path, boolean checkForExpanded) {
        fileBrowserTreeTable.getRoot().setExpanded(true);

        root = (TreeItem) fileBrowserTreeTable.getRoot().getChildren().get(0);
        root.setExpanded(true);
        root.getChildren().clear();

        FilePathTreeItem filePathTreeItem = (FilePathTreeItem) root;
        if (!checkForExpanded) {
            filePathTreeItem.populateSourceAndImmediateChildrenSameThread(filePathTreeItem);
        } else {
            filePathTreeItem.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem);
        }
        recurseAndSelectTreeItems(filePathTreeItem, path.iterator(), checkForExpanded);
    }

    public void recurseAndSelectTreeItems(FilePathTreeItem filePathTreeItem, Iterator pathIterator, boolean checkForExpanded) {
        Path nextPath = (Path) pathIterator.next();
        System.out.println("next path is " + nextPath);
        System.out.println("original tree item is " + filePathTreeItem.getFullPath());
        System.out.println("children are " + filePathTreeItem.getChildren());

        for (TreeItem<String> child : filePathTreeItem.getChildren()) {
            String treePathName = child.getValue().toString().replace("/", "");

            if (treePathName.equals(nextPath.toString())) {
                System.out.println("treePathName " + treePathName + " equal " + nextPath);

                FilePathTreeItem filePathTreeItem1 = (FilePathTreeItem) child;
                child.setExpanded(true);
                if (!checkForExpanded) {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThread(filePathTreeItem1);
                } else {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem1);
                }
                if (pathIterator.hasNext()) {

                    recurseAndSelectTreeItems(filePathTreeItem1, pathIterator, checkForExpanded);
                    break;
                } else {

                    fileBrowserTreeTable.getSelectionModel().select(child);
                    fileBrowserTreeTable.scrollTo(fileBrowserTreeTable.getSelectionModel().getSelectedIndex() - 10);
                    System.out.println("Selecting..." + child);
                    break;
                }
            } else {
//                System.out.println("treePathName " + treePathName + " not equal " + nextPath.toString());
            }
        }
    }

    public void updateRightSidePane(FileInfo newValue) {
        FileInfo fileInfo = newValue;
        if (fileInfo != null) {
            fileNamDetailLabel.setText("File: " + fileInfo.getFileName());
            sizeLabel.setText("Size: " + fileInfo.length() / 1000 + "kB");
            pathLabel.setText("Path: " + fileInfo.getAbsolutePath());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(fileInfo.lastModified()), ZoneId.systemDefault());

            lastModifiedLabel.setText("Last Modified: " + Utilities.formatDate(localDateTime));
        }
    }

    private void initCheckBoxes() {

        automaticSearchCheckBox.setOnAction(e -> {

            searchAndRefresh();
        });
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
            FilePathTreeItem treeNode = new FilePathTreeItem(name, this);

            rootnode.getChildren().add(treeNode);

            try {
                treeNode.populateTreeItem(treeNode);
                treeNode.setExpanded(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        rootnode.setExpanded(true);

        fileBrowserTreeTable.setRoot(rootnode);

        fileBrowserTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                if (newValue instanceof FilePathTreeItem) {
                    FilePathTreeItem filePathTreeItem = (FilePathTreeItem) newValue;

                    if (filePathTreeItem.isDirectory() && !filePathTreeItem.getChildren().isEmpty()) {
                        if (automaticSearchCheckBox.isSelected()) {
                            directoryToSearchTextField.setText(filePathTreeItem.getFullPath());
                            searchAndRefresh();
                        }
                    }
                }
            }
        });

        fileBrowserTreeTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {

                FilePathTreeItem filePathTreeItem = (FilePathTreeItem) fileBrowserTreeTable.getSelectionModel().getSelectedItem();

                FileInfo currentFile = new FileInfo(filePathTreeItem.getFullPath());
                ContextMenu cm = Utilities.createContextMenu(currentFile, mainTableView, files, this);
                fileBrowserTreeTable.setContextMenu(cm);
//                cm.show(event, event.getScreenX(), event.getScreenY());
            }
        });
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
        Platform.runLater(() -> {
            fileBrowserTreeTable.getRoot().setExpanded(false);
            if (!fileBrowserTreeTable.getRoot().getChildren().isEmpty()) {
                fileBrowserTreeTable.getRoot().getChildren().forEach(node -> {
                    collapseNodesRecursive((TreeItem) node);
                });
            }
        });
    }

    public void findDirectoryInTree(ActionEvent actionEvent) {
        Platform.runLater(() -> {

            if (!directoryToSearchTextField.getText().equals("")) {
                FileInfo fileInfo = new FileInfo(directoryToSearchTextField.getText());

                if (fileInfo.exists()) {
                    FilePathTreeItem filePathTreeItem = new FilePathTreeItem(Paths.get(fileInfo.getAbsolutePath()), MainController.this);

                    selectTreeItemRecursively(Paths.get(fileInfo.getAbsolutePath()), false);
                } else {
                    String error = "Not A Valid File or Folder.";
                    Utilities.showErrorAlert(error);
                }
            }
        });
    }

    public void goToHomeDirectory(ActionEvent actionEvent) {

        String home = System.getProperty("user.home");

        selectTreeItemRecursively(Paths.get(home), true);
    }
}
