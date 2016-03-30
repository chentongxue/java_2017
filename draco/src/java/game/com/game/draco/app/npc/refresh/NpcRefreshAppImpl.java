package com.game.draco.app.npc.refresh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.message.item.BossListItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.response.C0611_BossListRespMessage;


public class NpcRefreshAppImpl implements NpcRefreshApp{
	public static final Comparator<BossListItem> BOSS_ITEM_AVAILABLE_COMPARATOR = new BossItemComparator();
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathConfig pathConfig;
	private List<NpcRefreshConfig> npcRefreshConfigList ;
	
	//<规则id，规则配置>
	private Map<String, NpcRefreshRule> npcRefreshRuleMap ;
	
	//<地图ID，刷新配置>
	private Map<String, List<NpcRefreshConfig>> npcMapRefreshConfig = new HashMap<String, List<NpcRefreshConfig>>();
	
	/* 持久化刷新任务
	 * 避免分线地图实例销毁-创建-销毁，
	 * 反复走死亡刷新而不刷怪 
	 */
	//key = mapId+maplineId
	@Getter
	private Map<String, List<NpcRefreshTask>> npcRefreshTaskMap = new ConcurrentHashMap<String, List<NpcRefreshTask>>();
	
	//刷新前喊话间隔时间
	private final int refreshBeforeRunIntervalTime = 1 * 60 * 1000;
	
	//默认地图分线ID
	private final int defaultMapLineId = 1;
	//boss掉落
	private Map<String,BossLoot> bossLootMap ;
	//boss相关的刷新
	private Map<Short,NpcRefreshConfig> bossRefreshConfigMap = new LinkedHashMap<Short,NpcRefreshConfig>();
	
	
	@Override
	public void start(){
		this.load();
		for(NpcRefreshRule rule : this.npcRefreshRuleMap.values()){
			rule.init();
		}
		this.initNpcRefreshConfig();
		this.initNpcRefreshTask();
		this.npcRefreshBeforeSpeak();
		//init bossList
		this.initBossLoot();
	}
	
	private void initBossLoot(){
		if(null == this.bossLootMap){
			return ;
		}
		for(BossLoot loot: this.bossLootMap.values()){
			loot.init();
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void stop() {
		
	}
	
	//npc刷新前喊话
	private void npcRefreshBeforeSpeak(){
		Thread speakTask = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					try{
						if(Util.isEmpty(npcRefreshConfigList)){
							continue ;
						}
						for(List<NpcRefreshTask> list : npcRefreshTaskMap.values()){
							for(NpcRefreshTask task : list){
								task.refreshBeforeSpeak();
							}
						}
						
					}catch(Exception e){
						logger.error("",e);
					}finally{
						try {
							Thread.sleep(refreshBeforeRunIntervalTime);
						} catch (InterruptedException e) {
						}
					}
				}
				
			}
			
		});
		speakTask.start();
	}
	
	
	/**
	 * 创建地图实例时，放入NPC刷新任务
	 */
	@Override
	public void installMapNpcRefreshConfig(MapInstance instance){
		if(instance == null){
			return ;
		}
		
		String mapId = instance.getMap().getMapId();
		int lineId = instance.getLineId();
		if(lineId <= 0){
			lineId = this.defaultMapLineId;
		}
		
		String key = mapId + Cat.colon + lineId;
		List<NpcRefreshTask> list = this.npcRefreshTaskMap.get(key);
		if(Util.isEmpty(list)){
			List<NpcRefreshConfig> npcRefreshConfigs = this.npcMapRefreshConfig.get(mapId);
			if(Util.isEmpty(npcRefreshConfigs)){
				return ;
			}
			list = new ArrayList<NpcRefreshTask>();
			for(NpcRefreshConfig config : npcRefreshConfigs){
				NpcRefreshTask refreshTask = new NpcRefreshTask();
				refreshTask.setNpcRefreshConfig(config);
				refreshTask.setMapInstance(instance);
				list.add(refreshTask);
			}
			instance.setNpcRefreshConfigList(list);
			this.npcRefreshTaskMap.put(key, list);
			return ;
		}
		
		for(NpcRefreshTask task : list){
			task.setMapInstance(instance);
		}
		instance.setNpcRefreshConfigList(list);
	}
	
	
	private void load(){
		//npc刷新配置
		String configFile = pathConfig.getXlsPath()+XlsSheetNameType.npc_refresh_config.getXlsName();
		String configName = XlsSheetNameType.npc_refresh_config.getSheetName();
		npcRefreshConfigList = XlsPojoUtil.sheetToList(configFile, configName, NpcRefreshConfig.class);
		//npc刷新规则
		String ruleFile = pathConfig.getXlsPath()+XlsSheetNameType.npc_refresh_rule.getXlsName();
		String ruleName = XlsSheetNameType.npc_refresh_rule.getSheetName();
		npcRefreshRuleMap = XlsPojoUtil.sheetToMap(ruleFile, ruleName, NpcRefreshRule.class);
		//boss掉落
		String lootFile = pathConfig.getXlsPath()+XlsSheetNameType.npc_refresh_loot.getXlsName();
		String lootName = XlsSheetNameType.npc_refresh_loot.getSheetName();
		bossLootMap = XlsPojoUtil.sheetToMap(lootFile, lootName, BossLoot.class);
	}
	
	private void initNpcRefreshConfig(){
		for(NpcRefreshConfig config : this.npcRefreshConfigList){
			if(config == null){
				continue;
			}
			if(!config.init()){
				Log4jManager.CHECK.error("NpcRefreshConfig id="+config.getId()+" is error,check startDate, endDate,startDateRel, endDateRel ..");
				Log4jManager.checkFail();
				continue;
			}
			if(0 != config.getShow()){
				//需要显示在boss面板
				this.bossRefreshConfigMap.put(config.getId(), config);
			}
			
			config.setRule_1(this.buildNpcRefreshRule(config.get_1()));
			config.setRule_2(this.buildNpcRefreshRule(config.get_2()));
			config.setRule_3(this.buildNpcRefreshRule(config.get_3()));
			config.setRule_4(this.buildNpcRefreshRule(config.get_4()));
			config.setRule_5(this.buildNpcRefreshRule(config.get_5()));
			config.setRule_6(this.buildNpcRefreshRule(config.get_6()));
			config.setRule_7(this.buildNpcRefreshRule(config.get_7()));
			
			String mapId = config.getMapId();
			List<NpcRefreshConfig> list = this.npcMapRefreshConfig.get(mapId);
			if(Util.isEmpty(list)){
				List<NpcRefreshConfig> rfList = new ArrayList<NpcRefreshConfig>();
				rfList.add(config);
				this.npcMapRefreshConfig.put(mapId, rfList);
				continue;
			}
			list.add(config);
		}
	}
	
	
	
	//初始化所配置的地图刷怪默认任务
	private void initNpcRefreshTask(){
		for(NpcRefreshConfig config : this.npcRefreshConfigList){
			if(config == null){
				continue ;
			}
			
			NpcRefreshTask refreshTask = new NpcRefreshTask();
			refreshTask.setNpcRefreshConfig(config);
			//初始化刷新出生点
			refreshTask.resetBornPoint();
			
			String mapId = config.getMapId();
			String key = mapId + Cat.colon + this.defaultMapLineId;
			List<NpcRefreshTask> list = this.npcRefreshTaskMap.get(key);
			
			if(Util.isEmpty(list)){
				List<NpcRefreshTask> ntList = new ArrayList<NpcRefreshTask>();
				ntList.add(refreshTask);
				this.npcRefreshTaskMap.put(key, ntList);
				continue ;
			}
			list.add(refreshTask);
		}
	}
	
	
	//构建刷怪规则
	private NpcRefreshRule buildNpcRefreshRule(int ruleId){
		if(ruleId <= 0){
			return null;
		}
		return this.npcRefreshRuleMap.get(String.valueOf(ruleId)) ;
	}
	
	
	
	//地图销毁时需判定
	//如果怪物已经刷新出来（未死亡）；则设置为刷新状态，已便创建地图后立即刷新出来
	@Override
	public void mapDestroyRefreshProcess(MapInstance mapInstance){
		try{
			List<NpcRefreshTask> npcRefreshTaskList = mapInstance.getNpcRefreshConfigList();
			if(Util.isEmpty(npcRefreshTaskList)){
				return ;
			}
			
			for(NpcRefreshTask task : npcRefreshTaskList){
				if(task.getRefreshType() == RefreshType.none){
					//此时NPC为已经刷出状态
					task.setRefreshType(RefreshType.doing);
				}
			}
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	//npc死亡时刷新逻辑
	@Override
	public void npcDeathRefreshPross(NpcInstance npcInstance, AbstractRole owner){
		try{
			 NpcRefreshTask refreshTask = npcInstance.getNpcRefreshTask();
			 if(refreshTask == null){
				 return ;
			 }
			 RefreshType refreshType = refreshTask.getRefreshType();
			 //结束刷新消失，返回
			 if(refreshType == RefreshType.disappear){
				 return ;
			 }
			 refreshTask.setRefreshType(RefreshType.death);
			 //重新刷新出生点
			 refreshTask.resetBornPoint();
			 NpcRefreshRule rule = refreshTask.getNpcRefreshRule();
			 MapInstance mapInstance = npcInstance.getMapInstance();
			 if(mapInstance == null){
				 return ;
			 }
			 //死亡喊话
			 rule.speak(rule.getDeathChannel(), rule.getDeathContent(), npcInstance, mapInstance);
			 //还原出生喊话状态
			 refreshTask.setHadBornSpeak(false);
			 //打印死亡日志
			 this.printDeathLog(npcInstance, refreshTask, rule.getRuleId(), owner);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	private void printDeathLog(NpcInstance npcInstance, NpcRefreshTask task, int ruleId, AbstractRole owner){
		try{
			NpcRefreshConfig config = task.getNpcRefreshConfig();
			StringBuffer sb = new StringBuffer();
			sb.append(config.getId());
			sb.append(Cat.pound);
			sb.append(ruleId);
			sb.append(Cat.pound);
			sb.append(npcInstance.getNpcid());
			sb.append(Cat.pound);
			sb.append(npcInstance.getRoleId());
			sb.append(Cat.pound);
			sb.append(config.getMapId());
			sb.append(Cat.pound);
			sb.append(task.getMapInstance().getInstanceId());
			sb.append(Cat.pound);
			sb.append(config.getMapX());
			sb.append(Cat.pound);
			sb.append(config.getMapY());
			sb.append(Cat.pound);
			sb.append(DateUtil.date2FormatDate(System.currentTimeMillis(), NpcRefreshRule.format));
			sb.append(Cat.pound);
			sb.append(owner.getRoleId());
			sb.append(Cat.pound);
			sb.append(owner.getRoleName());
			Log4jManager.NPC_REFRESH_DEATH_LOG.info(sb.toString());
		}catch(Exception e){
			logger.error("printRefreshLog error", e);
		}
	}
	
	public PathConfig getPathConfig() {
		return pathConfig;
	}
	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}

	@Override
	public List<NpcRefreshConfig> getBossRefreshConfigList() {
		return this.npcRefreshConfigList ;
	}

	@Override
	public NpcRefreshConfig getBossRefreshConfig(short id) {
		if(null == this.bossRefreshConfigMap){
			return null ;
		}
		return this.bossRefreshConfigMap.get(id);
	}

	@Override
	public BossLoot getBossLoot(String lootId) {
		if(Util.isEmpty(lootId)){
			return null ;
		}
		return this.bossLootMap.get(lootId);
	}
	/**
	 * 
	 */
	@Override
	public C0611_BossListRespMessage getBossListRespMessage(RoleInstance role){
		C0611_BossListRespMessage respMsg = new C0611_BossListRespMessage();
		//npc_refresh.xls->config
		List<NpcRefreshConfig> bossList = GameContext.getNpcRefreshApp().getBossRefreshConfigList();
		if(Util.isEmpty(bossList)){
			return respMsg;
		}
		 List<BossListItem> bossItems = new ArrayList<BossListItem>();
		 for(NpcRefreshConfig cf : bossList){
			 try {
				if (0 == cf.getShow() || !cf.validateRefreshDate()) {
					//不显示或已过期
					continue;
				}
				String npcId = cf.getNpcId();

				NpcTemplate npc = GameContext.getNpcApp().getNpcTemplate(npcId);

				
				BossListItem item = new BossListItem();
				item.setId(cf.getId());
				item.setBossName(npc.getNpcname());
				item.setBossRes((short)npc.getResid());
				
				item.setSeriesId(npc.getSeriesId());
				item.setGearId(npc.getGearId());
				
				item.setTimeInfo(cf.getTimeInfo());
				item.setIconRatio(cf.getIconRatio());
				
				sacred.alliance.magic.app.map.Map mapInfo = GameContext.getMapApp().getMap(cf.getMapId());
				if(null != mapInfo){
					item.setMapName(mapInfo.getMapConfig().getMapdisplayname());
					item.setRoleLevelMin((short)mapInfo.getMapConfig().getMinTransLevel());
					item.setRoleLevelMax((short)mapInfo.getMapConfig().getMaxTransLevel());
				}
				
				//lootItems
				BossLoot loot = GameContext.getNpcRefreshApp().getBossLoot(cf.getLootId());
				if(null != loot){
					List<GoodsLiteItem> lootItems = new ArrayList<GoodsLiteItem>();
					for(Integer goodsId : loot.getGoodsList()){
						GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
						if(null == gb){
							continue ;
						}
						lootItems.add(gb.getGoodsLiteItem());
					}
					item.setLootItems(lootItems);
				}
				
				NpcRefreshTask task = GameContext.getNpcRefreshApp().getCurrentRefreshTask(cf);
				if(null != task){
					RefreshType refreshType = task.getRefreshType();
					if(refreshType == RefreshType.none ){
						//boss存活
						//item.setBossStatus(GameContext.getI18n().getText(TextId.BOSS_BORNED));
					}else if(refreshType == RefreshType.doing || refreshType == RefreshType.init){
						//设置刷新时间
						long time = task.getRefreshTime() - System.currentTimeMillis();
						if(time > 0){
							//item.setBossStatus(GameContext.getI18n().getText(TextId.BOSS_BORNED)); //次数状态表示时间倒计时到0时刻
							item.setRemainSecond((int)(time/1000));
						}else{
							//item.setBossStatus(GameContext.getI18n().getText(TextId.BOSS_BORNED_1));
							item.setRemainSecond(0);
						}
					}else if(refreshType == RefreshType.death || refreshType == RefreshType.disappear){
						//设置刷新时间
						long time = task.getRefreshTime() - System.currentTimeMillis();
						if(time > 0){
							item.setRemainSecond((int)(time/1000));
						}
						//item.setBossStatus(GameContext.getI18n().getText(TextId.BOSS_KILLED));
					}
				}
				bossItems.add(item);
			}catch(Exception ex){
				logger.error("",ex);
			 }
		 }
		 //排序
//		 Collections.sort(bossItems, BOSS_ITEM_AVAILABLE_COMPARATOR);
		 respMsg.setBossItems(bossItems);
		 return respMsg;
	}
	@Override
	public Result enterBossMap(RoleInstance role, short id) {
		Result result =  new Result().failure();
		
		NpcRefreshConfig cf = GameContext.getNpcRefreshApp().getBossRefreshConfig(id);
		if(cf == null){
			return result.setInfo(getText(TextId.BOSS_CONFIG_NONE));
		}
		sacred.alliance.magic.app.map.Map mapInfo = GameContext.getMapApp().getMap(cf.getMapId());
		if(null == mapInfo){
			return result.setInfo(getText(TextId.BOSS_MAP_GONE));
		}
		if(role.getLevel() > mapInfo.getMapConfig().getMaxTransLevel()){
			return result.setInfo(getText(TextId.BOSS_ROLELEVEL_LIMIT_MAX));
		}
		if(role.getLevel() < mapInfo.getMapConfig().getMinTransLevel()){
			return result.setInfo(getText(TextId.BOSS_ROLELEVEL_LIMIT));
		}
//		Point point = MapUtil.randomCorrectRoadPoint(tomapid);
		try {
			role.getBehavior().changeMap(new Point(cf.getMapId(), cf.getMapX(), cf.getMapY()));
		} catch (ServiceException e) {
			logger.error(".NpcRefreshAppImpl.enterBossMap() err: {}" , e);
			return result.setInfo(getText(TextId.BOSS_MAP_ENTER_ERROR));
		}
		return result.success();
	}

	private String getText(String text) {
		return GameContext.getI18n().getText(text);
	}
	
	@Override
	public NpcRefreshTask getCurrentRefreshTask(NpcRefreshConfig config){
		
		int lineId = this.defaultMapLineId;
		String mapId = config.getMapId();
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
		if(null != mapConfig && mapConfig.isHadLineMap()){
			//分线地图取第1分线
			lineId = 1;
			//!!!!!!!!!!!!!!
			//分线地图不取
			return null ;
		}
		String key = config.getMapId() + Cat.colon + lineId;
		List<NpcRefreshTask> taskList = this.npcRefreshTaskMap.get(key);
		if(Util.isEmpty(taskList)){
			return null ;
		}
		for(NpcRefreshTask task : taskList){
			NpcRefreshConfig taskConfig = task.getNpcRefreshConfig();
			if(null == taskConfig){
				continue ; 
			}
			if(taskConfig.getId() == config.getId()){
				return task ;
			}
		}
		return null ;
	}

}
