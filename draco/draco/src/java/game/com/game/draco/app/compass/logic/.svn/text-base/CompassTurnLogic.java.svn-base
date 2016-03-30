package com.game.draco.app.compass.logic;

import java.util.List;

import com.game.draco.app.compass.domain.Compass;
import com.game.draco.app.compass.domain.CompassRoleAward;
import com.google.common.collect.Lists;


public class CompassTurnLogic extends CompassLogic{

	@Override
	public List<CompassRoleAward> getAwardList(Compass compass, int count) {
		List<CompassRoleAward> ret = Lists.newArrayList();
		for (int i = 0; i < count; i++) {
			CompassRoleAward award = this.getCompassRoleAward(compass);
			if (null != award) {
				ret.add(award);
			}
		}
		return ret;
	}

}
