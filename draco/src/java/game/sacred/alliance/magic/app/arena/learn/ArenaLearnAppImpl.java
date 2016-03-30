package sacred.alliance.magic.app.arena.learn;

import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class ArenaLearnAppImpl implements ArenaLearnApp {

	private final int arenaLearnInviteOutTime = 30 * 1000; //切磋邀请未操作的超时时间（毫秒）
	private final int arenaLearnDistance = 800 ;//切磋最远距离
	
	
	/**  切磋条件  **/
	@Override
	public Status arenaLearnCondition(RoleInstance targetRole, RoleInstance role){
		if(targetRole == null || role == null){
			return Status.Role_No_Online;
		}
		if(null != targetRole && 
				GameContext.getSocialApp().isShieldByTarget(
						role.getRoleId(), targetRole.getRoleId())){
			//自己在对方黑名单中
			return Status.ArenaLearn_ShieldByTarget ;
		}
		
		//判断目标角色是否已被邀请切磋
		long inviteTime = targetRole.getArenaLearnInviteTime() + arenaLearnInviteOutTime;
		if(inviteTime > System.currentTimeMillis()){
			return Status.ArenaLearn_Role_Busy;
		}
		
		//判断目标角色是否已在擂台赛的匹配队列中
		String targetRoleId = targetRole.getRoleId();
		ApplyInfo targetApply = GameContext.getArenaApp().getApplyInfo(targetRoleId);
		if(null != targetApply){
			return Status.ArenaLearn_Role_HasArena;
		}
		String roleId = role.getRoleId();
		ApplyInfo selfApply = GameContext.getArenaApp().getApplyInfo(roleId);
		if(null != selfApply){
			return Status.ArenaLearn_Self_HasArena;
		}
		
		//判断双方是否在同一张地图
		MapInstance targetRoleMapInstance = targetRole.getMapInstance();
		MapInstance roleMapInstance = role.getMapInstance();
		if(targetRoleMapInstance == null || roleMapInstance == null){
			return Status.ArenaLearn_Beyond_Distance;
		}
		if(!targetRoleMapInstance.getInstanceId().equals(roleMapInstance.getInstanceId())){
			return Status.ArenaLearn_Beyond_Distance;
		}
		
		//判断地图是否允许邀请切磋
		if( 1 != roleMapInstance.getMap().getMapConfig().getCanLearnPk()){
			return Status.ArenaLearn_TheMap_NoAllow;
		}
		//判断两者是否在规定距离内
		if((Math.abs(role.getMapX() - targetRole.getMapX()) 
				+ Math.abs(role.getMapY() - targetRole.getMapY())) 
			> arenaLearnDistance ){
			return Status.ArenaLearn_Beyond_Distance;
		}
		if(GameContext.getCopyTeamApp().inApplyStatus(targetRole)){
			return Status.ArenaLearn_Role_COPY_TEAM ;
		}
		if(GameContext.getCopyTeamApp().inApplyStatus(role)){
			return Status.ArenaLearn_Self_COPY_TEAM ;
		}
		return Status.SUCCESS;
	}
}
