package sacred.alliance.magic.app.arena.top;

import java.util.List;

import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public interface ArenaTopApp extends NpcFunctionSupport{

	public Point safePoint() ;
	public List<Short> getEnterMapBuffList() ;
	
	public TopMapConfig getTopMapConfig() ;
	
	public Active getActive() ;
	
	public void sendReward(String roleId,String roleName,int level,int rank,int num);
	
	public void resetArenaTopScore(RoleArena roleArena );
	
	public Result canJoin(RoleInstance role) ;
	
	public void cleanRacers() ;
	
	public void racersMailAlert() ;
}
