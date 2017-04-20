package mainPackage;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Created by jacobmenke on 4/20/17.
 */
public class DraggingInit {
    public static void initDraggingBindings(MainController mainController) {


        acceptFiles(mainController.directoryToSearchTextField);
        acceptFiles(mainController.destinationCopyAllTextField);


    }

    private static void acceptFiles(Node node) {

        node.setOnDragOver(e->{
            Dragboard db = e.getDragboard();
            if (db.hasFiles() || db.hasUrl()){
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();

        });

        node.setOnDragDropped(e->{
            Dragboard db = e.getDragboard();
            String url = null;

            if (db.hasFiles()){
                url = db.getFiles().get(0).getAbsolutePath();
                ((TextField)node).setText(url);
            }

            e.setDropCompleted(true);
            e.consume();
        });
    }
}
