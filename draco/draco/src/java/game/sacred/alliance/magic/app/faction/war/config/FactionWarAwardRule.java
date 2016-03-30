package sacred.alliance.magic.app.faction.war.config;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class FactionWarAwardRule implements KeySupport<Integer>{
	private int ruleId;
	private int silverMoney;
	private int bindingGoldMoney;
	private int contribute;
	private int zp;
	private int goodsId1;
	private int goodsNum1;
	private byte goodsBind1;
	private int goodsId2;
	private int goodsNum2;
	private byte goodsBind2;
	private int goodsId3;
	private int goodsNum3;
	private byte goodsBind3;
	private String mailTitle;
	private String mailContent;
	private List<GoodsOperateBean> goodsList;
	
	public void init(){
		this.initGoodsBeanList(goodsId1, goodsNum1, goodsBind1);
		this.initGoodsBeanList(goodsId2, goodsNum2, goodsBind2);
		this.initGoodsBeanList(goodsId3, goodsNum3, goodsBind3);
	}
	
	private void initGoodsBeanList(int goodsId, int num, byte bind) {
		if (goodsId <= 0 || num <= 0) {
			return;
		}
		if (goodsList == null) {
			goodsList = new ArrayList<GoodsOperateBean>();
		}
		if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("LadderConfig error:goodId=" + goodsId + " is not exsit!");
		}
		goodsList.add(new GoodsOperateBean(goodsId, num, bind));
	}

	@Override
	public Integer getKey() {
		return this.ruleId;
	}
}
