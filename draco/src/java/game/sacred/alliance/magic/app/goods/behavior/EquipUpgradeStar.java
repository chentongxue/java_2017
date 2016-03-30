package sacred.alliance.magic.app.goods.behavior;

import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeStarParam;
import sacred.alliance.magic.base.Result;

import com.game.draco.GameContext;

public class EquipUpgradeStar extends AbstractGoodsBehavior{

	public EquipUpgradeStar(){
		this.behaviorType = GoodsBehaviorType.EquipUpgradeStar;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		return GameContext.getEquipApp().equipUpgradeStar((EquipUpgradeStarParam)param);
	}

}
