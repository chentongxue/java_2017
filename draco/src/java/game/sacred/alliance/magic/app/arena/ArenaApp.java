package sacred.alliance.magic.app.arena;

import java.util.Map;

import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.message.request.C3852_ArenaMatchConfirmReqMessage;
import com.game.draco.message.request.C3853_ArenaApplyKeepReqMessage;

public interface ArenaApp extends AppSupport{
	public final static String UN_SELECTED = "0" ;
	public final static String SELECTED = "1" ;
	public static final int DEFAULT_MATCH_CONFIRM_TIME = 10 ;
	public static final int DEFAULT_MATCH_CONFIRM_MILLIS_TIME = DEFAULT_MATCH_CONFIRM_TIME*1000 ;
	//public static final int SERVER_DEFAULT_MATCH_CONFIRM_TIME = DEFAULT_MATCH_CONFIRM_TIME  + 2 ;
	//匹配确认命令字
	public static final short ARENA_MATCH_CONFIRM_CMD = new C3852_ArenaMatchConfirmReqMessage().getCommandId() ;
	//超时服务器触发,给客户端发送cmd=0
	public static final short ARENA_MATCH_CONFIRM_TIME_OVER_CMD = /*ARENA_MATCH_CONFIRM_CMD*/ (short)0;
	public static final short ARENA_APPLY_KEEP_CONFIRM_CMD = new C3853_ArenaApplyKeepReqMessage().getCommandId() ;
	//超时服务器触发,给客户端发送cmd=0
	public static final short ARENA_APPLY_KEEP_CONFIRM_TIME_OVER_CMD = /*ARENA_APPLY_KEEP_CONFIRM_CMD*/ (short)0 ;
	public Arena getArena(int activeId);
	public ApplyInfo getApplyInfo(String roleId) ;
	public ArenaResult apply(RoleInstance role,int activeId) ;
	public ArenaResult applyCancel(RoleInstance role,int activeId) ;
	public ArenaResult applyKeep(String roleId,String selected) ;
	public ArenaResult matchConfirm(String roleId,String selected) ;
	public ArenaConfig getArenaConfig(String activeId);
	public Map<String,ApplyInfo> getAllRoleApplyInfo() ;
	public void removeApplyInfo(String roleId);
	public void removeArenaMatch(String key);
	public void addArenaMatch(ArenaMatch match);
	
	public void systemMatch(int activeId);
	public void systemClose(int activeId);
	public boolean isApplyMap(String mapId);
	public boolean canUseSkill(int skillId);
	public boolean canUseGoods(int goodsId);
	public void addMatchTimeoutListener(ArenaMatch match);
	public void addKeepTimeoutListener(ArenaMatch match);
	
	public ArenaConfig getArenaConfig(ArenaType arenaType);
	public void offlineLog(RoleInstance role);
	
	
}
