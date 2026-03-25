package mainPackage

import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.input.TransferMode

object DraggingInit {
    @JvmStatic
    fun initDraggingBindings(mainController: MainController) {
        acceptFiles(mainController.directoryToSearchTextField)
        acceptFiles(mainController.destinationCopyAllTextField)
    }

    private fun acceptFiles(node: Node) {
        node.setOnDragOver { e ->
            val db = e.dragboard
            if (db.hasFiles() || db.hasUrl()) {
                e.acceptTransferModes(*TransferMode.ANY)
            }
            e.consume()
        }
        node.setOnDragDropped { e ->
            val db = e.dragboard
            if (db.hasFiles()) {
                (node as TextField).text = db.files[0].absolutePath
            }
            e.isDropCompleted = true
            e.consume()
        }
    }
}
