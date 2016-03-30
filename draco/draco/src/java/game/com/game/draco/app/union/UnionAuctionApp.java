package com.game.draco.app.union;

import java.util.List;
import java.util.Set;

import com.game.draco.app.union.domain.auction.Auction;
import com.game.draco.app.union.domain.auction.GoodsItem;
import com.game.draco.app.union.vo.RoleAuctionResult;
import com.game.draco.message.response.C2759_UnionAuctionListRespMessage;



public interface UnionAuctionApp{
	
	/**
	 * 初始化排行数据
	 */
	void initAuction();
	
	/**
	 * 添加公会拍卖物品
	 * @param activityId
	 * @param groupId
	 * @param itemList
	 */
	void addAuctionGoods(String unionId,Auction auction);
	
	/**
	 * 封装公会拍卖物品
	 * @param unionId
	 * @param activityId
	 * @param groupId
	 * @param itemList
	 * @param roleSet
	 * @return
	 */
	Auction packagingAuction(String unionId,byte activityId, byte groupId, List<GoodsItem> itemList,Set<Integer> roleSet);
	
	/**
	 * 获得拍卖行物品列表
	 */
	List<Auction> getUnionAuction(String unionId,int roleId);

	/**
	 * 押注
	 * @param unionId
	 * @param uuid
	 * @param roleId
	 * @param price
	 */
	RoleAuctionResult addRoleAuction(String unionId, String uuid, int roleId, int price);
	
	/**
	 * 获得角色拍卖行物品
	 */
	C2759_UnionAuctionListRespMessage sendC2759_UnionAuctionListRespMessage(String unionId,int roleId);
	
}