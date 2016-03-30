package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActiveDiscountDetailItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C2316_ActiveDiscountDetailReqMessage;
import com.game.draco.message.response.C2316_ActiveDiscountDetailRespMessage;

import sacred.alliance.magic.app.active.discount.type.DiscountType;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.app.active.vo.DiscountReward;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveDiscountDetailAction extends BaseAction<C2316_ActiveDiscountDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C2316_ActiveDiscountDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C2316_ActiveDiscountDetailRespMessage respMsg = new C2316_ActiveDiscountDetailRespMessage();
		Discount discount = GameContext.getActiveDiscountApp().getAllListMap().get(reqMsg.getId());
		if(null == discount){
			return respMsg;
		}
		//打开活动详情的时候更新当前活动同类的活动
		GameContext.getActiveDiscountApp().updateAttriDiscount(role, discount);
		//活动详细信息
		respMsg.setTime(discount.getTimeDesc());
		respMsg.setDesc(discount.getDesc());
		List<DiscountCond> condList = discount.getCondList();
		List<ActiveDiscountDetailItem> detailItemList = new ArrayList<ActiveDiscountDetailItem>();
		DiscountDbInfo discountDbInfo = role.getDiscountDbInfo().get(discount.getId());
		this.calcDiscountDbInfo(discount, discountDbInfo);
		for(int i=0; i<condList.size(); i++){
			DiscountCond cond = condList.get(i);
			if(null == cond){
				continue;
			}
			DiscountReward reward = discount.getRewardList().get(i);
			ActiveDiscountDetailItem discountDetailItem = new ActiveDiscountDetailItem();
			discountDetailItem.setCondDesc(cond.getDesc());
			discountDetailItem.setBindMoney(reward.getBindMoney());
			discountDetailItem.setSliverMoney(reward.getSilverMoney());
			discountDetailItem.setGoldMoney(reward.calcRealGainGold(discountDbInfo));
			//领奖状态
			discountDetailItem.setRewardStatus((byte)discount.getRewardStatus(discountDbInfo, i).getType());
			
			//物品奖励
			List<GoodsOperateBean> goodsList = reward.getGoodsList();
			if(!Util.isEmpty(goodsList)){
				List<GoodsLiteNamedItem> itemList = new ArrayList<GoodsLiteNamedItem>();
				for(GoodsOperateBean good : goodsList){
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(good.getGoodsId());
					if(null == goodsBase){
						continue ;
					}
					GoodsLiteNamedItem goodItem = goodsBase.getGoodsLiteNamedItem();
					goodItem.setNum((short)good.getGoodsNum());
					goodItem.setBindType(good.getBindType().getType());
					itemList.add(goodItem);
				}
				discountDetailItem.setRewardList(itemList);
			}
			
			//=============================
			//完成度
			DiscountType discountType = discount.getDiscountType() ;
			//已经达成数值
			int curValue = 0 ;
			//条件值
			int condValue = cond.getMinValue();
			if(discountType.isTotal()){
				//积累值
				curValue = (null == discountDbInfo )? 0: discountDbInfo.getTotalValue();
			}else {
				//单次,用次数
				condValue = 1 ;
				curValue = (null == discountDbInfo )? 0: discountDbInfo.getCondCount(i);
				//可以多次只取一次
				curValue = Math.min(curValue, 1);
			}
			if(condValue <=0){
				//避免客户端除0奔溃
				condValue = 1 ;
			}
			discountDetailItem.setCurValue(curValue);
			discountDetailItem.setCondValue(condValue);
			//=============================
			detailItemList.add(discountDetailItem);
		}
		respMsg.setDetailItemList(detailItemList);
		return respMsg;
	}
	
	/**
	 * 如果discountType=PAY_SUITE || BUY_SUITE,则根据totalValue计数条件计数
	 * @param discount
	 * @param dbInfo
	 */
	private void calcDiscountDbInfo(Discount discount, DiscountDbInfo dbInfo){
		DiscountType discountType = discount.getDiscountType();
		if(!(discountType == DiscountType.PAY_SUITE || discountType == DiscountType.BUY_SUITE)){
			return ;
		}
		if(null == dbInfo){
			return ;
		}
		discount.calcCondByTotal(dbInfo);
	}

}
