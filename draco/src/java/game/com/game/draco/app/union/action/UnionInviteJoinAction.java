package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1710_UnionInviteJoinReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1710_UnionForwardInviteRespMessage;

/**
 * 邀请角色加入公会
 * @author mofun030602
 *
 */
public class UnionInviteJoinAction extends BaseAction<C1710_UnionInviteJoinReqMessage> {

	@Override
	public Message execute(ActionContext context, C1710_UnionInviteJoinReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		String targetRoleId = String.valueOf(reqMsg.getRoleId());
		if(GameContext.getSocialApp().isShieldByTarget(role.getRoleId(), targetRoleId)){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.UNION_INVITE_SHIELD_BY_TARGET));
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targetRoleId);
		if(null == targetRole){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.UNION_INVITE_NOT_ONLINE));
		}
		long currTime = System.currentTimeMillis();
		if(currTime - targetRole.getUnionBeInviteTime() < TimeoutConstant.Union_Reply_Timeout){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.UNION_TARGETROLE_BUSY));
		}
		targetRole.setUnionBeInviteTime(currTime);
		Result result = GameContext.getUnionApp().inviteJoinUnionValid(role, targetRole);
		if(!result.isSuccess()){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
		}
		//通知被邀请人
		Union union = GameContext.getUnionApp().getUnion(role);
		
		boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(union.getUnionId());
		if(isInActive){
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
		}
		
		C1710_UnionForwardInviteRespMessage resp = new C1710_UnionForwardInviteRespMessage();
		resp.setRoleId(role.getIntRoleId());
		resp.setRoleName(role.getRoleName());
		resp.setUnionId(union.getUnionId());
		resp.setUnionName(union.getUnionName());
		resp.setLeaderName(union.getLeaderName());
		resp.setMemberNum((short) union.getUnionMemberMap().size());
		resp.setMaxMemberNum((short)GameContext.getUnionApp().getUnionDataAllNum(union.getUnionLevel()));
		targetRole.getBehavior().sendMessage(resp);
		return null;
	}
	
}
