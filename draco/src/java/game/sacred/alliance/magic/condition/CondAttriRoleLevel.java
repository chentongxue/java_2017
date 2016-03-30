package sacred.alliance.magic.condition;

import sacred.alliance.magic.vo.RoleInstance;
/**
 * 取人物的等级属性
 */
public class CondAttriRoleLevel extends CondAttriLogic {

	public CondAttriRoleLevel(){
		logicType = ConditionType.ROLE_LEVEL.getType() ;
	}
	
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		return role.getLevel();
	}
}
