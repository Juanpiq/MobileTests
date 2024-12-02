package org.example.FuncionesExcel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FunExcel {

    // Método para obtener el número de filas en una hoja
    public int getRowCount(String filePath, String sheetName) throws IOException, InvalidFormatException {
        int rowCount = 0;
        try (FileInputStream file = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            rowCount = sheet.getPhysicalNumberOfRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowCount;
    }

    // Método para obtener el número de columnas en una hoja
    public int getColumnCount(String filePath, String sheetName) throws IOException, InvalidFormatException {
        int columnCount = 0;
        try (FileInputStream file = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return columnCount;
    }

    // Método para leer datos de una celda
    public String readData(String filePath, String sheetName, int rowNum, int colNum) throws IOException, InvalidFormatException {
        String cellValue = "";
        try (FileInputStream file = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum - 1); // Filas en Excel comienzan desde 1
            if (row != null) {
                Cell cell = row.getCell(colNum - 1); // Las celdas también comienzan desde 1
                if (cell != null) {
                    cellValue = cell.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cellValue;
    }

    // Método para escribir datos en una celda
    public void writeData(String filePath, String sheetName, int rowNum, int colNum, String data) throws IOException, InvalidFormatException {
        try (FileInputStream file = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }
            Row row = sheet.getRow(rowNum - 1); // Filas en Excel comienzan desde 1
            if (row == null) {
                row = sheet.createRow(rowNum - 1);
            }
            Cell cell = row.createCell(colNum - 1); // Las celdas comienzan desde 1
            cell.setCellValue(data);

            // Guardar los cambios en el archivo
            try (FileOutputStream outFile = new FileOutputStream(new File(filePath))) {
                workbook.write(outFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
