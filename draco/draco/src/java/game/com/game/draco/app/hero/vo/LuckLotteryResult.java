package com.game.draco.app.hero.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsBase;

import com.game.draco.app.hero.config.HeroLuckGoods;
import com.game.draco.message.item.HeroLuckPanelItem;

public @Data class LuckLotteryResult extends Result{

	/**
	 * 是否免费
	 */
	private boolean free ;
	private HeroLuckGoods goods ;
	private GoodsBase goodsBase ;
	private HeroLuckPanelItem luckItem ;
}
