package date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateLook {
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void main(String[] args) {
		Date now = new Date();
		Date next = getNextTime();
		System.out.println(dateDiffSecond(now, next)>0);
		System.out.println(12%8);
		
		getCalenderTime();
		getT();
	}
	public static Date getNextTime(){
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 22);
		cal.add(Calendar.DAY_OF_YEAR, 1);//明天
		System.out.println(format.format(cal.getTime()));
		return cal.getTime();
	}
	//容易有歧义的地方
	public static void getCalenderTime(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 7);
//		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.HOUR, 1);
		System.out.println(format.format(cal.getTime()));
		System.out.println(format.format(cal.getTime()));
	}
	//设置月和日 8月4日
	public static void getT(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, 7);
		c.set(Calendar.DAY_OF_MONTH, 4);
		c.set(Calendar.HOUR_OF_DAY,0);
		System.out.println(format.format(c.getTime()));
	}
	public static int dateDiffSecond(Date date1,Date date2){
		if(date1.getTime()>=date2.getTime()){
			return 0;
		}
		long reslut=date2.getTime()-date1.getTime();
		int remain=0;
		if(reslut % 1000==0){
			remain=(int)(reslut/1000);
		}else{
			remain=(int)(reslut/1000)+1;
		}
		return remain;
	}
}
