//package sacred.alliance.magic.vo;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import sacred.alliance.magic.app.faction.war.config.FactionWarAwardRoleConfig;
//import sacred.alliance.magic.app.map.Map;
//import sacred.alliance.magic.app.map.data.MapNpcBornData;
//import sacred.alliance.magic.app.map.data.NpcBorn;
//import sacred.alliance.magic.base.BindingType;
//import sacred.alliance.magic.base.FactionWarResultType;
//import sacred.alliance.magic.base.ForceRelation;
//import sacred.alliance.magic.base.MapRemainTimeType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.RebornType;
//import sacred.alliance.magic.base.RoleType;
//import sacred.alliance.magic.component.id.IdFactory;
//import sacred.alliance.magic.component.id.IdType;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.scheduler.job.LoopCount;
//import sacred.alliance.magic.util.Util;
//
//import com.game.draco.GameContext;
//import com.game.draco.app.mail.domain.Mail;
//import com.game.draco.app.mail.type.MailSendRoleType;
//import com.game.draco.app.npc.domain.NpcInstance;
//import com.game.draco.message.item.DeathNotifySelfItem;
//import com.game.draco.message.item.FactionWarRankItem;
//import com.game.draco.message.push.C0208_CopyRemainTimeNotifyMessage;
//import com.game.draco.message.push.C1740_FactionWarOverNotifyMessage;
//
//public class MapFactionWarInstance extends MapCopyInstance{
//	private FactionWarMapState mapState = FactionWarMapState.wait_begin;
//	private LoopCount whoWinLoopCount = new LoopCount(1000);
//	private LoopCount notifyLoopCount = new LoopCount(2 * 1000);
//	private long sendRewardTime = System.currentTimeMillis();
//	private AtomicBoolean winFlag = new AtomicBoolean(false);
//	private Object winLock = new byte[0] ;
//	//战斗结束后多长时间踢人
//	private final static int KICK_ROLE_WHEN_OVER_TIME = 2*1000 ;
//	private List<NpcBorn> npcBornList;
//	//存放roleName
//	private java.util.Map<String,String> roleNameMap = new HashMap<String,String>();
//	private FactionWarMatch match;
//	private int maxBattleTime = 1000 ;
//	private long beginTime = 0;
//	private FactionWarInfo winFaction = null;
//	private List<FactionWarRankItem> broadcastWinList = new ArrayList<FactionWarRankItem>();
//	private List<FactionWarRankItem> broadcastLoseList = new ArrayList<FactionWarRankItem>();
//	private AtomicBoolean notifyEnter = new AtomicBoolean(false);
//	private LoopCount destoryKickLoopCount = new LoopCount(2000);
//	private LoopCount warRoleLoopCount = new LoopCount(5 * 1000);
//	private int whoWinCount = 0;//计数用
//	
//	protected static enum FactionWarMapState {
//		wait_begin,
//		enter,
//		begin,
//		over,
//		kick_role,
//		destory,
//		;
//	}
//	
//	public MapFactionWarInstance(Map map, FactionWarMatch match) {
//		super(map);
//		this.match = match;
//		this.maxBattleTime = this.match.getConfig().getBattleTime();
//		this.beginTime = this.match.getBeginTime();
//	}
//	
//	@Override
//	protected void updateSub() throws ServiceException {
//		if(FactionWarMapState.wait_begin == this.mapState) {
//			long now = System.currentTimeMillis();
//			if(now >= this.match.getEnterTime()){
//				//刷新鼓舞NPC
//				createNpc();
//				//开始广播
//				GameContext.getFactionWarApp().broadCastBegin(match);
//				this.mapState = FactionWarMapState.enter;
//			}
//			return;
//		}
//		super.updateSub();
//		if(FactionWarMapState.enter == this.mapState) {
//			//TODO创建NPC，鼓舞用
//			long now = System.currentTimeMillis();
//			if(notifyLoopCount.isReachCycle()){
//				this.notifyEnterWar();
//			}
//			if(now >= this.match.getBeginTime()){
//				//进入准备阶段,此时清除障碍物
//				this.clearBaffle();
//				this.mapState = FactionWarMapState.begin;
//			}
//			return;
//		}
//		
//		if (mapState == FactionWarMapState.begin) {
//			if(whoWinLoopCount.isReachCycle()){
//				//判断输赢
//				whoWinTeam();
//			}
//			if(warRoleLoopCount.isReachCycle()){
//				this.kickNotWarRole();
//			}
//			return;
//		}
//		if(mapState == FactionWarMapState.over){
//			try {
//				// 计算战斗奖励
//				this.reward();
//				// 发送弹板
//				this.notifyOverMessage();
//			}finally{
//				//踢人后，build下一轮对战
//				GameContext.getFactionWarApp().factionWarOver(match, this.winFaction.getFactionId());
//				//将地图状态设置为踢人
//				sendRewardTime = System.currentTimeMillis();
//				this.mapState = FactionWarMapState.kick_role ;
//			}
//			return ;
//		} 
//		if(mapState == FactionWarMapState.kick_role){
//			if(System.currentTimeMillis()-sendRewardTime > KICK_ROLE_WHEN_OVER_TIME){
//				//奖励发送完毕后1s钟后开始t人
//				this.clearRole();
//				this.mapState = FactionWarMapState.destory ;
//			}
//			return;
//		}
//		if(mapState == FactionWarMapState.destory){
//			if(destoryKickLoopCount.isReachCycle()){
//				this.clearRole();
//			}
//			return;
//		}
//	}
//	
//	protected void sendRemainTime(RoleInstance role){
//		try {
//			long lifeTime = System.currentTimeMillis();
//			if(lifeTime >= beginTime){
//				return ;
//			}
//			C0208_CopyRemainTimeNotifyMessage notifyMsg = new C0208_CopyRemainTimeNotifyMessage();
//			notifyMsg.setType(MapRemainTimeType.Arean.getType());
//			notifyMsg.setTime((int)((beginTime - lifeTime)/1000));
//			role.getBehavior().sendMessage(notifyMsg);
//		}catch(Exception ex){
//			logger.error("",ex);
//		}
//	}
//
//	@Override
//    protected void enter(AbstractRole player){
//    	RoleInstance role = (RoleInstance)player;
//    	String roleId = role.getRoleId();
//    	if(this.roleNameMap.containsKey(roleId)){
//    		return;
//    	}
//    	if(role.getFactionId().equals(this.match.getFaction1().getFactionId())) {
//    		this.match.getFaction1().getRoleSet().add(roleId);
//    		this.match.getFaction1().getFactionKilledMap().put(roleId, 0);
//    	}else{
//    		this.match.getFaction2().getRoleSet().add(roleId);
//    		this.match.getFaction2().getFactionKilledMap().put(roleId, 0);
//    	}
//    	roleNameMap.put(role.getRoleId(), role.getRoleName());
//    	this.sendRemainTime(role);
//	}
//    
//	private void reward(){
//		reward(this.winFaction, FactionWarResultType.Win);
//		reward(this.match.getOtherFaction(this.winFaction), FactionWarResultType.Lose);
//	}
//	
//	private void reward(FactionWarInfo info, FactionWarResultType result) {
//		if(Util.isEmpty(info.getFactionKilledMap())){
//			return;
//		}
//		//排序
//		List<Entry<String, Integer>> list = Util.getSortedMapEntryListByValue(info.getFactionKilledMap());
//		for(Entry<String, Integer> entry : list){
//			if(null == entry){
//				continue;
//			}
//			String roleId = entry.getKey();
//			int killedNum = info.getFactionKilledMap().get(roleId);
//			this.reward(roleId, info.getFactionId(), info.getRounds(), killedNum, result);
//		}
//	}
//	
//	private void reward(String roleId, String factionId,int rounds, int killNum, FactionWarResultType result){
//		FactionWarAwardRoleConfig config = GameContext.getFactionWarApp().getRoleAwardConfig(rounds);
//		if(null == config){
//			return;
//		}
//		int silverMoney = 0;
//		int factionContribute = 0;
//		int goodsId = 0;
//		int goodsNum = 0;
//		byte goodsBind = 0;
//		String mailTitle = config.getMailTitle();
//		String mailContent = "";
//		
//		if(result == FactionWarResultType.Win) {
//			silverMoney = config.getWinMoney(killNum);
//			factionContribute = config.getWinContribution(killNum);
//			goodsId = config.getWinGoodsId();
//			goodsNum = config.getWinGoodsNum();
//			goodsBind = config.getWinGoodsBind();
//			mailContent = config.getWinMailContent();
//			
//			FactionWarRankItem item = new FactionWarRankItem();
//			item.setRoleName(this.roleNameMap.get(roleId));
//			item.setContribute(factionContribute);
//			item.setSilverMoney(silverMoney);
//			item.setKillNum((byte)killNum);
//			item.setPosition((byte)GameContext.getFactionApp().getFactionRolePosition(roleId, factionId));
//			this.broadcastWinList.add(item);
//		}else{
//			silverMoney = config.getLoseMoney(killNum);
//			factionContribute = config.getLoseContribution(killNum);
//			goodsId = config.getLoseGoodsId();
//			goodsNum = config.getLoseGoodsNum();
//			goodsBind = config.getLoseGoodsBind();
//			mailContent = config.getLoseMailContent();
//			
//			FactionWarRankItem item = new FactionWarRankItem();
//			item.setRoleName(this.roleNameMap.get(roleId));
//			item.setContribute(factionContribute);
//			item.setSilverMoney(silverMoney);
//			item.setKillNum((byte)killNum);
//			item.setPosition((byte)GameContext.getFactionApp().getFactionRolePosition(roleId, factionId));
//			this.broadcastLoseList.add(item);
//		}
//		this.sendMail(roleId, silverMoney, factionContribute, goodsId, goodsNum, goodsBind, mailTitle, mailContent);
//	}
//	
//	private void sendMail(String roleId, int silverMoney, int contribute, int goodsId, int goodsNum, byte goodsBind, String title, String Content) {
//		OutputConsumeType ocType = OutputConsumeType.faction_war_role_reward;
//		try {
//			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
//			mail.setSendRole(MailSendRoleType.System.getName());
//			mail.setTitle(title);
//			mail.setContent(Content);
//			mail.setRoleId(roleId);
//			mail.setSendSource(ocType.getType());
//			mail.setSilverMoney(silverMoney);
//			mail.setContribute(contribute);
//			mail.addMailAccessory(goodsId, goodsNum, BindingType.get(goodsBind));
//			GameContext.getMailApp().sendMail(mail);
//		}catch(Exception e){
//			logger.error("sendGoodsByMail error",e);
//		}
//	}
//	
//	@Override
//	public boolean canDestroy() {
//		if (mapState == FactionWarMapState.destory && !hasPlayer()) {
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	protected String createInstanceId() {
//		instanceId = "fac_war_" + instanceIdGenerator.incrementAndGet();
//		return instanceId;
//	}
//
//
//	@Override
//	protected void deathLog(AbstractRole victim) {
//		
//	}
//
//
//	@Override
//	public void exitMap(AbstractRole role) {
//		super.exitMap(role);
//		Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
//		role.setMapId(targetPoint.getMapid());
//		role.setMapX(targetPoint.getX());
//		role.setMapY(targetPoint.getY());
//		this.perfectBody(role);
//	}
//
//	private void clearRole(){
//		// 踢人
//		try {
//			if(Util.isEmpty(this.getRoleList())){
//				return;
//			}
//			for (RoleInstance role : this.getRoleList()) {
//				this.kickRole(role);
//			}
//		}catch(Exception ex){
//			
//		}
//	}
//	
//	private void clearBaffle(){
//		Collection<NpcInstance> list = new ArrayList<NpcInstance>();
//		list.addAll(this.baffleList);
//		this.baffleList.clear();
//		for(NpcInstance npc : list){
//			this.mapBaffleDeath(npc);
//		}
//		list.clear();
//		list = null ;
//	}
//	
//	private void notifyEnterWar(){
//		try{
//			//通知不在地图内的玩家进入
//			if(notifyEnter.get()){
//				return;
//			}
//			notifyEnter.set(true);
//			GameContext.getFactionWarApp().notifyEnterFactionWar(match, this.instanceId);
//		}catch(Exception e){
//			logger.error("notifyEnterWar error:", e);
//		}
//	}
//	
//	private boolean isBattleTimeOver(){
//		return System.currentTimeMillis()-this.beginTime > this.maxBattleTime*60*1000 ;
//	}
//	
//	/**
//	 * 表示战斗结束
//	 * @param denfendWin 防守方是否胜利
//	 */
//	private void flagOver(FactionWarInfo winFaction){
//		this.winFaction = winFaction ;
//		this.winFlag.set(true);
//		try {
//			mapState = FactionWarMapState.over;
//		} catch (RuntimeException e) {
//			logger.error("MapCampWarInstance.flagOver error: ", e);
//		}
//	}
//	
//	private boolean whoWinTeam() throws ServiceException {
//		synchronized (winLock) {
//			if(winFlag.get()){
//				//胜负已分,不再判断
//				return false;
//			}
//			try{
//				if(this.isBattleTimeOver()){
//					//战斗已经超时,根据规则获取胜利方
//					FactionWarInfo info = GameContext.getFactionWarApp().getTimeOverWinFaction(this.match, this.instanceId);
//					this.flagOver(info);
//					return true;
//				}
//				
//				FactionWarInfo info1 = this.match.getFaction1();
//				FactionWarInfo info2 = this.match.getFaction2();
//				int liveSize1 = GameContext.getFactionWarApp().getMapLiveListSize(info1.getRoleSet(), this.instanceId, info1.getFactionId());
//				int liveSize2 = GameContext.getFactionWarApp().getMapLiveListSize(info2.getRoleSet(), this.instanceId, info2.getFactionId());
//				if(0 == liveSize1 && 0 == liveSize2){
//					//平局
//					FactionWarInfo info = GameContext.getFactionWarApp().getTimeOverWinFaction(this.match, this.instanceId);
//					this.flagOver(info);
//					return true;
//				}
//				if(0 == liveSize2){
//					//faction1获得胜利
//					this.flagOver(info1);
//					return true;
//				}
//				if(0 == liveSize1){
//					//faction2获得胜利
//					this.flagOver(info2);
//					return true;
//				}
//			}catch(Exception e){
//				//容错，如果抛异常，则可能导致，无法完成本场比赛，如果超过10次，则faction1获胜
//				logger.error("whoWinTeam error",e);
//				if(whoWinCount > 10){
//					this.flagOver(this.match.getFaction1());
//					return true;
//				}
//				this.whoWinCount++;
//			}
//			return false ;
//		}
//	}
//	
//	@Override
//	public List<NpcBorn> getNpcBornList(){
//		if(null == this.npcBornList){
//			this.npcBornList = new ArrayList<NpcBorn>();
//			MapNpcBornData bornData = this.map.getNpcBornData();
//			if(null != bornData){
//				this.npcBornList.addAll(bornData.getNpcborn());
//			}
//		}
//		return this.npcBornList;
//	}
//	
//	/**
//	 * 刷新鼓舞NPC
//	 */
//	private void createNpc(){
//		try{
//			GameContext.getFactionWarApp().refreshFactionSoul(match, this);
//		}catch(Exception e){
//			logger.error("createNpc error:", e);
//		}
//	}
//	
//	@Override
//	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
//		if(attacker.getRoleType() != RoleType.PLAYER || victim.getRoleType() != RoleType.PLAYER){
//			return;
//		}
//		RoleInstance attackerRole = (RoleInstance)attacker;
//		RoleInstance victimRole = (RoleInstance)victim;
//		FactionWarInfo info = getFactionWarInfoByRole(victimRole);
//		if(null != info){
//			info.getRoleSet().remove(victimRole.getRoleId());
//			info.getRoleDeathSet().add(victimRole.getRoleId());
//		}
//		this.addKilled(attackerRole);
//		try {
//			this.whoWinTeam();
//		} catch (ServiceException e) {
//		}
//	}
//	
//	private FactionWarInfo getFactionWarInfoByRole(RoleInstance role){
//		if(!role.hasFaction()){
//			return null;
//		}
//		FactionWarInfo info1 = this.match.getFaction1();
//		FactionWarInfo info2 = this.match.getFaction2();
//		if(role.getFactionId().equals(info1.getFactionId())){
//			return info1;
//		}
//		return info2;
//	}
//	
//	/**
//	 * 增加杀人数&增加积分
//	 * @param role
//	 * @param point
//	 */
//	private void addKilled(RoleInstance role){
//		try{
//			if(null == role) {
//				return;
//			}
//			String roleId = role.getRoleId();
//			java.util.Map<String,Integer> killMap = null;
//			if(role.getFactionId().equals(this.match.getFaction1().getFactionId())) {
//				killMap = this.match.getFaction1().getFactionKilledMap();
//			}else{
//				killMap = this.match.getFaction2().getFactionKilledMap();
//			}
//			
//			int value = 0;
//			if(!killMap.containsKey(roleId)) {
//				return;
//			}
//			value = killMap.get(roleId);
//			value++ ;
//			killMap.put(roleId, value);
//			//杀人数喊话
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	/**
//	 * 广播排名消息
//	 * @param hurtList
//	 */
//	private void notifyOverMessage(){
//		this.notifyOverMessage(FactionWarResultType.Win);
//		this.notifyOverMessage(FactionWarResultType.Lose);
//	}
//	
//	private void notifyOverMessage(FactionWarResultType type){
//		try{
//			List<FactionWarRankItem> itemList = null;
//			FactionWarInfo info = null;
//			if(type == FactionWarResultType.Win){
//				itemList = this.broadcastWinList;
//				info = this.winFaction;
//			}else{
//				itemList = this.broadcastLoseList;
//				info = this.match.getOtherFaction(this.winFaction);
//			}
//			if(Util.isEmpty(itemList)){
//				return;
//			}
//			C1740_FactionWarOverNotifyMessage message = new C1740_FactionWarOverNotifyMessage();
//			message.setType(type.getType());
//			message.setList(itemList);
//			for(String roleId : info.getFactionKilledMap().keySet()){
//				GameContext.getMessageCenter().sendByRoleId(null, roleId, message);
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	@Override
//	public boolean mustRunMapLoop(){
//		return null != GameContext.getFactionWarApp().getActive();
//	}
//	
//	@Override
//	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
//		List<DeathNotifySelfItem> list = GameContext.getRoleRebornApp().getRebornOption(role);
//		for(Iterator<DeathNotifySelfItem> it = list.iterator();it.hasNext();){
//			DeathNotifySelfItem item = it.next();
//			if(item.getType() == RebornType.place.getId()){
//				//门派战地图不允许原地复活
//				it.remove();
//			}
//		}
//		return list ;
//	}
//	
//	@Override
//	public Point getRebornPoint(RoleInstance role,RebornType type){
//		if(RebornType.place == type /*|| RebornType.skill == type*/){
//			//擂台赛不允许原地复活和技能复活
//			return null ;
//		}
//		return super.getRebornPoint(role, type);
//	}
//	
//	@Override
//	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
//		if(Util.isEmpty(role.getFactionId()) || Util.isEmpty(target.getFactionId())) {
//			return ForceRelation.friend;
//		}
//		return role.getFactionId().equals(target.getFactionId()) ? ForceRelation.friend:ForceRelation.enemy;
//	}
//	
//	/**
//	 * 踢出不是此次门派战的人
//	 */
//	private void kickNotWarRole(){
//		try{
//			for (AbstractRole role : this.getRoleList()) {
//				try {
//					RoleInstance player = (RoleInstance)role;
//					if(!player.hasFaction() || !this.match.isFactionWarRole(player)) {
//						this.kickRole(player);
//					}
//				} catch (Exception ex) {
//				}
//			}
//		}catch(Exception e){
//		}
//	}
//}
