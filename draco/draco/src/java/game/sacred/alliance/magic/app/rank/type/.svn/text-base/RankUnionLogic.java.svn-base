package sacred.alliance.magic.app.rank.type;

import sacred.alliance.magic.app.rank.RankInfo;

import com.game.draco.app.union.domain.Union;

public abstract class RankUnionLogic extends RankLogic<Union>{
	
	@Override
	public void frozenRoleOffRankLog(Union t, RankInfo rankInfo) {
		rankInfo.getLogger().info(LOG_OFFRANK_FLAG + t.getUnionId());
	}
}
