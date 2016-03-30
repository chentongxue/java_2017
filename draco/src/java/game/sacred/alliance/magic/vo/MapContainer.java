package sacred.alliance.magic.vo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MapContainer<T>{
	
	private static final String NULL_STRING = "" ;
	protected static AtomicInteger instanceIdGenerator = new AtomicInteger();
	private AtomicBoolean inQueue = new AtomicBoolean(false);
	protected String instanceId;
	//模板ID，实例
	protected Map<String,MapInstance> subMapList = new ConcurrentHashMap<String,MapInstance>();
	
	protected String getNamePrefix(){
		return NULL_STRING ;
	}
	
	public MapContainer(){
		this.instanceId =  this.getNamePrefix() + instanceIdGenerator.incrementAndGet();
	}
	
	public MapContainer(String instanceId){
		this.instanceId = instanceId;
		if(this.instanceId==null || this.instanceId.length()<=0){
			this.instanceId = this.getNamePrefix()+ instanceIdGenerator.incrementAndGet();
		}
	}
	
	
	public MapInstance getMapInstance(String mapId){
		return subMapList.get(mapId);
	}
	
	/**
	 * 地图容器的主循环
	 * 每秒会执行一次
	 */
	public abstract void update();
	
	protected void addMapInstance(MapInstance mapInstance){
		subMapList.put(mapInstance.getMap().getMapId(), mapInstance);
		mapInstance.setMapContainer(this);
	}
	
	public abstract boolean canDestroy() ;
	
	public abstract void destroy();

	public abstract MapInstance createMapInstance(sacred.alliance.magic.app.map.Map map, AbstractRole role);
	
	
	public String getInstanceId() {
		return instanceId;
	}

	public AtomicBoolean getInQueue() {
		return inQueue;
	}
	
	
}
