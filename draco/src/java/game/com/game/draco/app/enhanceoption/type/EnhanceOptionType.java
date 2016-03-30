package com.game.draco.app.enhanceoption.type;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;

public enum EnhanceOptionType {
	NONE_OPTION((byte)0),//无
	DEATH_OPTION((byte)1),//神秘商店
	LEVEL_OPTION((byte)2)//兑换
	;
	private final byte type;
	
	private EnhanceOptionType(byte type){
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public static EnhanceOptionType getVipFunctionType(byte type) {
		for(EnhanceOptionType priv : EnhanceOptionType.values()){
			if(priv.getType() == type){
				return priv;
			}
		}
		return null ;
	}
	/**
	 * 
	 */
	public Result enterFunction(RoleInstance role, String param){
		Result result = new Result().setResult(Result.FAIL);
		switch(this){
		case DEATH_OPTION:
			C1618_ShopSecretOpenPanelReqMessage msg = new C1618_ShopSecretOpenPanelReqMessage();
			msg.setShopId(param);
			role.getBehavior().addCumulateEvent(msg);
			return result.setResult(Result.SUCCESS);
		default:
			return result.setResult(Result.FAIL);
		}
	} 
}
