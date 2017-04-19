package mainPackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = new File("src/main/java/mainPackage/main.fxml").toURL();

        FXMLLoader loader = new FXMLLoader(url);

        Parent root = loader.load();

        MainController mainController = loader.getController();

        primaryStage.setTitle("File Automator");
        primaryStage.setOnCloseRequest(e-> Platform.exit());

        Scene scene = new Scene(root, 1800, 1200);

        scene.getStylesheets().add("stylesheets/styles.css");
        primaryStage.setScene(scene);

        mainController.initBindings();
        Utilities.initMenuBar(mainController.menuBar, scene, primaryStage);


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
