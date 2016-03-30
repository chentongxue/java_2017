package com.game.draco.app.compass.config;

import com.game.draco.GameContext;
import lombok.Data;


import sacred.alliance.magic.domain.GoodsBase;

public @Data class CompassAward {
	
	private int award;
	private int num;
	private int odds;
	private byte bindType;
	private String broadcastInfo;
	
	public CompassAward(int award, int num, int odds, byte bindType, String broadcastInfo){
		this.award = award;
		this.num = num;
		this.odds = odds;
		this.bindType = bindType;
		this.broadcastInfo = broadcastInfo;
	}
	

	public GoodsBase getAwardGoods(){
		return GameContext.getGoodsApp().getGoodsBase(this.award);
	}
	
}
