package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2411_InviteActivationReqMessage;

import platform.message.request.C5951_InviteVoteFriendReqMessage;
import platform.message.response.C5951_InviteVoteFriendRespMessage;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.invite.InviteConfig;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class InviteActivationAction extends BaseAction<C2411_InviteActivationReqMessage> {
	@Override
	public Message execute(ActionContext context, C2411_InviteActivationReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String code = reqMsg.getCode() ;
		if(Util.isEmpty(code)){
			return new C0003_TipNotifyMessage(this.getText(TextId.ERROR_INPUT));
		}
		try {
			//判断背包是否满
			if(role.getRoleBackpack().freeGridCount() <=0 ){
				return new C0003_TipNotifyMessage(this.getText(TextId.Bag_Is_Full));
			}
			C5951_InviteVoteFriendReqMessage inviteReqMsg = new C5951_InviteVoteFriendReqMessage();
			inviteReqMsg.setCode(code);
			inviteReqMsg.setAppId(GameContext.getAppId());
			inviteReqMsg.setServerId(GameContext.getServerId());
			inviteReqMsg.setUserId(role.getUserId());
			inviteReqMsg.setRoleId(role.getRoleId());
			C5951_InviteVoteFriendRespMessage inviteRespMsg = (C5951_InviteVoteFriendRespMessage) GameContext
					.getHttpJsonClient().sendMessage(inviteReqMsg,
							GameContext.getPlatformConfig().getInviteHttpUrl());
			if(RespTypeStatus.SUCCESS != inviteRespMsg.getStatus()){
				return new C0003_TipNotifyMessage(this.getText(TextId.INVITE_HAVE_VOTE));
			}
			InviteConfig config = GameContext.getInviteApp().getInviteConfig();
			//添加物品
			GoodsResult result = GameContext.getUserGoodsApp().addGoodsForBag(role, config.getGoodsId(), 1, BindingType.already_binding, OutputConsumeType.invite_Vote);
			if(!result.isSuccess()){
				return new C0003_TipNotifyMessage(result.getInfo());
			}
			return new C0003_TipNotifyMessage(this.getText(TextId.INVITE_RECV_REWARD_SUCCESS));
		}catch(Exception ex){
			logger.error("InviteActivationAction error",ex);
		}
		return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
	}

}
