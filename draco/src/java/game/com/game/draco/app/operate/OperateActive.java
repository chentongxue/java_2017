package com.game.draco.app.operate;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.vo.OperateActiveType;

/**
 * 运营活动接口
 */
public interface OperateActive {

	/**
	 * 当前是否开启
	 * @param role
	 * @return
	 */
	public boolean isOpen(RoleInstance role) ;
	
	/**
	 * 是否显示在活动列表
	 * @param role
	 * @return
	 */
	public boolean isShow(RoleInstance role);
	
	/**
	 * 根据用户充值来更新活动
	 * @param role
	 * @param moneyValue 钻石
	 * @param outputConsumeType
	 */
	public void onPay(RoleInstance role,int moneyValue,OutputConsumeType outputConsumeType);
	
	/**
	 * 根据用户消费来更新活动
	 * @param role
	 * @param moneyValue 钻石
	 * @param outputConsumeType
	 */
	public void onConsume(RoleInstance role,int moneyValue,OutputConsumeType outputConsumeType);
	
	/**
	 * 登录触发运营活动
	 * @param role
	 */
	public void onLogin(RoleInstance role);
	
	/**
	 * 打开面板触发
	 * @param role
	 */
	public void onOpen(RoleInstance role);
	
	/**
	 * 获得运营活动ID
	 * @return
	 */
	public int getOperateActiveId();
	
	/**
	 * 获得运营活动名称
	 * @return
	 */
	public String getOperateActiveName();
	
	/**
	 * 获得运营活动类型
	 * @return
	 */
	public OperateActiveType getOperateActiveType();
	
	/**
	 * 获得活动详情
	 * @param role
	 * @return
	 */
	public Message getOperateActiveDetail(RoleInstance role);
	
	/**
	 * 获得活动状态
	 * @param role
	 * @return
	 */
	public byte getOperateActiveStatus(RoleInstance role);
	
	/**
	 * 是否有红点提示
	 * @param role
	 * @return
	 */
	public boolean hasHint(RoleInstance role);
	
}
