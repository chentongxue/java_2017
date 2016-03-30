package com.game.draco.app.union.battle;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.worldmap.WorldMapInfo;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapUnionBattleContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.exchange.ExchangeConstant;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.union.FunType;
import com.game.draco.app.union.battle.config.UnionBattleAppConfig;
import com.game.draco.app.union.battle.config.UnionBattleConfig;
import com.game.draco.app.union.battle.config.UnionBattleKillMsgConfig;
import com.game.draco.app.union.battle.config.UnionBattleKilledMsgConfig;
import com.game.draco.app.union.battle.config.UnionBattleMapBuffConfig;
import com.game.draco.app.union.battle.domain.UnionBattle;
import com.game.draco.app.union.battle.domain.UnionBattleRank;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.item.UnionBattleItem;
import com.game.draco.message.item.UnionBattleOccupyInfoItem;
import com.game.draco.message.item.UnionBattleRoleRankItem;
import com.game.draco.message.item.UnionBattleWinUnionItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C2534_UnionBattleDefenderExchangeNotifyMessage;
import com.game.draco.message.push.C2537_UnionBattleMapRenamableNotifyMessage;
import com.game.draco.message.push.C2538_UnionBattleNewCapitalMapNameNotifyMessage;
import com.game.draco.message.push.C2539_UnionBattleJoinNotifyMessage;
import com.game.draco.message.response.C2530_UnionBattlePanelRespMessage;
import com.game.draco.message.response.C2532_UnionBattleOccupyInfoRespMessage;
import com.game.draco.message.response.C2533_UnionBattleInfoRespMessage;
import com.game.draco.message.response.C2535_UnionBattleWinInfoRespMessage;
import com.game.draco.message.response.C2536_UnionBattleRenameRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class UnionBattleAppImpl implements UnionBattleApp {
	//公会战连胜次数排序
	private static class UnionBattleWinNumCmp implements Comparator<UnionBattleWinUnionItem>{
		@Override
		public int compare(UnionBattleWinUnionItem o1, UnionBattleWinUnionItem o2) {
			return o2.getWinNumber() - o1.getWinNumber();
		}
	}
	private static final Comparator<UnionBattleWinUnionItem> UNIONBATTLE_WIN_NUMBER_COMPARATOR = new UnionBattleWinNumCmp();

	//MVP排序
	private static class UnionBattleMvpCmp implements Comparator<UnionBattleRank>{
		@Override
		public int compare(UnionBattleRank o1, UnionBattleRank o2) {
			return o2.getDkp() - o1.getDkp();
		}
	}
	private static final Comparator<UnionBattleRank> UNIONBATTLE_MVP_COMPARATOR = new UnionBattleMvpCmp();

	//公会战连胜次数排序
	private static class UnionBattleRankCmp implements Comparator<UnionBattleRoleRankItem>{
		@Override
		public int compare(UnionBattleRoleRankItem o1, UnionBattleRoleRankItem o2) {
			int comparison = o2.getKillNum() - o2.getKillNum();
			if(comparison == 0){
				comparison = o1.getKilledNum() - o2.getKilledNum();
			}
			if(comparison == 0){
				comparison = o2.getDkp() - o1.getDkp();
			}
			return comparison;
		}
	}
	private static final Comparator<UnionBattleRoleRankItem> UNIONBATTLE_RANK_COMPARATOR = new UnionBattleRankCmp();
	
	private static Logger logger = LoggerFactory.getLogger(UnionBattleAppImpl.class);
	/** 连胜多少次后可以改名 */
	private static final int RENAMABLE_WIN_NUM = 1;
	private static final int NEW_MAP_NAME_NUM = 6;
//	private static final int RENAMABLE_WIN_NUM = 4;
	
	private UnionBattleAppConfig unionBattleAppConfig;
	private Map<Integer, UnionBattleConfig> unionBattleConfigMap;
	private Map<Integer, UnionBattleKilledMsgConfig> unionBattleKilledMsgConfigMap;
	private Map<Integer, UnionBattleKillMsgConfig> unionBattleKillMsgConfigMap;
	
	private Map<String, List<UnionBattleConfig>> npcMenuMap = Maps.newHashMap();
	// key = battleId
	private Map<Integer, UnionBattle> unionBattleMap = Maps.newConcurrentMap();
	private Map<Integer, String> originalDefenderMap = Maps.newHashMap();

	// 活动是否结束
	private AtomicBoolean activeEnd = new AtomicBoolean(false);

	// 连续击杀,key = battleId
	private Map<Integer, Map<String, Integer>> allKillMap = Maps.newConcurrentMap();

	// 角色累计得到的DKP
	private Map<String, Integer> roleDkpMap = Maps.newConcurrentMap();

	private Active active;

	//<mapIndex, batteleId> 
	private Map<Byte, Integer>mapIndexBattleIdMap = Maps.newHashMap();
	
	//公会战击杀、DKP排行榜数据
	private Map<Integer, UnionBattleRank> battleRankMap = Maps.newConcurrentMap();
	
	//所有参加公会的角色ID
	private List<Integer> roleIds = Lists.newArrayList();
	
	//公会战连胜次数排行
	List<UnionBattleWinUnionItem> winlist = Lists.newArrayList();
	//角色击杀获得DKP等数据排行
	List<UnionBattleRoleRankItem> roleRanklist = getUnionBattleRoleRankItemList();
	//攻击方BUFF
	private List<Short> buffIds;
	
	//MVP 公会ID，角色Id
	Map<String, Integer> mvpMap = Maps.newConcurrentMap();
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() { 
		initActive();
		loadUnionBattleAppConfig();
		loadUnionBattleConfigMapConfig();
		loadUnionBattleKilledMsgConfigMap();
		loadUnionBattleKillMsgConfigMap();
		loadUnionBattleMapBuffConfigList();
		intUnionBattleData();
		initBattleRank();
	}
	private void loadUnionBattleMapBuffConfigList() {
		List<UnionBattleMapBuffConfig> buffList = loadConfigList(XlsSheetNameType.union_battle_buff_config, UnionBattleMapBuffConfig.class);
		buffIds = Lists.newArrayList();
		for(UnionBattleMapBuffConfig cf : buffList){
			buffIds.add(cf.getMapBuffId());
		}
	}

	//启服时，初始化公会战角色数据
	private void initBattleRank(){
		List<UnionBattleRank> list = GameContext.getBaseDAO().selectAll(UnionBattleRank.class);
		for(UnionBattleRank ubr : list){
			battleRankMap.put(ubr.getRoleId(), ubr);
		}
	}
	
	// 检查活动是否配置
	private void initActive() {
		active = GameContext.getActiveApp().getOnlyOneActive(
				ActiveType.UnionBattle);
		if (active == null) {
//			checkFail("unionBattleApp.initActive() fail: UnionBattle active not configured");
		}
	}

	/**
	 * 清除未开始的公会战db数据
	 */
	private void intUnionBattleData() {
		try {
			List<UnionBattle> battles = selectUnionBattleList();
			for (UnionBattle battle : battles) {
				if (!unionBattleConfigMap.containsKey(battle.getBattleId())) {
					deleteUnionBattle(battle.getBattleId());
					continue;
				}
				unionBattleMap.put(battle.getBattleId(), battle);
				originalDefenderMap.put(battle.getBattleId(), battle.getUnionId());
			}
		} catch (Exception e) {
			logger.error("unionBattleApp.intUnionBattleData()", e);
		}
	}

	private List<UnionBattle> selectUnionBattleList() {
		return GameContext.getBaseDAO().selectAll(UnionBattle.class);
	}

	@Override
	public UnionBattle getUnionBattle(int battleId) {
		return getUnionBattleFromMap(battleId);
	}

	@Override
	public UnionBattle getUnionBattleNotNull(int battleId) {
		UnionBattle battle = getUnionBattleFromMap(battleId);
		if (battle != null) {
			return battle;
		}
		return addNewUnionBattle(battleId);
	}


	/**
	 * 通过地图Id获得
	 */
	@Override
	public UnionBattle getUnionBattleByMapId(String mapId) {
		if (Util.isEmpty(unionBattleConfigMap) || Util.isEmpty(mapId)) {
			return null;
		}
		UnionBattleConfig cf = getUnionBattleConfigByMapId(mapId);
		if (cf == null) {
			return null;
		}
		return getUnionBattle(cf.getBattleId());
	}
	@Override
	public String getNewMapNameByMapIndex(byte mapIndex){
		int battleId = getBattleIdByMapIndex(mapIndex);
		UnionBattle ub = getUnionBattle(battleId);
		if(ub == null){
			return null;
		}
		return ub.getNewMapName();
	}
	
	@Override
	public UnionBattleConfig getUnionBattleConfigByMapId(String mapId) {
		if (Util.isEmpty(unionBattleConfigMap) || Util.isEmpty(mapId)) {
			return null;
		}
		for (UnionBattleConfig cf : unionBattleConfigMap.values()) {
			if (mapId.equals(cf.getMap1()) || mapId.equals(cf.getMap2())) {
				return cf;
			}
		}
		return null;
	}

	@Override
	public Integer getUnionBattleIdByMapId(String mapId) {
		if (Util.isEmpty(unionBattleConfigMap) || Util.isEmpty(mapId)) {
			return null;
		}
		for (UnionBattleConfig cf : unionBattleConfigMap.values()) {
			if (mapId.equals(cf.getMap1()) || mapId.equals(cf.getMap2())) {
				return cf.getBattleId();
			}
		}
		return null;
	}

	@Override
	public String getUnionNameByMapId(String mapId) {
		UnionBattle unionBattle = getUnionBattleByMapId(mapId);
		if(unionBattle == null){
			return null;
		}
		String unionId = unionBattle.getUnionId();
		if(Util.isEmpty(unionId)){
			return null;
		}
		Union un = GameContext.getUnionApp().getUnion(unionId);
		if(un == null){
			return null;
		}
		return un.getUnionName();
	}
	private UnionBattle getUnionBattleFromMap(int battleId) {
		return unionBattleMap.get(battleId);
	}

	@Override
	public void saveUpdateDb(UnionBattle battle) {
		GameContext.getBaseDAO().saveOrUpdate(battle);
	}

	@Override
	public void deleteUnionBattle(int battleId) {
		GameContext.getBaseDAO().delete(UnionBattle.class,
				UnionBattle.BATTLE_ID, battleId);
	}

	@Override
	public void stop() {

	}

	private void loadUnionBattleKillMsgConfigMap() {
		unionBattleKillMsgConfigMap = loadConfigMap(
				XlsSheetNameType.union_battle_kill_msg_config,
				UnionBattleKillMsgConfig.class, false);
	}

	private void loadUnionBattleKilledMsgConfigMap() {
		unionBattleKilledMsgConfigMap = loadConfigMap(
				XlsSheetNameType.union_battle_killed_msg_config,
				UnionBattleKilledMsgConfig.class, false);
	}

	private void loadUnionBattleConfigMapConfig() {
		unionBattleConfigMap = loadConfigMap(
				XlsSheetNameType.union_battle_config, UnionBattleConfig.class,
				false);

		//世界地图索引不能重复
		Set<Byte> mapIndexSet = Sets.newHashSet();
		// check mapId不能重复
		Set<String> mapSet = Sets.newHashSet();
		for (UnionBattleConfig cf : unionBattleConfigMap.values()) {
			mapIndexBattleIdMap.put(cf.getMapIndex(), cf.getBattleId());
			
			checkMapConfig(mapSet, cf.getMap1());
			checkMapConfig(mapSet, cf.getMap2());
			//世界地图索引不能重复
			checkMapIndexConfig(mapIndexSet, cf.getMapIndex());
			
			checkUnionBattleConfig(cf);
			addNpcMenuMap(cf);
		}
	}

	private void addNpcMenuMap(UnionBattleConfig cf) {
		if (null == cf) {
			return;
		}
		String npcId = cf.getNpcId();
		if (!Util.isEmpty(npcId)) {
			if (!npcMenuMap.containsKey(npcId)) {
				npcMenuMap.put(npcId, new ArrayList<UnionBattleConfig>());
			}
			npcMenuMap.get(npcId).add(cf);
		}
	}

	private void checkMapIndexConfig(Set<Byte> mapIndexSet, byte mapIndex) {
		if (mapIndexSet.contains(mapIndex)) {
			checkFail("UnionBattleAppImpl.checkMapDuplicate() err: duplicate mapIndex config configured in union_battle.xls -> battle, mapIndex =  "
					+ mapIndex);
		}
		mapIndexSet.add(mapIndex);
	}

	private void checkMapConfig(Set<String> mapSet, String mapId) {
		checkFailMap(mapId);
		if (mapSet.contains(mapId)) {
			checkFail("UnionBattleAppImpl.checkMapDuplicate() err: duplicate mapId config configured in union_battle.xls -> battle, mapId =  "
					+ mapId);
		}
		mapSet.add(mapId);
	}

	/**
	 * 判断是否BOSS存在
	 * 
	 * @param cf
	 * @date 2014-12-5 下午07:22:58
	 */
	private void checkUnionBattleConfig(UnionBattleConfig cf) {
		if (cf == null) {
			return;
		}
		cf.init();
	}

	private void checkFailMap(String mapId) {
		// 地图
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(
				mapId);
		if (null == map) {
			checkFail("UnionBattleAppImpl.checkFailMap() : The map is not exist. mapId = "
					+ mapId + "in union_battle.xls battle");
			return;
		}
		Point point = MapUtil.randomCorrectRoadPoint(map.getMapId());
		if (null == point) {
			checkFail("UnionBattleAppImpl.checkFailMap() : The map can not random point. mapId = "
					+ mapId);
		}
		// 将地图类型为公会战
		if (!map.getMapConfig().changeLogicType(MapLogicType.unionBattle)) {
			checkFail("UnionBattleAppImpl.checkFailMap() : The map logic type config error. mapId= "
					+ mapId);
		}
	}

	private void loadUnionBattleAppConfig() {
		String fileName = XlsSheetNameType.union_battle_app_config.getXlsName();
		String sheetName = XlsSheetNameType.union_battle_app_config
				.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		try {
			this.unionBattleAppConfig = XlsPojoUtil.getEntity(sourceFile,
					sheetName, UnionBattleAppConfig.class);
			if (null == unionBattleAppConfig) {
				this.checkFail("load Excel error: " + fileName + ",sheet="
						+ sheetName + " is not config!");
			}
		} catch (Exception e) {
			this.checkFail("load Excel error: " + fileName + ",sheet="
					+ sheetName + " is not config!");
		}
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			checkFail("not config the " + clazz.getSimpleName() + " ,file="
					+ sourceFile + " sheet=" + sheetName);
		}
		return map;
	}
	private <T> List<T> loadConfigList(XlsSheetNameType xls,Class<T> t){
		  List<T> list = null;
		  String fileName = xls.getXlsName();
		  String sheetName = xls.getSheetName();
		  String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		  try {
		   list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
		     t);
		  } catch (Exception e) {
		   Log4jManager.CHECK.error("load "+t.getSimpleName()+" error:fileName=" + fileName+ ",sheetName=" + sheetName);
		   Log4jManager.checkFail();
		   
		  }
		  if(list == null){
		   Log4jManager.CHECK.error("load "+t.getSimpleName()+" error: result is null fileName=" + fileName+ ",sheetName=" + sheetName);
		   Log4jManager.checkFail();
		  }
		  return list;
	}
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	private UnionBattleConfig getUnionBattleConfig(int battleId) {
		return unionBattleConfigMap.get(battleId);
	}
	
	@Override
	public Result renameMap(RoleInstance role, byte mapIndex, String newMapName) {
		int battleId = getBattleIdByMapIndex(mapIndex);
		Result result = new Result().failure();
		if(Util.isEmpty(newMapName)){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_FAIL));
		}
		if(newMapName.length() > NEW_MAP_NAME_NUM){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_NEWNAME_BEYOND_LENGTH));
		}
		UnionBattleConfig cf = getUnionBattleConfig(battleId);
		if(!isRenamable(role, cf)){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_FAIL));
		}
		UnionBattle ub = getUnionBattle(battleId);
		if(ub == null){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_FAIL));
		}
		if(ub.getInstanceId() != 0 && ub.getInstanceId() == ub.getInstanceRenamedId()){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_DUPLICATE));
		}
		//过滤敏感词
		String illegalChar = GameContext.getIllegalWordsService().findIllegalChar(newMapName);
		if(illegalChar != null){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_ILLEGAL));
		}
		//过滤禁用词
		String forbidChar = GameContext.getIllegalWordsService().findForbiddenChar(newMapName);
		if(forbidChar != null){
			return result.setInfo(getText(TextId.UNION_BATTLE_RNAMEMAP_FORBIDDEN));
		}
		ub.setNewMapName(newMapName);
		ub.setInstanceRenamedId(ub.getInstanceId());
		saveUpdateDb(ub);
		
		//通知其他玩家
 		C2536_UnionBattleRenameRespMessage msg = new C2536_UnionBattleRenameRespMessage();
		msg.setMapIndex(mapIndex);
		msg.setNewMapName(newMapName);
		for(RoleInstance r : GameContext.getOnlineCenter().getAllOnlineRole()){
			r.getBehavior().sendMessage(msg);
		}
		//通知主城所有角色新地图名称
		UnionBattle capitalBattle = getCapitalUnionBattle();
		if(capitalBattle != null && capitalBattle.getBattleId() == ub.getBattleId()){
			C2538_UnionBattleNewCapitalMapNameNotifyMessage newCapitalNameMsg = new C2538_UnionBattleNewCapitalMapNameNotifyMessage();
			newCapitalNameMsg.setNewCapitalMapName(newMapName);
			String capitalMapId = getCapitalMapId(ub);
			newCapitalNameMsg.setMapId(capitalMapId);
			Collection<MapInstance>  mapInstances = GameContext.getMapApp().getAllMapInstance(capitalMapId);
			for(MapInstance map : mapInstances){
				for(RoleInstance r : map.getRoleList()){
					r.getBehavior().sendMessage(newCapitalNameMsg);
				}
			}
		}
		return result.success();
	}
	private String getCapitalMapId(UnionBattle ub){
		String capitalMapId = null;
		List<WorldMapInfo> mapInfos = GameContext.getWorldMapApp().getAllWorldMapInfo();
		if(Util.isEmpty(mapInfos)){
			return null;
		}
		byte capitalMapIndex = unionBattleAppConfig.getCapitalMapIndex();
		for (WorldMapInfo info : mapInfos) {
			if(info.getMapIndex() == capitalMapIndex){
				capitalMapId = info.getMapId();
				break;
			}
		}
		return capitalMapId;
	}
	/**
	 * 通过UI进入 活动开启则第一个玩家进入，创建地图实例
	 */
	@Override
	public Result joinBattle(RoleInstance role, byte mapIndex) {
		int battleId = getBattleIdByMapIndex(mapIndex);
		Result result = new Result().failure();
		//公会战进入加入角色等级限制（通过判断是否开启工会活动的等级限制）
		int levelLimit = unionBattleAppConfig.getRolelevel();
		if(role.getLevel() < levelLimit){
			return result.setInfo(getText(TextId.UNION_BATTLE_LEVEL_LIMIT));
		}
		// 不在开启时间内
		Active active = GameContext.getActiveApp().getOnlyOneActive(
				ActiveType.UnionBattle);
		if (!active.isTimeOpen()) {
			return result
					.setInfo(getText(TextId.UNION_BATTLE_NOT_IN_ACTIVE_TIME));
		}
		UnionBattleConfig cf = getUnionBattleConfig(battleId);
		if (cf == null) {
			return result.setInfo(getText(TextId.UNION_BATTLE_NOT_EXIST));
		}
		// 是否有公会
		if (!role.hasUnion()) {
			return result.setInfo(getText(TextId.UNION_BATTLE_NOT_MEMBER));
		}
		int unionLevelLimit = unionBattleAppConfig.getDkpMaxLimit();
		if(role.getUnionLevel() < unionLevelLimit){
			return result.setInfo(getText(TextId.UNION_BATTLE_UNION_LEVEL_LIMIT));
		}
		// 是否已经进入公会战地图
		MapInstance mapIn = role.getMapInstance();
		if (null != mapIn && isInUnionBattleMap(mapIn)) {
			return result.setInfo(getText(TextId.UNION_BATTLE_HAS_ENTERED));
		}

		// 目前的防守记录
		UnionBattle unionBattle = getUnionBattleNotNull(battleId);
		String roleUionId = GameContext.getUnionApp().getUnionId(
				role.getIntRoleId());
		String tarMapId = null;
		// 如果是防守方，进入室内地图
		tarMapId = roleUionId.equals(unionBattle.getUnionId()) ? cf.getMap2()
				: cf.getMap1();// 入口

		sacred.alliance.magic.app.map.Map mapInfo = GameContext.getMapApp()
				.getMap(tarMapId);
		if (null == mapInfo) {
			return result.setInfo(getText(TextId.UNION_BATTLE_MAP_GONE));
		}
		if (role.getLevel() < mapInfo.getMapConfig().getMinTransLevel()) {
			return result.setInfo(getText(TextId.UNION_BATTLE_ROLELEVEL_LIMIT));
		}
		// 判断人数是否已满
		MapInstance mapInstance = MapUnionBattleContainer.getMapContainer().createMapInstance(mapInfo, role);
		if (mapInstance == null) {//地图不存在
			return result.setInfo(getText(TextId.UNION_BATTLE_MAP_GONE));
		}
		if (mapInstance != null
				&& mapInstance.getRoleCount() >= mapInfo.getMapConfig()
						.getMaxRoleCount()) {
			return result.setInfo(getText(TextId.UNION_BATTLE_ROLECOUNT_LIMIT));
		}

		Point point = MapUtil.randomCorrectRoadPoint(tarMapId);
		try {
			role.getBehavior().changeMap(point);
		} catch (Exception e) {
			logger.info("UnionBattleApp.joinBattle(role, battleId) err:", e);
			return result.setInfo(getText(TextId.UNION_BATTLE_MAP_GONE));
		}
		
		C2539_UnionBattleJoinNotifyMessage msg = new C2539_UnionBattleJoinNotifyMessage();
		String notice = null;
		NpcTemplate npc = GameContext.getNpcApp().getNpcTemplate(cf.getBossId());
		//进入地图提示 进入{0}，击杀{1}即可占{0}！
		if(Util.isEmpty(unionBattle.getUnionId())){
			String ct = cf.getDefaultEnterNotice();
			notice = MessageFormat.format(ct ,cf.getName(),npc.getNpcname(),cf.getName());
			msg.setContent(notice);
			role.getBehavior().sendMessage(msg);
			return result.success();
		}
		Union un = GameContext.getUnionApp().getUnion(unionBattle.getUnionId());
		if(!roleUionId.equals(unionBattle.getUnionId())){
			//进入{0}，现在由{1}占领，击杀{2}即可占{0}！
			String ct = cf.getAttackerEnterNotice();
			notice = MessageFormat.format(ct, cf.getName(),un.getUnionName(), npc.getNpcname());
			msg.setContent(notice);
			role.getBehavior().sendMessage(msg);
			return result.success();
		}
		//进入{0}，现在由己方占领，守卫{1}坚持到活动结束，即可获得胜利！
		notice = MessageFormat.format(cf.getDefenderEnterNotice(),cf.getName(),npc.getNpcname());
		msg.setContent(notice);
		role.getBehavior().sendMessage(msg);
		roleIds.add(role.getIntRoleId());
		return result.success();
	}

	/**
	 * 是否是公会战地图
	 */
	private boolean isInUnionBattleMap(MapInstance mapInstance) {
		MapConfig mapConfig = mapInstance.getMap().getMapConfig();
		if (null == mapConfig) {
			return false;
		}
		MapLogicType type = mapConfig.getMapLogicType();
		if (type == MapLogicType.unionBattle) {
			return true;
		}
		return false;
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}

	/* 判断玩家角色是否可以修改公会战地图的名称 */
	@Override
	public int onLogin(RoleInstance role, Object context) {
//		C2532_UnionBattleOccupyInfoRespMessage msg = getUnionBattleOccupyInfoMessage((byte)0);
//		role.getBehavior().sendMessage(msg);
		for (UnionBattleConfig cf : unionBattleConfigMap.values()) {
			boolean isRenamable = isRenamable(role, cf);
			if(isRenamable){
				notifyRoleRenameMapname(role, cf.getBattleId());
			}
		}
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		return 0;
	}

	@Override
	public Message openPanel(RoleInstance role) {
		C2530_UnionBattlePanelRespMessage resp = new C2530_UnionBattlePanelRespMessage();
		List<UnionBattleItem> battleItemList = buildBattleItemList(role);
		resp.setBattleItemList(battleItemList);
		resp.setDesc(unionBattleAppConfig.getDesc());
		return resp;
	}

	private List<UnionBattleItem> buildBattleItemList(RoleInstance role) {
		List<UnionBattleItem> battleItemList = Lists.newArrayList();
		for (UnionBattleConfig cf : unionBattleConfigMap.values()) {
			UnionBattleItem it = new UnionBattleItem();
  			/* it.setBattleId(cf.getBattleId());
  			it.setBattleName(cf.getName());

			UnionBattle ub = getUnionBattle(cf.getBattleId());
			if(ub != null){
				it.setNewMapName(getNewMapNameByMapIndex);
			}*/
			it.setMapIndex(cf.getMapIndex());
			String unionId = getUnionBattleUnionId(cf.getBattleId());
			if (unionId == null) {
				battleItemList.add(it);
				continue;
			}
 			/*String unionName = GameContext.getUnionApp().getUnion(unionId)
 					.getUnionName();
 			it.setUnionName(unionName);
			// 是否正在开启
  			if (isUnionBattleActiveTimeOpen()) {
  				it.setBattling((byte) 1);
   			}
			boolean isRenamable = isRenamable(role, cf);
			if(isRenamable){
				it.setRenamable((byte)1);
			}
			*/
			battleItemList.add(it);
		}
		return battleItemList;
	}
	
	private boolean isRenamable(RoleInstance role, int battleId){
		UnionBattleConfig cf = getUnionBattleConfig(battleId);
		return isRenamable(role, cf);
	}
	
	private boolean isRenamable(RoleInstance role, UnionBattleConfig cf) {
		if(role == null || cf == null || cf.getRenamable() == 0){
			return false;
		}
		//只有会长可更改
		if(!role.hasUnion()){
			return false;
		}
		Union un = GameContext.getUnionApp().getUnion(role);
		if(role.getIntRoleId() != un.getLeaderId()){
			return false;
		}
		UnionBattle ub = getUnionBattle(cf.getBattleId());
		if(ub == null){
			return false;
		}
		if(ub.getInstanceId() != 0 && ub.getInstanceId() == ub.getInstanceRenamedId()){
			return false;
		}
		if(!role.getUnionId().equals(ub.getUnionId())){
			return false;
		}
/*		if(ub.getWinNumber() < RENAMABLE_WIN_NUM){
			return false;
		}*/
		return true;
	}
	
	private void notifyRoleRenameMapname(RoleInstance role, int battleId){
		UnionBattleConfig cf = getUnionBattleConfig(battleId);
		if(cf == null){
			return;
		}
		C2537_UnionBattleMapRenamableNotifyMessage msg = new C2537_UnionBattleMapRenamableNotifyMessage();
		msg.setMapIndex(cf.getMapIndex());
		role.getBehavior().sendMessage(msg);
	}

	@Override
	public Message getUnionBattleInfo(RoleInstance role, byte mapIndex) {
		C2533_UnionBattleInfoRespMessage resp = new C2533_UnionBattleInfoRespMessage();
		Integer battleId = mapIndexBattleIdMap.get(mapIndex) ;
		if(null == battleId){
			return new C0003_TipNotifyMessage(this.getText(TextId.UNION_BATTLE_NOT_EXIST));
		}
		UnionBattleConfig cf = unionBattleConfigMap.get(battleId);
		if(null == cf){
			return new C0003_TipNotifyMessage(this.getText(TextId.UNION_BATTLE_NOT_EXIST));
		}
		resp.setMapIndex(mapIndex);
//		resp.setUnionBattleName(cf.getName());
//		UnionBattle ub = getUnionBattle(battleId);
//		if(null != ub){
//			String unionName = getOccupyUnionName(ub.getUnionId());
//			resp.setUnionName(unionName);
//		}
		List<GoodsLiteNamedItem> awards = buildUnionBattleAwards(cf);
		if(Util.isEmpty(awards)){
			return resp;
		}
		resp.setAwards(awards);
		return resp;
	}
	
	private List<GoodsLiteNamedItem> buildUnionBattleAwards(
			UnionBattleConfig cf) {
		if(cf == null){
			return null;
		}
		return cf.getGoodsLiteNamedItemList();
	}

	private String getUnionBattleUnionId(int battleId) {
		UnionBattle battle = getUnionBattle(battleId);
		if (battle == null) {
			return null;
		}
		return battle.getUnionId();
	}

	@Override
	public boolean isUnionBattleActiveTimeOpen() {
		boolean isOpen = active.isTimeOpen();
		if (isOpen) {
			activeEnd.set(false);
		}
		return isOpen;
	}
	// 系统广播
	private void sendBroadcastInfo(RoleInstance role, String message) {
		if (Util.isEmpty(message)) {
			return;
		}
		GameContext.getChatApp().sendSysMessage(ChatSysName.System,
				ChannelType.Publicize_Personal, message, null, null);
	}

	@Override
	public void unionBattleOver(String unionId) {
	}

	@Override
	public void notifyEnterUnionBattle(String instanceId) {

	}

	@Override
	public Active getActive() {
		return GameContext.getActiveApp().getOnlyOneActive(
				ActiveType.UnionBattle);
	}


	@Override
	public UnionBattleKillMsgConfig getUnionBattleKillMsgConfig(int killNum) {
		if (Util.isEmpty(unionBattleKillMsgConfigMap)) {
			logger.error("union.battle.UnionBattleAppImpl.getUnionBattleKillMsgConfig(), unionBattleKillMsgConfigMap is empty");
			return null;
		}
		return unionBattleKillMsgConfigMap.get(killNum);
	}

	@Override
	public UnionBattleKilledMsgConfig getUnionBattleKilledMsgConfig(int killNum) {
		if (Util.isEmpty(unionBattleKilledMsgConfigMap)) {
			logger.error("union.battle.UnionBattleAppImpl.getUnionBattleKilledMsgConfig(), unionBattleKilledMsgConfigMap is empty");
			return null;
		}
		for (UnionBattleKilledMsgConfig cf : unionBattleKilledMsgConfigMap
				.values()) {
			if ((cf.getMinNum() >= killNum) && (cf.getMaxNum() <= killNum)) {
				return cf;
			}
		}
		return null;
	}

	@Override
	public int getKillDkpAward() {
		if (unionBattleAppConfig == null) {
			return 0;
		}
		return unionBattleAppConfig.getDkp();
	}

	private UnionBattle addNewUnionBattle(int battleId) {
		UnionBattle battle = new UnionBattle();
		battle.setBattleId(battleId);
		unionBattleMap.put(battle.getBattleId(), battle);
		return battle;
	}

	@Override
	public void broadCastBegin(String mapId) {
		UnionBattle battle = getUnionBattleByMapId(mapId);
		if (battle == null) {
			return;
		}
		if (Util.isEmpty(battle.getUnionId())) {
			return;
		}
		Union union = GameContext.getUnionApp().getUnion(battle.getUnionId());
		if (union == null) {
			return;
		}
		Collection<RoleInstance> roles = GameContext.getUnionApp()
				.getAllOnlineUnionMember(union);
		if (Util.isEmpty(roles)) {
			return;
		}
		String battleName = getBattleName(battle.getBattleId());
		String content = MessageFormat.format(
				getText(TextId.UNION_BATTLE_BEGIN), battleName);
		for (RoleInstance role : roles) {
			notifyMessage(role, content);
		}
	}

	private String getBattleName(int battleId) {
		UnionBattleConfig cf = getUnionBattleConfig(battleId);
		if (cf == null) {
			return null;
		}
		return cf.getName();
	}

	private void notifyMessage(RoleInstance role, String content) {
		C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(content);
		role.getBehavior().sendMessage(message);
	}

	@Override
	public boolean isUnionBattleBoss(String roleId, String mapId) {
		if (Util.isEmpty(unionBattleConfigMap) || Util.isEmpty(roleId)) {
			return false;
		}
		UnionBattleConfig cf = getUnionBattleConfigByMapId(mapId);
		if (cf != null && roleId.equals(cf.getBossId())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addDkp(RoleInstance role, int dkp) {
		String roleId = role.getRoleId();
		int dkp0 = roleDkpMap.containsKey(roleId) ? roleDkpMap.get(roleId) : 0;
		if (dkp0 >= unionBattleAppConfig.getDkpMaxLimit()) {
			return false;
		}
		int newDkp = Math
				.min(dkp0 + dkp, unionBattleAppConfig.getDkpMaxLimit());
		int addDkp = newDkp - dkp0;
		roleDkpMap.put(roleId, newDkp);
		GameContext.getUnionApp().changeMemberDkp(role, addDkp, OperatorType.Add, FunType.battleRewardDkp, true);
//		changeMemberDkp(role,AttributeType.dkp, OperatorType.Add, addDkp,OutputConsumeType.union_battle_role_reward);
		
		recordRoleDkp(role, addDkp);
		return true;
	}

	@Override
	public void putKillMap(int battleId, String roleId, int killNum) {
		Map<String, Integer> map = getKillMap(battleId);
		map.put(roleId, killNum);
	}

	@Override
	public Map<String, Integer> getKillMap(int battleId) {
		Map<String, Integer> map = allKillMap.get(battleId);
		if (map == null) {
			map = Maps.newConcurrentMap();
			allKillMap.put(battleId, map);
		}
		return map;
	}

	@Override
	public Integer removeFromKillMap(Integer battleId, String roleId) {
		Map<String, Integer> map = getKillMap(battleId);
		return map.remove(roleId);
	}

	@Override
	public int getUnionBattleKillNum(int unionBattleId, String roleId) {
		Map<String, Integer> map = getKillMap(unionBattleId);
		Integer killNum = map.get(roleId);
		return killNum == null ? 0 : killNum;
	}

	@Override
	public void endUnionBattleActive() {
  		if (activeEnd.get()) {
			return;
		}
		activeEnd.set(true);
		broadCast(getText(TextId.UNION_BATTLE_END), ChannelType.World);
		List<RoleInstance> roles = Lists.newArrayList();
		for(Integer roleId : roleIds){
			if(GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(roleId))){
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
				roles.add(role);
			}
		}
		//活动入库
		for(UnionBattle ub : unionBattleMap.values()){
			String orgDefenderUnionId = originalDefenderMap.get(ub.getBattleId());
			if(orgDefenderUnionId == null){
				ub.setWinNumber(1);
			}
			if(ub.getUnionId() != null){
				if(ub.getUnionId().equals(orgDefenderUnionId)){
					ub.setWinNumber(ub.getWinNumber() + 1);
				}else{
					ub.setWinNumber(1);
					//主城改名被中断
					ub.setNewMapName("");
				}
			}
			ub.setInstanceId(ub.getInstanceId() + 1);
			this.saveUpdateDb(ub);
			//如果有公会战连胜超过四次，则发送改名权限提示
			if(ub.getWinNumber() >= RENAMABLE_WIN_NUM){
				for(RoleInstance role : roles){
					if(isRenamable(role, ub.getBattleId())){
						notifyRoleRenameMapname(role, ub.getBattleId());
					}
				}
			}
		}
		generateUnionBattleEndData();
		//公会连胜列表
		Message msg = getUnionBattleWinInfoRespMessage();
/*		for(RoleInstance role : roles){
			role.getBehavior().sendMessage(msg);
		}*/
		MapUnionBattleContainer.getMapContainer().sendAllUnionBattleRoleMessage(msg);
	}
	@Override
	public Message getUnionBattleWinInfoRespMessage(){
		C2535_UnionBattleWinInfoRespMessage msg = new C2535_UnionBattleWinInfoRespMessage();
		msg.setWinnerlist(winlist);
//		msg.setRoleRanklist(roleRanklist);
		return msg;
	}
	private void generateUnionBattleEndData(){
		if(Util.isEmpty(unionBattleMap)||Util.isEmpty(battleRankMap)){
			return;
		}
		winlist = getUnionBattleWinItemList();
		if(Util.isEmpty(winlist));{
			winlist = getDefaultWinItemList();
		}
		/* 暂无角色排行信息 */
//		roleRanklist = getUnionBattleRoleRankItemList();
	}
	private List<UnionBattleWinUnionItem> getDefaultWinItemList() {
		List<UnionBattleWinUnionItem> list = Lists.newArrayList();
	    for(Map.Entry<Integer, UnionBattle> entry:unionBattleMap.entrySet()){
	        UnionBattle ub = entry.getValue();
	        UnionBattleWinUnionItem it = buildUnionBattleDefaultWinItem(ub);
	        if(it == null){
	        	continue;
	        }
	        list.add(it);
	    }
		return list;
	}

	private UnionBattleWinUnionItem buildUnionBattleDefaultWinItem(
			UnionBattle ub) {
		UnionBattleWinUnionItem it = buildUnionBattleWinItem(ub);
		if(it == null){
			return null;
		}
		RoleInstance role;
		try {
			role = GameContext.getUserRoleApp().getRoleByRoleId(String.valueOf(ub.getRoleId()));
			it.setMvpRoleName(role.getRoleName());
		} catch (ServiceException e) {
			logger.error(e.toString());
		}
		return it;
	}

	private ArrayList<UnionBattleWinUnionItem> getUnionBattleWinItemList(){
		if(Util.isEmpty(unionBattleMap)||Util.isEmpty(battleRankMap)){
			return null;
		}
		//unionId, rank
		Map<String, UnionBattleRank> mvpMap = Maps.newHashMap();
		for(UnionBattleRank ubr : battleRankMap.values()){
			UnionBattleRank mvpRank = mvpMap.get(ubr.getUnionId());
			if(mvpRank == null){
				mvpMap.put(ubr.getUnionId(), ubr);
				break;
			}
			if(ubr.getDkp() > mvpRank.getDkp()){
				mvpMap.put(ubr.getUnionId(), ubr);
			}
		}
		ArrayList<UnionBattleWinUnionItem> list = Lists.newArrayList();
		for(UnionBattle un : unionBattleMap.values()){
			UnionBattleWinUnionItem winItem = buildUnionBattleWinItem(un);
			if(winItem != null){
				UnionBattleRank mvpRank = mvpMap.get(un.getUnionId());
				if(mvpRank !=null ){
					RoleInstance role;
					try {
						role = GameContext.getUserRoleApp().getRoleByRoleId(String.valueOf(mvpRank.getRoleId()));
						winItem.setMvpRoleName(role.getRoleName());
					} catch (ServiceException e) {
						logger.error(e.toString());
					}
				}
				list.add(winItem);
			}
		}
		Collections.sort(list, UNIONBATTLE_WIN_NUMBER_COMPARATOR);
		return list;
	}
	private ArrayList<UnionBattleRoleRankItem> getUnionBattleRoleRankItemList(){
		if(Util.isEmpty(battleRankMap)){
			return null;
		}
		ArrayList<UnionBattleRoleRankItem> list = Lists.newArrayList();
		for(UnionBattleRank ubr : battleRankMap.values()){
			UnionBattleRoleRankItem rankItem = buildUnionBattleRoleRankItem(ubr);
			if(rankItem != null)
				list.add(rankItem);
		}
		Collections.sort(list, UNIONBATTLE_RANK_COMPARATOR);
		return list;
	}
	
	private UnionBattleRoleRankItem buildUnionBattleRoleRankItem(UnionBattleRank ubr) {
		UnionBattleRoleRankItem rankItem = new UnionBattleRoleRankItem();
		RoleInstance role = null;
		try {
			role = GameContext.getUserRoleApp().getRoleByRoleId(String.valueOf(ubr.getRoleId()));
		} catch (ServiceException e) {
			logger.error("unionBattleApp.buildUnionBattleRoleRankItem()err:",e);
		}
		if(role == null){
			return null;
		}
		rankItem.setRoleName(role.getRoleName());
		rankItem.setKillNum(ubr.getKillNum());
		rankItem.setKilledNum(ubr.getKilledNum());
		rankItem.setDkp(ubr.getDkp());
		return rankItem;
	}

	private UnionBattleWinUnionItem buildUnionBattleWinItem(UnionBattle un) {
		UnionBattleWinUnionItem winItem = new UnionBattleWinUnionItem();
		String unionName = this.getOccupyUnionName(un.getUnionId());
		if(Util.isEmpty(unionName)){
			return null;
		}
		winItem.setBattleName(this.getBattleName(un.getBattleId()));
		winItem.setUnionName(unionName);
		winItem.setWinNumber(un.getWinNumber());
		return winItem;
	}

	public void broadCast(String content, ChannelType channelType) {
		GameContext.getChatApp().sendSysMessage(ChatSysName.System,
				channelType, content, null, null);
	}

	@Override
	public void bossKilled(int battleId, RoleInstance role, AbstractRole boss) {
		if (role == null || boss == null) {
			return;
		}
		UnionBattle battle = getUnionBattle(battleId);
		battle.setRoleId(role.getIntRoleId());
		battle.setKillTime(new Date());
		Union union = GameContext.getUnionApp().getUnion(role);
		if (union == null) {
			logger.error("unionBattleAppImpl.changeWinner(), role "
					+ role.getRoleId() + " is not a union member");
			return;
		}
		battle.setUnionId(union.getUnionId());
		String content = MessageFormat.format(
				getText(TextId.UNION_BATTLE_BOSS_KILLED), boss.getRoleName(),
				union.getUnionName(), role.getRoleName());
		broadCast(content, ChannelType.World);
		//通知在线玩家更新攻守状态
		C2532_UnionBattleOccupyInfoRespMessage occupymsg = getUnionBattleOccupyInfoMessage(battleId);

		for(RoleInstance onlineRole : GameContext.getOnlineCenter().getAllOnlineRole()){
//			GameContext.getMessageCenter().sendSysMsg(onlineRole, msg);
			onlineRole.getBehavior().sendMessage(occupymsg);
		}
		/*
		 * 通知所有在公会战地图的角色攻守转化
		 */
		C2534_UnionBattleDefenderExchangeNotifyMessage notifyMsg = getUnionBattleDefenderExchangeNotifyMessage(battleId, role, boss, union);
		MapUnionBattleContainer.getMapContainer().sendAllUnionBattleRoleMessage(notifyMsg);
		this.saveUpdateDb(battle);
		
	}
	
	private C2534_UnionBattleDefenderExchangeNotifyMessage getUnionBattleDefenderExchangeNotifyMessage(int battleId,
			RoleInstance role, AbstractRole boss, Union union) {
		C2534_UnionBattleDefenderExchangeNotifyMessage notifyMsg = new C2534_UnionBattleDefenderExchangeNotifyMessage();
		String notify = this.unionBattleAppConfig.getDefenderExchangeInfo();
		//eg. 圣剑城指挥官被天下无敌公会的无敌大魔王击杀。圣剑城所有者转换成天下无敌公会。
		String mapName = this.getBattleName(battleId);
		String notifyContent = MessageFormat.format(notify, boss.getRoleName(),
				union.getUnionName(), role.getRoleName(), mapName);
		byte countDown = this.unionBattleAppConfig.getDefenderExchangeNotifySecondsLeft();
		notifyMsg.setInfo(notifyContent);
		notifyMsg.setSecondsLeft(countDown);
		return notifyMsg;
	}

	@Override
	public void initUnionBattle(int battleId) {
		this.getUnionBattleNotNull(battleId);
	}

	public void sendNotityMessage(RoleInstance role, String content) {
		C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(content);
		role.getBehavior().sendMessage(msg);
		GameContext.getChatApp().sendSysMessage(ChatSysName.System,
				ChannelType.Publicize_Personal, content, null, null);
	}

	@Override
	public String getBossId(int unionBattleId) {
		UnionBattleConfig cf = getUnionBattleConfig(unionBattleId);
		if (cf == null) {
			return null;
		}
		return cf.getBossId();
	}

	@Override
	public Point getBossPoint(int unionBattleId) {
		UnionBattleConfig cf = getUnionBattleConfig(unionBattleId);
		if (cf == null) {
			return null;
		}
		return new Point(cf.getMap2(), cf.getX(), cf.getY());
	}

	@Override
	public boolean hasBoss(int battleId, String mapId) {
		if (battleId <= 0 || Util.isEmpty(mapId)) {
			return false;
		}
		UnionBattleConfig cf = getUnionBattleConfig(battleId);
		if (cf == null) {
			return false;
		}
		return mapId.equals(cf.getMap2());
	}

	@Override
	public C2532_UnionBattleOccupyInfoRespMessage getUnionBattleOccupyInfoMessageByMapIndex(byte mapIndex) {
		int battleId = getBattleIdByMapIndex(mapIndex);
		return getUnionBattleOccupyInfoMessage(battleId);
	}
	/**
	 * @param battleId
	 * @return
	 * @date 2015-2-14 上午10:55:46
	 */
	public C2532_UnionBattleOccupyInfoRespMessage getUnionBattleOccupyInfoMessage(int battleId) {
		C2532_UnionBattleOccupyInfoRespMessage msg = new C2532_UnionBattleOccupyInfoRespMessage();
		List<UnionBattleOccupyInfoItem> infos = new ArrayList<UnionBattleOccupyInfoItem>(); 
		if(battleId != -1){
	        UnionBattleOccupyInfoItem it = buildUnionBattleOccupyInfoItem(getUnionBattle(battleId));
	        infos.add(it);
	        msg.setInfos(infos);
			return msg;
		}
	    for(Map.Entry<Integer, UnionBattle> entry:unionBattleMap.entrySet()){
	        UnionBattle ub = entry.getValue();
	        UnionBattleOccupyInfoItem it = buildUnionBattleOccupyInfoItem(ub);
	        infos.add(it);
	    }
	    msg.setInfos(infos);
		return msg;
	}

	private int getBattleIdByMapIndex(byte mapIndex){
		if(mapIndex == -1){
			return -1;
		}
		Integer v = mapIndexBattleIdMap.get(mapIndex) ;
		if( null == v){
			return -1;
		}
		return v ;
	}
	private UnionBattleOccupyInfoItem buildUnionBattleOccupyInfoItem(UnionBattle ub) {
		UnionBattleOccupyInfoItem it = new UnionBattleOccupyInfoItem();
		
		byte mapIndex = unionBattleConfigMap.get(ub.getBattleId()).getMapIndex();
		it.setMapIndex(mapIndex);
		
		String unionName = getOccupyUnionName(ub.getUnionId());

		it.setUnionName(unionName);
		return it;
	}
	public String getOccupyUnionName(String unionId){
		if(Util.isEmpty(unionId)){
			return null;
		}
		Union un = GameContext.getUnionApp().getUnion(unionId);
		if(un == null){
			return null;
		}
		return un.getUnionName();
	}
	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		if (Util.isEmpty(unionBattleConfigMap)) {
			return null;
		}

		if(!role.hasUnion() ){
			return null;
		}
		String uionId = role.getUnionId();
		List<NpcFunctionItem> functionList = null ;

		for(UnionBattle ub: unionBattleMap.values()){
			if(Util.isEmpty(ub.getUnionId())
					|| !uionId.equals(ub.getUnionId())){
				continue ;
			}
			UnionBattleConfig cf = getUnionBattleConfig(ub.getBattleId());
			if(null == cf || !cf.getNpcId().equals(npc.getNpcid())){
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			String exchangeName = GameContext.getExchangeApp().getExchangeName(Integer.parseInt(cf.getExchangeId()));
			item.setTitle(exchangeName);
			item.setCommandId(ExchangeConstant.EXCHANGE_NPC_ITEM_CMD);
			item.setParam(cf.getExchangeId());
			if(null == functionList){
				functionList = Lists.newArrayList() ;
			}
			functionList.add(item);
		}
		return functionList;
	}

	@Override
	public String getUnionNameByMapIndex(byte mapIndex) {
		if (Util.isEmpty(unionBattleConfigMap)) {
			return null;
		}
		for (UnionBattleConfig cf : unionBattleConfigMap.values()) {
			if(mapIndex == cf.getMapIndex()){
				UnionBattle ub = getUnionBattle(cf.getBattleId());
				if(ub == null){
					return null;
				}
				String unionId = ub.getUnionId();
				if(Util.isEmpty(unionId)){
					return null;
				}
				Union un = GameContext.getUnionApp().getUnion(unionId);
				if(un == null){
					return null;
				}
				return un.getUnionName();
			}
		}
		return null;
	}

	private void increaseKillNum(RoleInstance role){
		int roleId = role.getIntRoleId();
		String unionId = role.getUnionId();
		UnionBattleRank ubr = battleRankMap.get(roleId);
		if(ubr == null){
			ubr = new UnionBattleRank();
			ubr.setRoleId(roleId);
			ubr.setUnionId(unionId);
			ubr.setKillNum(1);
			battleRankMap.put(roleId, ubr);
			return;
		}
		ubr.setKilledNum(ubr.getKillNum() + 1);
	}
	private void increaseKilledNum(RoleInstance role){
		int roleId = role.getIntRoleId();
		String unionId = role.getUnionId();
		UnionBattleRank ubr = battleRankMap.get(roleId);
		if(ubr == null){
			ubr = new UnionBattleRank();
			ubr.setRoleId(roleId);
			ubr.setUnionId(unionId);
			ubr.setKilledNum(1);
			battleRankMap.put(roleId, ubr);
			return;
		}
		ubr.setKilledNum(ubr.getKilledNum() + 1);
	}
	/**
	 * 记录DKP总共获得的量
	 */
	private void recordRoleDkp(RoleInstance role, int addDkp){
		int roleId = role.getIntRoleId();
		String unionId = role.getUnionId();
		UnionBattleRank ubr = battleRankMap.get(role.getIntRoleId());
		if(ubr == null){
			ubr = new UnionBattleRank();
			ubr.setRoleId(roleId);
			ubr.setUnionId(unionId);
			ubr.setDkp(addDkp);
			battleRankMap.put(roleId, ubr);
		}else{
			ubr.setDkp(ubr.getDkp() + addDkp);
		}
	}
	@Override
	public void deathRecord(AbstractRole attacker, AbstractRole victim) {
		try {
			if (null == attacker || victim == null) {
				return;
			}
			if (RoleType.PLAYER == attacker.getRoleType()) {
				RoleInstance attcRole = (RoleInstance) attacker;
				increaseKillNum(attcRole);
			}
			if (RoleType.PLAYER == victim.getRoleType()) {
				RoleInstance victmRole = (RoleInstance) attacker;
				increaseKilledNum(victmRole);
			}
		} catch (Exception e) {
			logger.error("unionBattleApp.deathRecord() err:", e);
		}
	}

	@Override
	public String getOriginDefenderUnionId(Integer unionBattleId) {
		return originalDefenderMap.get(unionBattleId);
	}

	@Override
	public UnionBattleMapBuffParam getunionBattleMapParam(Integer battleId) {
		UnionBattleMapBuffParam param = new UnionBattleMapBuffParam();
		UnionBattle ub = getUnionBattle(battleId);
		if(ub.getWinNumber() >= 1){
			param.setMapbuffLevel(ub.getWinNumber());
			param.setBuffIds(buffIds);
		}
		return param;
	}

	@Override
	public String getCapitalName() {
		UnionBattle ub = getCapitalUnionBattle();
		if(ub == null){
			return null;
		}
		return ub.getNewMapName();
	}

	private UnionBattle getCapitalUnionBattle() {
		byte capitalId = unionBattleAppConfig.getCapitalMapIndex();
		int battleId = getBattleIdByMapIndex(capitalId);
		UnionBattle ub = getUnionBattle(battleId);
		return ub;
	}
}
