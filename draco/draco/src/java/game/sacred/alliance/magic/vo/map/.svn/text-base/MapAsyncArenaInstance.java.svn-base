package sacred.alliance.magic.vo.map;

import java.util.List;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncMap;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;

/**
 * 异步竞技场
 * @author zhouhaobing
 *
 */
public class MapAsyncArenaInstance extends MapAsyncPvpInstance{

	public MapAsyncArenaInstance(Map map) {
		super(map);
	}
	
	@Override
	protected String createInstanceId() {
		instanceId = "async_arena_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	protected void exitMapPushMessage() {
	}

	@Override
	protected Point getTargetPoint() {
		List<AsyncMap> config = GameContext.getAsyncArenaApp().getAsyncMapList();
		if(null == config) {
			return null;
		}
		return new Point(config.get(0).getMapId(), config.get(0).getTargetMapX(), config.get(0).getTargetMapY());
	}

	@Override
	protected void challengeOver(RoleInstance role,
			AsyncPvpBattleInfo battleInfo, ChallengeResultType result) {
		GameContext.getRoleAsyncArenaApp().challengeOver(role, battleInfo, result);
	}
	
}
