package sacred.alliance.magic.component.id;

import com.game.draco.GameContext;


public class StringIdGenerator extends sacred.alliance.magic.module.id.SectionDbMemoryStringIdGenerator{

	/**
	 * 不要轻易修改
	 * 一共支持服务器数
	 * 36*36*36=46656
	 */
	private final static int SUBFIX_LENGTH = 3 ;//serverId占位 
	private final static int DEFAULT_RADIX = 36 ;//进制
	
	@Override
	public void start() throws Exception{
		int serverId = GameContext.getServerId();
		int appId = GameContext.getAppId() ;
		this.setServerId(serverId);
		this.setAppId(appId);
		this.setSubfixLength(SUBFIX_LENGTH);
		this.setRadix(DEFAULT_RADIX);
		super.start() ;
	}
	
	@Override
	public void lessGetLimitSizeEvent() {
		//TODO日志,广播
		GameContext.getOnlineCenter().kickAllRole();
	}
	
}
