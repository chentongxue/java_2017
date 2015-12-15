package date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LookLong {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void main(String[] args) throws ParseException {
		long time = System.currentTimeMillis();
		long t = 9L;
//		Date now = new Date(t);
		Date date = sdf.parse( "2015-12-01 00:00:00" );
		Date date2 = sdf.parse( "2015-12-02 00:00:00" );
		Date now = new Date(1403005502277L);
		System.out.println(date.getTime());
		System.out.println(date2.getTime());
		
		
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		System.out.println(hour);
		
		
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(c.getTime());
		cal1.set(Calendar.HOUR_OF_DAY, hour);
		cal1.add(Calendar.HOUR_OF_DAY, -1);//上一个小时
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(c.getTime());
		cal2.set(Calendar.HOUR_OF_DAY, hour);
		cal2.set(Calendar.MINUTE, 59);
		cal2.set(Calendar.SECOND, 59);
		System.out.println(sdf.format(cal1.getTime()));
		System.out.println(sdf.format(cal2.getTime()));
	}
}
