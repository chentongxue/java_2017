package sacred.alliance.magic.app.chest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.*;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.team.Team;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class ChestAppImpl implements Service,ChestApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String,ChestTypeInfo> allChestTypeInfo = null ;
	private Map<String,List<ChestRefreshInfo>> mapRefreshInfoMap = new HashMap<String,List<ChestRefreshInfo>>();
	private Map<String,List<ChestTypeInfo>> chestTypeInMap = new HashMap<String,List<ChestTypeInfo>>();
	private Map<String,ChestRefreshRange> rangeMap = null ;
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadRange() ;
		this.loadChestTypeInfo() ;
		this.loadChestRefreshInfo();
	}
	
	
	private void loadChestTypeInfo(){
		String fileName = XlsSheetNameType.chest_type.getXlsName();
		String sheetName = XlsSheetNameType.chest_type.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allChestTypeInfo = XlsPojoUtil.sheetToMap(sourceFile, sheetName, ChestTypeInfo.class);
			if(!Util.isEmpty(allChestTypeInfo)){
				for(ChestTypeInfo info : allChestTypeInfo.values()){
					info.init();
				}
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void loadRange(){
		String fileName = XlsSheetNameType.chest_range.getXlsName();
		String sheetName = XlsSheetNameType.chest_range.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			rangeMap = XlsPojoUtil.sheetToMap(sourceFile, sheetName, ChestRefreshRange.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void loadChestRefreshInfo(){
		String fileName = XlsSheetNameType.chest_refresh.getXlsName();
		String sheetName = XlsSheetNameType.chest_refresh.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<ChestRefreshInfo> refreshList = XlsPojoUtil.sheetToList(sourceFile, sheetName, ChestRefreshInfo.class);
			if(Util.isEmpty(refreshList)){
				return ;
			}
			for(ChestRefreshInfo info : refreshList){
				String mapId = info.getMapId() ;
				List<ChestRefreshInfo> listInMap = this.getMapChestRefreshList(mapId);
				if(null == listInMap){
					listInMap = new ArrayList<ChestRefreshInfo>();
					mapRefreshInfoMap.put(mapId,listInMap);
					//判断地图ID是否存在
					sacred.alliance.magic.app.map.Map mapInfo = GameContext.getMapApp().getMap(mapId) ;
					if (null == mapInfo) {
						Log4jManager.checkFail();
						Log4jManager.CHECK
								.error("ChestRefreshInfo config error,mapId="
										+ mapId + " not exist" + " sourceFile="
										+ fileName + "sheetName=" + sheetName);
					}
				}
				listInMap.add(info);
			}
			
			//对每地图里面的刷新信息进行排序
			for(List<ChestRefreshInfo> infos : mapRefreshInfoMap.values()){
				this.sort(infos);
				Set<Short> chestTypeSet = new HashSet<Short>();
				for(ChestRefreshInfo info : infos){
					//初始化检测
					info.init() ;
					short chestType = info.getChestType() ;
					if(chestTypeSet.contains(chestType)){
						continue ;
					}
					chestTypeSet.add(chestType);
					//判断chestType是否正确
					ChestTypeInfo chestTypeInfo = this.getChestTypeInfo(chestType);
					if(null == chestTypeInfo){
						Log4jManager.checkFail();
						Log4jManager.CHECK
								.error("ChestRefreshInfo config error,chestType="
										+ info.getChestType() + " not exist" + " mapId="+ info.getMapId());
						continue ;
					}
					String mapId = info.getMapId() ;
					List<ChestTypeInfo> typeInMap = this.getMapChestTypeInfo(mapId);
					if(null == typeInMap){
						typeInMap = new ArrayList<ChestTypeInfo>();
						chestTypeInMap.put(mapId, typeInMap);
					}
					typeInMap.add(chestTypeInfo);
				}
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void sort(List<ChestRefreshInfo> refreshList){
		Collections.sort(refreshList, new Comparator<ChestRefreshInfo>(){
			@Override
			public int compare(ChestRefreshInfo rule1, ChestRefreshInfo rule2) {
				if (rule1.getRefreshTime() < rule2.getRefreshTime()) {
					return -1;
				}
				if (rule1.getRefreshTime() > rule2.getRefreshTime()) {
					return 1;
				}
				return 0;
			}
		});
	}


	@Override
	public void stop() {
		
	}

	@Override
	public boolean canOpen(RoleInstance role, long refreshTime,long readyTime,
			AngelChestInfoItem chest) {
		try{
			ChestTypeInfo chestTypeInfo = this.getChestTypeInfo(chest.getType());
			if(null == chestTypeInfo){
				return false;
			}
			//判断距离
			int dis_offset = GameContext.getParasConfig().getChestDisOffset();
			int dis = Point.math_DistPointPoint(role.getMapX()-chest.getX(),role.getMapY()-chest.getY());
			if(dis > dis_offset) {
				//提示玩家距离太远
				C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
				notifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.ANGELCHEST_DIS_NOT_ENOUGH));
				role.getBehavior().sendMessage(notifyMsg);
				return false;
			}
			
			//玩家开宝箱时判断时间的偏移量
			int open_time_offset = GameContext.getParasConfig().getChestOpenTimeOffset();
			int ready_time_offset = GameContext.getParasConfig().getChestReadyOpenTimeOffset();
			long now = System.currentTimeMillis();
			long roleTime = role.getAngelChestTime();
			int boxOpenTime = chestTypeInfo.getProgressTime() * 1000;
			if(roleTime <= 0) {
				roleTime = now - boxOpenTime;
			}
			if(refreshTime <= 0) {
				refreshTime = now - boxOpenTime;
			}
			//当前时间-刷出的时间+偏移量 < 读条时间 
			// 当前时间-角色开宝箱时间+偏移量 < 读条时间 
			//准备时间 到开启时间小于一个周期
			//返回false
			if( (now - refreshTime + open_time_offset < boxOpenTime) ||
					(now - roleTime + open_time_offset < boxOpenTime) ||
					(now - readyTime < boxOpenTime - ready_time_offset) ){
				//提示玩家CD不满足
				C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
				notifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.ANGELCHEST_CD_NOT_ENOUGH));
				role.getBehavior().sendMessage(notifyMsg);
				return false;
			}
		}catch(Exception e){
			logger.error("AngelChestApp.canOpen error:",e);
		}
		return true;
	}

	@Override
	public ChestTypeInfo getChestTypeInfo(int chestType) {
		if(Util.isEmpty(allChestTypeInfo)){
			return null ;
		}
		return allChestTypeInfo.get(String.valueOf(chestType));
	}

	@Override
	public List<ChestRefreshInfo> getMapChestRefreshList(String mapId) {
		if(Util.isEmpty(mapId)){
			return null ;
		}
		return mapRefreshInfoMap.get(mapId);
	}

	@Override
	public List<ChestTypeInfo> getMapChestTypeInfo(String mapId) {
		if(Util.isEmpty(mapId)){
			return null ;
		}
		return this.chestTypeInMap.get(mapId);
	}
	
	@Override
	public ChestRefreshRange getChestRefreshRange(String rangeId){
		if(Util.isEmpty(rangeId)){
			return null ;
		}
		return this.rangeMap.get(rangeId);
	}

	@Override
	public boolean openChest(RoleInstance role, int chestType,
			OutputConsumeType consumeType,OutputConsumeType mailConsumeType,
			String mailContext,MailSendRoleType mailSendRoleType) {
		if(null == role){
			return false;
		}
		ChestTypeInfo chestTypeInfo = this.getChestTypeInfo(chestType);
		if(null == chestTypeInfo){
			return false;
		}
		//统计伤害
		/*if(chestTypeInfo.getHurt() > 0){
			MapInstance mapInstance = role.getMapInstance();
			if(null != mapInstance){
				mapInstance.countHurt(role, chestTypeInfo.getHurt());
				this.sendTipNotifyMessage(role, chestTypeInfo.getHurt());
			}
		}*/
        String eventKey = chestTypeInfo.getEventKey() ;
        MapInstance mapInstance = role.getMapInstance();
		if(null != mapInstance && !Util.isEmpty(eventKey)){
            mapInstance.doEvent(role,new MapInstanceEvent(MapInstanceEvent.EventType.chestOpenSuccess,eventKey));
        }
		//buff
		short buffId = chestTypeInfo.getBuffId() ;
		if(buffId > 0){
			//buff级别往上叠加
			Buff buff = GameContext.getBuffApp().getBuff(buffId);
			int lv = role.getBuffLevel(buffId);
			int toAddLevel = Math.min(lv + 1, buff.getMaxLevel());
			GameContext.getUserBuffApp().addBuffStat(role, role, buffId,toAddLevel);
			//提示获得buff
			String name = buff.getBuffName();
			if(!Util.isEmpty(name)){
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System,
						GameContext.getI18n().messageFormat(TextId.ARENA_TOP_RECV_BUFF_TIPS, name), null, role);
			}
		}
		List<GoodsOperateBean> lootList = chestTypeInfo.getGoodsList();
		if(Util.isEmpty(lootList)){
			return true;
		}
		Team team = role.getTeam();
		if(null == team || team.getOnlinePlayerNum()<=1){
			//一个人获得
			this.giveChestGoods(role, lootList,chestTypeInfo,consumeType,mailConsumeType,mailContext,mailSendRoleType);
			return true;
		}
		//获得本地图内同队伍的玩家

		List<AbstractRole> sameMapMembers = new ArrayList<AbstractRole>();
		for(AbstractRole member : team.getMembers()){
			MapInstance mi = member.getMapInstance();
			if(null == mi|| !mi.getInstanceId().equals(mapInstance.getInstanceId())){
				continue ;
			}
			sameMapMembers.add(member);
		}
		//roll点
		Map<String,List<GoodsOperateBean>> rollList = GameContext.getFallApp().roll(lootList, sameMapMembers);
		for(AbstractRole itemRole : sameMapMembers){
			List<GoodsOperateBean> roleRollList = rollList.get(itemRole.getRoleId()) ;
			this.giveChestGoods((RoleInstance)itemRole, roleRollList,chestTypeInfo,
					consumeType,mailConsumeType,mailContext,mailSendRoleType);
		}
		return true ;
	}
	
	/*private void sendTipNotifyMessage(RoleInstance role,int hurt){
		C0003_TipNotifyMessage tip = new C0003_TipNotifyMessage();
		tip.setMsgContext(GameContext.getI18n().getText(TextId.RED_OPEN_CHEST_ADD_HURT + hurt));
		role.getBehavior().sendMessage(tip);
	}*/
	
	private void giveChestGoods(RoleInstance role,List<GoodsOperateBean> goods,ChestTypeInfo chestTypeInfo,
			OutputConsumeType consumeType,OutputConsumeType mailConsumeType,
			String mailContext,MailSendRoleType mailSendRoleType){
		if(Util.isEmpty(goods)){
			return ;
		}
		//向背包中添加物品
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, goods,consumeType);
		//背包满了发邮件
		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
		if(Util.isEmpty(putFailureList)){
			this.broadcastChestGoods(role, goods, chestTypeInfo);
			return ;
		}
		GameContext.getMailApp().sendMail(role.getRoleId(), mailSendRoleType.getName(), mailContext, 
				mailSendRoleType.getName(), mailConsumeType.getType(), putFailureList);
		//广播
		this.broadcastChestGoods(role, goods, chestTypeInfo);
	}
	
	private void broadcastChestGoods(RoleInstance role,List<GoodsOperateBean> goods,ChestTypeInfo chestTypeInfo){
		try {
			if (!chestTypeInfo.isBroadcast()) {
				return;
			}
			StringBuffer buffer = new StringBuffer("");
			String CAT = "";
			for (GoodsOperateBean bean : goods) {
				buffer.append(CAT);
				buffer.append(Wildcard.getChatGoodsContent(bean.getGoodsId(),
						ChannelType.World,bean.getGoodsNum()));
				CAT = " ";
			}
			String message = GameContext.getI18n().messageFormat(
					TextId.ANGELCHEST_OPEN_BROADCAST_GOODS, chestTypeInfo
							.getChestName(), role.getRoleName(), buffer
							.toString());
			GameContext.getChatApp().sendSysMessage(ChatSysName.System,
					ChannelType.World, message, null, null);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}

}
