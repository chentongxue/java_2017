package sacred.alliance.magic.app.goods.behavior.param;

import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;


/**
 * 丢弃物品参数对象
 * @author Wang.K
 *
 */
public class DiscardGoodsParam extends AbstractParam{
	private RoleGoods roleGoods;
	
	public DiscardGoodsParam(RoleInstance role) {
		super(role);
	}

	public RoleGoods getRoleGoods() {
		return roleGoods;
	}

	public void setRoleGoods(RoleGoods roleGoods) {
		this.roleGoods = roleGoods;
	}
	
}