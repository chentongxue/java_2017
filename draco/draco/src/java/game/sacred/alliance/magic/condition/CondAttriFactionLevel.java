package sacred.alliance.magic.condition;

import sacred.alliance.magic.vo.RoleInstance;

public class CondAttriFactionLevel extends CondAttriLogic {
	public CondAttriFactionLevel(){
		this.logicType = ConditionType.FACTION_LEVEL.getType() ;
	}
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		return role.getUnionLevel();
	}

}
