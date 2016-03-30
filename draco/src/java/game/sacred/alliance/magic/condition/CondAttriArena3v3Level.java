package sacred.alliance.magic.condition;

import sacred.alliance.magic.vo.RoleInstance;

/**
 * 3v3等级
 */
public class CondAttriArena3v3Level extends CondAttriLogic {

	public CondAttriArena3v3Level(){
		this.logicType = ConditionType.ROLE_ARENA_3V3_LEVEL.getType() ;
	}
	
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		return (int)role.getRoleArena().getArenaLevel3v3();
	}
}
