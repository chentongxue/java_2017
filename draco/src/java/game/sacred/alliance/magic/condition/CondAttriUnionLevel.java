package sacred.alliance.magic.condition;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;

public class CondAttriUnionLevel extends CondAttriLogic {
	public CondAttriUnionLevel(){
		this.logicType = ConditionType.UNION_LEVEL.getType() ;
	}
	
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		Union union = GameContext.getUnionApp().getUnion(role);
		if(union != null){
			return union.getUnionLevel();
		}
		return 0;
	}

}
