package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class OpenBoxParam extends AbstractParam{

	private RoleGoods boxGoods;
	
	public OpenBoxParam(RoleInstance role) {
		super(role);
	}

	public RoleGoods getBoxGoods() {
		return boxGoods;
	}
	public void setBoxGoods(RoleGoods boxGoods) {
		this.boxGoods = boxGoods;
	}
}
