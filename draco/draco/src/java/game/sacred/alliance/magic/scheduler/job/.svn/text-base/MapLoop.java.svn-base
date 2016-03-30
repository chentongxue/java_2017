package sacred.alliance.magic.scheduler.job;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.actor.Actor;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;

public class MapLoop implements Service{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private AtomicBoolean started = new AtomicBoolean(false);
	private Actor updateActor = null;
	private Actor destroyActor = null;
	
	private MapApp mapApp;
	public static long loopSleepMillis = 1000 ;
	private int corePoolSize = 2;
	private int maximumPoolSize = 16;
	private long keepAliveMillisTime = 60 * 1000;
	
	private LoopCount mapDestoryLoopCount = new LoopCount(LoopConstant.MAP_INSTANCE_DESTRPY_CYCLE) ;
	
	public void setMapApp(MapApp mapApp) {
		this.mapApp = mapApp;
	}
	public void setLoopSleepMillis(long loopSleepMillis) {
		MapLoop.loopSleepMillis = loopSleepMillis;
	}
	
	public long getLoopSleepMillis() {
		return MapLoop.loopSleepMillis;
	}
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}
	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}
	public void setKeepAliveMillisTime(long keepAliveMillisTime) {
		this.keepAliveMillisTime = keepAliveMillisTime;
	}
	/**计算是主循环的多少倍*/
	public static int multipleOfMainLoop(long loopMillis){
		return Math.max(1,(int)(loopMillis/loopSleepMillis)) ;
	}
	
	@Override
	public void start(){
		if(started.compareAndSet(false, true)){
			//ExecutorService serv = Executors.newFixedThreadPool(actorThreadPoolSize);
			ExecutorService serv = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
					keepAliveMillisTime, TimeUnit.MILLISECONDS,
					new SynchronousQueue<Runnable>(),
					new ThreadPoolExecutor.CallerRunsPolicy());
	        PoolFiberFactory fact = new PoolFiberFactory(serv);
	        Fiber fiber = fact.create();
	        MemoryChannel<MapInstance> channel = new MemoryChannel<MapInstance>();
	        updateActor = new MapLoopActor(channel,fiber);
	        updateActor.start();
	        
	        Fiber fiber2 = fact.create();
	        MemoryChannel<MapContainer> channel2 = new MemoryChannel<MapContainer>();
	        destroyActor = new MapContainerLoopActor(channel2,fiber2);
	        destroyActor.start();
	        
			Thread task = new Thread(new Task());
			task.setName("MapLoopThread");
			task.start();
		}
	}
	
	@Override
	public void stop(){
		if(started.compareAndSet(true, false)){
			if(null != updateActor){
				updateActor.stop();
			}
			if(null != destroyActor){
				destroyActor.stop();
			}
		}
	}
	
	
	
	private class Task implements Runnable {
		public void run() {
			while (started.get()) {
					try {
						Collection<MapInstance> allMap = mapApp.getAllMapInstance();
						if(null != allMap){
							for(MapInstance instance : allMap){
								if(instance.getInQueue().compareAndSet(false, true)){
									updateActor.publish(instance);
								}
							}
						}
					} catch (Exception ex) {
						logger.error("MapInstance update error",ex);
					}
					if(mapDestoryLoopCount.isReachCycle()){
						try {
							Collection<MapCopyContainer> allCopyContainer 
												= mapApp.getCopyContainerMap().values(); 
							if(null != allCopyContainer){
								for(MapCopyContainer container : allCopyContainer){
									if(container.getInQueue().compareAndSet(false, true)){
										destroyActor.publish(container);
									}
								}
							}
						} catch (Exception ex) {
							logger.error("MapCopyContainer destory error",ex);
						}
						
						try{
							Collection<MapLineContainer> allMapLineContaner = mapApp.getLineContainerMap().values();
							if(null != allMapLineContaner){
								for(MapLineContainer container : allMapLineContaner){
									if(container.getInQueue().compareAndSet(false, true)){
										destroyActor.publish(container);
									}
								}
							}
						}catch(Exception e){
							logger.error("MapLineContainer destory error",e);
						}
					}
					try {
						Thread.sleep(loopSleepMillis);
					} catch (Exception ex) {
						logger.error("",ex);
					}
			}
		}
	}

	@Override
	public void setArgs(Object args) {
		
	}
	
}
