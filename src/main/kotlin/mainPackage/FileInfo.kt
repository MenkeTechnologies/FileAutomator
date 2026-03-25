package mainPackage

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import java.io.File

class FileInfo(name: String) : File(name), CommonFileInterface {
    @JvmField var fileImage: Image? = null
    private val fileNameProp = SimpleStringProperty(getName())
    private val filePathProp = SimpleStringProperty(absolutePath)
    private val directoryProp = SimpleBooleanProperty(isDirectory)
    private val fileSizeProp = SimpleLongProperty(length())
    private val hiddenProp = SimpleBooleanProperty(isHidden)
    private val lastModifiedProp = SimpleStringProperty(Utilities.formatDate(lastModified()))
    private val fileTypeProp = SimpleStringProperty(FileTypeUtilities.getFileType(absolutePath))

    init {
        initImage()
    }

    fun getFileType(): String = fileTypeProp.get()
    fun setFileType(v: String) { fileTypeProp.set(v) }
    fun fileTypeProperty() = fileTypeProp

    fun getLastModified(): String = lastModifiedProp.get()
    fun setLastModified(v: String) { lastModifiedProp.set(v) }
    fun lastModifiedProperty() = lastModifiedProp

    fun isHiddenProperty(): Boolean = hiddenProp.get()
    fun setHiddenProperty(v: Boolean) { hiddenProp.set(v) }
    fun hiddenPropertyProperty() = hiddenProp

    fun getDirectoryProperty(): Boolean = directoryProp.get()
    fun setDirectoryProperty(v: Boolean) { directoryProp.set(v) }
    fun directoryPropertyProperty() = directoryProp

    fun getFileSize(): Long = fileSizeProp.get()
    fun setFileSize(v: Long) { fileSizeProp.set(v) }
    fun fileSizeProperty() = fileSizeProp

    fun getFileImage(): Image? = fileImage
    fun setFileImage(v: Image?) { fileImage = v }

    private fun initImage() {
        val type = FileTypeUtilities.getFileType(absolutePath)
        fileImage = FilePathTreeItem.getImageFromType(type)
    }

    fun getFileName(): String = fileNameProp.get()
    fun setFileName(v: String) { fileNameProp.set(v) }
    fun fileNameProperty() = fileNameProp

    fun getFilePath(): String = filePathProp.get()
    fun setFilePath(v: String) { filePathProp.set(v) }
    fun filePathProperty() = filePathProp

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int = super.hashCode()

    override fun toString(): String =
        "FileInfo{fileName=${fileNameProp.get()}, filePath=${filePathProp.get()}, directoryProperty=${directoryProp.get()}, fileSize=${fileSizeProp.get()}, lastModified=${lastModifiedProp.get()}, fileType=${fileTypeProp.get()}, hiddenProperty=${hiddenProp.get()}} "
}
