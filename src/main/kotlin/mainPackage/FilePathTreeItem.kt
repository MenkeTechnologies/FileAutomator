package mainPackage

import javafx.application.Platform
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File
import java.io.IOException
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.HashMap

class FilePathTreeItem : TreeItem<FilePathTreeItem>, CommonFileInterface {
    var isDirectory_ = false
    var mainController: MainController? = null
    private var fullPath: String
    private var type: String? = null
    private var isTextual: Boolean = false
    private var isHost: Boolean = false
    private var fileName_: String

    init {
        specialDirs[home] = homeImage
        specialDirs[downloads] = dlImage
        specialDirs[desktop] = desktopImage
    }

    constructor(fullPath: String, isHost: Boolean) : super() {
        if (isHost) {
            this.fullPath = fullPath
            this.fileName_ = fullPath
            this.isHost = isHost
            isDirectory_ = false
            type = "host"
            isTextual = false
            graphic = ImageView(computerImage)
        } else {
            this.fullPath = ""
            this.fileName_ = ""
            try {
                throw Exception("Must Be Host for this Constructor")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    constructor(file: Path, mainController: MainController?) : super() {
        isHost = false

        if (file.fileName != null) {
            this.fileName_ = file.fileName.toString()
        } else {
            this.fileName_ = "/"
        }
        this.fullPath = file.toString()
        this.mainController = mainController

        if (Files.isDirectory(file)) {
            isDirectory_ = true
            if (getPathString() == home) {
                graphic = ImageView(homeImage)
            } else if (getPathString() == downloads) {
                graphic = ImageView(dlImage)
            } else if (getPathString() == desktop) {
                graphic = ImageView(desktopImage)
            } else {
                graphic = ImageView(folderCollapseImage)
            }
            type = "directory"
        } else {
            val pathName = getPathString().lowercase()
            type = FileTypeUtilities.getFileType(pathName)
            isTextual = FileTypeUtilities.determineIfTextual(type!!)
            graphic = ImageView(getImageFromType(type!!))
        }

        value = this

        @Suppress("UNCHECKED_CAST")
        addEventHandler(TreeItem.branchExpandedEvent<FilePathTreeItem>() as javafx.event.EventType<TreeItem.TreeModificationEvent<FilePathTreeItem>>,
            EventHandler { event ->
                val source = event.source as FilePathTreeItem
                if (source.isDirectory() && source.isExpanded) {
                    if (!specialDirs.containsKey(source.getPathString())) {
                        val iv = source.graphic as ImageView
                        iv.image = folderExpandImage
                    }
                }
                populateSourceAndImmediateChildrenSameThread(source)
            }
        )

        @Suppress("UNCHECKED_CAST")
        addEventHandler(TreeItem.branchCollapsedEvent<FilePathTreeItem>() as javafx.event.EventType<TreeItem.TreeModificationEvent<FilePathTreeItem>>,
            EventHandler { event ->
                val source = event.source as FilePathTreeItem
                if (source.isDirectory() && !source.isExpanded) {
                    if (!specialDirs.containsKey(source.getPathString())) {
                        val iv = source.graphic as ImageView
                        iv.image = folderCollapseImage
                    }
                }
            }
        )
    }

    override fun toString(): String = fullPath

    fun isTextual(): Boolean = isTextual

    fun getType(): String = type ?: "file"

    fun getPath(): Path = Paths.get(getPathString())

    fun getPathString(): String = fullPath

    fun setFullPath(fullPath: String) {
        this.fullPath = fullPath
    }

    fun isDirectory(): Boolean = isDirectory_

    fun setDirectory(directory: Boolean) {
        isDirectory_ = directory
    }

    fun isHost(): Boolean = isHost

    fun getFileName(): String = fileName_

    fun populateSourceAndImmediateChildren(source: FilePathTreeItem) {
        try {
            source.children.clear()
            Platform.runLater {
                try {
                    populateTreeItem(source)
                    source.children.forEach { item ->
                        try {
                            populateTreeItem(item as FilePathTreeItem)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun populateSourceAndImmediateChildrenSameThread(source: FilePathTreeItem) {
        try {
            source.children.clear()
            try {
                populateTreeItem(source)
                source.children.forEach { item ->
                    try {
                        populateTreeItem(item as FilePathTreeItem)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(source: FilePathTreeItem) {
        try {
            if (!source.isExpanded) {
                source.children.clear()
            }
            try {
                if (!source.isExpanded) {
                    populateTreeItem(source)
                    source.children.forEach { item ->
                        try {
                            populateTreeItem(item as FilePathTreeItem)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun populateTreeItem(source: FilePathTreeItem) {
        val path = Paths.get(source.getPathString())
        val attribs = Files.readAttributes(path, BasicFileAttributes::class.java)
        if (attribs.isDirectory) {
            val dir: DirectoryStream<Path> = Files.newDirectoryStream(path)
            for (path1 in dir) {
                if (path1.toFile().isHidden) {
                    if (mainController != null && mainController!!.showHiddenFilesCheckBox.isSelected) {
                        val treeNode = FilePathTreeItem(path1, mainController)
                        source.children.add(treeNode)
                    }
                } else {
                    val treeNode = FilePathTreeItem(path1, mainController)
                    source.children.add(treeNode)
                }
            }
        }
    }

    fun recurseAndSelectTreeItems(pathIterator: Iterator<Path>, checkForExpanded: Boolean, mainController: MainController, select: Boolean) {
        val nextPath = pathIterator.next()

        for (child in children) {
            val treePathName = child.value.getFileName().replace("/", "")

            if (treePathName == nextPath.toString()) {
                val filePathTreeItem1 = child as FilePathTreeItem
                child.isExpanded = true

                if (!checkForExpanded) {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThread(filePathTreeItem1)
                } else {
                    filePathTreeItem1.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem1)
                }

                if (pathIterator.hasNext()) {
                    filePathTreeItem1.recurseAndSelectTreeItems(pathIterator, checkForExpanded, mainController, select)
                    break
                } else {
                    if (select) {
                        Platform.runLater {
                            @Suppress("UNCHECKED_CAST")
                            (mainController.fileBrowserTreeTable.selectionModel as javafx.scene.control.MultipleSelectionModel<TreeItem<Any>>).select(child as TreeItem<Any>)
                            mainController.fileBrowserTreeTable.scrollTo(mainController.fileBrowserTreeTable.selectionModel.selectedIndex - 10)
                        }
                    } else {
                        if (oldFilePathTreeItem != null && oldPathGraphic != null) {
                            oldFilePathTreeItem!!.graphic = oldPathGraphic
                        }

                        oldPathGraphic = child.graphic
                        oldFilePathTreeItem = child as FilePathTreeItem

                        if (mainController.showPlayingIconTreeCheckbox.isSelected) {
                            child.graphic = ImageView(playingImage)
                        } else {
                            child.graphic = oldPathGraphic
                        }
                        mainController.fileBrowserTreeTable.refresh()
                    }
                    break
                }
            }
        }
    }

    companion object {
        @JvmField
        var folderCollapseImage: Image = Image(MainController::class.java.getResourceAsStream("/png/folderClosed.png"))
        @JvmField
        var folderExpandImage: Image = Image(MainController::class.java.getResourceAsStream("/png/folderOpen.png"))
        @JvmField
        var fileImage: Image = Image(MainController::class.java.getResourceAsStream("/png/file.png"))
        @JvmField
        var homeImage: Image = Image(MainController::class.java.getResourceAsStream("/png/home.png"))
        @JvmField
        var pictureImage: Image = Image(MainController::class.java.getResourceAsStream("/png/picture.png"))
        @JvmField
        var movieImage: Image = Image(MainController::class.java.getResourceAsStream("/png/movie.png"))
        @JvmField
        var musicImage: Image = Image(MainController::class.java.getResourceAsStream("/png/music.png"))
        @JvmField
        var musicLargeImage: Image = Image(MainController::class.java.getResourceAsStream("/png/music-large.png"))
        @JvmField
        var documentImage: Image = Image(MainController::class.java.getResourceAsStream("/png/word.png"))
        @JvmField
        var excelImage: Image = Image(MainController::class.java.getResourceAsStream("/png/excel.png"))
        @JvmField
        var pythonImage: Image = Image(MainController::class.java.getResourceAsStream("/png/py.png"))
        @JvmField
        var rubyImage: Image = Image(MainController::class.java.getResourceAsStream("/png/ruby.png"))
        @JvmField
        var javaImage: Image = Image(MainController::class.java.getResourceAsStream("/png/java.png"))
        @JvmField
        var jsImage: Image = Image(MainController::class.java.getResourceAsStream("/png/js.png"))
        @JvmField
        var htmlImage: Image = Image(MainController::class.java.getResourceAsStream("/png/html.png"))
        @JvmField
        var xmlImage: Image = Image(MainController::class.java.getResourceAsStream("/png/xml.png"))
        @JvmField
        var pdfImage: Image = Image(MainController::class.java.getResourceAsStream("/png/pdf.png"))
        @JvmField
        var cssImage: Image = Image(MainController::class.java.getResourceAsStream("/png/css.png"))
        @JvmField
        var aiImage: Image = Image(MainController::class.java.getResourceAsStream("/png/ai.png"))
        @JvmField
        var psdImage: Image = Image(MainController::class.java.getResourceAsStream("/png/psd.png"))
        @JvmField
        var txtImage: Image = Image(MainController::class.java.getResourceAsStream("/png/txt.png"))
        @JvmField
        var dlImage: Image = Image(MainController::class.java.getResourceAsStream("/png/dl.png"))
        @JvmField
        var desktopImage: Image = Image(MainController::class.java.getResourceAsStream("/png/desktop.png"))
        @JvmField
        var playingImage: Image = Image(MainController::class.java.getResourceAsStream("/png/playing.png"))
        @JvmField
        var computerImage: Image = Image(MainController::class.java.getResourceAsStream("/png/computer.png"))

        @JvmField
        var oldFilePathTreeItem: FilePathTreeItem? = null
        @JvmField
        var oldPathGraphic: Node? = null
        @JvmField
        val specialDirs: HashMap<String, Image> = HashMap()

        @JvmStatic
        val home: String = System.getProperty("user.home")
        @JvmStatic
        val downloads: String = System.getProperty("user.home") + File.separator + "Downloads"
        @JvmStatic
        val desktop: String = System.getProperty("user.home") + File.separator + "Desktop"

        @JvmStatic
        fun selectTreeItemRecursively(mainController: MainController, path: Path, checkForExpanded: Boolean) {
            Platform.runLater {
                mainController.fileBrowserTreeTable.root.isExpanded = true
                val rootItem = mainController.fileBrowserTreeTable.root.children[0]
                mainController.root = rootItem
                rootItem.isExpanded = true

                val filePathTreeItem = rootItem as FilePathTreeItem

                if (!checkForExpanded) {
                    rootItem.children.clear()
                    filePathTreeItem.populateSourceAndImmediateChildrenSameThread(filePathTreeItem)
                } else {
                    filePathTreeItem.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem)
                }
                filePathTreeItem.recurseAndSelectTreeItems(path.iterator(), checkForExpanded, mainController, true)
            }
        }

        @JvmStatic
        fun selectTreeItemRecursivelyAndChangeGraphic(mainController: MainController, path: Path, checkForExpanded: Boolean) {
            Platform.runLater {
                mainController.fileBrowserTreeTable.root.isExpanded = true
                val rootItem = mainController.fileBrowserTreeTable.root.children[0]
                mainController.root = rootItem
                rootItem.isExpanded = true

                val filePathTreeItem = rootItem as FilePathTreeItem

                if (!checkForExpanded) {
                    rootItem.children.clear()
                    filePathTreeItem.populateSourceAndImmediateChildrenSameThread(filePathTreeItem)
                } else {
                    filePathTreeItem.populateSourceAndImmediateChildrenSameThreadCheckingForExpanded(filePathTreeItem)
                }
                filePathTreeItem.recurseAndSelectTreeItems(path.iterator(), checkForExpanded, mainController, false)
            }
        }

        @JvmStatic
        fun getImageFromType(type: String): Image? {
            return when (type) {
                "music" -> musicImage
                "image" -> pictureImage
                "video" -> movieImage
                "word" -> documentImage
                "excel" -> excelImage
                "js" -> jsImage
                "java", "jar" -> javaImage
                "css" -> cssImage
                "html" -> htmlImage
                "pdf" -> pdfImage
                "ai" -> aiImage
                "psd" -> psdImage
                "xml" -> xmlImage
                "ruby" -> rubyImage
                "python" -> pythonImage
                "text" -> txtImage
                "file" -> fileImage
                else -> null
            }
        }
    }
}
