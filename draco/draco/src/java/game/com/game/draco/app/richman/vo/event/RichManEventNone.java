package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.app.richman.vo.RichManRoleBehavior;

public class RichManEventNone extends RichManEventLogic {
	
	private static RichManEventNone instance = new RichManEventNone();
	
	private RichManEventNone() {
		
	}
	
	public static RichManEventNone getInstance() {
		return instance;
	}

	@Override
	public void execute(MapRichManInstance mapInstance, 
			RoleInstance role, RichManRoleBehavior behavior) {
		
	}

}
