package com.game.draco.app.rank;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.rank.domain.RankGroup;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLayout;
import com.game.draco.app.rank.domain.RankLogData;
import com.game.draco.app.rank.domain.RankLogRoleInfo;
import com.game.draco.app.rank.domain.RankReward;
import com.game.draco.app.rank.domain.RankRewardRank;
import com.game.draco.app.rank.domain.RankRewardRole;
import com.game.draco.app.rank.domain.RankRewardTime;
import com.game.draco.app.rank.domain.RankWorld;
import com.game.draco.app.rank.logic.RankLogic;
import com.game.draco.app.rank.type.RankActorType;
import com.game.draco.app.rank.type.RankFilterType;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.RankDetailItem;

public class RankAppImpl implements RankApp{
	private final static String CAT = "_";
	private final static String CAT_LOG = "#";
	private final static String LOG_FILE_SUBFIX = ".log";
	private final static String ALL = "-1"; //camp:-1表示不分阵营，gender:-1表示不分男女
	public final static String LOG_LAST = "last";
	public final static String LOG_REWARD_SUCESS = "sucess";
	public final static String LOG_REWARD_FAIL = "fail";
	public final static long ONE_HOUR = 60*60*1000;
	private final static long REWARD_SLEEP = 10;
	private final static String MAIL_DATE_FORMAT = "yyyy-MM-dd HH";
	private final static String FORMAT = "yyyy-MM-dd-HH";
	private final static SimpleDateFormat RANK_DATE_FORMAT = new SimpleDateFormat(FORMAT);
	private final static String URL_PAGE_DATA = "${serverAddr}/data/${appid}/${serverid}/${rankid}/last/${pageindex}";
	private final static String URL_ROLESORT_DATA = "${serverAddr}/rolesort/${appid}/${serverid}/${rankid}/${roleId}";
	private final static String URL_SORT_DATA = "${serverAddr}/sort/${appid}/${serverid}/${rankid}/${date}";
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 系统中世界排行榜组列表
	 */
	private List<RankGroup> rankGroupList = new ArrayList<RankGroup>();
	/**
	 * 所有的排行榜
	 */
	private Map<Integer, RankInfo> allRankInfoMap = null;
	
	/**
	 * 排行榜布局
	 */
	private Map<String, RankLayout> rankLayoutMap = null;
	
	private Map<String, RankReward> allRewardMap = new HashMap<String, RankReward>();
	/**
	 * key:<rankId_camp_gender>
	 * value:List<RankRewardRole> 
	 * 更加角色等级区分
	 */
	private Map<String, List<RankRewardRole>> rewardRoleListMap = new HashMap<String, List<RankRewardRole>>();
	/**
	 * key: roleKey
	 * value: 根据排名次数区分
	 */
	private Map<String, List<RankRewardRank>> rewardRankListMap = new HashMap<String, List<RankRewardRank>>();
	//活动排行榜
	private List<RankInfo> activeRankList = new ArrayList<RankInfo>();
	//spring 
	private String logLayout; //"%d %p [%C] - <%m>%n";
	private String logEncoding; //"UTF-8";
	private String logDatePat; //"'.'yyyy-MM-dd-HH-mm";
	private String logFilePath;
	
	public String getLogLayout() {
		return logLayout;
	}

	public void setLogLayout(String logLayout) {
		this.logLayout = logLayout;
	}

	public String getLogEncoding() {
		return logEncoding;
	}

	public void setLogEncoding(String logEncoding) {
		this.logEncoding = logEncoding;
	}

	public String getLogDatePat() {
		return logDatePat;
	}

	public void setLogDatePat(String logDatePat) {
		this.logDatePat = logDatePat;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	@Override
	public RankInfo getRankInfo(int rankId){
		return this.allRankInfoMap.get(rankId);
	}
	
	@Override
	public void addActiveRank(RankInfo rankInfo){
		if(null == rankInfo){
			return ;
		}
		this.activeRankList.add(rankInfo);
	}
	
	
	public RankReward getRankReward(String key){
		if(Util.isEmpty(key)){
			return null ;
		}
		return this.allRewardMap.get(key);
	}
	
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadRankLayout(); //rank_list.xls-->rank_layout    Map<String, RankLayout> rankLayoutMap
		this.loadRankInfo();   //rank_list.xls-->list           Map<Integer, RankInfo> allRankInfoMap
		this.loadRankWorld();  //rank_list.xls-->world_ranks    List<RankGroup> rankGroupList
		//加载奖励
		this.loadRandkReward();//rank_list.xls-->reward         Map<String, RankReward> allRewardMap
		this.loadRankRewardRank();//rank_list.xls-->rewardRank  Map<String, List<RankRewardRank>> rewardRankListMap
		this.loadRankRewardRole();//rank_list.xls-->rewardRole  Map<String, List<RankRewardRole>> rewardRoleListMap
		//初始化组件
		this.initReward();
		this.initLogger();
	}

	@Override
	public void stop() {
		
	}
	
	private void initReward(){
		//XLS表配置检验RankRewardRole.roleKey∈RankRewardRank.key
		//判断由排行榜id，页签下标，阵营，性别决定的roleLevelkey在rewardLevel中是否存在
		for(List<RankRewardRole> rewardRoleList : rewardRoleListMap.values()){
			if(Util.isEmpty(rewardRoleList)){
				continue ;
			}
			for(RankRewardRole rewardRole : rewardRoleList){
				if(null == rewardRole){
					continue;
				}
				String roleKey = rewardRole.getRoleKey();
				if(Util.isEmpty(roleKey)){
					continue ;
				}
				if(!rewardRankListMap.containsKey(roleKey)){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("rankApp init error: roleKey=" + roleKey + " do not exsit in tag rewardRank");
				}
			}//list
		}//map
		//RankRewardRank.rankKey∈RankReward.rankKey
		//判断上面逻辑中参数的roleKey和rewardRank表里面的rankStart, rankEnd，决定的rankKey在reward表中是否存在
		for(String roleKey : rewardRankListMap.keySet()){
			List<RankRewardRank> rewardRankList = rewardRankListMap.get(roleKey);
			if(Util.isEmpty(rewardRankList)){
				continue;
			}
			for(RankRewardRank rewardRank : rewardRankList){
				if(null == rewardRank){
					continue;
				}
				String rankKey = rewardRank.getRankKey();
				if(Util.isEmpty(rankKey)){
					continue ;
				}
				if(!allRewardMap.containsKey(rankKey)){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("rankApp init error: rankKey=" + rankKey + " do not exsit in tag reward");
				}
			}
		}
	}
	
	private void initLogger(){
		//logger 对象配置
		if(Util.isEmpty(logFilePath)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("rank logFilePath no config in spring");
		}
		for(RankInfo rankInfo : this.allRankInfoMap.values()){
			if(null == rankInfo){
				continue;
			}
			int rankId = rankInfo.getId();
			if(rankId <= 0){
				continue ;
			}
			RankType rankType = RankType.get(rankInfo.getType());
			if(null == rankType){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("rankApp init error: rankId=" + rankId + ", rankType do not exsit");
				continue;
			}
			//判断布局是否存在
			if(null == this.getRankLayout(rankInfo.getType())){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("rank layout conifg error,layout not exist: rankType=" + rankInfo.getType());
			}
			//初始化
			if(!rankInfo.init())//初始化奖励时间
			{
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("rankApp init error: rankId=" + rankId + ", rewardTime do not correct");
			}
			rankInfo.setRankType(rankType);
			Logger rankLogger = getLoggerFile(rankInfo);
			rankInfo.setLogger(rankLogger);
			String schedulerLoggerFileName = getLogFileName(rankInfo);
			rankInfo.setSchedulerLoggerFileName(schedulerLoggerFileName);
			//启动时候切换下日志
			rankType.createRankLogic(rankInfo).switchLog(rankInfo);
			//
		}
		
	}
	
	/**
	 * 加载排行榜奖励
	 */
	private void loadRandkReward(){
		String fileName = XlsSheetNameType.rank_reward.getXlsName();
		String sheetName = XlsSheetNameType.rank_reward.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allRewardMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, RankReward.class);
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(allRewardMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		for(RankReward rankReward : allRewardMap.values()){
			if(null == rankReward){
				continue;
			}
			rankReward.init();
		}
	}
	
	private void loadRankRewardRank(){
		String fileName = XlsSheetNameType.rank_rewardRank.getXlsName();
		String sheetName = XlsSheetNameType.rank_rewardRank.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<RankRewardRank> xlsRewardRankList = XlsPojoUtil.sheetToList(sourceFile, sheetName, RankRewardRank.class);
			for(RankRewardRank rewardRank : xlsRewardRankList){
				if(null == rewardRank){
					continue;
				}
				List<RankRewardRank> rewardRoleList = rewardRankListMap.get(rewardRank.getRoleKey());
				if(null == rewardRoleList){
					rewardRoleList = new ArrayList<RankRewardRank>();
					rewardRankListMap.put(rewardRank.getRoleKey(), rewardRoleList);
				}
				rewardRoleList.add(rewardRank);
			}
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(rewardRankListMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
	}
	
	private void loadRankRewardRole(){
		String fileName = XlsSheetNameType.rank_rewardRole.getXlsName();
		String sheetName = XlsSheetNameType.rank_rewardRole.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<RankRewardRole> xlsRewardRoleList = XlsPojoUtil.sheetToList(sourceFile, sheetName, RankRewardRole.class);
			for(RankRewardRole rewardRole : xlsRewardRoleList){
				if(null == rewardRole){
					continue;
				}
				int rankId = rewardRole.getRankId();
				if(rankId <= 0){
					continue ;
				}
				//1_0_1
				String cgKey = rewardRole.getKey() ;
				List<RankRewardRole> rewardRoleList = rewardRoleListMap.get(cgKey);
				if(null == rewardRoleList){
					rewardRoleList = new ArrayList<RankRewardRole>();
					rewardRoleListMap.put(cgKey, rewardRoleList);
				}
				rewardRoleList.add(rewardRole);
			}
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(rewardRoleListMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		
	}
	
	private void loadRankLayout(){
		String fileName = XlsSheetNameType.rank_layout.getXlsName();
		String sheetName = XlsSheetNameType.rank_layout.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			rankLayoutMap = XlsPojoUtil.sheetToMap(sourceFile, sheetName, RankLayout.class);
			for(RankLayout layout : rankLayoutMap.values()){
				layout.init() ;
			}
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	@Override
	public RankLayout getRankLayout(int rankType){
		if(Util.isEmpty(rankLayoutMap)){
			return null ;
		}
		String key = String.valueOf(rankType);
		return this.rankLayoutMap.get(key);
	}
	
	/**
	 * 加载排行榜
	 */
	private void loadRankInfo(){
		String fileName = XlsSheetNameType.rank_list.getXlsName();
		String sheetName = XlsSheetNameType.rank_list.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allRankInfoMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, RankInfo.class);
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(allRankInfoMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
	}
	//to be simplified
	private void loadRankWorld(){
		String fileName = XlsSheetNameType.rank_world.getXlsName();
		String sheetName = XlsSheetNameType.rank_world.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<RankWorld> allRankWorldList = XlsPojoUtil.sheetToList(sourceFile, sheetName, RankWorld.class);
			if(Util.isEmpty(allRankWorldList)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
				return ;
			}
			//排行榜组
			Map<Integer,RankGroup> groupMap = new HashMap<Integer,RankGroup>();
			for (RankWorld rankWorld : allRankWorldList) {
				if (null == rankWorld) {
					continue;
				}
				rankWorld.init();
				List<RankInfo> rankItemList = rankWorld.getRankInfoList();
				if (Util.isEmpty(rankItemList)) {
					continue;
				}
				RankGroup group = groupMap.get(rankWorld.getGroupId());
				if(null == group){
					group = new RankGroup();
					groupMap.put(rankWorld.getGroupId(), group);
				}
				group.addRankWorld(rankWorld);
			}
			this.rankGroupList.clear();
			this.rankGroupList.addAll(groupMap.values());
			groupMap.clear();
			groupMap = null ;
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		
	}
	private String getLogFileName(RankInfo rankInfo){
		String logName = getLogName(rankInfo);
		String fileName = logFilePath + logName + LOG_FILE_SUBFIX ;
		return fileName;
	}
	//文件格式
	//severID_rankXXXscore_1_1_-1_1.log.2014-06-07
	//固定值_排行榜名_排行榜ID_排行榜_区分_过滤类型_排序逻辑ID
	private Logger getLoggerFile(RankInfo rankInfo) {
		//appId_serverId_rankId_rankType_周期类型_开始日期_结束日期_typeName_subType.log
		//4_1_1_9_0_120101_130201_RoleLeve_l.log
		String logName = getLogName(rankInfo);
		
		String fileName = getLogFileName(rankInfo);
		
		RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
		Logger logger = LoggerFactory.getLogger(logName);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        ch.qos.logback.classic.Logger newLogger = (ch.qos.logback.classic.Logger)logger;
        newLogger.detachAndStopAllAppenders();

        //policy
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();
        policy.setContext(loggerContext);
        policy.setFileNamePattern(fileName + ".%d{"+FORMAT+"}");
        policy.setParent(appender);
        
        //encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(loggerContext);
        encoder.setPattern("%m%n");
        
        //start appender
        appender.setName(logName);
		appender.setFile(fileName);
        appender.setRollingPolicy(policy);
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
//      appender.setPrudent(true); //support that multiple JVMs can safely write to the same file.
        policy.start();
        encoder.start();
        /**
         * appender.start()方法必须放最后
         */
        appender.start();
        
        newLogger.addAppender(appender);
        //setup level
        newLogger.setLevel(Level.INFO);
        //remove the appenders that inherited 'ROOT'.
        newLogger.setAdditive(true);
		return newLogger;
	}

	private String getLogName(RankInfo rankInfo) {
		String logName = 
			GameContext.getAppId() 
			+ CAT + GameContext.getServerId() 
			+ CAT + rankInfo.getId() 
			+ CAT + rankInfo.getRankType().getType()
			+ CAT + rankInfo.getRankType().getRankCycle().getCycle()
			+ CAT + rankInfo.getStartTimeStr() 
			+ CAT + rankInfo.getEndTimeStr()
			+ CAT + rankInfo.getRankType().getName()
			+ CAT + rankInfo.getSubType() ;
		return logName;
	}
	

	private void printRankLog(RoleInstance role, boolean schedulerFlag, String timeStr) {
		// 遍历所以的rankItem,然后打出相关日志
		if (Util.isEmpty(this.allRankInfoMap)) {
			return;
		}
		for (RankInfo rankItem : allRankInfoMap.values()) {
			try {
				if (null == rankItem) {
					continue;
				}
				// 如果是活动排行榜判断是否在统计时间内
				if (!rankItem.isInStatDate()) {
					continue;
				}
				RankType rankType = rankItem.getRankType();
				if (null == rankType
						|| rankType.getActorType() != RankActorType.ROLE) {
					continue;
				}
				rankType.createRankLogic(rankItem).printLog(role, rankItem, schedulerFlag, timeStr);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
	}
	/* 含公会积分榜 */
	private void printRankLog(Union union) {
		// 遍历所以的rankItem,然后打出相关日志
		if (Util.isEmpty(this.allRankInfoMap)) {
			return;
		}
		for (RankInfo rankItem : allRankInfoMap.values()) {
			try {
				if (null == rankItem) {
					continue;
				}
				// 如果是活动排行榜判断是否在统计时间内
				if (!rankItem.isInStatDate()) {
					continue;
				}
				RankType rankType = rankItem.getRankType();
				if (null == rankType
						|| rankType.getActorType() != RankActorType.UNION) {
					continue;
				}
				String timeStr = getTimeStrHourBefore();
				rankType.createRankLogic(rankItem).printLog(union, rankItem, true, timeStr);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try{
			this.printRankLog(role, false,null);
		}catch (Exception e){
			logger.error("rankApp print rank log error: " , e);
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"rankApp.offline error, roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 打印玩家排行榜数据
	 * ①玩家下线
	 */
	@Override
	public void printRoleRankLog(RoleInstance role, boolean schedulerFlag, String timeStr) {
		try{
			this.printRankLog(role, schedulerFlag,timeStr);
		}catch (Exception e){
			logger.error("rankApp print rank log error: " , e);
		}
	}
	//打印所有玩家数据
	private void printRoleLog(){
		String timeStr = getTimeStrHourBefore();
		try {
			Collection<RoleInstance> onLineRoles = GameContext
					.getOnlineCenter().getAllOnlineRole();
			if (null == onLineRoles || onLineRoles.size() == 0) {
				return;
			}
			for (RoleInstance role : onLineRoles) {
				if (null == role) {
					continue;
				}
				printRoleRankLog(role,true,timeStr);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	/**
	 * @date 2015-1-15 下午04:15:
	 */
	private void printUnionLog(){
		Map<String, Union> uionMap = GameContext.getUnionApp().getUnionMap();
		if(Util.isEmpty(uionMap)){
			return ;
		}
		for(Union union : uionMap.values()){
			this.printRankLog(union);
		}
	}

	
	/**
	 * 打印所有排行榜日志 2014 06 16
	 * 在00点00分将在线玩家数据打印到上一个小时的日志中
	 */
	@Override
	public void printRankLogTimer(){
		logger.info("rankapp printRankLogTimer start");
		if(Util.isEmpty(this.allRankInfoMap)){
			return;
		}
//		FORMAT;
//		switchRankLogTimer();
		this.printRoleLog();
		this.printUnionLog();
		logger.info("rankapp printRankLogTimer end");
	}

	
	@Override
	public RankDbInfo getRankDbInfo(RoleInstance role, RankInfo rankInfo){
		int rankId = rankInfo.getId();
		RankDbInfo rankDbInfo = role.getRankDbInfo().get(rankId);
		if(null == rankDbInfo){
			rankDbInfo = new RankDbInfo();
			rankDbInfo.setRankId(rankId);
			rankDbInfo.setRoleId(role.getRoleId());
			rankDbInfo.setReward(RankDbInfo.REWARDED_NO);
			rankDbInfo.setExistRecord(false);
			role.getRankDbInfo().put(rankId, rankDbInfo);
		}
		return rankDbInfo;
	}
	
	@Override
	public void updateDonate(RoleInstance role,int rankId) {
		RankInfo rankInfo = this.getRankInfo(rankId);
		if(null == rankInfo){
			return ;
		}
		if(!rankInfo.isInStatDate()){
			return ;
		}
		RankType rankType = rankInfo.getRankType();
		if(null == rankType){
			return;
		}
		rankType.createRankLogic(rankInfo).count(role, rankInfo, 0, 0);
	}

	@Override
	public void updateTaobao(RoleInstance role,short taobaoType) {
		for(RankInfo rank : this.allRankInfoMap.values()){
			RankType rankType = rank.getRankType();
			if(null == rankType){
				continue ;
			}
			if(rankType.getType() != RankType.Role_Taobao.getType()){
				continue ;
			}
			if(rank.getSubType() != taobaoType){
				continue;
			}
			rankType.createRankLogic(rank).count(role, rank, 0, 0);
		}

	}

	private List<RankRewardRole> getRewardRoleList(byte sex, int rankId){
		String cGRoleKey = rankId  + CAT + sex;
		List<RankRewardRole> rewardRoleList = rewardRoleListMap.get(cGRoleKey);
		if(!Util.isEmpty(rewardRoleList)){
			return rewardRoleList;
		}

		cGRoleKey = rankId + CAT + ALL;
		rewardRoleList = rewardRoleListMap.get(cGRoleKey);
		if(!Util.isEmpty(rewardRoleList)){
			return rewardRoleList;
		}
		return null;
	}

	@Override
	public List<RankRewardRank> getRewardRankList(int level, byte sex, int rankId) {
		RankInfo rankItem = this.getRankInfo(rankId);
		if(null == rankItem){
			return null;
		}
		List<RankRewardRole> rewardRoleList = getRewardRoleList(sex, rankId);
		if(Util.isEmpty(rewardRoleList)){
			return null;
		}
		for(RankRewardRole rewardRole : rewardRoleList){
			if(null == rewardRole || !rewardRole.inRange(level)){
				continue;
			}
			return rewardRankListMap.get(rewardRole.getRoleKey());
		}
		return null;
	}
	

	@Override
	public RankReward getRankReward(RankLogRoleInfo roleInfo, short rank, int rankId) {
		if(Util.isEmpty(allRewardMap)){
			return null;
		}
		List<RankRewardRank> rewardRankList = getRewardRankList(roleInfo.getLevel(),
				roleInfo.getGender(), rankId);
		if(Util.isEmpty(rewardRankList)){
			return null;
		}
		for(RankRewardRank rewardRank : rewardRankList){
			if(null == rewardRank || !rewardRank.inRange(rank)){
				continue;
			}
			return allRewardMap.get(rewardRank.getRankKey());
		}
		return null;
	}
	/**
	 * 定时切换日志文件（每小时第5秒）
	 */
	@Override
	public void switchRankLogTimer() {
		try{
			if(Util.isEmpty(this.allRankInfoMap)){
				return;
			}
			for(RankInfo rankItem : allRankInfoMap.values()){
				if(null == rankItem){
					continue;
				}
				RankType rankType = rankItem.getRankType();
				if(null == rankType){
					continue;
				}
				rankType.createRankLogic(rankItem).switchLog(rankItem);
			}
		}catch (Exception ex){
			logger.error("", ex);
		}
	}

	@Override
	public void printLogOffRank(RankType rankType, String id) {
		if(Util.isEmpty(this.allRankInfoMap)){
			return;
		}
		for(RankInfo rankItem : allRankInfoMap.values()){
			if(null == rankItem){
				continue;
			}
			RankType currentRankType = rankItem.getRankType();
			if(null == currentRankType){
				continue;
			}
			if(rankType != currentRankType){
				continue;
			}
			rankType.createRankLogic(rankItem).offRankLog(rankItem, id);
		}
	}

	
	@Override
	public String[] getPageOriginalData(int rankId,short page){
		RankInfo rankItem = this.getRankInfo(rankId);
		if(null == rankItem){
			return null;
		}
		try {
			String content = RankHttpClient.get(getPageDataUrl(rankId, page));
			if(Util.isEmpty(content)){
				return null;
			}
			if(content.equals(RankHttpClient.RESP_NOT_SC_OK)){
				return null;
			}
			//解析字符串
			return Util.splitStr(content, RankLogic.ROW_CAT);
		} catch (Exception e) {
			logger.error("rankApp getPageData exception: ", e);
			return null;
		}
	}
	
	/*
       ###2014-06-25-09#0#6#4
       1#17000001#2987#18#93128#1402998071000#17000001#������˹������#1#18#2##
       2#17000002#2278#1#535#1403173897000#17000002#��ʲ��ˡ�׷��#1#1#-1##
       3#497000068#1690#2#588#1402997687000#497000068#��˹��¡��׻�#1#2#-1##
       4#1988000007#794#9#2#1401194291000#1988000007#�ȸ������#0#9#0##
	 */
	@Override
	public RankLogData getPageData(int rankId, int page) {
		RankInfo rankItem = this.getRankInfo(rankId);
		if(null == rankItem){
			return null;
		}
		try {
			String content = RankHttpClient.get(getPageDataUrl(rankId, page));
			if(Util.isEmpty(content)){
				return null;
			}
			if(content.equals(RankHttpClient.RESP_NOT_SC_OK)){
				RankLogData rlData = new RankLogData();
				rlData.setTotalPage((short)1);
				return rlData;
			}
			//解析字符串
			String[] rows = Util.splitStr(content, RankLogic.ROW_CAT);//回车
			if(Util.isEmpty(rows)){
				return null;
			}
			String headStr = rows[0];//###2014-06-25-09#0#6#4
			if(Util.isEmpty(headStr) || !headStr.startsWith(RankLogic.LOG_BASIC_DATA_FLAG)){//###
				logger.info("rankApp getPageData do not have file header");
				return null;
			}
			headStr = headStr.substring(RankLogic.LOG_BASIC_DATA_FLAG_LEN);//3
			String[] heads = Util.splitStr(headStr, RankLogic.CAT);//#
			if(Util.isEmpty(heads)){
				logger.info("rankApp getPageData do not have file header data");
				return null;
			}
			int curPage = Integer.parseInt(heads[1]);
			int perPageRecord = Integer.parseInt(heads[2]);
			int totalRecordNum = Integer.parseInt(heads[3]);
			//只处理策划要求的名次
			if(totalRecordNum > rankItem.getDisCount()){
				totalRecordNum = rankItem.getDisCount();
			}
			int totalPage = totalRecordNum / perPageRecord;
			if((totalRecordNum % perPageRecord) != 0){
				totalPage += 1;
			}
			List<RankDetailItem> rankDetailItemList = null;
			for(int i = 1; i < rows.length; i++){//从1开始
				if(Util.isEmpty(rows[i])){
					continue;
				}
				//处理表头
				RankDetailItem rankDetailItem = rankItem.getRankType().createRankLogic(rankItem).parseLog(rows[i]);
				if(null == rankDetailItem){
					continue;
				}
				if(null == rankDetailItemList){
					rankDetailItemList = new ArrayList<RankDetailItem>();
				}
				//如果条目数大于要显示的数目则退出
				if(rankDetailItem.getRank() > rankItem.getDisCount()){
					break;
				}
				rankDetailItemList.add(rankDetailItem);
			}
			RankLogData rankLogData = new RankLogData();
			rankLogData.setCurPage((short)curPage);
			rankLogData.setTotalPage((short)totalPage);
			rankLogData.setRdItemList(rankDetailItemList);
			return rankLogData;
		} catch (Exception e) {
			logger.error("rankApp getPageData exception: ", e);
			return null;
		}
	}

	/**
	 * 得到排行数据用于发奖
	 * 返回文件头格式###2012-11-11-09
	 * @param url 请求url：http://host:port/sort/appid/serverId/RankId/last(时间：2012-07-17-16)
	 * @return <排名， roleId>
	 */
	private List<RankLogRoleInfo> getRankData(String url, RankInfo rankItem) {
		try {
			String content = RankHttpClient.get(url);
			if(Util.isEmpty(content)){
				return null;
			}
			//解析字符串
			String[] rows = Util.splitStr(content, RankLogic.ROW_CAT);
			if(Util.isEmpty(rows)){
				return null;
			}
			List<RankLogRoleInfo> rankLogRoleInfoList = null;
			for(int i = 1; i < rows.length; i++){
				String[] cols = Util.splitStr(rows[i], RankLogic.CAT);
				if(Util.isEmpty(cols)){
					continue;
				}
				if(null == rankLogRoleInfoList){
					rankLogRoleInfoList = new ArrayList<RankLogRoleInfo>();
				}
				if(rankLogRoleInfoList.size() >= rankItem.getDisCount()){
					break ;
				}
				rankLogRoleInfoList.add(initRankLogRoleInfo(cols, rankItem));
			}
			return rankLogRoleInfoList;
		} catch (Exception e) {
			logger.error("rankApp getRankData exception: ", e);
			return null;
		}
	}
	
	private RankLogRoleInfo initRankLogRoleInfo(String[] cols, RankInfo rankItem){
		RankLogRoleInfo roleInfo = new RankLogRoleInfo();
		short rank = Short.valueOf(cols[0]);
		short disCount = rankItem.getDisCount();
		roleInfo.setRank(rank > disCount ? 0 : rank);
		roleInfo.setRoleId(Integer.valueOf(cols[1]));
		roleInfo.setRoleName(cols[2]);
		roleInfo.setGender(Byte.valueOf(cols[3]));
		roleInfo.setLevel(Integer.valueOf(cols[4]));
		//roleInfo.setCamp(Byte.valueOf(cols[5]));
		return roleInfo;
	}

	@Override
	public RankLogRoleInfo getRoleRank(int rankId, String roleId) {
		try {
			String content = RankHttpClient.get(getRoleSortDataUrl(rankId, roleId));
			if(Util.isEmpty(content)){
				return null;
			}
			String[] rows = Util.splitStr(content, RankLogic.ROW_CAT);
			//玩家未上榜
			if(Util.isEmpty(rows) || rows.length < 2){
				RankLogRoleInfo rlRoleInfo = new RankLogRoleInfo();
				rlRoleInfo.setRank((byte)0);
				return rlRoleInfo;
			}
			if(Util.isEmpty(rows[0])){
				logger.error("rankApp getRoleRank do not have header");
				return null;
			}
			if(Util.isEmpty(rows[1])){
				logger.error("rankApp getRoleRank do not have role data");
				return null;
			}
			String[] cols = Util.splitStr(rows[1], RankLogic.CAT);
			if(Util.isEmpty(cols)){
				return null;
			}
			return initRankLogRoleInfo(cols, this.getRankInfo(rankId));
		} catch (Exception e) {
			logger.error("rankApp getRoleRank exception: ", e);
			return null;
		}
	}

	/**
	 * 
	 * @param rankInfo
	 * @param dateStr 取排行榜数据的日期，格式：yyyy-MM-dd-HH
	 * @param date 当前发奖日期用来确定是否能发奖
	 */
	private void rewardByMail(RankInfo rankInfo, String dateStr, Date date) {
		if(!canReward(rankInfo, date)){
			Log4jManager.RANK_REWARD_BYMAIL_LOG.info("canot reward rankId={},dateStr={} ",rankInfo.getId(),dateStr);
			return ;
		}
		String url = getSortDataUrl(rankInfo.getId(), dateStr);
		//http://192.168.1.238:18810/sort/5/35/8/2014-06-27-09
		List<RankLogRoleInfo> rlRoleInfoList = getRankData(url, rankInfo);
		if(Util.isEmpty(rlRoleInfoList)){
			Log4jManager.RANK_REWARD_BYMAIL_LOG.info("rankInfoList is empty rankId={},dateStr={} ",rankInfo.getId(),dateStr);
			return ;
		}
		String rewardTimeStr = DateUtil.date2Str(date, MAIL_DATE_FORMAT);
		for(RankLogRoleInfo rLRoleInfo : rlRoleInfoList){
			if(null == rLRoleInfo){
				continue;
			}
			short rank = rLRoleInfo.getRank();
			int roleId = rLRoleInfo.getRoleId();
			RankReward rankReward = getRankReward(rLRoleInfo, rank, rankInfo.getId());
			if(null == rankReward){
				Log4jManager.RANK_REWARD_BYMAIL_LOG.info("rankReward is empty rankId={},dateStr={},roleId={},roleRank={} "
						,rankInfo.getId(),dateStr,roleId,rank);
				continue;
			}
			
			List<GoodsOperateBean> goodsList = rankReward.getGoodsList();
			String content = rankInfo.getMailInfo(rank, rewardTimeStr);
			GameContext.getMailApp().sendMail(String.valueOf(roleId), MailSendRoleType.Rank.getName(),
					content, MailSendRoleType.Rank.getName(), 
					OutputConsumeType.rank_output_mail.getType(), 0, 
					rankReward.getGameMoney(), goodsList);
			Log4jManager.RANK_REWARD_BYMAIL_LOG.info("{}#{}#{}#{}#{}#{}",LOG_REWARD_SUCESS,roleId,rankInfo.getId(),
					dateStr,rank,rankReward.getRankKey());
		}
	}
	/**
	 * 定时发送奖励
	 * spring-quartz.xml
	 * 如果在配置信息加载完之前出发会报异常
	 */
	@Override
	public void reward(){
		Log4jManager.RANK_REWARD_BYMAIL_LOG.info("start to exec reward");
		if(Util.isEmpty(allRankInfoMap)){
			Log4jManager.RANK_REWARD_BYMAIL_LOG.info("reward over cause by empty rankInfoMap");
			return ;
		}
		Date now = new Date();
		//getRankDateUrl(now) 为上一个小时的时间。eg:2014-06-26-15
		rewardByMail(getRankDateUrl(now), now);
	}
	
	/**
	 * 取当前时间的前一个小时的数据
	 * @param date
	 * @return
	 */
	private String getRankDateUrl(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long millSec = cal.getTime().getTime();
		long newMillSec = millSec - ONE_HOUR;
		cal.setTimeInMillis(newMillSec);
		return DateUtil.date2Str(cal.getTime(), FORMAT);
	}
	
	/**
	 * @param dateStr 取排行榜数据的日期，格式：yyyy-MM-dd-HH
	 * @param date 当前发奖日期用来确定是否能发奖
	 */
	private void rewardByMail(String dateStr, Date date){
		for(RankInfo rankInfo : this.allRankInfoMap.values()){
			if(null == rankInfo){
				continue;
			}
			rewardByMail(rankInfo, dateStr, date);
			try {
				Thread.sleep(REWARD_SLEEP);
			} catch (Exception e) {
				logger.error("rankApp rewardByMail sleep error, ", e);
			}
		}
	}
	
	private boolean canReward(RankInfo rankInfo, Date curDate){
		//如果是活动排行榜
		if(rankInfo.getActiveRankInfo() != null){
			return false;
		}
		List<RankRewardTime> rewardTimeList = rankInfo.getRewardTimeList();
		if(Util.isEmpty(rewardTimeList)){
			return false;
		}
		
		for(RankRewardTime rewardTime : rewardTimeList){
			if(null == rewardTime){
				continue ;
			}
			//其中有个一个时间能领奖即返回
			if(rewardTime.meetTime(curDate)){
				return true;
			}
		}
		return false;
//		return true; //测试用
	}

	/**
	 * 返回排行榜分页数据
	 */
	private String getPageDataUrl(int rankId, int page) {
		String url = StringUtil.replace(URL_PAGE_DATA, "${serverAddr}", GameContext.getPlatformConfig().getLogServerAddr());
		url = StringUtil.replace(url, "${appid}", String.valueOf(GameContext.getAppId()));
		url = StringUtil.replace(url, "${serverid}", String.valueOf(995));
		url = StringUtil.replace(url, "${rankid}", String.valueOf(rankId));
		url = StringUtil.replace(url, "${pageindex}", String.valueOf(page));
		return url;
	}

	/**
	 * 0812
	 * 返回玩家某个排行的名次
	 * URL_ROLESORT_DATA = /rolesort/&appid/&serverId/
	 * ipaddr:port/rolesort/&appid/&serverId/&rankId/&roleId
	 */
	public String getRoleSortDataUrl(int rankId, String roleId) {
		String url = StringUtil.replace(URL_ROLESORT_DATA, "${serverAddr}", GameContext.getPlatformConfig().getLogServerAddr());
		url = StringUtil.replace(url, "${appid}", String.valueOf(GameContext.getAppId()));
		url = StringUtil.replace(url, "${serverid}", String.valueOf(995));
		url = StringUtil.replace(url, "${rankid}", String.valueOf(rankId));
		url = StringUtil.replace(url, "${roleId}", roleId);
		return url;
	}

	/**
	 * 发奖励用
	 * 返回某个排行榜排行数据
	 * @param date gm补发奖励日期，为空则是正常发奖
	 */
	private String getSortDataUrl(int rankId, String date) {
		String url = StringUtil.replace(URL_SORT_DATA, "${serverAddr}", GameContext.getPlatformConfig().getLogServerAddr());
		url = StringUtil.replace(url, "${appid}", String.valueOf(GameContext.getAppId()));
		url = StringUtil.replace(url, "${serverid}", String.valueOf(995));
		url = StringUtil.replace(url, "${rankid}", String.valueOf(rankId));
		url = StringUtil.replace(url, "${date}", date);
		return url;
	}
	
	//GM工具初始化排行榜
	//http://192.168.1.230:17810/login.action
	@Override
	public RankInitResult initLogDataFormDB(int[] rankIds) {
		RankInitResult result = new RankInitResult();
		//传入参数为空，则初始化全部排行榜
		if(null == rankIds || rankIds.length == 0){
			for(Entry<Integer, RankInfo> entry : allRankInfoMap.entrySet()){
				RankInfo rankItem = entry.getValue();
				if(null == rankItem){
					continue;
				}
				//初始化单个排行榜
				if(initLog(rankItem)){
					result.getSuccessList().add(rankItem.getId());
					logger.info("rankApp initLogDataFromDB sucess, rankId=" + rankItem.getId());
				}else{
					result.getFailureList().add(rankItem.getId());
				}
			}
			return result;
		}
		for(Integer rankId : rankIds){
			RankInfo rankItem = this.getRankInfo(rankId);
			if(null == rankItem){
				continue;
			}
			if(initLog(rankItem)){
				result.getSuccessList().add(rankItem.getId());
				logger.info("rankApp initLogDataFromDB sucess, rankId=" + rankItem.getId());
			}else{
				result.getFailureList().add(rankItem.getId());
			}
		}
		return result;
	}
	
	private boolean initLog(RankInfo rankInfo){
		//如果是活动排行榜判断是否在统计时间内，非活动排行榜直接返回true。
		if(!rankInfo.isInStatDate()){
			return true ;
		}
		RankType rankType = rankInfo.getRankType();
		if(null == rankType){
			return false;
		}
		try{
			rankType.createRankLogic(rankInfo).initLogData(rankInfo);
			return true;
		}catch(Exception ex){
			logger.error("rankApp initLogDataFromDB error, rankId=" + rankInfo.getId(), ex);
			return false;
		}
	}

	@Override
	public Result rewardByMailFromGM(int rankId, String dateStr) {
		Result result = new Result();
		if(Util.isEmpty(dateStr) || Util.splitStr(dateStr, "-").length != 4){
			result.setInfo("reward date is wrong");
			result.setResult(Result.FAIL);
			return result;
		}
		Date curDate = DateUtil.str2Date(dateStr, FORMAT);
		String rewardDateStr = getRankDateUrl(curDate);
		if(null == curDate){
			result.setInfo("reward date is wrong");
			result.setResult(Result.FAIL);
			return result;
		}
		//如果rankId <= 0 则所有的奖励都要重发
		if(rankId <= 0){
			rewardByMail(rewardDateStr, curDate);
			result.setInfo("rewardByMailFromGM success");
			result.setResult(Result.SUCCESS);
			return result;
		}
		//对于rankId指定的排行榜发奖
		RankInfo rankInfo = this.getRankInfo(rankId);
		if(null == rankInfo){
			result.setInfo("rankItem is null, rankId=" +rankId);
			result.setResult(Result.FAIL);
			return result;
		}
		rewardByMail(rankInfo, rewardDateStr, curDate);
		result.setInfo("rewardByMailFromGM success");
		result.setResult(Result.SUCCESS);
		return result;
	}

	@Override
	public List<RankGroup> getRankGroupList() {
		return this.rankGroupList ;
	}

	@Override
	public void equipOffRank(RoleGoods roleGoods) {
		if(null == roleGoods){
			return ;
		}
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(null == goodsBase || !goodsBase.isEquipment()){
			return ;
		}
		this.printLogOffRank(RankType.ROLE_EQUIP, roleGoods.getId());
	}

	@Override
	public void unionOffRank(String unionId) {
        if(null == unionId || Util.isEmpty(this.allRankInfoMap)){
            return;
        }
        for(RankInfo rankItem : allRankInfoMap.values()){
            if(null == rankItem){
                continue;
            }
            RankType rankType = rankItem.getRankType();
            if(null == rankType || rankType.getActorType() != RankActorType.UNION){
                continue;
            }
            rankType.createRankLogic(rankItem).offRankLog(rankItem, unionId);
        }
	}
	
	@Override
	public void offlineRoleOffRank(String roleId){
		if(Util.isEmpty(this.allRankInfoMap)){
			return;
		}
		for(RankInfo rankItem : allRankInfoMap.values()){
			if(null == rankItem){
				continue;
			}
			RankType rankType = rankItem.getRankType();
			if(null == rankType || rankType.getActorType() != RankActorType.ROLE){
				continue;
			}
			rankType.createRankLogic(rankItem).offRankLog(rankItem, roleId);
		}
	}


	@Override
	public void roleChangeCampOffRank(String roleId, byte oldCampId) {
		if(Util.isEmpty(allRankInfoMap)){
			return ;
		}
		for(Entry<Integer, RankInfo> entry : allRankInfoMap.entrySet()){
			RankInfo rankInfo = entry.getValue();
			if(null == rankInfo){
				continue;
			}
			RankType rankType = rankInfo.getRankType();
			if(null == rankType){
				continue;
			}
			if(rankType.getFilter() != RankFilterType.CAMP){
				continue;
			}
			rankType.createRankLogic(rankInfo).offRankLog(rankInfo, roleId);
		}
	}

	//打印在线玩家的日志
	@Override
	public void writeLogFile(String fileName, String logStr) {
		FileOutputStream file = null ;
		OutputStreamWriter os = null ;
		BufferedWriter bw = null ;
		try {
			File f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();// 不存在则创建
			}
			file = new FileOutputStream(f,true);
			os = new OutputStreamWriter(file,"UTF-8");
			bw = new BufferedWriter(os);
			bw.write(logStr);
			bw.newLine();
			bw.flush();
		} catch (Exception e) {
			logger.error("writeLogFile error,fileName=" + fileName,e);
		}finally{
			if(null != bw){
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
			if(null != os){
				try {
					os.close();
				} catch (IOException e) {
				}
			}
			if(null != file){
				try {
					file.close();
				} catch (IOException e) {
				}
			}
		}
	}

	//得到上一个小时的时间
	private String getTimeStrHourBefore(){
		return RANK_DATE_FORMAT.format(DateUtil.addHours(new Date(),-1));
	}

    /**
     * 获得公会等级排行榜的排行榜id
     * @return
     */
    private int getUnionLevelRankId(){
        if(Util.isEmpty(this.allRankInfoMap)){
            return -1 ;
        }
        for(RankInfo rank : this.allRankInfoMap.values()){
            if(rank.getRankType() == RankType.UNION_LEVEL){
                return rank.getId() ;
            }
        }
        return -1 ;
    }

	@Override
	public List<String> getRankedUnionIds(int unionidsNum) {
        List<String> ids = new ArrayList<String>();
        int rankId = this.getUnionLevelRankId();
        if(rankId <=0){
            return ids;
        }
		String dateUrl = this.getZeroDateUrl();
		String url = this.getSortDataUrl(getUnionLevelRankId(), dateUrl);
		String content = RankHttpClient.get(url);
		if(Util.isEmpty(content)){
			return ids;
		}
		String[] rows = Util.splitStr(content, RankLogic.ROW_CAT);//回车
		if(Util.isEmpty(rows)){
			return ids;
		}
		for(int i = 1; i <= unionidsNum && i < rows.length; i++){
			String[] params = rows[i].split(RankLogic.CAT);
			if(Util.isEmpty(params) || params.length < 4){
				continue;
			}
			ids.add(params[3]);
		}
		return ids;
	}

	private String getZeroDateUrl(){
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 00);
		return this.getRankDateUrl(cal.getTime()) ;
	}
	
}
