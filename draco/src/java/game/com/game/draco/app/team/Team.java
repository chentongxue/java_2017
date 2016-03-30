package com.game.draco.app.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.message.push.C0401_ForceTypeNotifyMessage;

public abstract class Team implements java.io.Serializable {
	
	protected final Logger logger = LoggerFactory.getLogger(Team.class);
	protected String teamId; // 队伍ID
	protected TeamType teamType; //队伍类型
	protected AbstractRole leader;// 队长
	protected Map<String, AbstractRole> members = new HashMap<String, AbstractRole>();//保存队伍人员
	protected Map<String, AbstractRole> offlineMembers = new HashMap<String, AbstractRole>();//保存离队人员
	
	protected static AtomicInteger instanceIdGenerator = new AtomicInteger();
	private byte[] teamLock = new byte[0];
	/** 副本容器信息：KEY=副本ID,VALUE=副本容器ID */
	private Map<Short,String> copyContainerMap = new HashMap<Short,String>();
	private Object copyContainerLock = new byte[0];//副本容器锁
	
	public Team(RoleInstance leader, RoleInstance recipient){
		this.leader = leader;
		this.teamId = ""+instanceIdGenerator.incrementAndGet();
		this.members.put(leader.getRoleId(), leader);
		this.members.put(recipient.getRoleId(), recipient);
		leader.setTeam(this);
		recipient.setTeam(this);
		//同步势力关系
		this.notifyForceRelation(leader);
		this.syncSocialAttribute();
	}
	
	public Team(RoleInstance leader){
		this.leader = leader;
		this.members.put(leader.getRoleId(), leader);
		leader.setTeam(this);
		this.teamId = ""+instanceIdGenerator.incrementAndGet();
	}

	public Team(){
		
	}
	
	/** 队伍是否满员 **/
	public abstract boolean isFull() ;
	
	/** 能否离开队伍 **/
	public abstract boolean canLeave();
	
	/** 能否解散队伍 **/
	public abstract boolean canDisband();
	
	/** 能否更换队长 **/
	public abstract boolean canChangeLeader();
	
	/** 返回队伍类型 **/
	public TeamType getTeamType(){
		return this.teamType;
	}

	/** 队伍ID **/
	public String getTeamId(){
		return this.teamId;
	}
	
	/** 队伍最大人数 */
	public abstract int getMaxPlayerNum();
	
	
	/** 不发消息添加成员 **//*
	public void addMembers(AbstractRole role){
		if(role == null){
			return ;
		}
		members.put(role.getRoleId(), role);
	}*/
	
	/** 加入队伍 * */
	public void memberJoin(AbstractRole role) {
		if (this.isFull()) {
			return;
		}
		RoleInstance roleInstance = (RoleInstance) role;
		Team team = roleInstance.getTeam();
		if (null != team) {
			team.memberLeave(role, LeaveTeam.join);
		}
		this.members.put(role.getRoleId(), role);
		((RoleInstance)role).setTeam(this);
		if (leader == null) {
			this.leader = role;
		}
		this.sendMessage(GameContext.getI18n().messageFormat(TextId.Team_Join_Tips, ((RoleInstance) role).getRoleName()));
		this.notifyForceRelation(role);
		this.syncSocialAttribute();
	}
	
	/** 加入队伍 * */
	public void memberJoins(AbstractRole leader, AbstractRole role) {
		this.members.put(role.getRoleId(), role);
		((RoleInstance)role).setTeam(this);
		if (leader == null) {
			this.leader = role;
		}
		this.sendMessage(GameContext.getI18n().messageFormat(TextId.Team_Join_Tips, ((RoleInstance) role).getRoleName()));
		this.notifyForceRelation(leader);
		this.syncSocialAttribute();
	}
	
	/** 离开队伍(离队者，离队类型) **/
	public abstract void memberLeave(AbstractRole role, LeaveTeam reason);
	
	/** 新队长 **/
	public AbstractRole getNewLeader() {
		for (AbstractRole role : this.members.values()) {
			if (!isLeader(role)) {
				return role;
			}
		}
		return null;
	}
	
	/** 是否为队长 **/
	public boolean isLeader(AbstractRole role){
		if(role == null){
			return false;
		}
		if(this.leader == null){
			this.leader = role;
			return true;
		}
		
		if(role.getIntRoleId() == this.leader.getIntRoleId()){
			return true;
		}
		return false;
	}
	
	/** 获取队伍成员 **/
	public Collection<AbstractRole> getMembers(){
		return this.members.values();
	}
	
	/** 队伍的活人 **/
	public List<AbstractRole> getHealthPlayers() {
		List<AbstractRole> list = new ArrayList<AbstractRole>();
		for (AbstractRole item : this.members.values()) {
			if (!item.isDeath() && !this.offlineMembers.containsKey(item.getRoleId())) {
				list.add(item);
			}
		}
		return list;
	}
	
	/** 解散队伍 **/
	public boolean disband(){
		if(canDisband()){
			return enforceDisband();
		}
		return false;
	}
	
	/** 强制解散队伍 **/
	public abstract boolean enforceDisband();
	
	/** 返回队长 **/
	public AbstractRole getLeader(){
		return this.leader;
	}
	
	/** 当前队伍人数 ***/
	public int getPlayerNum(){
		return this.members.size() + this.offlineMembers.size();
	}
	
	/** 队伍在线人数 **/
	public int getOnlinePlayerNum(){
		return this.members.size();
	}
	
	/** 主推同步协议 **/
	public abstract void syschDataNotify();
	/** 单推同步消息 **/
	public abstract void syschDataNotify(AbstractRole role);
	
	/** 聊天栏提示 **/
	public void sendMessage(String message){
		try{
			GameContext.getChatApp().sendSysMessage(ChatSysName.Team, ChannelType.Team, message, null, this);
		}catch(Exception e){
			this.logger.error("Team.sendMessage error: ", e);
		}
	}
	
	public void broadcast(String sender, Message message,boolean includeSender) {
		if(null == message){
			return ;
		}
		for (AbstractRole item : this.members.values()) {
			if (!includeSender && item.getRoleId().equals(sender)) {
				continue;
			}
			GameContext.getMessageCenter().send("",
					((RoleInstance) item).getUserId(), message);
		}
	}
	
	/**
	 * 通知势力关系变化
	 * @param role
	 */
	protected void notifyForceRelation(AbstractRole role){
		try {
			MapInstance mapInstance = role.getMapInstance();
			//不是PVP地图不需要同步
			if(null == mapInstance || !mapInstance.getMap().getMapConfig().isPvpMap()){
				return;
			}
			for(AbstractRole item : this.members.values()){
				MapInstance instance = item.getMapInstance();
				//不在同一张地图不需要同步
				if(null == instance || !instance.getInstanceId().equals(mapInstance.getInstanceId())){
					continue;
				}
				byte forceRelation = item.getForceRelation(role).getType();
				//给前队友发自己的势力
				C0401_ForceTypeNotifyMessage msg1 = new C0401_ForceTypeNotifyMessage();
				msg1.setRoleId(role.getIntRoleId());
				msg1.setForceRelation(forceRelation);
				item.getBehavior().sendMessage(msg1);
				//给自己发前队友的势力
				C0401_ForceTypeNotifyMessage msg2 = new C0401_ForceTypeNotifyMessage();
				msg2.setRoleId(item.getIntRoleId());
				msg2.setForceRelation(forceRelation);
				role.getBehavior().sendMessage(msg2);
			}
		} catch (RuntimeException e) {
			this.logger.error("Team.notifyForceRelation error: ", e);
		}
	}
	
	/** 更换队长 **/
	public boolean changeLeader(AbstractRole role){
		if (!canChangeLeader()) {
			return false;
		}
		if (null == role) {
			return false;
		}
		this.leader = role;
		return true;
	}
	
	/**
	 * 获取副本容器实例ID
	 * @param copyId 副本ID
	 * @return
	 */
	public String getCopyContainerId(short copyId){
		synchronized(this.copyContainerLock){
			return this.copyContainerMap.get(copyId);
		}
	}
	
	/**
	 * 增加副本容器信息
	 * @param copyId 副本ID
	 * @param containerId 副本容器ID
	 */
	public void addCopyContainer(short copyId, String containerId){
		synchronized(this.copyContainerLock){
			this.copyContainerMap.put(copyId, containerId);
		}
	}
	
	/**
	 * 删除副本容器实例信息
	 * @param copyId 副本ID
	 * @return
	 */
	public void removeCopyContainer(short copyId,String containerId){
		synchronized(this.copyContainerLock){
			if(null == containerId ) {
				 this.copyContainerMap.remove(copyId);
				 return ;
			}
			String existId = this.copyContainerMap.get(copyId);
			if(null == existId || containerId.equals(existId) ){
				this.copyContainerMap.remove(copyId);
				return ;
			}
		}
	}
	
	public void setLeader(AbstractRole leader) {
		this.leader = leader;
	}
	public void setMember(Map<String, AbstractRole> members) {
		this.members = members;
	}
	public Map<String, AbstractRole> getMember(){
		return this.members;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public void setTeamType(TeamType teamType) {
		this.teamType = teamType;
	}
	public Map<String, AbstractRole> getOfflineMembers() {
		return offlineMembers;
	}
	
	public byte[] getTeamLock() {
		return teamLock;
	}
	
	/**
	 * 同步社交系统的属性
	 * 好友亲密度影响的属性
	 */
	protected void syncSocialAttribute(){
		for(AbstractRole role : this.members.values()){
			GameContext.getSocialApp().syncIntimateAttribute((RoleInstance)role);
		}
	}
	
	/**
	 * 同步社交系统的属性
	 * 好友亲密度影响的属性
	 * @param role
	 */
	protected void syncSocialAttribute(AbstractRole role){
		//亲密度影响属性
		GameContext.getSocialApp().syncIntimateAttribute((RoleInstance)role);
	}
	
	/**
	 * 队伍内同地图好友增加亲密度
	 * @param intimate
	 */
	public abstract void addFriendIntimate(int intimate);
	
	/**
	 * 判断队员是指定门派的队伍
	 * @param factionId 门派ID
	 * @return
	 */
	public boolean isFactionTeam(String factionId){
		try {
			if(null == factionId){
				return false;
			}
			for(AbstractRole member : this.getMembers()){
				if(null == member){
					continue;
				}
				RoleInstance mRole = (RoleInstance) member;
				String mFactionId = mRole.getUnionId();
				//没有门派或者不是同一个门派
				if(null == mFactionId || !mFactionId.equals(factionId)){
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".isFactionTeam error: ", e);
			return false;
		}
	}
	
	/**
	 * 队伍中是否包含某个角色
	 * @param roleId 角色ID
	 * @return
	 */
	public boolean contains(String roleId){
		try {
			for(AbstractRole member : this.getMembers()){
				if(null == member){
					continue;
				}
				if(member.getRoleId().equals(roleId)){
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".contains error: ", e);
			return false;
		}
	}
	
	public abstract void expiredLeaveTeam(String roleId)  ;
}
