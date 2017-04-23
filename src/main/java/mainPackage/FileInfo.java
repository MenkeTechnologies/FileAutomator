package mainPackage;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.io.File;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FileInfo extends File {
    private SimpleStringProperty fileName;
    private SimpleStringProperty filePath;
    private SimpleBooleanProperty directoryProperty;

    private SimpleLongProperty fileSize;
    private SimpleStringProperty lastModified;

    Image fileImage;


    public String getLastModified() {
        return lastModified.get();
    }

    public SimpleStringProperty lastModifiedProperty() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified.set(lastModified);
    }


    public boolean isHiddenProperty() {
        return hiddenProperty.get();
    }

    public SimpleBooleanProperty hiddenPropertyProperty() {
        return hiddenProperty;
    }

    public void setHiddenProperty(boolean hiddenProperty) {
        this.hiddenProperty.set(hiddenProperty);
    }

    private SimpleBooleanProperty hiddenProperty;

    public boolean getDirectoryProperty() {
        return directoryProperty.get();
    }

    public long getFileSize() {
        return fileSize.get();
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

    public void setFileSize(long fileSize) {
        this.fileSize.set(fileSize);
    }

    public SimpleBooleanProperty directoryPropertyProperty() {
        return directoryProperty;
    }


    public void setDirectoryProperty(boolean directoryProperty) {
        this.directoryProperty.set(directoryProperty);
    }



    public FileInfo(String name) {
        super(name);
        initImage();
        fileName = new SimpleStringProperty(getName());
        filePath = new SimpleStringProperty(getAbsolutePath());
        directoryProperty = new SimpleBooleanProperty(isDirectory());
        fileSize = new SimpleLongProperty(length());
        hiddenProperty = new SimpleBooleanProperty(isHidden());
        lastModified = new SimpleStringProperty(Utilities.formatDate(lastModified()));

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

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getFilePath() {
        return filePath.get();
    }

    public SimpleStringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}
