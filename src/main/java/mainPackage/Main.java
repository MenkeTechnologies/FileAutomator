package mainPackage;

import com.aquafx_project.AquaFx;
import com.guigarage.flatterfx.FlatterFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.aerofx.AeroFX;

import java.io.File;
import java.net.URL;
import java.util.prefs.Preferences;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//
//        URL url = new File("src/main/java/mainPackage/main.fxml").toURL();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Parent root = loader.load();

        MainController mainController = loader.getController();

        primaryStage.setTitle("File Automator");
        primaryStage.setOnCloseRequest(e -> {
            Preferences.userRoot().putDouble("dividerPos0", mainController.mainSplitPane.getDividerPositions()[0]);
            Preferences.userRoot().putDouble("dividerPos1", mainController.mainSplitPane.getDividerPositions()[1]);

            Platform.exit();
            System.exit(0);
        });

        Scene scene = new Scene(root, 1800, 1200);

        mainController.initBindings();
        scene.widthProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (Utilities.maximized.get()){
                    mainController.mainSplitPane.setDividerPositions(0,0);
                }
            }
        });

        Utilities.initMenuBar(mainController.menuBar, scene, primaryStage);

        Double sp = Preferences.userRoot().getDouble("dividerPos0", 0.2);
        Double sp2 = Preferences.userRoot().getDouble("dividerPos1", 0.8);

        mainController.mainSplitPane.setDividerPositions(sp, sp2);

        AquaFx.style();

        scene.getStylesheets().add("stylesheets/styles.css");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
