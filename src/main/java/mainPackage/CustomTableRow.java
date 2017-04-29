package mainPackage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import java.lang.reflect.Field;

/**
 * Created by jacobmenke on 4/23/17.
 */
public class CustomTableRow extends TableRow<FileInfo> {

    public static void changeToolTipTime(Tooltip tooltip, Integer time) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");

            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);


            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(time*1000)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Tooltip tooltip = new Tooltip();


    public CustomTableRow(TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {
        CommonUtilities.formatTooltip(tooltip);


        this.setOnMouseClicked(e-> {
            if (e.getButton() == MouseButton.SECONDARY) {
                FileInfo fileInfo = (FileInfo)this.getItem();


                ContextMenu cm = Utilities.createContextMenu(fileInfo, mainController.mainTableView, mainController.files, mainController, "tableView");
                cm.show(this, e.getScreenX(), e.getScreenY());
            }
        });
    }



    @Override
    protected void updateItem(FileInfo item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null){
            setTooltip(null);
        } else {
            FileInfo fileInfo = item;

            changeToolTipTime(tooltip,3);
            tooltip.setText(fileInfo.toString());

            if (!item.isDirectory()){
                tooltip.setGraphic(new ImageView(fileInfo.getFileImage()));
            } else {

                if (FilePathTreeItem.specialDirs.containsKey(item.getAbsolutePath())) {
                    tooltip.setGraphic(new ImageView(FilePathTreeItem.specialDirs.get(item.getAbsolutePath())));

                } else {
                    tooltip.setGraphic(new ImageView(FilePathTreeItem.folderCollapseImage));
                }

            }

            setTooltip(tooltip);
        }

    }
}
