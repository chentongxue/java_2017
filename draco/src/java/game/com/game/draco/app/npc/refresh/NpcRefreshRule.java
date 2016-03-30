package com.game.draco.app.npc.refresh;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class NpcRefreshRule {
	private int ruleId;
	private String refreshMinTime, refreshMaxTime;//单位为(小时:分)
	private String stopRefreshTime;
	private int deathRefMinTime, deathRefMaxTime; //单位为(秒)
	private int disappear;//停止刷新时消失规则（0：不消失，1：立即消失，2：非战斗消失，3：只刷新一次）
	private int bornChannel;//出生喊话频道
	private int deathChannel;//死亡喊话频道
	private int sayInterval; //活着时的喊话时间间隔(秒)
	private int sayChannel; //活着时的喊话频道
	private String deathContent; //死亡喊话内容
	private String bornContent;//出生喊话内容
	private String content; //活着时的喊话内容

	/* 初始化生成 */
	private static final int DISAPPEAR_NONE = 0; //不消失
	private static final int DISAPPEAR_CLEAR = 1; //立即消失
	private static final int DISAPPEAR_NO_FIGHTING = 2; //非战斗消失
	private static final int DISAPPEAR_ONLYONCE = 3; //只刷新一次
	//定点刷新模式下,最小刷新，最大刷新，停止刷新都是具体的刷新时间点
	private static final int DISAPPEAR_POINT_TIME = 4 ; // 定点刷新
	
	private int refreshMinHour , refreshMinMinute ; //起始时间（小时，分）
	private int refreshMaxHour , refreshMaxMinute ;
	private int stopRefreshHour , stopRefreshMinute ;
	private boolean configInDay = true ; //是否在同一天
	private int[][] pointTimes = null ;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final String format = "yyyy-MM-dd HH:mm:ss";
	
	public void init(){
		//是否定点刷新
		boolean pointTime = (this.disappear == DISAPPEAR_POINT_TIME) ;
		if(pointTime){
			refreshMinHour = -1 ;
			refreshMaxHour = -1 ;
			stopRefreshHour = -1 ;
		}
		
		int notEmptyNum = 0 ;
		if(!Util.isEmpty(refreshMinTime)){
			String[] times = refreshMinTime.split(Cat.colon);
			refreshMinHour = Integer.parseInt(times[0]);
			refreshMinMinute = Integer.parseInt(times[1]);
			notEmptyNum ++ ;
		}
		
		if(!Util.isEmpty(refreshMaxTime)){
			String[] times = refreshMaxTime.split(Cat.colon);
			refreshMaxHour = Integer.parseInt(times[0]);
			refreshMaxMinute = Integer.parseInt(times[1]);
			notEmptyNum ++ ;
		}
		
		if(!Util.isEmpty(stopRefreshTime)){
			String[] times = stopRefreshTime.split(Cat.colon);
			stopRefreshHour = Integer.parseInt(times[0]);
			stopRefreshMinute = Integer.parseInt(times[1]);
			notEmptyNum ++ ;
		}
		if(!pointTime){
			//非定点刷新上面三时间必须都配置
			if(notEmptyNum != 3){
				Log4jManager.CHECK.error("npc refresh rule config error,ruleId=" + this.ruleId);
				Log4jManager.checkFail();
				return ;
			}
			//是否在同一天
			/*this.configInDay = this.compare(this.stopRefreshHour, this.stopRefreshMinute, 
					this.refreshMinHour, this.refreshMinMinute) >= 0;*/
			return ;
		}
		//定点刷新情况
		//必须配置1个
		if(notEmptyNum == 0){
			Log4jManager.CHECK.error("npc refresh rule config error,ruleId=" + this.ruleId);
			Log4jManager.checkFail();
			return ;
		}
		//不能相同,而且必须递增
		int[][] pointTimes = new int[notEmptyNum][2];
		if(this.refreshMinHour >= 0 ){
			pointTimes[0][0] = this.refreshMinHour ;
			pointTimes[0][1] = this.refreshMinMinute ;
		}
		if(this.refreshMaxHour >= 0 ){
			pointTimes[1][0] = this.refreshMaxHour ;
			pointTimes[1][1] = this.refreshMaxMinute ;
		}
		if(this.stopRefreshHour >= 0 ){
			pointTimes[2][0] = this.stopRefreshHour ;
			pointTimes[2][1] = this.stopRefreshMinute ;
		}
		boolean ok = true ;
		for(int i= 1 ; i<pointTimes.length ; i++){
			if(0 > this.compare(pointTimes[i][0], pointTimes[i][1], pointTimes[i-1][0], pointTimes[i-1][1])){
				ok = false ;
			}
		}
		if(!ok){
			Log4jManager.CHECK.error("npc refresh rule config error,ruleId=" + this.ruleId);
			Log4jManager.checkFail();
		}
		this.pointTimes = pointTimes ;
	}
	
	
	//判断是否在同一周期
	private boolean inSameCycle(NpcRefreshTask task){
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_YEAR);
		if (this.configInDay) {
			//没有跨天
			if (day == task.getUpdateDay()) {
				return true;
			}
			return false;
		} 
		//已跨天,用UpdateDay无法判断
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		int nowMinute = now.get(Calendar.MINUTE);
		int now_start = this.compare(nowHour, nowMinute, this.refreshMinHour, this.refreshMinMinute);
		if(day == task.getUpdateDay()){
			//此时必须在刷新点后面
			return  now_start >=0;
		}
		if(day -1 != task.getUpdateDay()){
			return false ;
		}
		//此时必须在刷新点前面
		return now_start < 0 ;
	}
	
	
	private int getPointTimesIndex(int nowHour,int nowMinute,NpcRefreshTask task){
		for(int i=0;i<this.pointTimes.length;i++){
			int value = this.compare(nowHour, nowMinute, pointTimes[i][0], pointTimes[i][1]);
			if(task.getRefreshType() == RefreshType.original){
				if( value <= 0){
					return i ;
				}
			}else{
				if( value < 0){
					return i ;
				}
			}
			
		}
		//-1情况取0 位置值,但天需要+1
		return -1 ;
	}
	
	/**
	 * 定点刷新情况
	 * @param task
	 * @return
	 */
	private boolean canPointTimeRefresh(NpcRefreshTask task){
		if(task.getRefreshType() == RefreshType.death ||
				task.getRefreshType() == RefreshType.disappear){
			task.setRefreshTime(-1);
		}
		if(task.getRefreshTime() <=0){
			//计算下次刷新时间
			Calendar now = Calendar.getInstance();
			int nowHour = now.get(Calendar.HOUR_OF_DAY);
			int nowMinute = now.get(Calendar.MINUTE);
			int index = this.getPointTimesIndex(nowHour, nowMinute,task);
			boolean addDay = (index < 0) ;
			if(addDay){
				index = 0 ;
			}
			now.set(Calendar.HOUR_OF_DAY, this.pointTimes[index][0]);  
			now.set(Calendar.MINUTE, this.pointTimes[index][1]);  
			now.set(Calendar.SECOND, 0);  
			now.set(Calendar.MILLISECOND, 0);  
			if(addDay){
				now.add(Calendar.DAY_OF_YEAR, 1);
			}
			task.setRefreshTime(now.getTimeInMillis());
			task.setRefreshType(RefreshType.doing);
			return false ;
		}
		//判断是否已经到达时间
		return System.currentTimeMillis() >= task.getRefreshTime() ;
	}
	
	//判断此刻能否刷怪
	private boolean hadCanRefresh(NpcRefreshTask task){
		if(task.getRefreshType() == RefreshType.none){
			//NPC已经刷出
			return false;
		}
		if(this.disappear == DISAPPEAR_POINT_TIME){
			//定点刷新情况
			return this.canPointTimeRefresh(task);
		}
		return this.canRefresh(task);
	}
	
	/**
	 * 非定点刷新情况
	 * @param task
	 * @return
	 */
	private boolean canRefresh(NpcRefreshTask task){
		boolean overRefresh = this.overRefresh();
		//停止刷新判断
		if(overRefresh){
			if(null == task.getNpcRefreshRule() 
					|| task.getNpcRefreshRule().getRuleId() == this.ruleId){
				task.setRefreshType(RefreshType.disappear);
			}
			return false;
		} 
		if(!overRefresh 
				&& task.getRefreshType() == RefreshType.disappear){
			task.setRefreshType(RefreshType.init);
		}
		//每个规则只刷新一次的情况
		if(this.disappear == NpcRefreshRule.DISAPPEAR_ONLYONCE){
			if(task.getOnlyOnceRuleId() == this.ruleId 
					&& this.inSameCycle(task)) {
				//在同一周期
				return false ;
			}
			if(task.getRefreshType() == RefreshType.original || task.getRefreshType() == RefreshType.death){
				task.setRefreshType(RefreshType.init);
			}
		}
		
		//原始状态，过了最大刷新时间则走一个死亡周期
		if(task.getRefreshType() == RefreshType.original){
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, this.refreshMaxHour);
			calendar.set(Calendar.MINUTE, this.refreshMaxMinute);
			//当前时间超出最大刷新时间
			if(calendar.getTime().before(new Date())){
				task.setRefreshType(RefreshType.death);
			}else{
				task.setRefreshTime(this.randomInitRefreshTime());
				task.setRefreshType(RefreshType.doing);
			}
		}
		
		//初始状态
		if(task.getRefreshType() == RefreshType.init){
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, this.refreshMaxHour);
			calendar.set(Calendar.MINUTE, this.refreshMaxMinute);
			//当前时间超出最大刷新时间
			if(calendar.getTime().before(new Date())){
				
				task.setRefreshType(RefreshType.doing);
				task.setRefreshTime(System.currentTimeMillis());
				return true ;
			}else{
				task.setRefreshTime(this.randomInitRefreshTime());
				task.setRefreshType(RefreshType.doing);
			}
		}
		//死亡刷新状态
		if(task.getRefreshType() == RefreshType.death){
			long refreshTime = System.currentTimeMillis() 
					+ Util.randomInt(this.deathRefMinTime,this.deathRefMaxTime) * 1000;
			task.setRefreshTime(refreshTime);
			task.setRefreshType(RefreshType.doing);
		}
		//刷新状态
		if(task.getRefreshType() != RefreshType.doing 
				|| System.currentTimeMillis() < task.getRefreshTime()){
			return false;
		}
		return true;
	}
	
	private NpcInstance refresh(NpcRefreshTask task){
		//把刷怪规则放入npcInstance中
		MapInstance mapInstance = task.getMapInstance();
		if(mapInstance == null){
			return null;
		}
		NpcRefreshConfig config = task.getNpcRefreshConfig();
		String npcId = config.getNpcId();
		//int mapX = config.getMapX();
		//int mapY = config.getMapY();
		int mapX = task.getBornPoint().getX();
		int mapY = task.getBornPoint().getY();
		NpcInstance npcInstance = mapInstance.summonCreateNpc(npcId, mapX, mapY);
		if(npcInstance == null){
			return null;
		}
		task.setRefreshType(RefreshType.none);
		task.setNpcRefreshRule(this);//当前npc刷新的规则
		npcInstance.setNpcRefreshTask(task);
		//一条规则只刷新一次的情况
		if(this.disappear == NpcRefreshRule.DISAPPEAR_ONLYONCE){
			task.setOnlyOnceRuleId(this.ruleId);
			task.setUpdateDay(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		}
		//打印刷怪日志
		this.printRefreshLog(task, config, npcInstance.getRoleId());
		
		return npcInstance ;
	}

	
	public void update(NpcRefreshTask task){
		if(!this.hadCanRefresh(task)){
			return ;
		}
		
		NpcInstance npcInstance = this.refresh(task);
		MapInstance mapInstance = task.getMapInstance();
		if(npcInstance == null || mapInstance == null){
			return ;
		}
		
		//刷新时喊话逻辑
		if(!task.isHadBornSpeak()){
			this.speak(this.bornChannel, this.bornContent, npcInstance, mapInstance);
			task.setHadBornSpeak(true);
		}
	}
	
	
	
	//刷新之前喊话逻辑
	public void refreshBeforeSpeak(NpcRefreshTask task){
		ChannelType channelType = ChannelType.getChannelType((byte)this.bornChannel);
		if(channelType == null || 
				(channelType != ChannelType.World 
						&& channelType != ChannelType.System
						&& channelType != ChannelType.Publicize_System)){
			return ;
		}
		
		boolean hadBornSpeak = task.isHadBornSpeak();
		//是否已出生喊话；当前是否可以刷新
		if(hadBornSpeak || !this.hadCanRefresh(task)){
			return ;
		}
		
		NpcRefreshConfig config = task.getNpcRefreshConfig();
		if(config == null){
			return ;
		}
		
		String mapId = config.getMapId();
		String npcId = config.getNpcId();
		if(Util.isEmpty(mapId) || Util.isEmpty(npcId)){
			return ;
		}
		Map map = GameContext.getMapApp().getMap(mapId);
		if(map == null){
			return ;
		}
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
		if(null == npcTemplate){
			return ;
		}
		String mapName = map.getMapConfig().getMapdisplayname();
		String npcName = npcTemplate.getNpcname();
		MapConfig mapconfig = map.getMapConfig();
		//分线地图
		if(mapconfig.isHadLineMap()){
			MapLineContainer  mapLineCotainer = GameContext.getMapApp().getLineContainerMap().get(mapId);
			if(mapLineCotainer == null){
				this.speak(this.bornChannel, this.bornContent, npcName, mapName, "1");
				task.setHadBornSpeak(true);
				return ;
			}
			// 没其他分线实例，喊话
			int instanceSize = mapLineCotainer.getMapInstances().size();
			if(instanceSize == 0){
				this.speak(this.bornChannel, this.bornContent, npcName, mapName, "1");
				task.setHadBornSpeak(true);
				return ;
			}
		}
		
		// 普通地图，无实例、无玩家，喊话
		MapInstance mapInstance = task.getMapInstance();
		if(mapInstance == null || mapInstance.getRoleCount() <= 0){
			this.speak(this.bornChannel, this.bornContent, npcName, mapName, "");
			task.setHadBornSpeak(true);
		}
		//打印喊话日志
		this.printRefreshSpeakLog(task, config);
	}
	
	
	// 刷新之前喊话逻辑
	private void speak(int channleType, String speakContent,
			String npcName, String mapName, String mapLineId){
		
		if(Util.isEmpty(speakContent)){
			return ;
		}
		
		//替换通配符
		String content = this.replaceWildcards(speakContent, npcName, mapName, mapLineId);
		ChannelType channelType = ChannelType.getChannelType((byte)channleType);
		NpcInstance npcInstance = new NpcInstance();
		//npcInstance.setNpcname(npcName);
		npcInstance.setRoleId("-1");
		GameContext.getChatApp().sendSysMessage(npcInstance, channelType, content, null, null);
	}
	
	// 循环喊话
	public void loopSpeak(NpcInstance npcInstance){
		if(Util.isEmpty(this.content) 
				|| this.sayInterval <= 0
				|| this.sayChannel <= 0){
			return ;
		}
		
		if(npcInstance == null){
			return ;
		}
		long lastSpeakTime = npcInstance.getLastSpeakTime();
		if(lastSpeakTime == 0){
			lastSpeakTime = System.currentTimeMillis();
			npcInstance.setLastSpeakTime(lastSpeakTime);
			return ;
		}
		MapInstance mapInstance = npcInstance.getMapInstance();
		if(mapInstance == null){
			return ;
		}
		long currentTime = System.currentTimeMillis();
		if(currentTime > (lastSpeakTime + this.sayInterval * 1000)){
			this.speak(this.sayChannel, this.content, npcInstance, mapInstance);
			lastSpeakTime = System.currentTimeMillis();
			npcInstance.setLastSpeakTime(lastSpeakTime);
		}
	}
	
	
	//喊话
	public void speak(int channleType, String speakContent, NpcInstance npcInstance, MapInstance mapInstance){
		ChannelType channelType = ChannelType.getChannelType((byte)channleType);
		if(channelType == null){
			return ;
		}
		if(Util.isEmpty(speakContent) 
				|| npcInstance == null
				|| mapInstance == null ){
			return ;
		}
		
		//记录喊话数目
		npcInstance.addSpeakCount();
		
		//替换通配符
		String content = this.replaceWildcards(speakContent, npcInstance, mapInstance);
		Serializable target = null;
		if(channelType == ChannelType.Map
				|| channelType == ChannelType.Speak){
			target = mapInstance;
		}
		else if(channelType == ChannelType.System){
			target = npcInstance;
		}
		GameContext.getChatApp().sendSysMessage(npcInstance, channelType, content, null, target);
	}
	
	
	
	
	//替换通配符逻辑
	private String replaceWildcards(String content, NpcInstance npcInstance, 
			MapInstance mapInstance){
		
		String npcName = npcInstance.getNpcname();
		String sayCount = String.valueOf(npcInstance.getSpeakCount());
		String mapName = mapInstance.getMap().getMapConfig().getMapdisplayname();
		String systemNowTime = DateUtil.date2FormatDate(new Date(),"yyyy-MM-dd HH:mm");
		
		String mapLine = "";
		if(mapInstance.getLineId() > 0){
			mapLine = String.valueOf(mapInstance.getLineId());
		}
		
		String randomRoleName = "";
		for(RoleInstance roleInstance : mapInstance.getRoleList()){
			if(roleInstance != null){
				randomRoleName = roleInstance.getRoleName();
				break;
			}
		}
		
		return content.replace(Wildcard.NpcName, npcName)
					.replace(Wildcard.Npc_Say_Count, sayCount)
					.replace(Wildcard.Current_Map_Name, mapName)
					.replace(Wildcard.Random_Role_Name, randomRoleName)
					.replace(Wildcard.System_DateTime, systemNowTime)
					.replace(Wildcard.Current_Map_LineId, mapLine);
	}
	
	
	
	
	private String replaceWildcards(String content, String npcName, 
			String mapName, String mapLineId){
		String systemNowTime = DateUtil.date2FormatDate(new Date(),"yyyy-MM-dd HH:mm");
		String randomRoleName = "";
		return content.replace(Wildcard.NpcName, npcName)
					.replace(Wildcard.Current_Map_Name, mapName)
					.replace(Wildcard.Random_Role_Name, randomRoleName)
					.replace(Wildcard.System_DateTime, systemNowTime)
					.replace(Wildcard.Current_Map_LineId, mapLineId);
	}
	
	
	// npc结束刷新时是否消失
	public void npcDisappear(NpcInstance npc){
		if (npc == null) {
			return;
		}
		if (this.disappear == NpcRefreshRule.DISAPPEAR_NONE) {
			return;
		}
		if (!this.overRefresh()) {
			return;
		}
		NpcRefreshTask task = npc.getNpcRefreshTask();
		if (this.disappear == NpcRefreshRule.DISAPPEAR_CLEAR
				|| this.disappear == NpcRefreshRule.DISAPPEAR_ONLYONCE) {
			task.setRefreshType(RefreshType.disappear);
			npc.getBehavior().death(null);
			task.setHadBornSpeak(false);
			return;
		}
		if (this.disappear == NpcRefreshRule.DISAPPEAR_NO_FIGHTING) {
			if (npc.getTarget() == null) {
				task.setRefreshType(RefreshType.disappear);
				npc.getBehavior().death(null);
				task.setHadBornSpeak(false);
			}
		}
	}
	
	private  int	compare(int h1,int m1,int h2,int m2){
		if(h1 > h2){
			return 1 ;
		}
		if(h1 < h2){
			return -1 ;
		}
		if(m1 == m2){
			return 0 ;
		}
		return m1 > m2?1:-1 ;
	}
	
	
	private boolean inTimeNow() {
		//需要支持跨天,类似下面两情况
		//12:10 - 20:30
		//21:00 - 12:30
		
		Calendar now = Calendar.getInstance();
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		int nowMinute = now.get(Calendar.MINUTE);
		
		//停止刷新时间和最大刷新时间是否跨天
		int now_start = this.compare(nowHour, nowMinute,refreshMinHour,refreshMinMinute) ;
	    int now_stop = this.compare(nowHour,nowMinute,stopRefreshHour, stopRefreshMinute) ;
		if(this.configInDay){
			//在同一天
			return now_start >=0 && now_stop <=0 ;
		}
		//不在同一天
		if(now_start >=0){
			return true ;
		}
		//现在时间在停止时间的前面
		return now_stop < 0 ;
	}
	
	private boolean overRefresh() {
		return !this.inTimeNow() ;
		/*Calendar now = Calendar.getInstance();
		if (stopRefreshHour <= 0) {
			return false;
		}
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		int nowMinute = now.get(Calendar.MINUTE);
		if (nowHour > stopRefreshHour) {
			return true;
		}
		if (nowHour == stopRefreshHour && nowMinute > stopRefreshMinute) {
			return true;
		}
		return false;*/
	}
	
	private long randomInitRefreshTime(){
		int differenceTime = 0;
		//当前时间大于起始刷新时间
		//取当前时间计算时间差
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, this.refreshMinHour);
		calendar.set(Calendar.MINUTE, this.refreshMinMinute);
		
		Date now = new Date();
		if(now.after(calendar.getTime())){
			calendar.setTime(now);
			differenceTime = this.buildRefreshDifferenceTime(calendar.get(Calendar.HOUR_OF_DAY), 
					this.refreshMaxHour, this.refreshMaxMinute,calendar.get(Calendar.MINUTE));
			
		}else{
			differenceTime = this.buildRefreshDifferenceTime(this.refreshMinHour, 
					this.refreshMaxHour, this.refreshMaxMinute, this.refreshMinMinute);
		}
		int randomDiff = Util.randomInt(0, differenceTime);
		calendar.add(Calendar.MINUTE, randomDiff);
		
		return calendar.getTimeInMillis();
	}
	

	//计算刷新时间差
	private int buildRefreshDifferenceTime(int refreshMinHour, int refreshMaxHour,
			int refreshMaxMinute, int refreshMinMinute){
		int diffTime = 0;
		int hourDiff = refreshMaxHour - refreshMinHour;
		if(hourDiff > 0){
			diffTime = hourDiff * 60 ;//分
		}
		int minuteDiff = refreshMaxMinute - refreshMinMinute;
		diffTime += minuteDiff;
		
		return diffTime;
	}
	
	private void printRefreshLog(NpcRefreshTask task, NpcRefreshConfig config, String npcRoleId){
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(config.getId());
			sb.append(Cat.pound);
			sb.append(this.ruleId);
			sb.append(Cat.pound);
			sb.append(config.getNpcId());
			sb.append(Cat.pound);
			sb.append(npcRoleId);
			sb.append(Cat.pound);
			sb.append(config.getMapId());
			sb.append(Cat.pound);
			sb.append(task.getMapInstance().getInstanceId());
			sb.append(Cat.pound);
			sb.append(task.getBornPoint().getX());
			sb.append(Cat.pound);
			sb.append(task.getBornPoint().getY());
			sb.append(Cat.pound);
			sb.append(DateUtil.date2FormatDate(task.getRefreshTime(), format));
			Log4jManager.NPC_REFRESH_LOG.info(sb.toString());
		}catch(Exception e){
			logger.error("printRefreshLog error", e);
		}
	}
	
	private void printRefreshSpeakLog(NpcRefreshTask task, NpcRefreshConfig config){
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(config.getId());
			sb.append(Cat.pound);
			sb.append(this.ruleId);
			sb.append(Cat.pound);
			sb.append(config.getNpcId());
			sb.append(Cat.pound);
			sb.append(config.getMapId());
			sb.append(Cat.pound);
			sb.append(task.getBornPoint().getX());
			sb.append(Cat.pound);
			sb.append(task.getBornPoint().getY());
			sb.append(Cat.pound);
			sb.append(DateUtil.date2FormatDate(task.getRefreshTime(), format));
			Log4jManager.NPC_REFRESH_SPEAK_LOG.info(sb.toString());
		}catch(Exception e){
			logger.error("printBeforeRefreshSpeakLog error", e);
		}
	}
}
