package com.game.draco.app.shop;

import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.message.response.C1618_ShopSecretRespMessage;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface ShopSecretApp extends Service, NpcFunctionSupport{
	/**
	 * 登陆加载
	 * @param role
	 */
	public void login(RoleInstance role);
	
	/**
	 * 下线
	 * @param role
	 */
	public void offlineRole(RoleInstance role);
	
	/**
	 * 元宝刷新
	 * @param role
	 * @return
	 */
	public Result roleRefresh(RoleInstance role);
	
	/**
	 * 购买物品
	 * @param role
	 * @param id
	 * @return
	 */
	public GoodsResult buy(RoleInstance role, int id);
	
	/**
	 * 获取面板信息
	 * @param role
	 * @return
	 */
	public C1618_ShopSecretRespMessage getShopSecretRespMessage(RoleInstance role);
	
	/**
	 * 下线异常日志
	 * @param role
	 */
	public void offlineLog(RoleInstance role);
}
