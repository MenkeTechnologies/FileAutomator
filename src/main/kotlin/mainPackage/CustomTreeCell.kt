package mainPackage

import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton

class CustomTreeCell(
    fileBrowserTreeTable: TreeView<FilePathTreeItem>,
    mainTableView: TableView<FileInfo>,
    files: ObservableList<FileInfo>,
    mainController: MainController
) : TreeCell<FilePathTreeItem>() {
    var cm: ContextMenu? = null
    private val tooltip = Tooltip()

    init {
        setOnMouseClicked { e ->
            if (e.button == MouseButton.SECONDARY) {
                val filePathTreeItem = treeItem as FilePathTreeItem
                cm?.hide()
                cm = Utilities.createContextMenu(FileInfo(filePathTreeItem.getPathString()), mainTableView, files, mainController, "treeView")
                cm?.show(this, e.screenX, e.screenY)
            }
        }
    }

    override fun updateItem(item: FilePathTreeItem?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            text = null
            graphic = null
        } else {
            text = item.getFileName()
            styleProperty().bind(CommonUtilities.treeViewColorProperty)
            graphic = item.graphic
            CommonUtilities.formatTooltip(tooltip)
            CustomTableRow.changeToolTipTime(tooltip, 1)
            val fileInfo = FileInfo(item.getPathString())
            tooltip.text = fileInfo.toString()
            if (!item.isDirectory()) {
                tooltip.graphic = ImageView(fileInfo.getFileImage())
            } else {
                val img = FilePathTreeItem.specialDirs[item.getPathString()]
                tooltip.graphic = ImageView(img ?: FilePathTreeItem.folderCollapseImage)
            }
            setTooltip(tooltip)
        }
    }
}
