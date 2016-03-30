package com.game.draco.app.operate.discount;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.message.item.ActiveDiscountDetailItem;

public interface DiscountApp extends Service, AppSupport {

	/**
	 * 判断是否有领奖资格
	 * @param role
	 * @return
	 */
	public boolean canRecvReward(RoleInstance role);

	/**
	 * 获得所有折扣配置
	 * @return
	 */
	public Map<Integer, Discount> getAllDiscountConfigMap();

	/**
	 * 获得某个折扣配置
	 * @param discountId
	 * @return
	 */
	public Discount getDiscount(int discountId);

	/**
	 * 上线加载数据库中所有折扣活动记录
	 * @param roleId
	 * @return
	 */
	public Map<Integer, RoleDiscount> loadRoleActiveDiscount(String roleId);

	/**
	 * 批量保存玩家折扣信息s
	 * @param roleId
	 */
	public void saveRoleActiveDiscount(String roleId);

	/**
	 * 获得玩家所有折扣信息
	 * @param roleId
	 * @return
	 */
	public Map<Integer, RoleDiscount> getRoleDiscountMap(String roleId);

	/**
	 * 获得在线玩家折扣信息
	 * @param roleId
	 * @param activeId
	 * @return
	 */
	public RoleDiscount getRoleDiscount(String roleId, int activeId);

	/**
	 * 热加载接口
	 * @return
	 */
	public Result reLoad();

	/**
	 * 创建活动详细信息
	 * @param role
	 * @param discount
	 * @return
	 */
	public Message buildDiscountDetailMessage(RoleInstance role, Discount discount);
	
	/**
	 * 获得离线玩家折扣信息
	 * @param role
	 * @param activetId
	 * @return
	 */
	public RoleDiscount getOfflineRoleDiscount(String roleId, int activeId);
	
	/**
	 * 创建新的折扣活动（如果玩家在线放到内存中）
	 * @param roleId
	 * @param activeId
	 * @param online
	 * @return
	 */
	public RoleDiscount createRoleDiscount(String roleId, int activeId, boolean online);

	public List<ActiveDiscountDetailItem> getActiveDIscountDetailItems(
			RoleInstance role, Discount discount);
	
}