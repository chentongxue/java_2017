package com.game.draco.app.asyncarena.vo;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.Result;

import com.game.draco.message.item.AsyncArenaTargetItem;

public @Data class RoleAsyncRefResult extends Result {
	private int price;
	//对手数据
	private List<AsyncArenaTargetItem> targetItem;
}
