package com.game.draco.app.vip;

import com.game.draco.app.vip.vo.VipLevelUpResult;

import sacred.alliance.magic.app.hint.HintSupport;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 
 */
public interface VipApp extends Service, HintSupport {
	
	
	/**
	 * role login
	 */
	public void login(RoleInstance role);
	/**
	 * role offline
	 */
	public void offline(RoleInstance role);
	
	/**
	 * vip role increase vipExp directly
	 */
	public void addVipLevelExp(int roleId,int expAddValue);
//	public void addDiamands(RoleInstance role,int daimansAddValue);
	/**
	 * offline roles recharge ,handle msg queue
	 * @param roleId
	 * @param daimans
	 * @date 2014-5-4 下午01:39:53
	 */
	public void addDiamands(int roleId,int daimans);
	/**
	 * messge 2510
	 * @param role
	 */
	public Message openVipPanel(RoleInstance role);
	/**
	 * messge 2511
	 * @param role
	 * @param vipLevel
	 */
	public Message vipGalleryShift(RoleInstance role, byte vipLevel);
	/**
	 * msg 2512
	 * @param role
	 */
	public Message vipDailyAwardReceive(RoleInstance role);
	
	/**
	 * msg 2513
	 * @param role
	 * @param vipLevel
	 */
	public Message vipLevelUpAwardReceive(RoleInstance role, byte vipLevel);
	
	public byte getVipLevel(String roleId) ;
	public byte getVipLevel(RoleInstance role) ;
}
