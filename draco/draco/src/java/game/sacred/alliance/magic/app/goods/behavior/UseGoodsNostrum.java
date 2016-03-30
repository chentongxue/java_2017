package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class UseGoodsNostrum extends AbstractGoodsBehavior{

	public UseGoodsNostrum(){
		this.behaviorType = GoodsBehaviorType.Use;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		UseGoodsParam useParam = (UseGoodsParam)param;
		RoleGoods roleGoods = useParam.getRoleGoods();
		RoleInstance role = useParam.getRole();
		return GameContext.getNostrumApp().useNostrum(role, roleGoods);
	}

}
