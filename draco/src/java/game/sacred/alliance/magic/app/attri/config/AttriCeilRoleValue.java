package sacred.alliance.magic.app.attri.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class AttriCeilRoleValue implements KeySupport<String>{

	private int roleLevel ;
	private int maxValue ;
	
	public String getKey(){
		return String.valueOf(roleLevel);
	}
}
