package windows

import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextArea
import mainPackage.CommonUtilities
import mainPackage.MainController
import java.io.FileOutputStream
import java.io.PrintWriter

class CustomTextArea : TextArea() {
    @JvmField
    var mainController: MainController? = null

    init {
        val save = MenuItem("Save")

        save.setOnAction {
            val path = mainController!!.pathLabelContent.text

            try {
                PrintWriter(FileOutputStream(path)).use { pw ->
                    pw.print(this.text)
                    MainController.loadingTask.updateMessage("Saved $path!")
                }
            } catch (e: Exception) {
                CommonUtilities.showErrorAlert("Could not save $path")
            }
        }

        val contextMenu = ContextMenu()
        contextMenu.items.add(save)
        this.contextMenu = contextMenu
    }
}
