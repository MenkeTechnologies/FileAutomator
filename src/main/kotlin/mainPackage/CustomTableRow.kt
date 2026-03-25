package mainPackage

import javafx.collections.ObservableList
import javafx.scene.control.ContextMenu
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton

class CustomTableRow(
    mainTableView: TableView<FileInfo>,
    files: ObservableList<FileInfo>,
    mainController: MainController
) : TableRow<FileInfo>() {
    var cm: ContextMenu? = null
    private val tooltip = Tooltip()

    init {
        CommonUtilities.formatTooltip(tooltip)
        styleProperty().bind(CommonUtilities.tableViewColorProperty)
        setOnMouseClicked { e ->
            if (e.button == MouseButton.SECONDARY) {
                val fileInfo = item
                cm?.hide()
                cm = Utilities.createContextMenu(fileInfo, mainController.mainTableView, mainController.files, mainController, "tableView")
                cm?.show(this, e.screenX, e.screenY)
            }
        }
    }

    override fun computePrefHeight(width: Double): Double = font.size * 2.5

    override fun updateItem(item: FileInfo?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null) {
            setTooltip(null)
        } else {
            changeToolTipTime(tooltip, 3)
            tooltip.text = item.toString()
            if (!item.isDirectory) {
                tooltip.graphic = ImageView(item.getFileImage())
            } else {
                val img = FilePathTreeItem.specialDirs[item.absolutePath]
                tooltip.graphic = ImageView(img ?: FilePathTreeItem.folderCollapseImage)
            }
            setTooltip(tooltip)
        }
    }

    companion object {
        @JvmStatic
        fun changeToolTipTime(tooltip: Tooltip, time: Int) {
            tooltip.showDelay = javafx.util.Duration.seconds(time.toDouble())
        }
    }
}
