package sacred.alliance.magic.app.config;

public class SocialConfig extends PropertiesConfig{

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 好友列表最大数量
	 * @return
	 */
	public int getFriendMaxNum(){
		return Integer.valueOf(this.getConfig("friendMaxNum"));
	}
	/**
	 * 黑名单列表最大数量
	 * @return
	 */
	public int getBlacklistMaxNum(){
		return Integer.valueOf(this.getConfig("blacklistMaxNum"));
	}
	
}
