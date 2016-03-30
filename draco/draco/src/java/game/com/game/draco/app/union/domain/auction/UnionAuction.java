package com.game.draco.app.union.domain.auction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Set;

import lombok.Data;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Sets;

public @Data class UnionAuction implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

	//公会ID
	private String unionId;
	
	//数据
	private byte [] data;
	
	/**
	 * 读取某个公会拍卖行数据
	 * @param data
	 */
	public List<Auction> parseAuctionData() {
		List<Auction> auctionList = Lists.newArrayList();
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int list = in.readInt();
			for(int i=0;i<list;i++){
				Auction auction = new Auction();
				auction.setActivityId(in.readByte());
				auction.setGroupId(in.readByte());
				auction.setOverTime(in.readLong());
				
				int goodsSize = in.readInt();
				List<GoodsItem> itemList = Lists.newArrayList();
				for(int g=0;g<goodsSize;g++){
					GoodsItem item = new GoodsItem();
					item.setGoodsId(in.readInt());
					item.setGoodsType(in.readByte());
					item.setGoodsBinded(in.readByte());
					item.setGoodsNum(in.readByte());
					item.setUuid(in.readUTF());
					itemList.add(item);
				}
				auction.setGoodsList(itemList);
				
				int roleSize = in.readInt();
				Set<Integer> roleSet = Sets.newHashSet();
				for(int r=0;r<roleSize;r++){
					roleSet.add(in.readInt());
				}
				auction.setRoleSet(roleSet);
				
				auctionList.add(auction);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return auctionList;
	}

	/**
	 * 存储公会拍卖数据
	 * @param roleDpsMap
	 * @return
	 */
	public void buildAuctionData(List<Auction> auctionList) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			
			out.writeInt(auctionList.size());
			for(Auction auction : auctionList){
				out.writeByte(auction.getActivityId());
				out.writeByte(auction.getGroupId());
				out.writeLong(auction.getOverTime());

				List<GoodsItem> itemList = auction.getGoodsList();
				out.writeInt(itemList.size());
				for(GoodsItem item : itemList){
					out.writeInt(item.getGoodsId());
					out.writeByte(item.getGoodsType());
					out.writeByte(item.getGoodsBinded());
					out.writeByte(item.getGoodsNum());
					out.writeUTF(item.getUuid());
				}

				Set<Integer> roleSet = auction.getRoleSet();
				out.writeInt(roleSet.size());
				for(Integer roleId : roleSet){
					out.writeInt(roleId);
				}
			}
			
			out.flush();
			out.close();
			data = bout.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
