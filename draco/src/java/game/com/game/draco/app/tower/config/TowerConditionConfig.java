package com.game.draco.app.tower.config;

import com.game.draco.app.tower.type.TowerStarConditionType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import lombok.Data;

public @Data class TowerConditionConfig implements KeySupport<Integer>{
	
	private int starConditonId;
	private byte starConditonType;
	private int data;
	private String desc;

	public void init(){
       if(null == TowerStarConditionType.getType(this.starConditonType) ){
           this.checkFail("TowerConditionConfig config error,starConditonType not exist,starConditonId="
                   + starConditonId + " starConditonType=" + starConditonType);
       }
	}
	
	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}

	@Override
	public Integer getKey() {
		return starConditonId;
	}
}
