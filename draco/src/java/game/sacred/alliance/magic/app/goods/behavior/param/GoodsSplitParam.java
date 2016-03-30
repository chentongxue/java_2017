package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsSplitParam extends AbstractParam{

	private RoleGoods roleGoods;
	private int splitNum;
	
	public GoodsSplitParam(RoleInstance role) {
		super(role);
	}

	public RoleGoods getRoleGoods() {
		return roleGoods;
	}

	public void setRoleGoods(RoleGoods roleGoods) {
		this.roleGoods = roleGoods;
	}

	public int getSplitNum() {
		return splitNum;
	}

	public void setSplitNum(int splitNum) {
		this.splitNum = splitNum;
	}
}
