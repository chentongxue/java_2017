package com.game.draco.app.vip;

import java.util.List;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.message.item.ShopVipGiftItem;

/**
 * 
 */
public interface VipApp extends Service, AppSupport {
	
	/**
	 * vip role increase vipExp directly
	 */
	public void addVipLevelExp(int roleId, int expAddValue);

	// public void addDiamands(RoleInstance role,int daimansAddValue);
	/**
	 * offline roles recharge ,handle msg queue
	 * 
	 * @param roleId
	 * @param daimans
	 * @date 2014-5-4 下午01:39:53
	 */
	public void addDiamands(int roleId, int daimans);

	/**
	 * messge 2510
	 * 
	 * @param role
	 */
	public Message openVipPanel(RoleInstance role);

	/**
	 * messge 2511
	 * 
	 * @param role
	 * @param vipLevel
	 */
	public Message vipGalleryShift(RoleInstance role, byte vipLevel);

	/**
	 * msg 2513
	 * 
	 * @param role
	 * @param vipLevel
	 */
	public Message receiveVipLevelUpAward(RoleInstance role, byte vipLevel);

	public byte getVipLevel(String roleId);

	public byte getVipLevel(RoleInstance role);
	
	public int getVipPrivilegeTimes(String roleId, byte privilegeType,String param);

	public String getNextVipLevelPrivilegeInfo(RoleInstance role, int vipPriType,
			String param);

	public String getVipLevelPrivilegeInfo(RoleInstance role, int vipPriType,
			String param);

	public String getNextVipLevelPrivilegeInfo(byte vipLevel, int vipPriType,
			String param);
	
	/**
	 * 获得某个特权的vip开启等级
	 * @param vipPriType
	 * @return
	 */
	public int getOpenVipLevel(int vipPriType,String param) ;
	/**
	 * 点击VIP功能按钮
	 * @param role
	 * @param functionId
	 * @return
	 * @date 2014-9-2 下午04:59:28
	 */
	public Message callVipFunction(RoleInstance role, String functionId, byte confirm);
	
	public int getVipPrivilegeTimes(int vipLevel, int privilegeType,String param);


	int getRoleVipExp(RoleInstance role);

	int getVipExp4VipLevelUp(RoleInstance role);

	/**
	 * vip  商城礼包
	 * @param role
	 * @date 2014-9-27 下午04:58:16
	 */
	public List<ShopVipGiftItem> getShopVipGiftItems(RoleInstance role);
	public ShopVipGiftItem getShopVipGiftItemsByVipLevel(RoleInstance role, byte vipLevel);
	public Message receiveShopVipGift(RoleInstance role, byte vipLevel);
	public Message vipShopGiftDispaly(RoleInstance role);
	
	/**
	 * 是否到达最高VIP等级
	 * @param role
	 * @return
	 */
	public boolean isFullVipLevel(RoleInstance role);

	/**
	 * 【测试】期间，随着VIP升级来测试，VIP升级的提示和特效
	 */
	public void onRoleLevelUp(RoleInstance role);

	public Message getVipInfo(RoleInstance role);
}
