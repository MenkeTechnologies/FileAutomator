package mainPackage;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

import java.io.File;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FileInfo extends File implements CommonFileInterface {
    Image fileImage;
    private SimpleStringProperty fileName;
    private SimpleStringProperty filePath;
    private SimpleBooleanProperty directoryProperty;
    private SimpleLongProperty fileSize;
    private SimpleStringProperty lastModified;
    private SimpleStringProperty fileType;
    private SimpleBooleanProperty hiddenProperty;

    public FileInfo(String name) {
        super(name);
        initImage();
        fileName = new SimpleStringProperty(getName());
        filePath = new SimpleStringProperty(getAbsolutePath());
        directoryProperty = new SimpleBooleanProperty(isDirectory());
        fileSize = new SimpleLongProperty(length());
        hiddenProperty = new SimpleBooleanProperty(isHidden());
        lastModified = new SimpleStringProperty(Utilities.formatDate(lastModified()));
        fileType = new SimpleStringProperty(FileTypeUtilities.getFileType(getAbsolutePath()));
    }

    public String getFileType() {
        return fileType.get();
    }

    public void setFileType(String fileType) {
        this.fileType.set(fileType);
    }

    public SimpleStringProperty fileTypeProperty() {
        return fileType;
    }

    public String getLastModified() {
        return lastModified.get();
    }

    public void setLastModified(String lastModified) {
        this.lastModified.set(lastModified);
    }

    public SimpleStringProperty lastModifiedProperty() {
        return lastModified;
    }

    public boolean isHiddenProperty() {
        return hiddenProperty.get();
    }

    public void setHiddenProperty(boolean hiddenProperty) {
        this.hiddenProperty.set(hiddenProperty);
    }

    public SimpleBooleanProperty hiddenPropertyProperty() {
        return hiddenProperty;
    }

    public boolean getDirectoryProperty() {
        return directoryProperty.get();
    }

    public void setDirectoryProperty(boolean directoryProperty) {
        this.directoryProperty.set(directoryProperty);
    }

    public long getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(long fileSize) {
        this.fileSize.set(fileSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (fileName != null ? !fileName.equals(fileInfo.fileName) : fileInfo.fileName != null) return false;
        if (filePath != null ? !filePath.equals(fileInfo.filePath) : fileInfo.filePath != null) return false;
        if (directoryProperty != null ? !directoryProperty.equals(fileInfo.directoryProperty) : fileInfo.directoryProperty != null)
            return false;
        if (fileSize != null ? !fileSize.equals(fileInfo.fileSize) : fileInfo.fileSize != null) return false;
        if (lastModified != null ? !lastModified.equals(fileInfo.lastModified) : fileInfo.lastModified != null)
            return false;
        return hiddenProperty != null ? hiddenProperty.equals(fileInfo.hiddenProperty) : fileInfo.hiddenProperty == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        result = 31 * result + (directoryProperty != null ? directoryProperty.hashCode() : 0);
        result = 31 * result + (fileSize != null ? fileSize.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (hiddenProperty != null ? hiddenProperty.hashCode() : 0);
        return result;
    }

    public SimpleLongProperty fileSizeProperty() {
        return fileSize;
    }

    public SimpleBooleanProperty directoryPropertyProperty() {
        return directoryProperty;
    }

    public Image getFileImage() {
        return fileImage;
    }

    public void setFileImage(Image fileImage) {
        this.fileImage = fileImage;
    }

    private void initImage() {
        String type = FileTypeUtilities.getFileType(getAbsolutePath());
        fileImage = getImageFromType(type);
    }

    private Image getImageFromType(String type) {
        return FilePathTreeItem.getImageFromType(type);
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName=" + fileName.get() +
                ", filePath=" + filePath.get() +
                ", directoryProperty=" + directoryProperty.get() +
                ", fileSize=" + fileSize.get() +
                ", lastModified=" + lastModified.get() +
                ", fileType=" + fileType.get() +
                ", hiddenProperty=" + hiddenProperty.get() +
                "} ";
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public SimpleStringProperty filePathProperty() {
        return filePath;
    }
}
