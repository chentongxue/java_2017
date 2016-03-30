package sacred.alliance.magic.vo.map;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapDefaultContainer;
import sacred.alliance.magic.vo.MapInstance;

import com.game.draco.GameContext;

public class MapRoleBornGuideContainer extends MapDefaultContainer{
	@Override
	public boolean canDestroy() {
		return false;
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		MapInstance instance = new MapRoleBornGuideInstance(map);
		instance.initNpc(true);
		GameContext.getMapApp().addMapInstance(instance);
		return instance;
	}
}
