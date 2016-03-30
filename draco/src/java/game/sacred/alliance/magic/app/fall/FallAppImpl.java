package sacred.alliance.magic.app.fall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.map.point.CollectablePoint;
import sacred.alliance.magic.app.summon.Summon;
import sacred.alliance.magic.base.BroadcastType;
import sacred.alliance.magic.base.FallRespType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcLootType;
import com.game.draco.app.team.Team;
import com.game.draco.message.item.FallItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0602_FallListRespMessage;
import com.game.draco.message.response.C0603_FallPickupRespMessage;
import com.game.draco.message.response.C1802_ChatRouteRespMessage;
import com.google.common.collect.Maps;

public class FallAppImpl implements FallApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final AtomicLong boxIdGen = new AtomicLong(0);
	protected Map<String, LootList> worldLootListMap = new HashMap<String,LootList>();
	protected Map<String, LootList> npcLootListMap = new HashMap<String,LootList>();
	
	
	@Override
	public LootList getNpcLootList(String npcId) {
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(npcId);
		if(null == npcTemplate){
			return null ;
		}
		return npcLootListMap.get(npcTemplate.getLootNpc()+"");
	}
	
	/** 生成新宝箱ID */
	public String getBoxId(){
		// 生成宝箱ID
		return PointType.Box.getType() + Cat.underline
				+ boxIdGen.getAndIncrement();
	}
	
	@Override
	public boolean fallBox(NpcInstance dieNpc, RoleInstance role,OutputConsumeType ocType) {
		//1.获得npc掉落
		//2.获得世界掉落
		//3.获得任务掉落
		//4.生成宝箱
		if (null == dieNpc || null == role || null == role.getMapInstance()) {
			return false;
		}
		NpcTemplate npcTemplate = dieNpc.getNpc();
		if(null == npcTemplate){
			return false ;
		}
		String templateId = npcTemplate.getNpcid();
		Team team = role.getTeam();
		
		if(null == team || team.getOnlinePlayerNum()<=1){
			//获得掉落物品
			List<GoodsOperateBean> npcLootList = this.getLootGoodsBeanMap(templateId);
			//任务掉落
			List<GoodsOperateBean> questGoodsList = this.getQuestLootList(templateId, role);
			if(!Util.isEmpty(questGoodsList)){
				if(null == npcLootList){
					npcLootList = new ArrayList<GoodsOperateBean>();
				}
				npcLootList.addAll(questGoodsList);
			}
			return this.npcDieFallBox(role, npcLootList,dieNpc,ocType);
		}
		
		//获得本地图内同队伍的玩家
		MapInstance mapInstance = role.getMapInstance();
		List<AbstractRole> sameMapMembers = new ArrayList<AbstractRole>();
		for(AbstractRole member : team.getMembers()){
			MapInstance mi = member.getMapInstance();
			if(null == mi|| !mi.getInstanceId().equals(mapInstance.getInstanceId())){
				continue ;
			}
			sameMapMembers.add(member);
		}
		
		LootList lootList = npcLootListMap.get(npcTemplate.getLootNpc()+"");
		LootList worldLootList = worldLootListMap.get(npcTemplate.getLootWorld()+"");
		
		List<GoodsOperateBean> needRollList = null;
		List<GoodsOperateBean> allMapGoodsList = null;
		if(null != lootList && lootList.getLootType() == NpcLootType.NORMAL.getType()) {
			needRollList = this.mergerGoodsBeanMap(this.getGoodsBean(lootList),this.getGoodsBean(worldLootList));
		}else{
			needRollList = this.getGoodsBean(worldLootList);
			allMapGoodsList = this.getGoodsBean(lootList);
		}
		//roll点分配
		Map<String,List<GoodsOperateBean>> rollList = this.roll(needRollList, sameMapMembers) ;
		for(AbstractRole itemRole : sameMapMembers){
			List<GoodsOperateBean> roleRollList = rollList.get(itemRole.getRoleId()) ;
			List<GoodsOperateBean> questGoodsList = this.getQuestLootList(templateId,(RoleInstance)itemRole);
			
			List<GoodsOperateBean> roleList = new ArrayList<GoodsOperateBean>() ;
			if(!Util.isEmpty(roleRollList)) {
				roleList.addAll(roleRollList);
			}
			if(!Util.isEmpty(questGoodsList)) {
				roleList.addAll(questGoodsList);
			}
			if(!Util.isEmpty(allMapGoodsList)) {
				roleList.addAll(allMapGoodsList);
			}
			if(Util.isEmpty(roleList)) {
				continue;
			}
			this.npcDieFallBox((RoleInstance)itemRole,roleList,dieNpc,ocType);
		}
		return true ;
	}
	
	@Override
	public boolean summonFallBox(NpcInstance dieNpc,Summon summon,RoleInstance ownerInstance,RoleInstance summonInstance) {
		if (null == dieNpc || null == ownerInstance || null == ownerInstance.getMapInstance()) {
			return false;
		}
		String templateId = dieNpc.getNpc().getNpcid();
		OutputConsumeType ocType = OutputConsumeType.summon_monster_fall;
		List<GoodsOperateBean> npcLootList = this.getLootGoodsBeanMap(templateId);
		Collection<RoleInstance> sameMapMembers = ownerInstance.getMapInstance().getRoleList();
		
		if(null != summonInstance && !Util.isEmpty(npcLootList)){
			this.npcDieFallBox(summonInstance, npcLootList, dieNpc, ocType);
			//通知玩家个人获得的奖励
			sendSummonFall(sameMapMembers, npcLootList, summonInstance.getRoleName());
		}
		
		//全员奖励
		List<GoodsOperateBean> allLootList = summon.getAllGainGoods();
		if(Util.isEmpty(allLootList)){
			return true;
		}
		//获得本地图内的玩家
		for(AbstractRole itemRole : ownerInstance.getMapInstance().getRoleList()){
			//给地图内玩家发放奖励
			this.npcDieFallBox((RoleInstance)itemRole, allLootList, dieNpc, ocType);
		}
		return true ;
	}
	
	
	/**
	 * roll点分配
	 * @param itemList
	 * @param sameMapMembers
	 * @return
	 */
	public Map<String,List<GoodsOperateBean>> roll(List<GoodsOperateBean> itemList,
			List<AbstractRole> sameMapMembers){
		Map<String,List<GoodsOperateBean>> result = Maps.newHashMap();
		if(Util.isEmpty(itemList)){
			return result ;
		}
		int size = sameMapMembers.size();
		if(1 == size){
			result.put(sameMapMembers.get(0).getRoleId(), itemList);
			return result ;
		}
		for(GoodsOperateBean agb : itemList){
			try {
				int goodsId = agb.getGoodsId();
				int goodsNum = agb.getGoodsNum();
				GoodsBase goodsBase  = GameContext.getGoodsApp().getGoodsBase(
							goodsId);
				if (null == goodsBase) {
					continue;
				}
				int[] points = new int[size];
				int maxIndex = 0;
				int maxPoint = -1;
				int prePoint = -1 ; //前一次随机分数
				for (int index = 0; index < size; index++) {
					int point = 0;
					if (goodsBase.getCareer() < 0
							|| goodsBase.getCareer() == ((RoleInstance) sameMapMembers
									.get(index)).getCareer()) {
						// 职业匹配额外+50分,确保物品被匹配的职业roll到
						point = 50;
					}
					int thisPoint = RandomUtil.randomIntWithoutZero(50) + point;
					if(prePoint == thisPoint){
						//随机的点数与上次相同,再随机一次
						thisPoint = RandomUtil.randomIntWithoutZero(50) + point;
					}
					if (maxPoint < thisPoint) {
						maxPoint = thisPoint;
						maxIndex = index;
					} else if (maxPoint == thisPoint
							&& RandomUtil.randomBoolean()) {
						// 点数相同,随机处理一下是否替换,否则一直是第一个分数相同的人
						maxIndex = index;
					}
					points[index] = thisPoint ;
					prePoint = thisPoint;
				}
				
				AbstractRole winner = sameMapMembers.get(maxIndex);
				String maxRoleId = winner.getRoleId();
				if (!result.containsKey(maxRoleId)) {
					result.put(maxRoleId, new ArrayList<GoodsOperateBean>());
				}
				result.get(maxRoleId).add(agb);
				// 蓝色(含)品质的物品需要发送roll结构
				boolean sendRollResult = goodsBase.getQualityType() >= QualityType.blue
						.getType();
				if (sendRollResult) {
					sendRollInfo(sameMapMembers, points, goodsBase, goodsNum,
							winner, maxIndex);
				}
			}catch(Exception ex){
				logger.error("",ex);
			}
		}
		return result ;
	}
	
	private void sendRollInfo(List<AbstractRole> sameMapMembers,
			int[] points,GoodsBase goodsBase, int num,AbstractRole winner,int maxIndex){
		//构建roll点信息
		StringBuffer buffer = new StringBuffer();
		buffer.append(((RoleInstance)winner).getRoleName());
		buffer.append(GameContext.getI18n().messageFormat(TextId.FALL_ROLL_POINT_GAIN, points[maxIndex]));
		buffer.append(Wildcard.getChatGoodsContent(goodsBase, ChannelType.System,num));
		//buffer.append("[").append(goodsBase.getName()).append("]");
		int index = -1 ;
		for(AbstractRole role:sameMapMembers){
			index ++ ;
			if(role.getRoleId().equals(winner.getRoleId())){
				continue ;
			}
			buffer.append(",");
			buffer.append(((RoleInstance)role).getRoleName());
			buffer.append(" ");
			buffer.append(points[index]);
			buffer.append(GameContext.getI18n().getText(TextId.FALL_ROLL_POINT));
		}
		C1802_ChatRouteRespMessage trsMsg = new C1802_ChatRouteRespMessage();
		trsMsg.setChannelType(ChannelType.System.getType());
		trsMsg.setMessage(buffer.toString());
		trsMsg.setContextList(null);
		trsMsg.setSendRoleId(-1);
		trsMsg.setSendRoleName(GameContext.getI18n().getText(TextId.SYSTEM));
		for(AbstractRole role:sameMapMembers){
			role.getBehavior().sendMessage(trsMsg);
		}
	}
	
	
	
	private boolean npcDieFallBox(RoleInstance role,List<GoodsOperateBean> itemList,
			NpcInstance dieNpc,OutputConsumeType ocType){
		if(Util.isEmpty(itemList)){
			return false ;
		}
		boolean result = this.fallBox(role, itemList, ocType, dieNpc.getMapX(),dieNpc.getMapY(), true);
		//广播
		broadcast(role.getRoleName(), dieNpc.getNpcid(), itemList);
		if(dieNpc.getNpc().npcIsBoss()){
			//如果是boss，打印日志
			GameContext.getStatLogApp().goodsFallLog(role, dieNpc.getNpc(), itemList);
		}
		return result ;
	}
	
	private AddGoodsBeanResult fallMail(RoleInstance role, List<GoodsOperateBean> addList, OutputConsumeType ocType){
		if(role == null){
			return new AddGoodsBeanResult().setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
		
		if(Util.isEmpty(addList)){
			return new AddGoodsBeanResult().success();
		}
		
		//大于等于此品质发邮件
		byte mailQualityType = GameContext.getParasConfig().getSendMailQualityType();
		AddGoodsBeanResult result = new AddGoodsBeanResult();
		List<GoodsOperateBean> sendMailList = new ArrayList<GoodsOperateBean>();
		for(GoodsOperateBean bean : addList){
			if(null == bean){
				continue;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
			if(null == gb){
				continue;
			}
			if(gb.getQualityType() >= mailQualityType){
				sendMailList.add(bean);
				continue;
			}
			result.getPutFailureList().add(bean);
		}
		
		if(Util.isEmpty(sendMailList)){
			return result;
		}
		//发送邮件
		this.sendGoodsByMail(role, sendMailList, ocType);
		
		C0003_TipNotifyMessage tipNotifyMsg = new C0003_TipNotifyMessage();
		tipNotifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.FALL_MAIL_TIPS));
		role.getBehavior().sendMessage(tipNotifyMsg);
		
		return result;
	}
	
	private void sendGoodsByMail(RoleInstance role, List<GoodsOperateBean> addList, OutputConsumeType ocType){
		String title = GameContext.getI18n().getText(TextId.FALL_MAIL_TITLE);
		String content = GameContext.getI18n().getText(TextId.FALL_MAIL_CONTENT);
		GameContext.getMailApp().sendMail(role.getRoleId(), title, content,
				MailSendRoleType.System.getName(),
				ocType.getType(), addList);
	}
	
	private void broadcast(String roleName, String npcId, List<GoodsOperateBean> itemList) {
		try{
			if(Util.isEmpty(itemList)) {
				return;
			}
			for(GoodsOperateBean bean : itemList) {
				if(null == bean) {
					continue;
				}
				int goodsId = bean.getGoodsId();
				GameContext.getBroadcastApp().broadCast(roleName, goodsId, npcId, BroadcastType.loot);
			}
		}catch(Exception e){
			logger.error("FallApp.broadcast error:", e);
		}
	}
	

	private boolean boxed(RoleInstance role,List<GoodsOperateBean> goodsList,
			int x,int y,OutputConsumeType ocType){
		if(Util.isEmpty(goodsList)){
			return false;
		}
		//未拾取完毕的物品直接掉落背包
		//将未放入背包的物品(背包已满)放入box
		BoxEntry box = new BoxEntry(role,goodsList,this.getBoxId(),x,y,ocType.getType());
		box.cache();
		box.notifyOwner();
		
		C0003_TipNotifyMessage tipNotifyMsg = new C0003_TipNotifyMessage();
		tipNotifyMsg.setMsgContext(Status.GOODS_BACKPACK_FULL_TIPS.getTips());
		role.getBehavior().sendMessage(tipNotifyMsg);
		
		return true ;
	}

	@Override
	public boolean fallBox(RoleInstance role, List<GoodsOperateBean> itemList,
			OutputConsumeType ocType,int x,int y,boolean fullSendMail) {
		if(null == role || Util.isEmpty(itemList)){
			return false;
		}
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return false ;
		}
		AddGoodsBeanResult result = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(
				role, itemList, ocType);
		if(fullSendMail){
			result = this.fallMail(role, result.getPutFailureList(), ocType);
		}
		this.boxed(role, result.getPutFailureList(), x, y,ocType);
		return result.isSuccess();
	}

	@Override
	public void listEntry(String entryId, RoleInstance caller) {
		if(null == caller || Util.isEmpty(entryId)){
			return ;
		}
		MapInstance mapInstance = caller.getMapInstance();
		if (null == mapInstance) {
			return ;
		}
		
		//采集点情况
		PointType pointType = GameContext.getFallApp().getPointType(entryId);
		if (pointType.isCollectPoint()) {
			CollectablePoint<RoleInstance> cp = mapInstance.getCollectPointMap().get(entryId);
			if(null == cp){
				return ;
			}
			//采集点条件
			String info = cp.isSatisfyCond(caller);
			if(!Util.isEmpty(info)){
				caller.getBehavior().sendMessage(new C0602_FallListRespMessage(FallRespType.error.getType(),info));
				return;
			}
			//触发采集点
			try {
				cp.trigger(caller);
			} catch (ServiceException e) {
				logger.error("", e);
			}
			return ;
		}
		//box情况
		if(pointType.isBox()){
			C0602_FallListRespMessage respMsg = new C0602_FallListRespMessage();
			respMsg.setType(FallRespType.fall.getType());
			respMsg.setFallItemList(this.getBoxFallItemList(entryId, caller));
			respMsg.setInstanceId(entryId);
			caller.getBehavior().sendMessage(respMsg);
		}
	}

	private List<FallItem> getBoxFallItemList(String boxId,RoleInstance role){
		if(null == role){
			return null ;
		}
		Cache<String, BoxEntry> boxes = GameContext.getMapApp().getBoxesCache();
		BoxEntry entry = boxes.getQuiet(boxId);
		if (null == entry) {
			return null;
		}
		if(!entry.isOwner(role)){
			//非自己宝箱
			return null ;
		}
		return entry.listFallItem() ;
	}
	
	
	@Override
	public void pickupEntry(String entryId, RoleInstance role, int itemId) {
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return ;
		}
		PointType pointType = this.getPointType(entryId);
		if(pointType.isCollectPoint()){
			//采集点
			CollectablePoint<RoleInstance> cp = mapInstance.getCollectPointMap().get(entryId);
			if (null == cp) {
				C0603_FallPickupRespMessage respMsg = new C0603_FallPickupRespMessage();
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				respMsg.setInfo(GameContext.getI18n().getText(TextId.FALL_HAS_PICK));
				respMsg.setInstanceId(entryId);
				role.getBehavior().sendMessage(respMsg);
				return;
			}
			cp.pickup(role, itemId);
			return;
		}
		if(pointType.isBox()){
			//宝箱
			Cache<String, BoxEntry> boxes = GameContext.getMapApp().getBoxesCache();
			BoxEntry boxEntry = boxes.getQuiet(entryId);
			if(null == boxEntry || !boxEntry.isOwner(role)){
				C0603_FallPickupRespMessage respMsg = new C0603_FallPickupRespMessage();
				respMsg.setInstanceId(entryId);
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(GameContext.getI18n().getText(TextId.FALL_INVALID_TARGET));
				role.getBehavior().sendMessage(respMsg);
				return;
			}
			boxEntry.pickup(role, itemId);
		}
	}
	
	
	
	@Override
	public AddGoodsBeanResult pickupAction(String entryId, RoleInstance role,
			int itemId,List<FallItem> fallList,int outputType){
		
		AddGoodsBeanResult result = new AddGoodsBeanResult();
		if(Util.isEmpty(fallList)){
			result.setResult(Result.FAIL);
			return result;
		}
		
		List<GoodsOperateBean> pickupList = new ArrayList<GoodsOperateBean>();
		for (Iterator<FallItem> it = fallList.iterator(); it.hasNext();) {
			FallItem item = it.next();
			GoodsLiteNamedItem  goodsItem = item.getGoodsItem() ;
			// 全部拾取
			if(itemId <= 0){
				pickupList.add(GoodsOperateBean.createAddGoodsBean(goodsItem.getGoodsId(), 
						goodsItem.getNum(), goodsItem.getBindType()));
				continue ;
			}
			// 单个拾取
			if(goodsItem.getGoodsId() == itemId){
				pickupList.add(GoodsOperateBean.createAddGoodsBean(goodsItem.getGoodsId(),
						goodsItem.getNum(),goodsItem.getBindType()));
				break ; 
			}
		}
		
		if(Util.isEmpty(pickupList)){
			result.setResult(Result.FAIL);
			return result;
		}
		
		result = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, pickupList, OutputConsumeType.getType(outputType));
		result.setResult(Result.SUCCESS);
		
		return result;
	}
	
	
	/**
	 * 根据ID结构判断点类型
	 * 
	 * @param entryId
	 * @return
	 */
	public PointType getPointType(String entryId) {
		PointType def = PointType.Unknow;
		if (sacred.alliance.magic.util.Util.isEmpty(entryId) || entryId.indexOf(Cat.underline) <= 0) {
			return def;
		}
		int type = Integer.parseInt(entryId.split(Cat.underline)[0]);
		return PointType.get(type);
	}
	
	private void load(){
		String fileName = "";
		String sheetName = "";
		String sourceFile = "";
		try {	
			String path = GameContext.getPathConfig().getXlsPath();
			//世界掉落组
			fileName = XlsSheetNameType.loot_group_world.getXlsName();
			sheetName = XlsSheetNameType.loot_group_world.getSheetName();
			sourceFile = path + fileName;
			this.worldLootListMap = LootLoader.loadLootList(sourceFile);
			
			//NPC掉落组
			fileName = XlsSheetNameType.loot_group_npc.getXlsName();
			sheetName = XlsSheetNameType.loot_group_npc.getSheetName();
			sourceFile = path + fileName;
			this.npcLootListMap = LootLoader.loadLootList(sourceFile);
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	
	}
	
	/**
	 * 获得任务掉落
	 * @param npcTemplateId
	 * @param role
	 * @return
	 */
	private List<GoodsOperateBean> getQuestLootList(String npcTemplateId,RoleInstance role){
		try {
			return GameContext.getUserQuestApp().getQuestFall(
					role, npcTemplateId);
		} catch (Exception e) {
			logger.error("",e);
			return null;
		}
	}
	
	
	private List<GoodsOperateBean> getGoodsBean(LootList lootList){
		if(null == lootList){
			return null ;
		}
		return lootList.getGoodsBean();
	}
	
	
	/**
	 * npc死亡时的掉落(npc本身、世界)
	 * @param templateId
	 * @return
	 */
	private List<GoodsOperateBean> getLootGoodsBeanMap(String templateId){
		if(Util.isEmpty(templateId)) {
			return null;
		}
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(templateId);
		LootList npcLootList = npcLootListMap.get(npcTemplate.getLootNpc()+"");
		LootList worldLootList = worldLootListMap.get(npcTemplate.getLootWorld()+"");
		//npc掉落的物品Map
		//世界与npc掉落合并
		return this.mergerGoodsBeanMap(this.getGoodsBean(npcLootList),this.getGoodsBean(worldLootList));
	}
	/**
	 * 合并Map
	 * @param srcMap
	 * @param destMap
	 * @return
	 */
	private List<GoodsOperateBean> mergerGoodsBeanMap(List<GoodsOperateBean> srcList,List<GoodsOperateBean> destList ){
		List<GoodsOperateBean> valueList = new ArrayList<GoodsOperateBean>();
		if(null == srcList || 0 == srcList.size()){
			if(null != destList){
				valueList.addAll(destList);
			}
			return valueList ;
		}
		if(null == destList || 0 == destList.size()){
			valueList.addAll(srcList);
			return valueList ;
		}
		
		valueList.addAll(srcList);
		valueList.addAll(destList);
		return valueList;
	}
	
	private void sendSummonFall(Collection<RoleInstance> sameMapMembers, List<GoodsOperateBean> goodsList, String name) {
		try{
			if(Util.isEmpty(goodsList)) {
				return;
			}
			String mapStr = Wildcard.getChatGoodsName(goodsList, ChannelType.Map);
			
			C1802_ChatRouteRespMessage mapTrsMsg = new C1802_ChatRouteRespMessage();
			mapTrsMsg.setChannelType(ChannelType.Map.getType());
			mapTrsMsg.setMessage(name + GameContext.getI18n().getText(TextId.FALL_GAIN) + mapStr);
			mapTrsMsg.setContextList(null);
			mapTrsMsg.setSendRoleId(-1);
			mapTrsMsg.setSendRoleName(GameContext.getI18n().getText(TextId.SYSTEM));
			for(AbstractRole role:sameMapMembers){
				role.getBehavior().sendMessage(mapTrsMsg);
			}
		}catch(Exception e){
			logger.error("sendSummonFall error",e);
		}
	}
	
	@Override
	public void setArgs(Object args) {
	}

	@Override
	public void start() {
		this.load();
	}

	@Override
	public void stop() {
	}

	@Override
	public LootList getLootList(String lootId) {
		return this.npcLootListMap.get(lootId);
	}

}
