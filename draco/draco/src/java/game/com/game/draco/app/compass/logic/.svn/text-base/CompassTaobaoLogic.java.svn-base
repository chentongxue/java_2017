package com.game.draco.app.compass.logic;

import java.util.List;

import com.game.draco.app.compass.domain.Compass;
import com.game.draco.app.compass.domain.CompassAward;
import com.game.draco.app.compass.domain.CompassRoleAward;
import com.google.common.collect.Lists;


public class CompassTaobaoLogic extends CompassLogic{

	@Override
	public List<CompassRoleAward> getAwardList(Compass compass,int count) {
		List<CompassRoleAward> ret = Lists.newArrayList() ;
		synchronized (compass){
			for (int i = 0; i < count; i++) {
				CompassRoleAward award = this.getCompassRoleAward(compass);
				if(null != award){
					ret.add(award);
				}
			}
		}
		return ret ;
	}
	

	
	@Override
	protected int randomAwardIndex(Compass compass){
		int index = super.randomAwardIndex(compass);
		if(index < 0){
			return -1 ;
		}
		CompassAward award = compass.getAwardList().get(index);
		if(null == award){
			return -1;
		}
		//减权重
		int odds = award.getOdds() - 1;
		if(odds < 0){
			odds = 0;
		}
		award.setOdds(odds);
		compass.getPlaceMap().put(index, odds);
		return index ;
	}
}
