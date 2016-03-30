package sacred.alliance.magic.app.quickbuy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

public interface QuickBuyApp{
	
	/**
	 * 快速购买
	 * @param role 角色
	 * @param goodsId 物品ID
	 * @param goodsNum 物品数量
	 * @param outputType 消耗类型
	 * @param notEnoughGoodsTips 物品不足的提示信息（没有特殊需求直接传NULL）
	 * @return
	 */
	public Result doQuickBuy(RoleInstance role, int goodsId, int goodsNum,
			OutputConsumeType outputType, String notEnoughGoodsTips);
	
	/**
	 * 快速购买
	 * @param role 角色
	 * @param goodsList 物品列表
	 * @param outputType 消耗类型
	 * @param notEnoughGoodsTips 物品不足的提示信息（没有特殊需求直接传NULL）
	 * @return
	 */
	public Result doQuickBuy(RoleInstance role, List<GoodsOperateBean> goodsList,
			OutputConsumeType outputType, String notEnoughGoodsTips);
	
	/**
	 * 快速购买
	 * @param role 角色
	 * @param goodsMap 物品列表
	 * @param outputType 消耗类型
	 * @param notEnoughGoodsTips 物品不足的提示信息（没有特殊需求直接传NULL）
	 * @return
	 */
	public Result doQuickBuy(RoleInstance role, Map<Integer,Integer> goodsMap,
			OutputConsumeType outputType, String notEnoughGoodsTips);
	
	/**
	 * 获取参数中的命令字
	 * @param parameter
	 * @return
	 */
	public short getCommandIdByParameter(String parameter);
	
	/**
	 * 快速购买支持的命令字集合
	 * @return
	 */
	public Set<Short> getCommandIdSet();
	
}
