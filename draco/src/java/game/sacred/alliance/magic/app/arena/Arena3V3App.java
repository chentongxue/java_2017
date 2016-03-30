package sacred.alliance.magic.app.arena;

import com.game.draco.message.request.C3866_Arena3V3LevelDescReqMessage;
import sacred.alliance.magic.app.arena.config.ArenaMapRule;
import sacred.alliance.magic.app.arena.config.ScoreResult;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.response.C3863_Arena3V3DetailRespMessage;

public interface Arena3V3App {

	/**
	 * 获取地图规则
	 * @param arenaType
	 * @return
	 */
	public ArenaMapRule getArenaMapRule(int arenaType);
	
	/**
	 * 获取奖励
	 * @param role
	 * @param result
	 * @param match
	 * @param killNum
	 * @return
	 */
	public ScoreResult getScoreResult(RoleInstance role, BattleResult result, ArenaMatch match, int killNum);
	
	/**
	 * 获取3v3面板消息
	 * @param role
	 * @return
	 */
	public C3863_Arena3V3DetailRespMessage getArena3V3DetailRespMessage(RoleInstance role);

    public Message getArena3V3LevelDescMessage(RoleInstance role) ;
	
	/**
	 * buffId
	 * @param arenaType
	 * @return
	 */
	public short getBuffId(int arenaType);
	
	/**
	 * 打开面板
	 * @param role
	 */
	public void pushArena3v3RespMessage(RoleInstance role);
	
	/**
	 * 设置为跨服
	 */
	public void openDarkDoor(boolean flag);
	
	public boolean isOpenDarkDoor();
}
