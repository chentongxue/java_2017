package com.game.draco.app.union.domain.instance;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.Util;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufKV;
import com.google.common.collect.Maps;

public @Data class UnionRoleDpsRecord implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//公会ID
	private String unionId;
	
	//活动ID
	private byte activityId;
	
	//组Id
	private byte groupId;
	
	//角色数据
	private byte [] data;
	
	@Protobuf(fieldType=FieldType.KV,order=1)
	@ProtobufKV(clazz=RoleDps.class)
	private Map<Integer,RoleDps> roleDpsMap = Maps.newConcurrentMap();
	
	//最后操作时间
	private long lastTime;
	
	/**
	 * 读取某个BOSS中，所有角色DPS数据
	 */
	public void parseRoleDpsData(){
		try {
			if(null == this.data){
				return ;
			}
			UnionRoleDpsRecord value = Util.decode(this.data,UnionRoleDpsRecord.class);
			if (null == value || null == value.getRoleDpsMap()) {
				return;
			}
			this.roleDpsMap.putAll(value.getRoleDpsMap());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 存储某个BOSS中，所有角色DPS数据
	 * @return
	 */
	public void buildRoleDpsData(){
		this.data = Util.encode(this);
	}
	
	
	public void clear(){
		this.roleDpsMap.clear();
	}
	
	public RoleDps find(String key){
		return this.roleDpsMap.get(key) ;
	}
	
	public void put(RoleDps data){
		this.roleDpsMap.put(data.getRoleId(), data) ;
	}
	
}
