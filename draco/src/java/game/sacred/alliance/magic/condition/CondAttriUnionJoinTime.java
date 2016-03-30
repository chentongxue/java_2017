package sacred.alliance.magic.condition;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class CondAttriUnionJoinTime extends CondAttriLogic {
	public CondAttriUnionJoinTime(){
		this.logicType = ConditionType.UNION_lEAVE_TIME.getType() ;
	}
	
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		return 0;
	}
	
	@Override
	public boolean isMeet(RoleInstance role, Condition condition) {
		return GameContext.getUnionApp().isBuy(role.getIntRoleId());
		
	}

}
