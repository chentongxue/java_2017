package sacred.alliance.magic.app.arena.action;

import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C3856_ArenaLearnConfirmReqMessage;

public class ArenaLearnConfirmAction extends BaseAction<C3856_ArenaLearnConfirmReqMessage>{
	private  final byte CANCEL_FLAG = 2 ;
	@Override
	public Message execute(ActionContext context, C3856_ArenaLearnConfirmReqMessage req) {
		byte result = req.getResult(); //1：确认 2：取消
		int targetRoleId = req.getRoleId();
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		//邀请者
		RoleInstance targetRole = GameContext.getOnlineCenter()
				.getRoleInstanceByRoleId(String.valueOf(targetRoleId));
		if(targetRole == null){
			C0003_TipNotifyMessage tipNotify = new C0003_TipNotifyMessage(Status.ArenaLearn_Targ_Not_Online.getTips());
			return tipNotify ;
		}
		
		role.setArenaLearnInviteTime(0);
		targetRole.setArenaLearnInviteTime(0);
		
		//取消，需把被邀请者的切磋时间清零
		if(result == CANCEL_FLAG){
			C0003_TipNotifyMessage tipNotify = new C0003_TipNotifyMessage(role.getRoleName() + 
					Status.ArenaLearn_Refuse.getTips());
			targetRole.getBehavior().sendMessage(tipNotify);
			return null;
		}
		
		Status status = GameContext.getArenaLearnApp().arenaLearnCondition(targetRole, role);
		if(!status.isSuccess()){
			return new C0003_TipNotifyMessage(status.getTips());
		}
		
		ArenaConfig arenaConfig = GameContext.getArenaApp().getArenaConfig(ArenaType._LEARN);
		if(arenaConfig == null){
			return  new C0003_TipNotifyMessage(Status.FAILURE.getTips());
		}
		
		//把双方都放入到擂台赛匹配队列,以确保不允许报名参加擂台赛
		ApplyInfo targetRoleApplyInfo = new ApplyInfo();
		targetRoleApplyInfo.addApplyRole(targetRole.getRoleId());
		
		ApplyInfo roleApplyInfo = new ApplyInfo();
		roleApplyInfo.addApplyRole(role.getRoleId());
		
		ArenaMatch match = ArenaMatch.create(targetRoleApplyInfo, roleApplyInfo, arenaConfig);
		if(null == match){
			logger.error("Arena learn Match.create error,roleId=" + role.getRoleId() + " targetRoleId=" + targetRole.getRoleId());
			C0003_TipNotifyMessage tipNotify = new C0003_TipNotifyMessage(Status.ArenaLearn_Refuse.getTips());
			targetRole.getBehavior().sendMessage(tipNotify);
			return null;
		}
		this.leaveTeam(role);
		this.leaveTeam(targetRole);
		//将双方加入报名队列
		Map<String, ApplyInfo> applyInfoMap = GameContext.getArenaApp().getAllRoleApplyInfo();
		applyInfoMap.put(role.getRoleId(), roleApplyInfo);
		applyInfoMap.put(targetRole.getRoleId(), targetRoleApplyInfo);
		//不能忘记，创建地图实例必需
		targetRoleApplyInfo.setMatch(match);
		roleApplyInfo.setMatch(match);
		match.sendEnterArenaMap();
		return null;
	}
	
	private void leaveTeam(RoleInstance role) {
		try {
			// 如果有队伍则离开队伍
			Team team = role.getTeam();
			if (null != team && team.getPlayerNum() > 1) {
				team.memberLeave(role, LeaveTeam.apply);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

}
