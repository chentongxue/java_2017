package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.vo.RoleInstance;

public class MixParam extends AbstractParam{

	private int goodsId;
	private int mixNum;
	
	
	public MixParam(RoleInstance role) {
		super(role);
	}

	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getMixNum() {
		return mixNum;
	}
	public void setMixNum(int mixNum) {
		this.mixNum = mixNum;
	}
	
}
