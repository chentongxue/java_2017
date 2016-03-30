package sacred.alliance.magic.app.announce;

import java.util.Calendar;
import java.util.Date;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.SysAnnouncement;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;

public @Data class SystemBroadcast extends SysAnnouncement{
	private String startTimeStr;
	private String endTimeStr;
	private int relativeStartTime;//相对开始时间(天)
	private int relativeEndTime;//相对结束时间(天)
	private String startTimeSection;//开始时间段
	private String endTimeSection;//结束时间段
	private String week;//周
	private byte channelType;//频道
	
	
	private Date relativeStartDate;//相对开始时间(日期)
	private Date relativeEndDate;//相对结束时间(日期)
	
	private Date startSectionDate;//时间段开始时间
	private Date endSectionDate;//时间段结束时间
	
	/**初始化开服日期*/
	public void init(){
		if(relativeStartTime > 0 && relativeEndTime > 0){
			Date gameStartDate = GameContext.getGameStartDate();
			relativeStartDate = DateUtil.getStartDate(gameStartDate, relativeStartTime);
			relativeEndDate  = DateUtil.getEndDate(gameStartDate, relativeEndTime);
		}
		if(!Util.isEmpty(startTimeStr) && !Util.isEmpty(endTimeStr)){
			this.startTime = DateUtil.strToDate(startTimeStr, DateUtil.format_yyyy_MM_dd);
			this.endTime = DateUtil.strToDate(endTimeStr, DateUtil.format_yyyy_MM_dd);
		}
		
	}
	
	/**相对时间满足*/
	private boolean relativeTime(){
		if(relativeStartDate == null || relativeEndDate == null){
			return false;
		}
		Date now = new Date();
		if(now.before(relativeStartDate)){
			return false;
		}
		if(now.after(relativeEndDate)){
			return false;
		}
		if(!isTimeSection()){
			return false;
		}
		return this.isGap();
		
	}
	
	/**绝对时间满足*/
	private boolean absoluteTime(){
		if(startTime == null || endTime == null){
			return false;
		}
		if(!isTimeSection()){
			return false;
		}
		return canSend(new Date());
	}
	
	/**时间段满足*/
	private boolean isTimeSection(){
		Date now = new Date();
		startSectionDate = packEndTime(startTimeSection);
		endSectionDate = packEndTime(endTimeSection);
		
		if(now.before(startSectionDate)){
			return false;
		}
		if(timeGap == 0){
			if(DateUtil.dateDiffMinute(new Date(), startSectionDate) == 0){
				return true;
			}
			return false;
		}
		if(now.after(endSectionDate)){
			return false;
		}
		return true;
	}
	
	private Date packEndTime(String timeRegion){
		String[] time = timeRegion.split(Cat.colon);
		int hour = Integer.valueOf(time[0]);
		int minute = Integer.valueOf(time[1]);
		Calendar c = Calendar.getInstance(); //获取当前日期 
		c.set(Calendar.HOUR_OF_DAY,hour);
		c.set(Calendar.MINUTE,minute);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}
	
	
	/**满足星期几*/
	private boolean isAllowWeek(){
		if(Util.isEmpty(week)){
			return true;
		}
		String day = String.valueOf(DateUtil.getWeek());
		return week.indexOf(day) != -1;
	}

	/**
	 * 判断是否可发送
	 * @param now
	 * @return
	 */
	public boolean canPublish(Date now){
		if(!this.isAllowWeek()){
			return false;
		}
		if(!this.relativeTime() && !this.absoluteTime()){
			return false;
		}
		return true;
	}
	
	/**
	 * 校正初始化信息
	 * @param now
	 */
	public void checkIndex(Date now){
		
	}
	
}
