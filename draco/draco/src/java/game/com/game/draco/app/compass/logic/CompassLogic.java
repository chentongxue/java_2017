package com.game.draco.app.compass.logic;

import java.util.List;

import com.game.draco.app.compass.domain.Compass;
import com.game.draco.app.compass.domain.CompassAward;
import com.game.draco.app.compass.domain.CompassRoleAward;

import sacred.alliance.magic.util.Util;

public abstract class CompassLogic {
	
	public abstract List<CompassRoleAward> getAwardList(Compass compass,int count) ;
	
	protected int randomAwardIndex(Compass compass){
		Integer key = Util.getWeightCalct(compass.getPlaceMap());
		if(null == key){
			//重置概率
			compass.resetOdds();
			//重新抽奖
			key = Util.getWeightCalct(compass.getPlaceMap());
		}
		if(null == key){
			return -1 ;
		}
		return key.intValue() ;
	}
	
	protected CompassRoleAward getCompassRoleAward(Compass compass){
		int index = this.randomAwardIndex(compass);
		if(index < 0){
			return null ;
		}
		CompassAward award = compass.getAwardList().get(index);
		if(null == award){
			return null ;
		}
		return this.buildCompassRoleAward(compass, award,index);
	}
	
	protected CompassRoleAward buildCompassRoleAward(Compass compass,
			CompassAward award,int index){
		CompassRoleAward roleAward = new CompassRoleAward();
		roleAward.setId(compass.getId());//罗盘ＩＤ
		roleAward.setPlace((byte)index);//抽奖结果，从0开始
		roleAward.setGoodsId(award.getAward());
		roleAward.setGoodsNum(award.getNum());
		roleAward.setBindType(award.getBindType());
		roleAward.setBroadcastInfo(award.getBroadcastInfo());
		return roleAward;
	}
	
	
}
