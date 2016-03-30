package com.game.draco.app.copy.vo;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.Result;

import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;

public @Data class CopyRaidsResult extends Result{
	
	private byte copyRemCount;
	private byte typeRemCount;
	private List<GoodsLiteItem> goodsLiteList;
	private List<AttriTypeValueItem> attriItemList;
	
}
