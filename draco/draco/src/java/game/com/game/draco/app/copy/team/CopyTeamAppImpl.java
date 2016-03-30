package com.game.draco.app.copy.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.copy.CopyConfig;
import com.game.draco.app.copy.CopyType;
import com.game.draco.message.internal.C0066_CopyTeamMatchInternalMessage;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.team.PlayerTeam;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyTeamAppImpl implements CopyTeamApp{

	private final static Logger logger = LoggerFactory.getLogger(CopyTeamApp.class);
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	private Thread matchThread = null ;
	private boolean matchRunning = false ;
	private boolean matchNow = false ;
	//private Set<String> applyMapSet = null ;
	/**
	 * 报名信息
	 */
	private Map<String,ApplyInfo> applyMap = new ConcurrentHashMap<String,ApplyInfo>();
	
	@Override
	public void setArgs(Object arg0) {
		
	}
	
	private int getMatchInterval(){
		return GameContext.getParasConfig().getCopyTeamMatchSecond()*1000;
	}
	
	/*private void loadCanApplyMaps(){
		String fileName = XlsSheetNameType.copy_team_apply_map.getXlsName();
		String sheetName = XlsSheetNameType.copy_team_apply_map.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.applyMapSet = XlsPojoUtil.sheetToStringSet(sourceFile, sheetName);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}
	}*/

	@Override
	public void start() {
		this.matchRunning = true ;
		//启动系统匹配线程
		matchThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(matchRunning){
					try {
						Thread.sleep(getMatchInterval());
					} catch (Exception e) {
					}
					try {
						sendSystemMatchReq();
					}catch(Exception ex){
						logger.error("",ex);
					}
				}
			}
		});
		matchThread.setName("match thread for copy team ");
		//matchThread.setDaemon(true);
		matchThread.start();
	}
	
	private void sendSystemMatchReq(){
		if(this.matchNow){
			//匹配逻辑未结束
			return ;
		}
		this.matchNow = true ;
		C0066_CopyTeamMatchInternalMessage reqMsg = new C0066_CopyTeamMatchInternalMessage();
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}

	@Override
	public void stop() {
		this.matchRunning = false ;
	}
	
	private boolean canMatch(ApplyInfo applyInfo){
		//1.判断成员是否发生变化
		int size = applyInfo.getApplyRoles().size();
		for (AbstractRole role : applyInfo.getApplyRoles()) {
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
				return false ;
			}
			RoleInstance player = (RoleInstance)role;
			Team team = player.getTeam();
			if(null == team){
				return false ;
			}
			if(!team.getTeamId().equals(applyInfo.getTeamId())){
				return false ;
			}
			if(team.getPlayerNum() != size ){
				return false ;
			}
		}
		return true ;
	}
	
	private void sendMatchFail(ApplyInfo info) {
		for (AbstractRole role : info.getApplyRoles()) {
			if (!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())) {
				continue;
			}
			RoleInstance player = (RoleInstance)role;
			Team team = player.getTeam();
			if (null == team || !team.getTeamId().equals(info.getTeamId())) {
				return;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Copy_Team,
					ChannelType.System, Status.Team_Copy_MATCH_FAIL_BY_TEAM_MEMBER_CHANGE.getTips(), null,
					role);
		}

	}
	
	private CopyTeamResult canApply(RoleInstance role,short copyId){
		//判断副本是否存在,判断当前队伍中的人是否都符合条件
		CopyTeamResult result = new CopyTeamResult();
		CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		if(null == config){
			result.setInfo(Status.Team_Copy_Param_Copy_Null.getTips());
			return result ;
		}
		if(config.getCopyType() != CopyType.team){
			result.setInfo(Status.Team_Copy_Not_Allow.getTips());
			return result ;
		}
		Team team = role.getTeam();
		if(team.getPlayerNum() >= config.getMaxEnterCount()){
			result.setInfo(Status.Team_Copy_Member_Full.getTips());
			return result ;
		}
		ApplyInfo applyInfo = this.getApplyInfo(role);
		if(null != applyInfo){
			short oldCopyId = applyInfo.getCopyId();
			if(oldCopyId == copyId){
				result.setInfo(Status.Team_Copy_IsIn.getTips());
				return result ;
			}
			CopyConfig oldConfig = GameContext.getCopyLogicApp().getCopyConfig(oldCopyId);
			result.setInfo(Status.Team_Copy_In_Match.getTips().replace(Wildcard.CopyName, oldConfig.getCopyName()));
			return result ;
		}
		for(AbstractRole m : team.getMembers()){
			RoleInstance member = (RoleInstance)m ;
			//判断是否离线
			if(!GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())){
				result.setInfo(Status.Team_Copy_Role_Not_Online.getTips().replace(Wildcard.Role_Name, member.getRoleName()));
				result.setNotifyTeam(true);
				return result ;
			}
			//判断是否处于擂台赛报名
			sacred.alliance.magic.app.arena.ApplyInfo arenaApply = GameContext.getArenaApp().getApplyInfo(member.getRoleId());
			if(null != arenaApply){
				result.setInfo(Status.Team_Copy_Role_In_Arena.getTips().replace(Wildcard.Role_Name, member.getRoleName()));
				result.setNotifyTeam(true);
				return result ;
			}
			String mapId = member.getMapId();
			MapInstance mapInstance = member.getMapInstance();
			if(null != mapInstance){
				mapId = mapInstance.getMap().getMapId();
			}
			//判断是否在可报名地图
			if(null == mapId){// || !this.applyMapSet.contains(mapId)
				result.setInfo(Status.Team_Copy_Map_Not_Exist.getTips().replace(Wildcard.Role_Name, member.getRoleName()));
				result.setNotifyTeam(true);
				return result ;
			}
			Result condResult = config.enterCondition(member);
			if(!condResult.isSuccess()){
				result.setInfo(member.getRoleName() +  condResult.getInfo() + Status.Team_Copy_Not_Match.getTips());
				result.setNotifyTeam(true);
				return result ;
			}
		}
		result.success();
		result.setInfo(Status.Team_Copy_Enter_Copy_Match.getTips().replace(Wildcard.CopyName, config.getCopyName()));
		result.setNotifyTeam(true);
		return result ;
	}

	@Override
	public CopyTeamResult apply(RoleInstance role,short copyId) {
		CopyTeamResult result = new CopyTeamResult();
		Team team = role.getTeam();
		if(null == team){
			team = new PlayerTeam(role);
		}
		//判断是否符合条件报名
		result = this.canApply(role,copyId);
		if(!result.isSuccess()){
			return result ;
		}
		//构建报名信息类
		ApplyInfo info = new ApplyInfo(role.getTeam(),copyId);
		this.applyMap.put(team.getTeamId(), info);
		return result;
	}

	@Override
	public CopyTeamResult cancel(RoleInstance role) {
		CopyTeamResult result = new CopyTeamResult();
		ApplyInfo applyInfo = this.getApplyInfo(role);
		if(null == applyInfo){
			result.setInfo(Status.Team_Copy_Role_Not_Apply.getTips());
			return result ;
		}
		Team team = role.getTeam();
		if(!team.isLeader(role)){
			result.setInfo(Status.Team_Copy_Team_Can_Apply.getTips());
			return result ;
		}
		this.removeApplyInfo(team.getTeamId());
		result.success();
		CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(applyInfo.getCopyId());
		String copyName = "" ;
		if(null != config){
			copyName = config.getCopyName();
		}
		result.setInfo(Status.Team_Copy_Team_Leave_Match.getTips().replace(Wildcard.CopyName, copyName));
		//通知其他队员离开
		applyInfo.notifyLeave();
		return result ;
	}

	@Override
	public ApplyInfo getApplyInfo(RoleInstance role) {
		Team team = role.getTeam();
		if(null == team){
			return null ;
		}
		return this.applyMap.get(team.getTeamId());
	}

	@Override
	public boolean inApplyStatus(RoleInstance role) {
		return null != this.getApplyInfo(role);
	}
	
	
	
	@Override
	public void systemMatch() {
		try {
			int size = this.applyMap.size();
			if(size <=0){
				return ;
			}
			Map<Short, MatchResult> groupMap = new HashMap<Short, MatchResult>();
			List<ApplyInfo> list = new ArrayList<ApplyInfo>();
			list.addAll(this.applyMap.values());
			for (ApplyInfo info : list) {
				try {
					if (!this.canMatch(info)) {
						// 移除
						this.removeApplyInfo(info.getTeamId());
						//通知其他队员离开
						info.notifyLeave();
						// 发送匹配失败消息
						this.sendMatchFail(info);
						continue;
					}
				} catch (Exception ex) {
					logger.error("", ex);
				}
				// 分组
				short copyId = info.getCopyId();
				MatchResult match = groupMap.get(copyId);
				if (null == match) {
					match = new MatchResult(copyId);
					groupMap.put(copyId, match);
				}
				match.addApplyInfo(info);
			}
			for(MatchResult result : groupMap.values()){
				result.match();
			}
		}catch(Exception ex){
			logger.error("",ex);
		}finally{
			this.matchNow = false ;
		}
		
	}

	@Override
	public void removeApplyInfo(String teamId) {
		if(null == teamId){
			return ;
		}
		 this.applyMap.remove(teamId);
		 return ;
	}
	
}
