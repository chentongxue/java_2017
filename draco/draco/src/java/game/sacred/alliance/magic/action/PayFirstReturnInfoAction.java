package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C2313_PayFirstReturnInfoReqMessage;
import com.game.draco.message.response.C2313_PayFirstReturnInfoRespMessage;

import sacred.alliance.magic.app.active.discount.type.DiscountRewardStat;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountReward;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class PayFirstReturnInfoAction extends BaseAction<C2313_PayFirstReturnInfoReqMessage>{

	private final short DIS_GOLD = 888 ;
	@Override
	public Message execute(ActionContext context, C2313_PayFirstReturnInfoReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Discount discount = GameContext.getActiveDiscountApp().getCurrentPayFirstDiscount(role);
		if(null == discount){
			return null ;
		}
		DiscountRewardStat rewardStat = discount.getRewardStatus(role, 0);
		if(DiscountRewardStat.REWARD_DONE == rewardStat){
			//已经领取
			return null ;
		}
		C2313_PayFirstReturnInfoRespMessage respMsg = new C2313_PayFirstReturnInfoRespMessage();
		respMsg.setActiveId(discount.getId());
		DiscountReward reward = discount.getRewardList().get(0);
		
		int goldMoney = reward.getGoldMoney() ;
		if(goldMoney > 0){
			//百分比
			respMsg.setReturnRate((short)goldMoney);
		}else{
			respMsg.setDisGold(DIS_GOLD);
		}
		List<GoodsOperateBean> goodsList = reward.getGoodsList();
		if(!Util.isEmpty(goodsList)){
			List<GoodsLiteItem> itemList = new ArrayList<GoodsLiteItem>();
			for(GoodsOperateBean good : goodsList){
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(good.getGoodsId());
				if(null == goodsBase){
					continue ;
				}
				GoodsLiteItem goodItem = goodsBase.getGoodsLiteItem();
				goodItem.setNum((short)good.getGoodsNum());
				goodItem.setBindType(good.getBindType().getType());
				itemList.add(goodItem);
			}
			respMsg.setGoodsItems(itemList);
		}
		respMsg.setState((byte)0);
		if (DiscountRewardStat.REWARD_CAN == rewardStat) {
			// 可以领取
			respMsg.setState((byte)1);
			respMsg.setGainGoldNum(reward.calcRealGainGold(role
					.getDiscountDbInfo().get(discount.getId())));
		}
		return respMsg;
	}

}
