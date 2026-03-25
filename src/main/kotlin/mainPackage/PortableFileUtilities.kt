package mainPackage

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object PortableFileUtilities {
    @JvmStatic
    fun turnBytesIntoHumanReadable(length: Long): String {
        var power = 0
        var suffix = "B"
        when {
            length < 1024 -> {}
            length < Math.pow(1024.0, 2.0).toLong() -> { power = 1; suffix = "KiB" }
            length < Math.pow(1024.0, 3.0).toLong() -> { power = 2; suffix = "MiB" }
            length < Math.pow(1024.0, 4.0).toLong() -> { power = 3; suffix = "GiB" }
            length < Math.pow(1024.0, 5.0).toLong() -> { power = 4; suffix = "TiB" }
        }
        val factor = Math.pow(1024.0, power.toDouble())
        return String.format("%.2f %s", length / factor, suffix)
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
