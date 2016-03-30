package com.game.draco.app.vip.type;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C1401_ExchangeListReqMessage;
import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;

public enum VipFunctionType {
	NONE_FUNCTION((byte)0, ""),
	SHOP_SECRET((byte)1, "神秘商店"),
	EXCHANGE((byte)2,"兑换")
	;
	private final byte type;
	private final String name;
	
	private VipFunctionType(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	public static VipFunctionType getVipFunctionType(byte type) {
		for(VipFunctionType priv : VipFunctionType.values()){
			if(priv.getType() == type){
				return priv;
			}
		}
		return null ;
	}
	/**
	 * 
	 * @param param 对于神秘商店：填写神秘商店的shopId 对于兑换：填写兑换的菜单ID
	 * @date 2014-9-2 下午08:23:42
	 */
	public Result enterFunction(RoleInstance role, String param){
		Result result = new Result().setResult(Result.FAIL);
		switch(this){
		case SHOP_SECRET:
			C1618_ShopSecretOpenPanelReqMessage msg = new C1618_ShopSecretOpenPanelReqMessage();
			msg.setShopId(param);
			role.getBehavior().addCumulateEvent(msg);
			return result.setResult(Result.SUCCESS);
		case EXCHANGE :
			C1401_ExchangeListReqMessage exchangeMsg = new C1401_ExchangeListReqMessage();
			exchangeMsg.setParam(param);
			role.getBehavior().addCumulateEvent(exchangeMsg);
			return result.setResult(Result.SUCCESS);
		default:
			return result.setResult(Result.FAIL);
		}
	} 
}
