package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 时装续费
 * @author Wang.K
 *
 */
public class FashionRenewParam extends AbstractParam{

	private RoleGoods fashionGoods;
	
	public FashionRenewParam(RoleInstance role) {
		super(role);
	}

	public RoleGoods getFashionGoods() {
		return fashionGoods;
	}
	public void setFashionGoods(RoleGoods fashionGoods) {
		this.fashionGoods = fashionGoods;
	}
}
