package sacred.alliance.magic.app.summon;

import sacred.alliance.magic.app.summon.vo.SummonResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;

public interface SummonApp extends NpcFunctionSupport, Service, AppSupport {
	/**
	 * 
	 * @param role
	 * @param summon
	 * @param popIgnore 是否开启 金钱不足弹板
	 * @return
	 * @date 2014-9-15 下午08:53:48
	 */
	public SummonResult canSummon(RoleInstance role, Summon summon, boolean popAttrDialog);
	
	public void offlineLog(RoleInstance role);
	
	public Result summon(RoleInstance role, Summon item);
	
	public void summonDeath(NpcInstance dieNpc, RoleInstance role);
	
	public Summon getSummonById(int summonId);
	
	//public void loadCampSummon(Camp camp);
	
	public void resetSummonByCopyId(RoleInstance role, short copyId);
}
