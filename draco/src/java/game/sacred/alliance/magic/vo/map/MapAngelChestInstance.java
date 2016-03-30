package sacred.alliance.magic.vo.map;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.active.angelchest.MapRefreshStatus;
import sacred.alliance.magic.app.chest.ChestRefreshInfo;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.MapLineInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.base.AppType;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.push.C2376_ActiveAngelChestOverNotifyMessage;
import com.game.draco.message.response.C2372_ActiveAngelChestNewRespMessage;
import com.game.draco.message.response.C2374_ActiveAngelChestResetRespMessage;

/**
 * 神仙福地宝箱
 * 
 *
 */
public class MapAngelChestInstance extends MapLineInstance{
	/**
	 * 地图销毁的时候不要消耗此对象
	 */
	private MapRefreshStatus status = null ;
	/**
	 * 当前地图的所有宝箱
	 */
	private LoopCount refreshLoop = new LoopCount(LoopConstant.ANGEL_CHEST_REFRESH_CIRCLE_CYCLE); 
	private LoopCount resetLoop = new LoopCount(LoopConstant.ANGEL_CHEST_RESET_CIRCLE_CYCLE ); 
	private MapBoxSupport mapBox = null ;
		
	public MapAngelChestInstance(Map map, int lineId) {
		super(map, lineId);
		this.mapBox = new MapBoxSupport(this,OutputConsumeType.angel_chest,
				OutputConsumeType.angel_chest_mail, 
				GameContext.getI18n().getText(TextId.ANGELCHEST_MAIL_CONTEXT),
				MailSendRoleType.AngelChest);
		
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
	
	@Override
	protected void enter(AbstractRole role){
		if(null == role || role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		super.enter(role);
		if(null != this.mapBox){
			mapBox.enter(role, this.getActiveStatus(), this.getNextOpenTime());
		}
		//活跃度
		RoleInstance player = (RoleInstance)role ;
		GameContext.getDailyPlayApp().incrCompleteTimes(player, 1, DailyPlayType.angel_chest, "");
		GameContext.getCountApp().joinApp(player, AppType.angel_chest);
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		if(null == this.mapBox){
			return ;
		}
		this.mapBox.damageTaken(attacker, victim, hurt);
	}
	
	@Override
	public void doEvent(RoleInstance role,MapInstanceEvent event){
		if(null == this.mapBox){
			return ;
		}
		this.mapBox.doEvent(role, event);
	}
	
	private boolean reset(){
		try {
			// 当前周期
			if(this.status.inSameLoop()){
				return false ;
			}
			//初始化相关参数
			this.status.initState();
			if (mapBox.chestSize() > 0) {
				// 清除现在所有宝箱,并且通知地图内用户清除本地宝箱
				mapBox.cleanData();
			}
			//通知客户端
			this.broadcastMap(null,
					new C2374_ActiveAngelChestResetRespMessage());
			return true ;
		}catch(Exception ex){
			logger.error("",ex);
		}
		return false ;
	}
	
	
	private boolean isOver(){
		return this.isRefreshOver() && mapBox.chestSize() == 0 ;
	}

	@Override
	public void updateSub(){
		try {
			super.updateSub();
			if(this.resetLoop.isReachCycle() 
					&& this.reset()){
				return ;
				//重置
			}
			if(this.status.isCurLoopOver()){
				//当前周期已经结束
				return ;
			}
			if(refreshLoop.isReachCycle()){
				//刷新宝箱
				this.refresh();
				if(this.isOver()){
					this.curLoopOverAction();
				}
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	
	private void curLoopOverAction(){
		this.status.setCurLoopOver(true);
		//广播已经结束
		C2376_ActiveAngelChestOverNotifyMessage overMsg = new C2376_ActiveAngelChestOverNotifyMessage();
		overMsg.setNextOpenTime(this.status.getNextOpenTimeStr());
		this.broadcastMap(null, overMsg);
	}

	
	private boolean isRefreshOver(){
		return mapBox.getRefreshSize() <=0 
		|| this.status.getRefreshIndex()>= mapBox.getRefreshSize()
		|| null == mapBox.getRefreshList() ;
	}
	
	private void refresh(){
		if(this.isRefreshOver()){
			//已经刷新完毕
			return ;
		}
		int preRefreshTime = -1 ;
		List<AngelChestInfoItem> thisChestList = null ;
		//从倒计时开始到现在的时间（单位：秒）
		int time = DateUtil.getSecondMargin(status.getStartTime());
		int refreshIndex = status.getRefreshIndex() ;
		boolean isFirst = (refreshIndex == 0 );
		boolean chestAllClean = (mapBox.chestSize() == 0) ;
		for(int i= refreshIndex;i< mapBox.getRefreshSize();i++){
			ChestRefreshInfo born = mapBox.getRefreshList().get(i);
			boolean timeOk = (time >= born.getRefreshTime()) ;
			if(isFirst){
				//首次
				if(!timeOk){
					//首次必须等到时间条件满足
					break ;
				}
			} else if (!timeOk) {
				//非首次,时间未到
				if (!chestAllClean) {
					//当前还有宝箱
					break;
				} else if(-1 != preRefreshTime
							&& preRefreshTime != born.getRefreshTime()) {
					// 当前已经没有相关宝箱
					// 只刷一批
					break ;
				}
			}
		
			try {
				List<AngelChestInfoItem> subList = mapBox.refresh(born);
				if(!Util.isEmpty(subList)){
					if(null == thisChestList){
						thisChestList = new ArrayList<AngelChestInfoItem>();
					}
					thisChestList.addAll(subList);
				}
			}catch(Exception ex){
				logger.error("",ex);
			}
			//刷新序列+1
			refreshIndex++ ;
			status.setRefreshIndex(refreshIndex);
			preRefreshTime = born.getRefreshTime() ;
		}
		if(Util.isEmpty(thisChestList)){
			return ;
		}
		//广播
		C2372_ActiveAngelChestNewRespMessage respMsg = new C2372_ActiveAngelChestNewRespMessage();
		respMsg.setNewList(thisChestList);
		this.broadcastMap(null, respMsg);
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
	
	@Override
	public void destroy(){
		super.destroy();
		if(null != this.mapBox){
			mapBox.destroy();
		}
	}
	
	/*@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		List<DeathNotifySelfItem> list = GameContext.getRoleRebornApp().getRebornOption(role);
		for(Iterator<DeathNotifySelfItem> it = list.iterator();it.hasNext();){
			DeathNotifySelfItem item = it.next();
			if(item.getType() == RebornNotifyItemType.place.getType()){
				//不允许原地复活
				it.remove();
			}
		}
		return list ;
	}*/
	
}
