package com.game.draco.app.shop.domain;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufKV;

public @Data
class RoleShopDailyLimit implements KeySupport<String> {

	private static Logger logger = LoggerFactory
			.getLogger(RoleShopDailyLimit.class);
	public static final String ROLE_ID = "roleId";

	// db
	private String roleId;
	private Date refreshTime;
	private byte[] data; // 存储数据
	// init
	@Protobuf(fieldType=FieldType.KV,order=1)
	@ProtobufKV(clazz=ShopGoodsRecord.class)
	private Map<Integer, ShopGoodsRecord> recordMap = null ;


	//是否修改
	private boolean modify = false ;
	//是否已经在database
	private boolean inDatabase = false ;

	public void resetDay(){
		Date now = new Date() ;
		if(DateUtil.sameDay(now,refreshTime)){
			return ;
		}
		if(null != this.recordMap){
			this.recordMap.clear();
		}
		this.data = null ;
		this.refreshTime = now ;
		this.modify = true ;
	}

	public boolean isEmpty(){
		return null == this.data ;
	}


	public void preToDatabase() {
		if(Util.isEmpty(this.recordMap)){
			this.data = null ;
			return ;
		}
		this.data = Util.encode(this) ;
	}

	public void postFromDatabase() {
		if(null == this.data){
			return ;
		}
		RoleShopDailyLimit entity = Util.decode(this.data,RoleShopDailyLimit.class);
		if(null == entity || Util.isEmpty(entity.getRecordMap())){
			return ;
		}
		this.recordMap = Maps.newHashMap() ;
		this.recordMap.putAll(entity.getRecordMap());
	}


	@Override
	public String getKey() {
		return roleId;
	}

	public ShopGoodsRecord getRecord(int key) {
		if(null == recordMap){
			return null ;
		}
		return recordMap.get(key);
	}

	private void putRecord(ShopGoodsRecord record) {
		if(null == record){
			return ;
		}
		if(null == recordMap){
			recordMap = Maps.newHashMap();
		}
		recordMap.put(record.getId(), record);
	}

	/**
	 * 
	 * @param id
	 * @param num
	 *            本次购买的数量
	 * @date 2014-9-26 下午09:05:13
	 */
	public void putRecord(int id, short num) {
		ShopGoodsRecord record = getRecord(id);
		if (record == null) {
			record = new ShopGoodsRecord();
			record.setBuyNum(num);
			record.setId(id);
		} else {
			record.addBuyNum(num);
		}
		putRecord(record);
		this.modify = true ;
	}
}
