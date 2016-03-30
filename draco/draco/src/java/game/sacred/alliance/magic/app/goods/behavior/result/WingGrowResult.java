package sacred.alliance.magic.app.goods.behavior.result;

import lombok.Data;
import sacred.alliance.magic.app.goods.wing.WingGrid;
import sacred.alliance.magic.app.goods.wing.WingGridConfig;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.RoleGoods;

public @Data class WingGrowResult extends Result{

	private RoleGoods roleGoods;
	private WingGridConfig wingGridConfig;
	private WingGrid wingGrid;
}
