package mainPackage;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by jacobmenke on 4/21/17.
 */
public class MicrosoftUtilities {

    public static   String getStringFromWordDocument(String pathName) {
        XWPFWordExtractor extractor = null;

        try (InputStream is = new FileInputStream(pathName);
        ) {

            if (pathName.toLowerCase().endsWith("docx")) {

                XWPFDocument xwpfDocument = new XWPFDocument(OPCPackage.open(is));

                extractor = new XWPFWordExtractor(xwpfDocument);

                return extractor.getText();
            }
            else {

                HWPFDocument document = new HWPFDocument(is);

                WordExtractor wordExtractor = new WordExtractor(document);

                return wordExtractor.getText();


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (extractor != null) {
                try {
                    extractor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getStringFromExcelDocument(String pathName){

        StringBuilder sb = new StringBuilder();


        Sheet sheet;
        try (InputStream is = new FileInputStream(pathName)){
           if (pathName.toLowerCase().endsWith("xls")){
               HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
                sheet = hssfWorkbook.getSheetAt(0);
           } else {
               XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
                sheet = xssfWorkbook.getSheetAt(0);
           }




            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()){
                    Cell currentCell = cellIterator.next();

                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                       sb.append(currentCell.getStringCellValue());
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        sb.append(currentCell.getNumericCellValue() + "");
                    }

                    sb.append('\t');


                }

                sb.append("\n");


            }




        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();


    }
}
