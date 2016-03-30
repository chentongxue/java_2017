package sacred.alliance.magic.app.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C3855_ArenaLearnApplyNotifyMessage;
import com.game.draco.message.request.C3855_ArenaLearnApplyReqMessage;

public class ArenaLearnApplyAction extends BaseAction<C3855_ArenaLearnApplyReqMessage>{

	@Override
	public Message execute(ActionContext context, C3855_ArenaLearnApplyReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		
		//被邀请人角色ID
		int targetRoleId = req.getRoleId();
		RoleInstance targetRole = GameContext.getOnlineCenter()
				.getRoleInstanceByRoleId(String.valueOf(targetRoleId));
		
		Status status = GameContext.getArenaLearnApp().arenaLearnCondition(targetRole, role);
		if(!status.isSuccess()){
			C0003_TipNotifyMessage tipNotify = new C0003_TipNotifyMessage(status.getTips());
			return tipNotify;
		}
		
		//设置邀请者的切磋时间
		targetRole.setArenaLearnInviteTime(System.currentTimeMillis());
		role.setArenaLearnInviteTime(System.currentTimeMillis());
		
		//给被邀请者发送邀请消息
		C3855_ArenaLearnApplyNotifyMessage applyNotifyMsg = new C3855_ArenaLearnApplyNotifyMessage();
		applyNotifyMsg.setRoleId(role.getIntRoleId());
		applyNotifyMsg.setInfo(role.getRoleName()+Status.ArenaLearn_Prompt.getTips());
		targetRole.getBehavior().sendMessage(applyNotifyMsg);
		return null;
	}

}
