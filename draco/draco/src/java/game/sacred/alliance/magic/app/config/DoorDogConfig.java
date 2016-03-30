package sacred.alliance.magic.app.config;

import sacred.alliance.magic.util.Util;

public class DoorDogConfig extends PropertiesConfig {
	
	public int getCountByIp(String ip){
		String ipCounts = getConfig(ip);
		if(!Util.isEmpty(ipCounts)){
			return Integer.parseInt(ipCounts);
		}
		return Integer.parseInt(getConfig("default"));
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
