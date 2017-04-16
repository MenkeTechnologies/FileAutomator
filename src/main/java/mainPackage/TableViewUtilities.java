package mainPackage;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class TableViewUtilities {
    static public void initTableViewColumns(TableView mainTableView) {
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

        TableColumn<FileInfo, String> dateTimeTableColumn = new TableColumn<>("Last Modified");
        dateTimeTableColumn.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("lastModified"));

        mainTableView.getColumns().addAll(filesColumn, pathColumn, fileSizeColumn, dateTimeTableColumn, hiddenColumn, directoryColumn);
    }
}
