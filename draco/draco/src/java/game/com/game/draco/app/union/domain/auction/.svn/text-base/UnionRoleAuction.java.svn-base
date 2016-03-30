package com.game.draco.app.union.domain.auction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.python.google.common.collect.Maps;

public @Data class UnionRoleAuction implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//物品标识
	private String uuid;
	
	//角色数据
	private byte [] data;
	
	/**
	 * 读取某个公会角色拍卖数据
	 * @param data
	 */
	public Map<Integer,RoleAuction> parseRoleAuctionData() {
		Map<Integer,RoleAuction> auctionMap = Maps.newHashMap();
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			int list = in.readInt();
			for(int i=0;i<list;i++){
				RoleAuction roleAuction = new RoleAuction();
				roleAuction.setPrice(in.readInt());
				roleAuction.setRoleId(in.readInt());
				roleAuction.setUnionId(in.readUTF());
				auctionMap.put(roleAuction.getRoleId(),roleAuction);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return auctionMap;
	}

	/**
	 * 存储某个角色拍卖数据
	 * @param roleDpsMap
	 * @return
	 */
	public void buildAuctionData(Map<Integer,RoleAuction> roleAuctionMap) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			
			out.writeInt(roleAuctionMap.size());
			for(Entry<Integer,RoleAuction> roleAuction : roleAuctionMap.entrySet()){
				out.writeInt(roleAuction.getValue().getPrice());
				out.writeInt(roleAuction.getKey());
				out.writeUTF(roleAuction.getValue().getUnionId());
			}
			
			out.flush();
			out.close();
			data = bout.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
