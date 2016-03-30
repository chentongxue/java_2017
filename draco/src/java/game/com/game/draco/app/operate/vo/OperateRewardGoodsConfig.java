package com.game.draco.app.operate.vo;

import lombok.Data;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.Result;

import com.game.draco.GameContext;

public @Data class OperateRewardGoodsConfig {

	private int goodsId;
	private int goodsNum;
	private byte bindType;
	
	public Result init(String info) {
		Result result = new Result();
		boolean flag = false;// 错误标志
		if (null == GameContext.getGoodsApp().getGoodsBase(this.goodsId)) {
			flag = true;
			result.setInfo(info + " : goodsId " + this.goodsId + " not exist!\n");
		}
		if (goodsNum < 0) {
			flag = true;
			result.setInfo(info + " : goodsId " + this.goodsId + " goodsNum config error!\n");
		}
		BindingType bindingType = BindingType.get(bindType);
		if (null == bindingType) {
			flag = true;
			result.setInfo(info + " : goodsId " + this.goodsId + " bindType config error!\n");
		}
		if (flag) {
			return result;
		}
		return result.success();
	}
	
}
