package sacred.alliance.magic.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.map.MapApp;

public abstract class MapCopyContainer extends MapContainer<MapCopyInstance> {
	private static final Logger logger = LoggerFactory.getLogger(MapCopyContainer.class);
	private static final String PRE_FIX = "cpc_" ;
	private static MapApp mapApp = GameContext.getMapApp();
	
	@Override
	protected String getNamePrefix(){
		return PRE_FIX ;
	}
	
	public MapCopyContainer(){
		super();
	}
	
	public MapCopyContainer(String instanceId){
		super(instanceId);
	}
	
	@Override
	public boolean canDestroy() {
		if(null != subMapList.values() && 0 < subMapList.values().size()) {
			for(MapInstance item : subMapList.values()){
				try{
					if(!item.canDestroy()){
						return false ;
					}
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
		}
		return true ;
	}

	public void update(){
		
	}
	
	@Override
	public void destroy() {
		for(MapInstance item : subMapList.values()){
			try{
				//遍历的时候不能remove
				//this.subMapList.remove(item.getInstanceId());
				item.destroy();
			}catch(Exception ex){
				logger.error("MapInstance destroy error",ex);
			}
		}
		subMapList.clear();
		mapApp.removeCopyContainer(instanceId);
		
	}


	public java.util.Map<String, MapInstance> getSubMapList() {
		return subMapList;
	}

	public void setSubMapList(java.util.Map<String, MapInstance> subMapList) {
		this.subMapList = subMapList;
	}


}


