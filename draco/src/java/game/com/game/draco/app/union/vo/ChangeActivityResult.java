package com.game.draco.app.union.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class ChangeActivityResult extends Result {
	//下次所需钻石
	private int gem;
	 //下次所需人气
    private int popularity;
    
    //活动状态
    private byte state;
}
