package mainPackage.StylesheetUtilities;

import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.scene.Scene;
import mainPackage.MainController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobmenke on 5/1/17.
 */
public class Modify {

   public static String tempCssFile = System.getProperty("user.home") + File.separator + "modify.css";
   public static ArrayList<Scene> scenes = new ArrayList<>();

    public static void modifyStylesheet(MainController mainController, String element, String prop, boolean append, String value) {

        List<String> lines = new ArrayList<>();

        try {

            lines.addAll(Files.readAllLines(Paths.get(tempCssFile)));

            Integer startingIndex = -1;
            Integer endingIndex = 0;
            Integer propIndex = -1;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains(element)) {
                    startingIndex = i;
                    break;
                }
            }

            //doesnt contain element
            if (startingIndex == -1) {
                startingIndex = 0;
                //contains the element
            } else {
                for (int i = startingIndex; i < lines.size(); i++) {
                    if (lines.get(i).contains("}")) {
                        endingIndex = i;
                        break;
                    }
                }

                for (int i = startingIndex; i < endingIndex; i++) {
                    if (lines.get(i).contains(prop)) {
                        propIndex = i;
                        break;
                    }
                }

                if (!append) {
                    //clear out contents of selector
                    lines.subList(startingIndex + 1, endingIndex).clear();
                } else {

                    if (propIndex != -1) {
                        lines.subList(propIndex, propIndex + 1).clear();
                    }
                }
            }

            String line = "\t" + prop + ": " + value + ";";

            lines.add(startingIndex + 1, line);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        try (PrintWriter pw = new PrintWriter(new FileOutputStream(tempCssFile))) {

            for (int i = 0; i < lines.size(); i++) {
                pw.println(lines.get(i));
            }

            addStyleSheets(Modify.scenes, tempCssFile);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addStyleSheets(ArrayList<Scene> scenes, String tempCssFile) {


        for (Scene scene : scenes) {
            if (scene != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add("stylesheets/styles.css");
                URL url = null;
                try {
                    url = new File(tempCssFile).toURI().toURL();
                    scene.getStylesheets().add(url.toExternalForm());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("___________" + Thread.currentThread().getStackTrace()[1].getClassName()+ "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                "___ " + scene +" was null.");
            }

        }


    }
}
