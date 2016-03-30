package com.game.draco.app.union.domain.auction;

import java.util.List;
import java.util.Set;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

import lombok.Data;

public @Data class Auction{
	
	//活动ID
	@Protobuf(fieldType=FieldType.INT32,order=1)
	private byte activityId;
	
	//组ID（击杀BOSS所属分组）
	@Protobuf(fieldType=FieldType.INT32,order=2)
	private byte groupId;
	
	//击杀boss所有角色
	@Protobuf(fieldType=FieldType.INT32,order=3)
	private Set<Integer> roleSet;
	
	//物品
	@Protobuf(fieldType=FieldType.MESSAGE,order=4)
	private List<GoodsItem> goodsList;
	
	//结束时间
	@Protobuf(fieldType=FieldType.INT64,order=5)
	private long overTime;
	
	public boolean existRoleId(int roleId){
		return (null != this.roleSet) && this.roleSet.contains(roleId);
	}
	
	public GoodsItem findGoodsItem(String goodsInstanceId){
		if(null == goodsList || null == goodsInstanceId){
			return null ;
		}
		for(GoodsItem item : goodsList){
			if(item.getGoodsInstanceId().equals(goodsInstanceId)){
				return item ;
			}
		}
		return null ;
	}
}
