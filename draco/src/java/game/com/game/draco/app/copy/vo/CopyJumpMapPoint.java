package com.game.draco.app.copy.vo;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

public class CopyJumpMapPoint extends JumpMapPoint {
	
	@Override
	public void trigger(AbstractRole role) throws ServiceException {
		Point point = role.getCopyBeforePoint();
		if (point == null) {
			super.trigger(role);
			return;
		}
		GameContext.getUserMapApp().changeMap(role, point);
	}
	
}
