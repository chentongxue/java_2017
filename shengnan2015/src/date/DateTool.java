package date;

import java.util.Calendar;
import java.util.Date;

public class DateTool {
	public static void main(String args[]){
		
	}
	public static boolean sameDay(Date d0,Date d2){
		if(null == d0 || null == d2){
			return false ;
		}
		Calendar c0 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c0.setTime(d0);
		c2.setTime(d2);
		
		if(c0.get(Calendar.YEAR) != c2.get(Calendar.YEAR)){
			return false;
		}
		
		if(c0.get(Calendar.MONTH) != c2.get(Calendar.MONTH)){
			return false;
		}
		
		if(c0.get(Calendar.DATE) != c2.get(Calendar.DATE)){
			return false;
		}
		
		return true;
	}
	
	public static boolean isSameWeek(Date d0, Date d1) {
		Calendar c0 = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();
		c0.setTime(d0);
		c1.setTime(d1);
		int subYear = c0.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		if (0 == subYear) {
			if (c0.get(Calendar.WEEK_OF_YEAR) == c1.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == c1.get(Calendar.MONTH)) {
			if (c0.get(Calendar.WEEK_OF_YEAR) == c1.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == c0.get(Calendar.MONTH)) {
			if (c0.get(Calendar.WEEK_OF_YEAR) == c1
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	} 
}
