package sacred.alliance.magic.app.config;

public class OpenSecretkeyConfig extends PropertiesConfig {
	
	public String getSecretkey(int channelId){
		return this.getConfig("open.secretkey.4." + channelId);
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
}
