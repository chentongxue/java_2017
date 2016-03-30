package sacred.alliance.magic.app.ai;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.app.ai.config.NormalAiConfig;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.npc.domain.NpcInstance;

public abstract class AiApp implements Service{

	static HashMap<String, Ai> aiMap = new HashMap<String, Ai>();
	
	public abstract Ai getAi(AbstractRole role);
	
	public static void registerAi(Ai ai){
		if(null == ai){
			return ;
		}
		aiMap.put(ai.getAiId(), ai);
	}
	
	public abstract Map<String, NormalAiConfig> getNormalAiConfigMap();

    public abstract State<NpcInstance> getNpcDefaultState(NpcInstance instance);
    
    public abstract void deathRefresh(int groupId, AbstractRole role);
    
    public abstract void bossRefresh(int ruleId, AbstractRole role);
    
    public abstract Ai getAsyncPvpAi(AbstractRole role, AsyncPvpRoleAttr npc);
    
    public abstract Map<Integer,Float> getAutoMaxHpConfig(String npcId) ;
    
    public abstract boolean isAutoMapHpNpc(String npcId) ;

	public abstract void bossSpilt(AbstractRole npc,BossAction action);
	
	public abstract void bossSpiltDefinition(AbstractRole sprite,BossAction action);
	
	public abstract void bossSpiltBlock(AbstractRole sprite,BossAction action);
	
}
