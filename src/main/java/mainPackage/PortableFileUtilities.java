package mainPackage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Created by jacobmenke on 5/11/17.
 */
public class PortableFileUtilities {
    public static String turnBytesIntoHumanReadable(long length) {
        Integer power = 0;

        String suffix = "B";

        if (length < 1024) {

        } else if (length < Math.pow(1024, 2)) {
            power = 1;
            suffix = "KiB";
        } else if (length < Math.pow(1024, 3)) {
            power = 2;
            suffix = "MiB";
        } else if (length < Math.pow(1024, 4)) {
            power = 3;
            suffix = "GiB";
        } else if (length < Math.pow(1024, 5)) {
            power = 4;
            suffix = "TiB";
        }

        Double factor = Math.pow(1024, power);

        return String.format("%.2f %s", length / factor, suffix);
    }

    public static String quote(String s) {

        StringBuilder sb = new StringBuilder();

        sb.append("\"").append(s).append("\"");
        return sb.toString();
    }

    public static void copyToClipboard(String outputText) {
        StringSelection stringSelection = new StringSelection(outputText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }
}
