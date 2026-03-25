package mainPackage

import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.control.SplitPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import javafx.scene.layout.Region
import javafx.scene.paint.Color

object TileDragManager {
    private val TILE_DRAG_FORMAT = DataFormat("application/x-tile-drag")
    private var draggedNode: Node? = null
    private var sourcePaneId: String? = null
    private var sourceIndex: Int = -1

    @JvmStatic
    fun initTileDragging(mainController: MainController) {
        setupPane(mainController.topTilePane, "top", mainController)
        setupPane(mainController.bottomTilePane, "bottom", mainController)
    }

    private fun setupPane(tilePane: SplitPane, paneId: String, mainController: MainController) {
        for (i in 0 until tilePane.items.size) {
            makeDraggable(tilePane.items[i], tilePane, paneId, i, mainController)
        }
        setupDropTarget(tilePane, paneId, mainController)
    }

    private fun makeDraggable(node: Node, tilePane: SplitPane, paneId: String, index: Int, mainController: MainController) {
        node.setOnDragDetected { event ->
            val db = node.startDragAndDrop(TransferMode.MOVE)
            val content = ClipboardContent()
            content[TILE_DRAG_FORMAT] = "$paneId:$index"

            val params = SnapshotParameters()
            params.fill = Color.TRANSPARENT
            val snapshot = node.snapshot(params, null)
            db.dragView = snapshot
            db.setContent(content)

            draggedNode = node
            sourcePaneId = paneId
            sourceIndex = tilePane.items.indexOf(node)

            node.opacity = 0.4
            event.consume()
        }

        node.setOnDragDone { event ->
            node.opacity = 1.0
            draggedNode = null
            sourcePaneId = null
            sourceIndex = -1
            event.consume()
        }
    }

    private fun setupDropTarget(tilePane: SplitPane, paneId: String, mainController: MainController) {
        tilePane.setOnDragOver { event ->
            if (event.gestureSource != tilePane && draggedNode != null && event.dragboard.hasContent(TILE_DRAG_FORMAT)) {
                event.acceptTransferModes(TransferMode.MOVE)
            }
            event.consume()
        }

        tilePane.setOnDragEntered { event ->
            if (draggedNode != null && event.dragboard.hasContent(TILE_DRAG_FORMAT)) {
                tilePane.style = "-fx-border-color: #ff2a6d; -fx-border-width: 2; -fx-border-style: dashed;"
            }
            event.consume()
        }

        tilePane.setOnDragExited { event ->
            tilePane.style = ""
            event.consume()
        }

        tilePane.setOnDragDropped { event ->
            tilePane.style = ""
            val db = event.dragboard
            if (db.hasContent(TILE_DRAG_FORMAT) && draggedNode != null) {
                val node = draggedNode!!
                val sourcePane = resolvePane(sourcePaneId!!, mainController)

                val dropIndex = calculateDropIndex(tilePane, event.x, event.y)

                if (sourcePane == tilePane) {
                    val currentIndex = tilePane.items.indexOf(node)
                    if (currentIndex != dropIndex && currentIndex >= 0) {
                        tilePane.items.removeAt(currentIndex)
                        val insertAt = if (dropIndex > currentIndex) dropIndex - 1 else dropIndex
                        tilePane.items.add(insertAt.coerceIn(0, tilePane.items.size), node)
                    }
                } else {
                    sourcePane.items.remove(node)
                    tilePane.items.add(dropIndex.coerceIn(0, tilePane.items.size), node)

                    if (sourcePane.items.isEmpty()) {
                        mainController.mainSplitPane.items.remove(sourcePane)
                    }
                    if (!mainController.mainSplitPane.items.contains(tilePane)) {
                        mainController.mainSplitPane.items.add(tilePane)
                    }
                }

                refreshDragHandlers(mainController)
                event.isDropCompleted = true
            } else {
                event.isDropCompleted = false
            }
            event.consume()
        }

        for (item in tilePane.items) {
            setupItemDropTarget(item, tilePane, paneId, mainController)
        }
    }

    private fun setupItemDropTarget(item: Node, tilePane: SplitPane, paneId: String, mainController: MainController) {
        item.setOnDragOver { event ->
            if (event.gestureSource != item && draggedNode != null && event.dragboard.hasContent(TILE_DRAG_FORMAT)) {
                event.acceptTransferModes(TransferMode.MOVE)
            }
            event.consume()
        }

        item.setOnDragEntered { event ->
            if (draggedNode != null && draggedNode != item && event.dragboard.hasContent(TILE_DRAG_FORMAT)) {
                item.style = "-fx-border-color: #ff2a6d; -fx-border-width: 2;"
            }
            event.consume()
        }

        item.setOnDragExited { event ->
            item.style = ""
            event.consume()
        }

        item.setOnDragDropped { event ->
            item.style = ""
            val db = event.dragboard
            if (db.hasContent(TILE_DRAG_FORMAT) && draggedNode != null) {
                val node = draggedNode!!
                val sourcePane = resolvePane(sourcePaneId!!, mainController)
                val targetIndex = tilePane.items.indexOf(item)

                if (sourcePane == tilePane) {
                    val currentIndex = tilePane.items.indexOf(node)
                    if (currentIndex != targetIndex && currentIndex >= 0) {
                        tilePane.items.removeAt(currentIndex)
                        val insertAt = if (targetIndex > currentIndex) targetIndex else targetIndex
                        tilePane.items.add(insertAt.coerceIn(0, tilePane.items.size), node)
                    }
                } else {
                    sourcePane.items.remove(node)
                    tilePane.items.add((targetIndex + 1).coerceIn(0, tilePane.items.size), node)

                    if (sourcePane.items.isEmpty()) {
                        mainController.mainSplitPane.items.remove(sourcePane)
                    }
                    if (!mainController.mainSplitPane.items.contains(tilePane)) {
                        mainController.mainSplitPane.items.add(tilePane)
                    }
                }

                refreshDragHandlers(mainController)
                event.isDropCompleted = true
            } else {
                event.isDropCompleted = false
            }
            event.consume()
        }
    }

    private fun calculateDropIndex(tilePane: SplitPane, x: Double, y: Double): Int {
        val isHorizontal = tilePane.orientation == Orientation.HORIZONTAL
        val pos = if (isHorizontal) x else y
        val totalSize = if (isHorizontal) tilePane.width else tilePane.height
        val itemCount = tilePane.items.size

        if (itemCount == 0) return 0

        val fraction = pos / totalSize
        return (fraction * (itemCount + 1)).toInt().coerceIn(0, itemCount)
    }

    private fun resolvePane(paneId: String, mainController: MainController): SplitPane {
        return if (paneId == "top") mainController.topTilePane else mainController.bottomTilePane
    }

    fun refreshDragHandlers(mainController: MainController) {
        setupPane(mainController.topTilePane, "top", mainController)
        setupPane(mainController.bottomTilePane, "bottom", mainController)
    }
}
