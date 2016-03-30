package com.game.draco.app.union.battle;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.app.union.battle.config.UnionBattleConfig;
import com.game.draco.app.union.battle.config.UnionBattleKillMsgConfig;
import com.game.draco.app.union.battle.config.UnionBattleKilledMsgConfig;
import com.game.draco.app.union.battle.domain.UnionBattle;
import com.game.draco.message.response.C2532_UnionBattleOccupyInfoRespMessage;

public interface UnionBattleApp extends NpcFunctionSupport, Service, AppSupport{

	/**
	 * 打开公会站的面板
	 * @return
	 * @date 2014-11-28 上午11:08:28
	 */
	public Message openPanel(RoleInstance role);
	/**
	 * 进入到公会地图
	 * 防守公会的角色进入室内（map3）
	 * 进攻公会的角色进入野外（map1）
	 * @param role
	 * @return
	 * @date 2014-11-28 下午05:44:44
	 */
	public Result joinBattle(RoleInstance role, byte mapIndex);

	public void saveUpdateDb(UnionBattle battle);

	public UnionBattle getUnionBattle(int battleId);
	
	public UnionBattleKillMsgConfig getUnionBattleKillMsgConfig(int killNum);
	public UnionBattleKilledMsgConfig getUnionBattleKilledMsgConfig(int killNum);
	/**
	 * 击杀每一个目标所获得的DKP奖励
	 * @return
	 * @date 2014-12-3 上午11:55:16
	 */
	public int getKillDkpAward();
	public Integer getUnionBattleIdByMapId(String mapId);
	public UnionBattle getUnionBattleByMapId(String mapId);
	public void unionBattleOver( String unionId);
	public void notifyEnterUnionBattle( String instanceId);
	public Active getActive();
	/**
	 * 活动是否开启
	 */
	public boolean isUnionBattleActiveTimeOpen();
	/**
	 * 通知公会战开始
	 */
	public void broadCastBegin(String mapId);
	public boolean isUnionBattleBoss(String roleId, String mapId);
	public UnionBattleConfig getUnionBattleConfigByMapId(String mapId);
	public Map<String, Integer> getKillMap(int BattleId);
	public void putKillMap(int battleId, String roleId, int killNum);
	public Integer removeFromKillMap(Integer battleId, String roleId);
	public int getUnionBattleKillNum(int unionBattleId, String roleId);
	/**
	 * 公会战活动结束
	 * 通知全服（飘字）
	 * 在线玩家提示排行榜和战绩
	 */
	public void endUnionBattleActive();
	public void bossKilled(int battleId, RoleInstance role, AbstractRole boss);
	/**
	 * 初始化公会战
	 * @param unionBattleId
	 */
	public void initUnionBattle(int unionBattleId);
	public void deleteUnionBattle(int battleId);
	public UnionBattle getUnionBattleNotNull(int battleId);
	public String getBossId(int unionBattleId);
	public Point getBossPoint(int unionBattleId);
	public boolean hasBoss(int battleId, String mapId);
	public boolean addDkp(RoleInstance role, int dkp);
	public String getUnionNameByMapId(String mapId);

	public C2532_UnionBattleOccupyInfoRespMessage getUnionBattleOccupyInfoMessageByMapIndex(byte mapIndex);
	public String getUnionNameByMapIndex(byte mapIndex);
	public Message getUnionBattleInfo(RoleInstance role, byte mapIndex);
	public void deathRecord(AbstractRole attacker, AbstractRole victim);
	/**
	 * 公会战结束后可查询
	 */
	public Message getUnionBattleWinInfoRespMessage();
	public String getOriginDefenderUnionId(Integer unionBattleId);
	public UnionBattleMapBuffParam getunionBattleMapParam(Integer unionBattleId);
	public Result renameMap(RoleInstance role, byte mapIndex, String newMapName);
	public String getNewMapNameByMapIndex(byte mapIndex);
	public String getCapitalName();

}