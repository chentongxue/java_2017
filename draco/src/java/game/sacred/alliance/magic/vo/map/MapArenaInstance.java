package sacred.alliance.magic.vo.map;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.arena.BattleResult;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.app.arena.config.Reward1v1Bout;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.MapRemainTimeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapCopyInstance;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.base.AppType;
import com.game.draco.message.internal.C0075_Arena1V1AutoApplyInternalMessage;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
import com.game.draco.message.push.C3854_ArenaResultNotifyMessage;

/**
 * 
 * 擂台赛地图实例
 *
 */
public class MapArenaInstance extends MapCopyInstance {
	//战斗结束后多长时间踢人
	private final static int KICK_ROLE_WHEN_OVER_TIME = 5*1000 ;
	//等待对方进入地图最大时间
	private final static int MAX_WAIT_OTHER_TIME = 60*1000 ;
	protected final static String CRLF = "\n" ;
	//一秒钟判断一次输赢
	private LoopCount whoWinLoopCount = new LoopCount(2000);
	protected static enum ArenaMapState {
		create,
		enter,
		begin,
		over,
		kick_role,
		wait_destory,
		;
	}
	
	protected int getMaxPlayerNum(ArenaType areanType){
		if(null == areanType || ArenaType._1V1  == areanType){
			return 1 ;
		}
		return 4 ;
	}
	
	
	
	protected ArenaMatch match;
	private ArenaType arenaType ;
	private ArenaMapState mapState = ArenaMapState.create;
	private long createTime = System.currentTimeMillis();
	protected AtomicBoolean winFlag = new AtomicBoolean(false);
	private AtomicBoolean first = new AtomicBoolean(false);
	protected ApplyInfo winTeam = null ;
	protected Active active;

	private Lock winLock = new ReentrantLock();
	private int maxBattleTime = 1000 ;
	private long sendRewardTime = System.currentTimeMillis();
	//可以获得奖励列表
	protected Set<String> rewardRoleSet = new HashSet<String>() ;

	
	@Override
    protected void enter(AbstractRole role){
		if(null == role || RoleType.PLAYER != role.getRoleType()){
			return ;
		}
		super.enter(role);
		RoleInstance player = (RoleInstance)role ;
		//增加参入次数
		GameContext.getCountApp().incrArenaJoin(player, arenaType);
		//活动日志
		this.activeOutPutLog(player);
		//发送倒计时消息
		this.sendRemainTime(player);
		//活跃度
		if(ArenaType._1V1 == arenaType){
			GameContext.getDailyPlayApp().incrCompleteTimes(player, 1, DailyPlayType.arena_1v1, "");
			//通知参加了此活动
			GameContext.getCountApp().joinApp(player, AppType.arena_1v1);
			this.autoDismount(player);
		}
		//满血满蓝
		this.perfectBody(role);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime){
		super.broadcastMap(role, message, expireTime);
	}
	
	
	protected void sendRemainTime(RoleInstance role){
		try {
			int lifeTime = (int)(System.currentTimeMillis() - this.createTime)/1000 ; 
			int clearTime = this.match.getConfig().getClearBaffleTime();
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
	
	protected void activeOutPutLog(RoleInstance role){
		if(null == this.active){
			return ;
		}
		//活动参与度日志
		try {
			this.active.outputLog(role);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	@Override
	protected String createInstanceId() {
		instanceId = "arena_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	public MapArenaInstance(Map map, ArenaMatch match) {
		super(map);
		this.match = match;
		this.arenaType = ArenaType.get(this.match.getConfig().getArenaType());
		this.maxBattleTime = this.match.getConfig().getMaxBattleTime();
		Active active = GameContext.getActiveApp().getActive((short) this.match.getConfig().getActiveId());
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
		boolean thisJudeWhoWin = false ;
		try {
			thisJudeWhoWin = this.whoWinTeam(null,null, false);
		} catch (ServiceException e) {
		}
		//清除报名信息
		GameContext.getArenaApp().removeApplyInfo(player.getRoleId());
		//满血满蓝
		this.perfectBody(role);
		//胜负未分就离开地图的人一律脱离队伍,并且算失败(不能获得经验)
		//因为此人离开地图而分出的胜负,此人算失败(不能获得经验)
		boolean flagWin = this.winFlag.get();
		if(!flagWin ||(flagWin && thisJudeWhoWin)){
			GameContext.getCountApp().incrArenaFail(player, arenaType,0);
			/*if(player.hasTeam()){
				player.getTeam().memberLeave(role, LeaveTeam.apply);
			}*/
		}
		//push自动报名
		this.autoApply(player);
	}
	
	private void autoApply(RoleInstance player) {
		try {
			if (arenaType != ArenaType._1V1) {
				return;
			}
			if (player.isOfflined()
					|| !GameContext.getArena1V1App().isAutoApply(player)) {
				// 已经在下线 或者非自动报名
				return;
			}
			if (!GameContext.getArena1V1App().isAcitveTimes()) {
				// 活动结束
				return;
			}
			C0075_Arena1V1AutoApplyInternalMessage reqMsg = new C0075_Arena1V1AutoApplyInternalMessage();
			player.getBehavior().addCumulateEvent(reqMsg);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	private ArenaConfig getConfig(){
		return this.match.getConfig();
	}
	
	/*private Arena getArena(){
		return GameContext.getArenaApp().getArena(this.getConfig().getActiveId());
	}*/

	@Override
	public void addAbstractRole(AbstractRole role) {
		super.addAbstractRole(role);
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		//第一人进入后重装设置下创建时间
		if(first.compareAndSet(false, true)){
			this.mapState = ArenaMapState.enter ;
			createTime = System.currentTimeMillis();
		}
	}
	
	@Override
	protected void updateSub() throws ServiceException {
		//此地图只需要更新用户行为
		super.updatePlayer() ;
		if(ArenaMapState.enter == this.mapState 
				&& System.currentTimeMillis()-this.createTime>this.getConfig().getClearBaffleTime()*1000){
			//进入准备阶段,此时清除障碍物
			this.clearBaffle();
			//添加仇恨
			//this.addHatred();
			//开始战斗了
			this.mapState = ArenaMapState.begin;
			return ;
		}
		if (mapState == ArenaMapState.begin) {
			if(whoWinLoopCount.isReachCycle()){
				//判断输赢
				whoWinTeam(null,null,true);
			}
			//刷新宝箱
			this.refresh();
			return;
		}
		if(mapState == ArenaMapState.over){
			try {
				// 计算战斗奖励
				this.reward();
			}finally{
				//将地图状态设置为踢人
				sendRewardTime = System.currentTimeMillis();
				this.mapState = ArenaMapState.kick_role ;
			}
			return ;
		} 
		if(mapState == ArenaMapState.kick_role){
			if(System.currentTimeMillis()-sendRewardTime>KICK_ROLE_WHEN_OVER_TIME){
				//奖励发送完毕后10s钟后开始t人
				// 踢人
				this.clearRole();
				this.mapState = ArenaMapState.wait_destory ;
			}
		}
	}
	
	private void delArenaMatch(){
		try {
			if(null == this.match){
				return ;
			}
			this.match.cancelAll();
			this.match = null ;
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	
	public boolean canDestroy(){
		if ((mapState == ArenaMapState.wait_destory 
				|| (mapState == ArenaMapState.kick_role))
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
			//this.delArenaMatch();
		}catch(Exception ex){
			
		}
		
	}
	@Override
	public void destroy(){ 
		this.clearRole();
		super.destroy();
		logger.info(" mapAreanInstance destory: " + this.instanceId);
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		//不存在npc,不需要判断
		//this.notifyNpcAi(attacker);
		try {
			this.whoWinTeam(victim,attacker,false);
		} catch (ServiceException e) {
		}
	}

	private int getMapLiveListSize(ApplyInfo team){
		int total = 0 ;
		for(String roleId:team.getAppliers()){
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(null == role){
				continue ;
			}
			if(role.isDeath()){
				continue ;
			}
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				continue ;
			}
			if(!mapInstance.getInstanceId().equals(this.instanceId)){
				//没有在当前地图
				continue ;
			}
			total++ ;
		}
		return total ;
	}
	
	
	private boolean isBattleTimeOver(){
		return System.currentTimeMillis()-this.createTime > this.maxBattleTime*1000 ;
	}
	
	
	private boolean mustDestoryTimeOver(){
		return System.currentTimeMillis()-this.createTime > this.maxBattleTime*1000 + 30*1000 ;
	}
	
	/**
	 * 表示战斗结束
	 * @param winTeam 胜利队伍
	 */
	private void flagOver(ApplyInfo winTeam){
		this.winTeam = winTeam ;
		this.winFlag.set(true);
		//在此时获得可获得奖励成员(在地图内成员)
		rewardRoleSet = new HashSet<String>();
		for(RoleInstance role : this.getRoleList()){
			rewardRoleSet.add(role.getRoleId());
		}
		mapState = ArenaMapState.over;
	}
	
	/**
	 * 判断胜负
	 * 有三个触发点
	 * 1.成员死亡
	 * 2.成员退出地图
	 * 3.主循环
	 * 
	 * @return 本次操作是否发生了胜负的变化
	 * @param death 死亡者,只有触发点1才需要传入
	 * @param attacker 攻击者
	 * @param loopEvent 是否主循环触发点
	 * @throws ServiceException
	 */
	private boolean whoWinTeam(AbstractRole death,AbstractRole attacker,boolean loopEvent) throws ServiceException {
		this.winLock.lock();
		try {
			if (winFlag.get()) {
				// 胜负已分,不再判断
				return false;
			}
			if (this.isBattleTimeOver()) {
				// 战斗已经超时,无论伤亡情况如何一律平局
				this.flagOver(null);
				return true;
			}
			if (loopEvent) {
				// 在开始后1分钟才开始处理
				// 解决匹配成功后一方进入地图失败，导致不得不等到战斗超时
				if ((System.currentTimeMillis() - this.createTime) <= MAX_WAIT_OTHER_TIME) {
					return false;
				}
			}
			int liveSize1 = this.getMapLiveListSize(this.match.getTeam1());
			int liveSize2 = this.getMapLiveListSize(this.match.getTeam2());
			if (0 == liveSize1 && 0 == liveSize2) {
				// 双方都死亡 平局(应该是不会发生)
				this.flagOver(null);
				return true;
			}
			if (0 == liveSize2) {
				// team1获得胜利
				this.flagOver(match.getTeam1());
				return true;
			}
			if (0 == liveSize1) {
				// tema2获得胜利
				this.flagOver(match.getTeam2());
				return true;
			}
			return false;
		}finally{
			this.winLock.unlock();
		}
	}


	/** 给奖励 **/
	protected void reward() {
		try {
			if(Util.isEmpty(this.rewardRoleSet)){
				return ;
			}
			for (String currentRoleId : rewardRoleSet) {
				if (null == this.winTeam) {
					this.reward(currentRoleId, BattleResult.draw,
							this.getMaxPlayerNum(arenaType),arenaType);
					continue;
				}
				// 有胜负之分
				this.reward(currentRoleId,
						this.winTeam.isMember(currentRoleId)? BattleResult.win
								: BattleResult.fail, this.getMaxPlayerNum(arenaType)
								,arenaType);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	protected String getActiveName(){
		if(null == this.active){
			return GameContext.getI18n().getText(TextId.ARENA_1V1_NAME) ;
		}
		return active.getName();
	}
	
	protected void reward(String roleId,BattleResult result,int otherTeamNum,ArenaType arenaType){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		Integer roleLevel = 0 ;
		
		if(null == role){
			roleLevel = this.match.getRolelevels().get(roleId);
			if(null == roleLevel){
				return ;
			}
			//TODO:发送离线邮件
			return ;
		}
		
		//没有平局
		int score = 0 ;
		String tips = "" ;
		Reward1v1Bout rewardBout = GameContext.getArena1V1App().getReward1v1Bout(role.getLevel());
		boolean win = (BattleResult.win == result) ;
		if(win){
			score = this.match.getConfig().getSuccessScore() ;
			//胜利
			GameContext.getCountApp().incrArenaWin(role, arenaType,score);
			tips = GameContext.getI18n().getText(TextId.ARENA_1V1_SUCCESS_TIPS) ;
		}else {
			score = this.match.getConfig().getFailScore() ;
			//失败
			GameContext.getCountApp().incrArenaFail(role, arenaType,score);
			tips = GameContext.getI18n().getText(TextId.ARENA_1V1_FAIL_TIPS) ;
		}
		
		int winTimes = role.getRoleArena().getCycleWinTime(arenaType);
		int failTimes = role.getRoleArena().getCycleFailTime(arenaType);
		int allCount = winTimes + failTimes ;
		
		int exp = 0 ;
		int gameMoney = 0 ;
		if(null != rewardBout){
			exp = win?rewardBout.getSuccessExp():rewardBout.getFailureExp();
			gameMoney = win?rewardBout.getSuccessGameMoney():rewardBout.getFailureGameMoney();
		}
		if(allCount <= this.match.getConfig().getSpecialTimes()){
			//只有特殊次数内才有经验奖励和游戏币
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp, 
					OperatorType.Add, exp, OutputConsumeType.arena_output);
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.gameMoney, 
					OperatorType.Add, gameMoney, OutputConsumeType.arena_output);
			role.getBehavior().notifyAttribute();
		}
		
		C3854_ArenaResultNotifyMessage message = new C3854_ArenaResultNotifyMessage();
		message.setInfo(tips);
		message.setObtainExp(exp);
		message.setObtainScore(score);
		message.setObtainGameMoney(gameMoney);
		message.setTodayTimes((short)allCount);
		message.setWinTimes((short)winTimes);
		message.setFailTimes((short)failTimes);
		message.setTotalScore(role.getRoleArena().getScore(arenaType));
		message.setArenaType((byte)arenaType.getType());
		message.setResultType(win?BattleResult.win.getType():BattleResult.fail.getType());
		role.getBehavior().sendMessage(message);
	}
	
	@Override
	public Point getRebornPoint(RoleInstance role,RebornType type){
		if(RebornType.situ == type || RebornType.soul == type){
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
		//如果是单人模式直接返回敌对
		if(this.match.isSameTeam(role.getRoleId(), target.getRoleId())){
			return ForceRelation.friend;
		}
		return ForceRelation.enemy;
	}
	
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		//擂台赛内没有复活方式
		return null ;
	}
	
	@Override
	public boolean canBuildTeam(){
		return false ;
	}
	
	@Override
	public boolean canUseGoods(RoleInstance role,int goodsId){
		return GameContext.getArenaApp().canUseGoods(goodsId);
	}
	
protected void refresh(){
		
	}
}
