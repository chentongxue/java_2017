package a_serial.protobuf;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum DateUtil{
	INSTANCE;
	
	private static String format = "";
	private static SimpleDateFormat sdf = new SimpleDateFormat("YYYY_MM_dd_HH_");
	public String getTodayStamp(){
		return sdf.format(new Date());
	}
	public static void main(String args[]){
		System.out.println(DateUtil.INSTANCE.getTodayStamp());
	}
} 