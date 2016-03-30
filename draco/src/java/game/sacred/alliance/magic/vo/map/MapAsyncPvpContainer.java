package sacred.alliance.magic.vo.map;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapDefaultContainer;
import sacred.alliance.magic.vo.MapInstance;

/**
 * 天梯地图容器
 */
public class MapAsyncPvpContainer extends MapDefaultContainer {
	@Override
	public boolean canDestroy() {
		return false;
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public MapInstance createMapInstance(Map map, AbstractRole role) {
		MapInstance instance = null;
		MapLogicType logicType = map.getMapConfig().getMapLogicType();
		switch(logicType) {
		case pet:
			instance = new MapPetInstance(map);
			break;
		case asyncArena:
			instance = new MapAsyncArenaInstance(map);
			break;
		case heroArena:
			instance = new MapHeroArenaInstance(map);
			break;
		case qualify:
			instance = new MapQualifyInstance(map);
			break;
		}
		instance.initNpc(true);
		GameContext.getMapApp().addMapInstance(instance);
		return instance;
	}
}
