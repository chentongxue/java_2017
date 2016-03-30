package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.InvitePanelItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2410_InvitePanelReqMessage;
import com.game.draco.message.response.C2410_InvitePanelRespMessage;

import platform.message.request.C5950_InviteInfoReqMessage;
import platform.message.response.C5950_InviteInfoRespMessage;
import sacred.alliance.magic.app.invite.ActivatedReward;
import sacred.alliance.magic.app.invite.InviteConfig;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class InvitePanelAction extends BaseAction<C2410_InvitePanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C2410_InvitePanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		try {
			String roleChannelId = "," + role.getChannelId() + ",";
			String openChannelId = "," + GameContext.getParasConfig().getOpenInviteChannelIds() + ",";
			if(openChannelId.indexOf(roleChannelId) < 0){
				return new C0003_TipNotifyMessage(this.getText(TextId.FORBID_MESSAGE));
			}
			
			C2410_InvitePanelRespMessage respMsg = new C2410_InvitePanelRespMessage();
			C5950_InviteInfoReqMessage inviteReqMsg = new C5950_InviteInfoReqMessage();
			inviteReqMsg.setAppId(GameContext.getAppId());
			inviteReqMsg.setServerId(GameContext.getServerId());
			inviteReqMsg.setUserId(role.getUserId());
			inviteReqMsg.setRoleId(role.getRoleId());
			C5950_InviteInfoRespMessage inviteRespMsg = (C5950_InviteInfoRespMessage) GameContext
					.getHttpJsonClient().sendMessage(inviteReqMsg,GameContext.getPlatformConfig().getInviteHttpUrl());
			if (RespTypeStatus.SUCCESS != inviteRespMsg.getStatus()) {
				return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
			}
			// 自己已经激活次数
			respMsg.setActivatedTimes(inviteRespMsg.getActivatedTimes());
			// 自己的邀请码
			respMsg.setCode(inviteRespMsg.getCode());
			int voteTimes = inviteRespMsg.getActivationTimes() ;
			
			respMsg.setInfo(GameContext.getInviteApp().getSharedInfo(role,inviteRespMsg.getCode()));
			InviteConfig config = GameContext.getInviteApp().getInviteConfig();

			int goodsId = 0 ;
			if(voteTimes <=0){
				// 激活别人并领取的奖励
				goodsId = config.getGoodsId() ;
			}
			respMsg.setInviteRewardGoods(this.getGoodsLiteItem(goodsId));

			int rewardLevel = inviteRespMsg.getRewardLevel() ;
			// 被激活能领取的
			List<ActivatedReward> list = GameContext.getInviteApp()
					.getActivatedRewardList();
			if (Util.isEmpty(list)) {
				return respMsg;
			}
			List<InvitePanelItem> items = new ArrayList<InvitePanelItem>();
			for (ActivatedReward reward : list) {
				InvitePanelItem item = new InvitePanelItem();
				item.setStatus(reward.getTimes() <= rewardLevel? (byte) 1: (byte) 0);
				item.setTimes(reward.getTimes());
				item.setGoodsInfo(this.getGoodsLiteItem(reward.getGoodsId()));
				items.add(item);
			}
			respMsg.setItems(items);
			return respMsg;
		}catch(Exception ex){
			logger.error("InvitePanelAction error",ex);
		}
		return new C0003_TipNotifyMessage(this.getText(TextId.SYSTEM_ERROR));
	}

	private  GoodsLiteItem getGoodsLiteItem(int goodsId){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null != goodsBase){
			GoodsLiteItem item = goodsBase.getGoodsLiteItem() ;
			// 甚至为绑定
			item.setBindType(BindingType.already_binding.getType());
			return item ;
		}
		// 占位
		return new GoodsLiteItem();
	}
	
}
