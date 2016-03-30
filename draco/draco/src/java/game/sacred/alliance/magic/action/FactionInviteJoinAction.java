//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1710_FactionInviteJoinReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//import com.game.draco.message.response.C1710_FactionForwardInviteRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.constant.TimeoutConstant;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionInviteJoinAction extends BaseAction<C1710_FactionInviteJoinReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1710_FactionInviteJoinReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		String targetRoleId = String.valueOf(reqMsg.getRoleId());
//		if(GameContext.getSocialApp().isShieldByTarget(role.getRoleId(), targetRoleId)){
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.Faction_Invite_Shield_By_Target));
//		}
//		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targetRoleId);
//		if(null == targetRole){
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.Faction_Invite_Not_Online));
//		}
//		if(role.getCampId() != targetRole.getCampId()) {
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.Faction_Invite_Not_Same_Camp));
//		}
//		long currTime = System.currentTimeMillis();
//		if(currTime - targetRole.getFactionBeInviteTime() < TimeoutConstant.Faction_Reply_Timeout){
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.Faction_TargetRole_Busy));
//		}
//		targetRole.setFactionBeInviteTime(currTime);
//		Result result = GameContext.getFactionApp().inviteJoinFaction(role, targetRole);
//		if(!result.isSuccess()){
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
//		}
//		//通知被邀请人
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		C1710_FactionForwardInviteRespMessage resp = new C1710_FactionForwardInviteRespMessage();
//		resp.setRoleId(role.getIntRoleId());
//		resp.setRoleName(role.getRoleName());
//		resp.setFactionId(faction.getFactionId());
//		resp.setFactionName(faction.getFactionName());
//		resp.setLeaderName(faction.getLeaderName());
//		resp.setMemberNum((short) faction.getMemberNum());
//		resp.setMaxMemberNum((short) faction.getMaxMemberNum());
//		targetRole.getBehavior().sendMessage(resp);
//		return null;
//	}
//	
//}
