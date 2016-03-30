package sacred.alliance.magic.condition;

import com.game.draco.GameContext;

import sacred.alliance.magic.vo.RoleInstance;
/**
 * 取人物的VIP等级属性
 */
public class CondAttriVipLevel extends CondAttriLogic {

	public CondAttriVipLevel(){
		logicType = ConditionType.ROLE_VIP_LEVEL.getType() ;
	}
	
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		return GameContext.getVipApp().getVipLevel(role);
	}
}
