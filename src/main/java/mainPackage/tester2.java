package mainPackage;

import com.terminalfx.AppStarter;
import com.terminalfx.TerminalBuilder;
import com.terminalfx.TerminalTab;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.applet.Applet;

public class tester2 extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {

        TerminalBuilder terminalBuilder = new TerminalBuilder();
        TerminalTab terminal = terminalBuilder.newTerminal();



        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(terminal);

        primaryStage.setScene(new Scene(tabPane));

        primaryStage.show();
    }

    public static void main(String[] args) {
        AppStarter.main(args);
    }
}