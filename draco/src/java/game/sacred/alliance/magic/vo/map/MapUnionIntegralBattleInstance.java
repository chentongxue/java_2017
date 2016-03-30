package sacred.alliance.magic.vo.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.active.angelchest.MapRefreshStatus;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.chest.ChestRefreshInfo;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
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
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.MapInstanceEvent.EventType;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.unionbattle.config.UnionIntegral;
import com.game.draco.app.unionbattle.config.UnionIntegralNpc;
import com.game.draco.app.unionbattle.config.UnionIntegralReborn;
import com.game.draco.app.unionbattle.config.UnionIntegralRewGroup;
import com.game.draco.app.unionbattle.config.UnionIntegralSummon;
import com.game.draco.app.unionbattle.domain.UnionIntegralState;
import com.game.draco.app.unionbattle.type.IntegralBattleStateType;
import com.game.draco.app.unionbattle.type.IntegralBattleType;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.UnionIntegralRelationItem;
import com.game.draco.message.item.UnionIntegralResultItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C2543_UnionIntegralRelationBossNotifyMessage;
import com.game.draco.message.push.C2544_UnionIntegralRebornNotifyMessage;
import com.game.draco.message.push.C2545_UnionIntegralResultNotifyMessage;
import com.game.draco.message.push.C2548_NpcHpNotifyMessage;
import com.game.draco.message.response.C2372_ActiveAngelChestNewRespMessage;
import com.game.draco.message.response.C2374_ActiveAngelChestResetRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 公会战地图实例
 */
public class MapUnionIntegralBattleInstance extends MapInstance {
	private final LoopCount mapStateLoop = new LoopCount(1000);
	private final LoopCount mapRefBoxLoop = new LoopCount(6000);
	private final LoopCount mapSyncBossHp = new LoopCount(2000);
	private MapRefreshStatus status = null ;
	//结束时间
	private long overTime = -1;
	
	private int groupId;
	
	private int round;
	
	// 战斗结束2秒后踢人
	private final static int KICK_ROLE_WHEN_OVER_TIME = 6 * 1000;
	
	private long sendRewardTime = System.currentTimeMillis();

	private State mapState = State.init;
	
	private java.util.Map<String,Integer> npcTempMap = Maps.newHashMap();
	
	//<NPCID，公会ID>
	private java.util.Map<String,String> npcRelationMap = Maps.newConcurrentMap();
	
	//公会开箱子记录<公会ID，数量>
	private java.util.Map<String,Integer> openBoxMap = Maps.newConcurrentMap();
	
	//开箱子召唤NPC<召唤NPCID，指挥官ID>
	private java.util.Map<String,Set<String>> summonMap = Maps.newConcurrentMap();
	
	//对战状态
	private java.util.Map<String,Byte> stateMap = Maps.newConcurrentMap();
	
	//初始换状态
	private volatile AtomicBoolean initFlag = new AtomicBoolean(false);
	
	private int refreshIndex = 0;
	private Date refreshDate ;//开始时间(宝箱)
	private MapBoxSupport mapBox ;
	
	protected static enum State {
		init, 
		battle,
		over,
		kick,
		destory, // 关闭中
		;
	}

	public MapUnionIntegralBattleInstance(Map map) {
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

	/**
	 * 进入公会战地图
	 */
	@Override
	protected void enter(AbstractRole player) {
		if (!(player instanceof RoleInstance)) {
			return;
		}
		RoleInstance role = (RoleInstance) player;
		// 在角色进行公会战期间，角色退出公会的情况
		if (!role.hasUnion()) {
			String context = GameContext.getI18n().getText(
					TextId.UNION_NOT);
			notifyRole(role, context);
			return;
		}
		super.enter(player);
		notifyRelation(role);
		if(null != this.mapBox){
			mapBox.enter(role, this.getActiveStatus(), this.getNextOpenTime());
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
	
	private void notifyRelation(RoleInstance role){
		C2543_UnionIntegralRelationBossNotifyMessage notify = new C2543_UnionIntegralRelationBossNotifyMessage();
		List<UnionIntegralRelationItem> relationItemList = Lists.newArrayList();
		java.util.Map<String,UnionIntegralNpc> integralNpcMap = GameContext.getUnionIntegralBattleDataApp().getIntegralNpcMap();
		for(Entry<String,String> relation : npcRelationMap.entrySet()){
			UnionIntegralRelationItem item = new UnionIntegralRelationItem();
			if(!npcTempMap.containsKey(relation.getKey())){
				continue;
			}
			int npcIntRoleId = npcTempMap.get(relation.getKey());
			int num = 0;
			if(openBoxMap.containsKey(relation.getValue())){
				num = openBoxMap.get(relation.getValue());
			}
			item.setNum((byte)num);
			item.setNpcId(npcIntRoleId);
			byte relationType = ForceRelation.friend.getType();
			if(!role.getUnionId().equals(relation.getValue())){
				relationType = ForceRelation.enemy.getType();		
			}
			UnionIntegralNpc npc = integralNpcMap.get(relation.getKey());
			NpcTemplate n = GameContext.getNpcApp().getNpcTemplate(relation.getKey());
			item.setName(n.getNpcname());
			if(npc != null){
				item.setGrid((byte)npc.getId());
			}
			item.setRelation(relationType);
			
			relationItemList.add(item);
		}
		UnionIntegral unionIntegral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
		notify.setMaxNum((byte)unionIntegral.getOpenNum());
		notify.setRelationItemList(relationItemList);
		role.getBehavior().sendMessage(notify);
	}

	@Override
	public boolean canDestroy() {
		if (mapState == State.destory && !hasPlayer()) {
			return true;
		}
		return false;
	}

	@Override
	protected String createInstanceId() {
		return this.instanceId;
	}

	@Override
	protected void deathLog(AbstractRole victim) {

	}
	
	private void syncNpc(){
		Collection<NpcInstance> npcList = this.getNpcList();
		for(NpcInstance npc : npcList){
			if(!npcTempMap.containsKey(npc.getNpcid())){
				continue;
			}
			sendNpcHp(npc);
		}
	}
	
	private void sendNpcHp(NpcInstance npc){
		C2548_NpcHpNotifyMessage hpMsg = new C2548_NpcHpNotifyMessage();
		Collection<RoleInstance> roleList = getRoleList();
		for(RoleInstance r : roleList){
			hpMsg.setNpcInstanceId(npc.getIntRoleId());
			hpMsg.setCurHp(npc.getCurHP());
			String unionId = npcRelationMap.get(npc.getNpcid());
			int openNum = 0;
			if(openBoxMap.containsKey(unionId)){
				openNum = openBoxMap.get(unionId);
			}
			hpMsg.setNum((byte)openNum);
			r.getBehavior().sendMessage(hpMsg);
		}
	}

	/**
	 * 退出地图，返回上之前的地点
	 */
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		Point targetPoint = ((RoleInstance) role).getCopyBeforePoint();
		role.setMapId(targetPoint.getMapid());
		role.setMapX(targetPoint.getX());
		role.setMapY(targetPoint.getY());
		this.perfectBody(role);
	}

	/**
	 * 踢人所有角色回到上给自之前的传送点
	 */
	private void kickRole() {
		try {
			if (Util.isEmpty(this.getRoleList())) {
				return;
			}
			for (RoleInstance role : this.getRoleList()) {
				this.kickRole(role);
			}
		} catch (Exception ex) {
			logger.error("mapUnionBattleInstance.kickRole() err:", ex);
		}
	}
	/**
	 * 清除【公会战地图】中，退出公会的角色
	 */
	private void filteRoleNotUnionMember() {
		try {
			if (Util.isEmpty(this.getRoleList())) {
				return;
			}
			for (RoleInstance role : this.getRoleList()) {
				if(!role.hasUnion()){
					this.kickRole(role);
				}
			}
		} catch (Exception ex) {
			logger.error("mapUnionBattleInstance.kickRole() err:", ex);
		}
	}
	
	/**
	 * 不弹复活面板
	 */
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		return null ;
	}

	/**
	 * 角色死亡 清空被击杀者
	 */
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if (victim.getRoleType() != RoleType.PLAYER) {
			return;
		}
		RoleInstance role = (RoleInstance)victim;
		C2544_UnionIntegralRebornNotifyMessage notify = new C2544_UnionIntegralRebornNotifyMessage();
		java.util.Map<Byte,UnionIntegralReborn> rebornMap = GameContext.getUnionIntegralBattleDataApp().getIntegralRebornMap();
		if(Util.isEmpty(rebornMap)){
			return;	
		}
		
//		List<UnionIntegralRebornItem> rebornItemList = Lists.newArrayList();
//		for(Entry<Byte,UnionIntegralReborn> reborn : rebornMap.entrySet()){
//			UnionIntegralRebornItem item = new UnionIntegralRebornItem();
//			item.setId(reborn.getKey());
//			item.setName(reborn.getValue().getRebornName());
//			rebornItemList.add(item);
//		}
//		notify.setRebornItemList(rebornItemList);
		role.getBehavior().sendMessage(notify);
	}

	/**
	 * NPC死亡
	 */
	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim) {
		
		if (victim.getRoleType() != RoleType.NPC) {
			return;
		}
		
		int killRoleId = -1;
		if(attacker.getRoleType() == RoleType.PET){
			RolePet pet = (RolePet)attacker;
			killRoleId = pet.getMasterRole().getIntRoleId();
		}
		
		if(attacker.getRoleType() == RoleType.PLAYER){
			RoleInstance role = (RoleInstance)attacker;
			killRoleId = role.getIntRoleId();
		}
		
		String killUnionId = GameContext.getUnionApp().getUnionId(killRoleId);
		
		if("".equals(killUnionId) && attacker.getRoleType() == RoleType.NPC){
			NpcInstance npc = (NpcInstance)attacker;
			killUnionId = npc.getSummonRoleId();
		}
		
		NpcInstance npc = (NpcInstance) victim;
		if(!summonMap.containsKey(npc.getNpcid()) && !npcRelationMap.containsKey(npc.getNpcid())){
			super.npcDeathDiversity(attacker, victim);
			return;
		}
		
		String unionId = "";
		if(victim.getRoleType() == RoleType.NPC){
			unionId = npc.getSummonRoleId();
		}
		
		//如果召唤NPC被击杀
		if(summonMap.containsKey(npc.getNpcid())){
			//清指挥官BUFF
			java.util.Map<String,UnionIntegralSummon> integralSummonMap = GameContext.getUnionIntegralBattleDataApp().getIntegralSummonMap();
			if(Util.isEmpty(integralSummonMap)){
				return;
			}
			UnionIntegralSummon summon = integralSummonMap.get(npc.getNpcid());
			clearCommanderBuff(npc,summon.getBuffId());
			
			//奖励击杀公会
			GameContext.getUnionIntegralBattleApp().awardDkp(killUnionId,summon.getKillDkp(),this.getInstanceId(),true);
		}
		// 指挥官被击杀
		if(npcRelationMap.containsKey(npc.getNpcid())){
			
			if(this.mapState == State.over){
				return;
			}

			sendNpcHp(npc);
			
			UnionIntegralNpc commander = GameContext.getUnionIntegralBattleDataApp().getIntegralNpcMap().get(npc.getNpcid());
			//奖励击杀公会
			if(commander != null){
				awardKillCommander(killUnionId,commander.getId());
			}
			
			synchronized(stateMap){
				stateMap.put(npc.getNpcid(),IntegralBattleStateType.failure.getType());
				int liveSize = 0;
				for(Entry<String,Byte> state : stateMap.entrySet()){
					if(state.getValue() == IntegralBattleStateType.failure.getType()){
						liveSize++;
					}
				}
				IntegralBattleType integralType = IntegralBattleType.get(liveSize);
				byte integral = (byte)integralType.getOtherValue();
				//修改状态
				GameContext.getUnionIntegralBattleApp().updUnionIntegralState(round, unionId, IntegralBattleStateType.failure.getType(),integral,true);
				//添加积分
				GameContext.getUnionIntegralBattleApp().addUnionIntegral(unionId, integral,0,true);
				
				if(isOver()){
					String winUunionId = getWinUnion();
					if(winUunionId == null){
						return;
					}
					
					setWinCommandId(winUunionId);
					
					GameContext.getUnionIntegralBattleApp().updUnionIntegralState(round, winUunionId, IntegralBattleStateType.success.getType(),(byte)IntegralBattleType.one.getValue(),true);
					//添加积分
					GameContext.getUnionIntegralBattleApp().addUnionIntegral(winUunionId, (byte)IntegralBattleType.one.getValue(),0,true);
				}
			}
		}
	}
	
	public void setWinCommandId(String unionId){
		for(Entry<String,String> realtion : npcRelationMap.entrySet()){
			if(realtion.getValue().equals(unionId)){
				String npcId = realtion.getKey();
				stateMap.put(npcId,IntegralBattleStateType.success.getType());
				return;
			}
		}
		
	}
	
	private boolean isActiveOver(){
		
		if(System.currentTimeMillis() > getOverTime()){
			//活动结束还没分出胜负 全部发参与奖励
			mapState = State.over;
			return true;
		}
		
		UnionIntegral integral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
		Active active = GameContext.getActiveApp().getActive(integral.getActiveId());
		if(!active.isTimeOpen()){
			mapState = State.over;
			return true;
		}
		return false;
	}
	
	/**
	 * 通知结果
	 */
	private void notifyResult(){
		Collection<RoleInstance> roleList = this.getRoleList();
		if(Util.isEmpty(roleList)){
			return;
		}
		C2545_UnionIntegralResultNotifyMessage notifyMsg = new C2545_UnionIntegralResultNotifyMessage();
		List<UnionIntegralState> integralStateList = GameContext.getUnionIntegralBattleApp().getIntegralGroupInfoList(groupId);
		for(RoleInstance r : roleList){
			List<UnionIntegralResultItem> resultItemList = Lists.newArrayList();
			for(UnionIntegralState integralState : integralStateList){
				UnionIntegralResultItem item = new UnionIntegralResultItem();
				Union union = GameContext.getUnionApp().getUnion(integralState.getUnionId());
				if(union == null){
					continue;
				}
				item.setName(union.getUnionName());
				item.setState(integralState.getState());
				item.setIntegral(integralState.getIntegral());
				resultItemList.add(item);
				notifyMsg.setResultItemList(resultItemList);
			}
			r.getBehavior().sendMessage(notifyMsg);
		}
	}
	
	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}

	@Override
	public void useGoods(int goodsId) {

	}

	/**
	 * 主线程 
	 * 公会战结束踢人，指挥官被击杀踢人
	 */
	@Override
	protected void updateSub() throws ServiceException {
		
		super.updateSub();
		
		if (this.mapStateLoop.isReachCycle()) {
			
			switch (mapState) {
				case init: // 如果是创建状态，初始化
					if(initFlag.compareAndSet(false, true)){
						activeOpen();
					}
					mapState = State.battle; 
					break;
				case battle: 
					isOver();
					isActiveOver();
					break;
				case over: 
					//统计活着的指挥官
					statistics();
					//发弹板通知公会胜负积分
					notifyResult();
					mapState = State.kick;
					sendRewardTime = System.currentTimeMillis();
					break;
				case kick://踢人
					if(System.currentTimeMillis() >= sendRewardTime + KICK_ROLE_WHEN_OVER_TIME){
						kickRole();
						this.mapState = State.destory;
					}
					break;
				case destory:
					if (mapState == State.destory) {
						this.destroy();
					}
					break;
				default:
					break;
			}
			filteRoleNotUnionMember();
		}
		if(mapRefBoxLoop.isReachCycle()){
			//刷宝箱
			activeRuning();
		}
		if(mapSyncBossHp.isReachCycle()){
			syncNpc();
		}
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
	public void destroy() {
		super.destroy();
		if(!Util.isEmpty(npcTempMap)){
			npcTempMap.clear();
		}
		if(!Util.isEmpty(npcRelationMap)){
			npcRelationMap.clear();
		}
		if(!Util.isEmpty(openBoxMap)){
			openBoxMap.clear();
		}
		if(!Util.isEmpty(summonMap)){
			summonMap.clear();
		}
		if(!Util.isEmpty(stateMap)){
			stateMap.clear();
		}
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		if(null == mapBox){
			return ;
		}
		this.mapBox.damageTaken(attacker, victim, hurt);
	}
	
	/**
	 * 检查活着的指挥官数量
	 * @return
	 */
	private boolean isOver(){
		int size = 0;
		for(Entry<String,Byte> state : stateMap.entrySet()){
			if(state.getValue() == IntegralBattleStateType.nu.getType()){
				size++;
			}
		}
		if(size == 1){
			this.mapState = State.over; 
			return true;
		}
		return false;
	}
	
	private void statistics(){
		
		for(Entry<String,Byte> state : stateMap.entrySet()){
			if(state.getValue() != IntegralBattleStateType.nu.getType()){
				continue;
			}
			String unionId = npcRelationMap.get(state.getKey());
			if(Util.isEmpty(unionId)){
				continue;
			}
			//改成平状态
			GameContext.getUnionIntegralBattleApp().updUnionIntegralState(round,unionId,IntegralBattleStateType.all.getType(),IntegralBattleStateType.all.getValue(),true);
			//加积分
			GameContext.getUnionIntegralBattleApp().addUnionIntegral(unionId, IntegralBattleStateType.all.getValue(),0,true);
		}
		cleanBox();
	}
	
	/**
	 * 检查活着的指挥官数量
	 * @return
	 */
	private String getWinUnion(){
		for(Entry<String,Byte> state : stateMap.entrySet()){
			if(state.getValue() != IntegralBattleStateType.nu.getType()){
				continue;
			}
			return npcRelationMap.get(state.getKey());
		}
		return null;
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc,
			RoleInstance target) {
		return getForceRelation(target,npc);
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role,
			RoleInstance target) {
		if (role.hasUnion() && target.hasUnion()) {
			if (role.getUnionId().equals(target.getUnionId())) {
				return ForceRelation.friend;
			}
		}
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role,
			NpcInstance target) {
		if(role.getUnionId().equals(target.getSummonRoleId())){
			return ForceRelation.friend;
		}
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, NpcInstance target) {
		if(npc.getSummonRoleId().equals(target.getSummonRoleId())){
			return ForceRelation.friend;
		}
		return ForceRelation.enemy;
	}
	
	/**
	 * 地图广播
	 * 
	 * @param content
	 * @param channelType
	 */
	public void broadCast(String content, byte channelType) {
		GameContext.getChatApp().sendSysMessage(ChatSysName.System,
				ChannelType.getChannelType(channelType), content, null, null);
	}

	/**
	 * 通知玩家
	 */
	public void notifyRole(RoleInstance role, String context) {
		C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(context);
		role.getBehavior().sendMessage(msg);
	}
	
	@Override
	public boolean mustRunMapLoop(){
		//活动没开启的时候强制进入地图主循环逻辑便于自动销毁地图
		return !GameContext.getUnionBattleApp().isUnionBattleActiveTimeOpen(); 
	}
	
	public long getOverTime() {
		return overTime;
	}

	public void setOverTime(long overTime) {
		this.overTime = overTime;
	}
	
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	
	/**
	 * 初始化NPC
	 */
	public void initNpc(){
		UnionIntegral integral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
		
		java.util.Map<String,UnionIntegralNpc> npcMap = GameContext.getUnionIntegralBattleDataApp().getIntegralNpcMap();
		java.util.Map<Integer,Set<String>> groupMap = GameContext.getUnionIntegralBattleApp().getIntegralGroupMap(false);
		
		Set<String> unionSet = groupMap.get(groupId);
		
		java.util.Map<String,UnionIntegralState> integralStateMap = GameContext.getUnionIntegralBattleApp().getIntegralGroupInfoMap(groupId);
		
		for(String unionId : unionSet){
			Union union = GameContext.getUnionApp().getUnion(unionId);
			if(union == null){
				continue;
			}
			
			UnionIntegralState unionIntegralState = integralStateMap.get(unionId);
			
			for(Entry<String,UnionIntegralNpc> npc : npcMap.entrySet()){
				if(unionIntegralState.getGrid() != npc.getValue().getId()){
					continue;
				}
				Point point = new Point(integral.getMapId(),npc.getValue().getMapX(),npc.getValue().getMapY());
				NpcTemplate npcTemplate = GameContext.getNpcApp()
						.getNpcTemplate(npc.getKey());
				if (npcTemplate == null) {
					logger.error("mapUnionIntegralBattleInstance.initNpc() fail: npcId = "
							+ npc.getKey() + "getNpcTemplate  fail");
					continue;
				}
				npcRelationMap.put(npcTemplate.getNpcid(),unionId);
				NpcInstance npcInstance = summonCreateNpc(npcTemplate.getNpcid(), point.getX(), point.getY(),unionId);
				npcTempMap.put(npcTemplate.getNpcid(), npcInstance.getIntRoleId());
				stateMap.put(npcTemplate.getNpcid(), IntegralBattleStateType.nu.getType());
				GameContext.getUnionIntegralBattleApp().modifyTargetMap(unionId, npcTemplate.getNpcid());
			}
		}
		
	}
	
	@Override
	public void doEvent(RoleInstance role, MapInstanceEvent event) {
		if (null == event) {
			return;
		}
		
		this.mapBox.doEvent(role, event);
		
		if (event.getEventType() != EventType.chestOpenSuccess) {
			return;
		}
		
		UnionIntegral integral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
		
		if(!role.hasUnion()){
			return;
		}
		
		//最大开箱子数量
		int num = integral.getOpenNum();
		
		int openNum = 1;
		if(openBoxMap.containsKey(role.getUnionId())){
			openNum = openBoxMap.get(role.getUnionId());
			openNum++;
		}
		openBoxMap.put(role.getUnionId(), openNum);
		
		if(openNum%num == 0){
			openBoxMap.put(role.getUnionId(), 0);
			summonNpc(role);
		}
	}
	
	/**
	 * 召唤NPC
	 * @param unionId
	 */
	private void summonNpc(RoleInstance role){
		Random rand = new Random();
		java.util.Map<String,UnionIntegralSummon> integralSummonMap = GameContext.getUnionIntegralBattleDataApp().getIntegralSummonMap();
		if(Util.isEmpty(integralSummonMap)){
			return;
		}
		List<UnionIntegralSummon> list = Lists.newArrayList();
		for(Entry<String,UnionIntegralSummon> summon : integralSummonMap.entrySet()){
			list.add(summon.getValue());
		}
		int randId = rand.nextInt(list.size());
		UnionIntegralSummon summon = list.get(randId);
		Set<String> commanderSet = Sets.newHashSet();
		NpcInstance summonNpc = summonCreateNpc(summon.getKey(), role.getMapX(),role.getMapY(),role.getUnionId());
		for(Entry<String,String> relation : npcRelationMap.entrySet()){
			int npcId = npcTempMap.get(relation.getKey());
			NpcInstance npc = getNpcInstance(String.valueOf(npcId));
			if(npc == null || npc.isDeath()){
				continue;
			}
			//如果敌方
			if(summon.getRelation() == 0){
				if(npc.getSummonRoleId().equals(role.getUnionId())){
					continue;
				}
			}
			//如果友方
			if(summon.getRelation() == 1){
				if(!npc.getSummonRoleId().equals(role.getUnionId())){
					continue;
				}
			}
			//给指挥官添加BUFF
			BuffStat buffStat = initBuffStat(summonNpc,npc,summon.getBuffId());
			if(buffStat != null){
				npc.addBuffStat(buffStat);
				commanderSet.add(npc.getNpcid());
			}
		}
		summonMap.put(summon.getKey(), commanderSet);
		//奖励召唤公会
		GameContext.getUnionIntegralBattleApp().awardDkp(role.getUnionId(),summon.getSummonDkp(),this.getInstanceId(),true);
	}
	
	/**
	 * 初始化buff
	 * @param npc
	 * @param buffId
	 * @return
	 */
	private BuffStat initBuffStat(NpcInstance summon,NpcInstance npc,short buffId){
		try{
			Buff buff = GameContext.getBuffApp().getBuff(buffId);
			if(buff == null){
				return null;
			}
			BuffStat stat = new BuffStat(buff,1,buff.getIntervalTime(1));
			stat.setBuffId(buff.getBuffId());
			stat.setOwner(npc);
			stat.setCaster(summon);
			stat.setBuffInfo(null);
			stat.setContextInfo(null);
			stat.setLayer((short)1);
			stat.setRemainTime(buff.getPersistTime());
			Date now = new Date();
			stat.setLastExecuteTime(now.getTime());
			stat.setCreateTime(now);
			return stat;
		}catch(Exception e){
			logger.error("MapUnionIntegralBattleInstance initBuffStat",e);
			return null;
		}
	}
	
	/**
	 * 清指挥官BUFF
	 * @param npc
	 * @param buffId
	 */
	private void clearCommanderBuff(NpcInstance npc,short buffId){
		Set<String> commanderSet = summonMap.get(npc.getNpcid());
		if(Util.isEmpty(commanderSet)){
			return;
		}
		for(String npcId : commanderSet){
			NpcInstance commander = getNpcInstance(npcId);
			if(commander == null || commander.isDeath()){
				continue;
			}
			BuffStat buffStat = commander.getBuffStat(buffId);
			if(buffStat != null){
				commander.delBuffStat(buffStat);
			}
		}
	}
	
	private void awardKillCommander(String unionId,int id){
		UnionIntegralNpc commander = GameContext.getUnionIntegralBattleDataApp().getIntegralNpc(id);
		List<UnionIntegralRewGroup> rewGroupList = GameContext.getUnionIntegralBattleDataApp().getIntegralRewGroupList(commander.getRewGroupId());
		
		if(Util.isEmpty(rewGroupList)){
			return;
		}
		
		GameContext.getUnionIntegralBattleApp().reward(-1,unionId, this.instanceId, rewGroupList, true,true);
		
	}
	
}
