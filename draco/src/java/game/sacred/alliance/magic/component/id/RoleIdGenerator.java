package sacred.alliance.magic.component.id;

import com.game.draco.GameContext;

public class RoleIdGenerator extends sacred.alliance.magic.module.id.RangeIncrIntIdGenerator{

	/**
	 * 50w不要轻易改动
	 * 每一个服务器最多创建50w个角色
	 */
	public final static int RANGE_LENGTH = 500000 ;
	@Override
	public void start() throws Exception{
		int serverId = GameContext.getServerId();
		this.setServerId(serverId);
		this.setRangeLength(RANGE_LENGTH);
		super.start() ;
	}
}
