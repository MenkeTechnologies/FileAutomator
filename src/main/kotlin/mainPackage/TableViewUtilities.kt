package mainPackage

import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView

object TableViewUtilities {
    @JvmStatic
    fun initTableViewColumns(mainTableView: TableView<FileInfo>, mainController: MainController) {
        val filesColumn = TableColumn<FileInfo, String>("File Name")
        filesColumn.prefWidth = 150.0
        filesColumn.setCellFactory {
            object : TableCell<FileInfo, String>() {
                private val iconView = ImageView()

                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        graphic = null
                        return
                    }
                    text = item
                    val fileAtRow = tableRow?.item ?: return

                    val playingPath = mainController.pathLabelContent?.text ?: ""
                    if (playingPath.isNotEmpty()
                        && fileAtRow.absolutePath == playingPath
                        && mainController.showPlayingIconCheckbox.isSelected) {
                        iconView.image = FilePathTreeItem.playingImage
                    } else {
                        if (fileAtRow.isDirectory) {
                            val dirImage = FilePathTreeItem.specialDirs[fileAtRow.absolutePath]
                            iconView.image = dirImage ?: FilePathTreeItem.folderCollapseImage
                        } else {
                            iconView.image = FilePathTreeItem.getImageFromType(
                                FileTypeUtilities.getFileType(fileAtRow.absolutePath))
                        }
                    }
                    graphic = iconView
                }
            }
        }
        filesColumn.cellValueFactory = PropertyValueFactory("fileName")

        val pathColumn = TableColumn<FileInfo, String>("File Path")
        pathColumn.prefWidth = 600.0
        pathColumn.cellValueFactory = PropertyValueFactory("filePath")

        val directoryColumn = TableColumn<FileInfo, Boolean>("Directory")
        directoryColumn.cellValueFactory = PropertyValueFactory("directoryProperty")

        val fileSizeColumn = TableColumn<FileInfo, Long>("File Size")
        fileSizeColumn.cellValueFactory = PropertyValueFactory("fileSize")
        fileSizeColumn.setCellFactory {
            object : TableCell<FileInfo, Long>() {
                override fun updateItem(item: Long?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) null
                           else PortableFileUtilities.turnBytesIntoHumanReadable(item)
                }
            }
        }
        fileSizeColumn.setComparator { o1, o2 -> o1.compareTo(o2) }

        val fileTypeColumn = TableColumn<FileInfo, String>("File Type")
        fileTypeColumn.cellValueFactory = PropertyValueFactory("fileType")

        val hiddenColumn = TableColumn<FileInfo, Boolean>("Hidden")
        hiddenColumn.cellValueFactory = PropertyValueFactory("hiddenProperty")

        val dateTimeTableColumn = TableColumn<FileInfo, String>("Last Modified")
        dateTimeTableColumn.cellValueFactory = PropertyValueFactory("lastModified")

        mainTableView.columns.addAll(filesColumn, pathColumn, fileSizeColumn, dateTimeTableColumn, fileTypeColumn, hiddenColumn, directoryColumn)

        mainController.mainTableView.setRowFactory {
            CustomTableRow(mainController.mainTableView, mainController.files, mainController)
        }
    }
}
