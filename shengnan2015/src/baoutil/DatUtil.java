package baoutil;

import java.util.Arrays;
import java.util.Locale;

public class DatUtil {
	public static void main(String args[]){
		String s = "2018-12-31";
		String [] ss = s.split("-");
		System.out.println(Arrays.toString(ss));
		StringBuilder sb = new StringBuilder(ss[0]).append("年").append(ss[1]).append("月").append(ss[2]).append("日");
		System.out.println(sb.toString());
		
		
//		System.out.println("国际化----------");
//		Locale defaultLocale = Locale.getDefault(); //当前机器所在的国家和地区
//		System.out.println(defaultLocale);
//		System.out.println(defaultLocale.getLanguage()); //英文宿写的语言名
//		System.out.println(defaultLocale.getCountry()); //英文宿写的国家名
//		System.out.println(defaultLocale.getDisplayName()); //语言名(国家名)
//		System.out.println(defaultLocale.getDisplayLanguage()); //语言名
//		System.out.println(defaultLocale.getDisplayCountry()); //国家名
////		System.out.println(defaultLocale。); //国家名
//		System.out.println("--------------------------");
	}
	
}
