package mainPackage

import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.net.InetAddress
import java.nio.file.FileSystems

object TreeViewInitialization {
    @JvmStatic
    fun initTreeView(mainController: MainController) {
        var hostName = "computer"
        try { hostName = InetAddress.getLocalHost().hostName } catch (e: Exception) { e.printStackTrace() }

        val hostImage = Image(mainController.javaClass.getResourceAsStream("/png/computer.png"))
        val rootnode: TreeItem<Any> = TreeItem(FilePathTreeItem(hostName, true) as Any, ImageView(hostImage))

        for (name in FileSystems.getDefault().rootDirectories) {
            val treeNode = FilePathTreeItem(name, mainController)
            rootnode.children.add(treeNode as TreeItem<Any>)
            try {
                treeNode.populateTreeItem(treeNode)
                treeNode.isExpanded = true
            } catch (e: Exception) { e.printStackTrace() }
        }

        rootnode.isExpanded = true
        mainController.fileBrowserTreeTable.root = rootnode

        mainController.fileBrowserTreeTable.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue is FilePathTreeItem) {
                if (newValue.isDirectory() && newValue.children.isNotEmpty()) {
                    if (mainController.automaticSearchCheckBox.isSelected) {
                        mainController.directoryToSearchTextField.text = newValue.getPathString()
                        RegexUtilities.searchAndRefresh(mainController)
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        mainController.fileBrowserTreeTable.setCellFactory { treeView ->
            CustomTreeCell(treeView as TreeView<FilePathTreeItem>, mainController.mainTableView, mainController.files, mainController) as TreeCell<Any>
        }
    }
}
