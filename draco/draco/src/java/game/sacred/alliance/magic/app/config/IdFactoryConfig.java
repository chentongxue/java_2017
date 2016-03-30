package sacred.alliance.magic.app.config;

public class IdFactoryConfig extends PropertiesConfig {
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public int getIntMaxLength(){
		return Integer.parseInt((getConfig("intMaxLength")));
	}
	
	public int getIntNode(){
		return Integer.parseInt(getConfig("intNode"));
	}
	
	public int getDefendIntNode(){
		return Integer.parseInt(getConfig("defendIntNode"));
	}
	
	public int getStringMaxLength(){
		return Integer.parseInt(getConfig("stringMaxLength"));
	}
	
	public int getStringNode(){
		return Integer.parseInt(getConfig("stringNode"));
	}
	
	public int getDefendStringNode(){
		return Integer.parseInt(getConfig("defendStringNode"));
	}
	
	public long getDaemonThreadSleep(){
		return Long.valueOf(getConfig("daemonThreadSleep"));
	}
}
