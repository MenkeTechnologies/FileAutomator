package mainPackage

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object PortableFileUtilities {
    private val UNITS = arrayOf("B", "KiB", "MiB", "GiB", "TiB")

    @JvmStatic
    fun turnBytesIntoHumanReadable(length: Long): String {
        var value = length.toDouble()
        var unitIndex = 0
        while (unitIndex < UNITS.size - 1 && value >= 1024.0) {
            value /= 1024.0
            unitIndex++
        }
        return String.format("%.2f %s", value, UNITS[unitIndex])
    }

    @JvmStatic
    fun quote(s: String): String = "\"$s\""

    @JvmStatic
    fun copyToClipboard(outputText: String) {
        val stringSelection = StringSelection(outputText)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, stringSelection)
    }
}
