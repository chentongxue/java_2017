package sacred.alliance.magic.app.active.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.active.ActiveInitContext;
import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActivePanelDetailWayPointItem;
import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;

public @Data class Active {
	
	private short id;//活动ID
	private byte type;//活动类型
	private String name;//活动名称
	private String desc;//活动描述
	private String startDateAbs;//绝对开启日期
	private String endDateAbs;//绝对结束日期
	private int startDateRel;//相对开启日期
	private int endDateRel;//相对结束日期
	private String weekTerm;//星期条件 [空:日常0~6：周日~周六(例：1,2,3)]
	private String timeRange1;//开启具体时间范围 [例 13:00-14:00]
	private String timeRange2;
	private String resetTime;//重置时间 [15:00]
	private byte resetCycle;//重置周期 [天]
	private byte freeCounts;//周期内免费次数
	private byte extraCounts;//周期内额外次数
	private short minLevel;//最小角色等级
	private short maxLevel;//最大角色等级
	private String rewardGoods;//活动奖励id列表
	private String parameter;//
	private String npcIdStr;//活动NPC模板ID,多个NPC用,分割
	private String enterPointStr;//活动入口坐标
	private byte timeLimit;//活动类型（日常、周长、限时）
	private short showLevel;//可显示等级
	private short iconId;//活动图标
	private short maxNum;//最大次数
	
	//================================================
	private Date startDate;
	private Date endDate;
	private List<String> times = new ArrayList<String>();
	private StringBuffer timeRegions = new StringBuffer();
	private List<Integer> rewardGoodsList = new ArrayList<Integer>();
	private List<Point> enterPointList = new ArrayList<Point>();
	private List<String> npcIdList = new ArrayList<String>() ;
	
	
	private Result initNpcId(String info){
		Result result = new Result();
		if(Util.isEmpty(this.npcIdStr)){
			return result.success();
		}
		// 构建NPC
		String[] strs = this.npcIdStr.split(Cat.comma);
		int num = strs.length;
		if (1 != num && 3 != num) {
			return result.setInfo(info + "The npcIdStr config error!");
		}
		for(String npcId : strs){
			this.npcIdList.add(npcId);
		}
		result.success();
		return result ;
	}
	
	private Result initEnterPoint(String info){
		Result result = new Result();
		if(Util.isEmpty(this.enterPointStr)){
			return result.success();
		}
		// 构建活动进入点
		String[] pointStrs = this.enterPointStr.split(Cat.comma);
		int pointNum = pointStrs.length;
		if (1 != pointNum && 3 != pointNum) {
			return result.setInfo(info + "The enterPointStr config error!");
		}
		for (String pointInfo : pointStrs) {
			if (null == pointInfo) {
				continue;
			}
			String[] infos = pointInfo.split(Cat.colon);
			if (3 != infos.length) {
				return result.setInfo(info + "The enterPointStr config error!");
			}
			this.enterPointList.add(new Point(infos[0], Integer
					.valueOf(infos[1]), Integer.valueOf(infos[2])));
		}
		result.success();
		return result ;
	}
	/** 检测并初始化活动配置 */
	public Result checkInit(ActiveInitContext initContext){
		Result result = new Result();
		String info = "activeId=" + this.id + ",type=" + type + ".";
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
			//构建活动进入点
			result = this.initEnterPoint(info);
			if(!result.isSuccess()){
				return result ;
			}
			result.failure();
			//构建NPC
			result = this.initNpcId(info);
			if(!result.isSuccess()){
				return result ;
			}
			result.failure();
			
			//验证参数
			result = this.checkParameter(info,initContext);
			if(!result.isSuccess()){
				return result ;
			}
			result.failure();
			
			//构建开启时间信息
			this.buildTimes();
			this.initRewardGoods();
			return result.success();
		}catch(Exception e){
			return result.setInfo(info + "catch exception: " + e.toString());
		}
	}
	
	private Result checkParameter(String info, ActiveInitContext initContext) {
		Result result = new Result();
		if (ActiveType.ActiveMap.getType() == this.type) {
			// 活动地图,参数为地图ID
			if (Util.isEmpty(this.parameter)) {
				return result.setInfo(info + " the paramerter not config");
			}
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp()
					.getMap(this.parameter);
			if (null == map) {
				return result.setInfo(info
						+ " the paramerter config error,the map:"
						+ this.parameter + " not exist");
			}
			if (initContext.getActiveMapSet().contains(this.parameter)) {
				//一个地图配置在多个活动中
				return result.setInfo(info
						+ " the paramerter config error,mapId:"
						+ this.parameter + " in too many active");
			}
			//!!! 设置地图逻辑类型
			if (!map.getMapConfig().changeLogicType(MapLogicType.activeMap)) {
				return result.setInfo(info
						+ " the paramerter config error,mapId:"
						+ this.parameter + " the logicType is "
						+ map.getMapConfig().getLogictype() + " now ");
			}
			initContext.getActiveMapSet().add(this.parameter);
		}
		result.success();
		return result;
	}
	
	private void initRewardGoods(){
		if(Util.isEmpty(this.rewardGoods)){
			return ;
		}
		String[] ids = this.rewardGoods.split(Cat.comma);
		for(String id : ids){
			if(!Util.isNumeric(id)){
				Log4jManager.CHECK.error("active reward goods config error(not numeric),activeId=" + this.id + " goodsId=" + id);
				Log4jManager.checkFail();
				continue ;
			}
			int goodsId = Integer.parseInt(id);
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb){
				Log4jManager.CHECK.error("active reward goods config error(not exist),activeId=" + this.id + " goodsId=" + id);
				Log4jManager.checkFail();
				continue ;
			}
			this.rewardGoodsList.add(goodsId);
		}
	}
	
	/** 构建开启时间信息 */
	private void buildTimes(){
		boolean isWholeDayOpen = true;//三个时间范围都为空，表示全天开放
		if(!Util.isEmpty(this.weekTerm)){
			this.timeRegions.append(GameContext.getI18n().getText(TextId.Active_Week));
			String cat = "" ;
			for(String s : Util.splitString(this.weekTerm.trim())){
				int week = Integer.parseInt(s);
				this.timeRegions.append(cat);
				this.timeRegions.append(DateUtil.getWeekName(week));
				cat = "," ;
			}
			this.timeRegions.append(" ");
		}
		if(!StringUtil.nullOrEmpty(this.timeRange1)){
			this.times.add(this.timeRange1);
			this.timeRegions.append(this.timeRange1);
			isWholeDayOpen = false;
		}
		if(!StringUtil.nullOrEmpty(this.timeRange2)){
			this.times.add(this.timeRange2);
			this.timeRegions.append("，" + this.timeRange2);
			isWholeDayOpen = false;
		}
		if(isWholeDayOpen){
			this.timeRegions.append(GameContext.getI18n().getText(TextId.Active_All_Day));
		}
	}
	
	/** 是否符合活动开启时间 */
	public boolean isTimeOpen(){
		//活动过期
		if(this.isOutDate()){
			return false;
		}
		if(!this.isDayNowActive()){
			return false;
		}
		if(!this.isTimeNowActive()){
			return false;
		}
		return true;
	}
	
	/**
	 * 活动是否过期
	 * */
	public boolean isOutDate(){
		Date now = new Date();
		return now.before(this.startDate) || now.after(this.endDate);
	}
	
	/**
	 * 是否日常活动
	 * */
	private boolean isDayActive(){
		return StringUtil.nullOrEmpty(weekTerm);
	}
	
	/**
	 * 是否周活动
	 * */
	public boolean isWeekActive(int week){
		if(StringUtil.nullOrEmpty(weekTerm)){
			return true ;
		}
		if(weekTerm.indexOf(String.valueOf(week)) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否满足等级要求
	 * */
	public boolean isSuitLevel(RoleInstance role){
		if(null == role){
			return false;
		}
		return role.getLevel()>=minLevel && role.getLevel()<=maxLevel;
	}
	
	/**
	 * 是否今日可接活动(未校验等级)
	 * */
	public boolean isDayNowActive(){
		if(this.isDayActive()){
			return true;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		String weekOfDay = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
		if(weekTerm.indexOf(weekOfDay) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 当前时间是否可接
	 * */
	private boolean isTimeNowActive(){
		if(Util.isEmpty(this.times)){
			return true ;
		}
		Date now = new Date();
		for(String timeRegion : this.times){
			if(DateUtil.inOpenTime(now,timeRegion)){
				return true;
			}
		}
		return false;
	}
	
	/**得到满足条件的结束时间*/
	public Date getActiveEndTime(){
		if(!this.isDayNowActive()){
			return null;
		}
		if(Util.isEmpty(this.times)){
			return this.endDate;
		}
		Date now = new Date();
		for(String timeRegion : this.times){
			if(DateUtil.inOpenTime(now, timeRegion)){
				return packDateTime(timeRegion, false);
			}
		}
		return null;
	}
	
	/**得到满足条件的开始时间*/
	public Date getActiveStartTime(){
		if(!this.isDayNowActive()){
			return null;
		}
		if(Util.isEmpty(this.times)){
			return this.startDate;
		}
		Date now = new Date();
		for(String timeRegion : this.times){
			if(DateUtil.inOpenTime(now, timeRegion)){
				return packDateTime(timeRegion, true);
			}
		}
		return null;
	}
	
	public Date packDateTime(String timeRegion,boolean isStart){
		String[] info = timeRegion.split(Cat.strigula);
		String[] time = null;
		time = isStart?info[0].split(Cat.colon):info[1].split(Cat.colon);
		int hour = Integer.valueOf(time[0]);
		int minute = Integer.valueOf(time[1]);
		Calendar c = Calendar.getInstance(); //获取当前日期 
		c.set(Calendar.HOUR_OF_DAY,hour);
		c.set(Calendar.MINUTE,minute);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}

	/**
	 * 活动状态
	 * */
	public ActiveStatus getStatus(RoleInstance role){
		if(!this.isSuitLevel(role)){
			return ActiveStatus.NotOpen;
		}
		ActiveSupport activeSupp = this.getActiveSupport();
		if(null == activeSupp || !this.isTimeOpen()){
			return ActiveStatus.NotOpen;
		}
		activeSupp.checkReset(role, this);
		return activeSupp.getActiveStatus(role,this);
	}
	
	/**
	 * 获取红点提示
	 * @param role
	 * @return
	 */
	public byte getActiveHint(RoleInstance role) {
		ActiveSupport activeSupp = this.getActiveSupport();
		return activeSupp.getActiveHint(role, this) ? (byte) 1 : (byte) 0;
	}
	
	/** 根据请求的类型 判断活动是否可以显示 */
	public boolean canDisplay(RoleInstance role/*, ActiveClassifyType classifyType*/){
		ActiveSupport activeSupport = this.getActiveSupport();
		if(null == activeSupport){
			return false;
		}
		//活动过期或角色等级超过活动最大等级
		if( role.getLevel() > this.maxLevel || activeSupport.isOutDate(this)){
			return false;
		}
		return true;
	}
	
	/** 活动参与度的日志 */
	public void outputLog(RoleInstance role){
		GameContext.getStatLogApp().activePartLog(role, this.id, this.name, this.type);
	}
	
	private ActiveSupport getActiveSupport(){
		return GameContext.getActiveApp().getActiveSupport(type);
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
			if(!Util.isEmpty(this.weekTerm)){
				weekExpress = "? * " + this.weekTerm;//表示每周的周几执行
			}
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
		//活动的重置时间，每天的几点几分
		if(!Util.isEmpty(this.resetTime)){
			String[] values = this.resetTime.split(Cat.colon);
			String rsExpress = "0 " + Integer.valueOf(values[1]) + " " + Integer.valueOf(values[0]) + " ? * *";
			list.add(rsExpress);
		}
		return list;
	}
	
	/**
	 * 获取活动的进入点
	 * @param campId 阵营ID
	 * @return
	 */
	public Point getEnterPoint(int campId){
		int size = this.enterPointList.size();
		if(0 == size){
			return null ;
		}
		if(1 == size){
			return this.enterPointList.get(0);
		}
		return this.enterPointList.get(campId);
	}
	
	/**
	 * 获取活动进入点
	 * 多个进入点会随机获得一个
	 * @return
	 */
	public Point getEnterPoint(){
		int size = this.enterPointList.size();
		if(size <= 0){
			return null;
		}
		if(1 == size){
			return this.enterPointList.get(0);
		}
		int index = RandomUtil.randomInt(size);
		return this.enterPointList.get(index);
	}
	
	public String getNpcId(int campId){
		int size = this.npcIdList.size();
		if(0 == size){
			return null ;
		}
		if(1 == size){
			return this.npcIdList.get(0);
		}
		return this.npcIdList.get(campId);
	}
	/**
	 * 活动详情的默认信息
	 * 有寻路和传送功能的活动
	 * @param role
	 * @return
	 */
	public C2301_ActivePanelDetailRespMessage getDefaultPanelDetailMessage(RoleInstance role){
		ActivePanelDetailWayPointItem item = new ActivePanelDetailWayPointItem();
		//公用赋值
		GameContext.getActiveApp().buildActivePanelDetailBaseItem(item, this);
		item.setNpcName(GameContext.getNpcApp().getNpcTemplate(this.getNpcId(role.getCampId())).getNpcname());
		item.setNpcId(this.getNpcId(role.getCampId()));
		Point point = this.getEnterPoint(role.getCampId());
		if(null != point){
			item.setMapId(point.getMapid());
			item.setMapX((short) point.getX());
			item.setMapY((short) point.getY());
		}
		C2301_ActivePanelDetailRespMessage message = new C2301_ActivePanelDetailRespMessage();
		message.setDetailItem(item);
		return message;
	}
	
}
