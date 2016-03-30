package com.game.draco.app.horse.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class RoleHorseLevelUpResult extends Result {
	 //坐骑ID
    private int horseId;
    //坐骑升星数量
    private int starNum;
    
}
