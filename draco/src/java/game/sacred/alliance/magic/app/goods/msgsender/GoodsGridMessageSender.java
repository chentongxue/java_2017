package sacred.alliance.magic.app.goods.msgsender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.SynchType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsContainerItem;
import com.game.draco.message.item.GoodsGridItem;
import com.game.draco.message.item.GoodsSynchDataItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0503_GoodsSynchDataRespMessage;
import com.game.draco.message.response.C0505_GoodsGridUpdateRespMessage;
import com.game.draco.message.response.C0512_GoodsInfoChangedNotifyMessage;
import com.game.draco.message.response.C0569_ContainerExpandRespMessage;

/**
 * 物品栏同步信息类
 * 
 * 1.SynchType同步类型 = SOME时，表示无效客户端不会变动已画出的格子。
 * 	 此时containerSize = 需要同步的物品个数。
 * 
 * 2.SynchType同步类型 = ALL时，表示客户端需要全部重画格子。
 *   此时containerSize = 角色所拥有的最大格子数目。
 *   
 * 3.SynchType同步类型 = addGrid时，表示添加格子，需告知客户端扩容格子
 * 	 此时containerSize = 所扩容的格子数目，goodsSynchDataItem.ContainerList为空不必赋值
 * 
 * @author Wang.K
 *
 */
public class GoodsGridMessageSender {
	
	/**
	 * 扩容背包同步消息
	 * 此方法只关心消息，不对角色属性做任何处理
	 */
	public void notifyBackpackExpansionMessage(RoleInstance role, int addGirdNum){
		if(role == null || addGirdNum <= 0){
			return ;
		}
		C0569_ContainerExpandRespMessage respMsg = new C0569_ContainerExpandRespMessage();
		respMsg.setBagType(StorageType.bag.getType());
		respMsg.setAddSize((byte)addGirdNum);
		role.getBehavior().sendMessage(respMsg);
		
		C0003_TipNotifyMessage tipsMsg = new C0003_TipNotifyMessage() ;
		tipsMsg.setMsgContext(GameContext.getI18n().messageFormat(TextId.CONTAIN_SIZE_ADD_SUCCESS,String.valueOf(addGirdNum)));
		role.getBehavior().sendMessage(tipsMsg);
	}
	
	
	/**
	 * 同步部分物品栏信息（包含物品基本信息体）
	 *//*
	public void syncSomeGoodsGridMessage(RoleInstance role, Map<Integer,List<RoleGoods>> map){
		List<GoodsSynchDataItem> list = new ArrayList<GoodsSynchDataItem>();
		List<RoleGoods> goodsList ;
		for(int storageType : map.keySet()){
			goodsList = map.get(storageType);
			int size = (null == goodsList)?0:goodsList.size() ;
			GoodsSynchDataItem goodsSynchDataItem = GoodsHelper.buildSynchDataItem(role, goodsList, 
					SynchType.SOME.getType(),(byte)storageType, (byte)size);
			list.add(goodsSynchDataItem);
		}
		C0503_GoodsSynchDataRespMessage resp = new C0503_GoodsSynchDataRespMessage();
		resp.setList(list);
		role.getBehavior().sendMessage(resp);
		
	}*/
	
	/**
	 * 同步部分物品栏信息（包含物品基本信息体）
	 */
	public void syncSomeGoodsGridMessage(RoleInstance role, List<RoleGoods> goodsList, int storageType){
		List<GoodsSynchDataItem> list = new ArrayList<GoodsSynchDataItem>();
		int size = (null == goodsList)?0:goodsList.size() ;
		GoodsSynchDataItem goodsSynchDataItem = GoodsHelper.buildSynchDataItem(role, goodsList, 
				SynchType.SOME.getType(),(byte)storageType, (byte)size);
		list.add(goodsSynchDataItem);
		C0503_GoodsSynchDataRespMessage resp = new C0503_GoodsSynchDataRespMessage();
		resp.setList(list);
		role.getBehavior().sendMessage(resp);
	}
	
	
	public void syncSomeGoodsGridMessage(RoleInstance role, RoleGoods roleGoods){
		if(roleGoods == null){
			return ;
		}
		/*List<GoodsSynchDataItem> list = new ArrayList<GoodsSynchDataItem>();
		GoodsSynchDataItem goodsSynchDataItem = GoodsHelper.buildSynchDataItem(role, roleGoods, 
				SynchType.SOME.getType(), (byte)roleGoods.getStorageType(), (byte)1);
		list.add(goodsSynchDataItem);
		C0503_GoodsSynchDataRespMessage resp = new C0503_GoodsSynchDataRespMessage();
		resp.setList(list);
		role.getBehavior().sendMessage(resp);*/
		
		C0512_GoodsInfoChangedNotifyMessage respMsg = new C0512_GoodsInfoChangedNotifyMessage();
		respMsg.setBagType(roleGoods.getStorageType());
		respMsg.setTargetId(GoodsHelper.getTargetId(roleGoods));
		respMsg.setItem(GoodsHelper.createContainerItem(roleGoods));
		role.getBehavior().sendMessage(respMsg);
	}
	
	
	/**
	 * 同步所有物品栏信息（包含物品基本信息体）
	 */
	public void syncAllGoodsGridMessage(RoleInstance role, Map<Integer,List<RoleGoods>> map){
		List<GoodsSynchDataItem> list = new ArrayList<GoodsSynchDataItem>();
		for(int storageType : map.keySet()){
			List<RoleGoods> goodsList = map.get(storageType);
			list.addAll(this.buildList(role, goodsList, storageType));
		}
		C0503_GoodsSynchDataRespMessage resp = new C0503_GoodsSynchDataRespMessage();
		resp.setList(list);
		role.getBehavior().sendMessage(resp);
	}
	
	
	private List<GoodsSynchDataItem> buildList(RoleInstance role, 
			List<RoleGoods> goodsList, int storageType){
		List<GoodsSynchDataItem> list = new ArrayList<GoodsSynchDataItem>();
		int maxGridCount = 0;
		if(StorageType.bag.getType() == storageType){
			maxGridCount = role.getBackpackCapacity();
			maxGridCount = Math.max(maxGridCount, goodsList.size());
		}else if(StorageType.warehouse.getType() == storageType){
			maxGridCount = role.getWarehoseCapacity();
			maxGridCount = Math.max(maxGridCount, goodsList.size());
		}else if(StorageType.hero.getType() == storageType){
			maxGridCount = ParasConstant.HERO_EQUIP_MAX_NUM ;
		}
		GoodsSynchDataItem goodsSynchDataItem  = GoodsHelper.buildSynchDataItem(role, goodsList, 
				SynchType.ALL.getType(),(byte)storageType, (byte)maxGridCount);
		list.add(goodsSynchDataItem);
		return list ;
	}
	
	/**
	 * 同步所有物品栏信息（包含物品基本信息体）
	 */
	public void syncAllGoodsGridMessage(RoleInstance role, List<RoleGoods> goodsList, int storageType){
		C0503_GoodsSynchDataRespMessage resp = new C0503_GoodsSynchDataRespMessage();
		resp.setList(this.buildList(role, goodsList, storageType));
		role.getBehavior().sendMessage(resp);
	}
	
	
	
	/**
	 * 物品栏状态更新信息（物品叠放数、删除物品栏）
	 * 参数列表：角色ID，物品<容器类型,对应的物品集合>，操作提示（1：成功 0：失败），错误信息
	 */
	public void updateGoodsGridMessage(RoleInstance role, Map<Integer,List<RoleGoods>> map){
		if(Util.isEmpty(map)){
			return ;
		}
		List<GoodsContainerItem> updateGridItems = new ArrayList<GoodsContainerItem>();
		for(int storageType : map.keySet()){
			GoodsContainerItem storageItem = new GoodsContainerItem();
			storageItem.setContainerType((byte)storageType);
			List<GoodsGridItem> items = createGridItemList(map.get(storageType));
			storageItem.setItemes(items);
			updateGridItems.add(storageItem);
		}
		C0505_GoodsGridUpdateRespMessage resp = new C0505_GoodsGridUpdateRespMessage();
		resp.setStatus((byte)1);
		resp.setInfo("");
		resp.setItemes(updateGridItems);
		role.getBehavior().sendMessage(resp);
	}
	
	
	
	
	
	/**
	 * 物品栏状态更新信息（物品叠放数、删除物品栏）
	 * 参数列表：角色ID，物品<容器类型,对应的物品集合>，容器类型，操作提示（1：成功 0：失败），错误信息
	 */
	public void updateGoodsGridMessage(RoleInstance role, List<RoleGoods> goodsList, 
			StorageType storageType){
		if(Util.isEmpty(goodsList)){
			return ;
		}
		List<GoodsContainerItem> updateGridItems = new ArrayList<GoodsContainerItem>();
		GoodsContainerItem storageItem = new GoodsContainerItem();
		storageItem.setContainerType((byte)storageType.getType());
		List<GoodsGridItem> items = createGridItemList(goodsList);
		storageItem.setItemes(items);
		updateGridItems.add(storageItem);
		C0505_GoodsGridUpdateRespMessage resp = new C0505_GoodsGridUpdateRespMessage();
		resp.setStatus((byte)1);
		resp.setInfo("");
		resp.setItemes(updateGridItems);
		role.getBehavior().sendMessage(resp);
	}
	
	
	/** 封装更新格子工具类 */
	private  List<GoodsGridItem> createGridItemList(List<RoleGoods> goodsList){
		List<GoodsGridItem> items = new ArrayList<GoodsGridItem>();
		for(RoleGoods roleGoods : goodsList){
			GoodsGridItem gridItem = new GoodsGridItem();
			gridItem.setCount((short)roleGoods.getCurrOverlapCount());
			gridItem.setGoodsInstanceId(roleGoods.getId());
			gridItem.setIndex((byte)roleGoods.getGridPlace());
			items.add(gridItem);
		}
		return items;
	}
	
	
}
