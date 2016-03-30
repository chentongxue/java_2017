package sacred.alliance.magic.app.map.point;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.message.item.CollectPointIdItem;
import com.game.draco.message.item.CollectPointIdItem2;
import com.game.draco.message.item.FallItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C0606_CollectPointNotifyMessage;
import com.game.draco.message.response.C0603_FallPickupRespMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.CollectPointNotifyType;
import sacred.alliance.magic.base.FallOptType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.PointType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class QuestCollectPoint extends DefaultCollectPoint<RoleInstance> {

	private int questId;
	private boolean init = false;
	private static AtomicLong idGen = new AtomicLong(0);
	private AtomicBoolean lock  ;

	public QuestCollectPoint(int x, int y, CollectPoint collectPoint) {
		this.x = x;
		this.y = y;
		this.collectPoint = collectPoint;
		this.lock = new AtomicBoolean(false);
		this.questId = collectPoint.getMasterId();
		this.instanceId = PointType.QuestCollectPoint.getType() + Cat.underline
				+ idGen.getAndIncrement();
	}

	@Override
	public String isSatisfyCond(RoleInstance role) {
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if (null == quest) {
			return FALSE;
		}
		if (!role.getQuestLogMap().containsKey(questId)) {
			return FALSE;
		}
		// 已经提交或者已经完成
		if (/*GameContext.getGameContext().getUserQuestApp().hasFinishQuest(
				role, questId)
				|| */quest.canSubmit(role)) {
			return FALSE;
		}
		return TRUE;
	}
	
	private boolean isFallable(){
		return (null != this.collectPoint && this.collectPoint.isFallable()) ;
	}

	@Override
	public void trigger(RoleInstance role) throws ServiceException {
		//如果是任务采集点，并且不会掉落物品。则不让采集点消失。
		if(this.questId > 0 && !this.isFallable()){
			//this.doTrigger(role,false);
			//只触发任务不将采集点消失
			try {
				//触发采集任务
				GameContext.getUserQuestApp().triggerEvent(role, this.collectPoint.getId());
			} catch (Exception e) {
				logger.error("QuestCollectPoint.trigger error: ", e);
			}
			return;
		}
		boolean canDo = this.lock.compareAndSet(false, true) ;
		if(!canDo){
			this.busyAction(role,FallOptType.FALL_LIST);
			return ;
		}
		try {
			if (!EventPoint.TRUE.equals(this.isSatisfyCond(role))) {
				this.canotAction(role, FallOptType.FALL_LIST,
						GameContext.getI18n().getText(TextId.EVENT_POINT_NOT_COLLECT));
				return;
			}
			this.doTrigger(role, true);
		} finally {
			this.lock.set(false);
		}
	}
	
	
	private List<GoodsOperateBean>  getFallGoods(RoleInstance role){
		Quest quest = GameContext.getGameContext().getQuestApp().getQuest(questId);
		if (null == quest) {
			return null ; 
		}
		return quest.getQuestFall(role, collectPoint.getId());
	}
	
	
	private void doTrigger(RoleInstance role,boolean isFallable){
		try {
			//触发采集任务
			GameContext.getUserQuestApp().triggerEvent(role, this.collectPoint.getId());
		} catch (Exception e) {
			logger.error("QuestCollectPoint.trigger error: ", e);
		}
		try {
			if (isFallable) {
				List<GoodsOperateBean> list = this.getFallGoods(role);
				GameContext.getFallApp().fallBox(role, list,
						OutputConsumeType.quest_collection, role.getMapX(),
						role.getMapY(), true);
			}
		}finally{
			//不论如何,直接消失
			this.disappearAction(role,this.getPointType(this.instanceId));
		}
	}

	@Override
	public List<FallItem> getFallItemList(RoleInstance role) {
		if (init) {
			return fallItemList;
		}
		Quest quest = GameContext.getGameContext().getQuestApp().getQuest(questId);
		if (null == quest) {
			return null; 
		}
		List<GoodsOperateBean> list = quest.getQuestFall(role, collectPoint.getId());
		fallItemList = Converter.getFallItemList(list);
		init = true;
		return fallItemList;
	}
	
	public void disappearAction(RoleInstance role,PointType type) {
		// 通知地图用户此采集点消失
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return ;
		}
				
		C0606_CollectPointNotifyMessage notify = new C0606_CollectPointNotifyMessage();
		notify.setType(CollectPointNotifyType.Disappear.getType());
		CollectPointIdItem idItem = new CollectPointIdItem();
		CollectPointIdItem2 item2 = new CollectPointIdItem2();
		item2.setInstanceIds(instanceId);
		item2.setCanPick("");
		item2.setCollectType((byte)type.getType());
		item2.setDisplayFlag((byte)0);
		idItem.getList().add(item2);
		notify.setItem(idItem);
		mapInstance.broadcastMap(null, notify);
		// 从地图中删除此采集点,放入刷新列表
		mapInstance.removeCollectPoint(instanceId);
		if(TRUE == this.isSatisfyCond(role)){
			return ;
		}
		notifyDisappear(role,type,this.getCollectPoint().getId());
		
	}
	
	public static void notifyDisappear(RoleInstance role,PointType type,String id) {
		MapInstance mapInstance = role.getMapInstance();
		if (null == mapInstance) {
			return;
		}
		// 判断同类型的其他任务
		java.util.Map<String, Set<String>> mapping = mapInstance
				.getCollectPointMapping();
		Set<String> allInstance = mapping.get(id);
		if (null == allInstance || 0 == allInstance.size()) {
			return;
		}
		// 将这些采集点设置成不可采
		C0606_CollectPointNotifyMessage notifySelf = new C0606_CollectPointNotifyMessage();
		notifySelf.setType(CollectPointNotifyType.CollectUnable.getType());
		CollectPointIdItem cpIdObject = new CollectPointIdItem();
		for (String oneId : allInstance) {
			CollectPointIdItem2 oneIdItem = new CollectPointIdItem2();
			oneIdItem.setInstanceIds(oneId);
			oneIdItem.setType((byte) 0);
			oneIdItem.setCanPick(FALSE);
			oneIdItem.setCollectType((byte) type.getType());
			oneIdItem.setDisplayFlag((byte) 0);
			cpIdObject.getList().add(oneIdItem);
		}
		notifySelf.setItem(cpIdObject);
		GameContext.getMessageCenter().sendSysMsg(role, notifySelf);
	}

	@Override
	public void pickup(RoleInstance role, int itemId) {
		boolean canDo = this.lock.compareAndSet(false, true);
		if(!canDo){
			this.busyAction(role,FallOptType.FALL_PK);
			return ;
		}
		try {
			// 拾取操作
			if (Util.isEmpty(fallItemList)) {
				this.disappearAction(role,this.getPointType(this.instanceId));
				return;
			}
			// 拾取逻辑
			AddGoodsBeanResult result = GameContext.getFallApp().pickupAction(
					this.instanceId, role, itemId,fallItemList,OutputConsumeType.quest_collection.getType());
			
			C0603_FallPickupRespMessage respMsg = new C0603_FallPickupRespMessage();
			respMsg.setInstanceId(this.instanceId);
			respMsg.setItemId(itemId);
			// 参数错误的情况
			if(!result.isSuccess()){
				respMsg.setStatus(RespTypeStatus.FAILURE);
				respMsg.setInfo(GameContext.getI18n().getText(TextId.FALL_HAD_PICKUP));
				role.getBehavior().sendMessage(respMsg);
				return ;
			}
			
			List<GoodsOperateBean> failureList = result.getPutFailureList();
			if(!Util.isEmpty(failureList)){
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				respMsg.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
				role.getBehavior().sendMessage(respMsg);
			}
			
			// 删除添加成功的物品
			List<GoodsOperateBean> successList = result.getPutSuccessList();
			for(Iterator<FallItem> it = fallItemList.iterator();it.hasNext();){
				FallItem item = it.next();
				if(this.isSuccess(successList, item)){
					it.remove();
				}
			}
			
			// 拾取后再次判断是否应该消失
			if (Util.isEmpty(fallItemList)) {
				role.getMapInstance().removeCollectPoint(this.instanceId);
				this.disappearAction(role,PointType.QuestCollectPoint);
				respMsg.setStatus(RespTypeStatus.SUCCESS);
				role.getBehavior().sendMessage(respMsg);
				return;
			}
			
		} finally {
			this.lock.set(false);
		}

	}
	
	/**已经成功拾取*/
	private boolean isSuccess(List<GoodsOperateBean> successList,FallItem item){
		GoodsLiteNamedItem goodsItem = item.getGoodsItem() ;
		for(GoodsOperateBean agb : successList){
			if((agb.getGoodsId() == goodsItem.getGoodsId()) 
					&& (agb.getBindType() == BindingType.get(goodsItem.getBindType()))){
				return true;
			}
		}
		return false;
	}

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	
}
