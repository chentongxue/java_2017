package sacred.alliance.magic.app.active.discount;

import java.util.Map;

import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.hint.HintSupport;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public interface ActiveDiscountApp extends Service,HintSupport {
	
	public boolean canRecvReward(RoleInstance role) ;

	public Map<Integer, Discount> getAllListMap();
	
	public Discount getDiscount(int discountId);
	/**
	 * 上线加载折扣活动记录
	 */
	public Map<Integer, DiscountDbInfo> loadRoleActiveDiscount(String userId);
	
	/**
	 * 角色折扣活动记录入库
	 * */
	public void saveRoleActiveDiscount(Map<Integer, DiscountDbInfo> discountDbInfoMap);
	
	/**
	 * 根据用户充值和消费的值来更新折扣活动list
	 * @param userId
	 * @param payValue
	 * @param isPay
	 * @param channelId
	 */
	public void updateFeeDiscount(String userId, int payValue, boolean isPay, int channelId,OutputConsumeType outputConsumeType);
	/**
	 * 入库失败日志
	 * @param discountDbInfoMap
	 */
	public void offlineLog(Map<Integer, DiscountDbInfo> discountDbInfoMap);
	/**
	 * 创建折扣活动列表消息
	 * @param role
	 * @param isFromHelper 如果是从助手系统过来的则指定第一个能领取，否则指向第一个
	 * @return
	 */
	public Message createDiscountListMsg(RoleInstance role, boolean isFromHelper);
	
	/** 获取充值界面说明文字 */
	public String getChargeDesc();
	
	/**
	 * 根据角色某些属性来更新折扣活动中与请求活动同类型的活动
	 * @param role
	 * @param channelId
	 * @param discount 请求的活动
	 */
	public void updateAttriDiscount(RoleInstance role, Discount discount);
	
	/**
	 * 由角色登录触发的活动
	 * @param role
	 */
	public void updateLoginDiscount(RoleInstance role);
	
	/**
	 * 热加载接口
	 * @return
	 */
	public Result reLoad();
	
	public Discount getCurrentPayFirstDiscount(RoleInstance role) ;
}
