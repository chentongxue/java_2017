package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.constant.Status;

public class DefaultBehavior extends AbstractGoodsBehavior{

	@Override
	public GoodsResult operate(AbstractParam param) {
		GoodsResult result = new GoodsResult();
		return result.setInfo(Status.GOODS_NO_SUPPORT.getTips());
	}
	
}
