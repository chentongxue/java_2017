package sacred.alliance.magic.app.menu.before;

import sacred.alliance.magic.app.menu.MenuBefore;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuItem;

public class MenuArena3v3DarkDoorBefore extends MenuBefore{

	public MenuArena3v3DarkDoorBefore() {
		super(MenuIdType.arena_3v3_dark_door);
	}
	
	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		if(!GameContext.getArena3V3App().isOpenDarkDoor()){
			return null;
		}
		return super.createMenuItem(role);
	}
}
