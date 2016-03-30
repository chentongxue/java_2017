package com.game.draco.app.recovery;

import java.util.Collection;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hint.HintSupport;
import com.game.draco.app.recovery.config.RecoveryConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeHungUpConfig;
import com.game.draco.app.recovery.config.RecoveryOutPutConfig;
import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.vo.RecoveryResult;
/**
 * 一键追回
 */
public interface RecoveryApp extends Service, AppSupport, HintSupport{

	public Message openRecoveryPanel(RoleInstance role);
	public Message recoveryAward(RoleInstance role, String id, byte consumeType);
	public RecoveryResult recoveryAllAwards(RoleInstance role, byte recoveryType, byte confirm);
	public Message openRecoveryInfo(RoleInstance role, String id);
	/**
	 * 从数据库获取玩家的一键追回数据，并将之加入缓存
	 * @param role
	 * @param id
	 * @return
	 * @date 2014-10-24 下午04:51:32
	 */
	public RoleRecovery getRoleRecovery(RoleInstance role, String id);
	public RecoveryConsumeConfig getRoleRecoveryConsumeConfig(String recoveryId,
			byte consumeType, byte vipLevel);
	public RecoveryConsumeHungUpConfig getRecoveryConsumeHungUpConfig(byte consumeType, int roleLevel);

	
	public Collection<RecoveryConsumeConfig> getConsumeConfigsByRecoveryId(String id);
	public Collection<RecoveryOutPutConfig> getRecoveryOutPutConfigs(String id,
			int roleLevel);
	public RecoveryConfig getRecoveryConfig(String id);
	public RecoveryConsumeConfig getRecoveryConsumeConfig(String recoveryId,byte consumeType);
	public void saveUpdateRecovery(RoleRecovery recovery);

	/**
	 * 一键追回离线经验
	 * @param role
	 * @param recoveryId
	 * @param roleLevel 昨天的等级
	 * @param onlineSeconds
	 * 
	 */
	public void saveHungUpRecovery(RoleInstance role, int exp);
	/**
	 * 经验副本
	 * 无尽深渊副本
	 * 巨龙巢穴副本 
	 * @param role
	 * @param roleLevel 角色昨天的等级
	 * @param copyNum 可追回的副本次数
	 * @param copyId 副本id
	 */
	public void saveCopyRecovery(RoleInstance role, int copyNum, short copyId);

	/**
	 * 肉山必须死
	 * @param role
	 * @param roleLevel
	 * @param copyNum
	 * @date 2014-10-23 下午03:49:50
	 */
	public void saveBossKillRecovery(RoleInstance role, int copyNum);
	/**
	 * 阵营战
	 * @param role
	 * @param roleLevel
	 * @param copyNum 如果昨天未完成，置为1
	 * @date 2014-10-23 下午03:50:35
	 */
	public void saveCampBattleRecovery(RoleInstance role, int copyNum);
	/**
	 * 谁与争锋
	 * @param role
	 * @param roleLevel
	 * @param copyNum 如果昨天未完成，置为1
	 * @date 2014-10-23 下午03:52:07
	 */
	public void saveArenaRecovery(RoleInstance role, int copyNum);
	/**
	 * 每日任务
	 * @param role
	 * @param roleLevel
	 * @param copyNum
	 * @date 2014-10-23 下午03:54:08
	 */
	public void saveDailyQuestRecovery(RoleInstance role, int copyNum);
	/**
	 * 神仙福地（赏金宝库）
	 * @param role
	 * @param roleLevel
	 * @param copyNum
	 * @date 2014-10-23 下午08:41:59
	 */
	public void saveAngelChestRecovery(RoleInstance role, int copyNum);
	
	/**
	 * 需要在其他模块初始化后调用
	 * @param role
	 * @return
	 * @date 2014-10-29 下午03:52:22
	 */
	public boolean hasFreeRecovery(RoleInstance role);

	
}
