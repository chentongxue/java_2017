package com.game.draco.app.goddess;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.goddess.config.GoddessConfig;
import com.game.draco.app.goddess.config.GoddessGrade;
import com.game.draco.app.goddess.config.GoddessLevelup;
import com.game.draco.app.goddess.config.GoddessLinger;
import com.game.draco.app.goddess.config.GoddessPvpConfig;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.goddess.vo.GoddessEnlistResult;
import com.game.draco.app.goddess.vo.GoddessLingerResult;
import com.game.draco.app.goddess.vo.GoddessOnBattleResult;
import com.game.draco.app.goddess.vo.GoddessPvpInfoListResult;
import com.game.draco.app.goddess.vo.GoddessUpgradeResult;
import com.game.draco.message.item.GoddessBattleItem;

public interface GoddessApp extends Service{
	void login(RoleInstance role) ;
	void logout(RoleInstance role) ;
	Result useGoddessGoods(RoleInstance role, RoleGoods roleGoods) throws ServiceException ;
	List<Integer> getAllGoddessIds();
	GoodsGoddess getGoddessName(int goddessId);
	GoddessLevelup getGoddessLevelup(int goddessId, int level);
	GoddessOnBattleResult onBattle(RoleInstance role, RoleGoddess goddess, byte onBattle);
	GoddessGrade getGoddessGrade(byte grade);
	GoddessConfig getGoddessConfig();
	GoddessUpgradeResult upgrade(RoleInstance role, RoleGoddess goddess);
	GoddessLinger getGoddessLinger(int goddessId, int num);
	void roleGoddessUseSkill(RoleInstance role, AbstractRole targetRole);
	GoddessLingerResult goddessLinger(RoleInstance role, int goddessId);
	RoleGoddess getOnBattleGoddes(String roleId);
	void saveRoleGoddess(RoleGoddess goddess);
	Message createGoddessLingerInfoMsg(RoleGoddess roleGoddess);
	void addExp(RoleInstance role, int addExp);
	AttriBuffer getAttriBuffer(RoleInstance role) ;
	GoddessEnlistResult goddessEnlist(RoleInstance role, int goddessId);
	int getBattleScore(RoleGoddess goddess);
	AttriBuffer getEquipAttriBuffer(String roleId);
	GoddessBattleItem getOnBattleGoddessItem(RoleInstance role);
	//pvp
	GoddessPvpInfoListResult getPvpInfoList(RoleInstance role, byte opType);
	void sendPvpInfoPanel(RoleInstance role, byte opType);
	Result challenge(RoleInstance role, String targetRoleId, String targetRoleName, 
			int targetGoddssId, byte opType);
	void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type);
	Result refreshPvpInfoList(RoleInstance role);
	float getGoddessWeakRate(RoleGoddess goddess);
	void goddessWeakTimeOver(RoleInstance role);
	boolean isOwnGoddess(RoleInstance role);
	void preRoleAttrToStore(RoleInstance role, AsyncPvpRoleAttr roleAttr);
	GoddessPvpConfig getGoddessPvpConfig();
}
