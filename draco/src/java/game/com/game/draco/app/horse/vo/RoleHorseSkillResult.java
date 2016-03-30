package com.game.draco.app.horse.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class RoleHorseSkillResult extends Result {
	 //坐骑ID
    private int horseId;
    
    //技能ID
    private short skillId;
    
    //幸运值
    private int luck;
    
    //是否升级
    private boolean flag;
}
