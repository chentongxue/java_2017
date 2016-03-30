package sacred.alliance.magic.app.menu.func;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C2300_ActivePanelReqMessage;

public abstract class MenuAbstractActiveFunc extends MenuFunc{
	private final static int ACTIVE_CANOT_REMAIN_TIME = -1 ;

	public MenuAbstractActiveFunc(MenuIdType menuType) {
		super(menuType);
	}

	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		if(role.getLevel() < this.menuConfig.getRoleLevel()){
			//没有达到等级
			return null ;
		}
		
		int m = this.menuConfig.getActiveBeforeTimes() ;
		if(m <=0){
			Active active = this.getEnableActive(role);
			if(null == active){
				return null ;
			}
			MenuItem item = new MenuItem();
			return item ;
		}
		//有提前时间的情况
		int remainTime = this.getActiveRemainTime(role, m*60);
		if(remainTime == ACTIVE_CANOT_REMAIN_TIME){
			return null ;
		}
		/*if(remainTime/60 > menuConfig.getActiveBeforeTimes()){
			return null;
		}*/
		MenuItem item = new MenuItem();
		item.setActiveBeforeTimes((short)remainTime);
		return item ;
	}

	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		C2300_ActivePanelReqMessage reqMsg = new C2300_ActivePanelReqMessage();
		return reqMsg ;
	}

	protected Active getEnableActive(RoleInstance role){
		Active active = this.getActive();
		if(null == active){
			return null ;
		} 
		ActiveStatus status = active.getStatus(role);
		if(status == ActiveStatus.CanAccept 
				|| status == ActiveStatus.Underway 
				|| status == ActiveStatus.CanReward){
			return active ;
		}
		return null ;
	}
	
	/**
	 * 如果时间小于0则表示活动不可用
	 * 其余表示距离活动马上开启剩余的秒数
	 * @param role
	 * @param maxTime
	 * @return
	 */
	private int getActiveRemainTime(RoleInstance role,int maxTime){
		Active active = this.getActive();
		if(null == active){
			return ACTIVE_CANOT_REMAIN_TIME ;
		} 
		if(!active.isSuitLevel(role)){
			//等级不符合
			return ACTIVE_CANOT_REMAIN_TIME ;
		}
		//活动过期
		if(active.isOutDate()){
			return ACTIVE_CANOT_REMAIN_TIME;
		}
		//活动当天不开启
		if(!active.isDayNowActive()){
			return ACTIVE_CANOT_REMAIN_TIME;
		}
		List<String> times = active.getTimes();
		if(Util.isEmpty(times)){
			return 0 ;
		}
		
		Date now = new Date();
		for(String timeRegion : times){
			int time = this.remainTime(now, timeRegion,maxTime);
			if(time != ACTIVE_CANOT_REMAIN_TIME){
				return time;
			}
		}
		return ACTIVE_CANOT_REMAIN_TIME;
	}
	
	
	/**
	 * 判断是否在开启时间之内
	 * @param date 日期
	 * @param openTime 时间格式 8:10-12:25
	 * 24小时制，精确到分钟，多个时间段用逗号分隔，所有分隔符均是英文半角
	 * @return
	 */
	private int remainTime(Date date, String timeRegion,int maxTime){
		if(Util.isEmpty(timeRegion)){
			return ACTIVE_CANOT_REMAIN_TIME;
		}
		try{
			String time = DateUtil.date2FormatDate(date, "HH:mm:ss");
			int timeInt = toInt(time);
			
			String[] limitTime = timeRegion.split(Cat.strigula);
			int begin = toInt(limitTime[0]);
			int end = toInt(limitTime[1]);
			if (begin <= timeInt && timeInt < end) {
				//活动正在进行中
				return 0;
			}
			int willStartTime = begin - timeInt ;
			if(willStartTime >= 0 && willStartTime <= maxTime){
				return willStartTime ;
			}
			return ACTIVE_CANOT_REMAIN_TIME;
		}catch(Exception e){
			e.printStackTrace();
			return ACTIVE_CANOT_REMAIN_TIME;
		}
	}

	private int[] MULT = new int[]{60*60,60,1} ;
	private  int toInt(String str){
		String[] array = str.split(Cat.colon);
		int value = 0 ;
		for(int i=0;i< array.length;i++){
			value += Integer.valueOf(array[i])*MULT[i];
		}
		return value ;
		
	}
}
