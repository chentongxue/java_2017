package sacred.alliance.magic.scheduler.job;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

import com.game.draco.GameContext;

import sacred.alliance.magic.actor.AbstractActor;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.MapInstance;

public class MapLoopActor extends AbstractActor<MapInstance> {
	//private LoopCount loopCount = new LoopCount(LoopConstant.MAP_INSTANCE_DESTRPY_CYCLE) ;
	public MapLoopActor(Channel<MapInstance> inChannel,Fiber fiber){
		this.inChannel = inChannel;
		this.fiber = fiber ;
	}
	
	@Override
	public void action(MapInstance instance) {
		if (null == instance) {
			return;
		}
		try {
			if ((instance.mustRunMapLoop() || instance.hasPlayer()) && !instance.isUpdating()) {
				instance.getWorldTime().reset();
				try {
					if (Log4jManager.LOOP_LOG.isInfoEnabled()) {
						long start = System.currentTimeMillis();
						instance.update();
						long time = System.currentTimeMillis() - start;
						if (time > GameContext.getMapLoop().getLoopSleepMillis()) {
							Log4jManager.LOOP_LOG.info(time + "#" + instance.getMap().getMapId());
						}
					} else {
						instance.update();
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
		} finally {
			instance.getInQueue().set(false);
		}
	}

	
}
