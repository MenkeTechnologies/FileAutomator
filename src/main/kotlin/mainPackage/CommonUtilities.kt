package mainPackage

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.util.Duration
import org.apache.commons.io.FileUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.tools.imageio.ImageIOUtil
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicLong

class CommonUtilities {
    companion object {
        @JvmField
        var index: Int = 0
        @JvmField
        val TOTAL_FILE_COUNTER = AtomicLong(0)
        @JvmField
        val MATCHING_FILE_COUNTER = AtomicLong(0)
        @JvmField
        val treeViewColorProperty: StringProperty = SimpleStringProperty()
        @JvmField
        val tableViewColorProperty: StringProperty = SimpleStringProperty()
        @JvmField
        val terminalBackgroundColor: StringProperty = SimpleStringProperty()

        @JvmStatic
        fun toWebColor(value: Color): String {
            return value.toString().replace("0x", "#").substring(0, 7)
        }

        @JvmStatic
        fun formatTooltip(tooltip: Tooltip) {
            tooltip.isWrapText = true
            tooltip.maxWidth = 1000.0
            tooltip.maxHeight = 1000.0
        }

        @JvmStatic
        fun createLineNumberingFromString(s: String, mainController: MainController): String {
            val showLineNumbers = mainController.showLineNumbersCheckbox.isSelected
            if (!showLineNumbers) return s

            val lines = s.lines()
            val sb = StringBuilder(s.length + lines.size * 6)
            for (i in lines.indices) {
                sb.append(i + 1).append('\t').append(lines[i]).append('\n')
            }
            return sb.toString()
        }

        @JvmStatic
        fun createImageFromPDF(pathString: String): ArrayList<Image>? {
            val sourceFile = File(pathString)
            val destinationFile = File("/tmp$pathString")
            val images = ArrayList<Image>()

            try {
                PDDocument.load(sourceFile).use { document ->
                    val dpi = 72
                    val pdfRenderer = PDFRenderer(document)
                    val temp = System.getProperty("java.io.tmpdir")
                    val fileName = temp + File.separator + Paths.get(destinationFile.absolutePath).fileName

                    for (i in 0 until document.numberOfPages) {
                        val addition = "-" + (i + 1) + ".png"
                        if (!File(fileName + addition).exists()) {
                            MainController.loadingTask.updateMessage("Rastering $pathString page ${i + 1} of ${document.numberOfPages}.")
                            val bim = pdfRenderer.renderImageWithDPI(i, dpi.toFloat(), ImageType.RGB)
                            ImageIOUtil.writeImage(bim, fileName + addition, dpi)
                            val image = Image("file:$fileName$addition")
                            images.add(image)
                        } else {
                            MainController.loadingTask.updateMessage("Loading ${sourceFile.name} page ${i + 1} of ${document.numberOfPages}.")
                            val image = Image("file:$fileName$addition")
                            images.add(image)
                        }
                    }
                    return images
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        @JvmStatic
        fun formatDuration(duration: Duration): String {
            val millis = duration.toMillis()
            val seconds = (millis / 1000).toInt() % 60
            val minutes = (millis / (1000 * 60)).toInt()
            return String.format("%02d:%02d", minutes, seconds)
        }

        @JvmStatic
        fun createNewFile(fileInfo: FileInfo, mainController: MainController) {
            val textInputDialog = TextInputDialog("")
            textInputDialog.headerText = "Create New File"
            textInputDialog.dialogPane.style = Utilities.mainStyleProp.value

            val result = textInputDialog.showAndWait()
            if (result.isPresent) {
                try {
                    val path = fileInfo.toString() + File.separator + result.get()
                    val newFile = File(path)
                    println(path)
                    newFile.createNewFile()
                    RegexUtilities.searchAndRefresh(mainController)
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun openFile(fileInfo: FileInfo) {
            try {
                java.awt.Desktop.getDesktop().open(fileInfo)
            } catch (e1: IOException) {
                showErrorAlert("The file could not be opened.")
                e1.printStackTrace()
            }
        }

        @JvmStatic
        fun openEnclosingDirectory(fileInfo: FileInfo) {
            try {
                java.awt.Desktop.getDesktop().open(fileInfo.parentFile)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        @JvmStatic
        fun secureDelete(fileInfo: FileInfo, mainTableView: TableView<*>, files: ObservableList<FileInfo>, mainController: MainController) {
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.dialogPane.style = Utilities.mainStyleProp.value
            alert.headerText = "Are you sure you want to delete \"${fileInfo.getFileName()}\" permanently?"

            val res = alert.showAndWait()
            if (res.get() == ButtonType.OK) {
                try {
                    invokeCommandLine("rm", "-rf", fileInfo.absolutePath)
                    if (fileInfo.isDirectory) {
                        RegexUtilities.searchAndRefresh(mainController)
                    } else {
                        files.remove(fileInfo)
                        mainTableView.refresh()
                    }
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }

        @JvmStatic
        @Throws(IOException::class)
        fun invokeCommandLine(vararg command: String) {
            val pb = ProcessBuilder()
            pb.command(*command)
            val process = pb.start()
            process.inputStream.close()
            process.outputStream.close()
            process.errorStream.close()
        }

        @JvmStatic
        fun invokeCommandLineAndReturnString(vararg command: String): String {
            val pb = ProcessBuilder()
            pb.command(*command)
            pb.redirectErrorStream(true)

            return try {
                val p = pb.start()
                val output = p.inputStream.bufferedReader().readText()
                p.waitFor()
                output
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        @JvmStatic
        fun copyItem(fileInfo: FileInfo, mainTableView: TableView<*>, files: ObservableList<FileInfo>, mainController: MainController) {
            val textInputDialog = TextInputDialog(fileInfo.absolutePath)
            textInputDialog.dialogPane.prefWidth = 600.0
            textInputDialog.contentText = "New File: "
            textInputDialog.dialogPane.style = Utilities.mainStyleProp.get()

            textInputDialog.headerTextProperty().bind(
                Bindings.concat("Copy ", PortableFileUtilities.quote(fileInfo.absolutePath), " to \"", textInputDialog.editor.textProperty(), "\"")
            )

            val result = textInputDialog.showAndWait()
            textInputDialog.dialogPane.style = Utilities.mainStyleProp.value

            if (result.isPresent) {
                try {
                    val newFileName = result.get()
                    val newFileInfo = FileInfo(newFileName)

                    if (fileInfo.isDirectory) {
                        FileUtils.copyDirectory(fileInfo, File(newFileName))
                        RegexUtilities.searchAndRefresh(mainController)
                    } else {
                        FileUtils.copyFile(fileInfo, File(newFileName))
                        index = files.indexOf(fileInfo)
                        files.add(++index, FileInfo(newFileName))
                        mainTableView.refresh()
                    }
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun moveToTrash(fileInfo: FileInfo, mainTableView: TableView<*>, files: ObservableList<FileInfo>, mainController: MainController) {
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.dialogPane.style = Utilities.mainStyleProp.value
            alert.headerText = "Are you sure you want to delete \"${fileInfo.getFileName()}\"?"

            val res = alert.showAndWait()
            if (res.get() == ButtonType.OK) {
                try {
                    java.awt.Desktop.getDesktop().moveToTrash(fileInfo)
                    if (fileInfo.isDirectory) {
                        RegexUtilities.searchAndRefresh(mainController)
                    } else {
                        files.remove(fileInfo)
                        mainTableView.refresh()
                    }
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun renameFile(fileInfo: FileInfo, mainTableView: TableView<*>, files: ObservableList<FileInfo>, mainController: MainController) {
            val dialog = TextInputDialog(fileInfo.getFileName())
            dialog.headerText = "Rename \"${fileInfo.getFileName()}\""
            dialog.dialogPane.style = Utilities.mainStyleProp.value

            val result = dialog.showAndWait()
            if (result.isPresent) {
                val path = fileInfo.parent
                val oldFile: File = fileInfo
                val fileName = result.get()
                val newFilePath = path + File.separator + fileName
                var idx = files.indexOf(fileInfo)

                files.remove(fileInfo)
                fileInfo.renameTo(File(newFilePath))

                if (oldFile.isDirectory) {
                    println("here in change dir")
                    RegexUtilities.searchAndRefresh(mainController)
                } else {
                    println("not dir")
                    val newFile = FileInfo(newFilePath)
                    files.add(idx, newFile)
                    @Suppress("UNCHECKED_CAST")
                    (mainTableView as TableView<FileInfo>).selectionModel.select(files[idx])
                    mainTableView.refresh()
                }
            }
        }

        @JvmStatic
        fun showErrorAlert(error: String) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.dialogPane.style = Utilities.mainStyleProp.value
            alert.headerText = error
            alert.showAndWait()
        }

        @JvmStatic
        fun copyToClipboard(outputText: String) {
            val stringSelection = StringSelection(outputText)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(stringSelection, stringSelection)
        }
    }
}
