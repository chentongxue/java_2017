package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.GoodsSynchDataItem;
import com.game.draco.message.item.StorageContainerItem;

public class GoodsHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(GoodsHelper.class);
	
	/**
	 * 物品简单结构
	 * @param goodsList
	 * @return
	 */
	public static List<GoodsLiteItem> getGoodsLiteList(List<GoodsOperateBean> goodsList){
		List<GoodsLiteItem> liteList = new ArrayList<GoodsLiteItem>();
		if (Util.isEmpty(goodsList)) {
			return liteList;
		}
		for(GoodsOperateBean bean : goodsList){
			if(null == bean){
				continue;
			}
			int goodsId = bean.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb){
				continue;
			}
			GoodsLiteItem item = gb.getGoodsLiteItem();
			item.setBindType(bean.getBindType().getType());
			//设置数目
			item.setNum((short) bean.getGoodsNum());
			liteList.add(item);
		}
		return liteList;
	}
	
	/**
	 * 带名字的物品简单结构
	 * @param goodsList
	 * @return
	 */
	public static List<GoodsLiteNamedItem> getGoodsLiteNamedList(List<GoodsOperateBean> goodsList){
		List<GoodsLiteNamedItem> liteNamedList = new ArrayList<GoodsLiteNamedItem>();
		if (Util.isEmpty(goodsList)) {
			return liteNamedList;
		}
		for(GoodsOperateBean bean : goodsList){
			if(null == bean){
				continue;
			}
			int goodsId = bean.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb){
				continue;
			}
			GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
			item.setBindType(bean.getBindType().getType());
			//设置数目
			item.setNum((short) bean.getGoodsNum());
			liteNamedList.add(item);
		}
		return liteNamedList;
	}
	
	public static GoodsLiteNamedItem getMoneyGoodsLiteNamedItem(AttributeType attribute, short value) {
		if(value <= 0){
			return null;
		}
		short imageId=0;
		switch (attribute) {
		case bindingGoldMoney:
			imageId = 207;
			break;
		case goldMoney:
			imageId = 224;
			break;
		case silverMoney:
			imageId = 150;
			break;
		default:
			break;
		}
		GoodsLiteNamedItem item = new GoodsLiteNamedItem();
		item.setNum(value);
		item.setGoodsImageId(imageId);
		item.setQualityType((byte)2);
		item.setGoodsName(attribute.getName());
		return item;
	}
	
	public static GoodsLiteItem getMoneyGoodsLiteItem(AttributeType attribute, short value) {
		if(value <= 0){
			return null;
		}
		short imageId=0;
		switch (attribute) {
		case bindingGoldMoney:
			imageId = 207;
			break;
		case goldMoney:
			imageId = 224;
			break;
		case silverMoney:
			imageId = 150;
			break;
		default:
			break;
		}
		GoodsLiteItem item = new GoodsLiteItem();
		item.setNum(value);
		item.setGoodsImageId(imageId);
		item.setQualityType((byte)2);
		return item;
	}
	
	/**
	 * 添加物品 背包满了发邮件
	 * @param role 角色
	 * @param addList 物品列表
	 * @param outputConsumeType 产出消耗类型
	 * @param mailSendRoleType 邮件发件人类型
	 */
	public static void addGoodsForBagOrMail(RoleInstance role, List<GoodsOperateBean> addList, OutputConsumeType outputConsumeType,
			MailSendRoleType mailSendRoleType){
		//向背包中添加物品
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList, outputConsumeType);
		//背包满了发邮件
		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
		if(Util.isEmpty(putFailureList)){
			GameContext.getMailApp().sendMail(role.getRoleId(), mailSendRoleType.getName(), "",
					mailSendRoleType.getName(), outputConsumeType.getType(), putFailureList);
		}
	}
	
	private static  List<StorageContainerItem> createContainerItemList(Collection<RoleGoods> goodsList){
		List<StorageContainerItem> containerList = new ArrayList<StorageContainerItem>();
		if(Util.isEmpty(goodsList)){
			return containerList ;
		}
		for(RoleGoods roleGoods : goodsList){
			StorageContainerItem containerItem = createContainerItem(roleGoods);
			if(containerItem == null){
				continue;
			}
			containerList.add(containerItem);
		}
		return containerList;
	}
	
	private static  List<StorageContainerItem> createContainerItemList(RoleGoods[] goodsList){
		List<StorageContainerItem> containerList = new ArrayList<StorageContainerItem>();
		if(null == goodsList){
			return containerList ;
		}
		for(RoleGoods roleGoods : goodsList){
			StorageContainerItem containerItem = createContainerItem(roleGoods);
			if(containerItem == null){
				continue;
			}
			containerList.add(containerItem);
		}
		return containerList;
	}
	
	private static StorageContainerItem createContainerItem(RoleGoods roleGoods){
		if(roleGoods == null){
			return null;
		}
		GoodsBaseItem goodsBaseItem = getGoodsBaseItem(roleGoods);
		if(goodsBaseItem == null){
			return null;
		}
		StorageContainerItem containerItem = new StorageContainerItem();
		containerItem.setGoodsInstanceId(roleGoods.getId());
		containerItem.setIndex((byte)roleGoods.getGridPlace());
		containerItem.setCount((short)roleGoods.getCurrOverlapCount());
		containerItem.setBaseItem(goodsBaseItem);
		return containerItem;
	}
	
	private static GoodsBaseItem getGoodsBaseItem(RoleGoods roleGoods){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(goodsBase == null){
			return null;
		}
		return  goodsBase.getGoodsBaseInfo(roleGoods);
	}
	
	public static GoodsSynchDataItem buildSynchDataItem(RoleInstance role,Collection<RoleGoods> roleGoods,
			byte synchType,byte containerType,byte containerSize){
		GoodsSynchDataItem goodsSynchDataItem = new GoodsSynchDataItem();
		goodsSynchDataItem.setSynchType(synchType);
		goodsSynchDataItem.setContainerType(containerType);
		goodsSynchDataItem.setContainerSize(containerSize);
		goodsSynchDataItem.getContainerList().addAll(createContainerItemList(roleGoods));
		return goodsSynchDataItem ;
	}
	
	public static GoodsSynchDataItem buildSynchDataItem(RoleInstance role,RoleGoods[] roleGoods,
			byte synchType,byte containerType,byte containerSize){
		GoodsSynchDataItem goodsSynchDataItem = new GoodsSynchDataItem();
		goodsSynchDataItem.setSynchType(synchType);
		goodsSynchDataItem.setContainerType(containerType);
		goodsSynchDataItem.setContainerSize(containerSize);
		goodsSynchDataItem.getContainerList().addAll(createContainerItemList(roleGoods));
		return goodsSynchDataItem ;
	}
	
	public static GoodsSynchDataItem buildSynchDataItem(RoleInstance role, RoleGoods roleGoods,
			byte synchType,byte containerType,byte containerSize){
		GoodsSynchDataItem goodsSynchDataItem = new GoodsSynchDataItem();
		goodsSynchDataItem.setSynchType(synchType);
		goodsSynchDataItem.setContainerType(containerType);
		goodsSynchDataItem.setContainerSize(containerSize);
		goodsSynchDataItem.getContainerList().add(createContainerItem(roleGoods));
		return goodsSynchDataItem ;
	}
}
