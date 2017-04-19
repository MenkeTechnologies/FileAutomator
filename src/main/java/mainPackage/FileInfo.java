package mainPackage;

import javafx.beans.property.*;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class FileInfo extends File {
    private SimpleStringProperty fileName;
    private SimpleStringProperty filePath;
    private SimpleBooleanProperty directoryProperty;

    private SimpleLongProperty fileSize;
    private SimpleStringProperty lastModified;

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

        fileName = new SimpleStringProperty(getName());
        filePath = new SimpleStringProperty(getAbsolutePath());
        directoryProperty = new SimpleBooleanProperty(isDirectory());
        fileSize = new SimpleLongProperty(length());
        hiddenProperty = new SimpleBooleanProperty(isHidden());

        lastModified = new SimpleStringProperty(Utilities.formatDate(lastModified()));


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
