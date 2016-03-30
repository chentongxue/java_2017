package com.game.draco.app.operate;

import java.util.Collection;
import java.util.List;

import com.game.draco.app.hint.HintSupport;
import com.game.draco.app.operate.vo.OperateActiveType;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface OperateActiveApp extends Service, HintSupport {

	/**
	 * 获得运营活动列表
	 * @return
	 */
	public List<OperateActive> getAllOperateActive(RoleInstance role);
	
	/**
	 * 获得运营活动详情
	 * @param role
	 * @param activeId
	 * @return
	 */
	public Message getOperateActiveDetail(RoleInstance role,int activeId);
	
	/**
	 * 注册活动列表
	 * @param activeList
	 * @return
	 */
	public Result registerOperateActive(Collection<OperateActive> activeList, OperateActiveType operateActiveType);
	
	/**
	 * 根据用户充值来更新活动
	 * @param role
	 * @param rmbMoneyValue
	 * @param outputConsumeType
	 */
	public void onPay(RoleInstance role,int rmbMoneyValue,OutputConsumeType outputConsumeType);
	
	/**
	 * 根据用户消费来更新活动
	 * @param role
	 * @param rmbMoneyValue
	 * @param outputConsumeType
	 */
	public void onConsume(RoleInstance role,int rmbMoneyValue,OutputConsumeType outputConsumeType);
	
	/**
	 * 获得活动对象
	 * @param activeId
	 */
	public OperateActive getOperateActive(int activeId);
	
	/**
	 * 通知活动状态变化
	 * @param activeId
	 * @param status
	 */
	public void pushHintChange(RoleInstance role, int activeId, byte status);
	
	/**
	 * 是否有红点提示
	 * @param role
	 * @return
	 */
	public boolean hasHint(RoleInstance role);
	
}
