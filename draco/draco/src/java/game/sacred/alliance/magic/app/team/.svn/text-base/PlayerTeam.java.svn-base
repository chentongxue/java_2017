package sacred.alliance.magic.app.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.message.item.TeamDataItem;
import com.game.draco.message.push.C1304_TeamChangCaptainNotifyMessage;
import com.game.draco.message.push.C1303_TeamLeaveNotifyMessage;
import com.game.draco.message.response.C1300_TeamDataRespMessage;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public class PlayerTeam extends Team {
	
	public static int MAX_MEMBERS_NUM = 4;

	public PlayerTeam(RoleInstance leader, RoleInstance recipient) {
		super(leader,recipient);
		this.teamType = TeamType.PLAY_TEAM;
		//创建队伍时，在队伍频道广播队员加入队伍
		this.sendMessage(recipient.getRoleName() + "加入队伍");
		//同步退伍数据
		this.syschDataNotify();
	}
	
	public PlayerTeam(RoleInstance leader) {
		super(leader);
		this.teamType = TeamType.PLAY_TEAM;
	}
	
	/*** 不建议使用此构造方法 
	 * 此方法仅供子类实现多态构造函数 **/
	public PlayerTeam(){
		
	}
	
	
	/** 离开队伍(离队者，离队类型) **/
	@Override
	public void memberLeave(AbstractRole role, LeaveTeam reason){
		switch(reason){
		case apply:
			this.leaveTeam(role,reason.getType());
			break;
		case kicked:
			if(this.isLeader(role)){
				return ;
			}
			this.leaveTeam(role,reason.getType());
			break;
		case offline:
			this.offlineLeaveTeam(role,reason.getType());
			break;
		case exit:
			this.exitLeaveTeam(role);
			break;
		}
		//通知势力变化
		this.notifyForceRelation(role);
		this.syncSocialAttribute(role);
		this.syncSocialAttribute();
	}
	
	// 离队逻辑
	protected void leaveTeam(AbstractRole role, int type){
		//this.memberLeaveMessage(role);
		this.sendTeamLeaveNotify(role,type);
		if(isLeader(role)){
			this.leader = this.getNewLeader();
		}
		this.members.remove(role.getRoleId());
		this.offlineMembers.remove(role.getRoleId());
		RoleInstance player = (RoleInstance)role;
		if(canDisband()){
			enforceDisband();
			player.setTeam(null);
			return ;
		}
		this.syschDataNotify();
		//this.syschDataNotify(role);
		player.setTeam(null);
	}
	
	// 下线逻辑
	private void offlineLeaveTeam(AbstractRole role,int type){
		this.sendTeamLeaveNotify(role,type);
		if(isLeader(role)){
			this.leader = this.getNewLeader();
		}
		this.members.remove(role.getRoleId());
		this.offlineMembers.put(role.getRoleId(), role);
		if(this.canDisband()){
			leader = null;
		}
		this.addOfflineCache(role);
		this.syschDataNotify();
	}
	
	// 退出逻辑，队伍缓存过期调用
	private void exitLeaveTeam(AbstractRole role){
		if(role != null){
			offlineMembers.remove(role.getRoleId());
			if(this.canDisband()){
				this.enforceDisband();
				return ;
			}
			this.syschDataNotify();
		}
	}
	

	//是否能更换队长
	@Override
	public boolean canChangeLeader(){
		return true;
	}
	
	
	/** 更换队长 **/
	public boolean changeLeader(AbstractRole role){
		super.changeLeader(role);
		this.changeLeaderNotify();
		return true;
	}
	
	
	public boolean isFull() {
		return getPlayerNum()>= MAX_MEMBERS_NUM;
	}
	
	
	/** 加入队伍 * */
	public void memberJoin(AbstractRole role){
		super.memberJoin(role);
		//memberAddBuff();//加buff
		this.syschDataNotify();
	}
	
	
	@Override
	public void memberJoins(AbstractRole leader,AbstractRole role){
		super.memberJoins(leader, role);
		//memberAddBuff();//加buff
		this.syschDataNotify();
	}


	@Override
	public boolean enforceDisband() {
		if(null != leader){
			this.memberLeaveMessage(leader);
		}
		return true;
	}

	@Override
	public boolean canDisband() {
		return getPlayerNum() < 2;
	}

	@Override
	public boolean canLeave() {
		// TODO 自动生成方法存根
		return true;
	}
	
	/** 主推同步协议 **/
	public void syschDataNotify() {
		
		Collection<AbstractRole> list = this.getRuleMembers();
		Collection<AbstractRole> offlineList = this.getOfflineMembers().values();
		C1300_TeamDataRespMessage dataMsg = new C1300_TeamDataRespMessage();
		
		for (AbstractRole absRole : list) {
			dataMsg.getList().add(convertTeamDataItem(absRole, 1));
		}
		
		if(!Util.isEmpty(offlineList)){
			for(AbstractRole absRole : offlineList){
				dataMsg.getList().add(convertTeamDataItem(absRole, 0));
			}
		}
		
		Collection<AbstractRole> members = this.getMembers();
		/*for (Iterator<AbstractRole> it = members.iterator(); it.hasNext();) {
			GameContext.getMessageCenter().send("",
					((RoleInstance) it.next()).getUserId(), dataMsg);
			//不能这么发送同步消息，角色登录的时候还未加入到onlineCenter，他自己收不到消息
		}*/
		for(AbstractRole role : members){
			role.getBehavior().sendMessage(dataMsg);
		}
	}
	
	/** 队长放前面，客户端认为第一位为队长 **/
	protected List<AbstractRole> getRuleMembers() {
		List<AbstractRole> list = new ArrayList<AbstractRole>();
		AbstractRole leader = this.getLeader();
		int leaderId =  0 ;
		if(null != leader){
			leaderId = leader.getIntRoleId();
			list.add(leader);
		}
		for (Iterator<AbstractRole> it = members.values().iterator(); it
				.hasNext();) {
			AbstractRole absRole = it.next();
			if(absRole.getIntRoleId() == leaderId){
				continue ;
			}
			list.add(absRole);
		}
		return list;
	}
	
	
	/** 单推同步消息 **/
	public void syschDataNotify(AbstractRole role){
		Collection<AbstractRole> list = this.getRuleMembers();
		Collection<AbstractRole> offlineList = this.getOfflineMembers().values();
		C1300_TeamDataRespMessage dataMsg = new C1300_TeamDataRespMessage();

		for (AbstractRole absRole : list) {
			dataMsg.getList().add(convertTeamDataItem(absRole, 1));
		}
		
		if(!Util.isEmpty(offlineList)){
			for(AbstractRole absRole : offlineList){
				dataMsg.getList().add(convertTeamDataItem(absRole, 0));
			}
		}
		
		GameContext.getMessageCenter().send("",
				((RoleInstance) role).getUserId(), dataMsg);
	}
	
	
	/** 队员离队消息 **/
	protected void memberLeaveMessage(AbstractRole role){
		GameContext.getMessageCenter().send("",
				((RoleInstance) role).getUserId(), new C1300_TeamDataRespMessage());
	
	}
	
	/** 封装队员信息 **/
	private TeamDataItem convertTeamDataItem(AbstractRole absRole, int isOnline) {
		TeamDataItem dataItem = new TeamDataItem();
		dataItem.setCarrer((byte) ((RoleInstance) absRole).getCareer());
		dataItem.setCurrHp(absRole.getCurHP());
		dataItem.setCurrMp(absRole.getCurMP());
		dataItem.setMaxHp(absRole.getMaxHP());
		dataItem.setMaxMp(absRole.getMaxMP());
		dataItem.setRoleId(absRole.getIntRoleId());
		dataItem.setRoleName(((RoleInstance) absRole).getRoleName());
		dataItem.setLevel((byte) absRole.getLevel());
		dataItem.setOnline((byte) isOnline);
		return dataItem;
	}

	/** 更换队长通知 **/
	private void changeLeaderNotify() {
		C1304_TeamChangCaptainNotifyMessage pushMsg = new C1304_TeamChangCaptainNotifyMessage();
		RoleInstance role = (RoleInstance)this.leader;
		pushMsg.setRoleName(role.getRoleName());
		this.broadcast(role.getRoleId(), pushMsg, true);
	}
	
	private void addOfflineCache(AbstractRole role){
		GameContext.getTeamApp().addOfflineCache(role);
	}
	
	/** 离队消息 **/
	protected void sendTeamLeaveNotify(AbstractRole role, int leave){
		C1303_TeamLeaveNotifyMessage pushMsg = new C1303_TeamLeaveNotifyMessage();
		pushMsg.setType((byte)leave);
		String roleName = ((RoleInstance)role).getRoleName();
		pushMsg.setRoleName(roleName);
		broadcast(roleName, pushMsg, true);
	}
	
	@Override
	public int getMaxPlayerNum() {
		return MAX_MEMBERS_NUM;
	}

	@Override
	public void addFriendIntimate(int intimate) {
		try {
			if(intimate <= 0){
				return;
			}
			if(this.getPlayerNum() <= 1){
				return;
			}
			//记录已经添加过的roleId，避免重复
			Set<String> finishSet = new HashSet<String>();
			for(AbstractRole member1 : this.members.values()){
				if(null == member1){
					continue;
				}
				String member1Id = member1.getRoleId();
				for(AbstractRole member2 : this.members.values()){
					if(null == member2){
						continue;
					}
					//不再同地图
					if(!member1.getMapId().equals(member2.getMapId())){
						continue;
					}
					String member2Id = member2.getRoleId();
					//已经增加过了
					if(finishSet.contains(member1Id) && finishSet.contains(member2Id)){
						continue;
					}
					finishSet.add(member1Id);
					finishSet.add(member2Id);
					GameContext.getSocialApp().changeFriendIntimate((RoleInstance)member1, (RoleInstance)member2, intimate);
				}
			}
		} catch (RuntimeException e) {
			this.logger.error("PlayerTeam.addFriendIntimate error: ", e);
		}
	}
}
