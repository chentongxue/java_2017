package com.game.draco.app.npc.refreshrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class RefreshRuleAppImpl implements RefreshRuleApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer, List<RefreshRule>> ruleMap = new HashMap<Integer, List<RefreshRule>>();

	@Override
	public void start() {
		this.loadRule();
	}
	
	private void loadRule() {
		//加载配置项
		String fileName = XlsSheetNameType.map_npc_refresh_rule.getXlsName();
		String sheetName = XlsSheetNameType.map_npc_refresh_rule.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<RefreshRule> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, RefreshRule.class);
			if(Util.isEmpty(list)) {
				return;
			}
			for(RefreshRule rule : list) {
				if(null == rule) {
					continue;
				}
				int ruleId = rule.getRuleId();
				String npcId = rule.getBornnpcid();
				NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
				if (null == npcTemplate) {
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("RefreshRuleApp.loadRule error: ruleId=" + ruleId + ",npcId="+ npcId +",npcTemplate is null");
					continue;
				}
				if(!ruleMap.containsKey(ruleId)) {
					ruleMap.put(ruleId, new ArrayList<RefreshRule>());
				}
				ruleMap.get(ruleId).add(rule);
			}
			//刷怪规则排序
			for(List<RefreshRule> npcRuleList : this.ruleMap.values()){
				if(Util.isEmpty(npcRuleList)){
					continue;
				}
				this.sortNpcRule(npcRuleList);
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void sortNpcRule(List<RefreshRule> npcRuleList){
		Collections.sort(npcRuleList, new Comparator<RefreshRule>(){
			@Override
			public int compare(RefreshRule rule1, RefreshRule rule2) {
				if(rule1.getBatchIndex() < rule2.getBatchIndex()) {
					return -1;
				}
				if(rule1.getBatchIndex() > rule2.getBatchIndex()) {
					return 1;
				}
				if(rule1.getBatchIndex() == rule2.getBatchIndex()) {
					if(rule1.getBornTime() < rule2.getBornTime()) {
						return -1;
					}
					if(rule1.getBornTime() > rule2.getBornTime()) {
						return 1;
					}
				}
				return 0;
			}
		});
	}
	
	public int refresh(int ruleId, int index, Date startTime, MapInstance mapInstance, boolean isQuest) {
		List<RefreshRule> ruleList = this.ruleMap.get(ruleId);
		if(Util.isEmpty(ruleList)) {
			return index;
		}
		int size = ruleList.size();
		if(index >= size){
			return index;
		}
		int curBatchIndex = -1;
		//从倒计时开始到现在的时间（单位：秒）
		int time = DateUtil.getSecondMargin(startTime);
		boolean hasEnemy = hasEnemy(mapInstance.getNpcList());
		boolean flag = false;//是否已经开始按拨刷怪的标识
		for(int i = index; i < size; i++){
			try{
				RefreshRule npcRule = ruleList.get(i);
				int bornTime = npcRule.getBornTime();
				int batchIndex = npcRule.getBatchIndex();
				if(batchIndex <= 0) {//表示不是按拨刷新的怪
					if(bornTime > time){
						return index;
					}
					refreshNpc(npcRule, mapInstance);
					index++;
				}else{//是按拨刷新的怪
					//如果刷新时间是0，必须等地图里的怪都打完再刷新
					if(bornTime == 0 && hasEnemy) {
						return index;
					}
					//flag:如果按拨刷怪已经开始，不管时间是否符合都把本拨刷完
					//没到刷新时间，并且没开始按拨刷怪(如果按拨刷怪已经开始，不管时间是否符合都把本拨刷完)
					if(bornTime > time && !flag){
						//没到刷新时间的时候，如果是第一波怪或者地图里有怪就不刷新
						if(batchIndex == 1 || hasEnemy) {
							return index;
						}
					}
					if(curBatchIndex == -1) {
						curBatchIndex = batchIndex;
						if(isQuest){
							this.refreshQuest(mapInstance, batchIndex);
						}
					}
					if(curBatchIndex != batchIndex) {
						return index;
					}
					refreshNpc(npcRule, mapInstance);
					index++;
					flag = true;
				}
			}catch(Exception e){
				logger.error("RefreshRuleApp.refresh index=" +index+" error",e);
				index++;
			}
		}
		return index;
	}
	
	/**
	 * 刷怪
	 * @param npcRule
	 */
	private void refreshNpc(RefreshRule npcRule, MapInstance mapInstance){
		NpcBorn npcBorn = npcRule.getNpcBorn();
		mapInstance.npcBorn(-1, npcBorn, false);
		broadcast(npcRule.getBroadcast(), mapInstance);
	}
	
	private void broadcast(String content, MapInstance mapInstance){
		try{
			if(Util.isEmpty(content)){
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, content, null, mapInstance);
		}catch(Exception e){
			logger.error("broadcast error",e);
		}
	}

	private boolean hasEnemy(Collection<NpcInstance> npcList){
		if(Util.isEmpty(npcList)){
			return false;
		}
		for(NpcInstance npc : npcList){
			if(npc.getNpc().getNpctype() == NpcType.monster.getType()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getRefreshMax(int ruleId) {
		List<RefreshRule> list = ruleMap.get(ruleId);
		if(Util.isEmpty(list)) {
			return 0;
		}
		return list.size();
	}
	
	@Override
	public Set<String> getBossId(int ruleId) {
		Set<String> set = new HashSet<String>();
		List<RefreshRule> npcList = ruleMap.get(ruleId);
		if(Util.isEmpty(npcList)){
			return set;
		}
		for(RefreshRule npc : npcList){
			if(npc.isBoss()){
				set.add(npc.getBornnpcid());
			}
		}
		return set;
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public boolean ruleIsExist(int ruleId) {
		return this.ruleMap.containsKey(ruleId);
	}
	
	private void refreshQuest(MapInstance mapInstance, int batchIndex) {
		try{
			Collection<RoleInstance> roleList = mapInstance.getRoleList();
			if(Util.isEmpty(roleList)) {
				return;
			}
			for(RoleInstance role : roleList) {
				if(null == role) {
					continue;
				}
				GameContext.getUserQuestApp().mapRefreshNpc(role, batchIndex);
			}
		}catch(Exception e){
			logger.error("RefreshRuleApp.refreshQuest",e);
		}
	}
}
