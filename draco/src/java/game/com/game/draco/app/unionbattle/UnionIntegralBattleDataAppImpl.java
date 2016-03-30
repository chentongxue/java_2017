package com.game.draco.app.unionbattle;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.game.draco.app.unionbattle.config.UnionIntegral;
import com.game.draco.app.unionbattle.config.UnionIntegralMail;
import com.game.draco.app.unionbattle.config.UnionIntegralNpc;
import com.game.draco.app.unionbattle.config.UnionIntegralReborn;
import com.game.draco.app.unionbattle.config.UnionIntegralRewGroup;
import com.game.draco.app.unionbattle.config.UnionIntegralReward;
import com.game.draco.app.unionbattle.config.UnionIntegralSummon;
import com.game.draco.app.unionbattle.job.IntegralBattleAwardJob;
import com.game.draco.app.unionbattle.job.IntegralBattleClearJob;
import com.game.draco.app.unionbattle.job.IntegralBattleCreateFightJob;
import com.game.draco.app.unionbattle.job.IntegralBattleStartJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class UnionIntegralBattleDataAppImpl implements UnionIntegralBattleDataApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 定时任务序列
	private static AtomicInteger jobIndex = new AtomicInteger(0);
	
	//公会积分战基础配置
	@Getter @Setter private UnionIntegral integral = null;
	
	//邮件数据
	@Getter @Setter private Map<Byte,UnionIntegralMail> integralMailMap = Maps.newHashMap();
	
	//战场指挥官数据
	@Getter @Setter private Map<String,UnionIntegralNpc> integralNpcMap = Maps.newHashMap();
	
	//复活点数据
	@Getter @Setter private Map<Byte,UnionIntegralReborn> integralRebornMap = Maps.newHashMap();
	
	//奖励组数据
	@Getter @Setter private Map<Integer,List<UnionIntegralRewGroup>> integralRewGroupMap = Maps.newHashMap();
	
	//奖励数据
	@Getter @Setter private Map<Byte,List<UnionIntegralReward>> integralRewardMap = Maps.newHashMap();
	
	//召唤数据
	@Getter @Setter private Map<String,UnionIntegralSummon> integralSummonMap = Maps.newHashMap();
	
	/**
	 * 加载基础数据
	 */
	private void loadIntegralConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			integral = XlsPojoUtil.getEntity(sourceFile, sheetName, UnionIntegral.class);
			if(integral == null){
				Log4jManager.CHECK.error("unionIntegral is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(integral.getMapId());
			if(!map.getMapConfig().changeLogicType(MapLogicType.integral)) {
				Log4jManager.CHECK.error("UnionIntegral The map logic type config error. mapId= "	+ fileName);
				Log4jManager.checkFail();
			}
			
			integral.initOpenTime();
			
			integral.initAwardTime();
			
			integral.initCreateFightTime();
			
		}catch(Exception e){
			logger.error("loadIntegralConfig is error",e);
		}
	}
	
	/**
	 * 加载邮件数据
	 */
	private void loadIntegralMailConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_mail_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_mail_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			integralMailMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionIntegralMail.class);
			if(Util.isEmpty(integralMailMap)){
				Log4jManager.CHECK.error("integralMailMap is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadIntegralMailConfig is error",e);
		}
	}
	
	/**
	 * 加载指挥官数据
	 */
	private void loadIntegralNpcConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_npc_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_npc_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			integralNpcMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionIntegralNpc.class);
			if(Util.isEmpty(integralNpcMap)){
				Log4jManager.CHECK.error("integralNpcMap is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadIntegralNpcConfig is error",e);
		}
	}
	
	/**
	 * 加载复活数据
	 */
	private void loadIntegralRebornConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_reborn_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_reborn_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			integralRebornMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionIntegralReborn.class);
			if(Util.isEmpty(integralRebornMap)){
				Log4jManager.CHECK.error("integralRebornMap is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadIntegralRebornConfig is error",e);
		}
	}
	
	/**
	 * 加载奖励组数据
	 */
	private void loadIntegralRewGroupConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_rewgroup_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_rewgroup_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<UnionIntegralRewGroup> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionIntegralRewGroup.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("unionIntegralRewGroupList is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			List<UnionIntegralRewGroup> rewGroupList = null;
			for(UnionIntegralRewGroup rewGroup : list){
				if(integralRewGroupMap.containsKey(rewGroup.getGroupId())){
					rewGroupList = integralRewGroupMap.get(rewGroup.getGroupId());
				}else{
					rewGroupList = Lists.newArrayList();
				}
				rewGroupList.add(rewGroup);
				integralRewGroupMap.put(rewGroup.getGroupId(),rewGroupList);
			}
		}catch(Exception e){
			logger.error("loadIntegralRewGroupConfig is error",e);
		}
	}
	
	/**
	 * 加载奖励数据
	 */
	private void loadIntegralRewardConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_reward_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_reward_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<UnionIntegralReward> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, UnionIntegralReward.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("unionIntegralRewardList is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			List<UnionIntegralReward> rewardList = null;
			for(UnionIntegralReward reward : list){
				if(integralRewardMap.containsKey(reward.getRewType())){
					rewardList = integralRewardMap.get(reward.getRewType());
				}else{
					rewardList = Lists.newArrayList();
				}
				rewardList.add(reward);
				integralRewardMap.put(reward.getRewType(),rewardList);
			}
		}catch(Exception e){
			logger.error("loadIntegralRewardConfig is error",e);
		}
	}
	
	/**
	 * 加载召唤数据
	 */
	private void loadIntegralSummonConfig(){
		try{
			String fileName = XlsSheetNameType.union_integral_summon_config.getXlsName();
			String sheetName = XlsSheetNameType.union_integral_summon_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			integralSummonMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, UnionIntegralSummon.class);
			if(Util.isEmpty(integralSummonMap)){
				Log4jManager.CHECK.error("integralSummonMap is null,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
		}catch(Exception e){
			logger.error("loadIntegralSummonConfig is error",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try{
			loadIntegralConfig();
			loadIntegralMailConfig();
			loadIntegralNpcConfig();
			loadIntegralRebornConfig();
			loadIntegralRewGroupConfig();
			loadIntegralRewardConfig();
			loadIntegralSummonConfig();
			loadTimingTask();
			GameContext.getUnionIntegralBattleApp().initIntegralBattle();
		}catch(Exception e){
			logger.error("start is error",e);
		}
	}
	
	/**
	 * 加载定时任务
	 */
	private void loadTimingTask() {
		List<String> openTimeList = integral.getOpenTimeCronExpressionList();
		if (!Util.isEmpty(openTimeList)) {
			for (String ce : openTimeList) {
				if (Util.isEmpty(ce)) {
					continue;
				}
				Class clazz = IntegralBattleStartJob.class;
				try {
					int index = jobIndex.getAndDecrement();
					JobDetail jobDetail = new JobDetail("integral_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
					CronTrigger trigger = new CronTrigger("integral_cron_" + index, null, ce);
					GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
					logger.info("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
				} catch (Exception ex) {
					Log4jManager.CHECK.error("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce, ex);
					Log4jManager.checkFail();
				}
			}
		}
		
		List<String> awardList = integral.getAwardTimeCronExpressionList();
		if (!Util.isEmpty(awardList)) {
			for (String ce : awardList) {
				if (Util.isEmpty(ce)) {
					continue;
				}
				Class clazz = IntegralBattleAwardJob.class;
				try {
					int index = jobIndex.getAndDecrement();
					JobDetail jobDetail = new JobDetail("integral_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
					CronTrigger trigger = new CronTrigger("integral_cron_" + index, null, ce);
					GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
					logger.info("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
				} catch (Exception ex) {
					Log4jManager.CHECK.error("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce, ex);
					Log4jManager.checkFail();
				}
			}
		}
		
		List<String> clearList = integral.getClearTimeCronExpressionList();
		if (!Util.isEmpty(clearList)) {
			for (String ce : clearList) {
				if (Util.isEmpty(ce)) {
					continue;
				}
				Class clazz = IntegralBattleClearJob.class;
				try {
					int index = jobIndex.getAndDecrement();
					JobDetail jobDetail = new JobDetail("integral_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
					CronTrigger trigger = new CronTrigger("integral_cron_" + index, null, ce);
					GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
					logger.info("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
				} catch (Exception ex) {
					Log4jManager.CHECK.error("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce, ex);
					Log4jManager.checkFail();
				}
			}
		}
		
		List<String> createFightList = integral.getCreateFightTimeCronExpressionList();
		if (!Util.isEmpty(createFightList)) {
			for (String ce : createFightList) {
				if (Util.isEmpty(ce)) {
					continue;
				}
				Class clazz = IntegralBattleCreateFightJob.class;
				try {
					int index = jobIndex.getAndDecrement();
					JobDetail jobDetail = new JobDetail("integral_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
					CronTrigger trigger = new CronTrigger("integral_cron_" + index, null, ce);
					GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
					logger.info("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce);
				} catch (Exception ex) {
					Log4jManager.CHECK.error("register integral scheduler success,class=" + clazz.getName() + " cronExpression=" + ce, ex);
					Log4jManager.checkFail();
				}
			}
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public UnionIntegralNpc getIntegralNpc(int id) {
		for(Entry<String,UnionIntegralNpc> integralNpc : integralNpcMap.entrySet()){
			if(integralNpc.getValue().getId() == id){
				return integralNpc.getValue();
			}
		}
		return null;
	}
	
	@Override
	public List<UnionIntegralRewGroup> getIntegralRewGroupList(int groupId){
		return integralRewGroupMap.get(groupId);
	}

}