package com.game.draco.app.union;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.app.union.domain.auction.RoleAuction;
import com.game.draco.app.union.domain.auction.UnionAuction;
import com.game.draco.app.union.domain.auction.UnionRoleAuction;
import com.game.draco.app.union.vo.RoleAuctionResult;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.UnionAuctionItem;
import com.game.draco.message.response.C2759_UnionAuctionListRespMessage;

public class UnionAuctionAppImpl implements UnionAuctionApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//公会拍卖行 <公会ID,拍卖物品>
	Map<String,List<Auction>> unionAuctionMap = Maps.newConcurrentMap();
	
	//角色押注数据 <公会ID，Map<UUID，角色拍卖数据>>
	Map<String,Map<String,Map<Integer,RoleAuction>>> roleAuctionMap = Maps.newConcurrentMap();
	
	@Override
	public void initAuction() {
		try{
			List<UnionAuction> unionAuctionList = GameContext.getBaseDAO().selectAll(UnionAuction.class);
			if(unionAuctionList != null && !unionAuctionList.isEmpty()){
				for(UnionAuction unionAuction : unionAuctionList){
					List<Auction> auctionList = unionAuction.parseAuctionData();
					unionAuctionMap.put(unionAuction.getUnionId(), auctionList);
				}
			}
			
			//读取全部拍卖行数据
			List<UnionRoleAuction> unionRoleAuctionList = GameContext.getBaseDAO().selectAll(UnionRoleAuction.class);
				
			for(UnionRoleAuction unionRoleAuction : unionRoleAuctionList){
				//解析角色拍卖数据
				Map<Integer,RoleAuction> rAuctionMap = unionRoleAuction.parseRoleAuctionData();
				
				Map<String,Map<Integer,RoleAuction>> rmap = Maps.newConcurrentMap();
	
				for(Entry<Integer,RoleAuction> roleAuction : rAuctionMap.entrySet()){
					Map<Integer,RoleAuction> raMap = null;
					if(roleAuctionMap.containsKey(roleAuction.getValue().getUnionId())){
						if(rmap.containsKey(roleAuction.getValue().getUuid())){
							raMap = rmap.get(roleAuction.getValue().getUuid());
							raMap.put(roleAuction.getKey(),roleAuction.getValue());
						}else{
							raMap = Maps.newHashMap();
							raMap.put(roleAuction.getKey(), roleAuction.getValue());
							rmap.put(roleAuction.getValue().getUuid(), raMap);
						}
					}else{
						raMap = Maps.newHashMap();
						raMap.put(roleAuction.getKey(), roleAuction.getValue());
						rmap.put(roleAuction.getValue().getUnionId(), raMap);
						roleAuctionMap.put(roleAuction.getValue().getUnionId(), rmap);
					}
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
			List<Auction> list = null;
			if(unionAuctionMap.containsKey(unionId)){
				list = unionAuctionMap.get(unionId);
			}else{
				list = Lists.newArrayList();
			}
			list.add(auction);
			unionAuctionMap.put(unionId,list);
			UnionAuction uAuction = new UnionAuction();
			uAuction.setUnionId(unionId);
			uAuction.buildAuctionData(list);
			GameContext.getBaseDAO().saveOrUpdate(uAuction);
		}catch(Exception e){
			logger.error("addAuctionGoods",e);
		}
	}
	
	
	/**
	 * 封装拍卖数据
	 */
	@Override
	public Auction packagingAuction(String unionId, byte activityId,byte groupId, List<GoodsItem> itemList,Set<Integer> roleSet) {
		Auction auction = new Auction();
		auction.setActivityId(activityId);
		auction.setGroupId(groupId);
		auction.setGoodsList(itemList);
		auction.setRoleSet(roleSet);
		auction.setOverTime(System.currentTimeMillis());
		return auction;
	}

	@Override
	public List<Auction> getUnionAuction(String unionId,int roleId) {
		List<Auction> showList = Lists.newArrayList();
		try{
			//过滤过期的
			validAuction(unionId,roleId);
			if(unionAuctionMap.containsKey(unionId)){
				List<Auction> list = unionAuctionMap.get(unionId);
				for(Auction auction : list){
					//过滤角色打过的BOSS
					if(auction.getRoleSet().contains(roleId)){
						showList.add(auction);
					}
				}
				return showList; 
			}
		}catch(Exception e){
			logger.error("getUnionAuction",e);
		}
		return showList;
	}
	
	/**
	 * 时间检查
	 */
	private void validAuction(String unionId,int roleId){
		try{
			if(unionAuctionMap.containsKey(unionId)){
				List<Auction> auctionList = unionAuctionMap.get(unionId);
				if(auctionList == null || auctionList.isEmpty()){
					return;
				}
				Iterator<Auction> iter = auctionList.iterator();
				while(iter.hasNext()){
					Auction auction = (Auction)iter.next();
					if(auction.getOverTime() <= System.currentTimeMillis() - DateUtil.ONE_DAY_MILLIS){
						restitutionAuctionDkp(unionId,roleId);
						iter.remove();
					}
				}
			}
		}catch(Exception e){
			logger.error("validAuction",e);
		}
	}
	
	/**
	 * 返还押注DKP
	 */
	private void restitutionAuctionDkp(String unionId,int roleId){
		try{
			UnionMember member = GameContext.getUnionApp().getUnionMember(unionId, roleId);
			if(roleAuctionMap.containsKey(unionId)){
				Map<String,Map<Integer,RoleAuction>> auctionMap = roleAuctionMap.get(unionId);
				if(auctionMap == null || auctionMap.isEmpty()){
					return;
				}
				for(Entry<String,Map<Integer,RoleAuction>> roleAuction : auctionMap.entrySet()){
					if(roleAuction.getValue() == null){
						continue;
					}
					for(Entry<Integer,RoleAuction> auction : roleAuction.getValue().entrySet()){
						if(auction.getValue().getRoleId() == roleId){
							member.setDkp(member.getDkp() + auction.getValue().getPrice());
						}
					}
				}
				GameContext.getUnionApp().saveOrUpdUnionMember(member);
			}
		}catch(Exception e){
			logger.error("restitutionAuctionDkp",e);
		}
	}
	
	/**
	 * 押注
	 * @param unionId
	 * @param uuid
	 * @param roleId
	 * @param price
	 */
	@Override
	public RoleAuctionResult addRoleAuction(String unionId,String uuid,int roleId,int price){
	
		RoleAuctionResult result = curDuctAuctionDkp(unionId,roleId,price);
		try{
			if(result.isSuccess()){
				RoleAuction roleAuction = null;
				Map<Integer,RoleAuction> rAMap = null;
				Map<String,Map<Integer,RoleAuction>> rAuctionMap = null;
				boolean isInit = true;
				
				if(roleAuctionMap.containsKey(unionId)){
					rAuctionMap = roleAuctionMap.get(unionId);
					if(rAuctionMap.containsKey(uuid)){
						rAMap = rAuctionMap.get(uuid);
						for(Entry<Integer,RoleAuction> auction : rAMap.entrySet()){
							if(auction.getKey() == roleId){
								roleAuction = auction.getValue();
								auction.getValue().setPrice(roleAuction.getPrice() + price);
								isInit = false;
								break;
							}
						}
					}
				}
				
				//是否初始化
				if(isInit){
					roleAuction = new RoleAuction();
					roleAuction.setPrice(price);
					roleAuction.setRoleId(roleId);
					roleAuction.setUuid(uuid);
					roleAuction.setUnionId(unionId);
					Map<Integer,RoleAuction> map = Maps.newHashMap();
					map.put(roleId,roleAuction);
					rAuctionMap.put(uuid, map);
					roleAuctionMap.put(unionId,rAuctionMap);
				}
				
				if(roleAuction != null){
					result.setBidPrice(roleAuction.getPrice());
					GameContext.getBaseDAO().saveOrUpdate(roleAuction);
				}
			}
		}catch(Exception e){
			logger.error("addRoleAuction",e);
		}
		return result;
	}
	
	/**
	 * 扣除DKP
	 */
	private RoleAuctionResult curDuctAuctionDkp(String unionId,int roleId,int dkp){
		RoleAuctionResult result = new RoleAuctionResult();
		try{
			UnionMember member = GameContext.getUnionApp().getUnionMember(unionId, roleId);
			if(member != null){
				if(member.getDkp() < dkp){
					result.setInfo(GameContext.getI18n().getText(TextId.UNION_AUCTION_DKP_ERR));
					return result;
				}           
				member.setDkp(member.getDkp() - dkp);
				GameContext.getUnionApp().saveOrUpdUnionMember(member);
				result.success();
			}
		}catch(Exception e){
			logger.error("curDuctAuctionDkp",e);
		}
		return result;
	}

	@Override
	public C2759_UnionAuctionListRespMessage sendC2759_UnionAuctionListRespMessage(
			String unionId, int roleId) {
		
		C2759_UnionAuctionListRespMessage respMsg = new C2759_UnionAuctionListRespMessage();
		try{
			List<UnionAuctionItem> auctionItemList = Lists.newArrayList();
			
			List<Auction> auctionList = getUnionAuction(unionId, roleId);
			
			if(auctionList != null && !auctionList.isEmpty()){
				for(Auction auction : auctionList){
					if(auction == null){
						continue;
					}
					List<GoodsItem> goodsList = auction.getGoodsList();
					if(goodsList == null || goodsList.isEmpty()){
						continue;
					}
					for(GoodsItem item : goodsList){
						
						UnionAuctionItem auctionItem = new UnionAuctionItem();
						auctionItem.setUuid(item.getUuid());
						auctionItem.setEndTime(DateUtil.date2FormatDate(auction.getOverTime(), DateUtil.format3));
						int basePrice = GameContext.getUnionDataApp().getGoodsBasePrice(item.getGoodsId());
						auctionItem.setPrice(basePrice);
						
						GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(item.getGoodsId());
						
						GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
						goodsItem.setBindType(item.getGoodsBinded());
						goodsItem.setGoodsId(item.getGoodsId());
						goodsItem.setGoodsImageId(goodsBase.getImageId());
						goodsItem.setGoodsName(goodsBase.getName());
						goodsItem.setGoodsLevel((byte)goodsBase.getLevel());
						goodsItem.setNum(item.getGoodsNum());
						
						auctionItem.setGoodsItem(goodsItem);
						auctionItem.setBid(-1);
						
						if(roleAuctionMap.containsKey(unionId)){
							Map<String,Map<Integer,RoleAuction>> auctionMap = roleAuctionMap.get(unionId);
							if(auctionMap == null || auctionMap.isEmpty()){
								continue;
							}
							for(Entry<String,Map<Integer,RoleAuction>> roleAuction : auctionMap.entrySet()){
								if(roleAuction.getValue() == null){
									continue;
								}
								if(roleAuction.getValue().containsKey(roleId)){
									RoleAuction rAuction = roleAuction.getValue().get(roleId);
									if(rAuction.getUuid().equals(item.getUuid())){
										auctionItem.setBid(rAuction.getPrice());
									}
								}
							}
						}
						auctionItemList.add(auctionItem);
					}
				}
			}
			respMsg.setItem(auctionItemList);
		}catch(Exception e){
			logger.error("sendC2759_UnionAuctionListRespMessage",e);
		}
		return respMsg;
	}
	
}

