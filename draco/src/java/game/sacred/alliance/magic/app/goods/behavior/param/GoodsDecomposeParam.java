package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsDecomposeParam  extends AbstractParam{

	private RoleGoods roleGoods;
	private GoodsBase goodsBase ;
	private short num;//要分解的物品数量
	public GoodsDecomposeParam(RoleInstance role) {
		super(role);
	}
	public RoleGoods getRolegoods() {
		return roleGoods;
	}
	public void setRoleGoods(RoleGoods rolegoods) {
		this.roleGoods = rolegoods;
	}
	public GoodsBase getGoodsBase() {
		return goodsBase;
	}
	public void setGoodsBase(GoodsBase goodsBase) {
		this.goodsBase = goodsBase;
	}
	public RoleGoods getRoleGoods() {
		return roleGoods;
	}
	public short getNum() {
		return num;
	}
	public void setNum(short num) {
		this.num = num;
	}
	
}
