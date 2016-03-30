package com.game.draco.app.survival;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.survival.config.SurvivalBase;
import com.game.draco.app.survival.config.SurvivalMail;
import com.game.draco.app.survival.config.SurvivalReward;
import com.game.draco.app.survival.job.SurvivalBattleJob;
import com.google.common.collect.Maps;

public class SurvivalAppImpl implements SurvivalApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 定时任务序列
	private static AtomicInteger jobIndex = new AtomicInteger(0);
	
	//奖励数据
	@Getter @Setter private Map<Byte,SurvivalReward> survivalRewardMap = Maps.newHashMap();
	
	//邮件数据
	@Getter @Setter private Map<Byte,SurvivalMail> survivalMailMap = Maps.newHashMap();
	
	//商店ID
	@Getter @Setter private SurvivalBase survivalBase = null; 
	
	/**
	 * 加载生存战场基础数据
	 */
	private void loadSurvivalBaseConfig(){
		try{
			String fileName = XlsSheetNameType.survival_base_config.getXlsName();
			String sheetName = XlsSheetNameType.survival_base_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			survivalBase = XlsPojoUtil.getEntity(sourceFile, sheetName, SurvivalBase.class);
			if(survivalBase == null){
				Log4jManager.CHECK.error("SurvivalBase is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(survivalBase.getMapId());
			if(!map.getMapConfig().changeLogicType(MapLogicType.survival)) {
				Log4jManager.CHECK.error("SurvivalBase The map logic type config error. mapId= "	+ fileName);
				Log4jManager.checkFail();
			}
			
			survivalBase.initOpenTime();
		}catch(Exception e){
			logger.error("loadSurvivalBaseConfig is error",e);
		}
	}
	
	/**
	 * 加载生存战场邮件数据
	 */
	private void loadSurvivalMailConfig(){
		try{
			String fileName = XlsSheetNameType.survival_mail_config.getXlsName();
			String sheetName = XlsSheetNameType.survival_mail_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			survivalMailMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SurvivalMail.class);
			if(Util.isEmpty(survivalMailMap)){
				Log4jManager.CHECK.error("survivalMailMap is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
		}catch(Exception e){
			logger.error("loadSurvivalMailConfig is error",e);
		}
	}
	
	/**
	 * 加载生存战场奖励数据
	 */
	private void loadSurvivalRewardConfig(){
		try{
			String fileName = XlsSheetNameType.survival_reward_config.getXlsName();
			String sheetName = XlsSheetNameType.survival_reward_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			survivalRewardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SurvivalReward.class);
			if(Util.isEmpty(survivalRewardMap)){
				Log4jManager.CHECK.error("survivalRewardMap is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
		}catch(Exception e){
			logger.error("loadSurvivalRewardConfig is error",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try{
			loadSurvivalBaseConfig();
			loadSurvivalMailConfig();
			loadSurvivalRewardConfig();
			loadTimingTask();
			if(GameContext.getSurvivalBattleApp().isTimeOpen(false)){
				GameContext.getSurvivalBattleApp().start();
			}
		}catch(Exception e){
			logger.error("start is error",e);
		}
	}
	
	/**
	 * 加载定时任务
	 */
	private void loadTimingTask() {
		List<String> ces = survivalBase.getCronExpressionList();
		if (Util.isEmpty(ces)) {
			return;
		} 
		for (String ce : ces) {
			if (Util.isEmpty(ce)) {
				continue;
			}
			Class clazz = SurvivalBattleJob.class;
			try {
				int index = jobIndex.getAndDecrement();
				JobDetail jobDetail = new JobDetail("survival_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
				CronTrigger trigger = new CronTrigger("survival_cron_" + index, null, ce);
				GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
				logger.info("register survival scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
			} catch (Exception ex) {
				Log4jManager.CHECK.error("register survival scheduler success,class=" + clazz.getName() + " cronExpression=" + ce, ex);
				Log4jManager.checkFail();
			}
		}
	}

	@Override
	public void stop() {
		
	}

}