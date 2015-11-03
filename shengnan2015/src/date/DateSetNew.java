package date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateSetNew {
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		long t = 9L;
//		Date now = new Date(t);
		Date now = new Date(1403005502277L);
		System.out.println(format.format(now));
	}
}
