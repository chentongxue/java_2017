package sacred.alliance.magic.app.ai.state;
import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.ai.State;
import sacred.alliance.magic.scheduler.job.LoopCount;
public abstract class NpcState implements State<NpcInstance>{
	private LoopCount loopCount ;
	public void setLoopCount(LoopCount loopCount) {
		this.loopCount = loopCount;
	}

	@Override
	public final void execute(NpcInstance entity) {
		if(null != loopCount && !loopCount.isReachCycle()){
			//森奀潔珨隅腕樓奻
			loopCount.addTimeDiff(entity.getMapInstance().getWorldTime().getTimeDiff());
			return ;
		}
		this.doExecute(entity);
	}

	public long getTimeDiff(NpcInstance entity){
		long value = entity.getMapInstance().getWorldTime().getTimeDiff();
		if(null == loopCount){
			return value ; 
		}
		value += loopCount.getTimeDiff();
		loopCount.resetTimeDiff();
			
		return value ;
	}
	public abstract void doExecute(NpcInstance entity) ;
}
