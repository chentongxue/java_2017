package com.game.draco.app.talent.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;

import com.game.draco.app.talent.domain.RoleTalent;

public @Data class RoleTrainTalentResult extends Result {
	
	private RoleTalent temp;
    
}
