package sacred.alliance.magic.vo.map;

import sacred.alliance.magic.app.arena.ArenaMatch;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.arena.BattleResult;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C3854_ArenaResultNotifyMessage;

public class MapArenaLearnInstance extends MapArenaInstance{

	public MapArenaLearnInstance(Map map, ArenaMatch match) {
		super(map, match);
	}
	
	
	@Override
	protected int getMaxPlayerNum(ArenaType areanType){
		return 1 ;
	}
	
	@Override
    protected void enter(AbstractRole role){
		if(null == role || RoleType.PLAYER != role.getRoleType()){
			return ;
		}
		super.enter(role);
		RoleInstance player = (RoleInstance)role ;
		//发送倒计时消息
		this.sendRemainTime(player);
		this.autoDismount(player);
	}
	
	protected void reward(String roleId,BattleResult result,int otherTeamNum,ArenaType arenaType){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			return ;
		}
		StringBuilder buffer = new StringBuilder("");
		if(BattleResult.win == result){
			buffer.append(role.getRoleName());
			buffer.append(GameContext.getI18n().getText(TextId.ARENA_PK_SUCCESS_TIPS));
		}else if(BattleResult.fail == result){
			buffer.append(role.getRoleName());
			buffer.append(GameContext.getI18n().getText(TextId.ARENA_PK_FAIL_TIPS));
		}else {
			buffer.append(role.getRoleName());
			buffer.append(GameContext.getI18n().getText(TextId.ARENA_PK_DRAW_TIPS));
		}
		C3854_ArenaResultNotifyMessage message = new C3854_ArenaResultNotifyMessage();
		message.setInfo(buffer.toString());
		message.setArenaType((byte)ArenaType._LEARN.getType());
		message.setResultType(result.getType());
		role.getBehavior().sendMessage(message);
	}
	
	@Override
	protected String getActiveName(){
		return GameContext.getI18n().getText(TextId.ARENA_PK_NAME);
	}
}
