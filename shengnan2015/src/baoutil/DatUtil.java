package baoutil;

import java.util.Arrays;
import java.util.Locale;

public class DatUtil {
	public static void main(String args[]){
		String s = "2018-12-31";
		String [] ss = s.split("-");
		System.out.println(Arrays.toString(ss));
		StringBuilder sb = new StringBuilder(ss[0]).append("��").append(ss[1]).append("��").append(ss[2]).append("��");
		System.out.println(sb.toString());
		
		
//		System.out.println("���ʻ�----------");
//		Locale defaultLocale = Locale.getDefault(); //��ǰ�������ڵĹ��Һ͵���
//		System.out.println(defaultLocale);
//		System.out.println(defaultLocale.getLanguage()); //Ӣ����д��������
//		System.out.println(defaultLocale.getCountry()); //Ӣ����д�Ĺ�����
//		System.out.println(defaultLocale.getDisplayName()); //������(������)
//		System.out.println(defaultLocale.getDisplayLanguage()); //������
//		System.out.println(defaultLocale.getDisplayCountry()); //������
////		System.out.println(defaultLocale��); //������
//		System.out.println("--------------------------");
	}
	
}
