package com.game.draco.app.drama;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.game.draco.app.drama.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.drama.domain.RoleDrama;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaInfoItem;
import com.game.draco.message.item.DramaPointTriggerItem;
import com.game.draco.message.response.C3275_DramaInfoRespMessage;
import com.game.draco.message.response.C3276_DramaPointTriggerRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DramaAppImpl implements DramaApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Short, DramaNpc> dramaNpcMap;
	private Map<Integer, DramaBase> dramaBaseMap = Maps.newHashMap() ;
	private Map<Short, Drama> dramaMap;
	//点触发器map
	Map<Short, DramaPointTrigger> pointTriggerMap = Maps.newHashMap() ;
	Map<String, DramaEnterMapTrigger> enterMapTriggerMap = Maps.newHashMap() ;
	Map<String, DramaRoleDieTrigger> roleDieTriggerMap = Maps.newHashMap() ;
	//npcid_mapId_type
	Map<String, DramaQuestTrigger> questTriggerMap = Maps.newHashMap() ;
	/**<key,value> = <npcid_mapId_type, DramaNpcTrigger> */
	Map<String, DramaNpcTrigger> npcTriggerMap =  Maps.newHashMap() ;
	
	//需要加载的剧情元素配置列表
	private List<DramaLoadConfig> dramaLoadConfigList = Lists.newArrayList(
					new DramaLoadConfig(XlsSheetNameType.drama_camera,DramaCamera.class),
					new DramaLoadConfig(XlsSheetNameType.drama_npc_appear,DramaNpcAppear.class),
					new DramaLoadConfig(XlsSheetNameType.drama_npc_disppear,DramaNpcDisppear.class),
					new DramaLoadConfig(XlsSheetNameType.drama_npc_move,DramaNpcMove.class),
					new DramaLoadConfig(XlsSheetNameType.drama_talk,DramaNpcTalk.class),
					new DramaLoadConfig(XlsSheetNameType.drama_attack,DramaNpcAttack.class),
					new DramaLoadConfig(XlsSheetNameType.drama_bubble,DramaBubble.class),
					new DramaLoadConfig(XlsSheetNameType.drama_screen_shock,DramaScreenShock.class),
					new DramaLoadConfig(XlsSheetNameType.drama_npc_set,DramaNpcSet.class),
					new DramaLoadConfig(XlsSheetNameType.drama_tips,DramaTip.class),
					new DramaLoadConfig(XlsSheetNameType.drama_exit_map,DramaExitMap.class),
					new DramaLoadConfig(XlsSheetNameType.drama_fly,DramaNpcFly.class),
					new DramaLoadConfig(XlsSheetNameType.drama_effect,DramaEffect.class),
					new DramaLoadConfig(XlsSheetNameType.drama_name_item,DramaItemBossName.class),
					new DramaLoadConfig(XlsSheetNameType.drama_music_item,DramaItemMusic.class)
				) ;

	/**
	 * 地图内的点触发列表
	 */
	private Map<String,List<DramaPointTrigger>> mapPointTrigger = Maps.newHashMap() ;
	
	private DramaQuestTrigger getQuestTrigger(int questId,byte type){
		return this.questTriggerMap.get(questId + "_" + type);
	}
	
	private DramaNpcTrigger getNpcTrigger(String npcId,String mapId,byte type){
		return this.npcTriggerMap.get(npcId + "_" + mapId + "_" + type) ;
	}
	
	@Override
	public void setArgs(Object arg0) {

	}
	
	
	private class DramaLoadConfig{
		public DramaLoadConfig(XlsSheetNameType xls,Class<? extends DramaBase> clazz){
			this.xls = xls ;
			this.clazz = clazz ;
		}
		private XlsSheetNameType xls ;
		private Class<? extends DramaBase> clazz ;
	}

	
	@Override
	public void start() {
		//加载剧情数据
		this.loadNpcs();
		
		//加载各剧情元素
		for(DramaLoadConfig config : dramaLoadConfigList){
			this.loadDramaItem(config.xls, config.clazz);
		}
		
		//必须放在所有的剧情元素加载完以后
		this.loadDramaCompose();
		this.loadDrama();
		
		//加载触发数据
		this.loadPointTrigger();
		this.loadEnterMapTrigger();
		this.loadQuestTrigger();
		this.loadNpcTrigger();
		this.loadRoleDieTrigger();
	}
	
	private <T extends DramaBase>  void loadDramaItem(XlsSheetNameType xls, Class<T> clazz){
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		Map<Integer,T> itemMap = null;
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			itemMap = XlsPojoUtil.sheetToGenericMap(sourceFile,sheetName, clazz);
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
			return ;
		}
		if(Util.isEmpty(itemMap)) {
			return ;
		}
		if(DramaNpcSupport.class.isAssignableFrom(clazz)){
			for(T t : itemMap.values()){
				DramaNpcSupport npc = (DramaNpcSupport)t ;
				short npcId = npc.getNpcId();
				if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
							+ ", npcId=" + npcId + ", do exsit in tag npcs");
				}
			}
		}
		dramaBaseMap.putAll(itemMap);
	}

	@Override
	public void stop() {

	}
	
	private void loadNpcs() {
		//加载剧情npc配置
		String fileName = XlsSheetNameType.drama_npcs.getXlsName();
		String sheetName = XlsSheetNameType.drama_npcs.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpc.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
	}
	
	private void loadDramaCompose() {
		//加载剧情复合配置
		String fileName = XlsSheetNameType.drama_dramacompose.getXlsName();
		String sheetName = XlsSheetNameType.drama_dramacompose.getSheetName();
		Map<Integer, DramaCompose> dramaComposeMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaComposeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaCompose.class);
			if(Util.isEmpty(dramaComposeMap)){
				return;
			}
			for(DramaCompose dramaCompose : dramaComposeMap.values()) {
				if(null == dramaCompose) {
					continue;
				}
				String componentIds = dramaCompose.getDramaIds();
				if(Util.isEmpty(componentIds)) {
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
							+ ", dramaId=" + dramaCompose.getId() + ", do config componentIds");
					continue;
				}
				String[] ids = componentIds.split(Cat.comma);
				for(String id : ids) {
					DramaBase dramaBase = dramaBaseMap.get(Integer.valueOf(id));
					if(null == dramaBase){
						Log4jManager.checkFail();
						Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
								+ ", componentId=" + id + ", do exsit!");
						continue;
					}
					dramaCompose.getDramaBaseList().add(dramaBase);
				}
				dramaCompose.init();
			}
			dramaBaseMap.putAll(dramaComposeMap);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
	}
	
	private void loadDrama() {
		//加载剧情npc说话配置
		String fileName = XlsSheetNameType.drama_drama.getXlsName();
		String sheetName = XlsSheetNameType.drama_drama.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, Drama.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(dramaMap)){
			return ;
		}
		
		for(Entry<Short, Drama> entry : dramaMap.entrySet()) {
			Drama drama = entry.getValue();
			if(null == drama) {
				continue;
			}
			String componentIds = drama.getComponentIds();
			if(Util.isEmpty(componentIds)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", dramaId=" + drama.getDramaId() + ", do config componentIds");
				continue;
			}
			String[] ids = componentIds.split(Cat.comma);
			for(String id : ids) {
				DramaBase dramaBase = dramaBaseMap.get(Integer.valueOf(id));
				if(null == dramaBase){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
							+ ", componentId=" + id + ", do not exsit!");
					continue;
				}
				drama.getDramaBaseList().add(dramaBase);
			}
		}
		
	}
	
	private void loadPointTrigger() {
		//加载点触发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_point.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_point.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			pointTriggerMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaPointTrigger.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(pointTriggerMap)){
			this.mapPointTrigger.clear();
			return ;
		}
		
		this.mapPointTrigger.clear();
		
		for(Entry<Short, DramaPointTrigger> entry : pointTriggerMap.entrySet()) {
			DramaPointTrigger trigger = entry.getValue();
			String mapId = trigger.getMapId() ;
			List<DramaPointTrigger> list = this.mapPointTrigger.get(mapId);
			if(null == list){
				list = Lists.newArrayList() ;
				this.mapPointTrigger.put(mapId, list);
			}
			list.add(trigger);
			
			checkTrigger(fileName, sheetName, trigger.getDramaId(), trigger.getMapId(), 0, null);
		}
		
	}
	
	
	private void loadEnterMapTrigger() {
		//加载进入地图触发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_enter_map.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_enter_map.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			enterMapTriggerMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaEnterMapTrigger.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(enterMapTriggerMap)){
			return ;
		}
		
		for(Entry<String, DramaEnterMapTrigger> entry : enterMapTriggerMap.entrySet()) {
			DramaEnterMapTrigger trigger = entry.getValue();
			checkTrigger(fileName, sheetName, trigger.getDramaId(), trigger.getMapId(), 0, null);
		}
	}
	

	private void loadRoleDieTrigger() {
		// 加载角色死亡发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_role_die.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_role_die
				.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			this.roleDieTriggerMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
					sheetName, DramaRoleDieTrigger.class);
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName
					+ ", sheetName = " + sheetName, ex);
		}
		if (Util.isEmpty(roleDieTriggerMap)) {
			return;
		}

		for (Entry<String, DramaRoleDieTrigger> entry : roleDieTriggerMap
				.entrySet()) {
			DramaRoleDieTrigger trigger = entry.getValue();
			checkTrigger(fileName, sheetName, trigger.getDramaId(),
					trigger.getMapId(), 0, null);
		}
	}
	
	private void loadQuestTrigger() {
		//加载进入地图触发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_quest.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_quest.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.questTriggerMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaQuestTrigger.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(questTriggerMap)){
			return ;
		}
		for(Entry<String, DramaQuestTrigger> entry : questTriggerMap.entrySet()) {
			DramaQuestTrigger trigger = entry.getValue();
			int questId = trigger.getQuestId();
			checkTrigger(fileName, sheetName, trigger.getDramaId(), null, questId, null);
		}
	}
	
	private void loadNpcTrigger() {
		//加载进入地图触发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_npc.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_npc.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.npcTriggerMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcTrigger.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(npcTriggerMap)){
			return ;
		}
		for(DramaNpcTrigger trigger : npcTriggerMap.values()) {
			String npcId = trigger.getNpcId();
			checkTrigger(fileName, sheetName, trigger.getDramaId(), null, 0, npcId);
		}
	}
	
	
	
	/**
	 * 检测触发配置是否有效
	 */
	private void checkTrigger(String fileName, String sheetName, short dramaId
			, String mapId, int questId, String npcId) {
		if(!dramaMap.containsKey(dramaId)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
					+ ", dramaId=" + dramaId + ", do exsit");
		}
		
		if(!Util.isEmpty(mapId)) {
			sacred.alliance.magic.app.map.Map mapInfo = GameContext.getMapApp().getMap(mapId);
			if(null == mapInfo) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", mapId=" + mapId + ", do exsit");
			}
		}
		
		if(questId > 0) {
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			if(null == quest) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", questId=" + questId + ", do exsit");
			}
		}
		
		if(!Util.isEmpty(npcId)) {
			NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
			if(null == npcTemplate) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit");
			}
		}
		
	}
	

	@Override
	public DramaNpc getDramaNpc(short npcId) {
		return dramaNpcMap.get(npcId);
	}
	
	@Override
	public Drama triggerNpcBornDrama(RoleInstance role,String mapId,String npcId){
		DramaTrigger trigger = this.getNpcTrigger(npcId, mapId, DramaNpcTrigger.TYPE_BORN);
		if(null == trigger){
			return null ;
		}
		return this.doRoleDrama(role, trigger, false) ; 
	}
	
	@Override
	public DramaInfoItem createDramaInfoItem(Drama drama,String dramaMapId){
		if(null == drama){
			return null ;
		}
		List<DramaBase> dramaBaseList = drama.getDramaBaseList();
		if(Util.isEmpty(dramaBaseList)) {
			return null;
		}
		List<DramaBaseItem> dramaBaseItemList = new ArrayList<DramaBaseItem>();
		for(DramaBase dramaBase : dramaBaseList) {
			if(null == dramaBase) {
				continue;
			}
			dramaBaseItemList.add(dramaBase.getDramaBaseInfo());
		}
		DramaInfoItem dramaInfo = new DramaInfoItem() ;
		dramaInfo.setId(drama.getDramaId());
		dramaInfo.setMapId(dramaMapId);
		dramaInfo.setDramaBaseItemList(dramaBaseItemList);
		return dramaInfo ;
	}

	@Override
	public void triggerDrama(RoleInstance role, DramaTriggerType triggerType,
			short dramaId, String mapId, int questId, String npcId) {
		try{
			DramaTrigger trigger = null ;
			switch (triggerType) {
			case Point:
				trigger = pointTriggerMap.get(dramaId);
				break;
			case EntryMap:
				trigger = enterMapTriggerMap.get(mapId);
				break;
			case AcceptQuest:
				trigger = this.getQuestTrigger(questId,DramaQuestTrigger.TYPE_ACCEPT);
				break;
			case SubmitQuest:
				trigger = this.getQuestTrigger(questId,DramaQuestTrigger.TYPE_SUBMIT);
				break;
			case NpcDie:
				trigger = this.getNpcTrigger(npcId, mapId, DramaNpcTrigger.TYPE_DIE);
				break;
			case NpcBorn:
				trigger = this.getNpcTrigger(npcId, mapId, DramaNpcTrigger.TYPE_BORN);
				break;
			case RoleDie :
				trigger = this.roleDieTriggerMap.get(mapId);
				break ;
			default:
				break;
			}
			if(null == trigger) {
				return ;
			}
			this.doRoleDrama(role, trigger, true) ;
		} catch (Exception ex) {
			logger.error("dramaApp.triggerDrama error ", ex);
		}
	}
	
	private Drama doRoleDrama(RoleInstance role,DramaTrigger trigger,boolean sendMsg){
		short realDramaId = trigger.getDramaId() ;
		String dramaMapId = trigger.getMapId() ;
		//角色不在当前地图
		if(!role.getMapId().equals(dramaMapId)) {
			return null;
		}
		Drama drama = dramaMap.get(realDramaId);
		if(null == drama) {
			return null;
		}
		
		if(Drama.REPLAY_NO == drama.getReplay()
			&& null != GameContext.getUserDramaApp().getRoleDrama(role.getRoleId(), realDramaId)) {
			return null ;
		}
		if(sendMsg){
			Message respMsg = createDramaInfoMsg(drama, dramaMapId);
			if(null == respMsg) {
				return drama;
			}
			role.getBehavior().sendMessage(respMsg);
		}
		
		if(Drama.REPLAY_YES == drama.getReplay()) {
			return drama;
		}
		//剧情播放成功,只有不重播的,播放记录入库
		String roleId = role.getRoleId();
		RoleDrama roleDrama = GameContext.getUserDramaApp().getRoleDrama(roleId, realDramaId);
		if(null == roleDrama) {
			roleDrama = new RoleDrama();
			roleDrama.setRoleId(role.getIntRoleId());
			roleDrama.setExistDb(false);
			roleDrama.setDramaId(realDramaId);
			GameContext.getUserDramaApp().addRoleHero(roleId, roleDrama);
		}
		return drama ;
	}
	
	
	
	private  Message createDramaInfoMsg(Drama drama, String mapId){
		DramaInfoItem dramaInfo = this.createDramaInfoItem(drama, mapId) ;
		if(null == dramaInfo){
			return null ;
		}
		C3275_DramaInfoRespMessage resqMsg = new C3275_DramaInfoRespMessage();
		resqMsg.setDramaInfo(dramaInfo);
		return resqMsg;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try{
			List<RoleDrama> roleDramaList = GameContext.getBaseDAO().selectList(RoleDrama.class, 
					"roleId", role.getRoleId());
			if(Util.isEmpty(roleDramaList)) {
				return 1;
			}
			
			for(RoleDrama roleDrama : roleDramaList) {
				roleDrama.setExistDb(true);
				GameContext.getUserDramaApp().addRoleHero(role.getRoleId(), roleDrama);
			}
		}catch(Exception e){
			logger.error("dramaApp.login error, ",e);
			return 0;
		}
		
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try{
			String roleId = role.getRoleId();
			Map<Short, RoleDrama> roleDramaMap = GameContext.getUserDramaApp().getRoleDramas(roleId);
			if(Util.isEmpty(roleDramaMap)){
				return 1;
			}
			for(Entry<Short, RoleDrama> entry : roleDramaMap.entrySet()){
				RoleDrama roleDrama = entry.getValue();
				if(null == roleDrama){
					continue;
				}
				if(roleDrama.isExistDb()){
					continue;
				}
				GameContext.getBaseDAO().insert(roleDrama);
			}
			GameContext.getUserDramaApp().removeRoleDramas(roleId);
		} catch (Exception e){
			logger.error("dramaApp.offline error, ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}
	
	@Override
	public boolean canNpcTrigger(String npcId,String mapId,DramaTriggerType triggerType){
		if(DramaTriggerType.NpcBorn == triggerType){
			return null != this.getNpcTrigger(npcId, mapId, DramaNpcTrigger.TYPE_BORN) ;
		}
		if(DramaTriggerType.NpcDie == triggerType){
			return null != this.getNpcTrigger(npcId, mapId, DramaNpcTrigger.TYPE_DIE) ;
		}
		return false ;
	}

	@Override
	public void enterMap(RoleInstance role, String mapId) {
		try{
			List<DramaPointTrigger> list = this.mapPointTrigger.get(mapId);
			if(!Util.isEmpty(list)) {
				List<DramaPointTriggerItem> itemList = new ArrayList<DramaPointTriggerItem>();
				for(DramaPointTrigger trigger : list) {
					if(null == trigger) {
						continue;
					}
					if(!mapId.equals(trigger.getMapId())) {
						continue;
					}
					DramaPointTriggerItem item = new DramaPointTriggerItem();
					item.setDramaId(trigger.getDramaId());
					item.setMapId(trigger.getMapId());
					item.setPosX(trigger.getPosX());
					item.setPosY(trigger.getPosY());
					itemList.add(item);
				}
				if(!Util.isEmpty(itemList)) {
					C3276_DramaPointTriggerRespMessage respMsg = new C3276_DramaPointTriggerRespMessage();
					respMsg.setPointTriggerItemList(itemList);
					role.getBehavior().sendMessage(respMsg);
				}
			}
			
			//进入地图触发剧情
			this.triggerDrama(role, DramaTriggerType.EntryMap, (short)0, mapId, 0, null);
		} catch (Exception ex) {
			logger.error("dramaApp.enterMap error", ex);
		}
		
	}
}
