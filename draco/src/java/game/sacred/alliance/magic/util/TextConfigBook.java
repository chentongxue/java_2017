package sacred.alliance.magic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextConfigBook implements ConfigBook{

	private int rowNum ;
	private int colNum ;
	private List<String[]> cells = new ArrayList<String[]>();
	private String encoding = "utf-8" ;
	private String SPLIt = "\t" ;
	
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
		
	}

	@Override
	public String getCellValue(int col, int row) {
		//Log4jManager.CHECK.info("========================" + rowNum + " " + colNum + " " + row + " " + col);
		return cells.get(row)[col];
	}
	
	public static void main(String[] args){
		String s = "1###" ;
		//String[] ss = org.apache.commons.lang.StringUtils.split(s,"#");
		String[] ss = s.split("#",-1);
		System.out.println(ss.length);
	}

	
	@Override
	public void open(String fileName, String sheetName) throws Exception {
		BufferedReader br = null;
		String[] fileArr = StringUtil.splitString(fileName, "/|\\\\");
		String onlyFile = fileArr[fileArr.length - 1] ;
		String full_name = fileName.substring(0,fileName.length()-onlyFile.length())  + "txt" + File.separator
				+ onlyFile + "_" + sheetName + ".txt";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					full_name), encoding));
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				if (null != line && line.trim().length() > 0) {
					String[] arr = line.split(SPLIt,-1) ;
					cells.add(arr);
					rowNum++ ;
					int old = colNum ;
					colNum = arr.length ;
					if(old != 0 && colNum != old){
						Log4jManager.CHECK.info("========================" + fileName + " " + sheetName + " " + old + " " + colNum);
					}
				}
			}
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
