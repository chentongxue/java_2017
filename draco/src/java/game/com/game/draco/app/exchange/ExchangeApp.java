package com.game.draco.app.exchange;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.exchange.domain.ExchangeMenu;
import com.game.draco.app.exchange.trade.TradeFuctionConfig;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.message.item.ExchangeChildItem;

public interface ExchangeApp extends NpcFunctionSupport,Service, AppSupport {
	
	/**
	 * 判断物品能否兑换，返回相应信息
	 * @param role
	 * @param item
	 * @return
	 */
	public GoodsResult exchange(RoleInstance role, int id, short num, byte enterType, byte confirm);

	/**
	 * 入库失败日志
	 * @param role
	 */
	public void offlineLog(RoleInstance role);
	
	/**
	 * 获取兑换
	 * @param menuId
	 * @return
	 */
	public List<ExchangeChildItem> getChildList(RoleInstance role, int menuId);
	
	/**
	 * 获取兑换目录
	 * @return
	 */
	public Map<Integer, ExchangeMenu> getAllMenuMap();
	
	/**
	 * 获取非NPC兑换目录
	 * @return
	 */
	public Map<Integer, ExchangeMenu> getMenuMap();
	
	/**
	 * 获取NPC兑换目录
	 * @return
	 */
	public List<ExchangeMenu> getNpcMenuMap(String npcId);
	
	/**
	 * 根据copyId重置兑换次数
	 * @param role
	 * @param copyId
	 */
	public void resetExchangeByCopyId(RoleInstance role, short copyId);
	
	/**
	 * 获取交易配置
	 * @param interId
	 * @return
	 */
	public TradeFuctionConfig getTradeFuctionConfig(byte interId);
	
	/**
	 * 获取兑换名称
	 * @param exchangeId
	 * @return
	 */
	public String getExchangeName(int exchangeId);

}
