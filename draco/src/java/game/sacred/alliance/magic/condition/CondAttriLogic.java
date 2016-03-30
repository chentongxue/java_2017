package sacred.alliance.magic.condition;

import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class CondAttriLogic extends CondLogic{
	public boolean isMeet(RoleInstance role, Condition condition){
		return CondCompareType.isMeet(condition.getConditionCompareType()
				, getRoleAttri(role, condition), condition.getMinValue()
				, condition.getMaxValue(), condition.getCondOrValueList());
	}
}
