package sacred.alliance.magic.app.carnival;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

public @Data class CarnivalActive implements KeySupport<Short>{
	private static final String ALL = "-1" ;
	private short id;//活动ID
	private String name;//活动名称
	private String desc;//活动描述
	private String startDateAbs;//绝对开启日期
	private String endDateAbs;//绝对结束日期
	private int startDateRel;//相对开启日期
	private int endDateRel;//相对结束日期
	
	//================================================
	private Date startDate;
	private Date endDate;
	private List<String> times = new ArrayList<String>();
	private Set<String> serverIdSet = new HashSet<String>();
	private String serverId = "" ;
	
	/** 检测并初始化活动配置 */
	public Result checkInit(){
		Result result = new Result();
		String info = "activeId=" + this.id + ".";
		try{
			//转换时间格式
			DateTimeBean bean = DateConverter.getDateTimeBean(this.startDateRel, this.endDateRel, this.startDateAbs, this.endDateAbs, FormatConstant.DEFAULT_YMD);
			if(null == bean){
				return result.setInfo(info + "The startDateRel/endDateRel/startDateAbs/endDateAbs is error.");
			}
			this.startDate = bean.getStartDate();
			this.endDate = bean.getEndDate();
			if(null == this.startDate || null == this.endDate){
				return result.setInfo(info + "Please config the start time or the end time.");
			}
			//给时间字符串赋值
			this.startDateAbs = DateUtil.date2Str(this.startDate, FormatConstant.DEFAULT_YMD);
			this.endDateAbs = DateUtil.date2Str(this.endDate, FormatConstant.DEFAULT_YMD);
			return result.success();
		}catch(Exception e){
			return result.setInfo(info + "catch exception: " + e.toString());
		}
	}
	
	/**
	 * 是否符合活动开启时间
	 * */
	public boolean isTimeOpen(){
		Date now = new Date();
		return now.after(this.startDate) && now.before(this.endDate);
	}
	
	/**
	 * 获取定时任务的时间表达式
	 * 活动开始时间、活动结束时间、活动重置时间
	 * @return
	 */
	public List<String> getCronExpression(){
		List<String> list = new ArrayList<String>();
		//活动的开始时间和结束时间
		if(!Util.isEmpty(this.times)){
			String weekExpress = "? * *";//表示每天都执行
			for(String timeRange : this.times){
				String[] timeStrs = timeRange.split(Cat.strigula);
				for(String timeStr : timeStrs){
					String[] timeValue = timeStr.split(Cat.colon);
					StringBuffer cronExpress = new StringBuffer();
					cronExpress.append("0")//秒
						.append(Cat.blank)
						.append(Integer.valueOf(timeValue[1]))//分钟 转换为int是为了将00变成0
						.append(Cat.blank)
						.append(Integer.valueOf(timeValue[0]))//小时
						.append(Cat.blank)
						.append(weekExpress);//日期和星期条件
					list.add(cronExpress.toString());
				}
			}
		}else{
			list.add("10 0 0 * * ?");//每天凌晨过10秒
		}
		return list;
	}
	
	public void initServerId(){
		if(!Util.isEmpty(this.serverId)){
			String[] serverIds = this.serverId.trim().split(Cat.comma);
			for(String s : serverIds){
				if(Util.isEmpty(s)){
					continue ;
				}
				this.serverIdSet.add(s.trim());
			}
		}
	}
	
	public boolean isServerCanShow(){
		return this.serverIdSet.contains(ALL) 
			|| this.serverIdSet.contains(String.valueOf(GameContext.getServerId()));
	}
	
	private boolean isAfterEndDate(){
		Date now = new Date();
		return now.after(this.endDate);
	}

	@Override
	public Short getKey() {
		return this.id;
	}
	
	public boolean isCanOpen(){
		return isServerCanShow() && !isAfterEndDate();
	}
}
