package sacred.alliance.magic.app.menu;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuItem;

public abstract class MenuBefore extends MenuFunc{

	public MenuBefore(MenuIdType menuType) {
		super(menuType);
	}
	
	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		return null;
	}
	
	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		MenuItem item = new MenuItem();
		item.setStatus((byte)0);
		int remainTime = menuConfig.getActiveBeforeTimes()*60;
		item.setActiveBeforeTimes((short)remainTime);
		return item ;
	}
	
//	public static void main(String[] args){
//		int before = 21 ;
//		String weekTerm = "" ;
//		List<String> times = new ArrayList<String>();
//		times.add("19:20-19:30");
//		times.add("0:20-0:30");
//		List<String> list = MenuBefore.getCronExpression(before, times, weekTerm);
//		if(null == list){
//			System.out.println("NULL");
//			return ;
//		}
//		for(String s : list){
//			System.out.println(s);
//		}
//	}
	
	
	private static List<String> getCronExpression(int before,List<String> times,String weekTerm){
		if(Util.isEmpty(times)){
			return null ;
		}
		List<String> list = new ArrayList<String>();
		String weekExpress = "? * *";//表示每天都执行
		if(!Util.isEmpty(weekTerm)){
			weekExpress = "? * " + weekTerm ;//表示每周的周几执行
		}
		for(String timeRange : times){
			String[] timeStrs = timeRange.split(Cat.strigula);
			if(null == timeStrs || 0 == timeStrs.length){
				continue ;
			}
			//只需要获得开始时间
			String timeStr = timeStrs[0].trim() ;
			String[] timeValue = timeStr.split(Cat.colon);
			int minute = Integer.valueOf(timeValue[1]) ;
			int hour = Integer.valueOf(timeValue[0]) ;
			int minuteDiff = minute - before ;
			if(minuteDiff >=0 ){
				minute = minuteDiff ;
			}else{
				//分钟不够减,需要向小时借位
				int reduceHour = (int)(Math.ceil(-minuteDiff/(float)60)) ;
				hour -= reduceHour ;
				if(hour < 0 ){
					//!已经跨天不支持
					continue ;
				}
				minute = minuteDiff + 60 * reduceHour ;
			}
			StringBuffer cronExpress = new StringBuffer();
			cronExpress.append("0")//秒
					.append(Cat.blank)
					.append(minute)//分钟 转换为int是为了将00变成0
					.append(Cat.blank)
					.append(hour)//小时
					.append(Cat.blank)
					.append(weekExpress);//日期和星期条件
			list.add(cronExpress.toString());
		}
		return list ;
	}
	/**
	 * 获得活动开启前x分钟提示用户
	 * !!!!!
	 * 不支持跨天
	 */
	@Override
	public List<String> getCronExpression(){
		int before = this.menuConfig.getActiveBeforeTimes();
		if(before <=0 ){
			return null ;
		}
		Active active = this.getActive();
		if(null == active){
			return null ;
		}
		List<String> times = active.getTimes();
		String weekTerm = active.getWeekTerm() ;
		return this.getCronExpression(before, times, weekTerm);
	}
	
	@Override
	public void execute(JobExecutionContext paramJobExecutionContext)
			throws JobExecutionException {
		Active active = this.getActive();
		if(null == active){
			return ;
		}
		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
			try{
				if(active.isOutDate() || !active.isSuitLevel(role)){
					//活动过期或者角色不符合活动要求的等级范围
					continue ;
				}
				//通知角色x分钟后将开启此活动
				this.refresh(role);
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
	}
	
	@Override
	protected boolean needRefreshByUpgrade(RoleInstance role){
		//没有等级刷新需求
		return false ;
	}
}
