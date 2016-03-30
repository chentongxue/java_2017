package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1900_RoleOnlineRewardViewReqMessage;
import com.game.draco.message.response.C1900_RoleOnlineRewardViewRespMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.role.reward.OnlineReward;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleOnlineRewardViewAction extends BaseAction<C1900_RoleOnlineRewardViewReqMessage> {

	@Override
	public Message execute(ActionContext context, C1900_RoleOnlineRewardViewReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		OnlineReward reward = role.getOnlineReward();
		if(null == reward){
			return new C0003_TipNotifyMessage(Status.Role_No_Can_Reward.getTips());
		}
		List<GoodsLiteItem> goodsList = new ArrayList<GoodsLiteItem>();
		for(GoodsOperateBean bean : reward.getGoodsList()){
			if(null == bean){
				continue;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
			if(null == goodsBase){
				continue ;
			}
			GoodsLiteItem item = goodsBase.getGoodsLiteItem() ;
			byte bindType = bean.getBindType().getType();
			if(BindingType.template == bean.getBindType()){
				bindType = goodsBase.getBindType();
			}
			//鑒좆，곬땍잚謹
			item.setNum((short) bean.getGoodsNum());
			item.setBindType(bindType);
			goodsList.add(item);
		}
		
		C1900_RoleOnlineRewardViewRespMessage resp = new C1900_RoleOnlineRewardViewRespMessage();
		Date now = new Date();
		Date endDate = role.getOnlineRewardNextTime();
		boolean canTake = now.after(endDate);
		if(!canTake){
			int remainTime = DateUtil.dateDiffSecond(now, endDate);
			role.setOnlineRewardRemainTime(remainTime);
			resp.setTime(remainTime);
		}
		resp.setBindMoney(reward.getBindMoney());
		resp.setSilverMoney(reward.getSilverMoney());
		resp.setExp(reward.getExp());
		resp.setRewardGoodsList(goodsList);
		return resp;
	}

}
