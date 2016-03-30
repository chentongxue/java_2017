package sacred.alliance.magic.vo.map;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.goddess.config.GoddessPvpConfig;

public class MapGoddessInstance extends MapAsyncPvpInstance {

	public MapGoddessInstance(Map map) {
		super(map);
	}
	
	@Override
	protected String createInstanceId() {
		instanceId = "goddess_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	@Override 
	protected void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType result) {
		GameContext.getGoddessApp().challengeOver(role, battleInfo, result);
	}
	
	@Override 
	protected void exitMapPushMessage() {
		
	}

	@Override
	protected Point getTargetPoint() {
		GoddessPvpConfig config = GameContext.getGoddessApp().getGoddessPvpConfig();
		if(null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getTargetMapX(), config.getTargetMapY());
	}
}
