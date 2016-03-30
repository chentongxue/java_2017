package com.game.draco.app.operate.firstpay;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AttributeOperateBean;

import com.game.draco.app.operate.firstpay.config.FirstPayBaseConfig;
import com.game.draco.app.operate.vo.OperateRewardAttributeConfig;
import com.game.draco.app.operate.vo.OperateRewardGoodsConfig;
import com.google.common.collect.Lists;

public @Data class FirstPayResult extends Result {
	
	private FirstPayBaseConfig firstPayBaseConfig;
	private List<OperateRewardGoodsConfig> firstPayGoodsList;
	private List<OperateRewardAttributeConfig> firstPayAttributeList;
	
	/**
	 * 获取奖励物品
	 * @return
	 */
	public List<GoodsOperateBean> getGoodsOperateBeanList() {
		if (Util.isEmpty(this.firstPayGoodsList)) {
			return null;
		}
		List<GoodsOperateBean> goodsList = Lists.newArrayList();
		for (OperateRewardGoodsConfig config : this.firstPayGoodsList) {
			if (null == config) {
				continue;
			}
			goodsList.add(new GoodsOperateBean(config.getGoodsId(), config.getGoodsNum(), config.getBindType()));
		}
		return goodsList;
	}
	
	/**
	 * 获取奖励属性
	 * @return
	 */
	public List<AttributeOperateBean> getAttributeOperateBeanList() {
		if (Util.isEmpty(this.firstPayAttributeList)) {
			return null;
		}
		List<AttributeOperateBean> attriList = Lists.newArrayList();
		for (OperateRewardAttributeConfig config : this.firstPayAttributeList) {
			if (null == config) {
				continue;
			}
			attriList.add(new AttributeOperateBean(config.getAttriType(), config.getValue()));
		}
		return attriList;
	}
	
}
