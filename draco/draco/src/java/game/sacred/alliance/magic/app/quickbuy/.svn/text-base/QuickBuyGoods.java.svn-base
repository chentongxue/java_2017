package sacred.alliance.magic.app.quickbuy;

import sacred.alliance.magic.vo.RoleInstance;

public class QuickBuyGoods {
	
	private int goodsId;//所需物品ID
	private int goodsNum;//所需物品总数量
	private int goldPrice;//元宝价格 <=0 表示不支持
	
	private boolean calcFlag = false;//计算标记，只有计算之后，才会有购买的数量和扣除的数量
	private int payGoodsNum;//需要购买的道具数量
	private int delRoleGoodsNum;//需要扣除的物品数量
	
	/**
	 * 计算所要购买道具数量
	 * @param role
	 * @return true=道具数足够, false=需要购买道具
	 */
	public boolean isRoleGoodsEnough(RoleInstance role){
		//标记为已经计算过
		this.calcFlag = true;
		int roleGoodsNum = role.getRoleBackpack().countByGoodsId(this.goodsId);
		if(roleGoodsNum >= this.goodsNum){
			this.delRoleGoodsNum = this.goodsNum;
			this.payGoodsNum = 0;
			return true;
		}
		//需要购买的道具数量 = 所需道具总数 - 拥有的道具数
		this.delRoleGoodsNum = roleGoodsNum;
		this.payGoodsNum = this.goodsNum - roleGoodsNum;
		return false;
	}
	
	/**
	 * 需要支付的元宝数
	 * @param role
	 * @return
	 */
	public int getPayGoldMoney(RoleInstance role){
		//如果没有计算过，则先进行计算
		if(!this.calcFlag){
			this.isRoleGoodsEnough(role);
		}
		return this.payGoodsNum * this.goldPrice;
	}
	
	/**
	 * 需要购买的道具数量
	 * @param role
	 * @return
	 */
	public int getPayGoodsNum(RoleInstance role) {
		//如果没有计算过，则先进行计算
		if(!this.calcFlag){
			this.isRoleGoodsEnough(role);
		}
		return payGoodsNum;
	}

	/**
	 * 需要扣除的物品数量
	 * @param role
	 * @return
	 */
	public int getDelRoleGoodsNum(RoleInstance role) {
		//如果没有计算过，则先进行计算
		if(!this.calcFlag){
			this.isRoleGoodsEnough(role);
		}
		return delRoleGoodsNum;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}

	public int getGoldPrice() {
		return goldPrice;
	}

	public void setGoldPrice(int goldPrice) {
		this.goldPrice = goldPrice;
	}
	
}
