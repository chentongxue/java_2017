package sacred.alliance.magic.vo.map;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.hero.arena.HeroFightStatus;
import com.game.draco.app.hero.arena.config.HeroArenaGateConfig;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;
import com.game.draco.app.npc.NpcInstanceFactroy;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.HeroSwitchableInfoItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.game.draco.message.response.C1272_HeroSwitchUiRespMessage;
import com.game.draco.message.response.C2001_RoleRebornRespMessage;
import com.google.common.collect.Lists;

public class MapHeroArenaInstance extends MapInstance {
	
	private final LoopCount mapLoop = new LoopCount(1000);//每1秒判断一次地图状态
	private final static int KICK_ROLE_WHEN_OVER_TIME = 1000*5 ;
	private final static int Three = 3;
	private MapState mapState = MapState.init;
	private AtomicBoolean roleEnter = new AtomicBoolean(false);
	private int gateId;//关卡ID
	private int[] selectHeros;//自己的英雄
	private java.util.Map<Integer,Short> heroMap;//自己的英雄血量百分比
	private java.util.Map<Integer,Short> targetHeroMap;//对方的英雄血量百分比
	private String fightRoleId;//对手角色ID
	private List<RoleHero> fightHeros;//对手的英雄
	private java.util.Map<Integer,Short> finalHerosMap = new LinkedHashMap<Integer,Short>(Three);//进入时的英雄
	private int[] finalHelpHerosSet = new int[Three];//进入时的助威英雄
	private int index = 0;
	private int pkIndex = 0;
	private byte[] pkResults;
	private RoleHero hero;
	private RoleHero targetHero;
	private RoleHeroArenaRecord record;
	private int x;
	private int y;
	private long overTime = 0 ;
	
	private AtomicBoolean selfHeroEmpty = new AtomicBoolean(false);//自己英雄是否轮空
	private AtomicBoolean rivalHeroEmpty = new AtomicBoolean(false);//对手英雄是否轮空
	
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
				if(!roleEnter.get()){
					return ;
				}
				switch(this.mapState){
				case pk_ready:
					this.do_pk_ready();
					break;
				case pk_begin_notice:
					this.do_pk_begin_notice();
					break;
				case pk_fight:
					checkNpc();
					this.do_pk_fight();
					break;
				case game_over:
					this.do_game_over();
					break;
				case kick_role :
					if(this.overTime <=0){
						this.overTime = System.currentTimeMillis();
						break ;
					}
					this.do_kick_role();
					break ;
				case wait_destory:
					this.do_wait_destory();
					break ;
				}
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + "clearRole error : ", e);
		}
	}
	
	private void do_pk_ready(){
		if(this.pkIndex >= Three){
			this.mapState = MapState.kick_role;
			return;
		}
		this.selfHeroEmpty = new AtomicBoolean(false);//重置
		this.rivalHeroEmpty = new AtomicBoolean(false);//重置
//		this.winFlag = new AtomicBoolean(false);//重新创建一个胜负标记
		//以下：必须先角色后对手，顺序不能更改
		this.dispose_self_hero();//更新自己的英雄信息、地图坐标等
		
		for(RoleHero rh : this.fightHeros){
			if(targetHeroMap.containsKey(rh.getHeroId())){
				if(targetHeroMap.get(rh.getHeroId()) == 0){
					pkIndex++;
				}
			}
		}
		
		this.dispose_target_hero();//更新对手的英雄信息、地图坐标等
		this.mapState = MapState.pk_begin_notice;
	}
	
	private void do_pk_begin_notice(){
		//发通知开始提示
//		int index = this.pkIndex + 1;
//		C1344_HeroArenaRoundBeginNotifyMessage message = new C1344_HeroArenaRoundBeginNotifyMessage();
//		message.setIndex((byte) index);
//		this.sendMessage(message);
		this.mapState = MapState.pk_fight;
	}
	
	private void do_pk_fight(){
		if(this.pkIndex >= fightHeros.size() ){
			this.mapState = MapState.game_over;
			return;
		}
		//自己的英雄轮空，标记为失败
		if(this.selfHeroEmpty.get()){
			this.flagOver(HeroFightStatus.Failure);
		}else if(this.rivalHeroEmpty.get()){
			//如果对手轮空，标记为胜利
			this.flagOver(HeroFightStatus.Victory);
		}
	}
	
	private void do_game_over(){
		HeroFightStatus status = HeroFightStatus.Victory;
		
		if(fightHeros.size() != targetHeroMap.size()){
			status = HeroFightStatus.Failure;
		}else{
			for(RoleHero hero : fightHeros){
				if(!targetHeroMap.containsKey(hero.getHeroId())){
					status = HeroFightStatus.Failure;
					break;
				}
				if(targetHeroMap.get(hero.getHeroId()) > 0 ){
					status = HeroFightStatus.Failure;
					break;
				}
			}
		}
		
		//发胜负结算面板，发奖励
		GameContext.getHeroArenaApp().gameOver(this.role, this.gateId, status, this.fightRoleAttr.getRoleName());
		//回血
//		if(status == HeroFightStatus.Victory){
//			float f = role.getCurHP()/(float)role.getMaxHP() ;
//			hero.setHpRate((short)(f*(RoleHero.HP_RATE_FULL))) ;
//			hero.setHpRate((short)(hero.getHpRate() + RoleHero.HP_ADD_RATIO)) ;
//			record.getTargetHeroMap().clear();
//		}
//		heroMap.put(hero.getHeroId(),hero.getHpRate());
		record.setHeroMap(heroMap);
		record.setModified(true);
		record.setTargetHeroMap(targetHeroMap);
		//将地图切换到踢人状态
		this.mapState = MapState.kick_role;
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime){
		super.broadcastMap(role, message, expireTime);
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
			
			int [] heros = new int[selectHeros.length];
			int k=0;
			for(int i=0;i<selectHeros.length;i++){
				if(selectHeros[i] != 0){
					heros[k] = selectHeros[i];
					k++;
				}
			}
			
			selectHeros = heros;
			
			int heroId = this.selectHeros[this.index];
			String roleId = this.role.getRoleId();
			RoleHero rh = GameContext.getUserHeroApp().getRoleHero(roleId, heroId);
			//英雄不存在或死亡时，标记为自己英雄轮空
			if(null == rh || GameContext.getHeroArenaApp().isHeroDead(this.role, heroId)){
				this.selfHeroEmpty.set(true);
				return;
			}
			
			//更换出战英雄
			GameContext.getHeroApp().systemAutoOnBattle(this.role, heroId);
			RoleHero battleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
			if(battleHero != null){
				battleHero.setHpRate(getHeroRate(battleHero.getHeroId()));
				notifyAttribute(battleHero);
			}
			
			for(int hId : selectHeros){
				if(hId <= 0){
					continue;
				}
				RoleHero h = GameContext.getUserHeroApp().getRoleHero(roleId,hId);
				h.setHpRate(getHeroRate(hId));
			}
			
			sendHeroSwitchUiMessage(role);
			
			//更换角色坐标
			Point point = this.getSelfPoint();
			if(null != point){
				GameContext.getUserMapApp().changeMap(this.role, point);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".dispose_self_hero error: ", e);
		}
	}
	
	private void notifyAttribute(RoleHero curHero){
		hero = curHero;
		if(heroMap.containsKey(curHero.getHeroId())){
			int hp = (int)(role.getMaxHP()*(curHero.getHpRate()/(float)RoleHero.HP_RATE_FULL)) ;
			role.setCurHP(hp);
		}else{
			role.setCurHP(role.getMaxHP());
		}
		role.getBehavior().notifyAttribute();
	}
	
	private void dispose_target_hero(){
		try{
			//如果角色英雄轮空，则不需要刷出对手英雄
			if(pkIndex >= fightHeros.size()){
				mapState = MapState.game_over;
				return;
			}
			if(this.selfHeroEmpty.get()){
				return;
			}
			int maxSize = this.fightHeros.size();
			if(this.pkIndex >= maxSize){
				//标记为对手轮空
				this.rivalHeroEmpty.set(true);
				return;
			}
			
			RoleHero pkHero = this.fightHeros.get(this.pkIndex);
			Point targetPoint = this.getTargetPoint();
			if(null == pkHero || null == this.fightRoleAttr || null == targetPoint){
				return;
			}
			
			x = targetPoint.getX();
			y = targetPoint.getY();
			
			if(npcRole != null){
				x = npcRole.getMapX();
				y = npcRole.getMapY();
			}
			int pkHeroId = pkHero.getHeroId();
			
			int onHeroId = this.fightRoleAttr.getHeroId();
			
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
			
			targetHero = pkHero;
			GoodsHero gh = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, pkHero.getHeroId());
			
			this.fightRoleAttr.getSkillInfoMap().clear();
			
			GameContext.getHeroApp().initSkill(pkHero);
			GameContext.getHeroApp().preToStore(pkHero);
			
			this.fightRoleAttr.setHeroId(pkHeroId);
			//先修改对手的英雄外形资源
			this.fightRoleAttr.setClothesResId(gh.getResId());
			this.fightRoleAttr.setGearId(gh.getGearId());
			this.fightRoleAttr.setHeroHeadId(gh.getHeadId());
			this.fightRoleAttr.setSeriesId(gh.getSeriesId());
			
			//增加新英雄的技能ID
			this.fightRoleAttr.getSkillInfoMap().putAll(Util.parseShortIntMap(pkHero.getSkills()));
			NpcInstance npcInstance = NpcInstanceFactroy.createAsyncPvpNpcInstance(this.fightRoleAttr, targetPoint.getMapid(), x, y);
			npcInstance.setMapInstance(this);
			npcInstance.setNpcBornDataIndex(-1);
			
			short rate = RoleHero.HP_RATE_FULL;
			if(!targetHeroMap.containsKey(targetHero.getHeroId())){
				targetHeroMap.put(targetHero.getHeroId(), rate);
			}else{
				rate = targetHeroMap.get(targetHero.getHeroId());
			}
			
			int hp = (int)(npcInstance.getMaxHP()*(rate/(float)RoleHero.HP_RATE_FULL)) ;
			npcInstance.setCurHP(hp);
			this.addAbstractRole(npcInstance);
			// 通知
			for (RoleInstance ri : this.getRoleList()) {
				C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
				RoleBodyItem item = Converter.getAsyncPvpRoleBodyItem(npcInstance.getRoleId(), this.fightRoleAttr, (short) x, (short) y);
				item.setHeroHeadId(gh.getHeadId());
				message.setItem(item);
				GameContext.getMessageCenter().send("", ri.getUserId(), message);
				
			}
			this.npcRole = npcInstance;
		}catch(Exception e){
			logger.error(this.getClass().getName() + ".dispose_target_hero error:", e);
			//系统刷对手失败，标记为对手轮空
			this.rivalHeroEmpty.set(true);
		}
	}
	
	private void sendMessage(Message message){
		if(null == this.role){
			return;
		}
		GameContext.getMessageCenter().sendSysMsg(this.role, message);
	}
	
	
	
	/**
	 *  被挑战者坐标
	 */
	private Point getSelfPoint(){
		HeroArenaGateConfig config = GameContext.getHeroArenaApp().getHeroArenaGateConfig(gateId);
		if(null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getMapX1(), config.getMapY1());
	}
	
	private Point getTargetPoint(){
		HeroArenaGateConfig config = GameContext.getHeroArenaApp().getHeroArenaGateConfig(gateId);
		if(null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getMapX2(), config.getMapY2());
	}
	
	private RoleHeroArenaRecord getRoleHeroArenaRecord(int roleId){
		return GameContext.getHeroArenaApp().getRoleHeroArenaRecord(roleId);
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
			List<RoleHero> heroList = GameContext.getHeroApp().getRoleSwitchableHeroList(role.getRoleId());
			RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
			if(hero == null){
				hero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			}
			for(RoleHero roleHero : heroList){
				if(roleHero == null){
					continue;
				}
				short hpRate =  roleHero.getHpRate();
				if(hero.getHeroId() == roleHero.getHeroId() 
						&& status.getBattleHeroId() == roleHero.getHeroId()){
					float f = role.getCurHP()/(float)role.getMaxHP() ;
					hpRate = (short)(f*(float)RoleHero.HP_RATE_FULL);
				}
				heroMap.put(roleHero.getHeroId(),hpRate);
			}
			
			if(npcRole != null){
				float f = npcRole.getCurHP()/(float)npcRole.getMaxHP() ;
				targetHero.setHpRate((short)(f*RoleHero.HP_RATE_FULL)) ;
				targetHeroMap.put(targetHero.getHeroId(),targetHero.getHpRate());
				record.setTargetHeroMap(targetHeroMap);
			}
			
			record.setHeroMap(heroMap);
			record.setModified(true);
			
			Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
			role.setMapId(targetPoint.getMapid());
			role.setMapX(targetPoint.getX());
			role.setMapY(targetPoint.getY());
			
			RoleInstance r = (RoleInstance)role;
			
			this.liveIfDie(r);
			
			int maxBattleCount = this.finalHerosMap.size() ;
			int [] herosRecord = new int[maxBattleCount];
			//备份历史数据
			int index = 0 ;
			for(java.util.Map.Entry<Integer, Short> entry : this.finalHerosMap.entrySet()){
				herosRecord[index++] = entry.getKey() ;
				RoleHero rHero = GameContext.getHeroApp().getRoleHero(role.getRoleId(),entry.getKey());
				rHero.setHpRate(entry.getValue());
			}
			
			GameContext.getHeroApp().systemUpdateSwitchableHero(r,herosRecord,finalHelpHerosSet);
			RoleHero hero = GameContext.getUserHeroApp().getOnBattleRoleHero(r.getRoleId());
			role.setCurHP((int)((hero.getHpRate() / (float)RoleHero.HP_RATE_FULL) * r.getMaxHP()));
			role.getBehavior().notifyAttribute();
			
			Message msg = GameContext.getHeroArenaApp().getHeroArenaPanelMessage(r);
			role.getBehavior().sendMessage(msg);
			
			
		}catch(Exception e){
			logger.error("MapLadderInstance exitMap error",e);
		}finally{
			this.destroy();
		}
	}
	
	private void liveIfDie(RoleInstance role){
    	if(null == role){
    		return ;
    	}
		try {
			if(!role.isDeath()){
				return ;
			}
			role.setCurHP(role.getMaxHP());
			// !!!!
			role.getHasSendDeathMsg().compareAndSet(true, false);
			role.getBehavior().notifyAttribute();
			// 通知队伍
			if (role.hasTeam()) {
				role.getTeam().syschDataNotify();
			}
			// 告诉客户复活
			C2001_RoleRebornRespMessage respMsg = new C2001_RoleRebornRespMessage();
			respMsg.setType(RespTypeStatus.SUCCESS);
			role.getBehavior().sendMessage(respMsg);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	@Override
    protected void enter(AbstractRole role){
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		super.enter(role);
		if(this.roleEnter.compareAndSet(false, true)){
			this.role = (RoleInstance)role;
			RoleHero battleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			List<RoleHero> finalHeroList = GameContext.getHeroApp().getRoleSwitchableHeroList(role.getRoleId());
			for(RoleHero hero : finalHeroList ){
				short hpRate = hero.getHpRate();
				if(battleHero != null){
					if(battleHero.getHeroId() == hero.getHeroId()){
						float f = role.getCurHP()/(float)role.getMaxHP() ;
						hpRate = (short)(f * (RoleHero.HP_RATE_FULL)); 
					}
				}
				this.finalHerosMap.put(hero.getHeroId(),hpRate);
			}
			
			List<RoleHero> finalHelpHeroList = GameContext.getHeroApp().helpHeros(role.getRoleId());
			int i=0;
			for(RoleHero hero : finalHelpHeroList ){
				this.finalHelpHerosSet[i] = hero.getHeroId();
				i++;
			}
			
			GameContext.getAsyncPvpApp().resetRoleSkill(this.role);
			//进入地图逻辑
			this.do_role_enter();
			//活跃度
			GameContext.getDailyPlayApp().incrCompleteTimes(this.role, 1, DailyPlayType.hero_arena, "");
		}
	}
	
	/**
	 * 角色进入状态：取相关信息、记录开始时间
	 */
	private void do_role_enter(){
		if(null == this.role){
			return;
		}
		try {
			record = getRoleHeroArenaRecord(this.role.getIntRoleId());
			this.gateId = record.getFightGateId();
			this.fightRoleId = record.getFightRoleId();
			this.selectHeros = record.getSelectHeros();
			this.fightHeros = record.getFightHeroList();
			heroMap = record.getHeroMap();
			targetHeroMap = record.getTargetHeroMap();
			pkResults = new byte[fightHeros.size()];
			
			GameContext.getHeroApp().systemUpdateSwitchableHero(role,selectHeros,null);
			this.fightRoleAttr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(this.fightRoleId);
			//标记为第一轮PK
			this.pkIndex = 0;
			//角色进入之后，将地图切换到PK准备状态
			this.mapState = MapState.pk_ready;
			//更换出战英雄
//			GameContext.getHeroApp().systemAutoOnBattle(this.role, this.selectHeros[0]);
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".do_role_enter error: ", e);
			//如果获取对战角色信息异常，则将地图切换到踢人状态
			this.mapState = MapState.kick_role;
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
	
	private void checkNpc(){
		if(hero != null){
			List<RoleHero> heroList = GameContext.getHeroApp().getRoleSwitchableHeroList(role.getRoleId());
			RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());
			for(RoleHero h : heroList){
				if(status.getBattleHeroId() == h.getHeroId()){
					if(h == null){
						continue;
					}
					float f = role.getCurHP()/(float)role.getMaxHP() ;
					hero = h;
					hero.setHpRate((short)(f*(RoleHero.HP_RATE_FULL)));
					heroMap.put(hero.getHeroId(),hero.getHpRate());
					break;
				}
			}
		}
		if(pkIndex +1<= fightHeros.size()){
			if(npcRole != null){
				if(npcRole.getCurHP() <= npcRole.getMaxHP()*0.1){
					if(npcRole.isDeath()){
						return;
					}
					npcDeath(npcRole);
				}
			}
		}
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		npc.setCurHP(0);
		super.npcDeath(npc);
		flagOver(HeroFightStatus.Victory);
		targetHeroMap.put(targetHero.getHeroId(),(short)0);
		pkIndex++;
		dispose_target_hero();
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if(victim.getRoleType() != RoleType.PLAYER) {
			return ;
		}
		
		RoleHero pkHero = this.fightHeros.get(this.pkIndex);
		targetHero = pkHero;
		float f = attacker.getCurHP()/(float)attacker.getMaxHP() ;
		targetHero.setHpRate((short)(f*(RoleHero.HP_RATE_FULL))) ;
		targetHeroMap.put(targetHero.getHeroId(),targetHero.getHpRate());
		
		flagOver(HeroFightStatus.Failure);
		//记录英雄死亡
		GameContext.getHeroArenaApp().fightDeath((RoleInstance) victim);
		this.mapState = MapState.game_over;
	}
	
	private void flagOver(HeroFightStatus status){
//		if(winFlag.get()){
//			//胜负已分,不再判断
//			return;
//		}
//		this.winFlag.set(true);
		if(this.pkIndex >= this.pkResults.length){
			this.pkResults[this.pkResults.length-1] = status.getType();
			return;
		}
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
					//出地图之后，再次打开英雄试练面板
					GameContext.getMessageCenter().sendSysMsg(role, GameContext.getHeroArenaApp().getHeroArenaPanelMessage(role));
				}
			}
		}catch(Exception ex){
			logger.error(this.getClass().getName() + ".clearRole error : ", ex);
		}
	}
	
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		return null ;
	}
	
	@Override
	public boolean canUseGoods(RoleInstance role,int goodsId){
		return false;
	}

	public NpcInstance getNpcRole() {
		return npcRole;
	}
	
	private List<HeroSwitchableInfoItem> getSwitchableHeroInfoList(String roleId){
		
		List<HeroSwitchableInfoItem> list = Lists.newArrayList() ;
		List<RoleHero> heroList = GameContext.getHeroApp().getRoleSwitchableHeroList(role.getRoleId());
		for(RoleHero h : heroList){
			if(h == null){
				continue;
			}
			HeroSwitchableInfoItem item = new HeroSwitchableInfoItem();
			item.setHeroId(h.getHeroId());
			item.setHpRate(getHeroRate(h.getHeroId()));
			list.add(item);
		}
		return list ;
	}
	
	private short getHeroRate(int heroId){
		if(heroMap.containsKey(heroId)){
			return heroMap.get(heroId);
		}
		return RoleHero.HP_RATE_FULL;
	}
	
	public void sendHeroSwitchUiMessage(RoleInstance role) {
		C1272_HeroSwitchUiRespMessage respMsg = new C1272_HeroSwitchUiRespMessage();
		respMsg.setSwitchHeroList(this.getSwitchableHeroInfoList(role.getRoleId()));
		role.getBehavior().sendMessage(respMsg);
	}
	
}
