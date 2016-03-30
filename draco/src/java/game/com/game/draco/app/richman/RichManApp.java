package com.game.draco.app.richman;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.richman.config.RichManBox;
import com.game.draco.app.richman.config.RichManCard;
import com.game.draco.app.richman.config.RichManConfig;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.config.RichManMapEvent;
import com.game.draco.app.richman.config.RichManState;
import com.game.draco.app.richman.vo.RichManEventType;
import com.game.draco.app.richman.vo.event.RichManEventLogic;
import com.game.draco.message.item.RichManCardItem;

public interface RichManApp extends Service, AppSupport{
	final static int TEN_THOUSAND = 10000;
	
	/**
	 * 玩家总点券数
	 */
	int getTotalCoupon(int roleId);
	/**
	 * 更新玩家总点券数
	 */
	void setTotalCoupon(int roleId, int coupon);
	/**
	 * 玩家今日点券数
	 */
	int getTodayCoupon(int roleId);
	/**
	 * 更新玩家今日点券数
	 */
	void setTodayCoupon(int roleId, int todayCoupon);
	/**
	 * 玩家进入大富翁地图
	 */
	Result enterMap(RoleInstance role);
	/**
	 * 大富翁所有地图事件(固定事件)
	 */
	Map<Byte, RichManMapEvent> getAllMapEvent();
	/**
	 * 大富翁地图格子数
	 */
	byte getMapGridNum();
	/**
	 * 大富翁事件配置
	 */
	RichManEvent getRichManEvent(int eventId);
	/**
	 *  卡片物品简单信息列表
	 */
	List<RichManCardItem> getRichManCardItemList();
	/**
	 * 玩家剩余免费遥控骰子次数
	 */
	byte getFreeDiceNormalNum(int roleId);
	/**
	 * 玩家剩余免费遥控骰子次数
	 */
	byte getFreeDiceRemoteNum(int roleId);
	/**
	 * 玩家剩余免费双倍骰子次数
	 */
	byte getFreeDiceDoubleNum(int roleId);
	/**
	 * 玩家本轮参加大富翁次数 
	 */
	byte getRoundJionNum(int roleId);
	/**
	 * 玩家掷骰子 
	 */
	Result roleDice(RoleInstance role, String paramStr);
	/**
	 * 大富翁事件逻辑类 
	 */
	RichManEventLogic getEventLogic(RichManEventType eventType);
	/**
	 * 玩家到达指定格子 
	 */
	void roleArrived(RoleInstance role);
	/**
	 * 玩家活动物品
	 */
	void roleGetGoods(int roleId, int goodsId);
	/**
	 * 获得大富翁宝箱配置
	 */
	RichManBox getRichManBox(int boxId);
	/**
	 * 大富翁基本配置 
	 */
	RichManConfig getRichManConfig();
	/**
	 * 修改玩家今日点券数 
	 */
	void changeRoleToadyCoupon(RoleInstance role, int value);
	/**
	 * 随机一个事件 
	 */
	RichManEvent getRandomEvent();
	/**
	 *  随机一张卡片
	 */
	int getRandomCardId();
	/**
	 * 玩家使用道具卡 
	 */
	Result roleUseCard(RoleInstance role, int cardId, int[] targetIds);
	/**
	 * 返回道具卡配置 
	 */
	RichManCard getRichManCard(int cardId);
	/**
	 * 通知客户端大富翁状态(瘫痪，保护)结束 
	 */
	void notifyRichManRoleStatOver(RoleInstance role);
	/**
	 *  大富豪状态(瘫痪,保护)配置
	 */
	RichManState getRichManState(byte stateId);
	/**
	 * 返回大富翁地图数据并广播角色进入 
	 */
	void mapGetDataAndEnter(RoleInstance role);
}
