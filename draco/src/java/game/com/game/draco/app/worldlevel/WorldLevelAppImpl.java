package com.game.draco.app.worldlevel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.worldlevel.config.WorldLevelBaseConfig;
import com.game.draco.app.worldlevel.config.WorldLevelStageConfig;
import com.game.draco.message.push.C1200_WorldLevelRatioRespMessage;
import com.google.common.collect.Lists;

public class WorldLevelAppImpl implements WorldLevelApp {
	private static final Logger logger = LoggerFactory.getLogger(WorldLevelAppImpl.class);
	private static final int MIN_LEVEL = 1;
	private static final int MAX_LEVEL = 200;
	private static final int RATIO_PARAM1 = 2000;
	private static final int RATIO_PARAM2 = 1000;
	private WorldLevelBaseConfig worldLevelBaseConfig = new WorldLevelBaseConfig();
	private List<WorldLevelStageConfig> worldLevelStageConfigList = Lists.newArrayList();
	private int worldLevel;
	private static AtomicInteger jobIndex = new AtomicInteger(0);
	private static final String TIME_CRON = "0 0 0 * * ?";

	/**
	 * 获取世界等级
	 * @return
	 */
	@Override
	public int getWorldLevel() {
		return this.worldLevel;
	}

	/**
	 * 获取经验的比率
	 * @param role
	 * @return 10000=100%
	 */
	@Override
	public int getWorldLevelRatio(RoleInstance role) {
		try {
			return (int) this.calcWorldLevelRatio(role.getLevel());
		} catch (Exception e) {
			logger.error("WorldLevelAppImpl.getWorldLevelRatio error!", e);
		}
		return 1;
	}
	
	/**
	 * 计算经验比率(100%=10000)
	 * @param level
	 */
	private float calcWorldLevelRatio(int level) {
		if (level < this.getWorldLevelBaseConfig().getBaseLevel()) {
			return this.legalRatio(WorldLevelBaseConfig.PROPORTION);
		}
		if (level <= this.getWorldLevel() - this.getWorldLevelBaseConfig().getDiffLevelMin()) {
			return this.legalRatio(WorldLevelBaseConfig.PROPORTION + (this.getWorldLevel() - this.getWorldLevelBaseConfig().getDiffLevelMin() - level) * RATIO_PARAM1);
		}
		if (level <= this.getWorldLevel()) {
			return this.legalRatio(WorldLevelBaseConfig.PROPORTION);
		}
		if (level < this.getWorldLevel() + this.getWorldLevelBaseConfig().getDiffLevelMax()) {
			return this.legalRatio(WorldLevelBaseConfig.PROPORTION + (this.getWorldLevel() - level) * RATIO_PARAM2);
		}
		return 0;
	}
	
	/**
	 * 比率合法化
	 * @param ratio
	 * @return
	 */
	private float legalRatio(float ratio) {
		if (ratio < this.getWorldLevelBaseConfig().getMinRadio()) {
			ratio = this.getWorldLevelBaseConfig().getMinRadio();
		} else if (ratio > this.getWorldLevelBaseConfig().getMaxRadio()) {
			ratio = this.getWorldLevelBaseConfig().getMaxRadio();
		}
		return ratio;
	}
	
	/**
	 * 获取世界等级说明
	 * @return
	 */
	@Override
	public String getWorldLevelDesc() {
		return this.getWorldLevelBaseConfig().getDesc();
	}
	
	/**
	 * 获取世界等级基础配置
	 * @return
	 */
	private WorldLevelBaseConfig getWorldLevelBaseConfig() {
		return this.worldLevelBaseConfig;
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadWorldLevelBaseConfig(xlsPath);
		this.loadWorldLevelStageConfig(xlsPath);
		this.loadTimeTask();
		this.calcWorldLevel();
	}
	
	/**
	 * 加载配置
	 * @param xlsPath
	 */
	private void loadWorldLevelBaseConfig(String xlsPath) {
		try {
			String fileName = XlsSheetNameType.world_level_base.getXlsName();
			String sheetName = XlsSheetNameType.world_level_base.getSheetName();
			this.worldLevelBaseConfig = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, WorldLevelBaseConfig.class);
			this.worldLevelBaseConfig.init(fileName + " :");
		} catch (Exception e) {
			logger.error("WorldLevelAppImpl.loadWorldLevelBaseConfig error!", e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载配置
	 * @param xlsPath
	 */
	private void loadWorldLevelStageConfig(String xlsPath) {
		try {
			String fileName = XlsSheetNameType.world_level_stage.getXlsName();
			String sheetName = XlsSheetNameType.world_level_stage.getSheetName();
			this.worldLevelStageConfigList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, WorldLevelStageConfig.class);
			for (WorldLevelStageConfig config : this.worldLevelStageConfigList) {
				if (null == config) {
					continue;
				}
				config.init(fileName + " :");
			}
		} catch (Exception e) {
			logger.error("WorldLevelAppImpl.loadWorldLevelStageConfig error!", e);
			Log4jManager.checkFail();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadTimeTask() {
		Class clazz = CalcWorldLevel.class;
		try {
			int index = jobIndex.getAndDecrement();
			JobDetail jobDetail = new JobDetail("worldLevel_job_" + index, Scheduler.DEFAULT_GROUP, clazz);
			CronTrigger trigger = new CronTrigger("worldLevel_cron_" + index, null, TIME_CRON);
			GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
			logger.info("register worldLevel scheduler success,class=" + clazz.getName() + " cronExpression=" + TIME_CRON);
		} catch (Exception e) {
			Log4jManager.CHECK.error("register worldLevel scheduler success,class=" + clazz.getName() + " cronExpression=" + TIME_CRON, e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 计算世界等级
	 * @return
	 */
	@Override
	public void calcWorldLevel() {
		try {
			int worldLevel = Math.max(this.calcWorldLevelRank(), this.calcWorldLevelConfig());
			// 验证数据合法
			if (worldLevel < MIN_LEVEL || worldLevel > MAX_LEVEL) {
				return;
			}
			if (this.worldLevel == worldLevel) {
				return;
			}
			this.worldLevel = worldLevel;
			// 通知所有在线玩家世界等级改变
			for (RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()) {
				if (null == role) {
					continue;
				}
				this.pushRatioChange(role);
			}
		} catch (Exception e) {
			logger.error("WorldLevelAppImpl.calcWorldLevel error!", e);
		}
	}
	
	/**
	 * 计算排名等级
	 * @return
	 */
	private int calcWorldLevelRank() {
		List<RoleInstance> worldLevelList = GameContext.getRoleDAO().getRoleWorldLevelList(this.getWorldLevelBaseConfig().getMaxRank());
		if (Util.isEmpty(worldLevelList)) {
			return 0;
		}
		int minBattleScore = worldLevelList.get(worldLevelList.size() - 1).getBattleScore();
		for (RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()) {
			if (null == role) {
				continue;
			}
			if (inList(worldLevelList, role)) {
				continue;
			}
			if (role.getBattleScore() < minBattleScore) {
				continue;
			}
			worldLevelList.add(role);
		}
		// 如果取样不够，放弃计算，已配置为准
		if (worldLevelList.size() < this.getWorldLevelBaseConfig().getMaxRank()) {
			return 0;
		}
		this.sortWorldLevelListByScore(worldLevelList);
		worldLevelList = worldLevelList.subList(this.getWorldLevelBaseConfig().getMinRank() - 1, this.getWorldLevelBaseConfig().getMaxRank());
		this.sortWorldLevelListByLevel(worldLevelList);
		int flag = worldLevelList.size() % 2;
		int index = worldLevelList.size() / 2;
		if (0 == flag) {
			int level1 = worldLevelList.get(index - 1).getLevel();
			int level2 = worldLevelList.get(index).getLevel();
			return (level1 + level2) / 2;
		}
		return worldLevelList.get(index).getLevel();
	}
	
	/**
	 * 排序
	 * @param worldLevelList
	 */
	private void sortWorldLevelListByLevel(List<RoleInstance> worldLevelList) {
		Collections.sort(worldLevelList, new Comparator<RoleInstance>() {

			@Override
			public int compare(RoleInstance role1, RoleInstance role2) {
				if (role1.getLevel() > role2.getLevel()) {
					return 1;
				}
				if (role1.getLevel() < role2.getLevel()) {
					return -1;
				}
				return 0;
			}
			
		});
	}
	
	private void sortWorldLevelListByScore(List<RoleInstance> worldLevelList) {
		Collections.sort(worldLevelList, new Comparator<RoleInstance>() {

			@Override
			public int compare(RoleInstance role1, RoleInstance role2) {
				if (role1.getBattleScore() > role2.getBattleScore()) {
					return -1;
				}
				if (role1.getBattleScore() < role2.getBattleScore()) {
					return 1;
				}
				return 0;
			}
			
		});
	}
	
	/**
	 * 是否已在列表中
	 * @param list
	 * @param role
	 * @return
	 */
	private boolean inList(List<RoleInstance> list, RoleInstance role) {
		for (RoleInstance r : list) {
			if (null == r) {
				continue;
			}
			if (r.getRoleId().equals(role.getRoleId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 计算配置等级
	 * @return
	 */
	private int calcWorldLevelConfig() {
		WorldLevelStageConfig config = this.getWorldLevelStageConfig();
		if (null == config) {
			return 0;
		}
		return config.getLevel();
	}
	
	/**
	 * 获取世界等级配置
	 * @return
	 */
	private WorldLevelStageConfig getWorldLevelStageConfig() {
		if (Util.isEmpty(this.worldLevelStageConfigList)) {
			return null;
		}
		Date now = new Date();
		for (WorldLevelStageConfig config : this.worldLevelStageConfigList) {
			if (null == config) {
				continue;
			}
			if (config.isOnTime(now)) {
				return config;
			}
		}
		return null;
	}

	@Override
	public void stop() {
	}

	/**
	 * 同步比率变化
	 * @param role
	 */
	@Override
	public void pushRatioChange(RoleInstance role) {
		try {
			C1200_WorldLevelRatioRespMessage message = new C1200_WorldLevelRatioRespMessage();
			message.setWorldLevel((byte) this.worldLevel);
			message.setWorldRatio(this.getWorldLevelRatio(role));
			role.getBehavior().sendMessage(message);
		} catch (Exception e) {
			logger.error("WorldLevelAppImpl.pushRatioChange error!", e);
		}
	}

}
