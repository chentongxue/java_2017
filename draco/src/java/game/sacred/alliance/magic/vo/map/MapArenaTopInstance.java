package sacred.alliance.magic.vo.map;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.chest.ChestRefreshInfo;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapDpsModel;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.MapLineInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.push.C2363_ActiveDpsStopTimeNotifyMessage;
import com.game.draco.message.response.C2372_ActiveAngelChestNewRespMessage;
import com.game.draco.message.response.C2374_ActiveAngelChestResetRespMessage;

public class MapArenaTopInstance extends MapLineInstance{
	//10s判断一次活动是否结束
	private LoopCount activeLoopCount = new LoopCount(5 * 1000);
	//广播周期
	private LoopCount broadcastLoopCount = new LoopCount(5*1000); 
	
	private Map<String,Integer> roleLevelMap = new ConcurrentHashMap<String, Integer>();
	//活动当前是否结束
	private volatile boolean activeOpenNow = false ;
	//击杀数排行
	private MapDpsModel dpsModel = new MapDpsModel();
	
	private int refreshIndex = 0;
	private Date refreshDate ;//开始时间(宝箱)
	private MapBoxSupport mapBox ;
	
	public MapArenaTopInstance(sacred.alliance.magic.app.map.Map map, int lineId) {
		super(map, lineId);
		mapBox = new MapBoxSupport(this,OutputConsumeType.arena_Top_Reward,
				OutputConsumeType.arena_Top_Reward_Mail,
				GameContext.getI18n().getText(TextId.ARENA_TOP_MAIL_TITILE),
				MailSendRoleType.ArenaTop) ;
	}
	
	@Override
	public boolean mustRunMapLoop(){
		//结束状态且没人，可以不执行主循环
		if(!activeOpenNow && 0 == this.getRoleCount()){
			return false;
		}
		//依然要执行主循环,发奖
		return true ;
	}
	
	/**
	 * 通知活动倒计时
	 * @param role 角色对象,为NULL表示地图内广播
	 */
	private void broadcastStopTime(AbstractRole role){
		try {
			Active active = GameContext.getArenaTopApp().getActive() ;
			if(null == active || !active.isTimeOpen()){
				return;
			}
			//剩余时间（秒）
			int time = DateUtil.dateDiffSecond(new Date(), active.getActiveEndTime());
			C2363_ActiveDpsStopTimeNotifyMessage message = new C2363_ActiveDpsStopTimeNotifyMessage();
			message.setTime(time);
			if(null != role){
				role.getBehavior().sendMessage(message);
			}else{
				//地图内广播消息
				this.broadcastMap(null, message);
			}
		} catch (RuntimeException e) {
			logger.error("MapArenaTopInstance.notifyDpsStopTime error: ", e);
		}
	}
	
	protected void updateSub() throws ServiceException {
		//此地图只需要更新用户行为
		super.updatePlayer() ;
		//判断大师赛是否结束
		if(this.activeLoopCount.isReachCycle()){
			boolean openNow = GameContext.getArenaTopApp().getActive().isTimeOpen();
			if(!activeOpenNow && openNow){
				this.activeOpen() ;
			}
			if(activeOpenNow && !openNow){
				this.activeClose() ;
			}
			if(activeOpenNow && openNow){
				this.activeRuning() ;
			}
			this.activeOpenNow = openNow ;
		}
		if(this.activeOpenNow && this.broadcastLoopCount.isReachCycle()){
			//对击杀数排序,广播
			//this.broadcastKilled();
			this.dpsModel.notifyRankMessage(this);
		}
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
	
	private void activeOpen(){
		this.refreshIndex = 0 ;
		this.refreshDate = new Date() ;
	}
	
	private void activeRuning(){
		if(null == this.refreshDate){
			return ;
		}
		this.refresh() ;
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
	
	/**
	 * 活动结束,广播发奖
	 */
	private void activeClose(){
		try {
			this.refreshDate = null ;
			this.refreshIndex = 0 ;
			//!!! 清除参赛者
			GameContext.getArenaTopApp().cleanRacers();
			//清除宝箱数据
			this.cleanBox();
			List<Entry<String, AtomicLong>> sortKilled = this.dpsModel.getDpsRankList();
			if(null == sortKilled || 0 == sortKilled.size()){
				return;
			}
			boolean toBroadcast = false ;
			int topRank = GameContext.getArenaTopApp().getTopMapConfig().getTopRank() ;
			StringBuffer buffer = new StringBuffer("");
			for (int i = 0; i < sortKilled.size(); i++) {
				try {
					Entry<String, AtomicLong> entry = sortKilled.get(i);
					int num = (int) entry.getValue().get();
					String roleId = entry.getKey() ;
					String roleName = this.dpsModel.getRoleName(roleId);
					int rank = i+ 1; 
					//邮件发奖
					GameContext.getArenaTopApp().sendReward(roleId,roleName,this.roleLevelMap.get(roleId),rank,num);
					//广播
					if(rank <= topRank && num > 0){
						toBroadcast = true ;
						buffer.append(MessageFormat.format(
								GameContext.getI18n().getText(TextId.ARENA_TOP_OVER_ROLE_INFO),rank,roleName, num));
					}
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
			if(toBroadcast){
				// 广播内容
				String txt = GameContext.getI18n().messageFormat(TextId.ARENA_TOP_OVER_BROADCAST, buffer.toString());
				// 世界广播
				GameContext.getChatApp().sendSysMessage(ChatSysName.Arena_Top,
						ChannelType.Publicize_System, txt, null, null);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		//清除击杀数
		this.dpsModel.clearDpsValue();
		this.roleLevelMap.clear();
		//踢人
		this.kickAllRole();
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
	
	
	/**
	 * 增加击杀数
	 * @param roleId
	 */
	private void addKilledNum(AbstractRole role){
		if(!this.activeOpenNow){
			//未开启不计算
			return ;
		}
		String roleId = role.getRoleId();
		if(Util.isEmpty(roleId)){
			return ;
		}
		//累计击杀数量
		this.dpsModel.countDpsValue(role, 1);
		this.roleLevelMap.put(roleId, role.getLevel());
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if (null == attacker || null == victim
				|| attacker.getRoleId().equals(victim.getRoleId())) {
			return ;
		}
		if (RoleType.PLAYER != attacker.getRoleType()
				|| RoleType.PLAYER != victim.getRoleType()) {
			return;
		}
		this.addKilledNum(attacker);
	}
	

	protected void enter(AbstractRole role){
		super.enter(role);
		List<Short> buffList = GameContext.getArenaTopApp().getEnterMapBuffList() ;
		if(null == buffList){
			return ;
		}
		for(Short buffId : buffList){
			GameContext.getUserBuffApp().addBuffStat(role, role, buffId, 1);
		}
		if(null != this.mapBox){
			this.mapBox.enter(role, (byte)1, "");
		}
		//没有击杀也得有奖励（累计值的时候会把角色名存入缓存，因此不用再调用putRoleName方法）
		this.dpsModel.countDpsValue(role, 0);
		this.roleLevelMap.put(role.getRoleId(), role.getLevel());
		this.broadcastStopTime(role);
	}
	
	public Point getRebornPoint(RoleInstance role,RebornType type){
		if(RebornType.situ == type){
			return role.getCurrentPoint();
		}
		//复活点随机
		return GameContext.getArenaTopApp().safePoint();
	}
	
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		Point point = role.getCopyBeforePoint();
		if (null != point) {
			role.setMapId(point.getMapid());
			role.setMapX(point.getX());
			role.setMapY(point.getY());
		}
		try {
			// 删除相关buff
			List<Short> buffList = GameContext.getArenaTopApp()
					.getEnterMapBuffList();
			if (null != buffList) {
				for (Short buffId : buffList) {
					GameContext.getUserBuffApp().delBuffStat(role, buffId,
							false);
				}
			}
		}catch(Exception ex){
		}
	}
	
	
	/**
	 * 玩家看玩家,永远敌对
	 * @param role
	 * @param target
	 * @return
	 */
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
		if(null == role || null == target){
			return ForceRelation.enemy ;
		}
		if(role.getRoleId().equals(target.getRoleId())){
			return ForceRelation.friend ;
		}
		return ForceRelation.enemy ;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.dpsModel.clearDpsValue();
		if(null != this.mapBox){
			this.mapBox.destroy();
		}
	}
	
	@Override
	public void doEvent(RoleInstance role,MapInstanceEvent event){
		if(null == this.mapBox){
			return ;
		}
		this.mapBox.doEvent(role, event);
	}
}
