package sacred.alliance.magic.vo.map;

import java.util.List;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.pet.config.PetPvpConfig;
import com.game.draco.message.item.DeathNotifySelfItem;

public class MapPetInstance extends MapAsyncArenaInstance {
	
	public MapPetInstance(Map map) {
		super(map);
	}
	
	@Override
    protected void enter(AbstractRole role){
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		super.enter(role);
		//活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes((RoleInstance)role, 1, DailyPlayType.pet_plunder, "");
	}
	
	@Override
	protected String createInstanceId() {
		instanceId = "pet_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}
	
	@Override 
	protected void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType result) {
		GameContext.getPetApp().challengeOver(role, battleInfo, result);
	}
	
	@Override
	protected Point getTargetPoint() {
		PetPvpConfig config = GameContext.getPetApp().getPetPvpConfig();
		if(null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getTargetMapX(), config.getTargetMapY());
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
	}
	
	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		//没有复活方式
		return null ;
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime){
		super.broadcastMap(role, message, expireTime);
	}
	
}
