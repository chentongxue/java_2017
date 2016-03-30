package sacred.alliance.magic.condition;

import com.game.draco.GameContext;

import sacred.alliance.magic.vo.RoleInstance;

public class CondTitleIsHave extends CondLogic {
	
	public CondTitleIsHave(){
		logicType = ConditionType.TITLE_ISHAVE.getType() ;
	}

	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		return 0;
	}

	@Override
	public boolean isMeet(RoleInstance role, Condition condition) {
		return GameContext.getTitleApp().isExistEffectiveTitle(role, Integer.valueOf(condition.getParamId1()));
	}

}
