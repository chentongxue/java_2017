package sacred.alliance.magic.app.arena;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;

public class Arena_1V1 extends Arena{
	
	@Override
	protected void activeStart() {
		GameContext.getArena1V1App().activeStart();
	}
	
	@Override
	protected void activeStop() {
		GameContext.getArena1V1App().activeStop();
	}
	
	@Override
	protected void activeIng() {
		GameContext.getArena1V1App().activeIng();
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	@Override
	protected void onAutoApply(RoleInstance role){
		if(null == role){
			return ;
		}
		GameContext.getArena1V1App().setAutoApply(role, true);
	}
	
	@Override
	protected void offAutoApply(RoleInstance role){
		if(null == role){
			return ;
		}
		GameContext.getArena1V1App().setAutoApply(role, false);
	}
	
	@Override
	protected ArenaResult applyCheck(RoleInstance role) {
		ArenaResult result = new ArenaResult();
		if(!active.isSuitLevel(role)){
			result.setInfo(this.getText(TextId.ARENA_APPLY_LEVEL_NOT_MEET));
			return result ;
		}
		if(!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
			result.setInfo(this.getText(TextId.ARENA_APPLY_ROLE_NOT_ONLINE));
			return result ;
		}
		//查看是否已经报名
		ApplyInfo applyInfo = manager.getApplyInfo(role.getRoleId());
		if(null != applyInfo){
			if(applyInfo.getActiveId() == this.active.getId()){
				//当前已经是报名状态
				result.setCurrentApplyState(ApplyState.had_apply);
				result.setInfo(this.getText(TextId.ARENA_ALREADY_APPLY));
			}else{
				result.setInfo(this.getText(TextId.ARENA_ALREADY_APPLY_OTHER));
			}
			return result ;
		}
		/*MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance || !this.manager.isApplyMap(mapInstance.getMap().getMapId())){
			result.setInfo("当前地图不允许报名擂台赛");
			return result ;
		}*/
		//判断当前用户是否在副本报名队列
		if(GameContext.getCopyTeamApp().inApplyStatus(role)){
			result.setInfo(this.getText(TextId.ARENA_APPLY_NOT_ALLOW));
			return result ;
		}
		//判断战斗力
		if(null != this.config && 
				role.getBattleScore() < this.config.getBattleScore()){
			String str = GameContext.getI18n().messageFormat(TextId.ARENA_APPLY_BATTLESCORE, this.config.getBattleScore());
			result.setInfo(str);
			return result ;
		}
		result.success();
		return result;
	}
	
	

	@Override
	protected ApplyInfo createApplyInfo(RoleInstance role,Object context) {
		ApplyInfo info = new ApplyInfo();
		info.setCreateDate(System.currentTimeMillis());
		info.setActiveId(this.active.getId());
		info.setLevel(role.getLevel());
		info.addApplyRole(role.getRoleId());
		info.setLeaderId(role.getRoleId());
		//获得当前用户的竞技场积分
		int score = role.getRoleArena().getScore(ArenaType._1V1);
		info.setScore(score);
		return info;
	}


	@Override
	protected ArenaResult matchNo(String roleId,ApplyInfo info) {
		ArenaResult result = new ArenaResult();
		ArenaMatch match = info.getMatch();
		match.matchNo(roleId);
		//将role的报名信息删除
		GameContext.getArenaApp().removeApplyInfo(roleId);
		//操作成功
		result.success();
		//判断对方是否也已经全部取消
		ApplyInfo otherTeam = match.getOtherTeam(info);
		if(match.teamAllCancel(otherTeam)){
			//对方也已经全部取消
			//匹配失败,将相关信息是否
			//注意销毁的顺序
			match.cancelAll();
			return result ;
		}
		//通知对方是否继续排队
		otherTeam.sendApplyKeepConfirm(match);
		return result;
	}



	@Override
	protected ArenaResult matchYes(String roleId,ApplyInfo info) {
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			//不在线,取消
			return matchNo(roleId, info);
		}
		ArenaResult result = new ArenaResult();
		ArenaMatch match = info.getMatch();
		match.matchYes(roleId);
		//如果有队伍则离开队伍
		Team team = role.getTeam();
		if(null != team && team.getPlayerNum()>1){
			team.memberLeave(role, LeaveTeam.apply);
		}
		result.success();
		
		ApplyInfo otherTeam = match.getOtherTeam(info);
		if(match.teamAllCancel(otherTeam)){
			//判断对方是否全部取消,发送继续保持报名确认
			info.sendApplyKeepConfirm(match);
			return result ;
		}
		//判断对方是否已经确定,如果确定直接发送跳转地图协议
		if(!match.teamAllSelected(otherTeam)){
			return result;
		}
		//将对方都传入地图
		match.sendEnterArenaMap();
		return result;
	}


	@Override
	public boolean filterMatch(ApplyInfo info) {
		return super.filterMatch(info);
	}



	@Override
	public ArenaType getArenaType() {
		return ArenaType._1V1;
	}



}
