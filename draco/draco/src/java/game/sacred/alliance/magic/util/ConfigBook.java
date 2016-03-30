package sacred.alliance.magic.util;

public interface ConfigBook {

	int getRowNum();
	int getColNum();
	void close() ;
	String getCellValue(int col,int row);
	void open(String fileName,String sheetName) throws Exception ;
}
