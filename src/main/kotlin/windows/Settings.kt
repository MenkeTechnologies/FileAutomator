package windows

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import mainPackage.CommonUtilities
import mainPackage.MainController
import mainPackage.StylesheetUtilities.Modify
import mainPackage.Utilities
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.prefs.Preferences

class Settings {
    companion object {
        @JvmStatic
        fun initMenuBar(mainController: MainController, scene: Scene, stage: Stage) {
            val file = mainController.menuBar.menus[0]

            val oldScene = mainController.menuBar.scene
            val group = HBox()

            val cleanUpButton = Button("Clean up")
            cleanUpButton.setOnAction {
                try {
                    Files.walk(Paths.get(System.getProperty("java.io.tmpdir"))).forEach { path ->
                        path.toFile().delete()
                    }
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
                MainController.loadingTask.updateMessage("Cleaning old resources.")
            }

            val menuBar1 = MenuBar()
            menuBar1.menus.addAll(mainController.menuBar.menus)
            menuBar1.isUseSystemMenuBar = true

            val fontSizeLabel = Label("Font Size:")
            val textSizeTextField = TextField("12")

            val fontFamilyListView = ListView<String>()

            val bp = scene.root as BorderPane
            fontFamilyListView.items.addAll(Font.getFamilies())

            fontFamilyListView.id = "fontFamilyListView"
            textSizeTextField.id = "textSizeTextField"

            group.styleProperty().bind(bp.styleProperty())
            Utilities.mainStyleProp.bind(bp.styleProperty())

            val textSizeVBox = VBox(fontFamilyListView, fontSizeLabel, textSizeTextField)

            val backgroundColorPicker = ColorPicker()
            backgroundColorPicker.id = "backgroundColorPicker"
            val textColorPicker = ColorPicker()
            textColorPicker.id = "textColorPicker"
            val treeColorPicker = ColorPicker()
            treeColorPicker.id = "treeColorPicker"
            val tableColorPicker = ColorPicker()
            tableColorPicker.id = "tableColorPicker"
            val rightScrollPaneBackroundColorPicker = ColorPicker()
            rightScrollPaneBackroundColorPicker.id = "rightScrollPaneBackroundColorPicker"
            val accentColorPicker = ColorPicker()

            val tableColorVBox = VBox()
            val tableBackgroundColorLabel = Label("Choose A Background Table Color")

            val tableEH = EventHandler<ActionEvent> {
                val fill = tableColorPicker.value
                val webColor = fill.toString().replace("0x", "#").substring(0, 7)
                CommonUtilities.tableViewColorProperty.set("-fx-control-inner-background:$webColor")
                mainController.mainTableView.style = "-fx-control-inner-background: $webColor"
            }

            tableColorPicker.onAction = tableEH
            tableColorVBox.children.addAll(tableBackgroundColorLabel, tableColorPicker)

            val rightScrollPaneColorVBox = VBox()
            val rightScrollPaneColorLabel = Label("Choose A Background Scroll Pane Color")

            val scrollPaneEH = EventHandler<ActionEvent> {
                val fill = rightScrollPaneBackroundColorPicker.value
                val webColor = fill.toString().replace("0x", "#").substring(0, 7)
                mainController.rightPaneScrollPane.style = "-fx-background-color: $webColor"
            }

            rightScrollPaneBackroundColorPicker.onAction = scrollPaneEH

            fontFamilyListView.selectionModel.selectedItemProperty().addListener { _, _, _ ->
                changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, tableEH, scrollPaneEH)
            }

            val treeColorVBox = VBox()
            val treeBackgroundColorLabel = Label("Choose A Background Tree Color")

            val treeColorEH = EventHandler<ActionEvent> {
                val fill = treeColorPicker.value
                val webColor = fill.toString().replace("0x", "#").substring(0, 7)
                CommonUtilities.treeViewColorProperty.set("-fx-control-inner-background: $webColor")
                mainController.fileBrowserTreeTable.style = "-fx-control-inner-background: $webColor"
            }

            treeColorPicker.onAction = treeColorEH
            treeColorVBox.children.addAll(treeBackgroundColorLabel, treeColorPicker)

            textSizeTextField.setOnAction {
                changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH)
            }

            val backgroundColorVBox = VBox()
            val backgroundColorLabel = Label("Choose An Background Color")

            backgroundColorPicker.setOnAction {
                bp.style = "-fx-base: ${CommonUtilities.toWebColor(backgroundColorPicker.value)}"
            }

            backgroundColorVBox.children.addAll(backgroundColorLabel, backgroundColorPicker)

            val accentColorVBox = VBox()
            val accentColorLabel = Label("Choose An Accent Color")

            accentColorPicker.setOnAction {
                Modify.modifyStylesheet(mainController, "*", "-fx-base", false, CommonUtilities.toWebColor(accentColorPicker.value))
            }

            accentColorVBox.children.addAll(accentColorLabel, accentColorPicker)

            val textColorVBox = VBox()
            val textColorLabel = Label("Choose A Text Color")

            textColorPicker.setOnAction {
                changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH)
            }

            textColorVBox.children.addAll(textColorLabel, textColorPicker)

            rightScrollPaneColorVBox.children.addAll(rightScrollPaneColorLabel, rightScrollPaneBackroundColorPicker)

            val save = Button("Save Settings")
            val resetToBase = Button("Reset To Base")

            val rowSelectedColorLabel = Label("Select The Row Caret Color")
            val rowSelectedColorPicker = ColorPicker()

            rowSelectedColorPicker.setOnAction {
                bp.style = "-fx-selection-bar: ${CommonUtilities.toWebColor(rowSelectedColorPicker.value)}"
            }

            val rowSelectedColorVBox = VBox()
            rowSelectedColorVBox.children.addAll(rowSelectedColorLabel, rowSelectedColorPicker)

            val rowUnfocusedColorLabel = Label("Select the Row Caret Unfocused Color")
            val rowUnfocusedColorColorPicker = ColorPicker()

            rowUnfocusedColorColorPicker.setOnAction {
                bp.style = "-fx-selection-bar-non-focused: ${CommonUtilities.toWebColor(rowUnfocusedColorColorPicker.value)}"
            }

            val rowUnfocusedColorVBox = VBox()
            rowUnfocusedColorVBox.children.addAll(rowUnfocusedColorLabel, rowUnfocusedColorColorPicker)

            val rowHoveredColorLabel = Label("Select the Row Caret Hovered Color")
            val rowHoveredColorPicker = ColorPicker()

            rowHoveredColorPicker.setOnAction {
                val tempCssFile = System.getProperty("java.io.tmpdir") + "modify.css"

                try {
                    PrintWriter(FileOutputStream(tempCssFile)).use { pw ->
                        pw.println(
                            "\n" +
                                    ".table-row-cell:hover {\n" +
                                    "    -fx-background: ${CommonUtilities.toWebColor(rowHoveredColorPicker.value)};\n" +
                                    "}"
                        )

                        mainController.mainTableView.scene.stylesheets.clear()
                        mainController.mainTableView.scene.stylesheets.add("stylesheets/styles.css")

                        val url = File(tempCssFile).toURI().toURL()
                        mainController.mainTableView.scene.stylesheets.add(url.toExternalForm())
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            val rowHoveredColorVBox = VBox()
            rowHoveredColorVBox.children.addAll(rowHoveredColorLabel, rowHoveredColorPicker)

            val colorFlowPane = FlowPane()
            colorFlowPane.children.addAll(
                backgroundColorVBox, accentColorVBox, textColorVBox, treeColorVBox, tableColorVBox, rightScrollPaneColorVBox,
                rowSelectedColorVBox, rowUnfocusedColorVBox, rowHoveredColorVBox, HBox(save, resetToBase, cleanUpButton)
            )

            group.children.addAll(menuBar1, textSizeVBox, colorFlowPane)
            val newStage = Stage()

            val exportVBox = CSSVBox("export", newStage)
            val importVBox = CSSVBox("import", newStage)

            group.children.addAll(exportVBox, importVBox)

            newStage.initModality(Modality.APPLICATION_MODAL)

            val newScene = Scene(group)

            Modify.scenes.add(newScene)

            newStage.scene = newScene

            changeStyles(textSizeTextField, bp, backgroundColorPicker, fontFamilyListView, textColorPicker, tableEH, scrollPaneEH, treeColorEH)

            save.setOnAction {
                MainController.loadingTask.updateMessage("Saved Settings!")
                savePrefs(mainController, textSizeTextField, fontFamilyListView, backgroundColorPicker, textColorPicker, treeColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, newScene)
            }

            resetToBase.setOnAction {
                val style = "-fx-base: ${CommonUtilities.toWebColor(backgroundColorPicker.value)}"
                bp.style = style
                CommonUtilities.tableViewColorProperty.set("-fx-control-inner-background: transparent")
                CommonUtilities.treeViewColorProperty.set("-fx-control-inner-background: transparent")
                mainController.rightPaneScrollPane.style = "$style; -fx-background-color: transparent"
                mainController.fileBrowserTreeTable.style = "-fx-control-inner-background: transparent"
            }

            newStage.setOnCloseRequest {
                MainController.loadingTask.updateMessage("Saved Settings!")
                savePrefs(mainController, textSizeTextField, fontFamilyListView, backgroundColorPicker, textColorPicker, treeColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, newScene)
            }

            val root = scene.root

            file.items[0].setOnAction {
                Platform.exit()
            }

            file.items[1].setOnAction {
                newStage.showAndWait()
            }
        }

        @JvmStatic
        fun savePrefs(mainController: MainController, textSizeTextField: TextField, fontFamilyListView: ListView<String>, backgroundColorPicker: ColorPicker, textColorPicker: ColorPicker, treeColorPicker: ColorPicker, tableColorPicker: ColorPicker, rightScrollPaneBackroundColorPicker: ColorPicker, newScene: Scene) {
//            savePreferences(newScene, backgroundColorPicker, treeColorPicker, textColorPicker, tableColorPicker, rightScrollPaneBackroundColorPicker, textSizeTextField, fontFamilyListView, mainController.directoryToSearchTextField, mainController.mainTextField)
        }

        private fun savePreferences(newScene: Scene, vararg nodes: Node) {
            for (node in nodes) {
                val id = node.id
                val prop: String? = when (node) {
                    is ColorPicker -> {
                        val value = node.value.toString()
                        node.value = Color.valueOf(value)
                        value
                    }
                    is TextField -> node.text
                    is ListView<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        (node as ListView<String>).selectionModel.selectedItem
                    }
                    else -> null
                }

                if (prop != null) {
                    Preferences.userRoot().put(id, prop)
                }
            }
        }

        private fun loadPreferences(scene: Scene, vararg nodes: Node) {
            for (node in nodes) {
                val id = node.id
                val prop = Preferences.userRoot().get(node.id, null)

                if (prop != null) {
                    when (node) {
                        is ColorPicker -> node.value = Color.valueOf(prop)
                        is TextField -> node.text = prop
                        is ListView<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            (node as ListView<String>).selectionModel.select(prop)
                        }
                        else -> {}
                    }
                }
            }
        }

        @JvmStatic
        fun changeStyles(textSizeTextField: TextField, bp: BorderPane, backgroundColorPicker: ColorPicker, fontFamilyListView: ListView<String>, textColorPicker: ColorPicker, tableEH: EventHandler<ActionEvent>, scrollPaneEH: EventHandler<ActionEvent>, treeColorEH: EventHandler<ActionEvent>) {
            try {
                val size = Integer.parseInt(textSizeTextField.text)
                bp.style = Utilities.getStringBuilderStyle(backgroundColorPicker, fontFamilyListView, textSizeTextField, textColorPicker)
                tableEH.handle(null)
                scrollPaneEH.handle(null)
                treeColorEH.handle(null)
            } catch (e: Exception) {
                CommonUtilities.showErrorAlert("Font Size was not Valid.")
            }
        }
    }
}
