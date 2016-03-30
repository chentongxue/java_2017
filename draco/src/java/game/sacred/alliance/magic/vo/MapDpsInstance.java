package sacred.alliance.magic.vo;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import sacred.alliance.magic.app.active.dps.DpsHurtPoint;
import sacred.alliance.magic.app.active.dps.DpsMapConfig;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.base.AppType;
import com.game.draco.message.push.C2363_ActiveDpsStopTimeNotifyMessage;

public class MapDpsInstance extends MapLineInstance{
	
	private final int Broadcast_Time = 5 * 1000;//每5秒进行一次排名、广播
	private final int Kick_Role_Time = 10 * 1000;//踢人状态执行时间（进入踢人状态10秒之后踢人）
	private final LoopCount broadcastLoop = new LoopCount(Broadcast_Time);
	private final int End_Repeat_Time = 30 * 1000;//每30秒进行
	private final LoopCount repeatLoop = new LoopCount(End_Repeat_Time);//地图重复活动判断
	private Active active;//活动
	private short activeId = 0;//活动ID
	//private Date startTime;//活动开始时间
	private Date endTime;//活动结束时间
	//private boolean isGameOver = false;//活动结束已经发完奖励
	/** 伤害突破点配置 */
	private List<DpsHurtPoint> hurtPointList;
	private short buffId = 0;//发奖时加buff，避免多分线重复发奖
	/** 需要统计DPS的BOSS集合 */
	private Set<String> npcSet = new HashSet<String>();
	/** DPS输出模型 */
	private MapDpsModel dpsModel = new MapDpsModel();
	private long maxDpsHurt = 0;//最大的输出伤害值
	private MapState mapState = MapState.init;
	private boolean bossDeath = false;//BOSS是否死亡
	/**
	 * 不能在npc死亡的时候设置
	 */
	private int npcLevel;//boss死亡等级
	private long kickStartTime = 0;//踢人状态开始时间
	private DpsMapConfig mapCfg ;

	
	
	private void activeOverBroadcast(){
		try {
			// 活动结束广播
			if (null == mapCfg) {
				return;
			}
			String txt = mapCfg.getBroadcastText();
			if (Util.isEmpty(txt)) {
				return;
			}
			ChannelType channelType = ChannelType.getChannelType(mapCfg.getBroadcastChannel());
			if (null == channelType) {
				return;
			}
			txt = txt.replace(Wildcard.MapName, map.getMapConfig()
					.getMapdisplayname().replace(Wildcard.Map_LineId,
							this.lineId + ""));
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, channelType, txt, null, this);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	public MapDpsInstance(Map map, int lineId) {
		super(map, lineId);
		String mapId = map.getMapId();
		this.active = GameContext.getActiveDpsApp().getActiveByMapId(mapId);
		this.hurtPointList = GameContext.getActiveDpsApp().getHurtPointList();
		if(null != this.active){
			this.activeId = this.active.getId();
			//如果活动开启，将地图切到伤害统计状态
			if(this.active.isTimeOpen()){
				this.mapState = MapState.hurt_count;
				//this.startTime = this.active.getActiveStartTime();
				this.endTime = this.active.getActiveEndTime();
			}
		}
		mapCfg = GameContext.getActiveDpsApp().getDpsMapConfig(mapId);
		if(null != mapCfg){
			this.dpsModel.setShowSize(mapCfg.getShowSize());
			this.npcSet = mapCfg.getNpcIdSet();
			this.buffId = mapCfg.getBuffId();
		}
	}
	
	enum MapState{
		init,//初始
		hurt_count,//伤害统计
		reward,//发奖
		kick_role,//踢人状态
		end,//结束
		;
	}
	
	@Override
	protected void updateSub() throws ServiceException {
		super.updateSub();
		//5秒中循环一次，进行伤害排名、广播
		if(this.broadcastLoop.isReachCycle()){
			switch(this.mapState){
			case init:
				//初始化状态的逻辑
				this.do_mapState_init();
				break;
			case hurt_count:
				//伤害统计状态的逻辑
				this.do_mapState_hurt_count();
				break;
			case reward:
				//发奖状态逻辑
				try {
					this.do_mapState_reward();
				} catch (Exception e) {
					logger.error("", e);
				}
				//!!!!!!!!!!
				//发完奖，必须将BOSS死亡标记还原，否则下次直接活动结束了。
				this.bossDeath = false;
				this.npcLevel = 0 ;
				break;
			case kick_role:
				//将所有玩家穿到活动入口
				this.do_mapState_kick_role();
				break;
			case end:
				//活动结束不做处理
				break;
			}
		}
		//结束状态，重复活动逻辑
		if(this.repeatLoop.isReachCycle()){
			this.repeatActive();
		}
	}
	
	/**
	 * 初始化状态逻辑
	 */
	private void do_mapState_init(){
		if(null == this.active){
			return;
		}
		//如果活动开启，将地图切到伤害统计状态
		if(this.active.isTimeOpen()){
			this.change_to_hurt_count();
		}
	}
	
	/**
	 * 切到伤害统计状态
	 */
	private void change_to_hurt_count(){
		this.mapState = MapState.hurt_count;
		//this.startTime = this.active.getActiveStartTime();
		this.endTime = this.active.getActiveEndTime();
		//地图内广播，活动结束倒计时
		this.broadcastStopTime(null);
	}
	
	/**
	 * 重复活动
	 */
	private void repeatActive(){
		try {
			//只在结束状态时，才判断活动是否重新开启
			if(MapState.end != this.mapState){
				return;
			}
			Date now = new Date();
			if(now.before(this.endTime)){
				return;
			}
			//未达到活动开启时间
			if(!this.active.isTimeOpen()){
				return;
			}
			//切到伤害统计状态
			this.change_to_hurt_count();
		} catch (RuntimeException e) {
			logger.error("MapDpsInstance.repeatActive error: ", e);
		}
	}
	
	/**
	 * 通知活动倒计时
	 * @param role 角色对象,为NULL表示地图内广播
	 */
	private void broadcastStopTime(AbstractRole role){
		try {
			if(null == this.endTime){
				return;
			}
			//剩余时间（秒）
			int time = DateUtil.dateDiffSecond(new Date(), this.endTime);
			C2363_ActiveDpsStopTimeNotifyMessage message = new C2363_ActiveDpsStopTimeNotifyMessage();
			message.setTime(time);
			if(null != role){
				role.getBehavior().sendMessage(message);
			}else{
				//地图内广播消息
				this.broadcastMap(null, message);
			}
		} catch (RuntimeException e) {
			logger.error("MapDpsInstance.notifyDpsStopTime error: ", e);
		}
	}
	
	/**
	 * 伤害统计装的逻辑
	 */
	private void do_mapState_hurt_count(){
		//输出伤害排序
		this.sortHurtMap();
		//如果BOSS死亡了，将地图切换到发奖状态
		if(this.bossDeath){
			this.mapState = MapState.reward;
			return;
		}
		//如果超过了活动结束时间，将地图切换到发奖状态
		if(null != this.endTime){
			Date now = new Date();
			if(now.after(this.endTime)){
				this.mapState = MapState.reward;
			}
		}
	}
	
	/**
	 * 发奖状态逻辑
	 * ①将地图伤害比例系数的值还原
	 * ②如果Boss没死，将Boss杀死
	 * ③发奖励
	 * ④将地图切到踢人状态
	 */
	private void do_mapState_reward(){
		//将伤害比例系数还原
		this.hurtRatio = 1;
		//杀死BOSS
		this.killBoss();
		//发奖
		this.sendReward();
		//广播结束
		this.activeOverBroadcast();
		//切到踢人状态
		this.mapState = MapState.kick_role;
		//踢人状态开始时间
		this.kickStartTime = System.currentTimeMillis();
	}
	
	/**
	 * 击杀活动BOSS
	 */
	private void killBoss(){
		try {
			for(NpcInstance npc : this.getNpcList()){
				if(null == npc){
					continue;
				}
				//不是活动BOSS，不需要杀死
				if(!this.npcSet.contains(npc.getNpcid())){
					continue;
				}
				this.mapNpcDeath(npc);
			}
		} catch (Exception e) {
			logger.error("MapDpsInstance.killBoss error: ", e);
		}
	}
	
	/**
	 * 踢人状态逻辑
	 * 将地图切到结束状态
	 */
	private void do_mapState_kick_role(){
		if(this.kickStartTime <= 0){
			return;
		}
		//未达到指定时间（进入踢人状态一定时间后才踢人）
		if(System.currentTimeMillis() - this.kickStartTime < this.Kick_Role_Time){
			return;
		}
		//将所有玩家传出当前地图
		this.kickAllRole();
		//切到结束状态
		this.mapState = MapState.end;
	}
	
	/**
	 * 将所有玩家传出当前地图
	 */
	private void kickAllRole(){
		if(null == this.active){
			return;
		}
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			this.kickRole(role);
		}
	}
	
	/**
	 * 将玩家提出活动地图
	 * 目标点是活动的进入点
	 * @param role
	 */
	@Override
	protected void kickRole(RoleInstance role){
		try{
			Point point = role.getCopyBeforePoint();
			if(null == point){
				return;
			}
			GameContext.getUserMapApp().changeMap(role, point);
		}catch(Exception e){
			logger.error("MapDpsInstance.kickRole error: ", e);
		}
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		try {
			if(null == attacker || null == victim || 0 == hurt){
				return;
			}
			//非伤害统计状态，不做伤害累计
			if(MapState.hurt_count != this.mapState){
				return;
			}
			//只有玩家攻击怪的时候才累计伤害值
			if(RoleType.PLAYER != attacker.getRoleType() || RoleType.NPC != victim.getRoleType()){
				return;
			}
			NpcInstance npc = (NpcInstance) victim;
			//不是活动中的BOSS
			if(!this.npcSet.contains(npc.getNpcid())){
				return;
			}
			//统计伤害值
			this.dpsModel.countDpsValue(attacker, hurt);
			if(npc.getNpc().getLevel() > this.npcLevel){
				this.npcLevel = npc.getNpc().getLevel() ;
			}
		} catch (RuntimeException e) {
			logger.error("", e);
		}
	}
	
	@Override
	public boolean mustRunMapLoop(){
		//结束状态且没人，可以不执行主循环
		if(MapState.end == this.mapState && 0 == this.getRoleCount()){
			return false;
		}
		//依然要执行主循环,理由主循环清除boss
		return true ;
	}
	
	/**
	 * 输出伤害排序
	 */
	private void sortHurtMap(){
		try {
			//给地图内所有人发送伤害排名信息
			this.dpsModel.notifyRankMessage(this);
			//输出血量排序
			List<Entry<String, AtomicLong>> hurtList = this.dpsModel.getDpsRankList();
			if(null == hurtList || 0 == hurtList.size()){
				return;
			}
			//判断最高伤害是否突破伤害点
			Entry<String, AtomicLong> maxEntry = hurtList.get(0);
			if(null != maxEntry){
				this.broadcastHurtPoint(maxEntry.getKey(), maxEntry.getValue().get());
			}
		} catch (RuntimeException e) {
			logger.error("MapDpsInstance.sortHurtMap error: ", e);
		}
	}
	
	/**
	 * 广播伤害突破点
	 * @param hurtValue 已经输出的伤害值
	 * @return
	 */
	private void broadcastHurtPoint(String roleId, long hurtValue){
		try {
			//未超过最大伤害
			if(hurtValue <= this.maxDpsHurt){
				return;
			}
			for(DpsHurtPoint dpsPoint : this.hurtPointList){
				if(null == dpsPoint){
					continue;
				}
				int hurtPoint = dpsPoint.getHurtPoint();
				if(hurtPoint < this.maxDpsHurt){
					continue;
				}
				if(hurtValue >= hurtPoint){
					//记录最高伤害值
					this.maxDpsHurt = hurtValue;
					//修改伤害比例系数
					this.hurtRatio = dpsPoint.getRatio();
					//地图内广播伤害有新突破
					String message = dpsPoint.getBroadcast();
					RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
					if(!Util.isEmpty(message) && null != role){
						message = message.replace(Wildcard.Role_Name, role.getRoleName());
						GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, message, null, this);
					}
					return;
				}
			}
		} catch (RuntimeException e) {
			logger.error("MapDpsInstance.broadcastHurtPoint error: ", e);
		}
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		super.npcDeath(npc);
		//是活动中的BOSS
		if(this.npcSet.contains(npc.getNpcid())){
			this.bossDeath = true;
		}
	}
	
	/**
	 * 活动结束发奖
	 */
	private void sendReward(){
		try {
			//输出血量排序
			List<Entry<String, AtomicLong>> hurtList = this.dpsModel.getDpsRankList();
			if(null == hurtList || 0 == hurtList.size()){
				return;
			}
			int size = hurtList.size();
			for(int i=0; i<size; i++){
				try {
					Entry<String, AtomicLong> entry = hurtList.get(i);
					if(null == entry){
						continue;
					}
					RoleInstance role = this.roleMap.get(entry.getKey());
					if(null == role){
						continue;
					}
					//活动结束发奖
					long hurt = entry.getValue().get();
					//已经有发奖buff，表示已经领过奖了。
					if(this.buffId > 0 && role.hasBuff(this.buffId)){
						continue;
					}
					GameContext.getActiveDpsApp().sendReward(this.activeId, role, i+1, hurt,npcLevel);
					if(this.buffId > 0){
						//发完奖加buff
						GameContext.getUserBuffApp().addBuffStat(role, role, this.buffId, 1);
					}
				} catch (RuntimeException e) {
					logger.error("MapDpsInstance.gameOver sendReward error: ", e);
				}
			}
			//清空伤害缓存
			this.dpsModel.clearDpsValue();
		} catch (Exception e) {
			logger.error("MapDpsInstance.gameOver error: ", e);
		}
	}
	
	@Override
	protected void enter(AbstractRole role) {
		super.enter(role);
		if(RoleType.PLAYER == role.getRoleType() && 
				!this.dpsModel.isExistRoleName(role.getRoleId())){
			RoleInstance player = (RoleInstance)role ;
			//活跃度
			GameContext.getDailyPlayApp().incrCompleteTimes(player, 1, DailyPlayType.boss_dps, "");
			//通知参加了此活动
			GameContext.getCountApp().joinApp(player, AppType.boss_dps);
		}
		//进入地图显示倒计时
		this.broadcastStopTime(role);
		this.dpsModel.putRoleName(role.getRoleId(), role.getRoleName());
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.dpsModel.clearDpsValue();
	}
}
