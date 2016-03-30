package sacred.alliance.magic.condition;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.rank.RankLogRoleInfo;
import sacred.alliance.magic.vo.RoleInstance;

public class CondAttriRankRanking extends CondAttriLogic {
	public CondAttriRankRanking() {
		logicType = ConditionType.RANK_RANKING.getType();
	}
	@Override
	public int getRoleAttri(RoleInstance role, Condition condition) {
		RankLogRoleInfo rlRoleInfo = GameContext.getRankApp().getRoleRank(Integer.valueOf(condition.getParamId2()), role.getRoleId());
		if(null == rlRoleInfo){
			return 0;
		}
		return rlRoleInfo.getRank();
	}

}
