package sacred.alliance.magic.vo.map;

import com.game.draco.GameContext;

import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapDefaultContainer;
import sacred.alliance.magic.vo.MapInstance;

public class MapCopyLineContainer extends MapDefaultContainer {
	
	@Override
	public MapInstance createMapInstance(sacred.alliance.magic.app.map.Map map, AbstractRole role) {
		MapCopyLineInstance instance = new MapCopyLineInstance(map);
		instance.initNpc(true);
		GameContext.getMapApp().addMapInstance(instance);
		return instance;
	}
	
	@Override
	public boolean canDestroy() {
		return false;
	}
	
	@Override
	public void destroy() {
		
	}
	
}
