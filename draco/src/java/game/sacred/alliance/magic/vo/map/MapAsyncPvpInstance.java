package sacred.alliance.magic.vo.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.MapRemainTimeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.npc.NpcInstanceFactroy;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;

public abstract class MapAsyncPvpInstance extends MapInstance{
	private AtomicBoolean winFlag = new AtomicBoolean(false);
	private Object winLock = new byte[0] ;
	private LadderMapState mapState = LadderMapState.create;
	private AtomicBoolean roleEnter = new AtomicBoolean(false);
	//战斗结束后多长时间踢人
	private final static int KICK_ROLE_WHEN_OVER_TIME = 5 * 1000 ;
	private long createTime = System.currentTimeMillis();
	private int clearBaffleTime = 0; 
	protected RoleInstance role;
	private long sendRewardTime = System.currentTimeMillis();
	private NpcInstance npcRole;
	//pvpBattleInfo
	private AsyncPvpBattleInfo battleInfo = null;
	
	protected static enum LadderMapState {
		create,
		enter,
		begin,
		over,
		kick_role,
		wait_destory,
		;
	}
	
	public MapAsyncPvpInstance(Map map) {
		super(map);
		this.clearBaffleTime = this.getClearBaffleTime();
	}
	
	/**
	 * 初始化障碍消失时间，默认是3秒 
	 */
	protected int getClearBaffleTime() {
		return 3;
	}
	
	/**
	 *  被挑战者坐标
	 */
	protected abstract Point getTargetPoint();
	
	@Override
	protected String createInstanceId() {
		instanceId = "ladder_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	private NpcInstance createNpcInstance() {
		NpcInstance npcInstance = null;
		try{
			if(null == this.battleInfo) {
				return npcInstance;
			}
			String targetId = this.battleInfo.getTargetRoleId();
			AsyncPvpRoleAttr ladderRoleAttr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(targetId);
			if(null == ladderRoleAttr) {
				return npcInstance;
			}
			Point targetPoint = this.getTargetPoint();
			if(null == targetPoint) {
				return npcInstance;
			}
			
		    npcInstance = NpcInstanceFactroy.createAsyncPvpNpcInstance(ladderRoleAttr, targetPoint.getMapid(),
		    		targetPoint.getX(), targetPoint.getY());
			npcInstance.setMapInstance(this);
			npcInstance.setNpcBornDataIndex(-1);
			this.addAbstractRole(npcInstance);
			// 通知
			for (RoleInstance ri : this.getRoleList()) {
				C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
				message.setItem(Converter.getAsyncPvpRoleBodyItem(npcInstance.getRoleId(), ladderRoleAttr,
						(short)targetPoint.getX(), (short)targetPoint.getY()));
				GameContext.getMessageCenter().send("", ri.getUserId(), message);
			}
		}catch(Exception e){
			logger.error("LadderApp.createLadderNpc error:",e);
			return null;
		}
		return npcInstance;
	}
	
	protected abstract void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, 
			ChallengeResultType result);
	
	protected abstract void exitMapPushMessage();

	@Override
	public void updateSub(){
		try {
			super.updateSub();
			if(LadderMapState.enter == this.mapState 
					&& System.currentTimeMillis()-this.createTime > clearBaffleTime * 1000){
				//刷新挑战者
				npcRole = this.createNpcInstance();
				if(null == npcRole) {
					this.mapState = LadderMapState.kick_role;
					return;
				}
				//进入准备阶段,此时清除障碍物
				this.clearBaffle();
				//开始战斗了
				this.mapState = LadderMapState.begin;
				return ;
			}
			if (mapState == LadderMapState.begin) {
				//NPC是否变身
				if(npcRole.isDeath()){
					return;
				}
				return;
			}
			if(mapState == LadderMapState.over){
				sendRewardTime = System.currentTimeMillis();
				this.mapState = LadderMapState.kick_role ;
				return ;
			} 
			if(mapState == LadderMapState.kick_role){
				if(System.currentTimeMillis() - sendRewardTime > KICK_ROLE_WHEN_OVER_TIME){
					//奖励发送完毕后10s钟后开始t人
					this.clearRole();
					this.mapState = LadderMapState.wait_destory ;
				}
			}
			if(mapState == LadderMapState.wait_destory) {
				this.destroy();
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, RoleInstance target) {
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
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
	
	@Override
	public void destroy(){
		super.destroy();
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		try{
			super.exitMap(role);
			//离开地图时判断胜负
			this.flagOver(ChallengeResultType.Lose);
			Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
			role.setMapId(targetPoint.getMapid());
			role.setMapX(targetPoint.getX());
			role.setMapY(targetPoint.getY());
			this.perfectBody(role);
			exitMapPushMessage();
		}catch(Exception e){
			logger.error("MapLadderInstance exitMap error",e);
		}finally{
			this.destroy();
		}
	}
	
	@Override
    protected void enter(AbstractRole role){
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		super.enter(role);
		if(roleEnter.compareAndSet(false, true)){
			this.mapState = LadderMapState.enter ;
			createTime = System.currentTimeMillis();
			this.role = (RoleInstance)role;
			//发送倒计时消息
			this.sendRemainTime(this.role);
			this.perfectBody(role);
			GameContext.getAsyncPvpApp().resetRoleSkill(this.role);
			this.battleInfo = GameContext.getAsyncPvpApp().getAsyncPvpBattleInfo(role.getRoleId());
		}
	}
	
	@Override
	public boolean isCopy() {
		return true;
	}
	
	@Override
	public boolean canDestroy() {
		if(this.getRoleCount() == 0){
			return true;
		}
		return false;
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if(victim.getRoleType() != RoleType.PLAYER) {
			return ;
		}
		flagOver(ChallengeResultType.Lose);
	}
	
	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim){
		flagOver(ChallengeResultType.Win);
	}
	
	private void flagOver(ChallengeResultType result){
		synchronized (winLock) {
			if(winFlag.get()){
				//胜负已分,不再判断
				return;
			}
			this.winFlag.set(true);
			try {
				challengeOver(role, this.battleInfo, result);
				//清除NPC
//				this.clearNpc();
				mapState = LadderMapState.over;
			} catch (RuntimeException e) {
				logger.error("MapLadderInstance.flagOver error: ", e);
			}
		}
	}


	@Override
	protected void deathLog(AbstractRole victim) {
	}


	@Override
	public void useGoods(int goodsId) {
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
	
	protected void sendRemainTime(RoleInstance role){
		try {
			int lifeTime = (int)(System.currentTimeMillis() - this.createTime)/1000 ; 
			if(lifeTime >= clearBaffleTime){
				return ;
			}
			C0208_CopyRemainTimeNotifyMessage notifyMsg = new C0208_CopyRemainTimeNotifyMessage();
			notifyMsg.setType(MapRemainTimeType.Arean.getType());
			notifyMsg.setTime(clearBaffleTime - lifeTime);
			role.getBehavior().sendMessage(notifyMsg);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		return null ;
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message){
		this.broadcastMap(role, message);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message, int expireTime) {
		super.broadcastMap(role, message, expireTime);
	}
	
	@Override
	public boolean canUseGoods(RoleInstance role,int goodsId){
		return false;
	}

	public NpcInstance getNpcRole() {
		return npcRole;
	}
}
