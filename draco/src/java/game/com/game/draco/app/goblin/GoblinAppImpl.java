package com.game.draco.app.goblin;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import sacred.alliance.magic.app.ai.AiMessageListener;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.SortedValueMap;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.HatredTarget;
import sacred.alliance.magic.vo.MapGoblinContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.goblin.config.GoblinBaseConfig;
import com.game.draco.app.goblin.config.GoblinGeneralRewardConfig;
import com.game.draco.app.goblin.config.GoblinLocationConfig;
import com.game.draco.app.goblin.config.GoblinRealConfig;
import com.game.draco.app.goblin.config.GoblinRefreshConfig;
import com.game.draco.app.goblin.config.GoblinSecretConfig;
import com.game.draco.app.goblin.config.GoblinSecretRewardConfig;
import com.game.draco.app.goblin.map.GoblinJumpMapPoint;
import com.game.draco.app.goblin.map.MapLineInstanceCreatedListener;
import com.game.draco.app.goblin.vo.GoblinJumpPointInfo;
import com.game.draco.app.goblin.vo.GoblinRefreshInfo;
import com.game.draco.app.goblin.vo.GoblinSecretBossTemplate;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C3003_GoblinStatisticsRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GoblinAppImpl implements GoblinApp {
	public static final String Absolutely_Start_Time = "2001-01-01";
	public static final String Absolutely_End_Time = "2099-12-31";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int LINE_ID = 1;

	private GoblinBaseConfig goblinBaseConfig = new GoblinBaseConfig();// 基础配置
	private List<String> panelGoodsList = Lists.newArrayList();// 面板显示物品列表
	private List<String> refreshBossMapList = Lists.newArrayList();// 刷新BOSS地图列表
	private List<GoblinRefreshConfig> refreshBossList = Lists.newArrayList();// 刷新BOSS难度列表
	private Map<Byte, GoblinRealConfig> refreshRealMap = Maps.newHashMap();// 刷新真哥布林
	private List<GoblinSecretConfig> refreshSecretList = Lists.newArrayList();// 刷新密境BOSS
	private Map<String, GoblinLocationConfig> refreshLocationMap = Maps.newHashMap();// 密境BOSS位置
	private Map<String, GoblinGeneralRewardConfig> generalRewardMap = Maps.newHashMap();// 哥布林奖励
	private Map<Integer, List<GoblinSecretRewardConfig>> secretRewardMap = Maps.newHashMap();// 密境掉落组配置

	// 定时任务序列
	private static AtomicInteger jobIndex = new AtomicInteger(0);
	// 哥布林死亡监听
	private AiMessageListener listener = new GoblinDeathListener();
	// 保存刷新出的哥布林
	private Map<String, GoblinRefreshInfo> goblinInfoMap = Maps.newHashMap();
	// 真哥布林的地图列表
	private List<String> realGoblinMapList = Lists.newArrayList();
	// 哥布林密境对应跳转点集合
	private Map<String, GoblinJumpPointInfo> jumpMapPointMap = Maps.newHashMap();
	// 哥布林密境地图容器
	private MapGoblinContainer goblinMapContainer = new MapGoblinContainer();
	// 玩家密境对应列表
	private Map<String, String> roleSecretPointMap = Maps.newConcurrentMap();
	// 密境中哥布林BOSS信息
	private Map<String, GoblinSecretBossTemplate> secretGoblinInfoMap = Maps.newHashMap();
	// 普通地图对应的哥布林密境实例
	private Map<String, String> defaultToSecretInstanceMap = Maps.newHashMap();

	@Override
	public GoblinBaseConfig getGoblinBaseConfig() {
		return this.goblinBaseConfig;
	}

	private GoblinGeneralRewardConfig getGoblinGeneralRewardConfig(String bossId) {
		return this.generalRewardMap.get(bossId);
	}

	private List<GoblinSecretRewardConfig> getGoblinSecretRewardGroup(int dropGroup) {
		return this.secretRewardMap.get(dropGroup);
	}

	private GoblinRefreshConfig getGoblinRefreshConfig(Date date) {
		for (GoblinRefreshConfig config : this.refreshBossList) {
			if (null == config) {
				continue;
			}
			if (config.isOn(date)) {
				return config;
			}
		}
		return null;
	}

	private GoblinRealConfig getGoblinRealConfig(byte week) {
		return this.refreshRealMap.get(week);
	}

	@Override
	public GoblinSecretConfig getGoblinSecretConfig(Date date) {
		for (GoblinSecretConfig config : this.refreshSecretList) {
			if (null == config) {
				continue;
			}
			if (config.isOn(date)) {
				return config;
			}
		}
		return null;
	}

	@Override
	public GoblinLocationConfig getGoblinLocationConfig(String bossId) {
		return this.refreshLocationMap.get(bossId);
	}

	private GoblinRefreshInfo getGoblinRefreshInfo(String mapId) {
		return this.goblinInfoMap.get(mapId);
	}

	@Override
	public List<GoodsLiteItem> getPanelShowGoodsList() {
		if (Util.isEmpty(this.panelGoodsList)) {
			return null;
		}
		List<GoodsLiteItem> goodsList = Lists.newArrayList();
		for (String goodsId : this.panelGoodsList) {
			if (null == goodsId) {
				continue;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(Integer.parseInt(goodsId));
			if (null == goodsBase) {
				continue;
			}
			goodsList.add(goodsBase.getGoodsLiteItem());
		}
		return goodsList;
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadGoblinBaseConfig(xlsPath);
		this.loadGoblinPanelGoodsConfig(xlsPath);
		this.loadGoblinRefreshMapConfig(xlsPath);
		this.loadGoblinRefreshBossConfig(xlsPath);
		this.loadGoblinRefreshRealConfig(xlsPath);
		this.loadGoblinRefreshSecretConfig(xlsPath);
		this.loadGoblinLocationConfig(xlsPath);
		this.loadGoblinGeneralRewardConfig(xlsPath);
		this.loadGoblinSecretRewardConfig(xlsPath);
		// 注册分线地图创建监听
		GameContext.getEventBus().register(new MapLineInstanceCreatedListener());
		// 加载定时任务
		this.loadTimingTask();
		// 如果在活动时间内，创建并保存哥布林
		this.refreshGoblinOnStart();
	}

	@Override
	public boolean isOpen() {
		return !new Date().before(this.getGoblinBaseConfig().getActiveStartDate());
	}

	@Override
	public boolean isOnGoblinActive() {
		if (!this.isOpen()) {
			return false;
		}
		return this.getGoblinBaseConfig().isOpenNow();
	}

	private void loadGoblinBaseConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_base_config.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_base_config.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.goblinBaseConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, GoblinBaseConfig.class);
			this.goblinBaseConfig.init(fileInfo);
			
			// 处理地图逻辑
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(this.getGoblinBaseConfig().getSecretMapId());
			if (null == map) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("GoblinApp.loadGoblinBaseConfig : The map is not exist. mapId = " + this.getGoblinBaseConfig().getSecretMapId() + ",file = " + fileInfo);
			}
			Point point = MapUtil.randomCorrectRoadPoint(map.getMapId());
			if (null == point) {
				Log4jManager.checkFail(); 
				Log4jManager.CHECK.error("GoblinApp.loadGoblinBaseConfig : The map can not random point. mapId = " + this.getGoblinBaseConfig().getSecretMapId() + ",file = " + fileInfo);
			}
			// 将地图逻辑修改为goblin类型
			if (!map.getMapConfig().changeLogicType(MapLogicType.goblin)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("GoblinApp.loadGoblinBaseConfig : The map logic type config error. mapId= " + this.getGoblinBaseConfig().getSecretMapId());
			}
		} catch (Exception e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("GoblinApp.loadGoblinBaseConfig : " + fileInfo + "config error!", e);
		}
	}

	private void loadGoblinPanelGoodsConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_panel_goods.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_panel_goods.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.panelGoodsList = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
		} catch (Exception e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("GoblinApp.loadGoblinPanelGoodsConfig : " + fileInfo + "is config error!", e);
		}
	}

	private void loadGoblinRefreshMapConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_refresh_maps.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_refresh_maps.getXlsName();
		String fileInfo = sourceFile + "sheet :" + sheetName + " : ";
		StringBuffer buffer = new StringBuffer();
		try {
			List<String> mapList = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			boolean flag = false;
			if (Util.isEmpty(mapList)) {
				Log4jManager.CHECK.error("GoblinAppImpl.loadGoblinRefreshMapConfig error! " + fileInfo + "is Empty");
				Log4jManager.checkFail();
				return ;
			}
			for (String mapId : mapList) {
				sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
				if (null == map) {
					flag = true;
					buffer.append("GoblinApp.loadGoblinRefreshMapConfig : " + fileInfo + " is config error! " + mapId + " not exist!\n");
					continue;
				}
				MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
				if (null == mapConfig) {
					flag = true;
					buffer.append("GoblinApp.loadGoblinRefreshMapConfig : " + fileInfo + " is config error! " + mapId + " not exist!\n");
					continue;
				}
				if (!mapConfig.isHadLineMap() && mapConfig.getMapLogicType() != MapLogicType.defaultLogic) {
					buffer.append("GoblinApp.loadGoblinRefreshMapConfig : " + fileInfo + " is config error! " + mapId + " not HadLineMap or defaultLogic!\n");
					flag = true;
					continue;
				}
				if (null == MapUtil.randomCorrectRoadPoint(mapId)) {
					buffer.append("GoblinApp.loadGoblinRefreshMapConfig : " + fileInfo + " is config error! " + mapId + " can not random point!\n");
					flag = true;
					continue;
				}
				this.refreshBossMapList.add(mapId);
			}
			if (flag) {
				Log4jManager.CHECK.error(buffer.toString());
				Log4jManager.checkFail();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinAppImpl.loadGoblinRefreshMapConfig error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadGoblinRefreshBossConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_refresh_boss.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_refresh_boss.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.refreshBossList = XlsPojoUtil.sheetToList(sourceFile, sheetName, GoblinRefreshConfig.class);
			for (GoblinRefreshConfig config : this.refreshBossList) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinApp.loadGoblinRefreshBossConfig : " + fileInfo + " is config error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadGoblinRefreshRealConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_refresh_real.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_refresh_real.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.refreshRealMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GoblinRealConfig.class);
			for (GoblinRealConfig config : this.refreshRealMap.values()) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinApp.loadGoblinRefreshRealConfig : " + fileInfo + " is config error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadGoblinRefreshSecretConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_refresh_secret.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_refresh_secret.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.refreshSecretList = XlsPojoUtil.sheetToList(sourceFile, sheetName, GoblinSecretConfig.class);
			for (GoblinSecretConfig config : this.refreshSecretList) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinApp.loadGoblinRefreshSecretConfig : " + fileInfo + " is config error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadGoblinLocationConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_refresh_location.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_refresh_location.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.refreshLocationMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GoblinLocationConfig.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinApp.loadGoblinLocationConfig : " + fileInfo + " is config error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadGoblinGeneralRewardConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_general_reward.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_general_reward.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			this.generalRewardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GoblinGeneralRewardConfig.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinApp.loadGoblinGeneralRewardConfig : " + fileInfo + " is config error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadGoblinSecretRewardConfig(String xlsPath) {
		String sheetName = XlsSheetNameType.goblin_secret_reward.getSheetName();
		String sourceFile = xlsPath + XlsSheetNameType.goblin_secret_reward.getXlsName();
		String fileInfo = sourceFile + sheetName + " : ";
		try {
			List<GoblinSecretRewardConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, GoblinSecretRewardConfig.class);
			if (Util.isEmpty(list)) {
				return;
			}
			for (GoblinSecretRewardConfig config : list) {
				if (null == config) {
					continue;
				}
				if (this.secretRewardMap.containsKey(config.getDropGroupId())) {
					List<GoblinSecretRewardConfig> rewardList = this.secretRewardMap.get(config.getDropGroupId());
					rewardList.add(config);
				} else {
					List<GoblinSecretRewardConfig> rewardList = Lists.newArrayList();
					rewardList.add(config);
					this.secretRewardMap.put(config.getDropGroupId(), rewardList);
				}
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("GoblinApp.loadGoblinSecretRewardConfig : " + fileInfo + " is config error!", e);
			Log4jManager.checkFail();
		}
	}

	private void loadTimingTask() {
		this.loadSignTimingTask(GoblinStartTimingTask.class, this.getGoblinBaseConfig().getStartTimingExpression());
		this.loadSignTimingTask(GoblinEndTimingTask.class, this.getGoblinBaseConfig().getEndTimingExpression());
	}
	
	@SuppressWarnings("unchecked")
	private void loadSignTimingTask(Class classGoblin, String expression) {
		try {
			if (Util.isEmpty(expression)) {
				return;
			}
			int index = jobIndex.getAndDecrement();
			JobDetail jobDetail = new JobDetail("goblin_job_" + index, Scheduler.DEFAULT_GROUP, classGoblin);
			CronTrigger trigger = new CronTrigger("goblin_cron_" + index, null, expression);
			GameContext.getSchedulerApp().addToScheduler(jobDetail, trigger);
			logger.info("register goblin scheduler success,class=" + classGoblin.getName() + " cronExpression=" + expression);
		} catch (Exception ex) {
			Log4jManager.CHECK.error("register goblin scheduler exception,class=" + classGoblin.getName() + " cronExpression=" + expression, ex);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void stop() {
	}

	/**
	 * 启服时，如果在哥布林活动内，生成哥布林刷新信息并保存
	 */
	private void refreshGoblinOnStart() {
		if (!this.isOnGoblinActive()) {
			return;
		}
		this.refreshGoblin();
	}

	@Override
	public void refreshGoblin() {
		Date now = new Date();
		// 记录刷真哥布林的地图列表
		this.summonRealGoblin(this.getGoblinRealConfig((byte) DateUtil.getWeek(now)).getNumber());
		// 获得对应难度的哥布林列表
		List<String> goblinList = this.getGoblinRefreshConfig(now).getBossList();
		// 遍历地图，召唤哥布林
		for (String mapId : this.refreshBossMapList) {
			// 获得随机的路点
			Point point = MapUtil.randomCorrectRoadPoint(mapId);
			if (null == point) {
				continue;
			}
			// 随机出要刷新的哥布林ID
			String goblinId = this.getRandomGoblin(goblinList);
			// 获得NPC模版实例，并拷贝
			NpcTemplate goblinTemplate = GameContext.getNpcApp().getNpcTemplate(goblinId);
			if (null == goblinTemplate) {
				continue;
			}
			NpcTemplate goblinRandom = new NpcTemplate();
			BeanUtils.copyProperties(goblinTemplate, goblinRandom);
			// 给NPC模版赋值系数和档位
			goblinRandom.setSeriesId((byte) this.getGoblinBaseConfig().getRandomSeries());
			// 保存该哥布林信息
			this.saveRefreshGoblinInfo(mapId, point, goblinRandom);
			// 获得地图实例
			MapInstance mapInstance = GameContext.getMapApp().getExistMapInstance(mapId, LINE_ID);
			if (null == mapInstance) {
				continue;
			}
			// 在地图上刷新出哥布林
			this.summonGoblinOnMap(mapInstance, goblinRandom, point);
		}
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, GameContext.getI18n().getText(TextId.BROAD_CAST_GOBLIN_REFRESH), null, null);
	}

	private void saveRefreshGoblinInfo(String mapId, Point point, NpcTemplate goblin) {
		GoblinRefreshInfo refreshInfo = new GoblinRefreshInfo();
		refreshInfo.setMapId(mapId);
		refreshInfo.setPoint(point);
		refreshInfo.setGoblin(goblin);
		this.goblinInfoMap.put(mapId, refreshInfo);
	}

	/**
	 * 创建NPC实例并把它放到地图上
	 * @param mapInstance
	 * @param goblinId
	 * @param point
	 */
	private void summonGoblinOnMap(MapInstance mapInstance, NpcTemplate goblin, Point point) {
		// 创建NPC实例并把它放到地图上
		NpcInstance goblinInstance = mapInstance.summonCreateNpc(goblin.getNpcid(), point.getX(), point.getY());
		// 设置NPC的消失时间
		goblinInstance.setDisappearTime(this.getGoblinBaseConfig().getGoblinDisappearTime());// 变秒为毫秒
		// 将生成的模版放进去
		goblinInstance.setNpc(goblin);
		// 注册监听，哥布林死亡后通知
		goblinInstance.getAi().register(listener);
	}
	
	/**
	 * 保存属性真哥布林的地图集合
	 * 
	 * @param realGoblinNum
	 */
	private void summonRealGoblin(byte realGoblinNum) {
		if (realGoblinNum <= 0) {
			return;
		}
		int mapSize = this.refreshBossMapList.size();
		Set<String> realSet = Sets.newHashSet();
		// 防止死循环
		if (mapSize <= realGoblinNum) {
			realSet.addAll(this.refreshBossMapList);
		} else {
			while (realSet.size() < realGoblinNum) {
				realSet.add(this.refreshBossMapList.get(RandomUtil.randomInt(0, mapSize - 1)));
			}
		}
		// 保存刷新真哥布林的地图列表
		this.realGoblinMapList.addAll(realSet);
	}

	/**
	 * 再同已难度随机选择一个哥布林
	 * 
	 * @param goblinList
	 * @return
	 */
	private String getRandomGoblin(List<String> goblinList) {
		return goblinList.get(RandomUtil.randomInt(0, goblinList.size() - 1));
	}

	@Override
	public void goblinDeath(NpcInstance goblin) {
		String jumpMapId = goblin.getMapId();
		// 将该哥布林的刷新信息从Map中移除
		this.goblinInfoMap.remove(jumpMapId);
		this.giveGoblinReward(goblin);
		// 如果不是真哥布林
		if (!this.isRealGoblin(jumpMapId)) {
			return;
		}
		// 获得真哥布林死亡地图实例
		MapInstance mapInstance = goblin.getMapInstance();
		if (null == mapInstance) {
			return;
		}
		// 在真哥布林死亡的位置刷新出跳转点
		GoblinJumpMapPoint jumpPoint = new GoblinJumpMapPoint();
		jumpPoint.setMapid(jumpMapId);
		jumpPoint.setX(goblin.getMapX());
		jumpPoint.setY(goblin.getMapY());
		jumpPoint.setTomapid(this.getGoblinBaseConfig().getSecretMapId());
		// 添加跳转点
		this.refreshGoblinJumpPoint(jumpMapId, jumpPoint, goblin.getNpc().getNpcname());
		// 保存跳转点信息，删除时或重新刷新时使用
		GoblinJumpPointInfo jumpPointInfo = new GoblinJumpPointInfo();
		jumpPointInfo.setMapInstanceId(jumpPoint.getPointKey());
		jumpPointInfo.setMapId(jumpMapId);
		jumpPointInfo.setNpcName(goblin.getNpc().getNpcname());
		jumpPointInfo.saveGoblinJumpMapPoint(jumpPoint);
		this.jumpMapPointMap.put(jumpPoint.getPointKey(), jumpPointInfo);
		this.defaultToSecretInstanceMap.put(jumpMapId, jumpPoint.getPointKey());// 保存野图和密境的对应关系
	}
	
	/**
	 * 获取野图对应的密境实例Id
	 * @param mapId
	 * @return
	 */
	private String getSecretInstanceId(String mapId) {
		return this.defaultToSecretInstanceMap.get(mapId);
	}
	
	/**
	 * 从地图上刷新哥布林密境挑战点并世界广播
	 * @param mapInstance
	 * @param jumpPoint
	 * @param npcName
	 */
	private void refreshGoblinJumpPoint(String mapId, GoblinJumpMapPoint jumpPoint, String npcName) {
		MapInstance mapInstance = GameContext.getMapApp().getExistMapInstance(mapId, LINE_ID);
		if (null == mapInstance) {
			return ;
		}
		mapInstance.addJumpMapPoint(jumpPoint);
		// 系统广播
		String message = GameContext.getI18n().messageFormat(TextId.Goblin_Open_Secret_Map, mapInstance.getMap().getMapConfig().getMapdisplayname(), npcName);
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_System, message, null, null);
	}

	/**
	 * 发放哥布林金币潜能奖励
	 * @param goblin
	 */
	private void giveGoblinReward(NpcInstance goblin) {
		HatredTarget hatredTarget = goblin.getHatredTarget();
		if (null == hatredTarget) {
			return;
		}
		SortedValueMap roleMap = hatredTarget.getHatredMap();
		if (null == roleMap) {
			return;
		}
		GoblinGeneralRewardConfig config = this.getGoblinGeneralRewardConfig(goblin.getNpcid());
		if (null == config) {
			return;
		}
		for (Object roleId : roleMap.keySet()) {
			try {
				if (null == roleId) {
					continue;
				}

				MapInstance mapInstance = GameContext.getMapApp().getExistMapInstance(goblin.getMapId(), LINE_ID);
				if (null == mapInstance) {
					continue;
				}
				RoleInstance player = mapInstance.getRoleInstance(String.valueOf(roleId));
				if (null == player) {
					continue;
				}
				// 增加金币
				GameContext.getUserAttributeApp().changeRoleMoney(player, AttributeType.gameMoney, OperatorType.Add, config.getGoldMoney(), OutputConsumeType.goblin_general_award);
				// 增加潜能
				player.getBehavior().changeAttribute(AttributeType.potential, OperatorType.Add, config.getPotential());
				player.getBehavior().notifyAttribute();
			} catch (Exception e) {
				logger.error("GoblinAppImpl.giveGoblinReward error!", e);
			}
		}
	}

	/**
	 * 判断是否是真哥布林
	 * 
	 * @param mapId
	 * @return
	 */
	private boolean isRealGoblin(String mapId) {
		if (Util.isEmpty(this.realGoblinMapList)) {
			return false;
		}
		for (String id : this.realGoblinMapList) {
			if (id.equals(mapId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void refreshSignGoblinOrJumpPoint(MapInstance mapInstance) {
		String mapId = mapInstance.getMap().getMapId();
		// 如果没有该地图对应的哥布林信息
		GoblinRefreshInfo goblinInfo = this.getGoblinRefreshInfo(mapId);
		if (null != goblinInfo) {
			this.summonGoblinOnMap(mapInstance, goblinInfo.getGoblin(), goblinInfo.getPoint());
			// 如果有哥布林在地图上，一定没有传送点
			return ;
		}
		// 该地图对应的哥布林密境传送点信息
		String instanceId = this.getSecretInstanceId(mapId);
		if (Util.isEmpty(instanceId)) {
			return ;
		}
		GoblinJumpPointInfo pointInfo = this.getGoblinJumpPointInfo(instanceId);
		if (null != pointInfo) {
			this.refreshGoblinJumpPoint(pointInfo.getMapId(), pointInfo.createGoblinJumpMapPoint(), pointInfo.getNpcName());
		}
	}

	@Override
	public void goblinActiveEnd() {
		// 活动结束，清除哥布林刷新信息
		this.goblinInfoMap.clear();
		// 如果本次活动没有真哥布林，活动结束
		if (Util.isEmpty(this.realGoblinMapList)) {
			return;
		}
		// 踢出密境中玩家，销毁密境地图，销毁野图传送点
		this.goblinMapContainer.destroy();
		for (GoblinJumpPointInfo info : this.jumpMapPointMap.values()) {
			if (null == info) {
				continue;
			}
			this.removeSignJumpPoint(info.getMapInstanceId());
		}
		this.realGoblinMapList.clear();
		this.jumpMapPointMap.clear();
		this.roleSecretPointMap.clear();
		this.secretGoblinInfoMap.clear();
		this.defaultToSecretInstanceMap.clear();
		try {
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_System, GameContext.getI18n().getText(TextId.Goblin_Active_End), null, null);
		} catch (Exception e) {
			logger.error("", e);
		}
		
	}

	@Override
	public void removeSignJumpPoint(String mapInstanceId) {
		GoblinJumpPointInfo pointInfo = this.getGoblinJumpPointInfo(mapInstanceId);
		if (null == pointInfo) {
			return ;
		}
		this.jumpMapPointMap.remove(mapInstanceId);
		this.defaultToSecretInstanceMap.remove(pointInfo.getMapId());
		MapInstance map = GameContext.getMapApp().getExistMapInstance(pointInfo.getMapId(), LINE_ID);
		if (null == map) {
			return ;
		}
		// 删除传送点
		map.removeJumpMapPoint(pointInfo.createGoblinJumpMapPoint());
	}

	@Override
	public MapGoblinContainer getMapGoblinContainer() {
		return this.goblinMapContainer;
	}

	@Override
	public String getRoleSecretPointKey(String roleId) {
		return this.roleSecretPointMap.get(roleId);
	}

	@Override
	public void setRoleSecretPointKey(String roleId, String pointKey) {
		this.roleSecretPointMap.put(roleId, pointKey);
	}

	@Override
	public GoblinSecretBossTemplate getGoblinTemplate(String key) {
		return this.secretGoblinInfoMap.get(key);
	}

	@Override
	public void setGoblinTemplate(String key, GoblinSecretBossTemplate npcTemplate) {
		this.secretGoblinInfoMap.put(key, npcTemplate);
	}

	@Override
	public GoblinJumpPointInfo getGoblinJumpPointInfo(String mapId) {
		return this.jumpMapPointMap.get(mapId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void giveSecretBossReward(RoleInstance role, NpcInstance goblin) {
		this.sendSystemMessage(role, goblin);// 发送奖励系统消息
		GoblinLocationConfig locationConfig = this.getGoblinLocationConfig(goblin.getNpcid());
		if (null == locationConfig) {
			return;
		}
		String unionId = role.getUnionId();
		// 获得击杀公会所有玩家列表
		List<UnionMember> unionMemberList = GameContext.getUnionApp().getUnionMemberList(unionId);
		if (Util.isEmpty(unionMemberList)) {
			return;
		}
		// 获取哥布林掉落
		List<GoodsItem> goodsList = this.getGoblinDropList(locationConfig);
		C3003_GoblinStatisticsRespMessage message = new C3003_GoblinStatisticsRespMessage();
		message.setGoodsList(this.getGoodsLiteNameList(goodsList));
		Set<Integer> memberSet = Sets.newHashSet();
		// 发放奖励
		for (UnionMember member : unionMemberList) {
			if (null == member) {
				continue;
			}
			int dkp = locationConfig.getUnionDkp();
			// 如果玩家参与活动，发放额外的奖励
			boolean isPartake = false;
			if (goblin.getHatredTarget().inHatredMap(String.valueOf(member.getRoleId()))) {
				isPartake = true;
				dkp += locationConfig.getKillBossDkp();
				message.setKillDkp(dkp);
				this.sendDkpGiftPanel(String.valueOf(member.getRoleId()), message);
			}
			// 发放DKP奖
			this.sendDkpGiftMail(String.valueOf(member.getRoleId()), dkp, isPartake, goblin.getNpcname());
			memberSet.add(member.getRoleId());
		}
		// 添加拍卖物品
		this.addUnionReward(role.getUnionId(), memberSet, goodsList);
	}
	
	private void sendSystemMessage(RoleInstance role, NpcInstance goblin) {
		try {
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(role.getCopyBeforePoint().getMapid());
			if (null == map) {
				return;
			}
			GoblinSecretConfig config = this.getGoblinSecretConfig(new Date());
			if (null == config) {
				return;
			}
			String message = "";
			if (!config.getBossId9().equals(goblin.getNpc().getNpcid())) {
				message = GameContext.getI18n().messageFormat(TextId.Goblin_Secret_Kill_General, map.getMapConfig().getMapdisplayname(), goblin.getNpc().getNpcname(), role.getUnion().getUnionName(), role.getRoleName());
			} else {
				message = GameContext.getI18n().messageFormat(TextId.Goblin_Secret_Kill_Chest, map.getMapConfig().getMapdisplayname(), goblin.getNpc().getNpcname(), role.getUnion().getUnionName(), role.getRoleName());
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_System, message, null, null);
		} catch (Exception e) {
			logger.error("GoblinAppImpl.sendSystemMessage error!", e);
		}
	}
	
	/**
	 * 给单个玩家发送DKP奖励邮件
	 * @param member
	 * @param config
	 */
	private void sendDkpGiftMail(String roleId, int dkp, boolean isPartake, String npcName) {
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setRoleId(roleId);
			mail.setSendRole(GameContext.getI18n().getText(TextId.Goblin_Mail_Send_Role));
			if (isPartake) {
				mail.setTitle(GameContext.getI18n().messageFormat(TextId.Goblin_Secret_Reward_Title, npcName));
				mail.setContent(GameContext.getI18n().messageFormat(TextId.Goblin_Secret_Reward_Content, npcName));
			} else {
				mail.setTitle(GameContext.getI18n().messageFormat(TextId.Goblin_General_Reward_Title, npcName));
				mail.setContent(GameContext.getI18n().messageFormat(TextId.Goblin_General_Reward_Content, npcName));
			}
			mail.setDkp(dkp);
			GameContext.getMailApp().sendMailAsync(mail);
		} catch (Exception e) {
			logger.error("GoblinAppImpl.sendAllGiftMail error!", e);
		}
	}
	
	/**
	 * 发送结算面板
	 * @param roleId
	 * @param dkp
	 * @param goodsList
	 */
	private void sendDkpGiftPanel(String roleId, C3003_GoblinStatisticsRespMessage message) {
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if (null == role) {
			return;
		}
		role.getBehavior().sendMessage(message);
	}
	
	/**
	 * 获取掉落展示列表
	 * @param goodsList
	 * @return
	 */
	private List<GoodsLiteNamedItem> getGoodsLiteNameList(List<GoodsItem> goodsList) {
		if (Util.isEmpty(goodsList)) {
			return null;
		}
		List<GoodsLiteNamedItem> itemList = Lists.newArrayList();
		for (GoodsItem item : goodsList) {
			if (null == item) {
				continue;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(item.getGoodsId());
			if (null == goodsBase) {
				continue;
			}
			GoodsLiteNamedItem goodsItem = goodsBase.getGoodsLiteNamedItem();
			goodsItem.setNum(item.getGoodsNum());
			itemList.add(goodsItem);
		}
		return itemList;
	}
	
	/**
	 * 获取哥布林掉落
	 * @param locationConfig
	 * @return
	 */
	private List<GoodsItem> getGoblinDropList(GoblinLocationConfig locationConfig) {
		List<GoodsItem> itemList = Lists.newArrayList();
		if (Util.isEmpty(locationConfig.getDropgroupId())) {
			return null;
		}
		String[] gId = locationConfig.getDropgroupId().split(",");
		for (String id : gId) {
			List<GoblinSecretRewardConfig> dropGroupElement = this.getGoblinSecretRewardGroup(Integer.parseInt(id));
			int[] table = new int[dropGroupElement.size()];
			for (int i = 0; i < dropGroupElement.size(); i++) {
				GoblinSecretRewardConfig rewardConfig = dropGroupElement.get(i);
				table[i] = rewardConfig.getProbability();
			}
			// 根据概率获得掉落物品
			int index = Util.getProbabilityIndexByTable(table);
			GoblinSecretRewardConfig dropGroup = dropGroupElement.get(index);
			GoodsItem item = new GoodsItem();
			item.setGoodsInstanceId(GameContext.getGoodsApp().newGoodsInstanceId());
			item.setGoodsId(dropGroup.getGoodsId());
			item.setGoodsBinded(dropGroup.getGoodsBind());
			item.setGoodsNum(dropGroup.getGoodsNum());
			itemList.add(item);
		}
		return itemList;
	}

	/**
	 * 添加掉落拍卖
	 * @param unionId
	 * @param roleSet
	 * @param locationConfig
	 */
	private void addUnionReward(String unionId, Set<Integer> roleSet, List<GoodsItem> itemList) {
		if (Util.isEmpty(roleSet) || Util.isEmpty(itemList)) {
			return;
		}
		Auction auction = GameContext.getUnionAuctionApp().packagingAuction(unionId, (byte) -1, (byte) -1, itemList, roleSet);
		GameContext.getUnionAuctionApp().addAuctionGoods(unionId, auction);
	}

}
