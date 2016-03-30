package sacred.alliance.magic.app.config;

public class EnvConfig extends PropertiesConfig {
	
	public String getProductId(){
		return this.getConfig("PRODUCTID");
	}
	
	public String getRegionId(){
		return this.getConfig("REGIONID");
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
