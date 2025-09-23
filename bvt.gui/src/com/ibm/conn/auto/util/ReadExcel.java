package com.ibm.conn.auto.util;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * utility class to read data from excel file
 * 
 * @author rajashri.chitrakar
 *
 */

public class ReadExcel {
	
	/**
	 * it returns the object array of the rows in excel file
	 * @param filepath -- path of file the data reads from
	 * @param sheetname -- name of the exact sheet inside excel file the data reads from
	 * @param col -- number of columns in sheet
	 * @return object array with data from excel
	 */

	public Object[][] readExcel(String filepath, String sheetname, int col) throws IOException {

		FileInputStream fis = new FileInputStream(filepath);

		// Instantiating Excel file object
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		// Instantiating object for specific sheet in excel file
		XSSFSheet sheet = wb.getSheet(sheetname);

		// Returning total number of rows in a sheet
		int row = sheet.getLastRowNum();

		// Initialize object array with row and column
		Object[][] data = new Object[row][col];

		for (int r = 1; r <= row; r++) {
			for (int c = 0; c < col; c++) {

				if (sheet.getRow(r).getCell(c) != null) {
					if (sheet.getRow(r).getCell(c).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						double colval = sheet.getRow(r).getCell(c).getNumericCellValue();
						data[r - 1][c] = String.valueOf(colval);
					} else {
						String colval = sheet.getRow(r).getCell(c).getStringCellValue();
						data[r - 1][c] = colval;
					}
				} else {
					data[r - 1][c] = "";
				}
			}
		}
		return data;
	}
}
