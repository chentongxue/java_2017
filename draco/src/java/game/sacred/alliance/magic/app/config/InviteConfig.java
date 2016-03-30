package sacred.alliance.magic.app.config;

import sacred.alliance.magic.constant.Cat;

public class InviteConfig extends PropertiesConfig {
	
	public String getDownLoadUrl(int channelId,int osType){
		return this.getConfig(channelId + Cat.underline + osType);
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
}
