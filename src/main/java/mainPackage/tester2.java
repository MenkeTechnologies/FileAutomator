package mainPackage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import org.jpedal.examples.viewer.OpenViewerFX;
import org.jpedal.examples.viewer.SharedViewer;

import java.security.acl.Group;

/**
 * Created by jacobmenke on 4/21/17.
 */
public class tester2 extends Application{
    public static void main(String[] args) {
       System.out.println(System.getProperty("java.io.tmpdir"));
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        SplitPane root = new SplitPane();

        StackPane child = new StackPane();



        SharedViewer sharedViewer = new OpenViewerFX(child, null);

        root.getItems().add(child);
        root.getItems().add(new HTMLEditor());
        root.getItems().add(new ColorPicker());

        sharedViewer.setupViewer();

        sharedViewer.openDefaultFile("file:/Users/jacobmenke/Desktop/Master ReactJS Learn React JS from Scratch/Master ReactJS Learn React JS from Scratch/~Get Your Files Here - www.appwikia.com/Master ReactJS Learn React JS from Scratch/10 Conclusion and Code Download/001 Conclusion and Code Download.pdf");

        primaryStage.setScene(new Scene(root));
        primaryStage.show();


    }
}
