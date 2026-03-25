package mainPackage

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileInputStream

object MicrosoftUtilities {
    @JvmStatic
    fun getStringFromWordDocument(pathName: String): String? {
        var extractor: XWPFWordExtractor? = null
        try {
            FileInputStream(pathName).use { stream ->
                return if (pathName.lowercase().endsWith("docx")) {
                    val doc = XWPFDocument(OPCPackage.open(stream))
                    extractor = XWPFWordExtractor(doc)
                    extractor!!.text
                } else {
                    val doc = HWPFDocument(stream)
                    val we = WordExtractor(doc)
                    we.text
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try { extractor?.close() } catch (e: Exception) { e.printStackTrace() }
        }
        return null
    }

    @JvmStatic
    fun getStringFromExcelDocument(pathName: String): String {
        val sb = StringBuilder()
        try {
            FileInputStream(pathName).use { stream ->
                val sheet = if (pathName.lowercase().endsWith("xls")) {
                    HSSFWorkbook(stream).getSheetAt(0)
                } else {
                    XSSFWorkbook(stream).getSheetAt(0)
                }
                for (row in sheet) {
                    for (cell in row) {
                        when (cell.cellType) {
                            CellType.STRING -> sb.append(cell.stringCellValue)
                            CellType.NUMERIC -> sb.append("${cell.numericCellValue}")
                            else -> {}
                        }
                        sb.append('\t')
                    }
                    sb.append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}
