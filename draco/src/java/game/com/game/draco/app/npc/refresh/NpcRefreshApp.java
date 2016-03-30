package com.game.draco.app.npc.refresh;

import java.util.List;
import java.util.Map;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.response.C0611_BossListRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public interface NpcRefreshApp extends Service{
	public void installMapNpcRefreshConfig(MapInstance instance);
	public void mapDestroyRefreshProcess(MapInstance mapInstance);
	public void npcDeathRefreshPross(NpcInstance npcInstance, AbstractRole owner);
	public List<NpcRefreshConfig> getBossRefreshConfigList();
	public NpcRefreshConfig getBossRefreshConfig(short id);
	public BossLoot getBossLoot(String lootId) ;
	public NpcRefreshTask getCurrentRefreshTask(NpcRefreshConfig config);
	public C0611_BossListRespMessage getBossListRespMessage(RoleInstance role);
	/**
	 * 进入BOSS战地图
	 */
	public Result enterBossMap(RoleInstance role, short id);

	public Map<String, List<NpcRefreshTask>> getNpcRefreshTaskMap() ;
	
}
