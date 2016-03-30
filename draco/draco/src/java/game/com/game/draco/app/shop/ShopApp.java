package com.game.draco.app.shop;

import java.util.List;

import com.game.draco.app.shop.domain.ShopGoods;
import com.game.draco.app.shop.type.ShopMoneyType;
import com.game.draco.app.shop.type.ShopShowType;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface ShopApp extends Service {
	
	/**
	 * 重新加载商城配置表
	 */
	public Result reLoad();
	
	/**
	 * 分类获取商品列表
	 * @param showType 页签类型
	 * @return
	 */
	public List<ShopGoods> getShopGoodsList(ShopShowType showType);
	
	/**
	 * 获取商城物品
	 * @param goodsId 物品ID
	 * @return
	 */
	public ShopGoods getShopGoods(int goodsId);
	
	/**
	 * 购买商城物品
	 * @param role 角色对象
	 * @param goodsId 物品ID
	 * @param goodsNum 物品数量
	 * @param moneyType 货币类型
	 * @param isOneKey 是否是一键操作中的购买，需要去掉canSell()判断
	 * @return
	 */
	public Result shopping(RoleInstance role, int goodsId, short goodsNum, ShopMoneyType moneyType, boolean isOneKey);
	
}
