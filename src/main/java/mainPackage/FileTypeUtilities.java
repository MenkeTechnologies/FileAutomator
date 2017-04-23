package mainPackage;

/**
 * Created by jacobmenke on 4/22/17.
 */
public class FileTypeUtilities {
    public static boolean determineIfTextual(String type) {
        switch (type){
            case "pdf":
            case "java":
            case "py":
            case "json":
            case "rb":
            case "js":
            case "fxml":
            case "xml":
            case "html":
            case "css":
            case "doc":
            case "docx":
            case "xls":
            case "xlsx":
            case "txt":
            case "jar":
            case "pl":
            case "tcl":
            case "c":
            case "cpp":
            case "h":
            case "swift":
            case "plist":
            case "conf":
                return true;
            default:
                return false;

        }

    }

    static public String getFileType(String pathName) {
        String type = "file";

        if (pathName.lastIndexOf('.') > 0) {
            String fileType = pathName.substring(pathName.lastIndexOf('.') + 1);

            switch (fileType) {
                case "png":
                case "jpg":
                case "jpeg":
                case "svg":
                    type = "image";
                    break;
                case "psd":
                    type = "psd";
                    break;
                case "ai":
                    type = "ai";
                    break;
                case "mp4":
                case "mov":
                    type = "video";
                    break;
                case "java":
                    type = "java";
                    break;
                case "py":
                    type = "python";
                    break;
                case "rb":
                    type = "ruby";
                    break;
                case "json":
                case "js":
                    type = "js";
                    break;
                case "fxml":
                case "xml":
                    type = "xml";
                    break;
                case "pdf":
                    type = "pdf";
                    break;
                case "html":
                    type = "html";
                    break;
                case "css":
                    type = "css";
                    break;
                case "mp3":
                case "wav":
                case "aiff":
                case "flac":
                    type = "music";
                    break;

                case "doc":
                case "docx":
                    type = "word";
                    break;
                case "xls":
                case "xlsx":
                    type = "excel";
                    break;
                case "txt":
                    type = "text";
                    break;
                case "jar":
                    type = "jar";
                    break;
                case "pl":
                case "tcl":
                case "c":
                case "cpp":
                case "h":
                case "swift":
                case "plist":
                case "conf":
                default:
                    type = "file";
                    break;
            }
        } else {
            //no file ending
            type = "file";
        }
        return type;
    }
}
