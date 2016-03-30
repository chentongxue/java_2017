package com.game.draco.app.richman.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.map.MapRichManInstance;

public @Data class RichManCheckResult extends Result {
	MapRichManInstance mapInstance;
	RichManRoleStat roleStat;
}
