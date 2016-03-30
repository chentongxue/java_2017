package sacred.alliance.magic.vo.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.game.draco.app.dailyplay.DailyPlayType;
import sacred.alliance.magic.app.active.angelchest.MapRefreshStatus;
import sacred.alliance.magic.app.chest.ChestRefreshInfo;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.survival.config.SurvivalBase;
import com.game.draco.app.survival.vo.SurvivalApplyInfo;
import com.game.draco.app.team.Team;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.item.RoleNameItem;
import com.game.draco.message.push.C0284_SurvivalNotifyMessage;
import com.game.draco.message.response.C2372_ActiveAngelChestNewRespMessage;
import com.game.draco.message.response.C2374_ActiveAngelChestResetRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MapSurvivalBattleInstance extends MapInstance{
	private final LoopCount mapStateLoop = new LoopCount(1000);//1秒
	private final LoopCount mapRefBoxLoop = new LoopCount(6000);//1分钟
	private MapRefreshStatus status = null ;
	//结束书剑
	private long overTime;
	//等待时间
	private long waitTime;
	private SurvivalApplyInfo survivalApplyInfo;
	//<队伍ID，<成员ID，队伍状态>>
	private ConcurrentMap<String,ConcurrentMap<Integer,Boolean>> memberStateMap = Maps.newConcurrentMap();
	//<队伍ID，<成员ID，离开状态>>
	private ConcurrentMap<String,ConcurrentMap<Integer,Boolean>> memberExitStateMap = Maps.newConcurrentMap();
	private java.util.Map<String,Boolean> teamStateMap = Maps.newConcurrentMap();
	private Set<String> teamRewardSet = Sets.newConcurrentHashSet();
	private long sendRewardTime = System.currentTimeMillis();
	private final static int KICK_ROLE_WHEN_OVER_TIME = 5 * 1000;
	private int refreshIndex = 0;
	private Date refreshDate ;//开始时间(宝箱)
	private MapBoxSupport mapBox ;
	private MapState mapState = MapState.ready;

	public enum MapState {
		ready, // 准备阶段
		pk, // 准备阶段
		game_over, // gameover状态：发结算面板、奖励、记录结束时间
		kick_role, // 踢人状态：超过踢人保护时间后将角色从地图移除
		wait_destory, // 地图销毁阶段：销毁地图
		;
	}
	
	public long getOverTime() {
		return overTime;
	}

	public void setOverTime(long overTime) {
		this.overTime = overTime;
	}

	public SurvivalApplyInfo getSurvivalApplyInfo() {
		return survivalApplyInfo;
	}

	public void setSurvivalApplyInfo(SurvivalApplyInfo survivalApplyInfo) {
		this.survivalApplyInfo = survivalApplyInfo;
	}

	public MapSurvivalBattleInstance(Map map) {
		super(map);
		mapBox = new MapBoxSupport(this,OutputConsumeType.arena_Top_Reward,
				OutputConsumeType.arena_Top_Reward_Mail,
				GameContext.getI18n().getText(TextId.ARENA_TOP_MAIL_TITILE),
				MailSendRoleType.ArenaTop) ;
		String mapId = this.getMap().getMapId();
		MapRefreshStatus status = GameContext.getAngelChestApp()
				.getRefreshStatus(this.getMap().getMapId(), lineId);
		if (null == status) {
			status = new MapRefreshStatus();
			status.setMapId(mapId);
			status.setLineId(lineId);
			status.initState();
			GameContext.getAngelChestApp().putRefreshStatus(status);
			// 其他操作都放到主循环中进行
		}
		this.status = status;
	}
	
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		RoleInstance roleInstance = (RoleInstance) role;
		Point targetPoint = roleInstance.getCopyBeforePoint();
		if (null != targetPoint) {
			role.setMapId(targetPoint.getMapid());
			role.setMapX(targetPoint.getX());
			role.setMapY(targetPoint.getY());
		}
		if(roleInstance.getTeam() != null){
			java.util.Map<Integer,Boolean> map = Util.getIfAbsent(roleInstance.getTeam().getTeamId(), memberExitStateMap);
			map.put(role.getIntRoleId(), true);
		}
		// 满血满蓝
		this.perfectBody(role);
		exit(role);
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, RoleInstance target) {
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
		if(role.getTeam() == null || target.getTeam() == null){
			return ForceRelation.enemy;
		}
		if(role.getTeam().getTeamId().equals(target.getTeam().getTeamId())){
			return ForceRelation.friend;
		}
		if(role.inState(StateType.soul) || target.inState(StateType.soul)){
			return ForceRelation.neutral;
		}
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(RoleInstance role, NpcInstance target) {
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, NpcInstance target) {
		return ForceRelation.enemy;
	}
	
	private Point getBornPoint(){
		RoleBorn roleBorn = GameContext.getRoleBornApp().getRoleBorn();
		return roleBorn.getBornPoint();
	}
	
	private void exit(AbstractRole role){
				
		//删除buff
		RoleBornGuide guide = GameContext.getRoleBornApp()
				.getRoleBornGuide();
		if(guide == null){
			return;
		}
		if (guide.getBuffId() > 0) {
			GameContext.getUserBuffApp().delBuffStat(role, guide.getBuffId(), false);
		}
		
		if(guide.getGiveHeroId() >0){
			GameContext.getHeroApp().deleteHeroBySystem((RoleInstance)role, guide.getGiveHeroId());
		}
	}
	
	/**
	 * 将所有玩家传出当前地图
	 */
	private void kickAllRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			this.kickRole(role);
		}
	}
	
	@Override
	protected void enter(AbstractRole role) {
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		super.enter(role);
		RoleInstance r = (RoleInstance)role;
		//发送倒计时信息
		notifyCountDown(r);
		//上定身buff
		addBuff(r);
		//初始化状态
		changeTeamMap(r,false);
		//初始化队伍状态为胜利
		if(!teamStateMap.containsKey(r.getTeam().getTeamId())){
			teamStateMap.put(r.getTeam().getTeamId(), true);
		}
		
		if(null != this.mapBox){
			mapBox.enter(role, this.getActiveStatus(), this.getNextOpenTime());
		}
		//活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes(r, 1, DailyPlayType.survival_battle, "");
	}
	
	private void addBuff(RoleInstance role){
		SurvivalBase base = GameContext.getSurvivalApp().getSurvivalBase();
		Buff buff = GameContext.getBuffApp().getBuff(base.getBuffId());
		if(null == buff) {
			return;
		}
		BuffStat stat = new BuffStat(buff,1,buff.getIntervalTime(1));
		stat.setBuffId(buff.getBuffId());
		stat.setOwner(role);
		stat.setCaster(role);
		stat.setRemainTime(buff.getPersistTime());
		Date now = new Date();
		stat.setLastExecuteTime(now.getTime());
		stat.setCreateTime(now);
		stat.setLayer((short)1);
		role.addBuffStat(stat);
	}
	
	private void notifyCountDown(RoleInstance role){
		SurvivalBase base = GameContext.getSurvivalApp().getSurvivalBase();
		C0284_SurvivalNotifyMessage msg = new C0284_SurvivalNotifyMessage();
		msg.setWaitTime(base.getWaitTime());
		List<RoleNameItem> roleNameList = Lists.newArrayList();
		Iterator<Team> iter = survivalApplyInfo.getTeamList().iterator();
		
		String teamId = "";
		if(role.getTeam() != null){
			teamId = role.getTeam().getTeamId();
		}
		
		while(iter.hasNext()){
			byte flag = 0;
			Team team = iter.next();
			if(!teamId.equals(team.getTeamId())){
				flag = 1;
			}
			for(AbstractRole member : team.getMembers()){
				RoleNameItem roleName = new RoleNameItem();
				roleName.setId(flag);
				roleName.setName(member.getRoleName());
				roleNameList.add(roleName);
			}
		}
		msg.setRoleNameList(roleNameList);
		role.getBehavior().sendMessage(msg);
	}
	
	@Override
	public void updateSub()  throws ServiceException{
		super.updateSub();
		
		if (this.mapStateLoop.isReachCycle()) {
			if(mapState == MapState.ready){
				if(System.currentTimeMillis() < waitTime){
					return;
				}
				activeOpen();
				mapState = MapState.pk;
			}
			//检查队伍空的角色
			validTeamNull();
			
			if (mapState == MapState.pk) {
				//判断是否活动结束
				boolean isActiveOver = isActiveOver();
				if(!isActiveOver){
					//检查如果队伍成员是否全部退出
					validTeamExitAll();
					//检查如果队伍成员全部灭亡 发参与奖励
					validTeamKillAll();
					//检查是否有获胜队伍
					validTeamWin();
				}
			}
			if(mapState == MapState.game_over){
				sendRewardTime = System.currentTimeMillis();
				this.mapState = MapState.kick_role;
				return;
			}
			if(mapState == MapState.kick_role){
				if (System.currentTimeMillis() - sendRewardTime > KICK_ROLE_WHEN_OVER_TIME) {
					// 奖励发送完毕后5秒钟后开始t人
					over();
					this.mapState = MapState.wait_destory;
				}
			}
			if (mapState == MapState.wait_destory) {
				this.destroy();
			}
		}
		if(mapRefBoxLoop.isReachCycle()){
			//刷宝箱
			activeRuning();
		}
	}
	
	private void validTeamNull(){
		Collection<RoleInstance> roleList = this.getRoleList();
		for(RoleInstance r : roleList){
			if(r.getTeam() == null){
				kickRole(r);
			}
		}
	}
	
	@Override
	public void doEvent(RoleInstance role,MapInstanceEvent event){
		if(null == this.mapBox){
			return ;
		}
		this.mapBox.doEvent(role, event);
	}
	
	private boolean isActiveOver(){
		if(System.currentTimeMillis() > getOverTime()){
			//活动结束还没分出胜负 全部发参与奖励
			joinRreward();
			mapState = MapState.game_over;
			return true;
		}
		return false;
	}
	
	private void over(){
		cleanBox();
		kickAllRole();
		GameContext.getSurvivalBattleApp().pollApplyInfo(survivalApplyInfo);
	}
	
	private void activeRuning(){
		if(refreshDate == null){
			return ;
		}
		this.refresh() ;
	}
	
	private void activeOpen(){
		this.refreshIndex = 0 ;
		this.refreshDate = new Date() ;
	}
	
	/**
	 * 参与奖
	 */
	private void joinRreward(){
		Iterator<Team> iter = survivalApplyInfo.getTeamList().iterator();
		while(iter.hasNext()){
			Team team = iter.next();
			//过滤掉之前已发过参与奖的队伍
			if(teamRewardSet.contains(team.getTeamId())){
				continue;
			}
			GameContext.getSurvivalBattleApp().gameOver(team, ChallengeResultType.Lose,this.getInstanceId());
			//发参与奖
			GameContext.getSurvivalBattleApp().sendSurvivalBattleReward(team,ChallengeResultType.Lose,this.getInstanceId());
		}
	}
	
	protected void kickRole(RoleInstance role){
		try{
			GameContext.getUserMapApp().changeMap(role,this.getBornPoint());
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	public boolean canDestroy() {
		return (this.getRoleCount() == 0);
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}

	/**
	 * 角色死亡在不同地图的处理差异
	 * @param attacker 攻击者
	 * @param victim 受害者
	 */
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if(victim.getRoleType() != RoleType.PLAYER){
			return;
		}
		
		RoleInstance target = (RoleInstance)victim;
		if(target.getTeam() == null){
			kickRole(target);
			return;
		}
		
		changeTeamMap(target,true);
		
	}
	
	private void changeTeamMap(RoleInstance role,boolean isDead){
		java.util.Map<Integer,Boolean> map = Util.getIfAbsent(role.getTeam().getTeamId(), memberStateMap);
		map.put(role.getIntRoleId(), isDead);
	}
	
	private void validTeamKillAll(){
		if(survivalApplyInfo == null){
			return;
		}
		Iterator<Team> iter = survivalApplyInfo.getTeamList().iterator();
		while(iter.hasNext()){
			boolean flag = true;
			Team team = iter.next();
			java.util.Map<Integer,Boolean> map = memberStateMap.get(team.getTeamId());
			if(Util.isEmpty(map)){
				continue;
			}
			for(AbstractRole member : team.getMembers()){
				if(member == null){
					continue;
				}
				if(!GameContext.getOnlineCenter().isOnlineByRoleId(member.getRoleId())){
					continue;
				}
				if(!member.getMapInstance().getInstanceId().equals(this.getInstanceId())){
					continue;
				}
				if(!map.containsKey(member.getIntRoleId())){
					flag = false;
					continue;
				}
				if(!map.get(member.getIntRoleId())){
					flag = false;
				}
			}
			if(flag){
				if(teamRewardSet.contains(team.getTeamId())){
					continue;
				}
				teamRewardSet.add(team.getTeamId());
				teamStateMap.put(team.getTeamId(), false);
				//发参与奖
				GameContext.getSurvivalBattleApp().sendSurvivalBattleReward(team,ChallengeResultType.Lose,this.getInstanceId());
				GameContext.getSurvivalBattleApp().gameOver(team, ChallengeResultType.Lose,this.getInstanceId());
				iter.remove();
			}
		}
	}
	
	private void validTeamExitAll(){
		if(survivalApplyInfo == null){
			return;
		}
		Iterator<Team> iter = survivalApplyInfo.getTeamList().iterator();
		while(iter.hasNext()){
			boolean flag = true;
			Team team = iter.next();
			java.util.Map<Integer,Boolean> map = memberExitStateMap.get(team.getTeamId());
			if(Util.isEmpty(map)){
				continue;
			}
			for(AbstractRole member : team.getMembers()){
				if(member == null){
					continue;
				}
				if(!map.containsKey(member.getIntRoleId())){
					flag = false;	
					continue;
				}
				if(!map.get(member.getIntRoleId())){
					flag = false;
				}
			}
			if(flag){
				teamStateMap.remove(team.getTeamId());
				iter.remove();
			}
		}
	}
	
	/**
	 * 检查获胜队伍
	 */
	private void validTeamWin(){
		if(survivalApplyInfo == null){
			return;
		}
		//获胜队伍数量
		int winTeamNum = 0;
		Team winTeam = null;
		Iterator<Team> iter = survivalApplyInfo.getTeamList().iterator();
		while(iter.hasNext()){
			Team team = iter.next();
			if(Util.isEmpty(teamStateMap)){
				continue;
			}
			if(!teamStateMap.containsKey(team.getTeamId())){
				continue;
			}
			boolean result = teamStateMap.get(team.getTeamId());
			if(result){
				winTeamNum++;
				if(winTeamNum == 1){
					winTeam = team;
				}
			}
		}
		if(winTeamNum == 1){
			if(teamRewardSet.contains(winTeam.getTeamId())){
				return;
			}
			teamRewardSet.add(winTeam.getTeamId());
			GameContext.getSurvivalBattleApp().sendSurvivalBattleReward(winTeam,ChallengeResultType.Win,this.getInstanceId());
			//发大奖
			GameContext.getSurvivalBattleApp().gameOver(winTeam,ChallengeResultType.Win,this.getInstanceId());
			mapState = MapState.game_over;
			survivalApplyInfo.removeSurvivalApplyInfo(winTeam);
		}
	}
	
	@Override
	protected void deathLog(AbstractRole victim) {
		
	}

	@Override
	public void useGoods(int goodsId) {
		
	}

	@Override
	protected String createInstanceId() {
		return this.instanceId;
	}
	
	//刷新宝箱
	private void refresh(){
		List<ChestRefreshInfo> refreshList = mapBox.getRefreshList();
		if(null == refreshList || mapBox.getRefreshSize() == 0){
			return ;
		}
		if(refreshIndex >= refreshList.size()){
			return ;
		}
		int time = DateUtil.getSecondMargin(refreshDate);
		List<AngelChestInfoItem> thisChestList = null ;
		for(int i = refreshIndex ;i < refreshList.size() ; i++){
			ChestRefreshInfo cr = refreshList.get(i);
			if(time < cr.getRefreshTime()){
				break ;
			}
			try {
				List<AngelChestInfoItem> subList = mapBox.refresh(cr);
				if(!Util.isEmpty(subList)){
					if(null == thisChestList){
						thisChestList = new ArrayList<AngelChestInfoItem>();
					}
					thisChestList.addAll(subList);
				}
			}catch(Exception ex){
				logger.error("",ex);
			}
			refreshIndex ++;
		}
		if(Util.isEmpty(thisChestList)){
			return ;
		}
		//广播
		C2372_ActiveAngelChestNewRespMessage respMsg = new C2372_ActiveAngelChestNewRespMessage();
		respMsg.setNewList(thisChestList);
		this.broadcastMap(null, respMsg);
	}
	
	private void cleanBox() {
		try {
			if (null == this.mapBox) {
				return;
			}
			this.mapBox.cleanData();
			// 通知客户端
			this.broadcastMap(null, new C2374_ActiveAngelChestResetRespMessage());
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		if(null == mapBox){
			return ;
		}
		this.mapBox.damageTaken(attacker, victim, hurt);
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	@Override
	public void destroy() {
		super.destroy();
		if(!Util.isEmpty(memberStateMap)){
			memberStateMap.clear();
		}
		if(!Util.isEmpty(teamStateMap)){
			teamStateMap.clear();
		}
		if(!Util.isEmpty(teamRewardSet)){
			teamRewardSet.clear();
		}
		if(!Util.isEmpty(memberExitStateMap)){
			memberExitStateMap.clear();
		}
	}
	
	private byte getActiveStatus() {
		if(this.status.isCurLoopOver()){
			return (byte)0 ;
		}
		return (byte)1 ;
	}

	private String getNextOpenTime() {
		if(this.status.isCurLoopOver()){
			return this.status.getNextOpenTimeStr();
		}
		return "" ;
	}

}
