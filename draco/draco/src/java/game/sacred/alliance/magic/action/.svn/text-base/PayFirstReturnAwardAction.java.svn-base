package sacred.alliance.magic.action;

import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2314_PayFirstReturnAwardReqMessage;
import com.game.draco.message.response.C2314_PayFirstReturnAwardRespMessage;

import sacred.alliance.magic.app.active.discount.type.DiscountRewardStat;
import sacred.alliance.magic.app.active.discount.type.DiscountType;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountReward;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public class PayFirstReturnAwardAction extends BaseAction<C2314_PayFirstReturnAwardReqMessage> {

	@Override
	public Message execute(ActionContext context,
			C2314_PayFirstReturnAwardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2314_PayFirstReturnAwardRespMessage respMsg = new C2314_PayFirstReturnAwardRespMessage();
		respMsg.setResult(RespTypeStatus.FAILURE);
		int discountId = reqMsg.getActiveId() ;
		Discount discount = GameContext.getActiveDiscountApp().getDiscount(discountId);
		if(null == discount || 
				DiscountType.PAY_FIRST_RETURN != discount.getDiscountType()){
			respMsg.setInfo(this.getText(TextId.ERROR_INPUT));
			return respMsg ;
		}
		DiscountRewardStat rewardStat = discount.getRewardStatus(role, 0);
		if(DiscountRewardStat.REWARD_DONE == rewardStat){
			//已经领取
			respMsg.setInfo(this.getText(TextId.PAY_FIRST_REWARD_HAD_RECV));
			return respMsg ;
		}
		if(DiscountRewardStat.REWARD_CAN != rewardStat){
			respMsg.setInfo(this.getText(TextId.PAY_FIRST_REWARD_CANOT_RECV));
			return respMsg ;
		}
		// 可以领取
		DiscountReward reward = discount.getRewardList().get(0);
		if (null == reward) {
			return respMsg;
		}
		// 标识为已经领取
		DiscountDbInfo discountDbInfo = role.getDiscountDbInfo()
				.get(discountId);
		discountDbInfo.updateRewardCount(0);
		discountDbInfo.setOperateDate(new Date());
		// 发送奖励邮件
		int goldMoney = reward.calcRealGainGold(discountDbInfo);
		int bindGoldMoney = reward.getBindMoney();
		int silverMoney = reward.getSilverMoney();
		GameContext.getMailApp().sendMail(role.getRoleId(), 
				this.getText(TextId.PAY_FIRST_REWARD_MAIL_TITLE), 
				this.getText(TextId.PAY_FIRST_REWARD_MAIL_CONTENT),
				this.getText(TextId.PAY_FIRST_REWARD_MAIL_SEND_ROLE),
				OutputConsumeType.pay_first_award.getType(),
				goldMoney, bindGoldMoney, silverMoney, 
				reward.getGoodsList());
		//去掉动态菜单
//		GameContext.getMenuApp().refresh(role, MenuIdType.first_charge);
		respMsg.setResult(RespTypeStatus.SUCCESS);
		respMsg.setInfo(this.getText(TextId.PAY_FIRST_REWARD_RECV_SUCCESS));
		return respMsg;
		
	}

}
