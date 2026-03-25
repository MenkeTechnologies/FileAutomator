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
                    private final ImageView iconView = new ImageView();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }

                        setText(item);

                        FileInfo fileAtRow = (FileInfo) this.getTableRow().getItem();
                        if (fileAtRow == null) return;

                        String playingPath = mainController.pathLabelContent != null
                                ? mainController.pathLabelContent.getText() : "";

                        if (!playingPath.isEmpty()
                                && fileAtRow.getAbsolutePath().equals(playingPath)
                                && mainController.showPlayingIconCheckbox.isSelected()) {
                            iconView.setImage(FilePathTreeItem.playingImage);
                        } else {
                            setDefaultImage(fileAtRow);
                        }
                        setGraphic(iconView);
                    }

                    private void setDefaultImage(FileInfo fileAtRow) {
                        if (fileAtRow.isDirectory()) {
                            Image dirImage = FilePathTreeItem.specialDirs.get(fileAtRow.getAbsolutePath());
                            iconView.setImage(dirImage != null ? dirImage : FilePathTreeItem.folderCollapseImage);
                        } else {
                            iconView.setImage(FilePathTreeItem.getImageFromType(
                                    FileTypeUtilities.getFileType(fileAtRow.getAbsolutePath())));
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
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
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
