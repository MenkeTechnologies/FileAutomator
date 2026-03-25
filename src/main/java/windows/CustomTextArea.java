package windows;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import mainPackage.CommonUtilities;
import mainPackage.MainController;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by jacobmenke on 5/2/17.
 */
public class CustomTextArea extends TextArea {
    MainController mainController;

    public CustomTextArea() {

        MenuItem save = new MenuItem("Save");

        save.setOnAction(ex -> {

            String path = mainController.pathLabelContent.getText();

            try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {

                pw.print(this.getText());

                mainController.loadingTask.updateMessage("Saved " + path + "!");
            } catch (Exception e) {
                CommonUtilities.showErrorAlert("Could not save " + path);
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(save);
        this.setContextMenu(contextMenu);
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
