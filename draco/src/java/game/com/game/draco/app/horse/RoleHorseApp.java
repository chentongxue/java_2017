package com.game.draco.app.horse;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.domain.RoleHorseCache;
import com.game.draco.app.horse.domain.RoleHorseSkill;
import com.game.draco.app.horse.vo.RoleHorseLevelUpResult;
import com.game.draco.app.horse.vo.RoleHorseSkillResult;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.message.item.RoleHorseItem;
import com.game.draco.message.response.C2601_RoleHorseInfoRespMessage;
import com.game.draco.message.response.C2605_RoleHorseUpgradeInfoRespMessage;
import com.game.draco.message.response.C2612_RoleHorseSkillInfoRespMessage;
import com.game.draco.message.response.C2613_RoleHorseSkillListRespMessage;

public interface RoleHorseApp extends AppSupport{

	/**
	 * 获得坐骑
	 * @param roleId
	 * @return
	 */
	RoleHorse getRoleHorse(int roleId,int horseId) ;
	
	/**
	 * 坐骑升星
	 * @param role
	 * @param horseId
	 * @return
	 */
	RoleHorseLevelUpResult levelUp(RoleInstance role, int horseId);
	
	/**
	 * Map<坐骑ID,坐骑对象>
	 * @param roleId
	 * @return
	 */
	Map<Integer,RoleHorse> getAllRoleHorseByRoleId(int roleId) ;
	
	/**
	 * 判断是否可升级
	 * @param roleId
	 * @param horseId
	 * @return
	 */
	RoleHorseLevelUpResult isLevelUp(RoleInstance role,RoleHorse roleHorse); 
	
	/**
	 * 计算战力
	 * @param roleHorse
	 * @return
	 */
	public int getBattleScore(RoleHorse roleHorse,boolean flag);
	
	/**
	 * 发送2601 坐骑消息
	 */
	C2601_RoleHorseInfoRespMessage sendC2601_RoleHorseInfoRespMessage(RoleInstance role,int horseId);
	
	/**
	 * 添加或更新坐骑
	 */
	void saveOrUpdRoleHorse(RoleHorse roleHorse);
	
	/**
	 * 添加或更新坐骑技能
	 */
	void saveOrUpdRoleHorseSkill(RoleHorseSkill horseSkill);
	
	RoleHorse onBattle(RoleInstance role, int horseId,byte state);
	
	RoleHorse getOnBattleRoleHorse(int roleId);
	
	/**
	 * 获得坐骑列表
	 */
	List<RoleHorseItem> getRoleHorseList(RoleInstance role);
	
	/**
	 * 判断是否可以使用坐骑物品
	 */
	boolean isUseGoodsHorse(int roleId,int goodsHorseId);
	
	/**
	 * 添加新的坐骑
	 */
	Result addRoleHorse(RoleInstance role,int goodsHorseId);

	/**
	 * 坐骑属性
	 * @param role
	 * @return
	 */
	AttriBuffer getAttriBuffer(RoleInstance role);
	
	/**
	 * 获得坐骑骑乘资源
	 */
	HorseProp getOnBattleHorseProp(RoleInstance role);

	void calcHorseMoveSpeed(RoleInstance role, RoleHorse oldHorse);
	
	int getRoleHorseNum(int roleId);

	/**
	 * 获得最强坐骑ID
	 * @param roleId
	 * @return
	 */
	int getBestStrongHorse(RoleInstance role);

	/**
	 * 获得他人坐骑数据
	 * @param roleId
	 * @return
	 */
	RoleHorseCache getRoleHorseBattleCache(int roleId);

	/**
	 * 封装坐骑技能
	 * @param roleId
	 * @param horseSkillList
	 * @return
	 */
	Map<Short, RoleSkillStat> packRoleSkillStat(int roleId,List<RoleHorseSkill> horseSkillList);
	
	/**
	 * 兑换
	 * @param horseId
	 * @param goodsId
	 * @return
	 */
	Result exchange(RoleInstance role,int horseId);

	/**
	 * 训练技能
	 * @param roleHorse
	 * @param skillId
	 * @return
	 */
	RoleHorseSkillResult trainSkill(RoleInstance role,RoleHorse roleHorse, RoleHorseSkill horseSkill);
	
	/**
	 * 发送2612 坐骑技能消息
	 */
	C2612_RoleHorseSkillInfoRespMessage sendC2612_RoleHorseSkillInfoRespMessage(RoleInstance role,RoleHorse roleHorse,RoleHorseSkill horseSkill);

	/**
	 * 坐骑数据
	 * @param horseId
	 * @param quality
	 * @param star
	 * @return
	 */
	HorseProp getHorseProp(int horseId, byte quality, byte star);

	C2605_RoleHorseUpgradeInfoRespMessage sendC2605_RoleHorseUpgradeInfoRespMessage(RoleInstance role,RoleHorse roleHorse);
	
	C2613_RoleHorseSkillListRespMessage sendC2613_RoleHorseSkillListRespMessage(RoleInstance role,RoleHorse roleHorse);
	
	/**
	 * 使用物品获得坐骑
	 * @throws ServiceException 
	 */
	Result useHorseGoods(RoleInstance role,RoleGoods roleGoods,boolean confirm) throws ServiceException;
	
	Result useHorseTemplate(RoleInstance role, GoodsHorse goodsHorse)throws ServiceException;
	
	HorseProp getNextHorsePorp(int horseId,byte quality,byte star);
	
	public boolean isReachMaxStar(RoleHorse roleHorse);
	
	public List<RoleHorseSkill> getRoleHorseSkill(int roleId, int horseId);
	
	/**
	 * 广播坐骑变更视野消息
	 */
	void broadcastHorse(RoleInstance role,int resId,int horseId,byte state);
	
}
