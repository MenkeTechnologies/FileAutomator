package mainPackage;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.Comparator;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class TableViewUtilities {
    static public void initTableViewColumns(TableView<FileInfo> mainTableView, MainController mainController) {
        TableColumn<FileInfo, String> filesColumn = new TableColumn<>("File Name");
        filesColumn.setPrefWidth(150);
        filesColumn.setCellFactory(new Callback<TableColumn<FileInfo, String>, TableCell<FileInfo, String>>() {
            @Override
            public TableCell<FileInfo, String> call(TableColumn<FileInfo, String> param) {
                TableCell<FileInfo, String> tableCell = new TableCell<FileInfo, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);

                            FileInfo fileAtRow = (FileInfo) this.getTableRow().getItem();

                            FileInfo playingFile = null;
                            if (mainController.pathLabelContent != null && mainController.pathLabelContent.getText() != "") {
                                playingFile = new FileInfo(mainController.pathLabelContent.getText());
                            }

                            if (fileAtRow != null) {
                                if (playingFile != null) {
                                    if (fileAtRow.getAbsolutePath().equals(playingFile.getAbsolutePath()) && mainController.showPlayingIconCheckbox.isSelected()) {
                                        setGraphic(new ImageView(FilePathTreeItem.playingImage));
                                    } else {
                                        setDefaultGraphic(fileAtRow);
                                    }
                                } else {

                                    setDefaultGraphic(fileAtRow);
                                }
                            }
                        }
                    }

                    private void setDefaultGraphic(FileInfo fileAtRow) {
                        if (fileAtRow.isDirectory()) {
                            if (FilePathTreeItem.specialDirs.containsKey(fileAtRow.getAbsolutePath())) {
                                setGraphic(new ImageView(FilePathTreeItem.specialDirs.get(fileAtRow.getAbsolutePath())));
                            } else {
                                setGraphic(new ImageView(FilePathTreeItem.folderCollapseImage));
                            }
                        } else {

                            String type = FileTypeUtilities.getFileType(fileAtRow.getAbsolutePath());

                            Image image = FilePathTreeItem.getImageFromType(type);

                            setGraphic(new ImageView(image));
                        }
                    }
                };

                return tableCell;
            }
        });

        filesColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fileName"));

        TableColumn<FileInfo, String> pathColumn = new TableColumn<>("File Path");
        pathColumn.setPrefWidth(600);
        pathColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("filePath"));

        TableColumn<FileInfo, Boolean> directoryColumn = new TableColumn<>("Directory");
        directoryColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Boolean>("directoryProperty"));

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("File Size");
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Long>("fileSize"));
        fileSizeColumn.setCellFactory(new Callback<TableColumn<FileInfo, Long>, TableCell<FileInfo, Long>>() {
            @Override
            public TableCell<FileInfo, Long> call(TableColumn<FileInfo, Long> param) {

                TableCell<FileInfo, Long> cell = new TableCell<FileInfo, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        if (item != null) {
                            setText(PortableFileUtilities.turnBytesIntoHumanReadable(item));
                        }
                    }
                };
                return cell;
            }
        });

        fileSizeColumn.setComparator(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
            }
        });

        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("File Type");
        fileTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        TableColumn<FileInfo, Boolean> hiddenColumn = new TableColumn<>("Hidden");
        hiddenColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Boolean>("hiddenProperty"));

        TableColumn<FileInfo, String> dateTimeTableColumn = new TableColumn<>("Last Modified");
        dateTimeTableColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("lastModified"));

        mainTableView.getColumns().addAll(filesColumn, pathColumn, fileSizeColumn, dateTimeTableColumn, fileTypeColumn, hiddenColumn, directoryColumn);

        mainController.mainTableView.setRowFactory(new Callback<TableView<FileInfo>, TableRow<FileInfo>>() {
            @Override
            public TableRow<FileInfo> call(TableView<FileInfo> param) {
                return new CustomTableRow(mainController.mainTableView, mainController.files, mainController);
            }
        });
    }
}
