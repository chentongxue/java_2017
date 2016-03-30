package com.game.draco.app.camp.war.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.MapRemainTimeType;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapCopyInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.camp.war.vo.MatchInfo;
import com.game.draco.app.camp.war.vo.RoleRewardResult;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.base.AppType;
import com.game.draco.message.internal.C0085_CampWarAutoApplyInternalMessage;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
import com.game.draco.message.response.C0356_CampWarRolePkResultRespMessage;
import com.game.draco.message.response.C2001_RoleRebornRespMessage;

/**
 * 
 * 擂台赛地图实例
 *
 */
public class MapCampWarInstance extends MapCopyInstance {
	//战斗结束后多长时间踢人
	private final static int KICK_ROLE_WHEN_OVER_TIME = 5*1000 ;
	//等待对方进入地图最大时间
	private final static int MAX_WAIT_OTHER_TIME = 30*1000 ;
	//一秒钟判断一次输赢
	private LoopCount whoWinLoopCount = new LoopCount(2000);
	protected static enum MapState {
		create,
		enter,
		begin,
		over,
		kick_role,
		wait_destory,
		;
	}
	
	protected static enum BattleResult {
		win((byte)0),
		fail((byte)1),
		draw((byte)2),
		;
		private final byte type ;
		
		BattleResult(byte type){
			this.type = type ;
		}
		public byte getType() {
			return type;
		}
	}
	
	private MatchInfo match;
	private MapState mapState = MapState.create;
	private long createTime = System.currentTimeMillis();
	private AtomicBoolean winFlag = new AtomicBoolean(false);
	private AtomicBoolean first = new AtomicBoolean(false);
	private AbstractRole winRole = null ;
	private AbstractRole failRole = null ;
	private Active active;

	private Lock winLock = new ReentrantLock();
	private int maxBattleTime = 1000 ;
	private long sendRewardTime = System.currentTimeMillis();
	

	private void caclHp(AbstractRole role) {
		try {
			String roleId = role.getRoleId() ;
			RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
			Float hpRate = GameContext.getCampWarApp().getHeroHpRate(role.getRoleId(),hero.getHeroId());
			if(null != hpRate){
				int hp = (int)(role.getMaxHP()*hpRate) ;
				role.setCurHP(hp);
				role.getBehavior().notifyAttribute();
			}
			if (role.isDeath()) {
				// !!!!
				role.getHasSendDeathMsg().compareAndSet(true, false);
				// 告诉客户复活
				C2001_RoleRebornRespMessage respMsg = new C2001_RoleRebornRespMessage();
				respMsg.setType(RespTypeStatus.SUCCESS);
				role.getBehavior().sendMessage(respMsg);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	private void caclHpWhenExit(RoleInstance player){
		if(player.isDeath()){
			GameContext.getCampWarApp().clearAllHeroHpRate(player.getRoleId());
			this.perfectBody(player);
			return ;
		}
		if (!this.isOpenNow()) {
			// 活动结束
			return;
		}
		//计算HP百分比
		float rate = player.getCurHP()/(float)player.getMaxHP();
		RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(player.getRoleId());
		GameContext.getCampWarApp().addHeroHpRate(player.getRoleId(), hero.getHeroId(), rate);
	}
	
	 
	
	@Override
    protected void enter(AbstractRole role){
		if(null == role || RoleType.PLAYER != role.getRoleType()){
			return ;
		}
		RoleInstance player = (RoleInstance)role ;
		//计算当前hp
		this.caclHp(role);
		//发送倒计时消息
		this.sendRemainTime(player);
		//活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes(player, 1, DailyPlayType.camp_war, "");
		//通知参加了此活动
		GameContext.getCountApp().joinApp(player, AppType.camp_war);
	}
	
	
	protected void sendRemainTime(RoleInstance role){
		try {
			int lifeTime = (int)(System.currentTimeMillis() - this.createTime)/1000 ; 
			int clearTime = GameContext.getCampWarApp().getRoleBattleConfig().getClearBaffleTime();
			if(lifeTime >= clearTime){
				return ;
			}
			C0208_CopyRemainTimeNotifyMessage notifyMsg = new C0208_CopyRemainTimeNotifyMessage();
			notifyMsg.setType(MapRemainTimeType.Arean.getType());
			notifyMsg.setTime(clearTime -lifeTime );
			role.getBehavior().sendMessage(notifyMsg);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	
	@Override
	protected String createInstanceId() {
		instanceId = "campwar_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	public MapCampWarInstance(Map map, MatchInfo match) {
		super(map);
		this.match = match;
		this.maxBattleTime = GameContext.getCampWarApp().getRoleBattleConfig().getMaxBattleTime() ;
		Active active = GameContext.getCampWarApp().getCampWarActive();
		this.active = active;
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		RoleInstance player = (RoleInstance) role;
		if (!player.getJumpMap().get()) {
			// 非传输才需要获得
			// 获得出地图点
			Point point = role.getCopyBeforePoint();
			if (null != point) {
				role.setMapId(point.getMapid());
				role.setMapX(point.getX());
				role.setMapY(point.getY());
			}
		}
		// 将地图实例设置为null,否则判断胜负有误
		role.setMapInstance(null);
		// 此时也触发判断胜负
		//是否本次操作判断出的胜负
		this.whoWinWhenExit(player) ;
		//清除报名信息
		GameContext.getCampWarApp().removeApplyInfo(player.getRoleId());
		this.caclHpWhenExit(player);
		//push自动报名
		this.autoApply(player);
	}
	
	private boolean isOpenNow(){
		return null != this.active && this.active.isTimeOpen() ;
	}
	
	
	private void autoApply(RoleInstance player) {
		try {
			if (player.isOfflined()) {
				// 已经在下线 或者非自动报名
				return;
			}
			if (!this.isOpenNow()) {
				// 活动结束
				return;
			}
			C0085_CampWarAutoApplyInternalMessage reqMsg = new C0085_CampWarAutoApplyInternalMessage();
			reqMsg.setRole(player);
			reqMsg.setApply(true);
			player.getBehavior().addEvent(reqMsg);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	
	@Override
	public void addAbstractRole(AbstractRole role) {
		super.addAbstractRole(role);
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		//第一人进入后重装设置下创建时间
		if(first.compareAndSet(false, true)){
			this.mapState = MapState.enter ;
			createTime = System.currentTimeMillis();
		}
	}
	
	@Override
	protected void updateSub() throws ServiceException {
		// 此地图只需要更新用户行为
		super.updatePlayer();
		if (MapState.enter == this.mapState
				&& System.currentTimeMillis() - this.createTime > GameContext
						.getCampWarApp().getRoleBattleConfig()
						.getClearBaffleTime() * 1000) {
			// 进入准备阶段,此时清除障碍物
			this.clearBaffle();
			// 开始战斗了
			this.mapState = MapState.begin;
			return;
		}
		if (mapState == MapState.begin) {
			if (whoWinLoopCount.isReachCycle()) {
				// 判断输赢
				whoWinWhenLoop();
			}
			return;
		}
		//if (mapState == MapState.over) {
			//什么都不需要做
		//}
		if (mapState == MapState.kick_role) {
			if (System.currentTimeMillis() - sendRewardTime > KICK_ROLE_WHEN_OVER_TIME) {
				// 奖励发送完毕后10s钟后开始t人
				// 踢人
				this.clearRole();
				this.mapState = MapState.wait_destory;
			}
		}
	}
	
	public boolean canDestroy(){
		if ((mapState == MapState.wait_destory 
				|| (mapState == MapState.kick_role))
				&& !hasPlayer()) {
			return true;
		}
		if(!hasPlayer()
				&& this.isBattleTimeOver()){
			return true;
		}
		if(mustDestoryTimeOver()){
			return true ;
		}
		return false;
	}
	
	private void clearRole(){
		// 踢人
		try {
			if (null != this.getRoleList()) {
				for (RoleInstance role : this.getRoleList()) {
					this.kickRole(role);
				}
			}
		}catch(Exception ex){
			
		}
		
	}
	@Override
	public void destroy(){ 
		this.clearRole();
		super.destroy();
		if(null != this.match){
			this.match.destroy();
			this.match = null ;
		}
		logger.info(" mapCampWarInstance destory: " + this.instanceId);
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if(!this.isRole(attacker) || !this.isRole(victim)){
			return ;
		}
		//不存在npc,不需要判断
		//this.notifyNpcAi(attacker);
		this.whoWinWhenDeath(victim,attacker);
	}
	
	private boolean isBattleTimeOver(){
		return System.currentTimeMillis()-this.createTime > this.maxBattleTime*1000 ;
	}
	
	
	private boolean mustDestoryTimeOver(){
		return System.currentTimeMillis()-this.createTime > this.maxBattleTime*1000 + 30*1000 ;
	}
	
	/**
	 * 表示战斗结束
	 */
	private void flagOver(AbstractRole winRole,AbstractRole failRole){
		this.winRole = winRole ;
		this.failRole = failRole ;
		this.winFlag.set(true);
		this.mapState = MapState.over;
		//发奖
		this.reward();
	}
	
	private boolean whoWinWhenLoop(){
		this.winLock.lock();
		try {
			if (winFlag.get()) {
				// 胜负已分,不再判断
				return false;
			}
			if (this.isBattleTimeOver()) {
				// 战斗已经超时,无论伤亡情况如何一律平局
				this.flagOver(null,null);
				return true;
			}
			// 在开始后1分钟才开始处理
			// 解决匹配成功后一方进入地图失败，导致不得不等到战斗超时
			if ((System.currentTimeMillis() - this.createTime) <= MAX_WAIT_OTHER_TIME) {
				return false;
			}
			boolean role1Live = this.isLive(this.match.getRole1());
			boolean role2Live = this.isLive(this.match.getRole2()) ;
			if(role1Live && role2Live){
				return false ;
			}
			//胜负已分
			if(!role1Live && !role2Live){
				//平局
				this.flagOver(null, null);
				return true ;
			}
			this.flagOver((role1Live)?this.match.getRole1():this.match.getRole2(), 
					(!role1Live)?this.match.getRole1():this.match.getRole2());
			return true ;
		}finally{
			this.winLock.unlock();
		}
	}
	
	private boolean inRoleMap(AbstractRole role){
		return (null != this.roleMap)
				&& (null != this.roleMap.get(role.getRoleId())) ;
	}
	
	private boolean isLive(AbstractRole role){
		if(null == role){
			return false ;
		}
		return this.inRoleMap(role) && !role.isDeath() ;
	}
	
	private boolean whoWinWhenExit(AbstractRole exitRole) {
		this.winLock.lock();
		try {
			if (winFlag.get()) {
				// 胜负已分,不再判断
				return false;
			}
			//判断目标是否在地图
			AbstractRole target = (exitRole.getIntRoleId()==this.match.getRole1().getIntRoleId())? 
					this.match.getRole2() : this.match.getRole1() ;
			if(this.inRoleMap(target)){
				//目标在地图,推出者算失败
				this.flagOver(target, exitRole);
			}else{
				//目标不在地图
				this.flagOver(null, null);
			}
			return true ;
		}finally{
			this.winLock.unlock();
		}
	}
	
	private boolean whoWinWhenDeath(AbstractRole death,
			AbstractRole attacker){
		this.winLock.lock();
		try {
			if (winFlag.get()) {
				// 胜负已分,不再判断
				return false;
			}
			this.flagOver(attacker,death);
			return true ;
		}finally{
			this.winLock.unlock();
		}
	}


	/** 给奖励 **/
	private void reward() {
		try {
			List<RoleRewardResult> list = GameContext.getCampWarApp().roleBattleEnd(match, winRole, failRole);
			for(RoleRewardResult result : list){
				C0356_CampWarRolePkResultRespMessage respMsg = new C0356_CampWarRolePkResultRespMessage();
				respMsg.setStatus(result.getPkStatus());
				respMsg.setRewardCampPrestige(result.getEffectAddPrestige());
				respMsg.setRewardGameMoney(result.getGameMoney());
				respMsg.setWinTimes((short)result.getWinTimes());
				GameContext.getMessageCenter().sendByRoleId(null, result.getRoleId(), respMsg);
			}
		} catch (Exception ex) {
			logger.error("reward error", ex);
		}finally{
			// 将地图状态设置为踢人
			sendRewardTime = System.currentTimeMillis();
			this.mapState = MapState.kick_role;
		}
	}
	
	@Override
	public Point getRebornPoint(RoleInstance role,RebornType type){
		if(RebornType.situ == type){
			//擂台赛不允许原地复活和技能复活
			return null ;
		}
		return super.getRebornPoint(role, type);
	}
	
	@Override
	public boolean canUseSkill(RoleInstance role,int skillId){
		return GameContext.getArenaApp().canUseSkill(skillId);
	}
	
	@Override
	public ForceRelation getForceRelation(RoleInstance role,RoleInstance target){
		return ForceRelation.enemy;
	}
	
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		//没有复活方式
		return null ;
	}
	
	@Override
	public boolean canBuildTeam(){
		return false ;
	}
	
	@Override
	public boolean canUseGoods(RoleInstance role,int goodsId){
		return false ;
	}
	
	private boolean isRole(AbstractRole role){
		return null != role && RoleType.PLAYER == role.getRoleType() ;
	}
	
	
}
