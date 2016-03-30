package com.easter.test.language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.util.XlsPojoUtil;

public class I18nXls2Txt {
	
	private List<I18nXlsBean> xlsList = new ArrayList<I18nXlsBean>();
	
	private void loadI18nXls(String sourceFile, String sheetName){
		this.xlsList = XlsPojoUtil.sheetToList(sourceFile, sheetName, I18nXlsBean.class);
	}
	
	private void convert(String resultFile){
		try {
			BufferedWriter writer  = new BufferedWriter(new FileWriter(new File(resultFile)));
			for(I18nXlsBean bean : this.xlsList){
				if(null == bean){
					continue;
				}
				String str = bean.getTextId() + "=" + bean.getCn() + "\r\n";
				writer.write(str);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void xls2txt(String sourceFile, String sheetName, String resultFile){
		this.loadI18nXls(sourceFile, sheetName);
		this.convert(resultFile);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "server\\draco\\trunk\\conf\\game\\i18n\\";
		String sourceFile = path + "game_i18n.xls";
		String sheetName = "i18n";
		String resultFile = path + "game_i18n_cn.txt";
		System.out.println("****** start ******");
		I18nXls2Txt i18nXls2Txt = new I18nXls2Txt();
		i18nXls2Txt.xls2txt(sourceFile, sheetName, resultFile);
		System.out.println("****** end ******");
	}

}
