package com.game.draco.app.accumulatelogin.config;

import com.game.draco.GameContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import java.util.List;
import java.util.Map;
@Data
public class AccumulateLoginAwardConfig implements KeySupport<String> {
	private byte cycle;
	private byte day;

	private int awardId;
	private short awardNum;
	private byte awardBind;

	private int awardId2;
	private short awardNum2;
	private byte awardBind2;

	private int awardId3;
	private short awardNum3;
	private byte awardBind3;

	private int bindDiamond;
	private int silverMoney;
	private int potential ;
	private byte vipLevel;
	private byte times;

	private byte imageType = 0;
	private int imgeId;
	private byte ratio = 10;//缩放比例 1为10%, 10为100%
	private String awardInfo;

	private List<GoodsOperateBean> goodsList = Lists.newArrayList();
	private Map<AttributeType,Integer> attriMap = Maps.newHashMap();

	public void init() {
		this.goodsList.clear();
		this.attriMap.clear();
		
		init(awardId, awardNum, awardBind);
		init(awardId2, awardNum2, awardBind2);
		init(awardId3, awardNum3, awardBind3);
		
		this.initAttri(AttributeType.goldMoney,bindDiamond);
		this.initAttri(AttributeType.gameMoney,silverMoney);
		this.initAttri(AttributeType.potential,potential);
		
		/*if ((this.goodsList.size() + this.attriMap.size()) > 3) {
			Log4jManager.checkFail();
			Log4jManager.CHECK
					.error("accumulate rewardConfig init failed, the awards limit is 3 "
							+ "day="
							+ day
							+ ",bindDiamond="
							+ bindDiamond
							+ ",silverMoney="
							+ silverMoney
							+ ",potential="
							+ potential 
							+ ",goodsList.size()=" + goodsList.size());
		}*/
		// 判断是否完整goodsList.size() bindDiamond silverMoney
	}

	private void initAttri(AttributeType at,int value){
		if(null == at || value <=0){
			return ;
		}
		this.attriMap.put(at, value);
	}
	
	private void init(int goodsId, int num, byte bind) {
		if (goodsId <= 0 || num <= 0) {
			return;
		}
		if (GameContext.getGoodsApp().getGoodsBase(goodsId) == null) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("accumulate rewardConfig init failed," + "day="
					+ day + ",goodId=" + goodsId + " is not exsit!");
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, num, bind));
	}

	@Override
	public String getKey() {
		return cycle+"_"+day;
	}

}
