package com.game.draco.app.title;

import java.util.List;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.GoodsBaseInitCallback;
import sacred.alliance.magic.domain.GoodsBase;

public class TitleInitCallback implements GoodsBaseInitCallback{

	@Override
	public void callback(List<? extends GoodsBase> goodsBaseList) {
		GameContext.getTitleApp().initTitleCategory(goodsBaseList);
	}

}
