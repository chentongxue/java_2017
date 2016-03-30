package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.GoodsSplitParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsSplit extends AbstractGoodsBehavior{

	public GoodsSplit(){
		this.behaviorType = GoodsBehaviorType.split;
	}
	
	
	@Override
	public GoodsResult operate(AbstractParam param) {
		GoodsSplitParam splitParam = (GoodsSplitParam)param;
		RoleInstance role = splitParam.getRole();
		RoleGoods roleGoods = splitParam.getRoleGoods();
		int splitNum = splitParam.getSplitNum();
		return role.getRoleBackpack().split(roleGoods, splitNum);
	}

}
