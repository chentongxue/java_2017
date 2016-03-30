package sacred.alliance.magic.app.user.config;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class AttributeEnoughConfig implements KeySupport<String>{
	private byte type;
	private String tips;
	private short forwardId;
	@Override
	public String getKey() {
		return String.valueOf(type);
	}
	//check
	public void init() {
		AttributeType att = AttributeType.get(type);
		if(att == null){
			Log4jManager.CHECK.error("AttributeEnoughConfig init error [type = " + type + "no exist");
			Log4jManager.checkFail();
		}
	} 
}
