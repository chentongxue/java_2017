package sacred.alliance.magic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.python.google.common.collect.Lists;

import sacred.alliance.magic.constant.Cat;

import com.game.draco.app.union.domain.UnionActivityCd;

/**
 * @author Administrator
 *
 */
public class DateUtil {
	
	public static final int YEAR_0 = 1900 ;
	private static final String format = "HH:mm";
	public static final String format2 = "yyyy-MM-dd";
	public static final String format3 = "yyyy-MM-dd HH:mm:ss";
	public final static long ONE_DAY_MILLIS = 1000 * 60 * 60 * 24 ;
	private final static long ONE_HOUR_MILLIS = 1000 * 60 * 60  ;
	private final static int ONE_YEAR = 365;
	
	public static boolean isOutYear(Date date,int addNum){
		Date newDate = DateUtil.add(date, Calendar.MINUTE, addNum);
		return (dateDiffDay(newDate,new Date())) > ONE_YEAR;
	}
	
//	public static void main(String args[]){
		/*Date date = new Date();
		
		date = cal.getTime();
		System.out.println(date);*/
		//System.out.println(inOpenTime(date,"00:00-23:59"));
		//System.out.println(new Date().compareTo(date));
		//System.out.println(isResetTime(date,1,"9:00"));
		//System.out.println(dateInRegion(new Date(),"2012-1-1","2222-1-1"));
		//System.out.println(inOpenTime(new Date(),"00:00-23:59"));
		//System.out.println(DateUtil.getNearDate(3, "15:00"));	
		/*Date srcDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 5, 17, 9, 45);
		Date destDate = cal.getTime();
		System.out.println("same day : " + DateUtil.sameDay(srcDate, destDate));
		System.out.println("diff days : " + DateUtil.dateDiffDay(srcDate, destDate));*/
		
		//Date date1 = DateUtil.strToDate("2013-06-30 23:59", "yyyy-MM-dd HH:mm");
		// Date date2 = DateUtil.strToDate("2012-08-13 00:00", "yyyy-MM-dd
		// HH:mm");
		
		//System.out.println(DateUtil.date2FormatDate(new Date(), "MM-dd"));

		//Date date1 = new Date();
		
//		Date date1 = DateUtil.strToDate("2014-03-15 23:59", "yyyy-MM-dd HH:mm");
//		System.out.println(getMaxDayLastMonth(date1));
//		System.out.println(getMaxDayMonth(date1));
//		System.out.println(getMaxDayNextMonth(date1));
		//System.out.println(getYear(date1));
		//System.out.println(getMonth(date1));
		//System.out.println(getDay(date1));
		//System.out.println(getWeek(date1));
//	}
	
	public static int getMaxDayMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	}
	
	public static int getMaxDayLastMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE,1);
		cal.add(Calendar.MONTH,-1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	}
	
	public static int getMaxDayNextMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE,1);
		cal.add(Calendar.MONTH,1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	}
	
	public static Date getWeekDay(Date date,int weekDiff,int dayOfWeek){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// weekDiff 为推迟的周数，0本周，-1向前推迟一周，1下周，依次类推
		cal.add(Calendar.DATE, weekDiff * 7);
		// 想周几，这里就传几Calendar.MONDAY（TUESDAY...）
		cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		return cal.getTime() ;
	}
	
	
	/**
	 * [1,7]
	 * @param date
	 * @return
	 */
	public static int getWeek(Date date){
		Calendar c = Calendar.getInstance(); 
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);  
	}
	
	
	public static int getWeek(){
		Calendar c = Calendar.getInstance(); 
		return c.get(Calendar.DAY_OF_WEEK);  
	}
	
	
	/**
	 * 判断是否在开启时间之内
	 * @param date 日期
	 * @param openTime 时间格式 8:10-12:25,18:37-24:00 
	 * 24小时制，精确到分钟，多个时间段用逗号分隔，所有分隔符均是英文半角
	 * @return
	 */
	public static boolean inOpenTime(Date date, String openTime){
		if(null == openTime || openTime.equals("")){
			return false;
		}
		try{
			String time = date2FormatDate(date, format);
			int timeInt = stringToInt(time);
			
			for(String item : openTime.split(Cat.comma)){
				if(null == item){
					continue;
				}
				String[] limitTime = item.split(Cat.strigula);
				String begin = limitTime[0];
				String end = limitTime[1];
				if(stringToInt(begin) <= timeInt && timeInt < stringToInt(end)){
					return true;
				}
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private static int stringToInt(String str){
		String[] array = str.split(Cat.colon);
		return Integer.valueOf(array[0]) * 100 + Integer.valueOf(array[1]);
	}
	
	/**
	 * 判断日期是否在指定日期范围内
	 * @param date 日期
	 * @param dateStart 起始日期 格式 2012-03-07
	 * @param dateEnd   截止日期 格式 2012-04-07
	 * 如果dateStart=dateEnd=date证明同一天 返回true
	 * @return
	 * */
	public static boolean dateInRegion(Date date,String dateStart,String dateEnd){
		Date start = strToDate(dateStart,format2);
		Date end = strToDate(dateEnd,format2);
		if(null == date || null == start || null == end
				|| start.compareTo(end)==1){
			return false;
		}else if(start.compareTo(end)==0){
			return sameDay(date,start);
		}
		return date.compareTo(start)==1 && date.compareTo(end)==-1;
	}
	
	public static boolean dateInRegion(Date date,Date dateStart,Date dateEnd){
		if(null == date || null == dateStart || null==dateEnd
				|| dateStart.compareTo(dateEnd)==1){
			return false;
		}else if(dateStart.compareTo(dateEnd)==0){
			return sameDay(date,dateStart);
		}
		return date.compareTo(dateStart)==1 && date.compareTo(dateEnd)==-1;
	}
	
	public static Date strToDate(String strDate,String form){
		if(Util.isEmpty(strDate) || Util.isEmpty(form)){
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(form);
		Date result = null;
		try{
			result = format.parse(strDate);
		}catch(Exception e){
			Log4jManager.CHECK.error(DateUtil.class.getName()+" strToDate() strDate:"+strDate+" form:"+form);
		}
		return result;
	}
	
	/**
	 * 判断传入时间是否到达重置时间
	 * @param srcDate 传入时间
	 * @param day     重置周期(天)
	 * @param time    重置时间点(13:00)
	 * 如果dateStart=dateEnd=date证明同一天 返回true
	 * */
	public static boolean isResetTime(Date srcDate,int day,String time){
		if(null == srcDate ||
				day <= 0 || StringUtil.nullOrEmpty(time)){
			return false;
		}
		String[] timeArr = time.split(Cat.colon);
		if(timeArr.length!=2){
			return false;
		}
		int tarHour = Integer.parseInt(timeArr[0]);
		int tarMin = Integer.parseInt(timeArr[1]);
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcDate);
		cal.add(Calendar.DAY_OF_YEAR,day-1);
		if(cal.get(Calendar.HOUR_OF_DAY) >= tarHour){
			cal.add(Calendar.DAY_OF_YEAR,day);
		}
		cal.set(Calendar.HOUR_OF_DAY, tarHour);
		cal.set(Calendar.MINUTE,tarMin);
		cal.set(Calendar.SECOND, 0);
		//重置时间点
		Date tarDate = cal.getTime();
		Date now = new Date();
		int result = now.compareTo(tarDate);
		if(result == 1 || result == 0){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param srcDate 原始时间
	 * @param second 增加秒
	 * @return
	 */
	public static Date addSecond(Date srcDate,int second){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) srcDate.clone());
		calendar.add(Calendar.SECOND, second);
		return calendar.getTime();
	}
	
	public static Date addMinutes(Date srcDate,int minutes){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date)srcDate.clone());
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}
	
	public static Date addHours(Date srcDate,int hours){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date)srcDate.clone());
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}
	
	public static int daysBetween(Date d1,Date d2){
		return (int)((d2.getTime()-d1.getTime()+1000000)/(3600*24*1000));
	}
	
	public static int minsBetween(Date d1,Date d2){
		return (int)((d2.getTime()-d1.getTime()+1000000)/(3600*1000));
	}
	
	public static Date addMilliSecond(Date srcDate,int milliSecond){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) srcDate.clone());
		calendar.add(Calendar.MILLISECOND, milliSecond);
		return calendar.getTime();
	}
	
	public static int getYear(Date date){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		return cal1.get(Calendar.YEAR) ; 
	}
	
	/**
	 * [1-12]
	 * @param date
	 * @return
	 */
	public static int getMonthIncrOne(Date date){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		return cal1.get(Calendar.MONTH) + 1 ; 
	}
	
	/**
	 * [1-max]
	 * @param date
	 * @return
	 */
	public static int getDay(Date date){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date);
		return cal1.get(Calendar.DATE) ; 
	}
	
	/**
	 * 判断两时间是否同一天
	 * @param srcDate
	 * @param destDate
	 * @return
	 */
	public static boolean sameDay(Date srcDate,Date destDate){
		if(null == srcDate || null == destDate){
			return false ;
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(srcDate);
		cal2.setTime(destDate);
		
		if(cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)){
			return false;
		}
		
		if(cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)){
			return false;
		}
		
		if(cal1.get(Calendar.DATE) != cal2.get(Calendar.DATE)){
			return false;
		}
		
		return true;
	}
	
	public static boolean isSameWeek(Date srcDate, Date destDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(srcDate);
		cal2.setTime(destDate);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	} 
	
	
	/**
	 * 
	 * @param srcDate 原始时间
	 * @param type 增加类型
	 * @param value 增加值
	 * @return
	 */
	public static Date add(Date srcDate,int type,int value){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) srcDate.clone());
		calendar.add(type, value);
		return calendar.getTime();
	}
	
	/**
	 * 转换格式转换时间
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2FormatDate(Date date,String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	/**
	 * 两个时间相差分钟
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long dateDiffMinute(Date date1,Date date2){
		return (date1.getTime()-date2.getTime())/1000/60;
	}
	
	/**
	 * 两个时间相差秒
	 * @param date1
	 * @param date2
	 * @return
	 */
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
	/**
	 * 两个时间相差毫秒
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long dateDiffMilli(Date date1,Date date2){
		return date1.getTime()-date2.getTime();
	}
	
	/**
	 * 将String 转换成日期
	 * @param str
	 * @return
	 */
	public static Date str2Date(String str){
		if(str.trim().equals(""))
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将String 转换成日期
	 * @param str
	 * @return
	 */
	public static Date strDate(String str){
		if(str.trim().equals(""))
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将日期转换成string
	 * @param date
	 * @return
	 */
	public static String date2Str(Date date,String format){
		if(null == date) {
			return "" ;
		}
		//format = "yyyy年MM月dd日 HH:mm:ss";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	/**
	 * 将String转换成Date
	 * @param date
	 * @return
	 */
	public static Date str2Date(String str,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
		   return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param beforeTime
	 * @param nowTime
	 * @param value
	 * @param unit 1 分 2 时 3 天 4 周 5 月
	 * @return
	 */
	public static boolean inRange(Date beforeTime,Date nowTime,int value,int unit){
		if(value <=0 ){
			throw new java.lang.IllegalArgumentException("illegal value[]");
		}
		if(unit <1 || unit > 5){
			throw new java.lang.IllegalArgumentException("illegal unit,[1-5]");
		}
		if(null == beforeTime || null == nowTime ){
			return false ;
		}
		if(beforeTime.after(nowTime)){
			return false ;
		}
		Calendar calendar = Calendar.getInstance();
		
		if(1 == unit){
			//分钟
			calendar.set(beforeTime.getYear()+YEAR_0 , beforeTime.getMonth(),
					beforeTime.getDate(), beforeTime.getHours(), beforeTime.getMinutes());
			calendar.add(Calendar.MINUTE, value);
			return !nowTime.before(calendar.getTime());
		}
		
		if(2== unit){
			//时
			calendar.set(beforeTime.getYear()+YEAR_0, beforeTime.getMonth(),
					beforeTime.getDate(), beforeTime.getHours(),0);
			calendar.add(Calendar.HOUR_OF_DAY, value);
			return !nowTime.before(calendar.getTime());
		}
		if(3 == unit){
			//天	
			calendar.set(beforeTime.getYear()+YEAR_0, beforeTime.getMonth(),
					beforeTime.getDate(), 0,0);
			calendar.add(Calendar.DATE, value);
			return !nowTime.before(calendar.getTime());
		}
		
		if(4 == unit){
			//周
			calendar.setTime(beforeTime);
			calendar.set(beforeTime.getYear()+YEAR_0, beforeTime.getMonth(),
					beforeTime.getDate()-calendar.get(Calendar.DAY_OF_WEEK), 0,0);
			calendar.add(Calendar.WEEK_OF_YEAR, value);
			return !nowTime.before(calendar.getTime());
		}
		
		if(5 == unit){
			//月
			calendar.set(beforeTime.getYear()+YEAR_0, beforeTime.getMonth(),0, 0,0);
			calendar.add(Calendar.MONTH, value);
			return !nowTime.before(calendar.getTime());
		}
		return false ;
	}
	
	/**
	 * 
	 * @param date 时间
	 * @return 时间差：秒
	 */
	public static int getSecondMargin(Date date){
		long now = System.currentTimeMillis();
		long fact = 0;
		if(null != date){
			fact=date.getTime();
		}
		return Math.round((now - fact)/1000);
	}
	
	/**
	 * 获取时间间隔（毫秒）
	 * 某一时刻到限制的毫秒间隔
	 * @param date
	 * @return
	 */
	public static long getMillisecondGap(Date date){
		if(null == date){
			return 0;
		}
		long now = System.currentTimeMillis();
		return now - date.getTime();
	}
	
	/**
	 * 获得剩余时间
	 * @param endTime
	 * @param currTime
	 * @return
	 */
	public static int getRemainTime(long endTime,long currTime){
		long reslut=endTime-currTime;
		int remain=0;
		if(reslut % 1000==0){
			remain=(int)(reslut/1000);
		}else{
			remain=(int)(reslut/1000)+1;
		}
		return (int)remain;
	}

	/**
	 * @param lastprocesstime 上次执行的时间：毫秒
	 * @param skillCoolTime 冷确时间：毫秒
	 * @return
	 */
	public static long getTimeDiff(long lastprocesstime ,int skillCoolTime){
		long diff = System.currentTimeMillis() - lastprocesstime;
		if(diff <= 0){
			return 0 ;
		}
		long ret = skillCoolTime - diff;
		if(0 >= ret){ 
			return 0;
		}
		return ret;
	}
	
	public static boolean isNextDay(int roleTime){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date d1 = null;
		try {
			d1 = format.parse(String.valueOf(roleTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		if(cal2.get(Calendar.YEAR) > cal1.get(Calendar.YEAR)) {
			return true;
		}
		if(cal2.get(Calendar.DAY_OF_YEAR) > cal1.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 两个时间相差小时
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int dateDiffHour(Date date1,Date date2){
		if(null == date1 || null == date2){
			return 0 ;
		}
		Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(date1);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(date2);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0); 
        long times = toCalendar.getTime().getTime() - fromCalendar.getTime().getTime() ;
        return Math.abs((int)(times/ONE_HOUR_MILLIS)); 
	}
	
	/**
	 * 两个时间相差的天数
	 * @return
	 */
	public static int dateDiffDay(Date date1, Date date2){
		if(null == date1 || null == date2){
			return 0 ;
		}
		Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(date1);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(date2);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0); 
        long times = toCalendar.getTime().getTime() - fromCalendar.getTime().getTime() ;
        return Math.abs((int)(times/ONE_DAY_MILLIS));  
	}
	
	

	/**
	 * 将 Date 型时间转化为 int 型秒级时间
	 * @param date
	 * @return
	 */
	public static int dateFormatToInt(Date date){
		if(null == date){
			return 0 ;
		}
		return (int)date.getTime()/1000 ;
	}
	
	
	/**
	 * 将系统Date类型日期时间 转化为 (yyyy-MM-dd HH:mm:ss)类型日期时间
	 * @param date
	 * @return
	 * @desc 2011-09-20
	 */
	public static String getTimeByDate(Date date){
		SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date!=null){
			return formatOut.format(date);
		}
		return "0000-00-00 00:00:00";
	}
	
	
	
	/**
	 * 将2012-02-05 转换成2012年02月05日
	 * @param date
	 * @return
	 */
	public static String getDayFormat(Date date){
		if(null == date){
			return "" ;
		}
		SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd");
		return formatOut.format(date);
	}
	
	
	public static String getMinFormat(Date time){
		if(null == time){
			return "" ;
		}
		SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formatOut.format(time);
	}

	
	/**将分钟转化为以下格式*/
	public static String formatDuring(int minute) {
		long days = minute / (60 * 24);
		long hours = (minute % (60 * 24)) / 60;
		long minutes = (minute % 60);
		if(days == 0 && hours != 0){
			return hours + "小时" + minutes + "分钟";
		}
		if(hours == 0 && days == 0){
			return minutes + "分钟";
		}
		return days + "日" + hours + "小时" + minutes + "分钟";
	} 
	
	/**
	 * 判断传入时间与当前时间是否为同一天
	 * @param dateTime 13为秒级时间
	 * @return
	 */
	public static boolean sameDay(int dateTime){
		long startTime = ((Integer)dateTime).longValue() * 1000;
		
		Calendar calender = Calendar.getInstance(java.util.Locale.CHINESE);
		calender.setTimeInMillis(startTime);
		Date startDate = calender.getTime();
		
		return sameDay(startDate, new Date());
	}
	
	/**
	 * 判断传入时间与当前时间是否为同一天
	 * @param dateTime 时间1 13为秒级时间
	 * @param currTime 时间2 13为秒级时间
	 * @return
	 */
	public static boolean sameDay(int dateTime, int currDateTime){
		long currTime = ((Integer)(currDateTime * 1000)).longValue();
		long startTime = ((Integer)(dateTime * 1000)).longValue();
		
		Calendar calender = Calendar.getInstance(java.util.Locale.CHINESE);
		calender.setTimeInMillis(startTime);
		Date startDate = calender.getTime();
		
		calender.setTimeInMillis(currTime);
		Date currDate = calender.getTime();
		
		return sameDay(startDate, currDate);
	}
	
	/**
	 * 获取上一次的重置时间
	 * 从系统启动开始至现在，最接近的时间
	 * @param gap 间隔 单位天
	 * @param time 时间 格式18:27
	 * @return
	 */
	public static Date getLastResetDate(Date baseDate, int gap, String time){
		if(null == time){
			return null;
		}
		String[] timeStr = time.split(Cat.colon);
		if(null == timeStr || 2 != timeStr.length){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(baseDate);
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeStr[0]));
		cal.set(Calendar.MINUTE, Integer.valueOf(timeStr[1]));
		cal.set(Calendar.SECOND, 0);
		Date now = new Date();
		Date nearDate = cal.getTime();
		while(true){
			if(nearDate.after(now)){
				cal.add(Calendar.DAY_OF_YEAR, -gap);
				nearDate = cal.getTime();
				break ;
			}
			cal.add(Calendar.DAY_OF_YEAR, gap);
			nearDate = cal.getTime();
		}
		return nearDate;
	}
	
	/**
	 * 记录时间是否过期
	 * @param baseDate 基准时间（如：系统启动时间）
	 * @param recordDate 记录时间
	 * @param gap 间隔 单位天
	 * @param time 时间 格式18:27
	 * @return
	 */
	public static boolean isTimeOver(Date baseDate, Date recordDate, int gap, String time){
		return recordDate.before(DateUtil.getLastResetDate(baseDate, gap, time));
	}

	// /**
	// * 得到允许喂养的时间
	// * @param lastFeedDate
	// * @return
	// */
	// public static Date getAllowFeedDate(Date lastFeedDate){
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(lastFeedDate);
	// cal.add(Calendar.MINUTE, -GameContext.getSoulConfig().getFeedCoolTime());
	// return cal.getTime();
	//	}
	
	/**
	 * 判断两个日期是否是同一个月
	 */
	public static boolean isSameMonth(Date srcDate, Date destDate){
		if(null == srcDate || null == destDate){
			return false ;
		}
		if(srcDate.getYear() != destDate.getYear()){
			return false ;
		}
		if(srcDate.getMonth() != destDate.getMonth()){
			return false ;
		}
		return true;
	}
	/**
	 * 对日期添加对应的天数
	 * @param date
	 * @param addDay
	 * @return
	 */
	public static Date addDayToDate(Date date , int addDay){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, addDay);
		return cal.getTime();
	}
	
	/** 日期的凌晨时间 */
	public static Date getDateZero(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	/** 日期的最后时间 23:59:59 */
	public static Date getDateEndTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}
	
	public static boolean isSameHour(Date src, Date dest){
		Calendar calSrc = Calendar.getInstance();
		Calendar calDest = Calendar.getInstance();
		calSrc.setTime(src);
		calDest.setTime(dest);
		return ((calSrc.get(Calendar.HOUR_OF_DAY) - calDest.get(Calendar.HOUR_OF_DAY)) == 0);
	}
	
	/**
	 * 判断是否是一周的同一天
	 */
	public static boolean isSameDayInWeek(Date src, Date dest){
		Calendar calSrc = Calendar.getInstance();
		Calendar calDest = Calendar.getInstance();
		calSrc.setTime(src);
		calDest.setTime(dest);
		return ((calSrc.get(Calendar.DAY_OF_WEEK) - calDest.get(Calendar.DAY_OF_WEEK)) == 0);
	}
	
	/**
	 * 判断是否是一月的同一天
	 */
	public static boolean isSameDayInMonth(Date src, Date dest){
		Calendar calSrc = Calendar.getInstance();
		Calendar calDest = Calendar.getInstance();
		calSrc.setTime(src);
		calDest.setTime(dest);
		return ((calSrc.get(Calendar.DAY_OF_MONTH) - calDest.get(Calendar.DAY_OF_MONTH)) == 0);
	}
	
	/**
	 * 相对开始时间
	 * @param date 参照日期
	 * @param days 相对天数 1表示当天
	 * @return 当天的零点
	 */
	public static Date getStartDate(Date date, int days){
		return DateUtil.addDayToDate(DateUtil.getDateZero(date), days-1);
	}
	
	/**
	 * 相对结束时间
	 * @param date 参照日期
	 * @param days 相对天数 1表示当天
	 * @return 当天的23点59分59秒
	 */
	public static Date getEndDate(Date date, int days){
		return DateUtil.addDayToDate(DateUtil.getDateEndTime(date), days-1);
	}
	
	/**
	 * 重新设置时间的时、分、秒
	 * @param date
	 * @return
	 */
	public static Date setDate(Date date,int hour,int minute,int second){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}
	
	public static int getHour(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 转换成string格式
	 * @param time
	 * @param format
	 * @return
	 */
	public static String date2FormatDate(long time,String format){
		Date date = new Date();
		date.setTime(time);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	public static void main(String [] args){
		List<UnionActivityCd> list = Lists.newArrayList();
		long nowTime = System.currentTimeMillis();
		//3天
		int cd3 = 3*1000*3600*24;
		int cd1 = 1*1000*3600*24;
		for(int i=0;i<100;i++){
			nowTime-=cd3;
		}
		for(int i=0;i<1000;i++){
			UnionActivityCd time = new UnionActivityCd();
			time.setStartTime(nowTime);
			System.out.println("开始时间：" + getTimeByDate(getDateZero(new Date(nowTime))));
			nowTime+=cd3;
			
			time.setEndTime(nowTime);
			list.add(time);
			System.out.println("结束时间" + getTimeByDate(getDateEndTime(new Date(nowTime))));
			nowTime+=cd1;
			
			System.out.println(i + "行");
		}
		int k = 0;
		for(UnionActivityCd cd : list){
			if(System.currentTimeMillis() >= cd.getStartTime() && System.currentTimeMillis() <= cd.getEndTime()){
				System.out.println("startTime=" + getTimeByDate(getDateZero(new Date(cd.getStartTime()))) + ";endTime=" + getTimeByDate(getDateEndTime(new Date(cd.getEndTime()))) );
				System.out.println(k + "行");
			}
			k++;
		}
	}
	
}
