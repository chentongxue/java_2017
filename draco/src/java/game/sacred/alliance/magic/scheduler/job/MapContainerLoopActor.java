package sacred.alliance.magic.scheduler.job;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

import sacred.alliance.magic.actor.AbstractActor;
import sacred.alliance.magic.vo.MapContainer;

public class MapContainerLoopActor extends AbstractActor<MapContainer> {
	//private LoopCount loopCount = new LoopCount(LoopConstant.MAP_INSTANCE_DESTRPY_CYCLE) ;
	public MapContainerLoopActor(Channel<MapContainer> inChannel,Fiber fiber){
		this.inChannel = inChannel;
	    this.fiber = fiber;
	}

	@Override
	public void action(MapContainer container) {
		if (null == container) {
			return;
		}
		try {
			container.update();
			if (container.canDestroy()) {
				container.destroy();
			}
		} finally {
			container.getInQueue().set(false);
		}

	}

}
