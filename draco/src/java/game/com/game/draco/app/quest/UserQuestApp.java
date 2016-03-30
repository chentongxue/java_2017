package com.game.draco.app.quest;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.exception.GoodsIsOnlyException;
import sacred.alliance.magic.app.goods.exception.OutOfGoodsBagException;
import sacred.alliance.magic.base.CollectPointNotifyType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.app.quest.base.QuestListReqestType;
import com.game.draco.app.quest.base.QuestOpCode;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.base.QuestType;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.response.C0701_QuestListRespMessage;

public interface UserQuestApp extends NpcFunctionSupport, AppSupport{
	
	/**
	 * 主循环更新任务
	 */
	public void updateQuest(RoleInstance role)throws ServiceException;
	
	/**
	 * 获得任务掉落
	 */
	public List<GoodsOperateBean> getQuestFall(RoleInstance role,String npcId);
	
	/**
	 * 增加或修改任务日志信息
	 */
	public void insertOrUpdateQuestLog(RoleInstance role, int questId, int phase, int data1, int data2, int data3, QuestStatus status) throws ServiceException;
	
	/**
	 * 更新任务状态
	 */
	public void updateQuestStatus(RoleInstance role, int questId, QuestStatus status) throws ServiceException;
	
	/**
	 * 更新任务日志
	 */
	public void updateQuestLog(RoleInstance role, int questId, int index, OperatorType operator, int value) throws ServiceException ;
	
	/**
	 * 获得角色在某NPC上的任务选项
	 */
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,NpcInstance npc) ;
	
	/**
	 * 获取已接任务列表
	 */
	public Map<QuestType,List<QuestTempItem>> getReceiveQuest(RoleInstance role);
	
	/**
	 * 获取可接任务列表
	 */
	public Map<QuestType,List<QuestTempItem>> getCanAcceptQuest(RoleInstance role);
	
	/**
	 * 获取角色任务列表消息
	 */
	public C0701_QuestListRespMessage getQuestListRespMessage(RoleInstance role, QuestListReqestType reqestType);
	
	/**
	 * 接取任务
	 */
	public QuestOpCode acceptQuest(RoleInstance role,int questId) throws OutOfGoodsBagException, ServiceException;
	
	/** 
	 * 强制接受任务
	 */
	public void acceptQuestWithoutCondition(RoleInstance role,int questId) throws ServiceException;
	
	/** 
	 * 提交任务
	 */
	public QuestOpCode submitQuest(RoleInstance role,int questId) throws GoodsIsOnlyException,OutOfGoodsBagException,ServiceException;
	
	/** 
	 * 无条件提交任务（比如机器人）
	 */
	public QuestOpCode submitQuestNoCondition(RoleInstance role,int questId)throws ServiceException;
	
	/** 
	 * 放弃任务
	 */
	public int giveUpQuest(RoleInstance role, int questId) throws ServiceException;
	
	/** 
	 * 清除用户所有的任务(for debug)
	 */
	public void removeAllQuest(RoleInstance role) throws ServiceException;
	
    /** 
     * 接收并完成任务(for debug)
     */
    public void acceptAndCompleteQuest(RoleInstance role,int questId) throws  ServiceException;
    
    /** 
     * 接受任务(for debug)
     */
    public QuestOpCode debugAcceptQuest(RoleInstance role, int questId) throws OutOfGoodsBagException, ServiceException;
    
    /**
	 * 主推NPC处的任务详情面板
	 * 优先级：可提交的 > 可接取的主线 > 可接取的支线
	 */
	public void pushQuestViewMessage(RoleInstance role, String npcId);
	
	/** 主推下一个任务信息 **/
//	public void pushNextQuestViewMessage(RoleInstance role);
	
	/**
	 * 接取NPC身上所有任务
	 */
//	public void acceptNpcAllQuest(RoleInstance role, String npcId);
	
    /**
	 获取NPC头顶标识类型
	 */
	public byte getNpcQuestHeadSign(RoleInstance role,NpcInstance npc);
	
	/**
	 获取NPC头顶标识类型
	 */
	public byte getNpcQuestHeadSign(RoleInstance role,NpcTemplate npcTemp);
	
	/**
	 * 通知用户地图中所有NPC头顶任务标识
	 */
	public void notifyQuestNpcHeadSign(RoleInstance role);
	
	/**
	 * 将NPC头顶任务标识发给地图内用
	 * (NPC重生的时候调用)
	 */
	public void notifyQuestNpcHeadSign(NpcInstance npc);
	
	/**
	 * 进入地图
	 */
    public void enterMap(RoleInstance role)throws ServiceException;
	
    /**
     * 获得物品
     */
	public void pickupGoods(RoleInstance role,int goodsId,int goodsNum) throws ServiceException;
	
	/**
	 * 使用物品
	 */
	public void useGoods(RoleInstance role,int goodsId) throws ServiceException;
	
	/**
	 * 杀死怪
	 */
	public void killMonster(AbstractRole owner, NpcInstance npcInstance)throws ServiceException;
	
	/**
	 * 杀死玩家
	 */
	public void killRole(RoleInstance attacker, RoleInstance victim)throws ServiceException;
	
	/**
	 * 角色死亡
	 */
	public void death(RoleInstance role) throws ServiceException;
	
	public void triggerEvent(RoleInstance role,String eventId) throws ServiceException;
    
    /**
	 * 物品触发任务
	 * 1. 能
	 * 2. 任务不存在
	 * 3. 不符合条件
	 */
	public int questTrigger(RoleInstance role,GoodsTaskprops goods);
	
	/**
	 * 判断是否可以触发道具
	 * 1. 能
	 * 2. 任务不存在
	 * 3. 不符合条件
	 */
	public int isCanQuestTrigger(RoleInstance role, GoodsTaskprops goods);
	
    /**
     * 放弃物品通知
     * @param roleId 角色ID
     * @param goodsTemplateId
     * @param discardCount 放弃数目
     */
    public void discardGoodsNotify(String roleId,int goodsTemplateId,int discardCount) ;
    
    /**
     * 属性变化
     */
	public void discardAttributeNotify(String roleId);
    
	/**
	 * 角色升级
	 * @param role
	 */
    public void roleUpgrade(RoleInstance role);
    
    /**
     * 属性改变完成任务
     */
    public void changeAttriForQuest(RoleInstance role,int type) throws ServiceException;
    
    /**
     * 与NPC对话
     */
	public void chooseMenu(RoleInstance role, int menuId) throws ServiceException;
	
	/**
	 * 副本地图通关
	 */
	public void copyMapPass(RoleInstance role, String mapId) throws ServiceException;
	
	/** 副本通关 **/
	public void copyMapPass(RoleInstance role, short copyId) throws ServiceException;
	
	/**
	 * 地图刷怪波次
	 * @param role
	 * @param refreshIndex 刷怪波次
	 * @throws ServiceException
	 */
	public void mapRefreshNpc(RoleInstance role, int refreshIndex) throws ServiceException;
	
	/**
	 * 任务变化刷新采集点
	 * 是采集类型的任务才刷新
	 */
	public void refreshPoint(RoleInstance role, CollectPointNotifyType pointType, Quest quest);
	
}
