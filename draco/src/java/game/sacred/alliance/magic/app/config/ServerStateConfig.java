package sacred.alliance.magic.app.config;

public class ServerStateConfig extends PropertiesConfig{
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
/*	public int getFullsize(){
		return Integer.parseInt(getConfig("fullsize"));
	}*/
	
	public int getlowloadRate(){
		return Integer.parseInt(getConfig("lowload_rate"));
	}
	
	public int getNormalloadRate(){
		return Integer.parseInt(getConfig("normalload_rate"));
	}
	
	public int getHighloadRate(){
		return Integer.parseInt(getConfig("highload_rate"));
	}
	
	public int getUltrahighloadRate(){
		return Integer.parseInt(getConfig("ultrahighload_rate"));
	}
}
