package com.game.draco.app.shop;

import java.util.List;

import com.game.draco.app.AppSupport;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.shop.config.ShopGoodsConfig;

public interface ShopApp extends Service,AppSupport {
	
	/**
	 * 重新加载商城配置表
	 * 热加载
	 */
	public Result reLoad();
	
	/**
	 * 分类获取商品列表
	 * @param showType 页签类型
	 * @return
	 */
	public List<ShopGoodsConfig> getShopGoodsList();
	
	/**
	 * 获取商城物品
	 * @param goodsId 物品ID
	 * @return
	 */
	public ShopGoodsConfig getShopGoods(int goodsId);
	
	/**
	 * 购买商城物品
	 * @param role 角色对象
	 * @param goodsId 物品ID
	 * @param goodsNum 物品数量
	 * @param moneyType 货币类型
	 * @param isOneKey 是否是一键操作中的购买，需要去掉canSell()判断
	 * @return
	 */
	public Result shopping(RoleInstance role,byte moneyType, int goodsId, short goodsNum,  boolean isOneKey, byte confrim);

	public Message openShop(RoleInstance role);

	/**
	 * 打开普通商店
	 */
	public Message getGoodsList(RoleInstance role);

}
