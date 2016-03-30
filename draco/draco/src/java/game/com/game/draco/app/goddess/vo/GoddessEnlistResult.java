package com.game.draco.app.goddess.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsGoddess;

public @Data class GoddessEnlistResult extends Result {
	private GoodsGoddess goddessTemplate;
	private int goodsId;
	private short goodsNum;
}
