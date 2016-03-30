package com.game.draco.app.quest;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.message.item.QuestTermItem;

public @Data abstract class QuestTerm<T extends QuestTermItem> {
	
	protected QuestTermType questTermType;//任务条件类型
	protected String parameter;//不同类型对应的参数
	protected int count;//目标数量
	protected String mapId;//地图ID
	protected String mapName;//地图名称
	
	public QuestTerm(){
		
	}
	
	/**
	 * 任务条件初始化赋值
	 * @param questTermType 条件类型
	 * @param count 所需数量
	 * @param parameter 参数
	 */
	public void setBaseValue(QuestTermType questTermType, int count, String parameter){
		this.questTermType = questTermType;
		this.count = count;
		this.parameter = parameter;
	}
	
	/**
	 * 获取任务条件的Item
	 * @param role
	 * @param quest
	 * @param canSubmit
	 * @param index
	 * @return
	 */
	public abstract T getQuestTermItem(RoleInstance role, Quest quest, boolean canSubmit, int index);
	
	/**
	 * 公用赋值
	 * @param role
	 * @param quest
	 * @param canSubmit
	 * @param index
	 * @param item
	 */
	protected void setBaseItemValue(RoleInstance role, Quest quest, boolean canSubmit, int index, QuestTermItem item){
		item.setTermType(this.questTermType.getType());
		if(canSubmit){
			item.setCurrentNum((short) this.count);
		}else{
			item.setCurrentNum((short) this.getCurrCount(role, quest, index));
		}
		item.setMustNum((short) this.count);
	}
	
	/**
	 * 获取任务进度
	 * @param role 角色
	 * @param quest 任务
	 * @param index 条件所在序列
	 * @return
	 */
	public int getCurrCount(RoleInstance role, Quest quest, int index){
		int currCount = 0;
		//不需要特殊处理的，返回任务日志中的进度
		if(!this.questTermType.isSpecial()){
			RoleQuestLogInfo questLogInfo = role.getQuestLogMap().get(quest.getQuestId());
			if(null == questLogInfo){
				return 0;
			}
			int[] logData = questLogInfo.getDataValues();
			currCount = logData[index];
			return Math.min(currCount, this.count);
		}
		//处理特殊条件，如收集物品需要检查背包等
		if(this.questTermType.isNeedGoods()){
			int goodsId = Integer.parseInt(this.parameter);
			currCount = role.getRoleBackpack().countByGoodsId(goodsId);
		}
		if(QuestTermType.Attribute == this.questTermType){
			currCount = role.get(AttributeType.get(Byte.valueOf(this.parameter)));
		}
		return Math.min(currCount, this.count);
	}
	
	/**
	 * 查找物品名称
	 * @param goodsId
	 * @return
	 */
	protected String findGoodsName(int goodsId){
		GoodsBase goodsBase = GameContext.getGameContext().getGoodsApp().getGoodsBase(goodsId);
		if(null == goodsBase){
			return "";
		}
		return goodsBase.getName();
	}
	
	/**
	 * 查找NPC名称
	 * @param npcId
	 * @return
	 */
	protected String findNpcName(String npcId){
		if(Util.isEmpty(npcId)){
			return null;
		}
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
		if(null == npcTemplate){
			this.checkFail("npcId=" + npcId + ",this npc is not exist!");
			return null;
		}
		return npcTemplate.getNpcname();
	}
	
	/**
	 * 初始化地图名称
	 */
	protected void initMapName(){
		if(Util.isEmpty(this.mapId)){
			return;
		}
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(this.mapId);
		if(null == map){
			this.checkFail("mapId=" + this.mapId + ",this map is not exist!");
			return;
		}
		this.mapName = map.getMapConfig().getMapdisplayname();
	}
	
	/**
	 * 查找采集点名称
	 * @param eventId
	 * @return
	 */
	protected String findCollectPointName(String eventId){
		if(Util.isEmpty(eventId)){
			return null;
		}
		Map<String, CollectPoint> dataMap = GameContext.getCollectPointLoader().getDataMap();
		CollectPoint event = dataMap.get(eventId);
		if(null == event){
			this.checkFail("id=" + eventId + ",this CollectPoint is not exist!");
			return null;
		}
		return event.getName();
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error("quest script error: questTermType=" + this.questTermType.getType() + "," + info);
		Log4jManager.checkFail();
	}
	
}
