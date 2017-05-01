package windows;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainPackage.CommonUtilities;
import mainPackage.MainController;
import mainPackage.StylesheetUtilities.Modify;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jacobmenke on 5/1/17.
 */
public class CSSVBox extends VBox {
    public CSSVBox(String operation, Stage newStage) {

        TextField saveCssFileTextField = new TextField();

        saveCssFileTextField.setPromptText("Directory for CSS file");
        Button chooseDir = new Button();

        Button export = new Button();
        Label operationLabel = new Label();

        TextField nameSaveCssFileTextField = new TextField();
        nameSaveCssFileTextField.setPromptText("Name for CSS file");
        Button openCSS = new Button("Open CSS File.");

        if (operation.equals("export")) {

            chooseDir.setText("Choose an output directory to save styles.");
            export.setText("Export CSS File.");
            operationLabel.setText("Export CSS");
            getChildren().addAll(operationLabel, saveCssFileTextField, nameSaveCssFileTextField, chooseDir, export);
            chooseDir.setOnAction(e -> {
                DirectoryChooser dc = new DirectoryChooser();
                File newDirToSearch = dc.showDialog(newStage);
                saveCssFileTextField.setText(newDirToSearch.getAbsolutePath());
            });

            openCSS.setOnAction(e -> {
                String path = saveCssFileTextField.getText() + File.separator + nameSaveCssFileTextField.getText();
                if (!saveCssFileTextField.getText().equals("") && !nameSaveCssFileTextField.getText().equals("")) {


                    if (!nameSaveCssFileTextField.getText().endsWith(".css")) {
                        path += ".css";
                    }
                    try {

                        Desktop.getDesktop().open(new File(path));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CommonUtilities.showErrorAlert("Could not open CSS File at " + path);
                    }
                } else {
                    CommonUtilities.showErrorAlert("Name and Directory text fields cannot be blank.");

                }
            });

            export.setOnAction(e -> {
                if (!saveCssFileTextField.getText().equals("") && !nameSaveCssFileTextField.getText().equals("")) {
                    String path = saveCssFileTextField.getText() + File.separator + nameSaveCssFileTextField.getText();

                    if (!nameSaveCssFileTextField.getText().endsWith(".css")) {
                        path += ".css";
                    }

                    try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {
                        String string = new String(Files.readAllBytes(Paths.get(Modify.tempCssFile)));

                        pw.print(string);

                        MainController.loadingTask.updateMessage("Successfully saved CSS file to " + path);
                    } catch (Exception ex) {
                        CommonUtilities.showErrorAlert("Export File" + path + " was not valid");
                        ex.printStackTrace();
                    }
                } else {
                    CommonUtilities.showErrorAlert("Name and Directory text fields cannot be blank.");
                }
            });
        } else if (operation.equals("import")) {

            chooseDir.setText("Choose a CSS file to import styles.");
            export.setText("Import CSS File.");
            operationLabel.setText("Import CSS");
            getChildren().addAll(operationLabel, nameSaveCssFileTextField, chooseDir, export);

            chooseDir.setOnAction(e -> {
                FileChooser fc = new FileChooser();

                File newDirToSearch = fc.showOpenDialog(newStage);
                nameSaveCssFileTextField.setText(newDirToSearch.getAbsolutePath());
            });

            openCSS.setOnAction(e -> {
                String path = nameSaveCssFileTextField.getText();
                try {

                    Desktop.getDesktop().open(new File(path));
                } catch (Exception ex) {
                    CommonUtilities.showErrorAlert("Could not open CSS File at " + path);
                    ex.printStackTrace();
                }
            });

            export.setOnAction(e -> {
                if (!nameSaveCssFileTextField.getText().equals("")) {

                    String path = nameSaveCssFileTextField.getText();

                    String string = null;
                    try {
                        string = new String(Files.readAllBytes(Paths.get(path)));

                        try (PrintWriter pw = new PrintWriter(new FileOutputStream(Modify.tempCssFile))) {

                            pw.print(string);

                            MainController.loadingTask.updateMessage("Successfully imported CSS file at " + path);

                            Modify.addStyleSheets(Modify.scenes, Modify.tempCssFile);
                        } catch (Exception ex) {
                            CommonUtilities.showErrorAlert("Export File" + path + " was not valid");
                            ex.printStackTrace();
                        }
                    } catch (IOException e1) {
                        CommonUtilities.showErrorAlert("Could not import CSS File.");
                    }
                } else {
                    CommonUtilities.showErrorAlert("Name and Directory text fields cannot be blank.");
                }
            });
        }

        getChildren().addAll(openCSS);
    }
}
