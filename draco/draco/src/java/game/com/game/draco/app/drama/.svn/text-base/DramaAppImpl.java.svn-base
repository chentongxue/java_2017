package com.game.draco.app.drama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.game.draco.app.drama.config.Drama;
import com.game.draco.app.drama.config.DramaBase;
import com.game.draco.app.drama.config.DramaBubble;
import com.game.draco.app.drama.config.DramaCamera;
import com.game.draco.app.drama.config.DramaCompose;
import com.game.draco.app.drama.config.DramaEnterMapTrigger;
import com.game.draco.app.drama.config.DramaNpc;
import com.game.draco.app.drama.config.DramaNpcAppear;
import com.game.draco.app.drama.config.DramaNpcAttack;
import com.game.draco.app.drama.config.DramaNpcDisppear;
import com.game.draco.app.drama.config.DramaNpcMove;
import com.game.draco.app.drama.config.DramaNpcSet;
import com.game.draco.app.drama.config.DramaNpcTalk;
import com.game.draco.app.drama.config.DramaNpcTrigger;
import com.game.draco.app.drama.config.DramaPointTrigger;
import com.game.draco.app.drama.config.DramaQuestTrigger;
import com.game.draco.app.drama.config.DramaScreenShock;
import com.game.draco.app.drama.config.DramaTip;
import com.game.draco.app.drama.config.DramaTriggerType;
import com.game.draco.app.drama.domain.RoleDrama;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.Quest;
import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaPointTriggerItem;
import com.game.draco.message.response.C3275_DramaInfoRespMessage;
import com.game.draco.message.response.C3276_DramaPointTriggerRespMessage;

public class DramaAppImpl implements DramaApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Short, DramaNpc> dramaNpcMap;
	private Map<Integer, DramaBase> dramaBaseMap = new HashMap<Integer, DramaBase>();
	private Map<Short, Drama> dramaMap;
	//点触发器map
	Map<Short, DramaPointTrigger> pointTriggerMap = new HashMap<Short, DramaPointTrigger>();
	Map<String, DramaEnterMapTrigger> enterMapTriggerMap = new HashMap<String, DramaEnterMapTrigger>();
	Map<Integer, DramaQuestTrigger> acceptQuestTriggerMap = new HashMap<Integer, DramaQuestTrigger>();
	Map<Integer, DramaQuestTrigger> submitQuestTriggerMap = new HashMap<Integer, DramaQuestTrigger>();
	/**<key,value> = <npcid_mapId, DramaNpcTrigger> */
	Map<String, DramaNpcTrigger> npcDieTriggerMap = new HashMap<String, DramaNpcTrigger>();
	Map<String, DramaNpcTrigger> npcBornTriggerMap = new HashMap<String, DramaNpcTrigger>();

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		//加载剧情数据
		this.loadNpcs();
		this.loadCamera();
		this.loadNpcAppear();
		this.loadNpcDisppear();
		this.loadNpcMove();
		this.loadNpcTalk();
		this.loadNpcAttack();
		this.loadNpcBubble();
		this.loadScreenShock();
		this.loadNpcSet();
		this.loadDramaTips();
		//必须放在所有的剧情元素加载完以后
		this.loadDramaCompose();
		this.loadDrama();
		
		//加载触发数据
		this.loadPointTrigger();
		this.loadEnterMapTrigger();
		this.loadQuestTrigger();
		this.loadNpcTrigger();
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
	
	private void loadCamera() {
		//加载剧情镜头配置
		String fileName = XlsSheetNameType.drama_camera.getXlsName();
		String sheetName = XlsSheetNameType.drama_camera.getSheetName();
		Map<Integer, DramaCamera> dramaCameraMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaCameraMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaCamera.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		dramaBaseMap.putAll(dramaCameraMap);
	}
	
	private void loadNpcAppear() {
		//加载剧情npc出现配置
		String fileName = XlsSheetNameType.drama_npc_appear.getXlsName();
		String sheetName = XlsSheetNameType.drama_npc_appear.getSheetName();
		Map<Integer, DramaNpcAppear> dramaNpcAppearMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcAppearMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcAppear.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(dramaNpcAppearMap)) {
			return ;
		}
		
		for(Entry<Integer, DramaNpcAppear> entry : dramaNpcAppearMap.entrySet()) {
			DramaNpcAppear npcAppear = entry.getValue();
			short npcId = npcAppear.getNpcId();
			if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit in tag npcs");
			}
		}
		
		dramaBaseMap.putAll(dramaNpcAppearMap);
	}
	
	private void loadNpcDisppear() {
		//加载剧情npc消失配置
		String fileName = XlsSheetNameType.drama_npc_disppear.getXlsName();
		String sheetName = XlsSheetNameType.drama_npc_disppear.getSheetName();
		Map<Integer, DramaNpcDisppear> dramaNpcDisppearMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcDisppearMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcDisppear.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(dramaNpcDisppearMap)) {
			return ;
		}
		
		for(Entry<Integer, DramaNpcDisppear> entry : dramaNpcDisppearMap.entrySet()) {
			DramaNpcDisppear npcDisppear = entry.getValue();
			Short npcId = npcDisppear.getNpcId();
			if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit in tag npcs");
			}
		}
		
		dramaBaseMap.putAll(dramaNpcDisppearMap);
	}
	
	private void loadNpcMove() {
		//加载剧情npc移动配置
		String fileName = XlsSheetNameType.drama_npc_move.getXlsName();
		String sheetName = XlsSheetNameType.drama_npc_move.getSheetName();
		Map<Integer, DramaNpcMove> dramaNpcMoveMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcMoveMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcMove.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(dramaNpcMoveMap)) {
			return ;
		}
		
		for(Entry<Integer, DramaNpcMove> entry : dramaNpcMoveMap.entrySet()) {
			DramaNpcMove npcMove = entry.getValue();
			short npcId = npcMove.getNpcId();
			if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit in tag npcs");
			}
		}
		
		dramaBaseMap.putAll(dramaNpcMoveMap);
	}
	
	private void loadNpcTalk() {
		//加载剧情npc说话配置
		String fileName = XlsSheetNameType.drama_talk.getXlsName();
		String sheetName = XlsSheetNameType.drama_talk.getSheetName();
		Map<Integer, DramaNpcTalk> dramaNpcTalkMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcTalkMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcTalk.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(dramaNpcTalkMap)) {
			return ;
		}
		
		for(Entry<Integer, DramaNpcTalk> entry : dramaNpcTalkMap.entrySet()) {
			DramaNpcTalk npcTalk = entry.getValue();
			short npcId = npcTalk.getNpcId();
			if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit in tag npcs");
			}
		}
		
		dramaBaseMap.putAll(dramaNpcTalkMap);
	}
	
	private void loadNpcAttack() {
		//加载剧情npc攻击配置
		String fileName = XlsSheetNameType.drama_attack.getXlsName();
		String sheetName = XlsSheetNameType.drama_attack.getSheetName();
		Map<Integer, DramaNpcAttack> dramaNpcAttackMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcAttackMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcAttack.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(dramaNpcAttackMap)) {
			return ;
		}
		
		for(Entry<Integer, DramaNpcAttack> entry : dramaNpcAttackMap.entrySet()) {
			DramaNpcAttack npcAttack = entry.getValue();
			short npcId = npcAttack.getNpcId();
			if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit in tag npcs");
			}
		}
		
		dramaBaseMap.putAll(dramaNpcAttackMap);
	}
	
	private void loadNpcBubble() {
		//加载剧情npc说话配置
		String fileName = XlsSheetNameType.drama_bubble.getXlsName();
		String sheetName = XlsSheetNameType.drama_bubble.getSheetName();
		Map<Integer, DramaBubble> dramaBubbleMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaBubbleMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaBubble.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(dramaBubbleMap)) {
			return ;
		}
		
		for(Entry<Integer, DramaBubble> entry : dramaBubbleMap.entrySet()) {
			DramaBubble bubble = entry.getValue();
			short npcId = bubble.getNpcId();
			if(DramaNpc.ROLE_NPCID != npcId && !dramaNpcMap.containsKey(npcId)) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", npcId=" + npcId + ", do exsit in tag npcs");
			}
		}
		
		dramaBaseMap.putAll(dramaBubbleMap);
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
	
	private void loadScreenShock() {
		//加载剧情屏幕震动配置
		String fileName = XlsSheetNameType.drama_screen_shock.getXlsName();
		String sheetName = XlsSheetNameType.drama_screen_shock.getSheetName();
		Map<Integer, DramaScreenShock> dramaScreenShockMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaScreenShockMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaScreenShock.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(dramaScreenShockMap)){
			return ;
		}
		
		for(Entry<Integer, DramaScreenShock> entry : dramaScreenShockMap.entrySet()) {
			DramaScreenShock screenShock = entry.getValue();
			if(screenShock.getLastTime() == 0) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName
						+ ", id=" + screenShock.getId() + ", lastTime <= 0");
			}
		}
		
		dramaBaseMap.putAll(dramaScreenShockMap);
	}
	
	private void loadNpcSet() {
		//加载剧情镜头配置
		String fileName = XlsSheetNameType.drama_npc_set.getXlsName();
		String sheetName = XlsSheetNameType.drama_npc_set.getSheetName();
		Map<Integer, DramaNpcSet> dramaNpcSetMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaNpcSetMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaNpcSet.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(dramaNpcSetMap)){
			return ;
		}
		dramaBaseMap.putAll(dramaNpcSetMap);
	}
	
	private void loadDramaTips() {
		//加载剧情镜头配置
		String fileName = XlsSheetNameType.drama_tips.getXlsName();
		String sheetName = XlsSheetNameType.drama_tips.getSheetName();
		Map<Integer, DramaTip> dramaTipMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			dramaTipMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaTip.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(dramaTipMap)){
			return ;
		}
		dramaBaseMap.putAll(dramaTipMap);
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
			return ;
		}
		
		for(Entry<Short, DramaPointTrigger> entry : pointTriggerMap.entrySet()) {
			DramaPointTrigger trigger = entry.getValue();
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
	
	private void loadQuestTrigger() {
		//加载进入地图触发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_quest.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_quest.getSheetName();
		Map<Integer, DramaQuestTrigger> questTriggerMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			questTriggerMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, DramaQuestTrigger.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(questTriggerMap)){
			return ;
		}
		
		for(Entry<Integer, DramaQuestTrigger> entry : questTriggerMap.entrySet()) {
			DramaQuestTrigger trigger = entry.getValue();
			int questId = trigger.getQuestId();
			checkTrigger(fileName, sheetName, trigger.getDramaId(), null, questId, null);
			byte type = trigger.getType();
			if(type == DramaQuestTrigger.TYPE_ACCEPT) {
				acceptQuestTriggerMap.put(questId, trigger);
			}
			else {
				submitQuestTriggerMap.put(questId, trigger);
			}
		}
		
	}
	
	private void loadNpcTrigger() {
		//加载进入地图触发剧情配置
		String fileName = XlsSheetNameType.drama_trigger_npc.getXlsName();
		String sheetName = XlsSheetNameType.drama_trigger_npc.getSheetName();
		List<DramaNpcTrigger> npcTriggerList = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			npcTriggerList = XlsPojoUtil.sheetToList(sourceFile, sheetName, DramaNpcTrigger.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ", sheetName = " + sheetName, ex);
		}
		if(Util.isEmpty(npcTriggerList)){
			return ;
		}
		
		for(DramaNpcTrigger trigger : npcTriggerList) {
			String npcId = trigger.getNpcId();
			String mapId = trigger.getMapId();
			checkTrigger(fileName, sheetName, trigger.getDramaId(), null, 0, npcId);
			String key = npcId + Cat.underline + mapId;
			byte type = trigger.getType();
			if(type == DramaNpcTrigger.TYPE_DIE) {
				npcDieTriggerMap.put(key, trigger);
			}
			else {
				npcBornTriggerMap.put(key, trigger);
			}
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
	public void triggerDrama(RoleInstance role, DramaTriggerType triggerType,
			short dramaId, String mapId, int questId, String npcId) {
		try{
			short realDramaId = 0;
			String dramaMapId = null;
			switch (triggerType) {
			case Point:
				DramaPointTrigger pointTrigger = pointTriggerMap.get(dramaId);
				if(null == pointTrigger) {
					return ;
				}
				realDramaId = dramaId;
				dramaMapId = pointTrigger.getMapId();
				break;
			case EntryMap:
				DramaEnterMapTrigger enterMaptrigger = enterMapTriggerMap.get(mapId);
				if(null == enterMaptrigger) {
					return ;
				}
				realDramaId = enterMaptrigger.getDramaId();
				dramaMapId = mapId;
				break;
			case AcceptQuest:
				DramaQuestTrigger acceptQuestTrigger = acceptQuestTriggerMap.get(questId);
				if(null == acceptQuestTrigger) {
					return ;
				}
				realDramaId = acceptQuestTrigger.getDramaId();
				dramaMapId = acceptQuestTrigger.getMapId();
				break;
			case SubmitQuest:
				DramaQuestTrigger submitQuestTrigger = submitQuestTriggerMap.get(questId);
				if(null == submitQuestTrigger) {
					return ;
				}
				realDramaId = submitQuestTrigger.getDramaId();
				dramaMapId = submitQuestTrigger.getMapId();
				break;
			case NpcDie:
				DramaNpcTrigger npcDieTrigger = npcDieTriggerMap.get(npcId + Cat.underline + mapId);
				if(null == npcDieTrigger) {
					return ;
				}
				realDramaId = npcDieTrigger.getDramaId();
				dramaMapId = npcDieTrigger.getMapId();
				break;
			case NpcBorn:
				DramaNpcTrigger npcBornTrigger = npcBornTriggerMap.get(npcId + Cat.underline + mapId);
				if(null == npcBornTrigger) {
					return ;
				}
				realDramaId = npcBornTrigger.getDramaId();
				dramaMapId = npcBornTrigger.getMapId();
				break;
			default:
				break;
			}
			
			if(realDramaId == 0 || dramaMapId == null) {
				return ;
			}
			
			//角色不在当前地图
			if(!role.getMapId().equals(dramaMapId)) {
				return ;
			}
			
			Drama drama = dramaMap.get(realDramaId);
			if(null == drama) {
				return ;
			}
			
			if(Drama.REPLAY_NO == drama.getReplay()
				&& null != GameContext.getUserDramaApp().getRoleDrama(role.getRoleId(), realDramaId)) {
				return ;
			}
			
			Message respMsg = createDramaInfoMsg(drama, dramaMapId);
			if(null == respMsg) {
				return ;
			}
			role.getBehavior().sendMessage(respMsg);
			
			if(Drama.REPLAY_YES == drama.getReplay()) {
				return ;
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
		} catch (Exception ex) {
			logger.error("dramaApp.triggerDrama error ", ex);
		}
		
	}
	
	private  Message createDramaInfoMsg(Drama drama, String mapId){
		List<DramaBase> dramaBaseList = drama.getDramaBaseList();
		if(Util.isEmpty(dramaBaseList)) {
			return null;
		}
		C3275_DramaInfoRespMessage resqMsg = new C3275_DramaInfoRespMessage();
		List<DramaBaseItem> dramaBaseItemList = new ArrayList<DramaBaseItem>();
		for(DramaBase dramaBase : dramaBaseList) {
			if(null == dramaBase) {
				continue;
			}
			dramaBaseItemList.add(dramaBase.getDramaBaseInfo());
		}
		resqMsg.setId(drama.getDramaId());
		resqMsg.setMapId(mapId);
		resqMsg.setDramaBaseItemList(dramaBaseItemList);
		return resqMsg;
	}

	@Override
	public void login(RoleInstance role) {
		try{
			List<RoleDrama> roleDramaList = GameContext.getBaseDAO().selectList(RoleDrama.class, 
					"roleId", role.getRoleId());
			if(Util.isEmpty(roleDramaList)) {
				return ;
			}
			
			for(RoleDrama roleDrama : roleDramaList) {
				roleDrama.setExistDb(true);
				GameContext.getUserDramaApp().addRoleHero(role.getRoleId(), roleDrama);
			}
		}catch(Exception e){
			logger.error("dramaApp.login error, ",e);
		}
	}

	@Override
	public void offline(RoleInstance role) {
		try{
			String roleId = role.getRoleId();
			Map<Short, RoleDrama> roleDramaMap = GameContext.getUserDramaApp().getRoleDramas(roleId);
			if(Util.isEmpty(roleDramaMap)){
				return;
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
		}
	}

	@Override
	public void enterMap(RoleInstance role, String mapId) {
		try{
			if(!Util.isEmpty(pointTriggerMap)) {
				List<DramaPointTriggerItem> itemList = new ArrayList<DramaPointTriggerItem>();
				for(Entry<Short, DramaPointTrigger> entry : pointTriggerMap.entrySet()) {
					DramaPointTrigger trigger = entry.getValue();
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
