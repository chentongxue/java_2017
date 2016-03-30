package com.game.draco.app.horse;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.vo.RoleHorseLevelUpResult;
import com.game.draco.message.item.RoleHorseListItem;
import com.game.draco.message.response.C2601_RoleHorseInfoRespMessage;
import com.game.draco.message.response.C2604_RoleHorseManshipInfoRespMessage;
import com.game.draco.message.response.C2605_RoleHorseUpgradeInfoRespMessage;

public interface RoleHorseApp {

	/**
	 * 角色登录初始化坐骑数据
	 */
	void onJoinGame(RoleInstance role);
	
	/**
	 * 角色推出游戏
	 * @param role
	 */
	void onLeaveGame(int roleId) ;
	
	/**
	 * 获得坐骑
	 * @param roleId
	 * @return
	 */
	RoleHorse getRoleHorse(int roleId,int horseId) ;
	
	/**
	 * 坐骑升级
	 * @param role
	 * @param horseId
	 * @return
	 */
	RoleHorseLevelUpResult levelUp(RoleInstance role, int horseId);
	
	/**
	 * 坐骑骑术升级
	 * @param role
	 * @param horseId
	 * @return
	 */
	RoleHorseLevelUpResult manshipLevelUp(RoleInstance role, int horseId);
	
	/**
	 * 坐骑添加经验
	 * @param roleId
	 * @param horseId
	 * @param exp
	 */
	boolean addRoleHorseExp(RoleInstance role,RoleHorse roleHorse,int exp);
	
	/**
	 * Map<坐骑ID,坐骑对象>
	 * @param roleId
	 * @return
	 */
	Map<Integer,RoleHorse> getAllRoleHorseByRoleId(int roleId) ;
	
	/**
	 * 判断是否可升品
	 */
	boolean isUpgrade(int roleId,int horseId);
	
	/**
	 * 判断是否可升级
	 * @param roleId
	 * @param horseId
	 * @return
	 */
	boolean isLevelUp(int roleId, int horseId); 
	
	/**
	 * 计算战力
	 * @param roleHorse
	 * @return
	 */
	public int getBattleScore(RoleHorse roleHorse,boolean flag);
	
	/**
	 * 坐骑升阶扣物品
	 * @param role
	 * @param horseId
	 * @return
	 */
	RoleHorseLevelUpResult upgradeHorseLevelUpConsume(RoleInstance role, int horseId);
	
	/**
	 * 坐骑升阶到下一层
	 * @param role
	 * @param roleHorse
	 * @param num(数量)
	 */
	boolean roleHorseUpgrade(RoleInstance role,RoleHorse roleHorse,int num);
	
	/**
	 * 发送2601 坐骑消息
	 */
	C2601_RoleHorseInfoRespMessage sendC2601_RoleHorseInfoRespMessage(RoleInstance role,int horseId);
	
	/**
	 * 发送2604 坐骑骑术消息
	 */
	C2604_RoleHorseManshipInfoRespMessage sendC2604_RoleHorseManshipInfoRespMessage(int roleId,int horseId);
	
	/**
	 * 发送2605 坐骑升阶消息
	 */
	C2605_RoleHorseUpgradeInfoRespMessage sendC2605_RoleHorseUpgradeInfoRespMessage(RoleInstance role,int horseId);
	
	/**
	 * 添加或更新坐骑
	 */
	void saveOrUpdRoleHorse(RoleHorse roleHorse);
	
	RoleHorse onBattle(RoleInstance role, int horseId,byte state);
	
	RoleHorse getOnBattleRoleHorse(int roleId);
	
	/**
	 * 获得坐骑列表
	 */
	List<RoleHorseListItem> getRoleHorseList(int roleId);
	
	/**
	 * 获得坐骑
	 */
	RoleHorseListItem getRoleHorseItem(int roleId,int horseId);
	
	/**
	 * 广播坐骑变更视野消息
	 */
	void broadcast(RoleInstance role,Message msg);
	
	/**
	 * 判断是否可以使用坐骑物品
	 */
	boolean isUseGoodsHorse(int roleId,int goodsHorseId);
	
	/**
	 * 添加新的坐骑
	 */
	void addRoleHorse(RoleInstance role,int goodsHorseId);

	/**
	 * 坐骑属性
	 * @param role
	 * @return
	 */
	AttriBuffer getAttriBuffer(RoleInstance role);
	
	/**
	 * 获得坐骑骑乘资源ID
	 */
	HorseBase getHorseResId(int roleId);

	void calcHorseMoveSpeed(RoleInstance role, RoleHorse oldHorse);
}
