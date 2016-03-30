package sacred.alliance.magic.app.carnival;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class CarnivalReward {
	private int rewardKey;
	private byte career;
	private int bindMoney;
	private int silverMoney;
	private int exp;
	private int goods1;
	private int num1;
	private byte bind1;
	private int goods2;
	private int num2;
	private byte bind2;
	private int goods3;
	private int num3;
	private byte bind3;
	private int goods4;
	private int num4;
	private byte bind4;
	private int goods5;
	private int num5;
	private byte bind5;
	private String mailTitle;
	private String mailContent;
	//쉽쟨膠틔
	private List<GoodsOperateBean> goodsList;
	
	
	
	private void init(int goodsId, int num, byte bind) {
		if (goodsId <= 0 || num <= 0) {
			return;
		}
		if (this.goodsList == null) {
			this.goodsList = new ArrayList<GoodsOperateBean>();
		}
		if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("carnival reward," + "rewardKey=" + this.rewardKey
					+  ",goodId=" + goodsId + " is not exsit!");
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, num, bind));
	}
	
	public void init(){
		this.init(goods1, num1, bind1);
		this.init(goods2, num2, bind2);
		this.init(goods3, num3, bind3);
		this.init(goods4, num4, bind4);
		this.init(goods5, num5, bind5);
	}
}
