package sacred.alliance.magic.app.arena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;

import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class ArenaMatch {
	private final static Logger logger = LoggerFactory.getLogger(ArenaMatch.class);
	private static AtomicInteger KEY_GEN = new AtomicInteger(0);
	
	@Getter private String key ;
	private ArenaMatchStatus status = ArenaMatchStatus.common ;
	private ApplyInfo team1 ;
	private ApplyInfo team2 ;
	private ArenaConfig config ;
	//选择yes的角色列表
	private Set<String> selectYes = new HashSet<String>();
	//算经验的时候要用到等级,因为用户有可能下线所以在此将等级保存起来
	private Map<String,Integer> rolelevels = new HashMap<String,Integer>();
	//!!!很重要在创建Arena容器的时候需要将值赋进来
	private String containerId = "" ;
	//发送依然保持排队队列的角色名
	private String sendKeepRoleId = "" ;
	private long createTime = 0 ;
	//已经死亡列表
	//private Set<String> deathMembers = new HashSet<String>();
	
	
	private ArenaMatch(){
		this.key = String.valueOf(KEY_GEN.incrementAndGet());
		this.createTime = System.currentTimeMillis();
	}
	
	/*public boolean isLiveMember(String roleId){
		if(null == deathMembers){
			return false ;
		}
		return !deathMembers.contains(roleId);
	}
	public void addDeathMember(String roleId){
		if(null == roleId){
			return ;
		}
		this.deathMembers.add(roleId);
	}*/
	
	public boolean isTimeout(){
		return (System.currentTimeMillis() - this.createTime) >(config.getMaxBattleTime() + 30)*1000;
	}
	
	public boolean isSameTeam(String role1,String role2){
		boolean v1 = this.team1.isMember(role1);
		boolean v2 = this.team1.isMember(role2);
		if(v1 && v2){
			return true ;
		}
		if(!v1 && !v2){
			return true ;
		}
		return false ;
	}
	
	public void cancelAll(){
		if(null != team1){
			this.team1.cancelAll();
		}
		if(null != team2){
			this.team2.cancelAll();
		}
		destory();
	}
	
	public ApplyInfo getOtherTeam(ApplyInfo info){
		ApplyInfo team = this.team1 ;
		if(info.getId() == team1.getId()){
			team = team2 ;
		}
		return team ;
	}
	
	public boolean teamAllCancel(ApplyInfo team){
		return 0 == team.getAppliers().size();
	}
	/**
	 * 判断伍是否已经全部做出了选择
	 * @param info
	 * @return
	 */
	public boolean teamAllSelected(ApplyInfo team){
		for(String roleId : team.getAppliers()){
			if(!this.selectYes.contains(roleId)){
				return false ;
			}
		}
		return true ;
	}
	
	/**
	 * 判断是否全部都已经作出选择
	 * @return
	 */
	public boolean allSelected(){
		boolean v = this.teamAllSelected(this.team1);
		if(!v){
			return false ;
		}
		return this.teamAllSelected(this.team2);
	}
	
	public void matchYes(String roleId){
		if(null == roleId){
			return ;
		}
		this.selectYes.add(roleId);
	}
	
	public void matchNo(String roleId){
		if(null == roleId){
			return ;
		}
		if(this.selectYes.contains(roleId)){
			//用户已经选择了确定,这情况一般不会发生
			return ;
		}
		//这里就不判断是在哪队伍了,为了方便双方都删除
		this.team1.getAppliers().remove(roleId);
		this.team2.getAppliers().remove(roleId);
	}
	
	
	
	public void destory(){
		this.status = ArenaMatchStatus.destory;
		GameContext.getArenaApp().removeArenaMatch(this.key);
		this.team1 = null ;
		this.team2 = null ;
		this.config = null ;
		if(null != this.selectYes){
			this.selectYes.clear();
			this.selectYes = null ;
		}
		if(null != this.rolelevels){
			this.rolelevels.clear();
			this.rolelevels = null ;
		}
		/*if(null != this.deathMembers){
			this.deathMembers.clear();
			this.deathMembers = null ;
		}*/
		this.sendKeepRoleId = null ;
		this.key = null ;
	}
	/**
	 * 最初参加战斗人的数目
	 * @return
	 */
	public int getTeam1BattleMembersNum(){
		return this.team1.getAppliers().size();
	}
	
	public int getTeam2BattleMembersNum(){
		return this.team2.getAppliers().size();
	}
	
	public int getTeamBattleMembersNum(String roleId){
		return this.team1.isMember(roleId)?
				this.getTeam1BattleMembersNum():this.getTeam2BattleMembersNum();
	}
	
	public boolean isTeam1(ApplyInfo team){
		if(null == team){
			return false ;
		}
		return team.getId() == this.team1.getId();
	}
	
	public boolean isTeam2(ApplyInfo team){
		if(null == team){
			return false ;
		}
		return team.getId() == this.team2.getId();
	}
	
	public int getApplierSize(){
		return team1.getAppliers().size() + team2.getAppliers().size();
	}
	
	
	public static ArenaMatch create(ApplyInfo team1,ApplyInfo team2,ArenaConfig config){
		try {
			// 判断2队里面是否有相同的角色,如果有则创建失败
			Set<String> members = new HashSet<String>();
			members.addAll(team1.getAppliers());
			members.addAll(team2.getAppliers());
			int size = members.size();
			members.clear();
			members = null ;
			if (size != (team1.getAppliers().size() + team2
					.getAppliers().size())) {
				return null;
			}
			ArenaMatch match = new ArenaMatch();
			match.setTeam1(team1);
			match.setTeam2(team2);
			team1.setMatch(match);
			team2.setMatch(match);
			match.setConfig(config);
			// 添加到管理器
			GameContext.getArenaApp().addArenaMatch(match);
			return match;
		}catch(Exception ex){
			logger.error("",ex);
		}
		return null ;
	}
	
	
	public void sendMatchConfirm(){
		this.sendMatchConfirm(this.team1);
		this.sendMatchConfirm(this.team2);
		GameContext.getArenaApp().addMatchTimeoutListener(this);
	}
	
	/**
	 * 发送匹配成功通知消息,要求用户确认进入
	 * @param team
	 */
	private void sendMatchConfirm(ApplyInfo team){
		if(null == team){
			return ;
		}
		if(null == team.getAppliers()){
			return ;
		}
		//二次确认消息
		C0007_ConfirmationNotifyMessage message = null ;
		for(String roleId : team.getAppliers()){
			try {
				RoleInstance role = GameContext.getOnlineCenter()
						.getRoleInstanceByRoleId(roleId);
				if (null == role) {
					continue;
				}
				if (null == message) {
					message = new C0007_ConfirmationNotifyMessage();
					message.setAffirmCmdId(ArenaApp.ARENA_MATCH_CONFIRM_CMD);
					message.setAffirmParam(ArenaApp.SELECTED);
					message.setCancelCmdId(ArenaApp.ARENA_MATCH_CONFIRM_CMD);
					message.setCancelParam(ArenaApp.UN_SELECTED);
					message.setInfo(GameContext.getI18n().getText(TextId.ARENA_MATCH_SUCCESS_ENTER));
					message.setTime((byte)ArenaApp.DEFAULT_MATCH_CONFIRM_TIME);
					message.setTimeoutCmdId(ArenaApp.ARENA_MATCH_CONFIRM_TIME_OVER_CMD);
					//默认确认
					message.setTimeoutParam(ArenaApp.SELECTED);
				}
				role.getBehavior().sendMessage(message);
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
	}
	
	/**
	 * 将角色传入擂台赛地图
	 * @param role
	 */
	public void sendEnterArenaMap(){
		sendEnterArenaMap(this.team1,config.getPoint1());
		sendEnterArenaMap(this.team2,config.getPoint2());
		this.status = ArenaMatchStatus.entermap ;
	}
	
	private void sendEnterArenaMap(ApplyInfo team,Point point){
		for(String roleId : team.getAppliers()){
			try {
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
				if(null == role){
					return ;
				}
				role.getBehavior().changeMap(point);
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}
	
	
	public boolean isTeam1(String roleId){
		if(Util.isEmpty(roleId)){
			return false ;
		}
		return team1.getAppliers().contains(roleId);
	}
}
