package sacred.alliance.magic.app.summon;

import java.util.Map;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.SummonDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public interface SummonApp extends NpcFunctionSupport, Service {
	
	public Status canSummon(RoleInstance role, Summon summon);
	
	public void offlineLog(RoleInstance role);
	
	public Result summon(RoleInstance role, Summon item);
	
	public void summonDeath(NpcInstance dieNpc, RoleInstance role);
	
	public void loadRoleSummon(RoleInstance role);
	
//	public void loadFactionSummon(Faction faction);
	
	public void saveSummon(Map<Integer, SummonDbInfo> summonDbInfoMap);
	
	public Summon getSummonById(int summonId);
	
	//public void loadCampSummon(Camp camp);
	
	public void resetSummonByCopyId(RoleInstance role, short copyId);
}
