package sacred.alliance.magic.app.pk;

import java.util.List;

import com.game.draco.app.AppSupport;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.message.item.DeathNotifySelfItem;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public interface PkApp extends Service, NpcFunctionSupport, AppSupport{
	
	public void killPlayer(AbstractRole attacker, RoleInstance victim, List<DeathNotifySelfItem> optionList);
	
	public Result changePkStatus(RoleInstance role, byte pkStatus);
	
	public PkConfig getPkConfig();
	
	public void clearKillNum(AbstractRole player);
	
	public PkKillConfig getPkKillConfig(int killNum);
	
	public void changeColorByRemove(RoleInstance role);
	
	public void internalAttackerLogic(RoleInstance attacker, RoleInstance victim);
}
