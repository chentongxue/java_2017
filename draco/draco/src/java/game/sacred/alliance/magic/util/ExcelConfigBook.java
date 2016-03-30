package sacred.alliance.magic.util;

import java.io.File;

import jxl.Workbook;
import jxl.WorkbookSettings;

public class ExcelConfigBook implements ConfigBook{
	private int rowNum ;
	private int colNum ;
	private Workbook book = null;
	private jxl.Sheet sheet = null ;
	
	@Override
	public int getRowNum() {
		return this.rowNum ;
	}

	@Override
	public int getColNum() {
		return this.colNum ;
	}

	@Override
	public void close() {
		if(null != book){
			book.close();
		}
	}

	@Override
	public String getCellValue(int col, int row) {
		return sheet.getCell(col, row).getContents();
	}

	@Override
	public void open(String fileName, String sheetName) throws Exception {
		WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setEncoding("ISO-8859-1"); 
		this.book = Workbook.getWorkbook(new File(fileName),workbookSettings);
		this.sheet = book.getSheet(sheetName);
		this.rowNum = sheet.getRows();
		this.colNum = sheet.getColumns();
	}

}
