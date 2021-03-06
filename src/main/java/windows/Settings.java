package windows;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainPackage.CommonUtilities;
import mainPackage.MainController;
import mainPackage.StylesheetUtilities.Modify;
import mainPackage.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Created by jacobmenke on 4/30/17.
 */
public class Settings {
    public static void initMenuBar(MainController mainController, Scene scene, Stage stage) {
        Menu file = mainController.menuBar.getMenus().get(0);

        Scene oldScene = mainController.menuBar.getScene();
        HBox group = new HBox();

        Button cleanUpButton = new Button("Clean up");
        cleanUpButton.setOnAction(e -> {
            try {
                Files.walk(Paths.get(System.getProperty("java.io.tmpdir"))).forEach(path -> {
                    path.toFile().delete();
                });
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            MainController.loadingTask.updateMessage("Cleaning old resources.");
        });

        MenuBar menuBar1 = new MenuBar();
        menuBar1.getMenus().addAll(mainController.menuBar.getMenus());

        menuBar1.setUseSystemMenuBar(true);

        Label fontSizeLabel = new Label("Font Size:");
        TextField textSizeTextField = new TextField("12");

        ListView<String> fontFamilyListView = new ListView();

        BorderPane bp = (BorderPane) scene.getRoot();
        fontFamilyListView.getItems().addAll(Font.getFamilies());

        fontFamilyListView.setId("fontFamilyListView");
        textSizeTextField.setId("textSizeTextField");

        group.styleProperty().bind(bp.styleProperty());
        Utilities.mainStyleProp.bind(bp.styleProperty());

        VBox textSizeVBox = new VBox(fontFamilyListView, fontSizeLabel, textSizeTextField);

        ColorPicker backgroundColorPicker = new ColorPicker();
        backgroundColorPicker.setId("backgroundColorPicker");
        ColorPicker textColorPicker = new ColorPicker();
        textColorPicker.setId("textColorPicker");
        ColorPicker treeColorPicker = new ColorPicker();
        treeColorPicker.setId("treeColorPicker");
        ColorPicker tableColorPicker = new ColorPicker();
        tableColorPicker.setId("tableColorPicker");
        ColorPicker rightScrollPaneBackroundColorPicker = new ColorPicker();
        rightScrollPaneBackroundColorPicker.setId("rightScrollPaneBackroundColorPicker");
        ColorPicker accentColorPicker = new ColorPicker();

        VBox tableColorVBox = new VBox();

        Label tableBackgroundColorLabel = new Label("Choose A Background Table Color");

        EventHandler<ActionEvent> tableEH = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Paint fill = tableColorPicker.getValue();
                String webColor = fill.toString().replace("0x", "#").substring(0, 7);
                CommonUtilities.tableViewColorProperty.set("-fx-control-inner-background:" + webColor);
                mainController.mainTableView.setStyle("-fx-control-inner-background: " + webColor);
            }
        };

        tableColorPicker.setOnAction(tableEH);

        tableColorVBox.getChildren().addAll(tableBackgroundColorLabel, tableColorPicker);

        VBox rightScrollPaneColorVBox = new VBox();

        Label rightScrollPaneColorLabel = new Label("Choose A Background Scroll Pane Color");

        EventHandler<ActionEvent> scrollPaneEH = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Paint fill = rightScrollPaneBackroundColorPicker.getValue();
                String webColor = fill.toString().replace("0x", "#").substring(0, 7);
                mainController.rightPaneScrollPane.setStyle("-fx-background-color: " + webColor);
            }
        };

        rightScrollPaneBackroundColorPicker.setOnAction(scrollPaneEH);

        fontFamilyListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, tableEH, scrollPaneEH);
            }
        });

        VBox treeColorVBox = new VBox();

        Label treeBackgroundColorLabel = new Label("Choose A Background Tree Color");

        EventHandler<ActionEvent> treeColorEH = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Paint fill = treeColorPicker.getValue();

                String webColor = fill.toString().replace("0x", "#").substring(0, 7);
                CommonUtilities.treeViewColorProperty.set("-fx-control-inner-background: " + webColor);
                mainController.fileBrowserTreeTable.setStyle("-fx-control-inner-background: " + webColor);
            }
        };

        treeColorPicker.setOnAction(treeColorEH);

        treeColorVBox.getChildren().addAll(treeBackgroundColorLabel, treeColorPicker);

        textSizeTextField.setOnAction(e -> {

            changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH);
        });

        VBox backgroundColorVBox = new VBox();

        Label backgroundColorLabel = new Label("Choose An Background Color");

        backgroundColorPicker.setOnAction(e -> {
            bp.setStyle("-fx-base: " + CommonUtilities.toWebColor(backgroundColorPicker.getValue()));

            //changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH);
        });

        backgroundColorVBox.getChildren().addAll(backgroundColorLabel, backgroundColorPicker);

        VBox accentColorVBox = new VBox();

        Label accentColorLabel = new Label("Choose An Accent Color");

        accentColorPicker.setOnAction(e -> {

            Modify.modifyStylesheet(mainController, "*", "-fx-base", false, CommonUtilities.toWebColor(accentColorPicker.getValue()));
        });

        accentColorVBox.getChildren().addAll(accentColorLabel, accentColorPicker);

        VBox textColorVBox = new VBox();

        Label textColorLabel = new Label("Choose A Text Color");

        textColorPicker.setOnAction(e -> {

            changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH);
        });

        textColorVBox.getChildren().addAll(textColorLabel, textColorPicker);

        rightScrollPaneColorVBox.getChildren().addAll(rightScrollPaneColorLabel, rightScrollPaneBackroundColorPicker);

        Button save = new Button("Save Settings");

        Button resetToBase = new Button("Reset To Base");

        Label rowSelectedColorLabel = new Label("Select The Row Caret Color");

        ColorPicker rowSelectedColorPicker = new ColorPicker();

        rowSelectedColorPicker.setOnAction(e -> {
            bp.setStyle("-fx-selection-bar: " + CommonUtilities.toWebColor(rowSelectedColorPicker.getValue()));
        });

        VBox rowSelectedColorVBox = new VBox();

        rowSelectedColorVBox.getChildren().addAll(rowSelectedColorLabel, rowSelectedColorPicker);

        Label rowUnfocusedColorLabel = new Label("Select the Row Caret Unfocused Color");

        ColorPicker rowUnfocusedColorColorPicker = new ColorPicker();

        rowUnfocusedColorColorPicker.setOnAction(e -> {
            bp.setStyle("-fx-selection-bar-non-focused: " + CommonUtilities.toWebColor(rowUnfocusedColorColorPicker.getValue()));
        });

        VBox rowUnfocusedColorVBox = new VBox();

        rowUnfocusedColorVBox.getChildren().addAll(rowUnfocusedColorLabel, rowUnfocusedColorColorPicker);

        Label rowHoveredColorLabel = new Label("Select the Row Caret Hovered Color");

        ColorPicker rowHoveredColorPicker = new ColorPicker();

        rowHoveredColorPicker.setOnAction(e -> {

            String tempCssFile = System.getProperty("java.io.tmpdir") + "modify.css";

            try (PrintWriter pw = new PrintWriter(new FileOutputStream(tempCssFile))) {
                pw.println("\n" +
                        ".table-row-cell:hover {\n" +
                        "    -fx-background: " + CommonUtilities.toWebColor(rowHoveredColorPicker.getValue()) + ";\n" +
                        "}");

                mainController.mainTableView.getScene().getStylesheets().clear();
                mainController.mainTableView.getScene().getStylesheets().add("stylesheets/styles.css");

                URL url = new File(tempCssFile).toURI().toURL();

                mainController.mainTableView.getScene().getStylesheets().add(url.toExternalForm());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox rowHoveredColorVBox = new VBox();

        rowHoveredColorVBox.getChildren().addAll(rowHoveredColorLabel, rowHoveredColorPicker);

        FlowPane colorFlowPane = new FlowPane();

        colorFlowPane.getChildren().addAll(backgroundColorVBox, accentColorVBox, textColorVBox, treeColorVBox, tableColorVBox, rightScrollPaneColorVBox,
                rowSelectedColorVBox, rowUnfocusedColorVBox, rowHoveredColorVBox, new HBox(save, resetToBase, cleanUpButton));

        group.getChildren().addAll(menuBar1, textSizeVBox, colorFlowPane);
        Stage newStage = new Stage();

        CSSVBox exportVBox = new CSSVBox("export", newStage);

        CSSVBox importVBox = new CSSVBox("import", newStage);

        group.getChildren().addAll(exportVBox, importVBox);

        newStage.initModality(Modality.APPLICATION_MODAL);

        Scene newScene = new Scene(group);

        Modify.scenes.add(newScene);

        newStage.setScene(newScene);

        //loadPreferences(newScene, backgroundColorPicker, treeColorPicker, textColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, textSizeTextField, fontFamilyListView, mainController.directoryToSearchTextField, mainController.mainTextField);

        changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH);

        save.setOnAction(e -> {
            MainController.loadingTask.updateMessage("Saved Settings!");
            savePrefs(mainController, textSizeTextField, fontFamilyListView, backgroundColorPicker, textColorPicker, treeColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, newScene);
        });

        resetToBase.setOnAction(e -> {
            String style = "-fx-base: " + CommonUtilities.toWebColor(backgroundColorPicker.getValue());

            bp.setStyle(style);

            CommonUtilities.tableViewColorProperty.set("-fx-control-inner-background: transparent");
            CommonUtilities.treeViewColorProperty.set("-fx-control-inner-background: transparent");
            mainController.rightPaneScrollPane.setStyle(style + "; -fx-background-color: transparent");
            mainController.fileBrowserTreeTable.setStyle("-fx-control-inner-background: transparent");
        });

        newStage.setOnCloseRequest(e -> {
            MainController.loadingTask.updateMessage("Saved Settings!");
            savePrefs(mainController, textSizeTextField, fontFamilyListView, backgroundColorPicker, textColorPicker, treeColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, newScene);
        });

        Parent root = scene.getRoot();

        file.getItems().get(0).setOnAction(e -> {
            Platform.exit();
        });

        file.getItems().get(1).setOnAction(e -> {

            newStage.showAndWait();
        });
    }

    public static void savePrefs(MainController mainController, TextField textSizeTextField, ListView<String> fontFamilyListView, ColorPicker backgroundColorPicker, ColorPicker textColorPicker, ColorPicker treeColorPicker, ColorPicker tableColorPicker, ColorPicker rightScrollPaneBackroundColorPicker, Scene newScene) {
//        savePreferences(newScene, backgroundColorPicker, treeColorPicker, textColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, textSizeTextField, fontFamilyListView, mainController.directoryToSearchTextField, mainController.mainTextField);
    }

    private static void savePreferences(Scene newScene, Node... nodes) {

        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            String id = node.getId();
            String prop = null;

            if (node instanceof ColorPicker) {
                ColorPicker cp = (ColorPicker) node;
                prop = cp.getValue().toString();
                cp.setValue(Color.valueOf(prop));
            } else if (node instanceof TextField) {
                TextField textField = (TextField) node;
                prop = textField.getText();
            } else if (node instanceof ListView) {
                ListView<String> listView = (ListView<String>) node;
                prop = listView.getSelectionModel().getSelectedItem();
            } else {
                prop = null;
            }

            Preferences.userRoot().put(id, prop);
        }
    }

    private static void loadPreferences(Scene scene, Node... nodes) {

        for (int i = 0; i < nodes.length; i++) {

            Node node = nodes[i];
            String id = node.getId();

            String prop = Preferences.userRoot().get(node.getId(), null);

            if (prop != null) {
                if (node instanceof ColorPicker) {
                    ColorPicker cp = (ColorPicker) node;
                    cp.setValue(Color.valueOf(prop));
                } else if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    textField.setText(prop);
                } else if (node instanceof ListView) {
                    ListView<String> listView = (ListView<String>) node;
                    listView.getSelectionModel().select(prop);
                }
            }
        }
    }

    public static void changeStyles(TextField textSizeTextField, BorderPane bp, ColorPicker backgroundColorPicker, ListView<String> fontFamilyListView, ColorPicker textColorPicker, EventHandler<ActionEvent> tableEH, EventHandler<ActionEvent> scrollPaneEH, EventHandler<ActionEvent> treeColorEH) {
        try {
            Integer size = Integer.parseInt(textSizeTextField.getText());

            bp.setStyle(Utilities.getStringBuilderStyle(backgroundColorPicker, fontFamilyListView, textSizeTextField, textColorPicker));

            tableEH.handle(null);
            scrollPaneEH.handle(null);
            treeColorEH.handle(null);
        } catch (Exception e) {
            CommonUtilities.showErrorAlert("Font Size was not Valid.");
        }
    }
}
