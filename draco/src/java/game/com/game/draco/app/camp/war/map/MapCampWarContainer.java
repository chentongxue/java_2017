package com.game.draco.app.camp.war.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapContainer;
import sacred.alliance.magic.vo.MapCopyContainer;
import sacred.alliance.magic.vo.MapInstance;

import com.game.draco.GameContext;
import com.game.draco.app.camp.war.vo.ApplyInfo;
import com.game.draco.app.camp.war.vo.MatchInfo;


public class MapCampWarContainer extends MapCopyContainer {
	private final static Logger logger = LoggerFactory.getLogger(MapCampWarContainer.class);
	private final static String PRE_FIX = "campwar_" ;
	private MapInstance mapInstance;
	private MatchInfo match;

	@Override
	protected String getNamePrefix(){
		return PRE_FIX ;
	}
	
	public MapCampWarContainer(Map map,MatchInfo match) {
		super();
		this.match = match;
		//下面语句很重要,不要遗忘
		this.match.setContainerId(this.getInstanceId());
	}

	public static MapContainer getMapContainer(AbstractRole role,String mapId) {
		try {
			if (role == null) {
				return null;
			}
			// 获取此队伍中地图容器ID 
			ApplyInfo info = GameContext.getCampWarApp().getApplyInfo(role.getRoleId());
			MatchInfo match = info.getMatch();
			String containerId = match.getContainerId();
			MapContainer mapContainer = null ;
			if(!Util.isEmpty(containerId)){
				 //因为ArenaMatch的containerID是在创建MapArenaContainer时候赋值的
				 mapContainer = getMapApp().getCopyContainerMap().get(containerId);
			}
			if(null == mapContainer){
				synchronized(match){
					//containerID需要重新设置值,因为ArenaMatch的containerID是在创建MapArenaContainer时候赋值的
					containerId = match.getContainerId();
					if(!Util.isEmpty(containerId)){
						 mapContainer = getMapApp().getCopyContainerMap().get(containerId);
					}
					if(null != mapContainer){
						return mapContainer ;
					}
					//创建
					MapCopyContainer mc = new MapCampWarContainer(getMapApp().getMap(mapId),match);
					getMapApp().addCopyContainer(mc);
					mapContainer = mc ;
				}
			}
			return mapContainer;
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
		
	}
	
	
	private static MapApp getMapApp(){
		return GameContext.getMapApp();
	}

	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		if (null != mapInstance) {
			return mapInstance;
		}
		synchronized (this) {
			if (null != mapInstance) {
				return mapInstance;
			}
			MapInstance mapInstance = this.mapInstanceFactory(map, match);
			mapInstance.initNpc(true);
			mapInstance.setMapContainer(this);
			addMapInstance(mapInstance);
			GameContext.getMapApp().addMapInstance(mapInstance);
			this.mapInstance = mapInstance ;
			return mapInstance;
		}
	}
	
	private MapInstance mapInstanceFactory(Map map, MatchInfo match){
		return new MapCampWarInstance(map,match);
	}
	
	
	@Override
	public boolean canDestroy() {
		return mapInstance.canDestroy();
	}
	
	@Override
	public void destroy(){
		super.destroy();
		this.mapInstance = null ;
		//导致有时候无法取消的问题就是没有下面语句?
		if(null != match){
			//match.cancelAll();
		}
		
	}

	public MatchInfo getMatch() {
		return match;
	}

	public void setMatch(MatchInfo match) {
		this.match = match;
	}

	
}
