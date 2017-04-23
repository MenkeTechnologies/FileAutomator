package mainPackage;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.nio.file.Paths;

import static mainPackage.CustomTableRow.changeToolTipTime;

/**
 * Created by jacobmenke on 4/15/17.
 */
public class CustomTreeCell extends TreeCell {

    public CustomTreeCell(TreeView fileBrowserTreeTable, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {

        this.setOnMouseClicked(e->{
            if (e.getButton() == MouseButton.SECONDARY){

                FilePathTreeItem filePathTreeItem = (FilePathTreeItem)this.getTreeItem();

                ContextMenu cm = Utilities.createContextMenu(new FileInfo(filePathTreeItem.getPathString()), mainTableView, files, mainController, "treeView");

                cm.show(this, e.getScreenX(), e.getScreenY());
            }
        });
    }

    private Tooltip tooltip = new Tooltip();

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString());
            setGraphic(getTreeItem().getGraphic());
            changeToolTipTime(tooltip,3);

            if (item instanceof FilePathTreeItem){
                FilePathTreeItem filePathTreeItem = (FilePathTreeItem)item;
                FileInfo fileInfo = new FileInfo(filePathTreeItem.getPathString());

                tooltip.setText(fileInfo.toString());
                tooltip.setGraphic(new ImageView(fileInfo.getFileImage()));
            } else{
                tooltip.setText(item.toString());
            }

            setTooltip(tooltip);

        }
    }
}
