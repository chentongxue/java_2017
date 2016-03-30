package sacred.alliance.magic.app.config;

public class EnvConfig extends PropertiesConfig {
	
	public String getProductId(){
		return this.getConfig("PRODUCTID");
	}
	
	public String getRegionId(){
		return this.getConfig("REGIONID");
	}
	
	public boolean isCloseServerIdLimit(){
		String value = this.getConfig("close.serverid.limit");
		if(null == value){
			return false ;
		}
		return "1".equals(value.trim());
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
