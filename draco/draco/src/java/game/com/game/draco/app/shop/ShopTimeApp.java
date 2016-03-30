package com.game.draco.app.shop;

import java.util.Collection;

import com.game.draco.message.item.ShopTimeItem;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface ShopTimeApp extends Service {
	
	/**
	 * 获取结束时间
	 * @return
	 */
	public int getOverTime();
	
	/**
	 * 获取正在卖的商品
	 * @return
	 */
	public Collection<ShopTimeItem> getSellGoodsList();
	
	/**
	 * 购买商品
	 * @param role
	 * @param goodsId
	 * @param num
	 * @return
	 */
	public Result buyGoods(RoleInstance role, int goodsId, int num);
	
}
