package sacred.alliance.magic.app.map.logic;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

public class MapLogicAdaptor extends MapLogic{
	protected MapConfig mapConfig;
	public MapConfig getMapConfig() {
		return mapConfig;
	}

	public void setMapConfig(MapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}
	public MapLogicAdaptor(String mapId) {
		super(mapId);
		this.setMapConfig(GameContext.getMapApp().getMapConfig(mapId));
	}

	@Override
	public void enter(AbstractRole role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit(AbstractRole role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill(AbstractRole killer, AbstractRole victim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int maxPlayer() {
		return Integer.MAX_VALUE ;
	}

	@Override
	public boolean isStore() {
		return false;
	}

	@Override
	public Point rebornPoint(AbstractRole role) {
		return GameContext.getRoleRebornApp().getRebornPointDetail(mapId, role).createPoint();
	}

	/*@Override
	public boolean isIncrUserCopyTimes() {
		return false;
	}*/

/*	@Override
	public int indexOfCopyCluster() {
		return 0;
	}*/

}
