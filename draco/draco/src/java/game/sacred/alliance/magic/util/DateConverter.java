package sacred.alliance.magic.util;

import java.util.Date;

import org.joda.time.DateTime;

import com.game.draco.GameContext;

import sacred.alliance.magic.base.DateTimeBean;

public class DateConverter {
	
	/**
	 * 转化开始时间和结束时间
	 * @param relStartDay 相对开始天数
	 * @param relEndDay 相对结束天数
	 * @param absStartStr 绝对开始时间
	 * @param absEndStr 绝对结束时间
	 * @param format 绝对日期格式
	 * @return
	 */
	public static DateTimeBean getDateTimeBean(int relStartDay, int relEndDay, String absStartStr, String absEndStr, String format){
		
		boolean hasRel = relStartDay > 0 || relEndDay > 0;//是否有相对时间
		boolean hasAbs = !Util.isEmpty(absStartStr) || !Util.isEmpty(absEndStr);//是否有绝对时间
		if(!hasRel && !hasAbs){
			return null;
		}
		//相对时间
		Date relStartDate = null;
		Date relEndDate = null;
		if(hasRel){
			if(relEndDay < relStartDay){
				return null;
			}
			relStartDate = DateUtil.getStartDate(GameContext.gameStartDate, relStartDay);
			relEndDate = DateUtil.getEndDate(GameContext.gameStartDate, relEndDay);
		}
		//绝对时间
		Date absStartDate = null;
		Date absEndDate = null;
		if(hasAbs){
			if(Util.isEmpty(absStartStr) || Util.isEmpty(absEndStr)){
				return null;
			}
			//joda的DateTime参数格式yyyy-MM-ddTHH:mm:ss，故需要把空格替换成字母T
			absStartStr = absStartStr.trim().replace(" ", "T");
			absEndStr = absEndStr.trim().replace(" ", "T");
			DateTime dtStart = new DateTime(absStartStr);
			absStartDate = dtStart.toDate();
			DateTime dtEnd = new DateTime(absEndStr);
			//若为yyyy-MM-dd格式，结束时间需要变成当天的23点59分59秒；否则之间为配置的时间
			if(10 == absEndStr.length()){
				absEndDate = dtEnd.plusDays(1).plusSeconds(-1).toDate();
			}else{
				absEndDate = dtEnd.toDate();
			}
		}
		Date startDate = null;
		Date endDate = null;
		//相对时间和绝对时间都存在
		if(hasRel && hasAbs){
			startDate = relStartDate.after(absStartDate) ? relStartDate: absStartDate;
			endDate = relEndDate.before(absEndDate) ? relEndDate : absEndDate;
		}else{
			startDate = null != relStartDate ? relStartDate: absStartDate;
			endDate = null != relEndDate ? relEndDate : absEndDate;
		}
		if(null != startDate && null != endDate){
			DateTimeBean bean = new DateTimeBean();
			bean.setStartDate(startDate);
			bean.setEndDate(endDate);
			return bean;
		}
		return null;
	}
	
}
