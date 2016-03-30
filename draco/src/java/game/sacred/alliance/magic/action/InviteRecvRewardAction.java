package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import platform.message.request.C5952_InviteRecvRewardReqMessage;
import platform.message.response.C5952_InviteRecvRewardRespMessage;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.invite.ActivatedReward;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.request.C2412_InviteRecvRewardReqMessage;
import com.game.draco.message.response.C2412_InviteRecvRewardRespMessage;

public class InviteRecvRewardAction extends BaseAction<C2412_InviteRecvRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2412_InviteRecvRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		C2412_InviteRecvRewardRespMessage respMsg = new C2412_InviteRecvRewardRespMessage();
		
		try {
			List<ActivatedReward> list = GameContext.getInviteApp().getActivatedRewardList();
			if (Util.isEmpty(list)) {
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(this.getText(TextId.INVITE_NOT_ENABLE_REWARD));
				return respMsg ;
			}
			C5952_InviteRecvRewardReqMessage inviteReqMsg = new C5952_InviteRecvRewardReqMessage();
			inviteReqMsg.setAppId(GameContext.getAppId());
			inviteReqMsg.setServerId(GameContext.getServerId());
			inviteReqMsg.setUserId(role.getUserId());
			inviteReqMsg.setRoleId(role.getRoleId());
			C5952_InviteRecvRewardRespMessage inviteRespMsg = (C5952_InviteRecvRewardRespMessage) GameContext
					.getHttpJsonClient().sendMessage(inviteReqMsg,
							GameContext.getPlatformConfig().getInviteHttpUrl());
			if (RespTypeStatus.SUCCESS != inviteRespMsg.getStatus()) {
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(this.getText(TextId.INVITE_NOT_ENABLE_REWARD));
				return respMsg ;
			}
			int pre = inviteRespMsg.getPreRewardLevel();
			int now = inviteRespMsg.getNowRewardLevel();
			
			 List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
			for(ActivatedReward reward : list){
				if(reward.getTimes() > pre && reward.getTimes() <= now){
					GoodsOperateBean bean = new GoodsOperateBean();
					bean.setGoodsId(reward.getGoodsId());
					bean.setGoodsNum(1);
					bean.setBindType(BindingType.already_binding);
					addList.add(bean);
				}
			}
			if(0 == addList.size()){
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(this.getText(TextId.INVITE_NOT_ENABLE_REWARD));
				return respMsg ;
			}
			AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList, OutputConsumeType.invite_Recv_Reward);
			
			List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
			if(Util.isEmpty(putFailureList)){
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				respMsg.setInfo(this.getText(TextId.INVITE_RECV_REWARD_SUCCESS));
				return respMsg ;
			}
			//背包满了发邮件
			String mailContent = this.getText(TextId.INVITE_MAIL_CONTEXT) ;
			GameContext.getMailApp().sendMail(role.getRoleId(),
					MailSendRoleType.System.getName(), 
					mailContent,
					MailSendRoleType.System.getName(), 
					OutputConsumeType.invite_Recv_Reward_Mail.getType(), 
					putFailureList);
			
			respMsg.setStatus(RespTypeStatus.SUCCESS);
			respMsg.setInfo(this.getText(TextId.INVITE_RECV_REWARD_MAIL_SUCCESS));
		}catch(Exception ex){
			logger.error("InviteRecvRewardAction error",ex);
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(this.getText(TextId.SYSTEM_ERROR));
		}
		return respMsg ;
		
	}

}
