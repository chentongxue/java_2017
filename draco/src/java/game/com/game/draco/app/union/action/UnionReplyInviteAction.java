package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1711_UnionReplyInviteReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

/**
 * 邀请同意加入公会
 * @author mofun030602
 *
 */
public class UnionReplyInviteAction extends BaseAction<C1711_UnionReplyInviteReqMessage> {

	@Override
	public Message execute(ActionContext context, C1711_UnionReplyInviteReqMessage reqMsg) {
		try {
			String unionId = reqMsg.getUnionId();
			RoleInstance role = this.getCurrentRole(context);
			
			boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(unionId);
			if(isInActive){
				return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
			}
			
			if(Util.isEmpty(unionId) || null == role ){
				return null;
			}
			role.setUnionBeInviteTime(0);
			if(1 != reqMsg.getType()){
				//拒绝
				C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
				String str = this.messageFormat(TextId.Faction_Invite_Refuse, role.getRoleName());
				message.setMsgContext(str);
				GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(reqMsg.getRoleId()), message);
			    return null ;
			}
			Result result = GameContext.getUnionApp().inviteJoinUnion(reqMsg.getRoleId(), role);
			if(!result.isSuccess()){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
			}
			return null;
		} catch (Exception e) {
			this.logger.error("UnionReplyInviteAction", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.SYSTEM_ERROR));
		}
	}
	
}
