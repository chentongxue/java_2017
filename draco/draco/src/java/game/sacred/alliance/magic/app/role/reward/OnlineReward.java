package sacred.alliance.magic.app.role.reward;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;

public @Data class OnlineReward {
	
	private int index;//奖励次数
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	private int time;//倒计时间(单位秒)
	private int bindMoney;//绑金
	private int silverMoney;//银币
	private int exp;//经验
	private int goodsId1;//奖励1ID
	private int goodsNum1;//奖励1数量	
	private int bind1;//奖励1绑定类型
	private int goodsId2;
	private int goodsNum2;
	private int bind2;
	private int goodsId3;
	private int goodsNum3;
	private int bind3;
	//奖励物品列表
	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	public void init(){
		this.addMustGoods(this.goodsId1, this.goodsNum1, this.bind1);
		this.addMustGoods(this.goodsId2, this.goodsNum2, this.bind2);
		this.addMustGoods(this.goodsId3, this.goodsNum3, this.bind3);
	}
	
	/** 添加奖励物品 */
	private void addMustGoods(int goodsId, int goodsNum, int bind){
		if(goodsId <= 0 || goodsNum <= 0){
			return;
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, goodsNum, bind));
	}
	
}
