package com.game.draco.app.shopsecret;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.app.shopsecret.domain.RoleSecretShop;

public interface ShopSecretApp extends Service, NpcFunctionSupport, AppSupport{
	
	public Message openShopSecretEnterRespMessage(RoleInstance role, String shopId);

	public Result refreshRoleShopSecret(RoleInstance role, String shopId, byte confirm);

	/**
	 * 更新数据库
	 * @param roleSecretShop
	 * @date 2014-8-13 下午03:15:15
	 */
	public void saveOrUpdRoleShopSecret(RoleSecretShop roleSecretShop);

	public RoleSecretShop selectRoleSecretShopFromDB(String roleId, String shopId);

	public Result buy(RoleInstance role, String shopId, int id);
	
	/**
	 * 重新加载商城配置表
	 * 热加载
	 */
	public Result reLoad();
}
