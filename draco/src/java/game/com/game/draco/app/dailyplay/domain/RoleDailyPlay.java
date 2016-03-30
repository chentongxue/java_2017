package com.game.draco.app.dailyplay.domain;

import java.util.Date;
import java.util.Map;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufKV;
import com.google.common.collect.Maps;

public @Data class RoleDailyPlay {

	public static final String ROLE_ID = "roleId" ;
	private String roleId ;
	private Date updateOn = new Date();
	private byte[] data = null ;
	
	@Protobuf(fieldType=FieldType.KV,order=1)
	@ProtobufKV(clazz=DailyItemData.class)
	private Map<String,DailyItemData> times = Maps.newHashMap();
	/**
	 * 非数据库字段
	 */
	private boolean inDb = false ;
	
	public void postFromDatabase(){
		try {
			if(null == this.data){
				return ;
			}
			Codec<RoleDailyPlay> codec = ProtobufProxy
					.create(RoleDailyPlay.class);
			RoleDailyPlay value = codec.decode(this.data);
			if (null == value || null == value.getTimes()) {
				return;
			}
			this.times.putAll(value.getTimes());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void preToDatabase(){
		try {
			Codec<RoleDailyPlay> codec = ProtobufProxy
					.create(RoleDailyPlay.class);
			this.data = codec.encode(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void clear(){
		this.times.clear();
	}
	
	public DailyItemData find(String key){
		return this.times.get(key) ;
	}
	
	public void put(DailyItemData data){
		this.times.put(data.getDailyId(), data) ;
	}
	
}
