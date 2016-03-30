package com.game.draco.app.quest;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public abstract class QuestPhase {

	protected static final Logger logger = LoggerFactory.getLogger(QuestPhase.class);
	
	protected static GameContext context = GameContext.getGameContext();
	
	protected Quest master ;
	
	protected int phase;//当前阶段

	protected String taskInfoDesc;// 任务信息描述
	
	protected boolean checkFail = false;//配置检测失败
	
	public abstract int talkNpc(RoleInstance role, String npcId);
	
	public abstract int enterMap(RoleInstance role);
	
	public abstract int getGoods(RoleInstance role, int goodsId,int goodsNum);
	
	public abstract int getAttribute(RoleInstance role, int type);
	
	/**
	 * 杀怪收集物品
	 * 根据几率掉落物品
	 */
	public abstract int killMonster(RoleInstance role, String npcId);
	
	/**
	 * 杀怪收集物品
	 * 不掉落物品，只是按几率计数
	 */
	public abstract int killNpcFallCount(RoleInstance role, String npcId);
	
	public abstract int triggerEvent(RoleInstance role,String eventId);
	
	public abstract int death(RoleInstance role);
	
	public abstract int update(RoleInstance role);
	
	public abstract int useGoods(RoleInstance role, int goodsId);
	
	public abstract int chooseMenu(RoleInstance role, int menuId);
	
	public abstract void giveUp(RoleInstance role);
	
	public abstract List<GoodsOperateBean> getQuestFall(RoleInstance role, String key);
	
	/**判断当前阶段是否完成*/
	public abstract boolean isPhaseComplete(RoleInstance role) ;
	
	/**完成当前阶段时的操作  (eg. 接任务阶段给用户某个信件)*/
	public abstract int completePhaseAction(RoleInstance role);
	
	/**提交任务的逻辑 收回各阶段相关物品(额外)等*/
	public abstract Map<Integer,Integer> submitQuestGoodsMap() ;

    /**放弃时的逻辑 收回各阶段相关物品(额外)等*/
    public abstract Map<Integer,Integer> giveupQuestGoodsMap() ;

	/**任务条件*/
	public abstract List<QuestTerm> termList();
	
	/**获得阶段的当前序列数目*/
	public abstract int getCurrentNum(RoleInstance role,int index);
	
	protected String mapId;//任务所在的地图ID
	protected int mapX;//任务NPC x坐标
	protected int mapY;//任务NPC y坐标
	
	public int getMapX() {
		return mapX;
	}

	public void setMapX(int mapX) {
		this.mapX = mapX;
	}

	public int getMapY() {
		return mapY;
	}

	public void setMapY(int mapY) {
		this.mapY = mapY;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getTaskInfoDesc() {
		return taskInfoDesc;
	}

	public void setTaskInfoDesc(String taskInfoDesc) {
		this.taskInfoDesc = taskInfoDesc;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public Quest getMaster() {
		return master;
	}

	public void setMaster(Quest master) {
		this.master = master;
	}

	/**是否含有物品条件(1.接任务前有可能背包已经有需要的物品 2.放弃物品有可能使已经可提交的任务变为为完成状态)
	 * 含有物品条件的阶段必须将此字段设置为 true
	 * */
	protected boolean hasGoodsEffect = false ;

	public boolean isHasGoodsEffect() {
		return hasGoodsEffect;
	}
	
	/**
	 * 任务阶段的数量需要特殊处理
	 * 属性类型、组合任务、副本次数、活跃度数量、随机任务次数、好友数量
	 * @return
	 */
	public boolean isSpecialCurrCount() {
		return false;
	}
	
	public abstract int killRole(RoleInstance role);
	
	/** 杀怪限制 */
	public abstract int killMonsterLimit(RoleInstance role, String npcId);
	
	/** 副本某地图通关 */
	public abstract int copyMapPass(RoleInstance role, String mapId);
	
	/** 副本通关 */
	public abstract int copyPass(RoleInstance role, short copyId);
	
	/** 地图刷怪波次 */
	public abstract int mapRefreshNpc(RoleInstance role, int refreshIndex);
	
	/**
	 * 获得事件发生点
	 * @return
	 */
	public abstract Point getEventPoint(RoleInstance role) ;
	
	protected Point getPoint(String mapId,String npcId){
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId) ;
		if(null == map){
			return null ;
		}
		return map.getNpcBornPoint(npcId);
	}
	
	/**
	 * 任务阶段配置错误
	 * @param info
	 */
	protected void phaseCheckFail(String info){
		this.checkFail = true;
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	public boolean isCheckFail() {
		return checkFail;
	}
	
}
