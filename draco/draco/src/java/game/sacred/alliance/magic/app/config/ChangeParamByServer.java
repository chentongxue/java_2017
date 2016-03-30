package sacred.alliance.magic.app.config;


public class ChangeParamByServer extends PropertiesConfig {
	
	public int getMaxRoleName() {
		return Integer.parseInt(getConfig("maxRoleName"));
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
