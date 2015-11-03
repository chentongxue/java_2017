package date;

import java.util.Calendar;
import java.util.Date;

public class DateTag {
	public static void main(String args[]){
		Date d=  new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int a = c.get(Calendar.MINUTE)%10;
		System.out.println(a);
	}
}
