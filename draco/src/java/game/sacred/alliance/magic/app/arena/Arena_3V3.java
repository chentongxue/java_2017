package sacred.alliance.magic.app.arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.team.LeaveTeam;
import com.game.draco.app.team.Team;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class Arena_3V3 extends Arena{
	private final static int DEFAULT_MATCH_NUM = 6 ;
	@Override
	protected Comparator<ApplyInfo> createComparator(){
		return new ApplyInfoComparator(this.config);
	}
	
	@Override
	protected MatchResult matchRule(List<ApplyInfo> toDoList){
		if(Util.isEmpty(toDoList)){
			return null ;
		}
		
		if(!roleEnough(toDoList)){
			MatchResult result = new MatchResult();
			for(ApplyInfo info : toDoList){
				if(info.filterMatch()){
					continue ;
				}
				result.addRemain(info);
			}
			return result ;
		}
		
		List<ApplyInfo> _3v3List = new ArrayList<ApplyInfo>();
		List<ApplyInfo> _2v2List = new ArrayList<ApplyInfo>();
		List<ApplyInfo> _1v1List = new ArrayList<ApplyInfo>();
		
		//对等级,周胜率进行降序排列
		Collections.sort(toDoList,this.comparator);
		for(ApplyInfo aInfo : toDoList){
			if(aInfo.filterMatch()){
				continue ;
			}
			if(aInfo.getAppliers().size() == 3){
				_3v3List.add(aInfo);
				continue ;
			}
			if(aInfo.getAppliers().size() == 2){
				_2v2List.add(aInfo);
				continue ;
			}
			_1v1List.add(aInfo);
		}
		
		List<ApplyInfo> matchList = new ArrayList<ApplyInfo>();
		matchList.addAll(_3v3List);
		matchList.addAll(makeTeam(_2v2List, _1v1List));
		
		return _3v3MatchRule(matchList);
	}
	
	private boolean roleEnough(List<ApplyInfo> toDoList){
		int total = 0;
		for(ApplyInfo info : toDoList){
			total += info.getAppliers().size();
			if(total >= DEFAULT_MATCH_NUM){
				return true;
			}
		}
		return false;
	}
	
	private List<ApplyInfo> makeTeam(List<ApplyInfo> _2v2List, List<ApplyInfo> _1v1List){
		List<ApplyInfo> list = new ArrayList<ApplyInfo>();
		for(Iterator<ApplyInfo> _2v2It = _2v2List.iterator(); _2v2It.hasNext();){
			ApplyInfo _2v2Obj = _2v2It.next();
			for(Iterator<ApplyInfo> _1v1It = _1v1List.iterator(); _1v1It.hasNext();){
				ApplyInfo _1v1Obj = _1v1It.next();
				_2v2Obj.addApplyRole(_1v1Obj.getLeaderId());
				int _2v2Score = _2v2Obj.getScore();
				if(_1v1Obj.getScore() > _2v2Score){
					_2v2Obj.setScore(_2v2Score);
				}
				_1v1It.remove();
				manager.getAllRoleApplyInfo().put(_1v1Obj.getLeaderId(), _2v2Obj);
				break;
			}
			list.add(_2v2Obj);
		}
		
		ApplyInfo tempApplyInfo = null;
		for(Iterator<ApplyInfo> _1v1It = _1v1List.iterator(); _1v1It.hasNext();){
			if(null == tempApplyInfo && _1v1List.size() < 3){
				//不足以组成一个队伍。直接退出
				break;
			}
			ApplyInfo _1v1Obj = _1v1It.next();
			if(null == tempApplyInfo){
				tempApplyInfo = _1v1Obj;
				continue;
			}
			if(tempApplyInfo.getAppliers().size() < 3){
				tempApplyInfo.addApplyRole(_1v1Obj.getLeaderId());
				int score = tempApplyInfo.getScore();
				if(_1v1Obj.getScore() > score){
					tempApplyInfo.setScore(score);
				}
				manager.getAllRoleApplyInfo().put(_1v1Obj.getLeaderId(), tempApplyInfo);
				_1v1It.remove();
			}
			//组成3人组了，变成一个队伍
			if(tempApplyInfo.getAppliers().size() >= 3){
				tempApplyInfo = null;
			}
		}
		if(null != tempApplyInfo){
			list.add(tempApplyInfo);
		}
		list.addAll(_1v1List);
		return list;
	}
	
	private MatchResult _3v3MatchRule(List<ApplyInfo> toDoList){
		if(Util.isEmpty(toDoList)){
			return null ;
		}
		//对等级,周胜率进行降序排列
		Collections.sort(toDoList,this.comparator);
		MatchResult result = new MatchResult();
		int toDoSize = toDoList.size();
		for(int index=0;index<toDoSize;){
			int step = 1 ;
			int nextIndex = 0 ;
			ApplyInfo current = toDoList.get(index);
			if(current.filterMatch()){
				index++;
				continue ;
			}
			if(current.getAppliers().size() < 3){
				result.addRemain(current);
				index++;
				continue;
			}
			while(true){
				nextIndex = index + step ;
				if(nextIndex>=toDoSize){
					result.addRemain(current);
					return result ;
				}
				ApplyInfo next = toDoList.get(nextIndex);
				if(next.filterMatch()){
					step ++ ;
					continue ;
				}
				if(next.getAppliers().size() < 3){
					result.addRemain(next);
					step ++;
					continue;
				}
				if(!this.inLevelRange(current, next)){
					result.addRemain(current);
					index = nextIndex ;
					break ;
				}
				//匹配成功
				ArenaMatch match = ArenaMatch.create(current, next, config);
				if(null != match){
					result.addSuccess(match);
				}else{
					current.cancelAll();
					next.cancelAll();
				}
				index = nextIndex + 1 ;
				break ;
			}
		}
		return result;
	}
	
	protected ArenaResult applyCancelCheck(RoleInstance role) {
		//查看是否已经报名
		ArenaResult result = super.applyCancelCheck(role);
		if(!result.isSuccess()){
			return result ;
		}
		result.failure();
		//判断是否是队长
		Team team = role.getTeam();
		if(null == team){
			ApplyInfo info = GameContext.getArenaApp().getApplyInfo(role.getRoleId());
			if(info.getAppliers().size()>1){
				result.setInfo(this.getText(TextId.ARENA_NVN_LEVEL_TEAM_NOT_CANCEL));
				return result ;
			}
		}else {
			if(!team.isLeader(role)){
				result.setInfo(this.getText(TextId.ARENA_NVN_NOT_LEADER_NOT_CANCEL));
				return result ;
			}
		}
		result.success();
		return result ;
	}
	
	@Override
	protected ArenaResult applyCheck(RoleInstance role) {
		ArenaResult result = new ArenaResult();
		if(!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
			result.setInfo(this.getText(TextId.ARENA_APPLY_ROLE_NOT_ONLINE));
			return result ;
		}
		Collection<AbstractRole> roleList = new ArrayList<AbstractRole>();
		Team team = role.getTeam();
		if(null == team){
			roleList.add(role);
		}else{
			//判断是否队长
			if(!team.isLeader(role)){
				result.setInfo(this.getText(TextId.ARENA_NVN_NOT_LEADER_NOT_APPLY));
				return result ;
			}
			Map<String, AbstractRole> offlines = team.getOfflineMembers();
			if(null != offlines && offlines.size()>0){
				result.setInfo(this.getText(TextId.ARENA_NVN_HAS_NOT_ONLINE_NOT_APPLY));
				return result ;
			}
			if(team.getMembers().size() > 3){
				result.setInfo(this.getText(TextId.ARENA_3V3_TEAM_ROLE_MAX));
				return result ;
			}
			roleList.addAll(team.getMembers());
		}
		if(null == roleList || 0 == roleList.size()){
			result.setInfo(this.getText(TextId.ARENA_NVN_TEAM_STATUS_NOT_APPLY));
			return result ;
		}
		for(AbstractRole member : roleList){
			RoleInstance roleMember = (RoleInstance)member;
			if(!active.isSuitLevel(roleMember)){
				String str = messageFormat(TextId.ARENA_NVN_MEMBER_LEVEL_NOT_MEET, roleMember.getRoleName());
				result.setInfo(str);
				return result ;
			}
			//查看是否已经报名
			ApplyInfo applyInfo = manager.getApplyInfo(roleMember.getRoleId());
			if(null != applyInfo){
				if(applyInfo.getActiveId() == this.active.getId()){
					String str = messageFormat(TextId.ARENA_NVN_MEMBER_HAS_APPLY, roleMember.getRoleName());
					result.setInfo(str);
					if(role.getRoleId().equals(roleMember.getRoleId())){
						//自己当前已经是报名状态
						result.setCurrentApplyState(ApplyState.had_apply);
					}
				}else{
					String str = this.messageFormat(TextId.ARENA_NVN_MEMBER_HAS_APPLY_OTHER, roleMember.getRoleName());
					result.setInfo(str);
				}
				return result ;
			}
			if(GameContext.getCopyTeamApp().inApplyStatus(roleMember)){
				String str = messageFormat(TextId.ARENA_NVN_MEMBER_NOT_ALLOW, roleMember.getRoleName());
				result.setInfo(str);
				return result ;
			}
			//判断战斗力
			if(null != this.config && 
					roleMember.getBattleScore() < this.config.getBattleScore()){
				String str = messageFormat(TextId.ARENA_NVN_MEMBER_BATTLE_SCORE_NOT_ENOUGH, roleMember.getRoleName(), 
						this.config.getBattleScore());
				result.setInfo(str);
				return result ;
			}
		}
		result.setRoleList(roleList);
		result.success();
		return result;
	}
	

	@Override
	protected ApplyInfo createApplyInfo(RoleInstance role,Object context) {
		ApplyInfo info = new ApplyInfo();
		info.setCreateDate(System.currentTimeMillis());
		info.setActiveId(this.active.getId());
		info.setLeaderId(role.getRoleId());
		Collection<AbstractRole> roleList = (Collection<AbstractRole>)context ;
		int score = 0 ;
		int maxLevel = 0 ;
		for(AbstractRole actor: roleList){
			RoleInstance roleActor = (RoleInstance)actor ;
			info.addApplyRole(actor.getRoleId());
			int actorScore = (int)roleActor.getRoleArena().getArenaLevel3v3();
			if(actorScore > score){
				score = actorScore ;
			}
			if(roleActor.getLevel()>maxLevel){
				maxLevel = roleActor.getLevel();
			}
		}
		info.setLevel(maxLevel);
		info.setScore(score);
		info.setTeamRoleNum(roleList.size());
		return info ;
	}


	@Override
	protected ArenaResult matchNo(String roleId, ApplyInfo info) {
		ArenaResult result = new ArenaResult();
		ArenaMatch match = info.getMatch();
		match.matchNo(roleId);
		//将role的报名信息删除
		GameContext.getArenaApp().removeApplyInfo(roleId);
		//如果有队伍则离开队伍
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null != role){
			Team team = role.getTeam();
			if(null != team && team.getMembers().size()>1){
				team.memberLeave(role, LeaveTeam.apply);
				//队长离队,重新设置leaderId
				if(role.getRoleId().equals(info.getLeaderId())){
					info.setLeaderId(team.getLeader().getRoleId());
				}
			}
		}
		//操作成功
		result.success();
		//判断自己方是否已经全部取消
		if(match.teamAllCancel(info)){
			ApplyInfo otherTeam = match.getOtherTeam(info);
			if(match.teamAllCancel(otherTeam)){
				//双方都全部取消
				match.cancelAll();
				return result ;
			}
			//通知对方是否继续排队
			otherTeam.sendApplyKeepConfirm(match);
			return result ;
		}
		//判断双方是否已经全部作出选择,如果是则传入地图
		if(!match.allSelected()){
			return result;
		}
		//将对方都传入地图
		match.sendEnterArenaMap();
		return result;
	}

	@Override
	protected ArenaResult matchYes(String roleId, ApplyInfo info) {
		ArenaResult result = new ArenaResult();
		ArenaMatch match = info.getMatch();
		match.matchYes(roleId);
		result.success();
		//判断对方是否已经全部取消,如果是给本队发送是否继续排队协议
		if(match.teamAllCancel(match.getOtherTeam(info))){
			info.sendApplyKeepConfirm(match);
			return result ;
		}
		//判断双方是否已经全部作出选择,如果是则传入地图
		if(!match.allSelected()){
			return result;
		}
		//将对方都传入地图
		match.sendEnterArenaMap();
		return result;
	}

	@Override
	public boolean filterMatch(ApplyInfo info) {
		boolean value = super.filterMatch(info);
		if(value){
			//说明队长已经下线,成员发生了变化
			this.sendSystemCancelApply(info);
			return true ;
		}
		if(info.getAppliers().size() > 3){
			this.sendSystemCancelApply(info);
			return true ;
		}
		//判断成员是否发生了变化,如果发生变化过滤掉
		RoleInstance leader = GameContext.getOnlineCenter().getRoleInstanceByRoleId(info.getLeaderId());
		if(null == leader){
			//说明队长已经下线,成员发生了变化
			this.sendSystemCancelApply(info);
			return true ;
		}
		Collection<AbstractRole> list = new ArrayList<AbstractRole>();
		Team team = leader.getTeam();
		if(null == team){
			//可以个人报名
			list.add(leader);
		}else{
			list = team.getMember().values();
		}
		if(list.size() != info.getTeamRoleNum()){
			this.sendSystemCancelApply(info);
			return true ;
		}
		for(AbstractRole role : list){
			if(!info.isMember(role.getRoleId())){
				this.sendSystemCancelApply(info);
				return true ;
			}
		}
		if(null != team){
			//因为有可能更换了队长
			info.setLeaderId(team.getLeader().getRoleId());
		}
		return false ;
	}
	
	private void sendSystemCancelApply(ApplyInfo info){
		for(String roleId : info.getAppliers()){
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(null == role){
				continue ;
			}
			String msg = this.getText(TextId.ARENA_NVN_MSG) ;
			GameContext.getChatApp().sendSysMessage(ChatSysName.Arena, ChannelType.System, msg, null, role);
		}
	}

	@Override
	public ArenaType getArenaType() {
		return ArenaType._3V3;
	}
	
	@Override
	public ArenaResult apply(RoleInstance role){
		//判断活动是否开启
		ArenaResult result = new ArenaResult();
		if(!active.isTimeOpen()){
			result.setInfo(this.getText(TextId.ARENA_ACTIVE_NOT_OPEN));
			return result ;
		}
		result = this.applyCheck(role);
		if(!result.isSuccess()){
			return result ;
		}
		//构建报名者信息
		ApplyInfo info = this.createApplyInfo(role,result.getRoleList());
		//放入等待匹配队列
		this.applyList.add(info);
		//加入到所有报名集合中(一用户同一时刻只能参加一种擂台赛)
		for(String roleId : info.getAppliers()){
			manager.getAllRoleApplyInfo().put(roleId, info);
			sendApplyMsg(roleId);
		}
		//可以将result中的roleList释放
		result.releaseRoleList();
		//当前已经报名状态
		result.setCurrentApplyState(ApplyState.had_apply);
		return result ;
	}
	
	private void sendApplyMsg(String roleId){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			return;
		}
		Team team = role.getTeam();
		if(null == team){
			return;
		}
		if(team.isLeader(role)){
			return;
		}
		role.getBehavior().sendMessage(new C0003_TipNotifyMessage(
				this.getText(TextId.ARENA_3V3_TEAM_APPLY)));
	}
	
	private String getText(String textId){
		return com.game.draco.GameContext.getI18n().getText(textId);
	}
	
	private String messageFormat(String textId, Object ... args){
		return GameContext.getI18n().messageFormat(textId, args) ;
	}
	
}
