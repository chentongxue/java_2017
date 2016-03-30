package com.game.draco.app.operate.firstpay;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hint.HintSupport;
import com.game.draco.app.operate.firstpay.config.FirstPayBaseConfig;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;

public interface FirstPayApp extends Service, AppSupport, HintSupport {
	
	/**
	 * 充值
	 * @param role
	 * @param rmbMoneyValue
	 */
	public void onPay(RoleInstance role, int rmbMoneyValue);
	
	/**
	 * 首冲状态
	 * @param role
	 * @return
	 */
	public byte getRoleFirstPayStatus(RoleInstance role);
	
	/**
	 * 获得首次奖励列表
	 * @return
	 */
	public List<GoodsLiteNamedItem> getFirstPayGift();
	
	/**
	 * 首冲奖励属性列表
	 * @return
	 */
	public List<AttriTypeValueItem> getFirstPayAttriList();
	
	/**
	 * 领取首冲礼包（包括重置）
	 * @param role
	 * @return
	 */
	public Result receiveAwards(RoleInstance role);
	
	/**
	 * 是否显示在动态菜单
	 * @param role
	 * @return
	 */
	public boolean isShowMenu(RoleInstance role);
	
	/**
	 * 获得首冲的基本配置
	 * @return
	 */
	public FirstPayBaseConfig getFirstPayBaseConfig();
	
	/**
	 * 热加载接口
	 * @return
	 */
	public Result reLoad();

}
