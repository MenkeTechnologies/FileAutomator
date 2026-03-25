package mainPackage

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.effect.Reflection
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.util.Duration
import org.apache.commons.io.FileUtils
import windows.ServiceWindow
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Utilities {
    companion object {
        @JvmField
        var toggle: Boolean = false
        @JvmField
        var stopSlider: Boolean = false
        @JvmField
        var swipeRight: Boolean = false
        @JvmField
        var imageViews: ArrayList<ImageView>? = null
        @JvmField
        val maximized: BooleanProperty = SimpleBooleanProperty(false)
        @JvmField
        var textContent: String? = null
        @JvmField
        var image: Image? = null
        @JvmField
        var fromAutoPlay: Boolean = false
        @JvmField
        val mainStyleProp: StringProperty = SimpleStringProperty("")

        @JvmStatic
        fun getStringBuilderStyle(backgroundColorPicker: ColorPicker, listView: ListView<String>, textField: TextField, textColorPicker: ColorPicker): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("-fx-base: ").append(CommonUtilities.toWebColor(backgroundColorPicker.value)).append(";")

            if (listView.selectionModel.selectedItem != null) {
                stringBuilder.append("-fx-font-family: ").append(listView.selectionModel.selectedItem).append(";")
            }
            stringBuilder.append(" -fx-font-size: ").append(textField.text).append(";")
            stringBuilder.append(" -fx-mid-text-color: ").append(CommonUtilities.toWebColor(textColorPicker.value))
                .append("; -fx-light-text-color: ").append(CommonUtilities.toWebColor(textColorPicker.value))

            return stringBuilder.toString()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun isText(file: File): Boolean {
            val reader = FileReader(file)
            var data = reader.read()
            val c = CharArray(3)

            for (i in 0 until 3) {
                c[i] = data.toChar()
                data = reader.read()
            }
            reader.close()
            println()
            var type = c[0].toString()
            for (i in 1 until 2) {
                type += c[i].toString()
            }

            if (!type.matches(Regex("[_a-zA-Z0-9\\-\\.]*"))) {
                return false
            }

            return true
        }

        @JvmStatic
        fun updateThumbnailRightSidePane(mainController: MainController, filePathTreeItem: FilePathTreeItem) {
            System.gc()

            if (filePathTreeItem.getType() != "pdf") {
                MainController.loadingTask.updateMessage("Loading File: ${filePathTreeItem.getPath().fileName}")
            }

            val fileType = filePathTreeItem.getType()
            val fileInfo = FileInfo(filePathTreeItem.getPathString())

            when (fileType) {
                "image" -> image = Image("file://${filePathTreeItem.getPathString()}")
                "music" -> {
                    image = FilePathTreeItem.musicLargeImage
                    val m = Media(fileInfo.toURI().toString())
                    MainController.mediaPlayer = MediaPlayer(m)
                }
                "video" -> {
                    val m = Media(fileInfo.toURI().toString())
                    MainController.mediaPlayer = MediaPlayer(m)
                }
                "pdf" -> getImageViewsFromPDF(mainController, filePathTreeItem)
                "jar" -> textContent = CommonUtilities.createLineNumberingFromString(
                    CommonUtilities.invokeCommandLineAndReturnString("jar", "tf", filePathTreeItem.getPathString()), mainController
                )
                "excel" -> textContent = CommonUtilities.createLineNumberingFromString(
                    MicrosoftUtilities.getStringFromExcelDocument(filePathTreeItem.getPathString()), mainController
                )
                "word" -> textContent = CommonUtilities.createLineNumberingFromString(
                    MicrosoftUtilities.getStringFromWordDocument(filePathTreeItem.getPathString())!!, mainController
                )
                "java", "js", "ruby", "python", "xml", "html", "css", "text" -> {
                    try {
                        textContent = CommonUtilities.createLineNumberingFromString(
                            String(Files.readAllBytes(filePathTreeItem.getPath())), mainController
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            Platform.runLater {
                removeFromView(mainController.mediaStackPane)
                removeFromView(mainController.rightPaneMediaView)
                removeFromView(mainController.mediaPlayerControls)
                removeFromView(mainController.imagesVBox)
                removeFromView(mainController.sliderHbox)
                removeFromView(mainController.volumeAndCurrentTimeSwipeLabel)
                removeTextFromRightPane(mainController)

                if (mainController.rightPaneMediaView.mediaPlayer != null) {
                    mainController.rightPaneMediaView.mediaPlayer.stop()
                    mainController.rightPaneMediaView.mediaPlayer.dispose()
                    mainController.rightPaneMediaView.mediaPlayer = null
                }

                mainController.imagesVBox.children.clear()

                if (filePathTreeItem.getPath().fileName != null) {
                    mainController.fileNameLabelMediaControls.text = "Playing ${filePathTreeItem.getPath().fileName}"
                }

                when (fileType) {
                    "image" -> {
                        mainController.initMediaPlayerBindings("image")
                        addToView(mainController.mediaStackPane)
                        addToView(mainController.imagesVBox)
                        mainController.imagesVBox.children.add(mainController.rightPaneImageView)
                        mainController.rightPaneImageView.image = image
                    }
                    "video" -> {
                        try {
                            displayAudioUI(mainController)
                            addToView(mainController.rightPaneMediaView)
                            addToView(mainController.volumeAndCurrentTimeSwipeLabel)
                            mainController.rightPaneMediaView.mediaPlayer = MainController.mediaPlayer
                            mainController.initMediaPlayerBindings("video")
                            setupSlider(mainController)
                            checkForAutoPlay(mainController, fileInfo)
                            MainController.mediaPlayer.play()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    "music" -> {
                        displayAudioUI(mainController)
                        addToView(mainController.rightPaneImageView)
                        addToView(mainController.volumeAndCurrentTimeSwipeLabel)
                        addToView(mainController.imagesVBox)
                        mainController.rightPaneImageView.image = image
                        mainController.imagesVBox.children.add(mainController.rightPaneImageView)
                        println(mainController.imagesVBox.children)
                        println(mainController.rightPaneImageView.image)
                        mainController.rightPaneMediaView.mediaPlayer = MainController.mediaPlayer
                        setupSlider(mainController)
                        mainController.initMediaPlayerBindings("music")
                        checkForAutoPlay(mainController, fileInfo)
                        MainController.mediaPlayer.play()
                    }
                    "word", "excel", "jar" -> {
                        mainController.textLabel.text = "Content: "
                        mainController.textContent?.setText(textContent ?: "")
                    }
                    "pdf" -> {
                        mainController.initMediaPlayerBindings("pdf")
                        addToView(mainController.mediaStackPane)
                        addToView(mainController.imagesVBox)
                        for (i in 0 until imageViews!!.size) {
                            mainController.imagesVBox.children.add(imageViews!![i])
                        }
                    }
                    "java", "js", "ruby", "python", "xml", "html", "css", "text" -> {
                        mainController.textLabel.text = "Content: "
                        mainController.textContent?.setText(textContent ?: "")
                    }
                }
            }
        }

        @JvmStatic
        fun getImageViewsFromPDF(mainController: MainController, filePathTreeItem: FilePathTreeItem) {
            imageViews = ArrayList()
            MainController.loadingTask.updateMessage("Rasterizing PDF: ${filePathTreeItem.getPath().fileName}")
            val images = CommonUtilities.createImageFromPDF(filePathTreeItem.getPathString())
            if (images != null) {
                for (i in 0 until images.size) {
                    val imageView = ImageView(images[i])
                    imageView.isPreserveRatio = true
                    imageView.fitWidthProperty().bind(mainController.rightPaneScrollPane.widthProperty())
                    imageViews!!.add(imageView)
                }
            }
        }

        @JvmStatic
        fun displayAudioUI(mainController: MainController) {
            addToView(mainController.mediaStackPane)
            addToView(mainController.mediaPlayerControls)
            if (!mainController.removeSliderMediaControl.isSelected) {
                addToView(mainController.sliderHbox)
            }
            mainController.mediaPlayerControls.isVisible = false
        }

        @JvmStatic
        fun addToView(node: Node) {
            node.isManaged = true
            node.isVisible = true
        }

        @JvmStatic
        fun removeFromView(node: Node) {
            node.isManaged = false
            node.isVisible = false
        }

        private fun checkForAutoPlay(mainController: MainController, fileInfo: FileInfo) {
            MainController.mediaPlayer.setOnEndOfMedia {
                endOfMediaAction(mainController, true)
            }
        }

        @JvmStatic
        fun endOfMediaAction(mainController: MainController, forward: Boolean) {
            MainController.mediaPlayer.pause()

            if (mainController.loopButton.isSelected) {
                MainController.mediaPlayer.stop()
                MainController.mediaPlayer.seek(Duration.ZERO)
                MainController.mediaPlayer.play()
            } else {
                if (mainController.autoplayCheckbox.isSelected) {
                    val currentlyPlayingFile: File = FileInfo(mainController.pathLabelContent.text)
                    var currentIndex = 0

                    if (mainController.filesForAutoplay.indexOf(currentlyPlayingFile.absolutePath) >= 0) {
                        currentIndex = mainController.filesForAutoplay.indexOf(currentlyPlayingFile.absolutePath)

                        if (forward) {
                            while (currentIndex + 1 < mainController.filesForAutoplay.size) {
                                currentIndex++
                                val nextFile = FileInfo(mainController.filesForAutoplay[currentIndex])
                                val type = FileTypeUtilities.getFileType(nextFile.absolutePath)
                                if (type == "music" || type == "video") {
                                    fromAutoPlay = true
                                    mainController.startPlayingMedia(nextFile, true, false)
                                    break
                                } else {
                                    println("${nextFile.getFileName()} no match")
                                }
                            }
                        } else {
                            while (currentIndex > 0) {
                                currentIndex--
                                val nextFile = FileInfo(mainController.filesForAutoplay[currentIndex])
                                val type = FileTypeUtilities.getFileType(nextFile.absolutePath)
                                if (type == "music" || type == "video") {
                                    fromAutoPlay = true
                                    mainController.startPlayingMedia(nextFile, true, false)
                                    break
                                } else {
                                    println("${nextFile.getFileName()} no match")
                                }
                            }
                        }
                    } else {
                        mainController.startPlayingMedia(FileInfo(mainController.filesForAutoplay[currentIndex]), true, false)
                    }
                } else {
                    val currentlyPlayingFile: File = FileInfo(mainController.pathLabelContent.text)
                    var currentIndex = 0
                    val list: ObservableList<FileInfo> = mainController.mainTableView.items
                    for (i in 0 until list.size) {
                        val nextFile = list[i]
                        if (nextFile.absolutePath == currentlyPlayingFile.absolutePath) {
                            currentIndex = i
                        }
                    }

                    val nextFile: File = if (forward) {
                        FileInfo(mainController.mainTableView.items[++currentIndex].absolutePath)
                    } else {
                        FileInfo(mainController.mainTableView.items[--currentIndex].absolutePath)
                    }

                    mainController.startPlayingMedia(nextFile, true, false)
                }
            }
        }

        private fun setupSlider(mainController: MainController) {
            val mp = MainController.mediaPlayer
            mainController.playPositionSlider.isManaged = true
            mainController.playPositionSlider.isVisible = true

            mp.currentTimeProperty().addListener(InvalidationListener {
                mainController.currentTimeLabel.text = CommonUtilities.formatDuration(mp.currentTime)
                mainController.totalTimeLabel.text = CommonUtilities.formatDuration(mp.totalDuration)
                if (swipeRight) {
                    if (mainController.removeSliderMediaControl.isSelected) {
                        mainController.volumeAndCurrentTimeSwipeLabel.text =
                            "${CommonUtilities.formatDuration(mp.currentTime)} of ${CommonUtilities.formatDuration(mp.totalDuration)}"
                    } else {
                        mainController.volumeAndCurrentTimeSwipeLabel.text = CommonUtilities.formatDuration(mp.currentTime)
                    }
                }
            })

            val sliderListener = InvalidationListener {
                Platform.runLater {
                    updatePositionSlider(mainController)
                }
            }

            mp.currentTimeProperty().addListener(sliderListener)

            mainController.playPositionSlider.setOnMousePressed { e ->
                val xPercentage = e.x / mainController.playPositionSlider.width
                mp.seek(mp.totalDuration.multiply(xPercentage))
            }

            mainController.playPositionSlider.valueChangingProperty().addListener(object : ChangeListener<Boolean> {
                override fun changed(observable: ObservableValue<out Boolean>?, oldValue: Boolean, newValue: Boolean) {
                    val mp2 = mainController.rightPaneMediaView.mediaPlayer
                    if (mp2 != null) {
                        if (mp2.status == MediaPlayer.Status.UNKNOWN || mp2.status == MediaPlayer.Status.STOPPED) {
                            mp2.play()
                        }
                        if (oldValue && !newValue) {
                            val pos = mainController.playPositionSlider.value
                            mp2.seek(mp2.totalDuration.multiply(pos))
                        }
                    }
                }
            })
        }

        private fun updatePositionSlider(mainController: MainController) {
            if (mainController.playPositionSlider.isValueChanging) {
                return
            }
            val mp = MainController.mediaPlayer
            val total = mp.totalDuration
            val currentTime = mp.currentTime
            if (total == null || currentTime == null) {
                mainController.playPositionSlider.value = 0.0
            } else {
                mainController.playPositionSlider.value = currentTime.toMillis() / total.toMillis()
            }
        }

        @JvmStatic
        fun initEffectsRightPane(mainController: MainController) {
            if (mainController.showReflectionBottomButton.isSelected) {
                initEffects(mainController.rightPaneMediaView)
                initEffects(mainController.rightPaneImageView)
                initEffects(mainController.playPositionSlider)
                initEffects(mainController.mediaPlayerControls)
            }
        }

        @JvmStatic
        fun removeEffects(node: Node) {
            node.effect = null
        }

        @JvmStatic
        fun initEffects(node: Node) {
            node.effect = Reflection()
        }

        @JvmStatic
        fun removeTextFromRightPane(mainController: MainController) {
            mainController.textLabel.text = ""
        }

        @JvmStatic
        fun copyFilesFromTableViewToDirectory(mainTableView: TableView<*>, destinationPathString: String) {
            if (destinationPathString != "") {
                val serviceWindow = ServiceWindow(mainTableView, destinationPathString)
                serviceWindow.title = "Transfer Window"
                serviceWindow.initModality(Modality.APPLICATION_MODAL)
                serviceWindow.showAndWait()
            } else {
                CommonUtilities.showErrorAlert("No Destination Directory.")
            }
        }

        @JvmStatic
        fun copyFile(fileInfo: FileInfo, destinationPath: FileInfo) {
            val destinationFile = FileInfo(destinationPath.absolutePath + File.separator + fileInfo.getFileName())
            try {
                FileUtils.copyFile(fileInfo, destinationFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun formatDate(millis: Long): String {
            val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            return localDateTime.format(DateTimeFormatter.ISO_DATE) + " " + localDateTime.format(DateTimeFormatter.ISO_TIME)
        }

        @JvmStatic
        fun createContextMenu(fileInfo: FileInfo, mainTableView: TableView<*>, files: ObservableList<FileInfo>, mainController: MainController, sender: String): ContextMenu {
            val cm = ContextMenu()
            val name = "Open \"${fileInfo.getFileName()}\""

            val openItem = MenuItem(name)
            val openInEnclosingItem = MenuItem("Open Parent Directory")
            val deleteItem = MenuItem("Move To Trash ")
            val secureDeleteItem: MenuItem = Menu("Secure Delete")
            val renameItem = MenuItem("Rename")
            val copyItem = MenuItem("Copy")
            val createNewFile = MenuItem("Create New File")
            val copyAbsolutePathItem = MenuItem("Copy Absolute Path")
            val sendToSourceDirectoryTextFieldItem = MenuItem("Send to Source Text Field")
            val sendToDestinationDirectoryTextFieldItem = MenuItem("Send to Destination Text Field")
            val showInTreeView = MenuItem("Show in Tree View")
            val playInRightPane = MenuItem("Play in Right Pane")
            val updateAutoPlaylist = MenuItem("Update Auto Playlist With Contents Of Table")
            val maximizeThisPane = MenuItem("Maximize This Pane")
            val hideThisPane = MenuItem("Hide This Pane")
            val restorePanes = MenuItem("Restore Panes")
            val quarterScreen = MenuItem("Resize to Quarter of Screen")

            cm.items.addAll(openItem, openInEnclosingItem, deleteItem, secureDeleteItem, renameItem, copyItem, copyAbsolutePathItem,
                sendToSourceDirectoryTextFieldItem, sendToDestinationDirectoryTextFieldItem, quarterScreen)

            quarterScreen.setOnAction { mainController.fitScreenAction(null, 0.25) }

            if (sender == "tableView" || sender == "stackPane") {
                cm.items.add(showInTreeView)
            }

            cm.items.addAll(updateAutoPlaylist, restorePanes)
            cm.items.add(maximizeThisPane)

            if (mainController.mainSplitPane.items.size > 1) {
                cm.items.add(hideThisPane)
            }

            hideThisPane.setOnAction { mainController.removePaneSingular(sender) }

            restorePanes.setOnAction {
                if (sender == "tableView") {
                    mainController.restorePanesToOriginal(sender)
                } else if (sender == "treeView") {
                    mainController.restorePanesToOriginal(sender)
                } else {
                    mainController.restorePanesToOriginal("mediaContextMenu")
                }
            }

            maximizeThisPane.setOnAction {
                if (sender == "tableView") {
                    mainController.removePanes(mainController.mainTableView)
                } else if (sender == "treeView") {
                    mainController.removePanes(mainController.fileBrowserTreeTable)
                } else {
                    mainController.removePanes(mainController.rightPaneScrollPane)
                }
            }

            updateAutoPlaylist.setOnAction { mainController.storeFileList(null) }

            cm.items.addAll(playInRightPane)

            playInRightPane.setOnAction {
                if (sender == "tableView") {
                    mainController.startPlayingMedia(fileInfo, true, true)
                } else {
                    mainController.startPlayingMediaFromTree(FilePathTreeItem(Paths.get(fileInfo.absolutePath), mainController))
                }
            }

            showInTreeView.setOnAction {
                mainController.runInBackgroundThreadSecondary(Runnable {
                    FilePathTreeItem.selectTreeItemRecursively(mainController, Paths.get(fileInfo.absolutePath), true)
                })
            }

            if (fileInfo.isDirectory) {
                cm.items.add(createNewFile)
            }

            copyAbsolutePathItem.setOnAction { CommonUtilities.copyToClipboard(fileInfo.absolutePath) }
            createNewFile.setOnAction { CommonUtilities.createNewFile(fileInfo, mainController) }
            copyItem.setOnAction { CommonUtilities.copyItem(fileInfo, mainTableView, files, mainController) }
            secureDeleteItem.setOnAction { CommonUtilities.secureDelete(fileInfo, mainTableView, files, mainController) }
            deleteItem.setOnAction { CommonUtilities.moveToTrash(fileInfo, mainTableView, files, mainController) }
            openItem.setOnAction { CommonUtilities.openFile(fileInfo) }
            openInEnclosingItem.setOnAction { CommonUtilities.openEnclosingDirectory(fileInfo) }
            renameItem.setOnAction { CommonUtilities.renameFile(fileInfo, mainTableView, files, mainController) }
            sendToSourceDirectoryTextFieldItem.setOnAction { mainController.directoryToSearchTextField.text = fileInfo.absolutePath }
            sendToDestinationDirectoryTextFieldItem.setOnAction { mainController.destinationCopyAllTextField.text = fileInfo.absolutePath }

            return cm
        }
    }
}
