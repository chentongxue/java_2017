package sacred.alliance.magic.app.team;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C1301_TeamApplyNotifyMessage;
import com.game.draco.message.push.C1311_TeamInviteNotifyMessage;

import sacred.alliance.magic.app.role.systemset.TeamShieldType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class TeamAppImpl implements TeamApp {
	private AtomicBoolean started = new AtomicBoolean(false);
	private Cache<String, Team> teamCache;//下线后角色ID和队伍对象
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public void start(){
		if (!started.compareAndSet(false, true)) {
			return;
		}
		teamCache.addCacheListener(new CacheListener() {
			@Override
			public void entryRemoved(CacheEvent event) {
				if (null == event) {
					return;
				}
				Team team = (Team)event.getValue();
				if(team == null){
					return ;
				}
				
				try {
					String roleId = (String) event.getKey();
					Map<String,AbstractRole> off = team.getOfflineMembers();
					if(off.containsKey(roleId)){
						team.memberLeave(getRoleInstance(roleId), LeaveTeam.exit);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void entryAccessed(CacheEvent event) {
			}
			@Override
			public void entryAdded(CacheEvent event) {
			}
			@Override
			public void entryCleared(CacheEvent event) {
			}
			@Override
			public void entryExpired(CacheEvent event) {
			}
			@Override
			public void entryUpdated(CacheEvent event) {
			}
		});
	}

	/** 上线时加载队伍 **/
	public void onlineLoadTeam(AbstractRole role){
		try{
			String roleId = role.getRoleId();
			Team team = this.teamCache.get(roleId);
			if(team == null){
				return ;
			}
			
			Map<String,AbstractRole> off = team.getOfflineMembers();
			if(off.containsKey(roleId)){
				off.remove(roleId);
				team.memberJoin(role);
			}
		}catch(Exception e){
			logger.error("onlineLoadTeam error",e);
		}
	}
	
	@Override
	public Status canBuildTeam(RoleInstance role, RoleInstance targRole){
		if (null == role || null == targRole) {
			return Status.Team_Role_Not_Online;
		}
		if(role.getIntRoleId() == targRole.getIntRoleId()){
			return Status.Team_Role_Self;
		}
		//在同一队伍中，组队失败
		if(this.isInSameTeam(role, targRole)){
			return Status.Team_In_SameTeam;
		}
		//双方都有队伍，不是同一个队伍，组队失败
		if(this.hasTeam(role) && this.hasTeam(targRole)){
			return Status.Team_In_differentTeam;
		}
		Team team = role.getTeam();
		Team targTeam = targRole.getTeam();
		boolean isInviteor = true;//是否是邀请组队（组队成功后自己是队长）
		if(this.hasTeam(role)){
			//邀请组队（自己有队伍，目标没有队伍）
			if(team.getLeader().getIntRoleId() != role.getIntRoleId()){
				return Status.Team_Role_OwnTeam;
			}
			if(this.hasTeam(targRole)){
				return Status.Team_Targ_OwnTeam;
			}
		}else if(this.hasTeam(targRole)){
			//申请入队（自己没队伍，目标有队伍）
			if(targTeam.isFull()){
				return Status.Team_Full;
			}
			isInviteor = false;
		}
		Status status = this.checkMapType(role);
		if(!status.isSuccess()){
			return status;
		}
		status = this.checkMapType(targRole);
		if(!status.isSuccess()){
			return status;
		}
		//判断对方是否繁忙
		long currTime = System.currentTimeMillis();
		if(currTime - targRole.getTeamApplyTime() <= TimeoutConstant.Team_Reply_Timeout){
			return Status.Team_TargetRole_Busy;
		}
		targRole.setTeamApplyTime(currTime);
		TeamShieldType shieldType = TeamShieldType.Open;
		if(isInviteor){//组队邀请时，对方是否拒绝
			shieldType = TeamShieldType.get(targRole.getSystemSet().getTeamInvite());
		}else{//入队申请时，对方是否拒绝
			RoleInstance leader = (RoleInstance) targRole.getTeam().getLeader();
			shieldType = TeamShieldType.get(leader.getSystemSet().getTeamApply());
		}
		if(TeamShieldType.Open == shieldType){
			//正常组队，发送组队弹板消息
			this.notifyBuilTeam(role, targRole, isInviteor);
		}else if(TeamShieldType.Auto == shieldType){
			//自动组队，不需要给目标发组队弹板，直接组队
			return this.buildTeam(targRole, role);
		}
		return Status.SUCCESS;
	}
	
	/**
	 * 组队检测地图类型
	 * @param role
	 * @return
	 */
	private Status checkMapType(RoleInstance role){
		String mapId = role.getMapId();
		sacred.alliance.magic.app.map.Map map = null;
		if(mapId != null){
			 map = GameContext.getMapApp().getMap(mapId);
			 int logicType = map.getMapConfig().getLogictype();
			 if(MapLogicType.isCopyType((byte)logicType)){
				 return Status.Team_Map_Not_Support;
			 }
		 }
		return Status.SUCCESS;
	}
	
	/**
	 * 发送组队弹板消息
	 * @param role
	 * @param targRole
	 * @param isInviteor
	 */
	private void notifyBuilTeam(RoleInstance role, RoleInstance targRole, boolean isInviteor){
		if(isInviteor){
			Team team = role.getTeam();
			byte currNum = 1;
			byte maxNum = 4;
			if(null != team){
				currNum = (byte) team.getPlayerNum();
				maxNum = (byte) team.getMaxPlayerNum();
			}
			C1311_TeamInviteNotifyMessage inviteMsg = new C1311_TeamInviteNotifyMessage();
			inviteMsg.setRoleId(role.getIntRoleId());
			inviteMsg.setRoleName(role.getRoleName());
			inviteMsg.setRoleLevel((byte) role.getLevel());
			inviteMsg.setCurrNum(currNum);
			inviteMsg.setMaxNum(maxNum);
			targRole.getBehavior().sendMessage(inviteMsg);
		}else{
			C1301_TeamApplyNotifyMessage applyMsg = new C1301_TeamApplyNotifyMessage();
			applyMsg.setRoleId(role.getIntRoleId());
			applyMsg.setRoleName(role.getRoleName());
			applyMsg.setRoleLevel((byte) role.getLevel());
			AbstractRole leader = targRole.getTeam().getLeader();
			if(null == leader){
				leader = targRole;
			}
			leader.getBehavior().sendMessage(applyMsg);
		}
	}
	
	public Status buildTeam(RoleInstance role, RoleInstance invitorRole){
		if (null == role || null == invitorRole) {
			return Status.Team_Role_Not_Online;
		}
		//判断当前地图是否允许组队
		MapInstance roleMap = role.getMapInstance();
		MapInstance invitorRoleMap = invitorRole.getMapInstance();
		if(null == roleMap 
				|| null == invitorRoleMap
				|| !roleMap.canBuildTeam()
				|| !invitorRoleMap.canBuildTeam()){
			return Status.Team_Map_Not_Support ;
		}
		//在同一队伍中，组队失败
		if(this.isInSameTeam(role, invitorRole)){
			return Status.Team_In_SameTeam;
		}
		//双方都有队伍，不是同一个队伍，组队失败
		if(this.hasTeam(role) && this.hasTeam(invitorRole)){
			return Status.Team_In_differentTeam;
		}
		Team team = role.getTeam();
		Team invitorTeam = invitorRole.getTeam();
		//申请入队（自己有队伍，邀请者没有队伍）
		if(this.hasTeam(role) && !this.hasTeam(invitorRole)){
			if(team.isFull()){
				return Status.Team_Full;
			}
			team.memberJoin(invitorRole);
			return Status.SUCCESS;
		}
		//邀请组队（自己没队伍，邀请者有队伍）
		if(!this.hasTeam(role) && this.hasTeam(invitorRole)){
			if(invitorTeam.isFull()){
				return Status.Team_Full;
			}
			invitorTeam.memberJoin(role);
			return Status.SUCCESS;
		}
		//邀请组队（邀请者和自己都没有队伍，发起邀请的人是队长）
		if(null != invitorTeam){
			invitorTeam.memberJoin(role);
		}else{
			team = new PlayerTeam(invitorRole,role);
			//new PlayerTeam里面已经同步
			//team.syschDataNotify();
		}
		return Status.SUCCESS;
	}
	
	private boolean hasTeam(RoleInstance role){
		return null != role.getTeam() && role.getTeam().getPlayerNum() > 1;
	}
	

	@Override
	public boolean isInSameTeam(AbstractRole role1, AbstractRole role2) {
		if (null == role1||null == role2) {
			return false;
		}
		
		if(role1.getRoleType() != RoleType.PLAYER || role2.getRoleType() != RoleType.PLAYER) {
			return false;
		}
		
		RoleInstance player1 = (RoleInstance) role1;
		RoleInstance player2 = (RoleInstance) role1;
		if (null == player1.getTeam()||null == player2.getTeam()) {
			return false;
		}
		if (player1.getTeam().getTeamId().equals(player2.getTeam().getTeamId())){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取同一地图的活着的组队玩家数量
	 * @param role
	 * @return
	 */
	public List<AbstractRole> getTeamMembersInSameMap(RoleInstance role) {
		List<AbstractRole> result = new ArrayList<AbstractRole>();
		MapInstance roleMapInstance = role.getMapInstance();
		if(!role.hasTeam() || null == roleMapInstance){
	       if(role.isDeath()){
	    	   return result ;
	       }
	       result.add(role);
	       return result;
		}
		
		MapInstance currentRoleMap = null ;
		for(AbstractRole current : role.getTeam().getMembers()){
			currentRoleMap = current.getMapInstance();
			if(null == currentRoleMap){
				continue ;
			}
			if(!currentRoleMap.getInstanceId().equals(roleMapInstance.getInstanceId())){
				continue;
			}
			if(current.isDeath()){
				continue ;
			}
			result.add(current);
		}
		currentRoleMap = null ;
		return result;
	}
	
	/**
	 * 组队系数
	 * @param teamMembers
	 * @return
	 */
	public double teamCoefficient(int teamMembers) {
		double coefficient = 1 - teamMembers * (teamMembers * 0.01 + 0.02) - 0.05;
		return coefficient;
	}
	
	/** 加入下线队伍缓存 **/
	public void addOfflineCache(AbstractRole role) {
		Team team = ((RoleInstance)role).getTeam();
		if (team == null) {
			return;
		}
		teamCache.put(role.getRoleId(), team);
	}

	public Cache<String, Team> getTeamCache() {
		return teamCache;
	}
	public void setTeamCache(Cache<String, Team> teamCache) {
		this.teamCache = teamCache;
	}
	private RoleInstance getRoleInstance(String roleId){
		try{
			return GameContext.getUserRoleApp().getRoleByRoleId(roleId);
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	@Override
	public void offline(RoleInstance role) {
		Team team = role.getTeam();
		if(null == team){
			return ;
		}
		team.memberLeave(role, LeaveTeam.offline);
	}
}
