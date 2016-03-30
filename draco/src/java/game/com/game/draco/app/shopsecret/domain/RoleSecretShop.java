package com.game.draco.app.shopsecret.domain;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.KeySupport;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public @Data
class RoleSecretShop implements KeySupport<String> {

	private static Logger logger = LoggerFactory
			.getLogger(RoleSecretShop.class);
	//
	public static final String ROLE_ID = "roleId";
	public static final String SHOP_ID = "shopId";

	// db
	private String roleId;
	private String shopId;
	private Date refreshTime;
	private int currentDayRefreshTimes;
	private byte[] data;
	
	@Protobuf(fieldType = FieldType.MESSAGE, order = 1)
	private List<ShopSecretGoodsRecord> goodsRecordList;

	
	public void preToDatabase() {
		this.data = buildData();
	}

	public RoleSecretShop postFromDatabase() {
		try {
			goodsRecordList = parseData(this.data);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("RoleSecretShop.initDB() error: ", e);
		}
		return this;
	}

	public ShopSecretGoodsRecord getShopSecretGoodsRecord(int shopItemId) {
		for (ShopSecretGoodsRecord record : goodsRecordList) {
			if (record.getShopItemId() == shopItemId) {
				return record;
			}
		}
		return null;
	}

	private List<ShopSecretGoodsRecord> parseData(byte[] targetData)
			throws IOException {
		Codec<RoleSecretShop> codec = ProtobufProxy.create(RoleSecretShop.class);
		RoleSecretShop shop = codec.decode(targetData) ;
		return shop.getGoodsRecordList();
	}

	private byte[] buildData() {
		try {
			Codec<RoleSecretShop> codec = ProtobufProxy.create(RoleSecretShop.class);
			return codec.encode(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public String getKey() {
		return String.valueOf(shopId);
	}

}
