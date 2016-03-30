package sacred.alliance.magic.vo;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;

public class MapLineInstance extends MapInstance {
	private long lastOutTime = System.currentTimeMillis();
	private int destoryTimeInterval = 0;
	private LineMapStatus lineMapStatus = LineMapStatus.Init; //分线地图状态
	
	public MapLineInstance(Map map, int lineId){
		super(map,lineId);
		MapConfig mapConfig = map.getMapConfig();
		destoryTimeInterval = mapConfig.getLifeCycle() * 1000;
	}
	
	protected boolean isMapLineContainer(){
		return (null != this.getMapContainer()) 
		&& (this.getMapContainer() instanceof MapLineContainer) ;
	}
	
	protected void destoryBaseLogic(){
		super.destroy() ;
	}
	
	@Override
	public boolean canDestroy() {
		if(!this.isMapLineContainer()){
			return false ;
		}
		if(!this.hasPlayer()
				&& (System.currentTimeMillis() - lastOutTime) > destoryTimeInterval){
			this.changeMapLineStatus(LineMapStatus.Destroy);
			return true;
		}
		return false;
	}
	
	@Override
	public void destroy(){
		if(!this.isMapLineContainer()){
			this.destoryBaseLogic();
			return ;
		}
		if(this.lineMapStatus != LineMapStatus.Destroy){
			return ;
		}
		super.destroy();
		MapLineContainer container = (MapLineContainer)this.getMapContainer();
		container.getLineInstanceMap().remove(this.lineId);
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		if(!this.isMapLineContainer()){
			return true ;
		}
		if(this.lineMapStatus == LineMapStatus.Destroy){
			return false;
		}
		return true;
	}

	@Override
	protected String createInstanceId(){
		instanceId = "line_"+instanceIdGenerator.incrementAndGet();
		return instanceId;
	}

	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		lastOutTime = System.currentTimeMillis();
	}

	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
//		this.notifyNpcAi(victim);
	}

	@Override
	public void useGoods(int goodsId) {
		
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		
	}

	public boolean changeMapLineStatus(LineMapStatus lineMapStatus){
		synchronized (this){
			if(this.lineMapStatus == LineMapStatus.Destroy){
				return false;
			}
			this.lineMapStatus = lineMapStatus;
			return true;
		}
	}
}

enum LineMapStatus{
	Init, //初始
	Assign, //分配
	Destroy, //销毁
}
