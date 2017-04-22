package mainPackage;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class TableViewUtilities {
    static public void initTableViewColumns(TableView mainTableView, MainController mainController) {
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

//                            FileInfo playingFile = null;
//                            if (mainController.pathLabelContent != null && mainController.pathLabelContent.getText() != "") {
//                                playingFile = new FileInfo(mainController.pathLabelContent.getText());
//                            }

//                            if (fileAtRow != null) {

//                                if (fileAtRow.equals(playingFile) && mainController.showPlayingIconCheckbox.isSelected()) {
//                                    setGraphic(new ImageView(FilePathTreeItem.playingImage));
//                                }
//                            } else {
                            if (fileAtRow != null) {

                                if (fileAtRow.isDirectory()) {
                                    setGraphic(new ImageView(FilePathTreeItem.folderCollapseImage));
                                } else {

                                    String type = FilePathTreeItem.getFileType(fileAtRow.getAbsolutePath());

                                    System.out.println(type);
                                    Image image = FilePathTreeItem.getImageFromType(type);

                                    System.out.println(image);

                                    setGraphic(new ImageView(image));
                                }
                            }
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
                            setText(CommonUtilities.turnBytesIntoHumanReadable(item));
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

        TableColumn<FileInfo, Boolean> hiddenColumn = new TableColumn<>("Hidden");
        hiddenColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, Boolean>("hiddenProperty"));

        TableColumn<FileInfo, String> dateTimeTableColumn = new TableColumn<>("Last Modified");
        dateTimeTableColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("lastModified"));

        mainTableView.getColumns().addAll(filesColumn, pathColumn, fileSizeColumn, dateTimeTableColumn, hiddenColumn, directoryColumn);
    }
}
