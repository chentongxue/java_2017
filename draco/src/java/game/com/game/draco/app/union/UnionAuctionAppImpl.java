package com.game.draco.app.union;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.module.cache.SimpleCache;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.app.union.domain.auction.UnionAuction;
import com.game.draco.app.union.domain.auction.UnionRoleAuction;
import com.game.draco.app.union.vo.RoleAuctionResult;
import com.game.draco.message.internal.C0088_UnionAuctionCalculateMessage;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.UnionAuctionItem;
import com.game.draco.message.response.C2759_UnionAuctionListRespMessage;

public class UnionAuctionAppImpl implements UnionAuctionApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	
	private Cache<String,String> auctionCache = null ;

	//公会拍卖行 Map<公会ID,Map<拍卖行唯一ID,拍卖物品>>
	private ConcurrentMap<String,ConcurrentMap<String,Auction>> unionAuctionMap = Maps.newConcurrentMap();
	
	//角色押注数据 Map<物品ID，Map<角色ID,角色拍卖数据>>
	private ConcurrentMap<String,ConcurrentMap<Integer,UnionRoleAuction>> roleAuctionMap = Maps.newConcurrentMap();
	
	private Map<String, Auction> getAuctionMapForUnion(String unionId){
		if (Util.isEmpty(unionId)) {
			return null;
		}
		return unionAuctionMap.get(unionId);
	}
	
	private Map<String, Auction> mustGetAuctionMapForUnion(String unionId) {
		return Util.getIfAbsent(unionId, this.unionAuctionMap);
	}
	
	
	@Override
	public void initAuction() {
		//启动cache
		this.initCache();
		try{
			//先加载拍卖行竞拍价格
			List<UnionRoleAuction> unionRoleAuctionList = GameContext.getBaseDAO().selectAll(UnionRoleAuction.class);
			if(!Util.isEmpty(unionRoleAuctionList)){
				for(UnionRoleAuction unionRoleAuction : unionRoleAuctionList){
					String instanceId = unionRoleAuction.getUuid() ;
					Map<Integer,UnionRoleAuction> priceMap = Util.getIfAbsent(instanceId,this.roleAuctionMap) ;
					priceMap.put(unionRoleAuction.getRoleId(), unionRoleAuction);
				}
			}
			
			//加载拍卖行中的物品
			List<UnionAuction> unionAuctionList = GameContext.getBaseDAO().selectAll(UnionAuction.class);
			if(!Util.isEmpty(unionAuctionList)){
				for(UnionAuction unionAuction : unionAuctionList){
					Map<String,Auction> map = this.mustGetAuctionMapForUnion(unionAuction.getUnionId());
					Auction auction = unionAuction.parseAuctionData();
					map.put(unionAuction.getId(), auction);
					
					Date now = new Date();
					long time = auction.getOverTime() - now.getTime();
					if(time < 0){
						time = 5000;
					}
					auctionCache.put(unionAuction.getId(), unionAuction.getId(), time, TimeUnit.MILLISECONDS);
				}
			}
		}catch(Exception e){
			logger.error("initAuction",e);
		}
	}

	/**
	 *  添加物品到拍卖行
	 */
	@Override
	public void addAuctionGoods(String unionId,Auction auction) {
		try{
			if(GameContext.getUnionApp().getUnion(unionId) == null){
				return;
			}
			
			//获得本公会的拍卖行map
			Map<String,Auction> map = this.mustGetAuctionMapForUnion(unionId);
			
			String auctionId = newAuctionInstanceId();
			map.put(auctionId, auction);
			
			UnionAuction uAuction = new UnionAuction();
			uAuction.setUnionId(unionId);
			uAuction.buildAuctionData(auction);
			uAuction.setId(auctionId);
			GameContext.getBaseDAO().insert(uAuction);
			
			auctionCache.put(auctionId, unionId);
		}catch(Exception e){
			logger.error("addAuctionGoods",e);
		}
	}
	
	
	/**
	 * 封装拍卖数据
	 */
	@Override
	public Auction packagingAuction(String unionId, byte activityId,byte groupId, 
			List<GoodsItem> itemList,Set<Integer> roleSet) {
		Auction auction = new Auction();
		auction.setActivityId(activityId);
		auction.setGroupId(groupId);
		auction.setGoodsList(itemList);
		auction.setRoleSet(roleSet);
		auction.setOverTime((long)(System.currentTimeMillis() + DateUtil.ONE_DAY_MILLIS));
		return auction;
	}

	@Override
	public Map<String,Auction> getUnionAuction(String unionId,int roleId) {
		Map<String,Auction> showMap = Maps.newHashMap() ;
		try{
			Map<String,Auction> auctionMap = this.getAuctionMapForUnion(unionId);
			if(Util.isEmpty(auctionMap)){
				return showMap ;
			}
			for(Entry<String,Auction> entry :auctionMap.entrySet()){
				//过滤角色打过的BOSS
				Auction auction = entry.getValue() ;
				if(!auction.existRoleId(roleId)){
					continue ;
				}
				showMap.put(entry.getKey(),auction);
			}
			return showMap; 
		}catch(Exception e){
			logger.error("getUnionAuction",e);
		}
		return showMap;
	}
	
	
	/**
	 * 发送邮件
	 * @param type
	 * @param receiveRoleId
	 * @param goodsName
	 * @param buyRoleName
	 * @param goods
	 */
	private void sendMail(boolean isSuccess, int receiveRoleId, String buyRoleName,
			GoodsItem goods,int consumeDkp) {
		if (goods == null) {
			return;
		}
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
				goods.getGoodsId());
		if (goodsBase == null) {
			return;
		}
		String content = "";
		List<GoodsOperateBean> goodsList = Lists.newArrayList();
		MailAttriBean attriBean = null ;
		if (isSuccess) {
			goodsList.add(new GoodsOperateBean(goods.getGoodsId(), goods
					.getGoodsNum(), BindingType.get(goods.getGoodsBinded())));
			content = GameContext.getI18n().messageFormat(
					TextId.UNION_AUCTIN_SUCCESS, goodsBase.getName(),consumeDkp);
		} else {
			content = GameContext.getI18n().messageFormat(
					TextId.UNION_AUCTIN_ERROR, goodsBase.getName(),
					buyRoleName,consumeDkp);
			//返回的DKP
			attriBean = new MailAttriBean() ;
			attriBean.setDkp(consumeDkp);
		}
		GameContext.getMailApp().sendMailAsync(String.valueOf(receiveRoleId),
				MailSendRoleType.UnionAuction.getName(), content,
				MailSendRoleType.Union.getName(),
				OutputConsumeType.union_auction_mail_reward.getType(),
				goodsList,attriBean);
	}
	
	/**
	 * 押注
	 * @param unionId
	 * @param uuid
	 * @param roleId
	 * @param price
	 */
	@Override
	public RoleAuctionResult addRoleAuction(RoleInstance role,
			String goodsInstanceId, int price) {
		RoleAuctionResult result = curDuctAuctionDkp(role, price,
				goodsInstanceId);
		if (!result.isSuccess()) {
			return result;
		}
		try {
			Map<Integer, UnionRoleAuction> priceMap = Util.getIfAbsent(
					goodsInstanceId, this.roleAuctionMap);
			UnionRoleAuction rolePrice = priceMap.get(role.getIntRoleId());
			if (null == rolePrice) {
				// 首次出价
				rolePrice = new UnionRoleAuction();
				rolePrice.setPrice(price);
				rolePrice.setRoleId(role.getIntRoleId());
				rolePrice.setUuid(goodsInstanceId);
				rolePrice.setUnionId(role.getUnionId());
				rolePrice.setCreateTime(System.currentTimeMillis());
				priceMap.put(role.getIntRoleId(), rolePrice);
			} else {
				rolePrice.setPrice(price);
			}
			result.setBidPrice(rolePrice.getPrice());
			GameContext.getBaseDAO().saveOrUpdate(rolePrice);
		} catch (Exception e) {
			logger.error("addRoleAuction", e);
		}
		return result;
	}
	
	/**
	 * 扣除DKP
	 */
	private RoleAuctionResult curDuctAuctionDkp(RoleInstance role,int price,String goodsInstanceId){
		RoleAuctionResult result = new RoleAuctionResult();
		try{
			Map<String,Auction> mapAuction = getUnionAuction(role.getUnionId(),role.getIntRoleId());
			if(Util.isEmpty(mapAuction)){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_AUCTION_REFRESH));
				return result;
			}
			int basePrice = 0;
			for(Entry<String,Auction> auction : mapAuction.entrySet()){
				GoodsItem goodsItem = auction.getValue().findGoodsItem(goodsInstanceId);
				if(null != goodsItem){
					basePrice = GameContext.getUnionDataApp().getGoodsBasePrice(goodsItem.getGoodsId());
					break;
				}
			}
			int oldPrice = getRoleAuctionPrice(role.getIntRoleId(),goodsInstanceId);
			if(price < oldPrice){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_AUCTION_NOW_PRICE_ERR));
				return result;
			}
			
			if(oldPrice > basePrice){
				price -= oldPrice;
			}else if(price <= basePrice){
				result.setInfo(GameContext.getI18n().getText(TextId.UNION_AUCTION_PRICE_ERR));
				return result;
			}
			
			UnionMember member = GameContext.getUnionApp().getUnionMember(role.getUnionId(),role.getIntRoleId());
			if(member != null){
				if(member.getDkp() < price){
					result.setInfo(GameContext.getI18n().getText(TextId.UNION_AUCTION_DKP_ERR));
					return result;
				}
				GameContext.getUnionApp().changeMemberDkp(role,price,OperatorType.Decrease,FunType.didAuction,false);
				result.success();
			}
		}catch(Exception e){
			logger.error("curDuctAuctionDkp",e);
		}
		return result;
	}
	
	/**
	 * 获得角色出价
	 * @param role
	 * @param uuid
	 * @return
	 */
	private int getRoleAuctionPrice(int roleId,String goodsInstanceId){
		Map<Integer,UnionRoleAuction> priceMap = this.roleAuctionMap.get(goodsInstanceId);
		if(null == priceMap){
			return 0 ;
		}
		UnionRoleAuction rolePrice = priceMap.get(roleId);
		return (null == rolePrice)?0:rolePrice.getPrice() ;
	}
	

	@Override
	public C2759_UnionAuctionListRespMessage sendC2759_UnionAuctionListRespMessage(
			String unionId, int roleId) {
		C2759_UnionAuctionListRespMessage respMsg = new C2759_UnionAuctionListRespMessage();
		try{
			List<UnionAuctionItem> auctionItemList = Lists.newArrayList();
			Map<String,Auction> auctionMap = getUnionAuction(unionId, roleId);
			if(Util.isEmpty(auctionMap)){
				return respMsg;
			}
			for(Entry<String,Auction> auction : auctionMap.entrySet()){
				if(auction == null){
					continue;
				}
				List<GoodsItem> goodsList = auction.getValue().getGoodsList();
				if(goodsList == null || goodsList.isEmpty()){
					continue;
				}
				for(GoodsItem item : goodsList){
					UnionAuctionItem auctionItem = new UnionAuctionItem();
					auctionItem.setUuid(item.getGoodsInstanceId());
					int overTime = (int)((auction.getValue().getOverTime() - System.currentTimeMillis())/1000);
					if(overTime <= 1){
						continue;
					}
					auctionItem.setOverTime(overTime);
					int basePrice = GameContext.getUnionDataApp().getGoodsBasePrice(item.getGoodsId());
					auctionItem.setPrice(basePrice);
					
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(item.getGoodsId());
					
					GoodsLiteNamedItem goodsItem = goodsBase.getGoodsLiteNamedItem() ;
					goodsItem.setBindType(item.getGoodsBinded());
					goodsItem.setGoodsId(item.getGoodsId());
					goodsItem.setNum(item.getGoodsNum());
					auctionItem.setGoodsItem(goodsItem);
					//出价
					int price = this.getRoleAuctionPrice(roleId, item.getGoodsInstanceId());
					if(price > 0){
						auctionItem.setBid(price);
						auctionItem.setPrice(-1);
					}
					auctionItemList.add(auctionItem);
				}
			}
			
			Collections.sort(auctionItemList,itemComparator);
			
			respMsg.setItem(auctionItemList);
		}catch(Exception e){
			logger.error("sendC2759_UnionAuctionListRespMessage",e);
		}
		return respMsg;
	}
	
	Comparator<UnionAuctionItem> itemComparator = new Comparator<UnionAuctionItem>(){
		@Override
		public int compare(UnionAuctionItem h1, UnionAuctionItem h2) {
			//结束时间
			if(h1.getOverTime() < h2.getOverTime()){
				return -1;
			}
			return 0;
		}
	} ;
	
	
	/**
	 * 拍卖行排序
	 * @param unionRoleAuctionList
	 */
	private void sortRoleAuction(List<UnionRoleAuction> unionRoleAuctionList){
		Collections.sort(unionRoleAuctionList, new Comparator<UnionRoleAuction>() {
			public int compare(UnionRoleAuction info1, UnionRoleAuction info2) {
				if(info1.getPrice() > info2.getPrice()){
					return -1;
				}
				if(info1.getPrice() < info2.getPrice()){
					return 1;
				}
				if(info1.getCreateTime() < info2.getCreateTime()){
					return -1;
				}
				if(info1.getCreateTime() > info2.getCreateTime()){
					return 1;
				}
				UnionMember member1 = GameContext.getUnionApp().getUnionMember(info1.getUnionId(),info1.getRoleId());
				UnionMember member2 = GameContext.getUnionApp().getUnionMember(info2.getUnionId(),info2.getRoleId());
				//此时有可能某人已经没有公会
				//优先有公会的
				if(null == member1 && null == member2){
					return 1 ;
				}
				if(null == member1 || null == member2){
					return (null == member2)?1:-1 ;
				}
				if(member1.getPosition() < member2.getPosition()){
					return -1;
				}
				if(member1.getPosition() > member2.getPosition()){
					return 1;
				}
				
				if(member1.getLevel() > member2.getLevel()){
					return -1;
				}
				if(member1.getLevel() < member2.getLevel()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	private String getRoleName(String unionId, int roleId) {
		RoleInstance role = GameContext.getOnlineCenter()
				.getRoleInstanceByRoleId(String.valueOf(roleId));
		if (null != role) {
			return role.getRoleName();
		}
		UnionMember member = GameContext.getUnionApp().getUnionMember(unionId,
				roleId);
		if (null != member) {
			return member.getRoleName();
		}
		//只能从数据库中取了
		try {
			role = GameContext.getRoleService().selectByRoleId(String.valueOf(roleId));
			if(null != role){
				return role.getRoleName();
			}
		} catch (Exception e) {
		}
		return "" ;
	}
	
	@Override
	public void calculateRoleAuction(String auctionId,String unionId){
		Map<String,Auction> auctionMap = this.getAuctionMapForUnion(unionId);
		if(Util.isEmpty(auctionMap)){
			return ;
		}
		Auction auction = auctionMap.get(auctionId) ;
		if(null == auction){
			return ;
		}
		List<GoodsItem> goodsItemList = auction.getGoodsList();
		if(Util.isEmpty(goodsItemList)){
			return ;
		}
		for(GoodsItem item : goodsItemList){
			//获得每件物品的排名报价
			Map<Integer,UnionRoleAuction> priceMap = roleAuctionMap.get(item.getGoodsInstanceId()) ;
			if(Util.isEmpty(priceMap)){
				continue ;
			}
			List<UnionRoleAuction> priceList = Lists.newArrayList();
			priceList.addAll(priceMap.values());
			//排序获得最高出价者
			sortRoleAuction(priceList);
			UnionRoleAuction roleAuction = priceList.get(0);
			//购买者名称
			String buyRoleName = this.getRoleName(unionId, roleAuction.getRoleId()) ;
			//给竞拍成功者发送邮件
			sendMail(true,roleAuction.getRoleId(),null,item,roleAuction.getPrice());
			for(UnionRoleAuction unionRoleAuction : priceList){
				if(roleAuction.getRoleId() == unionRoleAuction.getRoleId()){
					//购买者
					continue;
				}
				//给竞拍失败者发送邮件
				sendMail(false,unionRoleAuction.getRoleId(),buyRoleName,item,
						unionRoleAuction.getPrice());
			}
			//删除价格
			this.roleAuctionMap.remove(item.getGoodsInstanceId());
			GameContext.getBaseDAO().delete(UnionRoleAuction.class,UnionRoleAuction.UUID, item.getGoodsInstanceId());
		}
		//删除拍卖行记录
		Map<String,Auction> um = this.getAuctionMapForUnion(unionId);
		if(null != um){
			um.remove(auctionId);
		}
		GameContext.getBaseDAO().delete(UnionAuction.class,UnionAuction.ID, auctionId);
	}

	/**
	 * 公会拍卖行生成事例ID
	 * @return
	 */
	public String newAuctionInstanceId() {
		String id;
		try {
			id = IdFactory.getInstance().nextId(IdType.UNIONAUCTION);
		} catch (Exception e) {
			logger.error("The idfactory generate newAuctionInstanceId  exception", e);
			return null;
		}
		return id;
	}

	private void initCache() {
		auctionCache = new SimpleCache<String,String>();
		//默认超时时间为1天
		auctionCache.setTimeToLiveMillisecond(DateUtil.ONE_DAY_MILLIS);
		auctionCache.addCacheListener(new CacheListener<String,String>(){
			@Override
			public void entryAccessed(CacheEvent<String, String> arg0) {
			}

			@Override
			public void entryAdded(CacheEvent<String, String> arg0) {
			}

			@Override
			public void entryCleared(CacheEvent<String, String> arg0) {
			}

			@Override
			public void entryExpired(CacheEvent<String, String> arg0) {
			}

			@Override
			public void entryRemoved(CacheEvent<String, String> event) {
				//超时方法
				doCalculateListener(event.getKey(),event.getValue());
			}

			@Override
			public void entryUpdated(CacheEvent<String, String> arg0) {
			}
			
		});
		auctionCache.start();
	}

	private void doCalculateListener(String auctionId,String unionId){
		C0088_UnionAuctionCalculateMessage reqMsg = new C0088_UnionAuctionCalculateMessage();
		reqMsg.setAuctionId(auctionId);
		reqMsg.setUnionId(unionId);
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}
	
}

