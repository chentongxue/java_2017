package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.siege.SiegeMapConfig;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.internal.C0065_ActiveSiegeAwardInternalMessage;
import com.game.draco.message.push.C2326_ActiveSiegeNotifyMessage;

public @Data class MapSiegeInstance extends MapLineInstance{
	private static final ChannelSession EMPTY_SESSION = new EmptyChannelSession();
	private final static int AWARD_TIME_OUT_MIN = 2 ; //发送奖励超时时间
	
	private String success_info = "";
	private String fail_info = "";
	protected final static Logger logger = LoggerFactory.getLogger(MapInstance.class);
	private int refreshIndex;//刷新NPC下标
	private int broadcastIndex;//广播下标
	private Date endTime;//活动结束时间
	private Active active;//活动
	private Date startTime;//开始时间
	private Set<String> bossSet;//BOSS
	private int refreshRuleId;//刷怪ID
	private int refreshRuleMax;
	
	private final int mapStateRefreshMaxIndex = 2 ;
	private int mapStateRefreshIndex = 0 ;
	
	private LoopCount delayLoop ;
	private MapState mapState = MapState.init;//初始状态
	

	public MapSiegeInstance(Map map, int lineId) {
		super(map, lineId);
		String mapId = map.getMapId();
		delayLoop= new LoopCount(LoopConstant.SIEGE_CIRCLE_CYCLE);
		SiegeMapConfig config = GameContext.getActiveSiegeApp().getSiegeMapConfig(mapId);
		active = GameContext.getActiveSiegeApp().getActive(config.getActiveId());
		if(null != active){
			this.setStartTime(active.getActiveStartTime());
			this.setEndTime(active.getActiveEndTime());
		}
		
		refreshRuleId = config.getRuleId() ;
		refreshRuleMax = GameContext.getRefreshRuleApp().getRefreshMax(refreshRuleId);
		String mapName = map.getMapConfig().getMapdisplayname();
		
		bossSet = GameContext.getRefreshRuleApp().getBossId(refreshRuleId);
		
		refreshIndex = 0;
		broadcastIndex = 0;
		
		success_info = config.getSuccessContent();
		success_info = success_info.replace("${mapName}", mapName);
		success_info = success_info.replace("${line}", this.lineId+"");
	
		fail_info = config.getFailContent() ;
		fail_info = fail_info.replace("${mapName}", mapName);
		fail_info = fail_info.replace("${line}", this.lineId+"");
	}
	
	enum MapState{
		init,
		refresh,
		winAward,
		failAward,
		awardEnd,
		end,
		;
		
	}
	
	//重新设置刷新NPC的状态
	private void reStart(){
		if(null == active){
			return ;
		}
		if(!isTimeout()){
			return ;
		}
		if(active.isTimeOpen()){
			this.setEndTime(active.getActiveEndTime());
			this.setStartTime(active.getActiveStartTime());
			mapState = MapState.init;
			refreshIndex = 0;
			broadcastIndex = 0;
		}
	}
	
	//完成后(发奖励)
	private void bossDieComplete() {
		mapState = MapState.winAward;
		//战斗结束,活动时间没结束,怪物不清除
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		super.npcDeath(npc);
		if(this.isBoss(npc.getNpc().getNpcid())){
			this.bossDieComplete();
			this.endBroadcast(true);
			GameContext.getStatLogApp().bossDeathLog(npc);
		}
	}
	
	private void endBroadcast(boolean isSuccess){
		C2326_ActiveSiegeNotifyMessage message = new C2326_ActiveSiegeNotifyMessage();
		if(isSuccess){
			message.setType(Status.SUCCESS.getInnerCode());
			message.setInfo(success_info);
		}else{
			message.setType(Status.FAILURE.getInnerCode());
			message.setInfo(fail_info);
		}
		broadcastMap(null, message);
	}
	
	//清除NPC并且广播
	private void delNpcAndBroadcast(NpcInstance npc){
		if(npc.getNpcBornDataIndex() != -1){
			return ;
		}
		super.npcDeath(npc);
	}
	
	//系统结束
	private void systemComplete(){
		mapState = MapState.failAward;
		boolean timeount = awardTimeout();
		List<NpcInstance> allNpc = new ArrayList<NpcInstance>();
		allNpc.addAll(this.npcList);
		//没有杀死，并且没有boos,默认99%
		int currHp = 99 ;
		int maxHp = 100 ;
		try {
			for (NpcInstance npc : allNpc) {
				if (isBoss(npc.getNpcid()) && !timeount) {
					currHp = npc.get(AttributeType.curHP);
					maxHp = npc.get(AttributeType.maxHP);
				}
				this.delNpcAndBroadcast(npc);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		if(!timeount){
			this.packMessage(this.getRoleList(), currHp, maxHp, false);
		}
		mapState = MapState.awardEnd;
		this.endBroadcast(false);
	}
	
	private void packMessage(Collection<RoleInstance> roleList, int currHp, int maxHp,boolean success){
		C0065_ActiveSiegeAwardInternalMessage message = new C0065_ActiveSiegeAwardInternalMessage();
		message.setCurrHp(currHp);
		message.setMaxHp(maxHp);
		message.setRoleList(roleList);
		message.setSuccess(success);
		message.setActive(this.active);
		GameContext.getUserSocketChannelEventPublisher().publish(null, 
					message,EMPTY_SESSION);
	}
	
	//发奖时间超时
	private boolean awardTimeout(){
		return DateUtil.dateDiffMinute(new Date(), endTime)> AWARD_TIME_OUT_MIN ;
	}
	
	//超时处理
	private void timeout(){
		mapState = MapState.end;
		for(NpcInstance npc : this.npcList){
			this.delNpcAndBroadcast(npc);
		}
	}
	
	//超时
	private boolean isTimeout(){
		return new Date().after(endTime);
	}
	
	private boolean isBoss(String npcId) {
		if(Util.isEmpty(npcId) || null == this.bossSet){
			return false ;
		}
		return this.bossSet.contains(npcId);
	}
	
	@Override
	public void updateSub() {
		try {
			super.updateSub();
			if (!delayLoop.isReachCycle()) {
				return;
			}
			
			if(null == active){
				return ;
			}
			
			if (mapState == MapState.init) {
				this.initState();
				return;
			}

			if (mapState == MapState.refresh) {
				if (isTimeout()) {
					this.systemComplete();
					return;
				}
				this.mapStateRefreshIndex ++ ;
				if(this.mapStateRefreshIndex >= this.mapStateRefreshMaxIndex){
					this.mapStateRefreshIndex = 0 ;
					this.refreshNpc();
				}
				return ;
			}
			
			if(mapState == MapState.winAward){
				mapState = MapState.awardEnd;
				this.packMessage(this.getRoleList(), 0, 0, true);
				return ;
			}
			
			if(mapState == MapState.awardEnd){
				if (isTimeout()) {
					this.timeout();
				}
				return ;
			}

			if (mapState == MapState.end) {
				this.reStart();
				return;
			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	//初始状态
	private void initState() {
		if (null == endTime) {
			if (null == active.getActiveEndTime()) {
				return;
			}
			this.setEndTime(active.getActiveEndTime());
			this.setStartTime(active.getActiveStartTime());
			return;
		}
		mapState = MapState.refresh;
	}
	
	private void refreshNpc(){
		if(this.refreshIndex >= this.refreshRuleMax){
			return;
		}
		this.refreshIndex = GameContext.getRefreshRuleApp().refresh(this.refreshRuleId, 
				this.refreshIndex, startTime, this, false);
	}

}
