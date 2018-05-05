package mainPackage;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import static mainPackage.CustomTableRow.changeToolTipTime;

/**
 * Created by jacobmenke on 4/15/17.
 */
public class CustomTreeCell extends TreeCell<FilePathTreeItem> {
    ContextMenu cm;
    private Tooltip tooltip = new Tooltip();

    public CustomTreeCell(TreeView fileBrowserTreeTable, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {

                FilePathTreeItem filePathTreeItem = (FilePathTreeItem) this.getTreeItem();
                if (cm != null) {
                    cm.hide();
                }
                cm = Utilities.createContextMenu(new FileInfo(filePathTreeItem.getPathString()), mainTableView, files, mainController, "treeView");

                cm.show(this, e.getScreenX(), e.getScreenY());
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
            setText(item.getFileName());

            styleProperty().bind(CommonUtilities.treeViewColorProperty);

            ImageView imageView = (ImageView) getTreeItem().getGraphic();

            setGraphic(item.getGraphic());

            CommonUtilities.formatTooltip(tooltip);

            changeToolTipTime(tooltip, 1);

            FileInfo fileInfo = new FileInfo(item.getPathString());

            tooltip.setText(fileInfo.toString());

            if (!item.isDirectory()) {
                tooltip.setGraphic(new ImageView(fileInfo.getFileImage()));
            } else {
                if (item.specialDirs.containsKey(item.getPathString())) {
                    tooltip.setGraphic(new ImageView(item.specialDirs.get(item.getPathString())));
                } else {
                    tooltip.setGraphic(new ImageView(FilePathTreeItem.folderCollapseImage));
                }
            }

            setTooltip(tooltip);
        }
    }
}
