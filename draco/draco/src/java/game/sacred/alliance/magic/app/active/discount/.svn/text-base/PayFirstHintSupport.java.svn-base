package sacred.alliance.magic.app.active.discount;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.active.discount.type.DiscountRewardStat;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.hint.HintId;
import sacred.alliance.magic.app.hint.HintSupport;
import sacred.alliance.magic.vo.RoleInstance;

public class PayFirstHintSupport implements HintSupport{

	@Override
	public Set<HintId> getHintIdSet(RoleInstance role) {
		Discount discount = GameContext.getActiveDiscountApp().getCurrentPayFirstDiscount(role);
		if(null == discount){
			return null ;
		}
		DiscountRewardStat rewardStat = discount.getRewardStatus(role, 0);
		if(DiscountRewardStat.REWARD_CAN != rewardStat){
			//已经领取
			return null ;
		}
		Set<HintId> set = new HashSet<HintId>();
		set.add(HintId.First_Recharge);
		return set ;
	}

	@Override
	public void hintChange(RoleInstance role, HintId hintId) {
		
	}

}
