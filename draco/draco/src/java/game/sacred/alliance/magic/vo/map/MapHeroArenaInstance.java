package sacred.alliance.magic.vo.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.hero.arena.HeroFightStatus;
import com.game.draco.app.hero.arena.config.HeroArenaBaseConfig;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.npc.NpcInstanceFactroy;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;

public class MapHeroArenaInstance extends MapInstance {
	
	private final LoopCount mapLoop = new LoopCount(3*1000);//每3秒判断一次地图状态
	private final static int KICK_ROLE_WHEN_OVER_TIME = 10*1000;//战斗结束后多长时间踢人
	private final static int Three = 3;
	private MapState mapState = MapState.init;
	private AtomicBoolean roleEnter = new AtomicBoolean(false);
	private int gateId;//关卡ID
	private int[] selectHeros;//自己的英雄
	private String fightRoleId;//对手角色ID
	private List<RoleHero> fightHeros;//对手的英雄
	private int pkIndex = 0;
	private byte[] pkResults = new byte[3];
	
	private AtomicBoolean winFlag = new AtomicBoolean(false);
	
	private long startTime = System.currentTimeMillis();//开始时间
	private long overTime = System.currentTimeMillis();//结束时间
	protected RoleInstance role;
	private NpcInstance npcRole;
	//pvpBattleInfo
	private AsyncPvpRoleAttr fightRoleAttr;
	
	public MapHeroArenaInstance(Map map) {
		super(map);
	}
	
	@Override
	protected String createInstanceId() {
		instanceId = "hero_arena_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	public enum MapState {
		init,//初始化状态
		enter,//角色进入状态：取相关信息、记录开始时间
		pk_ready,//每轮的准备阶段：重置双方的状态及坐标
		pk_begin_notice,//每轮的开始前广播阶段：发第几轮的PK开始信息、特效
		pk_fight,//每轮的PK阶段：判断是否决出胜负
		pk_end_notice,//每轮的结束前广播阶段：发第几轮的PK结束信息、特效
		pk_end,//每轮结束阶段：判断是否达到最大轮次、进而决定是下一轮PK还是gameover
		game_over,//gameover状态：发结算面板、奖励、记录结束时间
		kick_role,//踢人状态：超过踢人保护时间后将角色从地图移除
		wait_destory,//地图销毁阶段：销毁地图
		;
	}
	
	@Override
	public void updateSub(){
		try {
			super.updateSub();
			if(this.mapLoop.isReachCycle()){
				switch(this.mapState){
				case enter:
					this.do_role_enter();
					break;
				case pk_ready:
					this.do_pk_ready();
					break;
				case pk_begin_notice:
					this.do_pk_begin_notice();
					break;
				case pk_fight:
					this.do_pk_fight();
					break;
				case pk_end_notice:
					this.do_pk_end_notice();
					break;
				case pk_end:
					this.do_pk_end();
					break;
				case game_over:
					this.do_game_over();
					break;
				case kick_role:
					this.do_kick_role();
					break;
				case wait_destory:
					this.do_wait_destory();
					break;
				}
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + "clearRole error : ", e);
		}
	}
	
	private void do_role_enter(){
		if(null == this.role){
			return;
		}
		try {
			RoleHeroArenaRecord record = GameContext.getHeroArenaApp().getRoleHeroArenaRecord(this.role.getRoleId());
			this.gateId = record.getFightGateId();
			this.fightRoleId = record.getFightRoleId();
			this.selectHeros = record.getSelectHeros();
			this.fightHeros = record.getFightHeroList();
			this.fightRoleAttr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(this.fightRoleId);
			//标记为第一轮PK
			this.pkIndex = 0;
			//记录开始时间
			this.startTime = System.currentTimeMillis();
			//角色进入之后，将地图切换到PK准备状态
			this.mapState = MapState.pk_ready;
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".do_role_enter error: ", e);
			//如果获取对战角色信息异常，则将地图切换到踢人状态
			this.mapState = MapState.kick_role;
		}
	}
	
	private void do_pk_ready(){
		if(this.pkIndex >= Three){
			this.mapState = MapState.kick_role;
			return;
		}
		//重新创建一个胜负标记
		this.winFlag = new AtomicBoolean(false);
		//更新自己的英雄信息、地图坐标等
		this.dispose_self_hero();
		//更新对手的英雄信息、地图坐标等
		this.dispose_target_hero();
		this.mapState = MapState.pk_begin_notice;
	}
	
	private void do_pk_begin_notice(){
		//TODO:发通知开始提示
		int index = this.pkIndex + 1;
		String message = "Round " + index + " Ready GO !";
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, message, null, this);
		this.mapState = MapState.pk_fight;
	}
	
	private void do_pk_fight(){
		//胜负已分，切换地图状态
		if(this.winFlag.get()){
			this.mapState = MapState.pk_end_notice;
		}
	}
	
	private void do_pk_end_notice(){
		//TODO:发送本次PK结果提示信息
		int index = this.pkIndex + 1;
		String message = "Round " + index + " KO !";
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, message, null, this);
		this.mapState = MapState.pk_end;
	}
	
	private void do_pk_end(){
		//如果达到最大PK次数，则将地图切换到gameover状态；否则，切换到pk_ready状态，开始下一轮PK。
		if(this.pkIndex >= Three-1){
			this.mapState = MapState.game_over;
		}else{
			this.pkIndex ++;
			this.mapState = MapState.pk_ready;
		}
	}
	
	private void do_game_over(){
		String message = "Game Over ! Please wait for the reward .";
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, message, null, this);
		//记录结束时间
		this.overTime = System.currentTimeMillis();
		//发胜负结算面板，发奖励
		GameContext.getHeroArenaApp().gameOver(this.role, this.gateId, this.pkResults, this.fightRoleAttr.getRoleName());
		//将地图切换到踢人状态
		this.mapState = MapState.kick_role;
	}
	
	private void do_kick_role(){
		//超过踢人保护之间，移除地图内角色
		if(System.currentTimeMillis() - this.overTime > KICK_ROLE_WHEN_OVER_TIME){
			this.clearRole();
			this.mapState = MapState.wait_destory;
		}
	}
	
	private void do_wait_destory(){
		this.destroy();
	}
	
	private void dispose_self_hero(){
		try {
			if(null == this.role){
				return;
			}
			int heroId = this.selectHeros[this.pkIndex];
			RoleHero onHero = GameContext.getUserHeroApp().getOnBattleRoleHero(this.role.getRoleId());
			//当前英雄已经出战，则不需要处理
			if(null != onHero && onHero.getHeroId() == heroId){
				return;
			}
			//更换出战英雄
			GameContext.getHeroApp().onBattle(this.role, heroId);
			//更换角色坐标
			Point point = this.getSelfPoint();
			if(null != point){
				GameContext.getUserMapApp().changeMap(this.role, point);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".dispose_self_hero error: ", e);
		}
	}
	
	private void dispose_target_hero(){
		try{
			RoleHero pkHero = this.fightHeros.get(this.pkIndex);
			Point targetPoint = this.getTargetPoint();
			if(null == pkHero || null == this.fightRoleAttr || null == targetPoint){
				return;
			}
			int pkHeroId = pkHero.getHeroId();
			int onHeroId = this.fightRoleAttr.getHeroId();
			//需要更换英雄
			if(onHeroId != pkHeroId){
				GoodsHero gh = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, pkHeroId);
				//英雄属性变化
				RoleHero oldHero = null;
				for(RoleHero rh : this.fightHeros){
					if(null != rh && rh.getHeroId() == onHeroId){
						oldHero = rh;
						break;
					}
				}
				AttriBuffer oldBuffer = GameContext.getHeroApp().getHeroAttriBuffer(oldHero);
				AttriBuffer pkBuffer = GameContext.getHeroApp().getHeroAttriBuffer(pkHero);
				AttriBuffer buffer = AttriBuffer.createAttriBuffer();
				buffer.append(pkBuffer).append(oldBuffer.reverse());
				for(AttriItem ai : buffer.getMap().values()){
					if(null == ai){
						continue;
					}
					byte type = ai.getAttriTypeValue();
					int oldVal = this.fightRoleAttr.getAttriValue(type);
					float val = ai.getValue() + oldVal;
					int value = val >= 0 ? (int) val : 0;
					this.fightRoleAttr.setAttriValue(type, value);
				}
				this.fightRoleAttr.setHeroId(pkHeroId);
				//先修改对手的英雄外形资源
				this.fightRoleAttr.setEquipResId((short) gh.getWeaponResId());
				this.fightRoleAttr.setClothesResId(gh.getResId());
			}
			NpcInstance npcInstance = NpcInstanceFactroy.createAsyncPvpNpcInstance(this.fightRoleAttr, targetPoint.getMapid(), targetPoint.getX(), targetPoint.getY());
			npcInstance.setMapInstance(this);
			npcInstance.setNpcBornDataIndex(-1);
			this.addAbstractRole(npcInstance);
			// 通知
			for (RoleInstance ri : this.getRoleList()) {
				C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
				message.setItem(Converter.getAsyncPvpRoleBodyItem(npcInstance.getRoleId(), this.fightRoleAttr,
						(short)targetPoint.getX(), (short)targetPoint.getY()));
				GameContext.getMessageCenter().send("", ri.getUserId(), message);
			}
			this.npcRole = npcInstance;
		}catch(Exception e){
			logger.error(this.getClass().getName() + ".dispose_target_hero error:", e);
		}
	}
	
	/**
	 *  被挑战者坐标
	 */
	private Point getSelfPoint(){
		HeroArenaBaseConfig config = GameContext.getHeroArenaApp().getHeroArenaBaseConfig();
		if(null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getMapX1(), config.getMapY1());
	}
	
	private Point getTargetPoint(){
		HeroArenaBaseConfig config = GameContext.getHeroArenaApp().getHeroArenaBaseConfig();
		if(null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getMapX2(), config.getMapY2());
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
			this.flagOver(HeroFightStatus.Failure);
			Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
			role.setMapId(targetPoint.getMapid());
			role.setMapX(targetPoint.getX());
			role.setMapY(targetPoint.getY());
			this.perfectBody(role);
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
		if(roleEnter.compareAndSet(false, true)){
			this.mapState = MapState.enter ;
			this.startTime = System.currentTimeMillis();
			this.role = (RoleInstance)role;
			this.perfectBody(role);
			GameContext.getAsyncPvpApp().resetRoleSkill(this.role);
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
		flagOver(HeroFightStatus.Failure);
		//记录英雄死亡
		GameContext.getHeroArenaApp().fightDeath((RoleInstance) attacker);
	}
	
	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim){
		flagOver(HeroFightStatus.Victory);
	}
	
	private void flagOver(HeroFightStatus status){
		if(winFlag.get()){
			//胜负已分,不再判断
			return;
		}
		this.winFlag.set(true);
		this.pkResults[this.pkIndex] = status.getType();
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
			logger.error(this.getClass().getName() + "clearRole error : ", ex);
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
	public boolean canUseGoods(RoleInstance role,int goodsId){
		return false;
	}

	public NpcInstance getNpcRole() {
		return npcRole;
	}
	
}
