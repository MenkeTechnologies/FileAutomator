package windows

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import mainPackage.CommonUtilities
import mainPackage.MainController
import mainPackage.StylesheetUtilities.Modify
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths

class CSSVBox(operation: String, newStage: Stage) : VBox() {
    init {
        val saveCssFileTextField = TextField()
        saveCssFileTextField.promptText = "Directory for CSS file"
        val chooseDir = Button()
        val export = Button()
        val operationLabel = Label()

        val nameSaveCssFileTextField = TextField()
        nameSaveCssFileTextField.promptText = "Name for CSS file"
        val openCSS = Button("Open CSS File.")

        if (operation == "export") {
            chooseDir.text = "Choose an output directory to save styles."
            export.text = "Export CSS File."
            operationLabel.text = "Export CSS"
            children.addAll(operationLabel, saveCssFileTextField, nameSaveCssFileTextField, chooseDir, export)

            chooseDir.setOnAction {
                val dc = DirectoryChooser()
                val newDirToSearch = dc.showDialog(newStage)
                saveCssFileTextField.text = newDirToSearch.absolutePath
            }

            openCSS.setOnAction {
                var path = saveCssFileTextField.text + File.separator + nameSaveCssFileTextField.text
                if (saveCssFileTextField.text != "" && nameSaveCssFileTextField.text != "") {
                    if (!nameSaveCssFileTextField.text.endsWith(".css")) {
                        path += ".css"
                    }
                    try {
                        Desktop.getDesktop().open(File(path))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        CommonUtilities.showErrorAlert("Could not open CSS File at $path")
                    }
                } else {
                    CommonUtilities.showErrorAlert("Name and Directory text fields cannot be blank.")
                }
            }

            export.setOnAction {
                if (saveCssFileTextField.text != "" && nameSaveCssFileTextField.text != "") {
                    var path = saveCssFileTextField.text + File.separator + nameSaveCssFileTextField.text

                    if (!nameSaveCssFileTextField.text.endsWith(".css")) {
                        path += ".css"
                    }

                    try {
                        PrintWriter(FileOutputStream(path)).use { pw ->
                            val string = String(Files.readAllBytes(Paths.get(Modify.tempCssFile)))
                            pw.print(string)
                            MainController.loadingTask.updateMessage("Successfully saved CSS file to $path")
                        }
                    } catch (ex: Exception) {
                        CommonUtilities.showErrorAlert("Export File$path was not valid")
                        ex.printStackTrace()
                    }
                } else {
                    CommonUtilities.showErrorAlert("Name and Directory text fields cannot be blank.")
                }
            }
        } else if (operation == "import") {
            chooseDir.text = "Choose a CSS file to import styles."
            export.text = "Import CSS File."
            operationLabel.text = "Import CSS"
            children.addAll(operationLabel, nameSaveCssFileTextField, chooseDir, export)

            chooseDir.setOnAction {
                val fc = FileChooser()
                val newDirToSearch = fc.showOpenDialog(newStage)
                nameSaveCssFileTextField.text = newDirToSearch.absolutePath
            }

            openCSS.setOnAction {
                val path = nameSaveCssFileTextField.text
                try {
                    Desktop.getDesktop().open(File(path))
                } catch (ex: Exception) {
                    CommonUtilities.showErrorAlert("Could not open CSS File at $path")
                    ex.printStackTrace()
                }
            }

            export.setOnAction {
                if (nameSaveCssFileTextField.text != "") {
                    val path = nameSaveCssFileTextField.text

                    try {
                        val string = String(Files.readAllBytes(Paths.get(path)))

                        try {
                            PrintWriter(FileOutputStream(Modify.tempCssFile)).use { pw ->
                                pw.print(string)
                                MainController.loadingTask.updateMessage("Successfully imported CSS file at $path")
                                Modify.addStyleSheets(Modify.scenes, Modify.tempCssFile)
                            }
                        } catch (ex: Exception) {
                            CommonUtilities.showErrorAlert("Export File$path was not valid")
                            ex.printStackTrace()
                        }
                    } catch (e1: Exception) {
                        CommonUtilities.showErrorAlert("Could not import CSS File.")
                    }
                } else {
                    CommonUtilities.showErrorAlert("Name and Directory text fields cannot be blank.")
                }
            }
        }

        children.addAll(openCSS)
    }
}
